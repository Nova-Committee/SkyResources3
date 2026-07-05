package committee.nova.mods.skyresources3.common.block.entity;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.common.recipe.CrucibleRecipe;
import committee.nova.mods.skyresources3.common.recipe.CrucibleRecipes;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.util.HeatSources;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import committee.nova.mods.skyresources3.common.compat.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.fluid.FluidResource;
import committee.nova.mods.skyresources3.common.compat.transfer.fluid.FluidStacksResourceHandler;

public final class CrucibleBlockEntity extends BlockEntity {
    public static final int DEFAULT_CAPACITY = FluidType.BUCKET_VOLUME * 4;
    private static final String FLUIDS_KEY = "fluids";
    private static final String ITEM_KEY = "item";
    private static final String ITEM_AMOUNT_KEY = "item_amount";
    private static final int TANK = 0;

    private final CrucibleFluidHandler fluids = new CrucibleFluidHandler(this);
    private final LazyOptional<IFluidHandler> fluidCapability = LazyOptional.of(() -> this.fluids);
    private ItemStack itemIn = ItemStack.EMPTY;
    private int itemAmount;

    public CrucibleBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.CRUCIBLE.get(), pos, blockState);
    }

    @Override
    public void load(final net.minecraft.nbt.CompoundTag tag) {
        super.load(tag);
        final ValueInput input = new ValueInput(tag);
        input.readChild(FLUIDS_KEY, this.fluids);
        this.itemIn = input.read(ITEM_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.itemAmount = input.getIntOr(ITEM_AMOUNT_KEY, 0);
        if (this.itemAmount <= 0) {
            this.itemAmount = 0;
            this.itemIn = ItemStack.EMPTY;
        }
    }

    @Override
    protected void saveAdditional(final net.minecraft.nbt.CompoundTag tag) {
        super.saveAdditional(tag);
        final ValueOutput output = new ValueOutput(tag);
        output.putChild(FLUIDS_KEY, this.fluids);
        output.store(ITEM_KEY, ItemStack.CODEC, this.itemIn);
        output.putInt(ITEM_AMOUNT_KEY, this.itemAmount);
    }

    public void serverTick(final ServerLevel level) {
        boolean changed = this.absorbItemEntities(level);
        changed |= this.absorbInserter(level);
        changed |= this.meltStoredInput(level);
        if (changed) {
            this.markUpdated(level);
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(final Capability<T> capability, final net.minecraft.core.Direction side) {
        if (capability == ForgeCapabilities.FLUID_HANDLER) {
            return this.fluidCapability.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.fluidCapability.invalidate();
    }

    public ResourceHandler<FluidResource> getFluidHandler() {
        return this.fluids;
    }

    public int getComparatorSignal() {
        if (this.itemAmount <= 0) {
            return 0;
        }
        return Math.max(1, (int) ((long) this.itemAmount * 15L / getCapacity()));
    }

    public static int getCapacity() {
        return Config.crucibleCapacity > 0 ? Config.crucibleCapacity : DEFAULT_CAPACITY;
    }

    private boolean absorbItemEntities(final ServerLevel level) {
        boolean changed = false;
        final AABB area = new AABB(
                this.worldPosition.getX(),
                this.worldPosition.getY() + 0.2D,
                this.worldPosition.getZ(),
                this.worldPosition.getX() + 1.0D,
                this.worldPosition.getY() + 1.0D,
                this.worldPosition.getZ() + 1.0D
        );
        for (final ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, area, item -> !item.getItem().isEmpty())) {
            final ItemStack stack = entity.getItem();
            final int before = stack.getCount();
            if (this.tryInsertStack(level, stack)) {
                changed = true;
                if (stack.isEmpty()) {
                    entity.discard();
                } else if (stack.getCount() != before) {
                    entity.setItem(stack);
                }
            }
        }
        return changed;
    }

    private boolean absorbInserter(final ServerLevel level) {
        if (!(level.getBlockEntity(this.worldPosition.above()) instanceof CrucibleInserterBlockEntity inserter)) {
            return false;
        }

        final ItemStack stack = inserter.getItem(0);
        if (stack.isEmpty() || !this.tryInsertStack(level, stack)) {
            return false;
        }

        inserter.setItem(0, stack);
        return true;
    }

    private boolean tryInsertStack(final ServerLevel level, final ItemStack stack) {
        final Optional<CrucibleRecipe> holder = CrucibleRecipes.find(level, stack);
        if (holder.isEmpty()) {
            return false;
        }

        final CrucibleRecipe recipe = holder.get();
        final FluidStack output = recipe.output();
        if (!this.canAcceptRecipe(stack, recipe, output)) {
            return false;
        }

        if (this.itemIn.isEmpty()) {
            this.itemIn = stack.copy();
            this.itemIn.setCount(1);
        }
        this.itemAmount += output.getAmount();
        stack.shrink(recipe.input().count());
        return true;
    }

    private boolean canAcceptRecipe(
            final ItemStack stack,
            final CrucibleRecipe recipe,
            final FluidStack output
    ) {
        if (!recipe.input().matches(stack)) {
            return false;
        }
        if (!this.itemIn.isEmpty() && !ItemStack.isSameItemSameTags(this.itemIn, stack)) {
            return false;
        }
        final FluidResource resource = FluidResource.of(output);
        return this.canAcceptFluid(resource)
                && (long) this.fluids.getAmountAsInt(TANK) + this.itemAmount + output.getAmount() <= getCapacity();
    }

    private boolean canAcceptFluid(final FluidResource resource) {
        final FluidResource stored = this.fluids.getResource(TANK);
        return stored.isEmpty() || stored.equals(resource);
    }

    private boolean meltStoredInput(final ServerLevel level) {
        if (this.itemAmount <= 0 || this.itemIn.isEmpty()) {
            return false;
        }

        final Optional<CrucibleRecipe> holder = CrucibleRecipes.find(level, this.itemIn);
        if (holder.isEmpty()) {
            return false;
        }

        final FluidResource resource = FluidResource.of(holder.get().output());
        final int remainingCapacity = this.getRemainingCapacity(resource);
        final int amount = Math.min(Math.min(this.getHeatProgress(level), this.itemAmount), remainingCapacity);
        if (amount <= 0) {
            return false;
        }

        this.fluids.set(TANK, resource, this.fluids.getAmountAsInt(TANK) + amount);
        this.itemAmount -= amount;
        if (this.itemAmount <= 0) {
            this.itemAmount = 0;
            this.itemIn = ItemStack.EMPTY;
        }
        return true;
    }

    private int getRemainingCapacity(final FluidResource resource) {
        if (!this.canAcceptFluid(resource)) {
            return 0;
        }
        return Math.max(0, getCapacity() - this.fluids.getAmountAsInt(TANK));
    }

    private int getHeatProgress(final ServerLevel level) {
        final int heat = HeatSources.getHeatSourceValue(level, this.worldPosition.below());
        if (heat <= 0) {
            return 0;
        }
        return Math.max(heat * Config.crucibleSpeed / 8, 1);
    }

    private void markUpdated(final ServerLevel level) {
        this.setChanged();
        final BlockState state = this.getBlockState();
        level.sendBlockUpdated(this.worldPosition, state, state, 3);
        level.updateNeighbourForOutputSignal(this.worldPosition, state.getBlock());
    }

    private static final class CrucibleFluidHandler extends FluidStacksResourceHandler {
        private final CrucibleBlockEntity owner;

        private CrucibleFluidHandler(final CrucibleBlockEntity owner) {
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
            return CrucibleBlockEntity.getCapacity();
        }

        @Override
        protected void onContentsChanged(final int index, final FluidStack previousContents) {
            this.owner.setChanged();
        }
    }
}
