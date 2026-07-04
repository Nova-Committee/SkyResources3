package committee.nova.mods.skyresources3.common.block.entity;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import committee.nova.mods.skyresources3.common.compat.ValueOutput;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandlerUtil;
import committee.nova.mods.skyresources3.common.compat.transfer.fluid.FluidResource;
import committee.nova.mods.skyresources3.common.compat.transfer.fluid.FluidStacksResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.fluid.FluidUtil;

public final class FluidDropperBlockEntity extends BlockEntity {
    public static final int DEFAULT_CAPACITY = FluidType.BUCKET_VOLUME;
    private static final String FLUIDS_KEY = "fluids";
    private static final int TANK = 0;
    private static final Direction[] PULL_DIRECTIONS = {
            Direction.UP,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST
    };

    private final FluidDropperFluidHandler fluids = new FluidDropperFluidHandler(this);

    public FluidDropperBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.FLUID_DROPPER.get(), pos, blockState);
    }

    @Override
    public void load(final net.minecraft.nbt.CompoundTag tag) {
        super.load(tag);
        final ValueInput input = new ValueInput(tag);
        input.readChild(FLUIDS_KEY, this.fluids);
    }

    @Override
    protected void saveAdditional(final net.minecraft.nbt.CompoundTag tag) {
        super.saveAdditional(tag);
        final ValueOutput output = new ValueOutput(tag);
        output.putChild(FLUIDS_KEY, this.fluids);
    }

    public void serverTick(final ServerLevel level) {
        if (!level.hasNeighborSignal(this.worldPosition)) {
            this.pullFromNeighbors(level);
        }
        this.placeStoredFluid(level);
    }

    public ResourceHandler<FluidResource> getFluidHandler() {
        return this.fluids;
    }

    public static int getCapacity() {
        return Config.fluidDropperCapacity > 0 ? Config.fluidDropperCapacity : DEFAULT_CAPACITY;
    }

    private void pullFromNeighbors(final ServerLevel level) {
        for (final Direction direction : PULL_DIRECTIONS) {
            if (this.getRemainingCapacity() <= 0) {
                return;
            }

            final BlockPos neighborPos = this.worldPosition.relative(direction);
            final IFluidHandler neighbor = level.getBlockEntity(neighborPos) == null
                    ? null
                    : level.getBlockEntity(neighborPos)
                            .getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite())
                            .orElse(null);
            if (neighbor == null) {
                continue;
            }

            ResourceHandlerUtil.moveFirst(
                    neighbor,
                    this.fluids,
                    this::canAccept,
                    this.getRemainingCapacity(),
                    null
            );
        }
    }

    private boolean canAccept(final FluidResource resource) {
        if (resource.isEmpty()) {
            return false;
        }
        final FluidResource stored = this.fluids.getResource(TANK);
        return stored.isEmpty() || stored.equals(resource);
    }

    private int getRemainingCapacity() {
        return Math.max(0, getCapacity() - this.fluids.getAmountAsInt(TANK));
    }

    private void placeStoredFluid(final ServerLevel level) {
        if (this.fluids.getAmountAsInt(TANK) < FluidType.BUCKET_VOLUME) {
            return;
        }

        final BlockPos below = this.worldPosition.below();
        if (!level.isEmptyBlock(below)) {
            return;
        }

        final FluidResource resource = this.fluids.getResource(TANK);
        if (!resource.isEmpty() && FluidUtil.tryPlaceFluid(resource, null, level, InteractionHand.MAIN_HAND, below)) {
            this.fluids.clear();
        }
    }

    private static final class FluidDropperFluidHandler extends FluidStacksResourceHandler {
        private final FluidDropperBlockEntity owner;

        private FluidDropperFluidHandler(final FluidDropperBlockEntity owner) {
            super(1, DEFAULT_CAPACITY);
            this.owner = owner;
        }

        @Override
        public void deserialize(final ValueInput input) {
            super.deserialize(input);
            if (this.size() != 1) {
                this.setStacks(NonNullList.withSize(1, FluidStack.EMPTY));
            }
        }

        @Override
        public boolean isValid(final int index, final FluidResource resource) {
            return index == TANK && !resource.isEmpty();
        }

        @Override
        protected int getCapacity(final int index, final FluidResource resource) {
            return FluidDropperBlockEntity.getCapacity();
        }

        @Override
        protected void onContentsChanged(final int index, final FluidStack previousContents) {
            this.owner.setChanged();
        }

        private void clear() {
            this.set(TANK, FluidResource.EMPTY, 0);
        }
    }
}
