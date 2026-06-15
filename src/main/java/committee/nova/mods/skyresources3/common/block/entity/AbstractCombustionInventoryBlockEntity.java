package committee.nova.mods.skyresources3.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public abstract class AbstractCombustionInventoryBlockEntity extends BlockEntity {
    public static final int SLOT_COUNT = 5;
    private static final String ITEMS_KEY = "items";

    private final StoredItemStacks items = new StoredItemStacks(this);

    protected AbstractCombustionInventoryBlockEntity(
            final BlockEntityType<?> type,
            final BlockPos pos,
            final BlockState blockState
    ) {
        super(type, pos, blockState);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        input.readChild(ITEMS_KEY, this.items);
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(ITEMS_KEY, this.items);
    }

    @Override
    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        this.dropContents();
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return this.items;
    }

    public ItemStack getStackInSlot(final int slot) {
        return isMachineSlot(slot) ? this.items.stack(slot) : ItemStack.EMPTY;
    }

    public void setStackInSlot(final int slot, final ItemStack stack) {
        if (!isMachineSlot(slot)) {
            return;
        }
        this.items.setStack(slot, this.normalizeStackForSlot(slot, stack));
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
        this.setChanged();
        return removed;
    }

    public ItemStack removeStackNoUpdate(final int slot) {
        if (!isMachineSlot(slot)) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.items.stack(slot);
        this.items.setStack(slot, ItemStack.EMPTY);
        return removed;
    }

    public boolean mayPlaceInSlot(final int slot, final ItemStack stack) {
        return isMachineSlot(slot) && !stack.isEmpty();
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
        this.clearItems();
    }

    protected ItemStack insertIntoInventory(final ItemStack stack) {
        ItemStack remaining = stack.copy();
        for (int slot = 0; slot < SLOT_COUNT && !remaining.isEmpty(); slot++) {
            remaining = this.insertIntoSlot(slot, remaining);
        }
        return remaining;
    }

    protected int slotCapacity(final int slot, final ItemResource resource) {
        return resource.isEmpty() ? 0 : resource.toStack().getMaxStackSize();
    }

    public int getSlotLimit(final int slot, final ItemStack stack) {
        return isMachineSlot(slot) && !stack.isEmpty() ? this.slotCapacity(slot, ItemResource.of(stack)) : 0;
    }

    protected ItemStack normalizeStackForSlot(final int slot, final ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return stack.copyWithCount(Math.min(stack.getCount(), this.slotCapacity(slot, ItemResource.of(stack))));
    }

    public static boolean isMachineSlot(final int slot) {
        return slot >= 0 && slot < SLOT_COUNT;
    }

    private ItemStack insertIntoSlot(final int slot, final ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final ItemStack current = this.items.stack(slot);
        if (current.isEmpty()) {
            final int moved = Math.min(stack.getCount(), this.slotCapacity(slot, ItemResource.of(stack)));
            if (moved <= 0) {
                return stack;
            }
            this.items.setStack(slot, stack.copyWithCount(moved));
            final ItemStack remaining = stack.copy();
            remaining.shrink(moved);
            return remaining;
        }
        if (!ItemStack.isSameItemSameComponents(current, stack)) {
            return stack;
        }
        final int moved = Math.min(stack.getCount(), this.slotCapacity(slot, ItemResource.of(stack)) - current.getCount());
        if (moved <= 0) {
            return stack;
        }
        current.grow(moved);
        this.setChanged();
        final ItemStack remaining = stack.copy();
        remaining.shrink(moved);
        return remaining;
    }

    private void clearItems() {
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            this.items.setStack(slot, ItemStack.EMPTY);
        }
    }

    private static final class StoredItemStacks extends ItemStacksResourceHandler {
        private final AbstractCombustionInventoryBlockEntity owner;

        private StoredItemStacks(final AbstractCombustionInventoryBlockEntity owner) {
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
        public boolean isValid(final int index, final ItemResource resource) {
            return isMachineSlot(index) && this.owner.mayPlaceInSlot(index, resource.toStack());
        }

        @Override
        protected int getCapacity(final int index, final ItemResource resource) {
            return Math.min(super.getCapacity(index, resource), this.owner.slotCapacity(index, resource));
        }

        @Override
        protected void onContentsChanged(final int index, final ItemStack previousContents) {
            this.owner.setChanged();
        }

        private ItemStack stack(final int slot) {
            return this.stacks.get(slot);
        }

        private void setStack(final int slot, final ItemStack stack) {
            this.stacks.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
            this.owner.setChanged();
        }
    }
}
