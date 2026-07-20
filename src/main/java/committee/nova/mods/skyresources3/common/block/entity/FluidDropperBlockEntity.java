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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

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
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        input.readChild(FLUIDS_KEY, this.fluids);
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
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
            final ResourceHandler<FluidResource> neighbor = level.getCapability(
                    Capabilities.Fluid.BLOCK,
                    neighborPos,
                    direction.getOpposite()
            );
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
        if (!FluidUtil.tryPlaceFluid(this.fluids, null, level, InteractionHand.MAIN_HAND, below).isEmpty()) {
            this.setChanged();
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
    }
}
