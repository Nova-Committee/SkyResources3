package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.menu.CombustionControllerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class CombustionControllerScreen extends AbstractCombustionInventoryScreen<CombustionControllerMenu> {
    public CombustionControllerScreen(
            final CombustionControllerMenu menu,
            final Inventory playerInventory,
            final Component title
    ) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderLabels(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        final Component filterText = Component.translatable("screen.skyresources3.combustion_controller.filter");
        guiGraphics.drawString(
                this.font,
                filterText,
                (this.imageWidth - this.font.width(filterText)) / 2,
                40,
                4210752,
                false
        );
    }
}
