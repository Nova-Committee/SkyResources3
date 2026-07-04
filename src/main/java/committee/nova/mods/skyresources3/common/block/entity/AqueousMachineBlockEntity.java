package committee.nova.mods.skyresources3.common.block.entity;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.core.machine.AqueousMachineMode;
import committee.nova.mods.skyresources3.common.recipe.WaterExtractorRecipes;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import committee.nova.mods.skyresources3.common.compat.ValueOutput;
import net.minecraftforge.fluids.FluidStack;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.energy.EnergyHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.energy.SimpleEnergyHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.fluid.FluidResource;
import committee.nova.mods.skyresources3.common.compat.transfer.fluid.FluidStacksResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemResource;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemStacksResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.transaction.TransactionContext;

public final class AqueousMachineBlockEntity extends BlockEntity {
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int SLOT_COUNT = 2;
    public static final int MAX_PROGRESS = 100;
    public static final int ENERGY_CAPACITY = 100_000;
    public static final int WATER_CAPACITY = 4_000;
    private static final int TANK = 0;
    private static final int MAX_ENERGY_INSERT = 2_000;
    private static final int MAX_ENERGY_EXTRACT = 0;
    private static final FluidResource WATER = FluidResource.of(Fluids.WATER);
    private static final String ITEMS_KEY = "items";
    private static final String ENERGY_KEY = "energy";
    private static final String FLUIDS_KEY = "fluids";
    private static final String PROGRESS_KEY = "progress";

    private final AqueousItemHandler items = new AqueousItemHandler(this);
    private final AqueousEnergyHandler energy = new AqueousEnergyHandler(this);
    private final AqueousFluidHandler fluids = new AqueousFluidHandler(this);
    private float progress;

