package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.menu.FreezerMenu;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class FreezerScreen extends AbstractContainerScreen<FreezerMenu> {
    private static final Identifier BACKGROUND =
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "textures/gui/blank_inventory.png");
    private static final Identifier ICONS =
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "textures/gui/gui_icons.png");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int PROGRESS_COLOR = 0xAA8EC7FF;
    private static final int SPEED_LABEL_X = 100;
    private static final int SPEED_LABEL_Y = 60;
    private static final int SPEED_LABEL_WIDTH = 68;
    private static final int STATUS_X = 3;
    private static final int STATUS_Y = 12;
    private static final int STATUS_WIDTH = 48;
    private static final int STATUS_HEIGHT = 28;

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
        GuiBlit.blit(
                guiGraphics,
                BACKGROUND,
                this.leftPos,
                this.topPos,
                0,
                0,
                this.imageWidth,
                this.imageHeight,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );

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
                0xFF404040,
                false
        );
        guiGraphics.drawString(
                this.font,
                Component.translatable("screen.skyresources.freezer.speed", formatSpeed(this.menu.getSpeed())),
                SPEED_LABEL_X,
                SPEED_LABEL_Y,
                0xFF404040,
                false
        );
        guiGraphics.drawString(
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY,
                0xFF404040,
                false
        );
        this.renderProgress(guiGraphics);
        this.renderMultiblockStatus(guiGraphics);
    }

    private void renderSlotBackground(final GuiGraphics guiGraphics, final int inputSlot, final int y) {
        final int x = this.leftPos + FreezerMenu.SLOT_START_X + inputSlot * FreezerMenu.SLOT_SPACING;
        final int top = this.topPos + y;
        GuiBlit.blit(guiGraphics, BACKGROUND, x - 1, top - 1, 7, 83, 18, 18, TEXTURE_WIDTH, TEXTURE_HEIGHT);
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
        GuiBlit.blit(guiGraphics, ICONS, STATUS_X, STATUS_Y, 0, 16, 32, 28, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        GuiBlit.blit(
                guiGraphics,
                ICONS,
                35,
                20,
                this.menu.hasValidMultiblock() ? 0 : 16,
                0,
                16,
                16,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
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
