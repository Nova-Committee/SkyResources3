package committee.nova.mods.skyresources3.common.block.entity;

import committee.nova.mods.skyresources3.common.block.FreezerBlock;
import committee.nova.mods.skyresources3.common.recipe.ProcessIngredient;
import committee.nova.mods.skyresources3.common.recipe.ProcessRecipes;
import committee.nova.mods.skyresources3.common.recipe.SkyResourcesProcessRecipe;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import java.util.List;
import java.util.Optional;
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

public final class FreezerBlockEntity extends BlockEntity {
    private static final String ITEMS_KEY = "items";
    private static final String PROGRESS_KEY_PREFIX = "progress";
    private static final int PROGRESS_SCALE = 1_000;

    private final FreezerBlock.Tier tier;
    private final FreezerItemHandler items;
    private final float[] progress;

    public FreezerBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.FREEZER.get(), pos, blockState);
        this.tier = blockState.getBlock() instanceof FreezerBlock freezerBlock
                ? freezerBlock.tier()
                : FreezerBlock.Tier.MINI;
        this.items = new FreezerItemHandler(this);
        this.progress = new float[this.tier.inputCount()];
    }

    @Override
    public void load(final net.minecraft.nbt.CompoundTag tag) {
        super.load(tag);
        final ValueInput input = new ValueInput(tag);
        input.readChild(ITEMS_KEY, this.items);
        for (int index = 0; index < this.progress.length; index++) {
            this.progress[index] = input.getFloatOr(PROGRESS_KEY_PREFIX + index, 0.0F);
        }
    }

    @Override
    protected void saveAdditional(final net.minecraft.nbt.CompoundTag tag) {
        super.saveAdditional(tag);
        final ValueOutput output = new ValueOutput(tag);
        output.putChild(ITEMS_KEY, this.items);
        for (int index = 0; index < this.progress.length; index++) {
            output.putFloat(PROGRESS_KEY_PREFIX + index, this.progress[index]);
        }
    }

    public void serverTick(final ServerLevel level) {
        if (!this.isController()) {
            return;
        }
        this.updateMultiblockState(level);
        if (!this.hasValidMultiblock()) {
            return;
        }

        boolean changed = false;
        for (int inputSlot = 0; inputSlot < this.getInputCount(); inputSlot++) {
            changed |= this.tickInput(level, inputSlot);
        }
        if (changed) {
            this.setChanged();
        }
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return this.getController().items;
    }

    public FreezerBlock.Tier getTier() {
        return this.tier;
    }

    public int getSlotCount() {
        return this.tier.slotCount();
    }

    public int getInputCount() {
        return this.tier.inputCount();
    }

    public float getSpeed() {
        return this.tier.speed();
    }

    public boolean requiresMultiblock() {
        return this.tier.requiresMultiblock();
    }

    public boolean isController() {
        if (!this.tier.requiresMultiblock()) {
            return true;
        }
        return this.getBlockState().getBlock() instanceof FreezerBlock
                && this.getBlockState().getValue(FreezerBlock.PART) == FreezerBlock.FreezerPart.BOTTOM;
    }

    public boolean hasValidMultiblock() {
        if (!this.tier.requiresMultiblock()) {
            return true;
        }
        if (!this.isController() || this.level == null) {
            return false;
        }

        final BlockState bottom = this.getBlockState();
        final BlockState top = this.level.getBlockState(this.worldPosition.above());
        return top.is(bottom.getBlock())
                && top.getValue(FreezerBlock.PART) == FreezerBlock.FreezerPart.TOP
                && top.getValue(FreezerBlock.FACING) == bottom.getValue(FreezerBlock.FACING);
    }

    public ItemStack getStackInSlot(final int slot) {
        return this.isMachineSlot(slot) ? this.items.stack(slot) : ItemStack.EMPTY;
    }

    public void setStackInSlot(final int slot, final ItemStack stack) {
        if (!this.isMachineSlot(slot)) {
            return;
        }
        this.items.setStack(slot, stack);
        if (slot < this.getInputCount()) {
            this.progress[slot] = 0.0F;
        }
    }

    public ItemStack removeStack(final int slot, final int amount) {
        if (!this.isMachineSlot(slot) || amount <= 0) {
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
        if (slot < this.getInputCount()) {
            this.progress[slot] = 0.0F;
        }
        this.setChanged();
        return removed;
    }

    public ItemStack removeStackNoUpdate(final int slot) {
        if (!this.isMachineSlot(slot)) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.items.stack(slot);
        this.items.setStack(slot, ItemStack.EMPTY);
        if (slot < this.getInputCount()) {
            this.progress[slot] = 0.0F;
        }
        return removed;
    }

    public boolean mayPlaceInSlot(final int slot, final ItemStack stack) {
        return !stack.isEmpty() && slot >= 0 && slot < this.getInputCount();
    }

    public int getProgressRatioScaled(final int inputSlot) {
        if (this.level instanceof ServerLevel serverLevel) {
            final Optional<SkyResourcesProcessRecipe> recipe = this.recipeToCraft(serverLevel, inputSlot);
            if (recipe.isPresent()) {
                final int required = this.getTimeRequired(recipe.get(), this.items.stack(inputSlot));
                if (required > 0) {
                    return Math.min(PROGRESS_SCALE, Math.round(this.progress[inputSlot] * PROGRESS_SCALE / required));
                }
            }
        }
        return 0;
    }

    public void dropContents() {
        if (this.level == null) {
            return;
        }
        final SimpleContainer container = new SimpleContainer(this.getSlotCount());
        for (int slot = 0; slot < this.getSlotCount(); slot++) {
            container.setItem(slot, this.items.stack(slot).copy());
        }
        Containers.dropContents(this.level, this.worldPosition, container);
    }

    private boolean tickInput(final ServerLevel level, final int inputSlot) {
        final Optional<SkyResourcesProcessRecipe> recipe = this.recipeToCraft(level, inputSlot);
        if (recipe.isEmpty()) {
            this.progress[inputSlot] = 0.0F;
            return false;
        }

        final ItemStack input = this.items.stack(inputSlot);
        final int outputSlot = inputSlot + this.getInputCount();
        final ItemStack output = recipe.get().outputs().get(0);
        if (output.isEmpty() || !this.canFitOutput(output, outputSlot)) {
            return false;
        }

        final int required = this.getTimeRequired(recipe.get(), input);
        if (this.progress[inputSlot] >= required) {
            final int processed = this.ejectFinishedGroups(recipe.get(), inputSlot, outputSlot);
            if (processed > 0) {
                final int inputCount = recipe.get().inputs().get(0).count();
                input.shrink(inputCount * processed);
                if (input.isEmpty()) {
                    this.items.setStack(inputSlot, ItemStack.EMPTY);
                }
            }
            this.progress[inputSlot] = 0.0F;
            return processed > 0;
        }

        this.progress[inputSlot] += this.tier.speed() * 10.0F;
        return true;
    }

    private Optional<SkyResourcesProcessRecipe> recipeToCraft(final ServerLevel level, final int inputSlot) {
        if (inputSlot < 0 || inputSlot >= this.getInputCount()) {
            return Optional.empty();
        }
        final ItemStack input = this.items.stack(inputSlot);
        if (input.isEmpty()) {
            return Optional.empty();
        }
        return ProcessRecipes.find(level, ProcessRecipes.FREEZER, List.of(input.copy()))
                .map(recipe -> recipe);
    }

    private int getTimeRequired(final SkyResourcesProcessRecipe recipe, final ItemStack input) {
        return Math.max(1, Math.round(recipe.parameter() * this.getGroupsFreezing(recipe, input)));
    }

    private int getGroupsFreezing(final SkyResourcesProcessRecipe recipe, final ItemStack input) {
        final ProcessIngredient ingredient = recipe.inputs().get(0);
        return input.getCount() / ingredient.count();
    }

    private int ejectFinishedGroups(
            final SkyResourcesProcessRecipe recipe,
            final int inputSlot,
            final int outputSlot
    ) {
        int processed = 0;
        final int groups = this.getGroupsFreezing(recipe, this.items.stack(inputSlot));
        for (int group = 0; group < groups; group++) {
            final ItemStack output = recipe.outputs().get(0).copy();
            if (!this.insertOutput(output, outputSlot)) {
                break;
            }
            processed++;
        }
        return processed;
    }

    private boolean canFitOutput(final ItemStack output, final int outputSlot) {
        final ItemStack current = this.items.stack(outputSlot);
        if (current.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameTags(current, output)) {
            return false;
        }
        final int result = current.getCount() + output.getCount();
        return result <= current.getMaxStackSize();
    }

    private boolean insertOutput(final ItemStack output, final int outputSlot) {
        if (!this.canFitOutput(output, outputSlot)) {
            return false;
        }
        final ItemStack current = this.items.stack(outputSlot);
        if (current.isEmpty()) {
            this.items.setStack(outputSlot, output);
            return true;
        }
        current.grow(output.getCount());
        return true;
    }

    private void updateMultiblockState(final ServerLevel level) {
        if (!this.tier.requiresMultiblock() || !this.isController()) {
            return;
        }

        final BlockState bottom = this.getBlockState();
        final BlockState top = level.getBlockState(this.worldPosition.above());
        if (top.is(bottom.getBlock()) && top.getValue(FreezerBlock.PART) == FreezerBlock.FreezerPart.BOTTOM) {
            level.setBlock(
                    this.worldPosition.above(),
                    top.setValue(FreezerBlock.PART, FreezerBlock.FreezerPart.TOP)
                            .setValue(FreezerBlock.FACING, bottom.getValue(FreezerBlock.FACING)),
                    3
            );
        }
    }

    private FreezerBlockEntity getController() {
        if (this.isController() || this.level == null || !this.tier.requiresMultiblock()) {
            return this;
        }
        if (this.level.getBlockEntity(this.worldPosition.below()) instanceof FreezerBlockEntity freezer
                && freezer.getTier() == this.tier) {
            return freezer;
        }
        return this;
    }

    private boolean isMachineSlot(final int slot) {
        return slot >= 0 && slot < this.getSlotCount();
    }

    private static class StoredItemStacks extends ItemStacksResourceHandler {
        protected final FreezerBlockEntity owner;

        StoredItemStacks(final FreezerBlockEntity owner) {
            super(owner.getSlotCount());
            this.owner = owner;
        }

        @Override
        public void deserialize(final ValueInput input) {
            super.deserialize(input);
            if (this.size() != this.owner.getSlotCount()) {
                this.setStacks(NonNullList.withSize(this.owner.getSlotCount(), ItemStack.EMPTY));
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

    private static final class FreezerItemHandler extends StoredItemStacks {
        private FreezerItemHandler(final FreezerBlockEntity owner) {
            super(owner);
        }

        @Override
        public boolean isValid(final int index, final ItemResource resource) {
            return !resource.isEmpty() && index >= 0 && index < this.owner.getInputCount();
        }

        @Override
        public int extract(
                final int index,
                final ItemResource resource,
                final int amount,
                final TransactionContext transaction
        ) {
            if (index < this.owner.getInputCount()) {
                return 0;
            }
            return super.extract(index, resource, amount, transaction);
        }
    }
}
