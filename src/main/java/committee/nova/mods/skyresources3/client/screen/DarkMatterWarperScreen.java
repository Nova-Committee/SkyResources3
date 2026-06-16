package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.client.utils.GuiTooltips;
import committee.nova.mods.skyresources3.client.utils.MachineGuiTheme;
import committee.nova.mods.skyresources3.common.menu.DarkMatterWarperMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class DarkMatterWarperScreen extends AbstractContainerScreen<DarkMatterWarperMenu> {
    private static final int FUEL_X = 54;
    private static final int FUEL_Y = 31;
    private static final int FUEL_WIDTH = 68;
    private static final int FUEL_HEIGHT = 16;

    public DarkMatterWarperScreen(
            final DarkMatterWarperMenu menu,
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
        this.renderComponentTooltips(guiGraphics, mouseX, mouseY);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderComponentTooltips(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (this.isHovering(FUEL_X, FUEL_Y, FUEL_WIDTH, FUEL_HEIGHT, mouseX, mouseY)) {
            GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                    "screen.skyresources.dark_matter_warper.fuel",
                    this.menu.getBurnTime(),
                    this.menu.getMaxBurnTime()
            ));
        }
        if (this.isHovering(DarkMatterWarperMenu.SLOT_X, DarkMatterWarperMenu.SLOT_Y, 16, 16, mouseX, mouseY)) {
            GuiTooltips.render(
                    guiGraphics,
                    this.font,
                    mouseX,
                    mouseY,
                    Component.translatable("screen.skyresources.dark_matter_warper.fuel_slot")
            );
        }
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
        this.renderFuelBar(guiGraphics);
        MachineGuiTheme.renderInventoryLabel(
                guiGraphics,
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY
        );
    }

    private void renderFuelBar(final GuiGraphics guiGraphics) {
        MachineGuiTheme.renderHorizontalGauge(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.fuel"),
                MachineGuiTheme.percent(this.menu.getFuelRatio()),
                FUEL_X,
                FUEL_Y,
                FUEL_WIDTH,
                this.menu.getFuelRatio(),
                MachineGuiTheme.MATTER
        );
    }
}
