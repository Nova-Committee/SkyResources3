package committee.nova.mods.skyresources3.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

final class GuiBlit {
    private GuiBlit() {
    }

    static void blit(
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

    static void sprite(
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
