package committee.nova.mods.skyresources3.common.block.entity;

import committee.nova.mods.skyresources3.common.recipe.ProcessIngredient;
import committee.nova.mods.skyresources3.common.recipe.ProcessRecipes;
import committee.nova.mods.skyresources3.common.recipe.SkyResourcesProcessRecipe;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import committee.nova.mods.skyresources3.common.compat.ValueOutput;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemResource;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemStacksResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.transaction.TransactionContext;

public final class FusionTableBlockEntity extends BlockEntity {
    private static final String ITEMS_KEY = "items";
    private static final String FILTER_KEY = "filter";
    private static final String YIELD_KEY = "yield";
    private static final String YIELD_RATE_KEY = "yieldRate";
    private static final String OUTPUT_KEY = "output";
    private static final String CATALYST_YIELD_KEY = "itemYield";
    private static final String CATALYST_LEFT_KEY = "itemLeft";
    private static final String PROGRESS_KEY = "progress";

    public static final int CATALYST_SLOT = 0;
    public static final int FIRST_INPUT_SLOT = 1;
    public static final int INPUT_SLOT_COUNT = 9;
    public static final int OUTPUT_SLOT = 10;
    public static final int SLOT_COUNT = 11;
    public static final int MAX_PROGRESS = 100;
    private static final int YIELD_ROUNDING_SCALE = 10_000;

    private final FusionTableItemHandler items = new FusionTableItemHandler(this);
    private final StoredItemStacks filter = new StoredItemStacks(this, INPUT_SLOT_COUNT);

    private double yieldAmount;
    private float yieldRate;
    private float catalystYield;
    private float catalystLeft;
    private int progress;
    private ItemStack outputStack = ItemStack.EMPTY;

