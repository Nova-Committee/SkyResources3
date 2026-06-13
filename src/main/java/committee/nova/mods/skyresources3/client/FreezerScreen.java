package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.menu.FreezerMenu;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class FreezerScreen extends AbstractContainerScreen<FreezerMenu> {
    private static final int BACKGROUND_COLOR = 0xFFC6C6C6;
    private static final int BORDER_DARK = 0xFF555555;
    private static final int BORDER_LIGHT = 0xFFFFFFFF;
    private static final int SLOT_BACKGROUND = 0xFF8B8B8B;
    private static final int PROGRESS_COLOR = 0xAA8EC7FF;
    private static final int VALID_COLOR = 0xFF3AA655;
    private static final int INVALID_COLOR = 0xFFB33A3A;
    private static final int STATUS_X = 35;
    private static final int STATUS_Y = 20;

    public FreezerScreen(
            final FreezerMenu menu,
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
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(
            final GuiGraphics guiGraphics,
            final float partialTick,
            final int mouseX,
            final int mouseY
    ) {
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, BACKGROUND_COLOR);
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + 1, BORDER_LIGHT);
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + 1, this.topPos + this.imageHeight, BORDER_LIGHT);
        guiGraphics.fill(this.leftPos, this.topPos + this.imageHeight - 1, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, BORDER_DARK);
        guiGraphics.fill(this.leftPos + this.imageWidth - 1, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, BORDER_DARK);

        for (int inputSlot = 0; inputSlot < this.menu.getInputCount(); inputSlot++) {
            this.renderSlotBackground(guiGraphics, inputSlot, FreezerMenu.INPUT_SLOT_Y);
            this.renderSlotBackground(guiGraphics, inputSlot, FreezerMenu.OUTPUT_SLOT_Y);
        }
    }

    @Override
    protected void renderLabels(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        guiGraphics.drawString(
                this.font,
                this.title,
                (this.imageWidth - this.font.width(this.title)) / 2,
                6,
                4210752,
                false
        );
        guiGraphics.drawString(
                this.font,
                Component.translatable("screen.skyresources3.freezer.speed", formatSpeed(this.menu.getSpeed())),
                100,
                60,
                4210752,
                false
        );
        guiGraphics.drawString(
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY,
                4210752,
                false
        );
        this.renderProgress(guiGraphics);
        this.renderMultiblockStatus(guiGraphics);
    }

    private void renderSlotBackground(final GuiGraphics guiGraphics, final int inputSlot, final int y) {
        final int x = this.leftPos + FreezerMenu.SLOT_START_X + inputSlot * FreezerMenu.SLOT_SPACING;
        final int top = this.topPos + y;
        guiGraphics.fill(x - 1, top - 1, x + 17, top + 17, BORDER_DARK);
        guiGraphics.fill(x, top, x + 16, top + 16, SLOT_BACKGROUND);
        guiGraphics.fill(x, top, x + 16, top + 1, BORDER_LIGHT);
        guiGraphics.fill(x, top, x + 1, top + 16, BORDER_LIGHT);
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
        final int color = this.menu.hasValidMultiblock() ? VALID_COLOR : INVALID_COLOR;
        guiGraphics.fill(STATUS_X, STATUS_Y, STATUS_X + 16, STATUS_Y + 16, BORDER_DARK);
        guiGraphics.fill(STATUS_X + 2, STATUS_Y + 2, STATUS_X + 14, STATUS_Y + 14, color);
    }

    private static String formatSpeed(final float speed) {
        if (speed == Math.rint(speed)) {
            return Integer.toString((int) speed);
        }
        return String.format(Locale.ROOT, "%.2f", speed);
    }
}
