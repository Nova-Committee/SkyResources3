package committee.nova.mods.skyresources3.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.EmptyBlockAndTintGetter;

public final class GuideStructurePictureRenderer extends PictureInPictureRenderer<GuideStructureRenderState> {
    private static final int PACKED_LIGHT = LightTexture.FULL_BRIGHT;

    public GuideStructurePictureRenderer(final MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    public Class<GuideStructureRenderState> getRenderStateClass() {
        return GuideStructureRenderState.class;
    }

    @Override
    protected void renderToTexture(final GuideStructureRenderState renderState, final PoseStack poseStack) {
        final List<GuideStructureRenderState.StructureBlock> blocks = renderState.blocks();
        if (blocks.isEmpty()) {
            return;
        }

        final Minecraft minecraft = Minecraft.getInstance();
        final BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
        minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);

        final StructureBounds bounds = StructureBounds.from(blocks);
        final float centerX = (bounds.minX() + bounds.maxX() + 1.0F) * 0.5F;
        final float centerY = (bounds.minY() + bounds.maxY() + 1.0F) * 0.5F;
        final float centerZ = (bounds.minZ() + bounds.maxZ() + 1.0F) * 0.5F;

        poseStack.mulPose(Axis.XP.rotationDegrees(renderState.pitch()));
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.yaw()));
        poseStack.translate(-centerX, -centerY, -centerZ);

        for (final GuideStructureRenderState.StructureBlock block : blocks) {
            poseStack.pushPose();
            poseStack.translate(block.x(), block.y(), block.z());
            blockRenderer.renderSingleBlock(
                    block.state(),
                    poseStack,
                    this.bufferSource,
                    PACKED_LIGHT,
                    OverlayTexture.NO_OVERLAY,
                    EmptyBlockAndTintGetter.INSTANCE,
                    BlockPos.ZERO
            );
            poseStack.popPose();
        }
    }

    @Override
    protected float getTranslateY(final int height, final int guiScale) {
        return height / 2.0F;
    }

    @Override
    protected String getTextureLabel() {
        return "guide_structure";
    }

    private record StructureBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        static StructureBounds from(final List<GuideStructureRenderState.StructureBlock> blocks) {
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            int maxZ = Integer.MIN_VALUE;
            for (final GuideStructureRenderState.StructureBlock block : blocks) {
                minX = Math.min(minX, block.x());
                minY = Math.min(minY, block.y());
                minZ = Math.min(minZ, block.z());
                maxX = Math.max(maxX, block.x());
                maxY = Math.max(maxY, block.y());
                maxZ = Math.max(maxZ, block.z());
            }
            return new StructureBounds(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }
}
