package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.common.menu.WildlifeAttractorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class WildlifeAttractorScreen extends AbstractContainerScreen<WildlifeAttractorMenu> {
    private static final int ENERGY_X = 22;
    private static final int ENERGY_Y = 30;
    private static final int ENERGY_WIDTH = 8;
    private static final int ENERGY_HEIGHT = 52;
    private static final int WATER_X = 142;
    private static final int WATER_Y = 30;
    private static final int WATER_WIDTH = 16;
    private static final int WATER_HEIGHT = 52;
    private static final int MATTER_X = 55;
    private static final int MATTER_Y = 25;
    private static final int MATTER_WIDTH = 66;
    private static final int MATTER_HEIGHT = 16;

    public WildlifeAttractorScreen(
            final WildlifeAttractorMenu menu,
            final Inventory playerInventory,
            final Component title
    ) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 189;
        this.inventoryLabelY = 96;
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderEnergyTooltip(guiGraphics, mouseX, mouseY);
        this.renderWaterTooltip(guiGraphics, mouseX, mouseY);
        this.renderMatterTooltip(guiGraphics, mouseX, mouseY);
        this.renderMatterSlotTooltip(guiGraphics, mouseX, mouseY);
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
        MachineGuiTheme.renderInventoryLabel(
                guiGraphics,
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY
        );
        this.renderEnergy(guiGraphics);
        this.renderWater(guiGraphics);
        this.renderMatter(guiGraphics);
    }

    private void renderEnergy(final GuiGraphics guiGraphics) {
        MachineGuiTheme.renderVerticalGauge(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.energy_short"),
                ENERGY_X,
                ENERGY_Y,
                ENERGY_WIDTH,
                ENERGY_HEIGHT,
                this.menu.getEnergyRatio(),
                MachineGuiTheme.ENERGY
        );
    }

    private void renderWater(final GuiGraphics guiGraphics) {
        MachineGuiTheme.renderVerticalGauge(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.water_short"),
                WATER_X,
                WATER_Y,
                WATER_WIDTH,
                WATER_HEIGHT,
                this.menu.getWaterRatio(),
                MachineGuiTheme.WATER
        );
    }

    private void renderMatter(final GuiGraphics guiGraphics) {
        MachineGuiTheme.renderHorizontalGauge(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.matter"),
                MachineGuiTheme.percent(this.menu.getMatterRatio()),
                MATTER_X,
                MATTER_Y,
                MATTER_WIDTH,
                this.menu.getMatterRatio(),
                MachineGuiTheme.MATTER
        );
    }

    private void renderEnergyTooltip(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(ENERGY_X, ENERGY_Y, ENERGY_WIDTH, ENERGY_HEIGHT + 1, mouseX, mouseY)) {
            return;
        }
        final Component text = Component.translatable(
                "screen.skyresources.wildlife_attractor.energy",
                this.menu.getEnergyStored(),
                this.menu.getMaxEnergyStored()
        );
        this.renderTextTooltip(guiGraphics, text, mouseX, mouseY);
    }

    private void renderWaterTooltip(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(WATER_X, WATER_Y, WATER_WIDTH, WATER_HEIGHT + 1, mouseX, mouseY)) {
            return;
        }
        final Component text = Component.translatable(
                "screen.skyresources.wildlife_attractor.water",
                this.menu.getWaterStored(),
                this.menu.getMaxWaterStored()
        );
        this.renderTextTooltip(guiGraphics, text, mouseX, mouseY);
    }

    private void renderMatterTooltip(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(MATTER_X, MATTER_Y, MATTER_WIDTH, MATTER_HEIGHT, mouseX, mouseY)) {
            return;
        }
        final Component text = Component.translatable(
                "screen.skyresources.wildlife_attractor.matter",
                this.menu.getMatterLeft(),
                this.menu.getMaxMatterLeft()
        );
        this.renderTextTooltip(guiGraphics, text, mouseX, mouseY);
    }

    private void renderMatterSlotTooltip(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(WildlifeAttractorMenu.SLOT_X, WildlifeAttractorMenu.SLOT_Y, 16, 16, mouseX, mouseY)) {
            return;
        }
        this.renderTextTooltip(
                guiGraphics,
                Component.translatable("screen.skyresources.wildlife_attractor.matter_slot"),
                mouseX,
                mouseY
        );
    }

    private void renderTextTooltip(
            final GuiGraphics guiGraphics,
            final Component text,
            final int mouseX,
            final int mouseY
    ) {
        GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, text);
    }
}
