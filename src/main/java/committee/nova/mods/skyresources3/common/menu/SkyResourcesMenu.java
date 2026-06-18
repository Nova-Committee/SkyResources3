package committee.nova.mods.skyresources3.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

abstract class SkyResourcesMenu extends AbstractContainerMenu {
    protected SkyResourcesMenu(final MenuType<?> menuType, final int containerId) {
        super(menuType, containerId);
    }

    protected void addStandardInventorySlots(final Inventory inventory, final int left, final int top) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, left + column * 18, top + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inventory, column, left + column * 18, top + 58));
        }
    }
}
