package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.client.utils.GuiTooltips;
import committee.nova.mods.skyresources3.client.utils.MachineGuiTheme;
import committee.nova.mods.skyresources3.common.menu.EndPortalCoreMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class EndPortalCoreScreen extends AbstractContainerScreen<EndPortalCoreMenu> {
    private static final int STATUS_X = 20;
    private static final int STATUS_Y = 30;
    private static final int STATUS_WIDTH = 136;
    private static final int STATUS_HEIGHT = 18;

    public EndPortalCoreScreen(
            final EndPortalCoreMenu menu,
            final Inventory playerInventory,
            final Component title
    ) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 72;
    }

    @Override
    protected void renderBg(
            final GuiGraphics guiGraphics,
            final float partialTick,
            final int mouseX,
            final int mouseY
    ) {
        MachineGuiTheme.renderPanel(guiGraphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        MachineGuiTheme.renderSlots(guiGraphics, this.leftPos, this.topPos, this.menu.slots);
    }

    @Override
    protected void renderLabels(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        MachineGuiTheme.renderTitle(guiGraphics, this.font, this.title, this.imageWidth);
        MachineGuiTheme.renderStatus(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.structure"),
                this.structureValue(),
                STATUS_X,
                STATUS_Y,
                STATUS_WIDTH,
                this.menu.hasValidMultiblock()
        );
        MachineGuiTheme.renderInventoryLabel(
                guiGraphics,
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY
        );
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderStructureTooltip(guiGraphics, mouseX, mouseY);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderStructureTooltip(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(STATUS_X, STATUS_Y, STATUS_WIDTH, STATUS_HEIGHT, mouseX, mouseY)) {
            return;
        }
        final String key;
        if (this.menu.hasValidTier2()) {
            key = "screen.skyresources.end_portal_core.structure.improved";
        } else if (this.menu.hasValidMultiblock()) {
            key = "screen.skyresources.end_portal_core.structure.basic";
        } else {
            key = "screen.skyresources.end_portal_core.structure.missing";
        }
        GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(key));
    }

    private Component structureValue() {
        if (this.menu.hasValidTier2()) {
            return Component.translatable("screen.skyresources.metric.improved");
        }
        if (this.menu.hasValidMultiblock()) {
            return Component.translatable("screen.skyresources.metric.basic");
        }
        return Component.translatable("screen.skyresources.metric.missing");
    }
}
