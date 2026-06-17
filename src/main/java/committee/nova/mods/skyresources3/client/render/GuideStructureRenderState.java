package committee.nova.mods.skyresources3.client.render;

import java.util.List;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public record GuideStructureRenderState(
        List<StructureBlock> blocks,
        float yaw,
        float pitch,
        int x0,
        int y0,
        int x1,
        int y1,
        float scale,
        SceneBounds sceneBounds,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {
    public GuideStructureRenderState(final List<StructureBlock> blocks,
                                     final float yaw,
                                     final float pitch,
                                     final int x0,
                                     final int y0,
                                     final int x1,
                                     final int y1,
                                     final float scale,
                                     final SceneBounds sceneBounds,
                                     @Nullable final ScreenRectangle scissorArea) {
        this(List.copyOf(blocks), yaw, pitch, x0, y0, x1, y1, scale, sceneBounds, scissorArea,
                PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
    }

    public record SceneBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
    }

    public record StructureBlock(
            @Nullable BlockState state,
            ItemStack stack,
            int x,
            int y,
            int z,
            int displayY,
            int index
    ) {
        public StructureBlock {
            stack = stack.copy();
        }
    }
}
