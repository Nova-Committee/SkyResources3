package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.common.menu.CombustionCollectorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class CombustionCollectorScreen extends AbstractCombustionInventoryScreen<CombustionCollectorMenu> {
    public CombustionCollectorScreen(
            final CombustionCollectorMenu menu,
            final Inventory playerInventory,
            final Component title
    ) {
        super(menu, playerInventory, title);
    }
}
