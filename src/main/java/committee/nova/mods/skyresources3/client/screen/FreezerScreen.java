package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.client.utils.MachineGuiTheme;
import committee.nova.mods.skyresources3.client.utils.GuiTooltips;
import committee.nova.mods.skyresources3.common.menu.FreezerMenu;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class FreezerScreen extends AbstractContainerScreen<FreezerMenu> {
    private static final int PROGRESS_COLOR = 0xAA55D7FF;
    private static final int SPEED_LABEL_X = 100;
    private static final int SPEED_LABEL_Y = 60;
    private static final int SPEED_LABEL_WIDTH = 68;
    private static final int STATUS_X = 14;
    private static final int STATUS_Y = 58;
    private static final int STATUS_WIDTH = 78;
    private static final int STATUS_HEIGHT = 18;

    public FreezerScreen(
            final FreezerMenu menu,
            final Inventory playerInventory,
            final Component title
    ) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 174;
        this.inventoryLabelY = 80;
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderComponentTooltips(guiGraphics, mouseX, mouseY);
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
                Component.literal("x" + formatSpeed(this.menu.getSpeed())),
                SPEED_LABEL_X,
                SPEED_LABEL_Y - 4,
                SPEED_LABEL_WIDTH,
                MachineGuiTheme.PROGRESS
        );
        MachineGuiTheme.renderInventoryLabel(
                guiGraphics,
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY
        );
        this.renderProgress(guiGraphics);
        this.renderMultiblockStatus(guiGraphics);
    }

    private void renderProgress(final GuiGraphics guiGraphics) {
        for (int inputSlot = 0; inputSlot < this.menu.getInputCount(); inputSlot++) {
            final int height = Math.round(this.menu.getProgressRatio(inputSlot) * 16.0F);
            if (height <= 0) {
                continue;
            }
            final int x = FreezerMenu.SLOT_START_X + inputSlot * FreezerMenu.SLOT_SPACING;
            final int bottom = FreezerMenu.INPUT_SLOT_Y + 16;
            guiGraphics.fill(x, bottom - height, x + 16, bottom, PROGRESS_COLOR);
        }
    }

    private void renderMultiblockStatus(final GuiGraphics guiGraphics) {
        if (!this.menu.requiresMultiblock()) {
            return;
        }
        MachineGuiTheme.renderStatus(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.structure"),
                Component.translatable(this.menu.hasValidMultiblock()
                        ? "screen.skyresources.metric.formed"
                        : "screen.skyresources.metric.missing"),
                STATUS_X,
                STATUS_Y,
                STATUS_WIDTH,
                this.menu.hasValidMultiblock()
        );
    }

    private void renderComponentTooltips(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (this.isHovering(SPEED_LABEL_X, SPEED_LABEL_Y - 1, SPEED_LABEL_WIDTH, this.font.lineHeight + 2, mouseX, mouseY)) {
            GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                    "screen.skyresources.freezer.speed.tooltip",
                    formatSpeed(this.menu.getSpeed())
            ));
            return;
        }
        if (this.menu.requiresMultiblock()
                && this.isHovering(STATUS_X, STATUS_Y, STATUS_WIDTH, STATUS_HEIGHT, mouseX, mouseY)) {
            GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(this.menu.hasValidMultiblock()
                    ? "screen.skyresources.freezer.multiblock.formed"
                    : "screen.skyresources.freezer.multiblock.missing"));
            return;
        }
        for (int inputSlot = 0; inputSlot < this.menu.getInputCount(); inputSlot++) {
            final int x = FreezerMenu.SLOT_START_X + inputSlot * FreezerMenu.SLOT_SPACING;
            if (this.isHovering(x, FreezerMenu.INPUT_SLOT_Y, 16, 16, mouseX, mouseY)) {
                GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                        "screen.skyresources.freezer.progress",
                        GuiTooltips.percent(this.menu.getProgressRatio(inputSlot))
                ));
                return;
            }
        }
    }

    private static String formatSpeed(final float speed) {
        if (speed == Math.rint(speed)) {
            return Integer.toString((int) speed);
        }
        return String.format(Locale.ROOT, "%.2f", speed);
    }
}
