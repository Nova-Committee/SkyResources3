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
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
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
        final ItemModelResolver itemModelResolver = minecraft.getItemModelResolver();
        final FeatureRenderDispatcher featureDispatcher = minecraft.gameRenderer.getFeatureRenderDispatcher();
        minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);

        final GuideStructureRenderState.SceneBounds bounds = renderState.sceneBounds();
        final float centerX = (bounds.minX() + bounds.maxX() + 1.0F) * 0.5F;
        final float centerY = (bounds.minY() + bounds.maxY() + 1.0F) * 0.5F;
        final float centerZ = (bounds.minZ() + bounds.maxZ() + 1.0F) * 0.5F;

        // PIP applies a z-negative scale for GUI textures; cancel it for world-space block states.
        poseStack.scale(1.0F, 1.0F, -1.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(renderState.pitch()));
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.yaw()));
        poseStack.translate(-centerX, -centerY, -centerZ);

        for (final GuideStructureRenderState.StructureBlock block : blocks) {
            if (block.state() != null) {
                poseStack.pushPose();
                poseStack.translate(block.x(), block.displayY(), block.z());
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
                continue;
            }

            final ItemStackRenderState itemState = new ItemStackRenderState();
            itemModelResolver.updateForTopItem(
                    itemState,
                    block.stack(),
                    ItemDisplayContext.NONE,
                    minecraft.level,
                    null,
                    block.index()
            );
            if (itemState.isEmpty()) {
                continue;
            }
            poseStack.pushPose();
            poseStack.translate(block.x() + 0.5F, block.displayY() + 0.5F, block.z() + 0.5F);
            itemState.submit(
                    poseStack,
                    featureDispatcher.getSubmitNodeStorage(),
                    PACKED_LIGHT,
                    OverlayTexture.NO_OVERLAY,
                    0
            );
            poseStack.popPose();
        }
        featureDispatcher.renderAllFeatures();
    }

    @Override
    protected float getTranslateY(final int height, final int guiScale) {
        return height / 2.0F;
    }

    @Override
    protected String getTextureLabel() {
        return "guide_structure";
    }
}
