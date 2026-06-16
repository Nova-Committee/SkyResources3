package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.client.utils.GuiTooltips;
import committee.nova.mods.skyresources3.client.utils.MachineGuiTheme;
import committee.nova.mods.skyresources3.common.menu.RockCrusherMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class RockCrusherScreen extends AbstractContainerScreen<RockCrusherMenu> {
    private static final int ENERGY_X = 22;
    private static final int ENERGY_Y = 34;
    private static final int ENERGY_WIDTH = 8;
    private static final int ENERGY_HEIGHT = 52;
    private static final int PROGRESS_X = 78;
    private static final int PROGRESS_Y = 53;
    private static final int PROGRESS_WIDTH = 24;

    public RockCrusherScreen(
            final RockCrusherMenu menu,
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
        this.renderProgressTooltip(guiGraphics, mouseX, mouseY);
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
                Component.translatable("screen.skyresources.metric.speed"),
                Component.literal(Integer.toString(this.menu.getSpeed())),
                55,
                20,
                66,
                MachineGuiTheme.CATALYST
        );
        MachineGuiTheme.renderInventoryLabel(
                guiGraphics,
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY
        );
        this.renderEnergy(guiGraphics);
        this.renderProgress(guiGraphics);
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

    private void renderProgress(final GuiGraphics guiGraphics) {
        MachineGuiTheme.renderHorizontalFill(
                guiGraphics,
                PROGRESS_X,
                PROGRESS_Y + 5,
                PROGRESS_WIDTH + 1,
                7,
                this.menu.getProgressRatio(),
                MachineGuiTheme.PROGRESS
        );
        guiGraphics.drawString(
                this.font,
                MachineGuiTheme.percent(this.menu.getProgressRatio()),
                PROGRESS_X - 2,
                PROGRESS_Y - 6,
                MachineGuiTheme.TEXT,
                false
        );
    }

    private void renderEnergyTooltip(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(ENERGY_X, ENERGY_Y, ENERGY_WIDTH, ENERGY_HEIGHT + 1, mouseX, mouseY)) {
            return;
        }
        GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                "screen.skyresources.rock_crusher.energy",
                this.menu.getEnergyStored(),
                this.menu.getMaxEnergyStored()
        ));
    }

    private void renderProgressTooltip(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(PROGRESS_X, PROGRESS_Y, PROGRESS_WIDTH + 1, 16, mouseX, mouseY)) {
            return;
        }
        GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                "screen.skyresources.rock_crusher.progress",
                GuiTooltips.percent(this.menu.getProgressRatio())
        ));
    }
}
