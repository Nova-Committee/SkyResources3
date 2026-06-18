package committee.nova.mods.skyresources3.common.compat.transfer;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.minecraft.world.item.ItemStack;
import committee.nova.mods.skyresources3.common.compat.transfer.transaction.TransactionContext;

public interface ResourceHandler<T> extends IItemHandler, IFluidHandler {
    default int insert(final T resource, final int amount, final TransactionContext transaction) {
        return 0;
    }

    default int insert(
            final int index,
            final T resource,
            final int amount,
            final TransactionContext transaction
    ) {
        return 0;
    }

    default int extract(
            final int index,
            final T resource,
            final int amount,
            final TransactionContext transaction
    ) {
        return 0;
    }

    @Override
    default int getSlots() {
        return 0;
    }

    @Override
    default ItemStack getStackInSlot(final int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    default ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
        return stack;
    }

    @Override
    default ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    default int getSlotLimit(final int slot) {
        return 0;
    }

    @Override
    default boolean isItemValid(final int slot, final ItemStack stack) {
        return false;
    }

    @Override
    default int getTanks() {
        return 0;
    }

    @Override
    default FluidStack getFluidInTank(final int tank) {
        return FluidStack.EMPTY;
    }

    @Override
    default int getTankCapacity(final int tank) {
        return 0;
    }

    @Override
    default boolean isFluidValid(final int tank, final FluidStack stack) {
        return false;
    }

    @Override
    default int fill(final FluidStack resource, final FluidAction action) {
        return 0;
    }

    @Override
    default FluidStack drain(final FluidStack resource, final FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    default FluidStack drain(final int maxDrain, final FluidAction action) {
        return FluidStack.EMPTY;
    }
}
