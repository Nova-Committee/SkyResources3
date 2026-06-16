package committee.nova.mods.skyresources3.client.utils;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.Slot;

public final class MachineGuiTheme {
    public static final int TEXT = 0xFFE8EEF5;
    public static final int MUTED = 0xFF98A7B3;
    public static final int GOOD = 0xFF7EE36D;
    public static final int WARN = 0xFFFFB84D;
    public static final int BAD = 0xFFFF6B6B;
    public static final int HEAT = 0xFFFF8C2E;
    public static final int ENERGY = 0xFF55D7FF;
    public static final int WATER = 0xFF4C8DFF;
    public static final int LIFE = 0xFFFF5F77;
    public static final int MATTER = 0xFFB678FF;
    public static final int PROGRESS = 0xFF45E0C2;
    public static final int CATALYST = 0xFF8DE35E;

    private static final int PANEL = 0xFF182029;
    private static final int PANEL_DARK = 0xFF10151B;
    private static final int PANEL_LIGHT = 0xFF2C3742;
    private static final int PANEL_EDGE = 0xFF4B5966;
    private static final int COPPER = 0xFFC87B45;
    private static final int SLOT_DARK = 0xFF0D1116;
    private static final int SLOT = 0xFF202833;
    private static final int GAUGE_BACK = 0xFF0C1117;
    private static final int GAUGE_EDGE = 0xFF3B4650;

    private MachineGuiTheme() {
    }

    public static void renderPanel(final GuiGraphics guiGraphics, final int x, final int y, final int width, final int height) {
        guiGraphics.fill(x - 3, y - 3, x + width + 3, y + height + 3, 0x78000000);
        guiGraphics.fill(x, y, x + width, y + height, PANEL_EDGE);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, PANEL_DARK);
        guiGraphics.fill(x + 3, y + 3, x + width - 3, y + height - 3, PANEL);
        guiGraphics.fill(x + 5, y + 5, x + width - 5, y + 20, PANEL_LIGHT);
        guiGraphics.fill(x + 5, y + 20, x + width - 5, y + 21, 0xFF0B0F14);
        guiGraphics.fill(x + 12, y + 8, x + 42, y + 10, COPPER);
        guiGraphics.fill(x + width - 42, y + 8, x + width - 12, y + 10, COPPER);
    }

    public static void renderSlots(
            final GuiGraphics guiGraphics,
            final int left,
            final int top,
            final List<Slot> slots
    ) {
        for (final Slot slot : slots) {
            renderSlot(guiGraphics, left + slot.x - 1, top + slot.y - 1);
        }
    }

    public static void renderSlot(final GuiGraphics guiGraphics, final int x, final int y) {
        guiGraphics.fill(x, y, x + 18, y + 18, SLOT_DARK);
        guiGraphics.fill(x + 1, y + 1, x + 17, y + 17, PANEL_EDGE);
        guiGraphics.fill(x + 2, y + 2, x + 16, y + 16, SLOT);
        guiGraphics.fill(x + 2, y + 2, x + 16, y + 3, 0xFF394450);
        guiGraphics.fill(x + 2, y + 2, x + 3, y + 16, 0xFF394450);
    }

    public static void renderTitle(
            final GuiGraphics guiGraphics,
            final Font font,
            final Component title,
            final int imageWidth
    ) {
        guiGraphics.drawString(font, title, (imageWidth - font.width(title)) / 2, 7, TEXT, false);
    }

    public static void renderInventoryLabel(
            final GuiGraphics guiGraphics,
            final Font font,
            final Component label,
            final int x,
            final int y
    ) {
        guiGraphics.drawString(font, label, x, y, MUTED, false);
    }

    public static void renderHorizontalGauge(
            final GuiGraphics guiGraphics,
            final Font font,
            final Component label,
            final Component value,
            final int x,
            final int y,
            final int width,
            final float ratio,
            final int color
    ) {
        guiGraphics.drawString(font, label, x, y, MUTED, false);
        guiGraphics.drawString(font, value, x + width - font.width(value), y, TEXT, false);
        renderHorizontalFill(guiGraphics, x, y + 10, width, 6, ratio, color);
    }

    public static void renderHorizontalFill(
            final GuiGraphics guiGraphics,
            final int x,
            final int y,
            final int width,
            final int height,
            final float ratio,
            final int color
    ) {
        guiGraphics.fill(x, y, x + width, y + height, GAUGE_EDGE);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, GAUGE_BACK);
        final int fillWidth = Math.round(Mth.clamp(ratio, 0.0F, 1.0F) * (width - 2));
        if (fillWidth > 0) {
            guiGraphics.fill(x + 1, y + 1, x + 1 + fillWidth, y + height - 1, color);
            guiGraphics.fill(x + 1, y + 1, x + 1 + fillWidth, y + 2, 0x80FFFFFF);
        }
    }

    public static void renderVerticalGauge(
            final GuiGraphics guiGraphics,
            final Font font,
            final Component label,
            final int x,
            final int y,
            final int width,
            final int height,
            final float ratio,
            final int color
    ) {
        guiGraphics.fill(x, y, x + width, y + height, GAUGE_EDGE);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, GAUGE_BACK);
        final int fillHeight = Math.round(Mth.clamp(ratio, 0.0F, 1.0F) * (height - 2));
        if (fillHeight > 0) {
            guiGraphics.fill(x + 1, y + height - 1 - fillHeight, x + width - 1, y + height - 1, color);
            guiGraphics.fill(x + 1, y + height - 1 - fillHeight, x + 2, y + height - 1, 0x80FFFFFF);
        }
        guiGraphics.drawString(font, label, x + (width - font.width(label)) / 2, y + height + 3, MUTED, false);
    }

    public static void renderStatus(
            final GuiGraphics guiGraphics,
            final Font font,
            final Component label,
            final Component value,
            final int x,
            final int y,
            final int width,
            final boolean valid
    ) {
        final int color = valid ? GOOD : BAD;
        guiGraphics.fill(x, y, x + width, y + 18, 0xFF111820);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + 17, 0xFF232C35);
        guiGraphics.fill(x + 4, y + 5, x + 10, y + 11, color);
        final int valueX = x + width - font.width(value) - 4;
        if (x + 14 + font.width(label) + 3 < valueX) {
            guiGraphics.drawString(font, label, x + 14, y + 3, MUTED, false);
            guiGraphics.drawString(font, value, valueX, y + 3, color, false);
            return;
        }
        guiGraphics.drawString(font, value, x + 14, y + 3, color, false);
    }

    public static void renderMetricChip(
            final GuiGraphics guiGraphics,
            final Font font,
            final Component label,
            final Component value,
            final int x,
            final int y,
            final int width,
            final int color
    ) {
        guiGraphics.fill(x, y, x + width, y + 16, 0xFF111820);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + 15, 0xFF232C35);
        guiGraphics.fill(x + 3, y + 3, x + 5, y + 13, color);
        final int valueX = x + width - font.width(value) / 2 - 14;
        if (x + 8 + font.width(label) + 3 < valueX) {
            guiGraphics.drawString(font, label, x + 8, y + 4, MUTED, false);
            guiGraphics.drawString(font, value, valueX, y + 4, TEXT, false);
            return;
        }
        guiGraphics.drawString(font, value, x + 8, y + 4, TEXT, false);
    }

    public static Component percent(final int value) {
        return Component.translatable("screen.skyresources.metric.percent", value);
    }

    public static Component percent(final float ratio) {
        return percent(Math.round(Mth.clamp(ratio, 0.0F, 1.0F) * 100.0F));
    }
}
