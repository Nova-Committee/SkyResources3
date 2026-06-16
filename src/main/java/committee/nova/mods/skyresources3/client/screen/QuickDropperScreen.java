package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.client.utils.GuiTooltips;
import committee.nova.mods.skyresources3.client.utils.MachineGuiTheme;
import committee.nova.mods.skyresources3.common.menu.QuickDropperMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class QuickDropperScreen extends AbstractContainerScreen<QuickDropperMenu> {
    public QuickDropperScreen(
            final QuickDropperMenu menu,
            final Inventory playerInventory,
            final Component title
    ) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 72;
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderSlotTooltip(guiGraphics, mouseX, mouseY);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
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
        MachineGuiTheme.renderMetricChip(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.output"),
                Component.translatable("screen.skyresources.metric.ready"),
                55,
                30,
                66,
                MachineGuiTheme.PROGRESS
        );
        MachineGuiTheme.renderInventoryLabel(
                guiGraphics,
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY
        );
    }

    private void renderSlotTooltip(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(QuickDropperMenu.SLOT_X, QuickDropperMenu.SLOT_Y, 16, 16, mouseX, mouseY)) {
            return;
        }
        GuiTooltips.render(
                guiGraphics,
                this.font,
                mouseX,
                mouseY,
                Component.translatable("screen.skyresources.quick_dropper.slot")
        );
    }
}