    public AqueousMachineBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.AQUEOUS_MACHINE.get(), pos, blockState);
    }

    @Override
    public void load(final net.minecraft.nbt.CompoundTag tag) {
        super.load(tag);
        final ValueInput input = new ValueInput(tag);
        input.readChild(ITEMS_KEY, this.items);
        input.readChild(ENERGY_KEY, this.energy);
        input.readChild(FLUIDS_KEY, this.fluids);
        this.progress = input.getFloatOr(PROGRESS_KEY, 0.0F);
    }

    @Override
    protected void saveAdditional(final net.minecraft.nbt.CompoundTag tag) {
        super.saveAdditional(tag);
        final ValueOutput output = new ValueOutput(tag);
        output.putChild(ITEMS_KEY, this.items);
        output.putChild(ENERGY_KEY, this.energy);
        output.putChild(FLUIDS_KEY, this.fluids);
        output.putFloat(PROGRESS_KEY, this.progress);
    }

    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        this.dropContents();
    }

    public void serverTick(final ServerLevel level) {
        final AqueousMachineMode mode = this.getMode();
        final Optional<WaterExtractorRecipes.ItemWaterRecipe> recipe = this.findRecipe(mode);
        if (recipe.isEmpty()) {
            this.resetProgressIfNeeded();
            return;
        }

        if (!this.canWork(mode, recipe.get())) {
            return;
        }

        final int powerUsage = Math.max(0, this.powerUsage(mode));
        if (this.energy.getAmountAsInt() < powerUsage) {
            return;
        }

        this.consumeEnergy(powerUsage);
        this.progress += Math.max(1, this.speed(mode));
        boolean changed = true;
        if (this.progress >= MAX_PROGRESS) {
            changed |= this.completeRecipe(mode, recipe.get());
        }
        if (changed) {
            this.setChanged();
        }
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return this.items;
    }

    public EnergyHandler getEnergyHandler() {
        return this.energy;
    }

    public ResourceHandler<FluidResource> getFluidHandler() {
        return this.fluids;
    }

    public AqueousMachineMode getMode() {
        return this.getBlockState().is(ModBlocks.AQUEOUS_DECONCENTRATOR.get())
                ? AqueousMachineMode.DECONCENTRATOR
                : AqueousMachineMode.CONCENTRATOR;
    }

    public ItemStack getStackInSlot(final int slot) {
        return isMachineSlot(slot) ? this.items.stack(slot) : ItemStack.EMPTY;
    }

    public void setStackInSlot(final int slot, final ItemStack stack) {
        if (!isMachineSlot(slot)) {
            return;
        }
        this.items.setStack(slot, stack);
        if (slot == INPUT_SLOT) {
            this.progress = 0.0F;
        }
    }

    public ItemStack removeStack(final int slot, final int amount) {
        if (!isMachineSlot(slot) || amount <= 0) {
            return ItemStack.EMPTY;
        }
        final ItemStack stack = this.items.stack(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = stack.split(amount);
        if (stack.isEmpty()) {
            this.items.setStack(slot, ItemStack.EMPTY);
        }
        if (slot == INPUT_SLOT) {
            this.progress = 0.0F;
        }
        this.setChanged();
        return removed;
    }

    public ItemStack removeStackNoUpdate(final int slot) {
        if (!isMachineSlot(slot)) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.items.stack(slot);
        this.items.setStack(slot, ItemStack.EMPTY);
        if (slot == INPUT_SLOT) {
            this.progress = 0.0F;
        }
        return removed;
    }

    public boolean mayPlaceInSlot(final int slot, final ItemStack stack) {
        return slot == INPUT_SLOT && !stack.isEmpty();
    }

    public int getProgress() {
        return Math.min(MAX_PROGRESS, Math.round(this.progress));
    }

    public int getEnergyStored() {
        return this.energy.getAmountAsInt();
    }

    public int getWaterStored() {
        return this.fluids.getAmountAsInt(TANK);
    }

    public void dropContents() {
        if (this.level == null) {
            return;
        }
        final SimpleContainer container = new SimpleContainer(SLOT_COUNT);
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            container.setItem(slot, this.items.stack(slot).copy());
        }
        Containers.dropContents(this.level, this.worldPosition, container);
    }

    private Optional<WaterExtractorRecipes.ItemWaterRecipe> findRecipe(final AqueousMachineMode mode) {
        final ItemStack input = this.items.stack(INPUT_SLOT);
        if (input.isEmpty()) {
            return Optional.empty();
        }
        return mode.isConcentrator()
                ? WaterExtractorRecipes.findConcentration(input)
                : WaterExtractorRecipes.findDeconcentration(input);
    }

    private boolean canWork(
            final AqueousMachineMode mode,
            final WaterExtractorRecipes.ItemWaterRecipe recipe
    ) {
        return this.canInsertOutput(recipe.output())
                && (mode.isConcentrator()
                ? this.hasEnoughWater(recipe.waterAmount())
                : this.canAddWater(recipe.waterAmount()));
    }

    private boolean completeRecipe(
            final AqueousMachineMode mode,
            final WaterExtractorRecipes.ItemWaterRecipe recipe
    ) {
        if (!this.canWork(mode, recipe)) {
            return false;
        }
        if (recipe.hasOutput()) {
            this.insertOutput(recipe.output());
        }

        final ItemStack input = this.items.stack(INPUT_SLOT);
        input.shrink(1);
        if (input.isEmpty()) {
            this.items.setStack(INPUT_SLOT, ItemStack.EMPTY);
        }

        if (mode.isConcentrator()) {
            this.drainWater(recipe.waterAmount());
        } else {
            this.addWater(recipe.waterAmount());
        }
        this.progress = 0.0F;
        return true;
    }

    private boolean canInsertOutput(final ItemStack output) {
        if (output.isEmpty()) {
            return true;
        }
        final ItemStack current = this.items.stack(OUTPUT_SLOT);
        if (current.isEmpty()) {
            return output.getCount() <= output.getMaxStackSize();
        }
        return ItemStack.isSameItemSameTags(current, output)
                && current.getCount() + output.getCount() <= current.getMaxStackSize();
    }

    private void insertOutput(final ItemStack output) {
        if (output.isEmpty()) {
            return;
        }
        final ItemStack current = this.items.stack(OUTPUT_SLOT);
        if (current.isEmpty()) {
            this.items.setStack(OUTPUT_SLOT, output.copy());
            return;
        }
        current.grow(output.getCount());
    }

    private boolean hasEnoughWater(final int amount) {
        return this.fluids.getResource(TANK).equals(WATER)
                && this.fluids.getAmountAsInt(TANK) >= amount;
    }

    private boolean canAddWater(final int amount) {
        final int stored = this.fluids.getAmountAsInt(TANK);
        final FluidResource resource = this.fluids.getResource(TANK);
        return (stored <= 0 || resource.equals(WATER)) && stored + amount <= WATER_CAPACITY;
    }

    private void addWater(final int amount) {
        final int total = this.fluids.getAmountAsInt(TANK) + amount;
        this.fluids.set(TANK, WATER, Math.min(WATER_CAPACITY, total));
    }

    private void drainWater(final int amount) {
        final int remaining = this.fluids.getAmountAsInt(TANK) - amount;
        if (remaining <= 0) {
            this.fluids.set(TANK, FluidResource.EMPTY, 0);
            return;
        }
        this.fluids.set(TANK, WATER, remaining);
    }

    private void consumeEnergy(final int amount) {
        if (amount <= 0) {
            return;
        }
        this.energy.set(this.energy.getAmountAsInt() - amount);
    }

    private int powerUsage(final AqueousMachineMode mode) {
        return mode.isConcentrator()
                ? Config.aqueousConcentratorPowerUsage
                : Config.aqueousDeconcentratorPowerUsage;
    }

    private int speed(final AqueousMachineMode mode) {
        return mode.isConcentrator()
                ? Config.aqueousConcentratorSpeed
                : Config.aqueousDeconcentratorSpeed;
    }

    private void resetProgressIfNeeded() {
        if (this.progress > 0.0F) {
            this.progress = 0.0F;
            this.setChanged();
        }
    }

    private static boolean isMachineSlot(final int slot) {
        return slot >= 0 && slot < SLOT_COUNT;
    }

    private static class StoredItemStacks extends ItemStacksResourceHandler {
        protected final AqueousMachineBlockEntity owner;

        StoredItemStacks(final AqueousMachineBlockEntity owner) {
            super(SLOT_COUNT);
            this.owner = owner;
        }

        @Override
        public void deserialize(final ValueInput input) {
            super.deserialize(input);
            if (this.size() != SLOT_COUNT) {
                this.setStacks(NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY));
            }
        }

        @Override
        protected void onContentsChanged(final int index, final ItemStack previousContents) {
            this.owner.setChanged();
        }

        final ItemStack stack(final int slot) {
            return this.stacks.get(slot);
        }

        final void setStack(final int slot, final ItemStack stack) {
            this.stacks.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
            this.owner.setChanged();
        }
    }

    private static final class AqueousItemHandler extends StoredItemStacks {
        private AqueousItemHandler(final AqueousMachineBlockEntity owner) {
            super(owner);
        }

        @Override
        public boolean isValid(final int index, final ItemResource resource) {
            return index == INPUT_SLOT && !resource.isEmpty();
        }

        @Override
        public int extract(
                final int index,
                final ItemResource resource,
                final int amount,
                final TransactionContext transaction
        ) {
            if (index == INPUT_SLOT) {
                return 0;
            }
            return super.extract(index, resource, amount, transaction);
        }
    }

    private static final class AqueousFluidHandler extends FluidStacksResourceHandler {
        private final AqueousMachineBlockEntity owner;

        private AqueousFluidHandler(final AqueousMachineBlockEntity owner) {
            super(1, WATER_CAPACITY);
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
            return index == TANK
                    && !resource.isEmpty()
                    && resource.getFluid() == Fluids.WATER;
        }

        @Override
        protected int getCapacity(final int index, final FluidResource resource) {
            return WATER_CAPACITY;
        }

        @Override
        public int insert(
                final int index,
                final FluidResource resource,
                final int amount,
                final TransactionContext transaction
        ) {
            if (!this.owner.getMode().isConcentrator()) {
                return 0;
            }
            return super.insert(index, resource, amount, transaction);
        }

        @Override
        public int extract(
                final int index,
                final FluidResource resource,
                final int amount,
                final TransactionContext transaction
        ) {
            if (!this.owner.getMode().isConcentrator()) {
                return super.extract(index, resource, amount, transaction);
            }
            return 0;
        }

        @Override
        protected void onContentsChanged(final int index, final FluidStack previousContents) {
            this.owner.setChanged();
        }
    }

    private static final class AqueousEnergyHandler extends SimpleEnergyHandler {
        private final AqueousMachineBlockEntity owner;

        private AqueousEnergyHandler(final AqueousMachineBlockEntity owner) {
            super(ENERGY_CAPACITY, MAX_ENERGY_INSERT, MAX_ENERGY_EXTRACT);
            this.owner = owner;
        }

        @Override
        protected void onEnergyChanged(final int previousAmount) {
            this.owner.setChanged();
        }
    }
}
