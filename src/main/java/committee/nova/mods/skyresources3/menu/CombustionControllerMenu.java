package committee.nova.mods.skyresources3.menu;

import committee.nova.mods.skyresources3.block.entity.AbstractCombustionInventoryBlockEntity;
import committee.nova.mods.skyresources3.block.entity.CombustionControllerBlockEntity;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class CombustionControllerMenu extends AbstractCombustionInventoryMenu {
    public CombustionControllerMenu(
            final int containerId,
            final Inventory playerInventory,
            final RegistryFriendlyByteBuf data
    ) {
        super(
                ModMenuTypes.COMBUSTION_CONTROLLER.get(),
                ModBlocks.COMBUSTION_CONTROLLER.get(),
                containerId,
                playerInventory,
                readClientData(playerInventory, data)
        );
    }

    public CombustionControllerMenu(
            final int containerId,
            final Inventory playerInventory,
            final CombustionControllerBlockEntity blockEntity
    ) {
        super(
                ModMenuTypes.COMBUSTION_CONTROLLER.get(),
                ModBlocks.COMBUSTION_CONTROLLER.get(),
                containerId,
                playerInventory,
                new ClientData(
                        blockEntity.getBlockPos(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos())
                )
        );
    }

    @Override
    public void clicked(final int slotId, final int button, final ClickType clickType, final Player player) {
        if (this.isFilterSlot(slotId)) {
            this.handleFilterClick(slotId, button, clickType, player);
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        if (index < 0 || index >= this.slots.size()) {
            return ItemStack.EMPTY;
        }
        if (this.isFilterSlot(index)) {
            this.setFilter(index, ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }

        final ItemStack stack = this.slots.get(index).getItem();
        if (!stack.isEmpty()) {
            this.copyToFirstEmptyFilter(stack);
        }
        return ItemStack.EMPTY;
    }

    private void handleFilterClick(
            final int slotId,
            final int button,
            final ClickType clickType,
            final Player player
    ) {
        if (clickType == ClickType.PICKUP) {
            this.setFilter(slotId, this.getCarried());
            return;
        }
        if (clickType == ClickType.SWAP && button >= 0 && button < Inventory.getSelectionSize()) {
            this.setFilter(slotId, player.getInventory().getItem(button));
            return;
        }
        if (clickType == ClickType.QUICK_MOVE || clickType == ClickType.THROW) {
            this.setFilter(slotId, ItemStack.EMPTY);
        }
    }

    private void copyToFirstEmptyFilter(final ItemStack stack) {
        for (int slot = 0; slot < AbstractCombustionInventoryBlockEntity.SLOT_COUNT; slot++) {
            if (!this.slots.get(slot).hasItem()) {
                this.setFilter(slot, stack);
                return;
            }
        }
    }

    private void setFilter(final int slotId, final ItemStack stack) {
        final Slot slot = this.slots.get(slotId);
        final ItemStack filter = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        slot.setByPlayer(filter);
        slot.setChanged();
    }

    private boolean isFilterSlot(final int slotId) {
        return slotId >= 0 && slotId < AbstractCombustionInventoryBlockEntity.SLOT_COUNT;
    }
}
