package committee.nova.mods.skyresources3.common.compat.transfer.item;

import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import java.util.stream.IntStream;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class WorldlyContainerWrapper implements ResourceHandler<ItemResource> {
    private final WorldlyContainer container;
    private final @Nullable Direction side;

    public WorldlyContainerWrapper(final WorldlyContainer container, @Nullable final Direction side) {
        this.container = container;
        this.side = side;
    }

    @Override
    public int getSlots() {
        return this.slots().length;
    }

    @Override
    public ItemStack getStackInSlot(final int slot) {
        final int containerSlot = this.containerSlot(slot);
        return containerSlot < 0 ? ItemStack.EMPTY : this.container.getItem(containerSlot);
    }

    @Override
    public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final int containerSlot = this.containerSlot(slot);
        if (containerSlot < 0 || !this.canPlace(containerSlot, stack)) {
            return stack;
        }

        final ItemStack current = this.container.getItem(containerSlot);
        if (!current.isEmpty() && !ItemStack.isSameItemSameTags(current, stack)) {
            return stack;
        }

        final int limit = Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
        final int inserted = Math.min(stack.getCount(), limit - current.getCount());
        if (inserted <= 0) {
            return stack;
        }

        if (!simulate) {
            final ItemStack next = current.isEmpty() ? stack.copyWithCount(inserted) : current.copy();
            if (!current.isEmpty()) {
                next.grow(inserted);
            }
            this.container.setItem(containerSlot, next);
            this.container.setChanged();
        }

        final ItemStack remainder = stack.copy();
        remainder.shrink(inserted);
        return remainder;
    }

    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }
        final int containerSlot = this.containerSlot(slot);
        if (containerSlot < 0) {
            return ItemStack.EMPTY;
        }
        final ItemStack current = this.container.getItem(containerSlot);
        if (current.isEmpty() || !this.canTake(containerSlot, current)) {
            return ItemStack.EMPTY;
        }

        final ItemStack extracted = current.copyWithCount(Math.min(amount, current.getCount()));
        if (!simulate) {
            current.shrink(extracted.getCount());
            if (current.isEmpty()) {
                this.container.setItem(containerSlot, ItemStack.EMPTY);
            }
            this.container.setChanged();
        }
        return extracted;
    }

    @Override
    public int getSlotLimit(final int slot) {
        return this.containerSlot(slot) < 0 ? 0 : this.container.getMaxStackSize();
    }

    @Override
    public boolean isItemValid(final int slot, final ItemStack stack) {
        final int containerSlot = this.containerSlot(slot);
        return containerSlot >= 0 && this.canPlace(containerSlot, stack);
    }

    private int containerSlot(final int exposedSlot) {
        final int[] slots = this.slots();
        return exposedSlot < 0 || exposedSlot >= slots.length ? -1 : slots[exposedSlot];
    }

    private int[] slots() {
        if (this.side != null) {
            return this.container.getSlotsForFace(this.side);
        }
        return IntStream.range(0, this.container.getContainerSize()).toArray();
    }

    private boolean canPlace(final int slot, final ItemStack stack) {
        return this.container.canPlaceItem(slot, stack)
                && (this.side == null || this.container.canPlaceItemThroughFace(slot, stack, this.side));
    }

    private boolean canTake(final int slot, final ItemStack stack) {
        return this.side == null || this.container.canTakeItemThroughFace(slot, stack, this.side);
    }
}
