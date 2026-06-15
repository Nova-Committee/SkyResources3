package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.common.menu.CombustionControllerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class CombustionControllerScreen extends AbstractCombustionInventoryScreen<CombustionControllerMenu> {
    private static final int FILTER_TEXT_Y = 40;

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
        MachineGuiTheme.renderMetricChip(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.filter"),
                Component.translatable("screen.skyresources.metric.priority"),
                47,
                FILTER_TEXT_Y - 4,
                82,
                MachineGuiTheme.PROGRESS
        );
    }

    @Override
    protected void renderComponentTooltips(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(0, FILTER_TEXT_Y - 1, this.imageWidth, this.font.lineHeight + 2, mouseX, mouseY)) {
            return;
        }
        GuiTooltips.render(
                guiGraphics,
                this.font,
                mouseX,
                mouseY,
                Component.translatable("screen.skyresources.combustion_controller.filter.tooltip")
        );
    }
}
