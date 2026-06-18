package committee.nova.mods.skyresources3.client.utils;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class GuiTooltips {
    public GuiTooltips() {
    }

    public static void render(
            final GuiGraphics guiGraphics,
            final Font font,
            final int mouseX,
            final int mouseY,
            final Component... lines
    ) {
        if (lines.length == 0) {
            return;
        }
        guiGraphics.renderTooltip(
                font,
                java.util.Arrays.stream(lines).map(Component::getVisualOrderText).toList(),
                mouseX,
                mouseY
        );
    }

    public static int percent(final float ratio) {
        return Math.round(Math.max(0.0F, Math.min(1.0F, ratio)) * 100.0F);
    }
}
