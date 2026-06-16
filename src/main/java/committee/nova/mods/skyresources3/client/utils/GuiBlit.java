package committee.nova.mods.skyresources3.client.utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public final class GuiBlit {
    private GuiBlit() {
    }

    public static void blit(
            final GuiGraphics guiGraphics,
            final Identifier texture,
            final int x,
            final int y,
            final float u,
            final float v,
            final int width,
            final int height,
            final int textureWidth,
            final int textureHeight
    ) {
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                x,
                y,
                u,
                v,
                width,
                height,
                textureWidth,
                textureHeight
        );
    }

    public static void sprite(
            final GuiGraphics guiGraphics,
            final Identifier sprite,
            final int x,
            final int y,
            final int width,
            final int height
    ) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height);
    }
}
