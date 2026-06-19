package committee.nova.mods.skyresources3.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

final class SkyResourcesButton extends Button {
    private static final int TEXT_COLOR = 0xFFF4EBD6;
    private static final int MUTED_TEXT_COLOR = 0xFFC8BEA8;
    private static final int GOLD_COLOR = 0xFFE2BD56;

    private final Tone tone;

    private SkyResourcesButton(final Builder builder, final Tone tone) {
        super(builder);
        this.tone = tone;
    }

    static Button create(
            final Component label,
            final OnPress onPress,
            final int x,
            final int y,
            final int width,
            final int height,
            final Tooltip tooltip,
            final Tone tone
    ) {
        final Builder builder = Button.builder(label, onPress).bounds(x, y, width, height);
        if (tooltip != null) {
            builder.tooltip(tooltip);
        }
        return builder.build(buttonBuilder -> new SkyResourcesButton(buttonBuilder, tone));
    }

    static void renderFrame(
            final GuiGraphics guiGraphics,
            final int x,
            final int y,
            final int width,
            final int height,
            final boolean active,
            final boolean hovered,
            final float alpha,
            final Tone tone
    ) {
        final int fillColor = active
                ? (hovered ? tone.hoverFillColor : tone.fillColor)
                : 0xAA20242A;
        final int borderColor = active
                ? (hovered ? GOLD_COLOR : tone.borderColor)
                : 0x665C6266;

        guiGraphics.fill(x, y, x + width, y + height, withAlpha(fillColor, alpha));
        guiGraphics.renderOutline(x, y, width, height, withAlpha(borderColor, alpha));
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + 2, withAlpha(0x33FFFFFF, alpha));
        guiGraphics.fill(x + 1, y + height - 2, x + width - 1, y + height - 1, withAlpha(0x66000000, alpha));
        if (hovered && active) {
            guiGraphics.fill(x + 2, y + height - 4, x + width - 2, y + height - 3, withAlpha(GOLD_COLOR, alpha));
        }
    }

    private static void renderFrame(
            final GuiGraphics guiGraphics,
            final int x,
            final int y,
            final int width,
            final int height,
            final boolean active,
            final boolean hovered,
            final boolean focused,
            final float alpha,
            final Tone tone
    ) {
        renderFrame(guiGraphics, x, y, width, height, active, hovered, alpha, tone);
        if (focused && !hovered && active && width > 4 && height > 4) {
            guiGraphics.renderOutline(x + 1, y + 1, width - 2, height - 2, withAlpha(GOLD_COLOR, alpha));
        }
    }

    static void renderLabel(
            final GuiGraphics guiGraphics,
            final Font font,
            final Component label,
            final int x,
            final int y,
            final int width,
            final int height,
            final boolean active,
            final float alpha,
            final Tone tone
    ) {
        final int textWidth = font.width(label);
        final int textX = x + Math.max(2, (width - textWidth) / 2);
        final int textY = y + (height - 8) / 2;
        final int color = textColor(tone, active, alpha);
        if (width > 4) {
            guiGraphics.enableScissor(x + 2, y, x + width - 2, y + height);
        }
        guiGraphics.drawString(font, label, textX, textY, color, false);
        if (width > 4) {
            guiGraphics.disableScissor();
        }
    }

    static int textColor(final Tone tone, final boolean active, final float alpha) {
        return withAlpha(active ? tone.textColor : 0xFF858585, alpha);
    }

    static int mutedTextColor(final float alpha) {
        return withAlpha(MUTED_TEXT_COLOR, alpha);
    }

    @Override
    protected void renderWidget(
            final GuiGraphics guiGraphics,
            final int mouseX,
            final int mouseY,
            final float partialTick
    ) {
        final boolean hovered = this.isHovered();
        renderFrame(
                guiGraphics,
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                this.active,
                hovered,
                this.isFocused(),
                this.alpha,
                this.tone
        );
        renderLabel(
                guiGraphics,
                Minecraft.getInstance().font,
                this.getMessage(),
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                this.active,
                this.alpha,
                this.tone
        );
    }

    private static int withAlpha(final int color, final float alpha) {
        final int baseAlpha = color >>> 24;
        return (color & 0x00FFFFFF) | ((int) Math.ceil(baseAlpha * alpha) << 24);
    }

    enum Tone {
        DEFAULT(0xDD30363A, 0xEE3B4544, 0xFF7C896D, TEXT_COLOR),
        PRIMARY(0xDD385635, 0xEE486C43, 0xFFB7D66E, TEXT_COLOR),
        DANGER(0xDD5B241F, 0xEE743127, 0xFFFF826F, 0xFFFFE1D8),
        QUIET(0xCC242A2F, 0xDD30383D, 0xFF68766A, MUTED_TEXT_COLOR);

        private final int fillColor;
        private final int hoverFillColor;
        private final int borderColor;
        private final int textColor;

        Tone(final int fillColor, final int hoverFillColor, final int borderColor, final int textColor) {
            this.fillColor = fillColor;
            this.hoverFillColor = hoverFillColor;
            this.borderColor = borderColor;
            this.textColor = textColor;
        }
    }
}
