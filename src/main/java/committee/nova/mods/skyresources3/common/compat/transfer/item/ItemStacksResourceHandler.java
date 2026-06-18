package committee.nova.mods.skyresources3.common.compat.transfer.item;

import committee.nova.mods.skyresources3.common.compat.LegacyNbtSerializable;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.transaction.TransactionContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ItemStacksResourceHandler extends ItemStackHandler
        implements ResourceHandler<ItemResource>, LegacyNbtSerializable {
    public ItemStacksResourceHandler(final int size) {
        super(size);
    }

    public int size() {
        return this.stacks.size();
    }

    public void setStacks(final NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
        this.onLoad();
    }

    public boolean isValid(final int index, final ItemResource resource) {
        return !resource.isEmpty();
    }

    protected int getCapacity(final int index, final ItemResource resource) {
        if (index < 0 || index >= this.getSlots()) {
            return 0;
        }
        return Math.min(super.getSlotLimit(index), resource.toStack().getMaxStackSize());
    }

    @Override
    public boolean isItemValid(final int slot, final ItemStack stack) {
        return this.isValid(slot, ItemResource.of(stack));
    }

    public int insert(final ItemResource resource, final int amount, final TransactionContext transaction) {
        if (resource.isEmpty() || amount <= 0) {
            return 0;
        }
        int remaining = amount;
        for (int slot = 0; slot < this.getSlots() && remaining > 0; slot++) {
            remaining -= this.insert(slot, resource, remaining, transaction);
        }
        return amount - remaining;
    }

    public int insert(
            final int index,
            final ItemResource resource,
            final int amount,
            final TransactionContext transaction
    ) {
        if (!this.isValid(index, resource) || amount <= 0) {
            return 0;
        }
        final int insertable = Math.min(amount, this.getCapacity(index, resource));
        final ItemStack stack = resource.toStack().copyWithCount(insertable);
        final ItemStack remainder = this.insertItem(index, stack, transaction != null && transaction.isSimulation());
        return insertable - remainder.getCount();
    }

    public int extract(
            final int index,
            final ItemResource resource,
            final int amount,
            final TransactionContext transaction
    ) {
        if (resource.isEmpty() || amount <= 0) {
            return 0;
        }
        final ItemStack current = this.getStackInSlot(index);
        if (current.isEmpty() || !ItemStack.isSameItemSameComponents(current, resource.toStack())) {
            return 0;
        }
        return this.extractItem(index, amount, transaction != null && transaction.isSimulation()).getCount();
    }

    @Override
    public void setStackInSlot(final int slot, final ItemStack stack) {
        final ItemStack previous = this.stacks.get(slot).copy();
        super.setStackInSlot(slot, stack);
        this.onContentsChanged(slot, previous);
    }

    @Override
    public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
        final ItemStack previous = this.stacks.get(slot).copy();
        final ItemStack remainder = super.insertItem(slot, stack, simulate);
        if (!simulate && !ItemStack.matches(previous, this.stacks.get(slot))) {
            this.onContentsChanged(slot, previous);
        }
        return remainder;
    }

    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
        final ItemStack previous = this.stacks.get(slot).copy();
        final ItemStack extracted = super.extractItem(slot, amount, simulate);
        if (!simulate && !ItemStack.matches(previous, this.stacks.get(slot))) {
            this.onContentsChanged(slot, previous);
        }
        return extracted;
    }

    @Override
    public void deserialize(final ValueInput input) {
        this.deserializeNBT(input.registries(), input.tag().getCompound("value"));
    }

    @Override
    public CompoundTag serialize(final HolderLookup.Provider registries) {
        final CompoundTag tag = new CompoundTag();
        tag.put("value", this.serializeNBT(registries));
        return tag;
    }

    protected void onContentsChanged(final int index, final ItemStack previousContents) {
    }
}