    public FusionTableBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.FUSION_TABLE.get(), pos, blockState);
    }

    @Override
    public void load(final net.minecraft.nbt.CompoundTag tag) {
        super.load(tag);
        final ValueInput input = new ValueInput(tag);
        input.readChild(ITEMS_KEY, this.items);
        input.readChild(FILTER_KEY, this.filter);
        this.yieldAmount = input.getDoubleOr(YIELD_KEY, 0.0D);
        this.yieldRate = input.getFloatOr(YIELD_RATE_KEY, 0.0F);
        this.outputStack = input.read(OUTPUT_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.catalystYield = input.getFloatOr(CATALYST_YIELD_KEY, 0.0F);
        this.catalystLeft = input.getFloatOr(CATALYST_LEFT_KEY, 0.0F);
        this.progress = input.getIntOr(PROGRESS_KEY, 0);
    }

    @Override
    protected void saveAdditional(final net.minecraft.nbt.CompoundTag tag) {
        super.saveAdditional(tag);
        final ValueOutput output = new ValueOutput(tag);
        output.putChild(ITEMS_KEY, this.items);
        output.putChild(FILTER_KEY, this.filter);
        output.putDouble(YIELD_KEY, this.yieldAmount);
        output.putFloat(YIELD_RATE_KEY, this.yieldRate);
        output.store(OUTPUT_KEY, ItemStack.CODEC, this.outputStack);
        output.putFloat(CATALYST_YIELD_KEY, this.catalystYield);
        output.putFloat(CATALYST_LEFT_KEY, this.catalystLeft);
        output.putInt(PROGRESS_KEY, this.progress);
    }

    public void serverTick(final ServerLevel level) {
        boolean changed = false;

        if (this.outputStack.isEmpty()) {
            changed |= this.tryStartRecipe(level);
        }
        if (this.canAdvanceCraft()) {
            changed = true;
            this.progress++;
            this.catalystLeft -= this.yieldRate;
            this.yieldAmount = roundYield(this.yieldAmount + this.catalystYield / 100.0D);
            this.tryInsertYieldedOutput();
        }
        if (!this.outputStack.isEmpty() && this.catalystLeft < this.yieldRate) {
            changed |= this.consumeCatalyst();
        }
        if (this.progress >= MAX_PROGRESS) {
            changed = true;
            this.progress = 0;
            this.outputStack = ItemStack.EMPTY;
            this.yieldRate = 0.0F;
        }

        if (changed) {
            this.setChanged();
        }
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return this.items;
    }

    public ItemStack getStackInSlot(final int slot) {
        if (!isMachineSlot(slot)) {
            return ItemStack.EMPTY;
        }
        return this.items.stack(slot);
    }

    public void setStackInSlot(final int slot, final ItemStack stack) {
        if (!isMachineSlot(slot)) {
            return;
        }
        final ItemStack stored = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        this.items.setStack(slot, stored);
        this.updateFilterFromMenu(slot, stored);
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
        this.clearMenuFilterIfInputEmpty(slot);
        this.setChanged();
        return removed;
    }

    public ItemStack removeStackNoUpdate(final int slot) {
        if (!isMachineSlot(slot)) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.items.stack(slot);
        this.items.setStack(slot, ItemStack.EMPTY);
        this.clearMenuFilterIfInputEmpty(slot);
        return removed;
    }

    public boolean mayPlaceInSlot(final int slot, final ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (slot == CATALYST_SLOT) {
            return isCatalyst(stack);
        }
        return isInputSlot(slot) && slot != OUTPUT_SLOT;
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

    public void setFilter(final int index, final ItemStack stack) {
        if (index < 0 || index >= INPUT_SLOT_COUNT) {
            return;
        }
        this.filter.setStack(index, stack.copy());
    }

    public ItemStack getFilterStack(final int index) {
        if (index < 0 || index >= INPUT_SLOT_COUNT) {
            return ItemStack.EMPTY;
        }
        return this.filter.stack(index).copy();
    }

    public int getProgress() {
        return this.progress;
    }

    public double getCurrentYield() {
        return this.yieldAmount;
    }

    public float getCurrentCatalystYield() {
        return this.catalystYield;
    }

    public float getCurrentCatalystLeft() {
        return this.catalystLeft;
    }

    public void clearStoredCatalyst() {
        this.catalystLeft = 0.0F;
        this.yieldAmount = 0.0D;
        this.setChanged();
    }

    public static boolean isCatalyst(final ItemStack stack) {
        return getCatalystValue(stack) > 0.0F;
    }

    private boolean tryStartRecipe(final ServerLevel level) {
        final List<ItemStack> inputs = this.getStacksForRecipe();
        if (inputs.isEmpty()) {
            return false;
        }
        return ProcessRecipes.find(level, ProcessRecipes.FUSION, inputs)
                .map(this::startRecipe)
                .orElse(false);
    }

    private boolean startRecipe(final SkyResourcesProcessRecipe recipe) {
        final ItemStack output = recipe.outputs().get(0);
        if (output.isEmpty() || recipe.parameter() <= 0.0F) {
            return false;
        }
        if (!this.consumeRecipeInputs(recipe.inputs())) {
            return false;
        }
        this.outputStack = output.copy();
        this.yieldRate = recipe.parameter();
        return true;
    }

    private List<ItemStack> getStacksForRecipe() {
        final List<ItemStack> stacks = new ArrayList<>(INPUT_SLOT_COUNT);
        for (int slot = FIRST_INPUT_SLOT; slot < OUTPUT_SLOT; slot++) {
            final ItemStack stack = this.items.stack(slot);
            if (!stack.isEmpty()) {
                stacks.add(stack.copy());
            }
        }
        return stacks;
    }

    private boolean consumeRecipeInputs(final List<ProcessIngredient> ingredients) {
        final boolean[] usedSlots = new boolean[INPUT_SLOT_COUNT];
        for (final ProcessIngredient ingredient : ingredients) {
            final int slot = this.findInputSlotFor(ingredient, usedSlots);
            if (slot < 0) {
                return false;
            }
            usedSlots[slot - FIRST_INPUT_SLOT] = true;
            final ItemStack stack = this.items.stack(slot);
            stack.shrink(ingredient.count());
            if (stack.isEmpty()) {
                this.items.setStack(slot, ItemStack.EMPTY);
            }
        }
        return true;
    }

    private int findInputSlotFor(final ProcessIngredient ingredient, final boolean[] usedSlots) {
        for (int slot = FIRST_INPUT_SLOT; slot < OUTPUT_SLOT; slot++) {
            if (!usedSlots[slot - FIRST_INPUT_SLOT] && ingredient.matches(this.items.stack(slot))) {
                return slot;
            }
        }
        return -1;
    }

    private boolean canAdvanceCraft() {
        return this.progress < MAX_PROGRESS
                && !this.outputStack.isEmpty()
                && this.catalystLeft >= this.yieldRate
                && this.canFitOutput(this.outputStack);
    }

    private void tryInsertYieldedOutput() {
        final int crafts = (int) Math.floor(this.yieldAmount);
        if (crafts <= 0) {
            return;
        }

        final int insertedCrafts = Math.min(crafts, this.getOutputCraftsSpace(this.outputStack));
        if (insertedCrafts <= 0) {
            return;
        }

        final ItemStack output = this.outputStack.copyWithCount(this.outputStack.getCount() * insertedCrafts);
        this.insertOutput(output);
        this.yieldAmount -= insertedCrafts;
    }

    private boolean consumeCatalyst() {
        final ItemStack catalyst = this.items.stack(CATALYST_SLOT);
        if (!isCatalyst(catalyst)) {
            return false;
        }
        this.catalystLeft += 1.0F;
        this.catalystYield = getCatalystValue(catalyst);
        catalyst.shrink(1);
        if (catalyst.isEmpty()) {
            this.items.setStack(CATALYST_SLOT, ItemStack.EMPTY);
        }
        return true;
    }

    private boolean canFitOutput(final ItemStack stack) {
        return this.getOutputCraftsSpace(stack) > 0;
    }

    private int getOutputCraftsSpace(final ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        final ItemStack output = this.items.stack(OUTPUT_SLOT);
        final int countPerCraft = stack.getCount();
        if (output.isEmpty()) {
            return stack.getMaxStackSize() / countPerCraft;
        }
        if (!ItemStack.isSameItemSameTags(output, stack)) {
            return 0;
        }
        return (output.getMaxStackSize() - output.getCount()) / countPerCraft;
    }

    private void insertOutput(final ItemStack stack) {
        final ItemStack output = this.items.stack(OUTPUT_SLOT);
        if (output.isEmpty()) {
            this.items.setStack(OUTPUT_SLOT, stack.copy());
            return;
        }
        output.grow(stack.getCount());
    }

    public static float getCatalystValue(final ItemStack stack) {
        if (stack.isEmpty()) {
            return 0.0F;
        }
        if (stack.getItem() == ModItems.PRIMUS_ALCHEMICAL_DUST.get()) {
            return 0.75F;
        }
        if (stack.getItem() == ModItems.SECUNDUS_ALCHEMICAL_DUST.get()) {
            return 1.75F;
        }
        if (stack.getItem() == ModItems.TERTIUS_ALCHEMICAL_DUST.get()) {
            return 4.5F;
        }
        if (stack.getItem() == ModItems.QUARTUS_ALCHEMICAL_DUST.get()) {
            return 32.0F;
        }
        return 0.0F;
    }

    private static boolean matchesFilter(final ItemStack filterStack, final ItemResource resource) {
        return !filterStack.isEmpty() && ItemStack.isSameItemSameTags(filterStack, resource.toStack());
    }

    private static boolean isMachineSlot(final int slot) {
        return slot >= 0 && slot < SLOT_COUNT;
    }

    private static boolean isInputSlot(final int slot) {
        return slot >= FIRST_INPUT_SLOT && slot < OUTPUT_SLOT;
    }

    private static double roundYield(final double value) {
        return Math.round(value * YIELD_ROUNDING_SCALE) / (double) YIELD_ROUNDING_SCALE;
    }

    private void updateFilterFromMenu(final int slot, final ItemStack stack) {
        if (!isInputSlot(slot)) {
            return;
        }
        this.setFilter(slot - FIRST_INPUT_SLOT, stack);
    }

    private void clearMenuFilterIfInputEmpty(final int slot) {
        if (isInputSlot(slot) && this.items.stack(slot).isEmpty()) {
            this.setFilter(slot - FIRST_INPUT_SLOT, ItemStack.EMPTY);
        }
    }

    private static class StoredItemStacks extends ItemStacksResourceHandler {
        protected final FusionTableBlockEntity owner;
        private final int expectedSize;

        StoredItemStacks(final FusionTableBlockEntity owner, final int size) {
            super(size);
            this.owner = owner;
            this.expectedSize = size;
        }

        @Override
        public void deserialize(final ValueInput input) {
            super.deserialize(input);
            if (this.size() != this.expectedSize) {
                this.setStacks(NonNullList.withSize(this.expectedSize, ItemStack.EMPTY));
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

    private static final class FusionTableItemHandler extends StoredItemStacks {
        private FusionTableItemHandler(final FusionTableBlockEntity owner) {
            super(owner, SLOT_COUNT);
        }

        @Override
        public boolean isValid(final int index, final ItemResource resource) {
            if (resource.isEmpty()) {
                return false;
            }
            if (index == CATALYST_SLOT) {
                return isCatalyst(resource.toStack());
            }
            if (index >= FIRST_INPUT_SLOT && index < OUTPUT_SLOT) {
                return matchesFilter(this.owner.filter.stack(index - FIRST_INPUT_SLOT), resource);
            }
            return false;
        }

        @Override
        public int extract(
                final int index,
                final ItemResource resource,
                final int amount,
                final TransactionContext transaction
        ) {
            if (index != OUTPUT_SLOT) {
                return 0;
            }
            return super.extract(index, resource, amount, transaction);
        }
    }
}
