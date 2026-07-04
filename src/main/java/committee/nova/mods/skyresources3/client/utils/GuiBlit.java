package committee.nova.mods.skyresources3.client.utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class GuiBlit {
    private GuiBlit() {
    }

    public static void blit(
            final GuiGraphics guiGraphics,
            final ResourceLocation texture,
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
            final ResourceLocation sprite,
            final int x,
            final int y,
            final int width,
            final int height
    ) {
        guiGraphics.blit(sprite, x, y, 0.0F, 0.0F, width, height, width, height);
    }
}
