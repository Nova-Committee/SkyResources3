package committee.nova.mods.skyresources3.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import committee.nova.mods.skyresources3.common.block.entity.MachineCasingBlockEntity;
import committee.nova.mods.skyresources3.core.machine.CombustionHeaterType;
import committee.nova.mods.skyresources3.core.machine.CondenserType;
import committee.nova.mods.skyresources3.core.machine.CasingType;
import committee.nova.mods.skyresources3.core.machine.HeatProviderType;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class MachineCasingBlockEntityRenderer
        implements BlockEntityRenderer<MachineCasingBlockEntity, MachineCasingBlockEntityRenderer.State> {
    private static final float MODEL_SCALE = 1.0F / 16.0F;
    private final ItemModelResolver itemModelResolver;
    private final MaterialSet materials;

    public MachineCasingBlockEntityRenderer(final BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.materials = context.materials();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
            final MachineCasingBlockEntity blockEntity,
            final State state,
            final float partialTick,
            final Vec3 cameraPos,
            final ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        final CasingType casingType = blockEntity.casingType();
        state.elements = casingType.elements();
        state.sprite = this.materials.get(new Material(TextureAtlas.LOCATION_BLOCKS, casingType.texture()));

        state.machine.clear();
        state.combustionElements = List.of();
        state.combustionBodySprite = null;
        state.combustionTopSprite = null;
        state.condenserElements = List.of();
        state.condenserBodySprite = null;
        state.condenserPartSprite = null;
        state.installedElements = List.of();
        state.installedSprite = null;
        if (blockEntity.combustionHeaterTypeId() != null) {
            final CombustionHeaterType heaterType = blockEntity.combustionHeaterType();
            state.combustionElements = heaterType.elements();
            state.combustionBodySprite =
                    this.materials.get(new Material(TextureAtlas.LOCATION_BLOCKS, heaterType.bodyTexture()));
            state.combustionTopSprite =
                    this.materials.get(new Material(TextureAtlas.LOCATION_BLOCKS, heaterType.topTexture()));
            return;
        }
        if (blockEntity.heatProviderTypeId() != null) {
            final HeatProviderType providerType = blockEntity.heatProviderType();
            state.installedElements = providerType.elements();
            state.installedSprite = this.materials.get(new Material(TextureAtlas.LOCATION_BLOCKS, providerType.texture()));
            return;
        }
        if (blockEntity.condenserTypeId() != null) {
            final CondenserType condenserType = blockEntity.condenserType();
            state.condenserElements = condenserType.elements();
            state.condenserBodySprite =
                    this.materials.get(new Material(TextureAtlas.LOCATION_BLOCKS, condenserType.texture()));
            state.condenserPartSprite =
                    this.materials.get(new Material(TextureAtlas.LOCATION_BLOCKS, condenserType.partTexture()));
            return;
        }

        final ItemStack machine = blockEntity.heater();
        if (!machine.isEmpty()) {
            this.itemModelResolver.updateForTopItem(
                    state.machine,
                    machine,
                    ItemDisplayContext.FIXED,
                    blockEntity.getLevel(),
                    null,
                    blockEntity.getBlockPos().hashCode()
            );
        }
    }

    @Override
    public void submit(
            final State state,
            final PoseStack poseStack,
            final SubmitNodeCollector submitter,
            final CameraRenderState cameraState
    ) {
        final TextureAtlasSprite sprite = state.sprite;
        if (sprite != null && !state.elements.isEmpty()) {
            final List<CasingType.Element> elements = state.elements;
            final int lightCoords = state.lightCoords;
            submitter.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS),
                    (pose, buffer) -> {
                        for (final CasingType.Element element : elements) {
                            renderElement(element, pose, buffer, sprite, lightCoords, OverlayTexture.NO_OVERLAY);
                        }
                    }
            );
        }

        if (!state.combustionElements.isEmpty()) {
            final List<CombustionHeaterType.Element> elements = state.combustionElements;
            final TextureAtlasSprite bodySprite = state.combustionBodySprite;
            final TextureAtlasSprite topSprite = state.combustionTopSprite;
            if (bodySprite != null && topSprite != null) {
                final int lightCoords = state.lightCoords;
                submitter.submitCustomGeometry(
                        poseStack,
                        RenderTypes.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS),
                        (pose, buffer) -> {
                            for (final CombustionHeaterType.Element element : elements) {
                                final TextureAtlasSprite elementSprite = switch (element.texture()) {
                                    case BODY -> bodySprite;
                                    case TOP -> topSprite;
                                };
                                renderElement(element, pose, buffer, elementSprite, lightCoords, OverlayTexture.NO_OVERLAY);
                            }
                        }
                );
            }
        }

        if (!state.installedElements.isEmpty()) {
            final List<CasingType.Element> elements = state.installedElements;
            final TextureAtlasSprite installedSprite = state.installedSprite;
            if (installedSprite != null) {
                final int lightCoords = state.lightCoords;
                submitter.submitCustomGeometry(
                        poseStack,
                        RenderTypes.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS),
                        (pose, buffer) -> {
                            for (final CasingType.Element element : elements) {
                                renderElement(element, pose, buffer, installedSprite, lightCoords, OverlayTexture.NO_OVERLAY);
                            }
                        }
                );
            }
        }

        if (!state.condenserElements.isEmpty()) {
            final List<CondenserType.Element> elements = state.condenserElements;
            final TextureAtlasSprite bodySprite = state.condenserBodySprite;
            final TextureAtlasSprite partSprite = state.condenserPartSprite;
            if (bodySprite != null && partSprite != null) {
                final int lightCoords = state.lightCoords;
                submitter.submitCustomGeometry(
                        poseStack,
                        RenderTypes.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS),
                        (pose, buffer) -> {
                            for (final CondenserType.Element element : elements) {
                                switch (element.texture()) {
                                    case BODY -> renderElement(
                                            element.from(),
                                            element.to(),
                                            pose,
                                            buffer,
                                            bodySprite,
                                            lightCoords,
                                            OverlayTexture.NO_OVERLAY
                                    );
                                    case PART -> renderTopFace(
                                            element.from(),
                                            element.to(),
                                            pose,
                                            buffer,
                                            partSprite,
                                            lightCoords,
                                            OverlayTexture.NO_OVERLAY
                                    );
                                }
                            }
                        }
                );
            }
        }

        if (state.machine.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        state.machine.submit(poseStack, submitter, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    private static void renderElement(
            final CasingType.Element element,
            final PoseStack.Pose pose,
            final VertexConsumer buffer,
            final TextureAtlasSprite sprite,
            final int packedLight,
            final int packedOverlay
    ) {
        renderElement(element.from(), element.to(), pose, buffer, sprite, packedLight, packedOverlay);
    }

    private static void renderElement(
            final CombustionHeaterType.Element element,
            final PoseStack.Pose pose,
            final VertexConsumer buffer,
            final TextureAtlasSprite sprite,
            final int packedLight,
            final int packedOverlay
    ) {
        renderElement(element.from(), element.to(), pose, buffer, sprite, packedLight, packedOverlay);
    }

    private static void renderTopFace(
            final Vector3f from,
            final Vector3f to,
            final PoseStack.Pose pose,
            final VertexConsumer buffer,
            final TextureAtlasSprite sprite,
            final int packedLight,
            final int packedOverlay
    ) {
        final float x1 = Math.min(from.x(), to.x()) * MODEL_SCALE;
        final float y = Math.max(from.y(), to.y()) * MODEL_SCALE;
        final float z1 = Math.min(from.z(), to.z()) * MODEL_SCALE;
        final float x2 = Math.max(from.x(), to.x()) * MODEL_SCALE;
        final float z2 = Math.max(from.z(), to.z()) * MODEL_SCALE;
        if (x1 == x2 || z1 == z2) {
            return;
        }
        quad(buffer, pose, sprite, x1, y, z1, x2, y, z1, x2, y, z2, x1, y, z2, 0.0F, 1.0F, 0.0F, packedLight, packedOverlay);
    }

    private static void renderElement(
            final Vector3f from,
            final Vector3f to,
            final PoseStack.Pose pose,
            final VertexConsumer buffer,
            final TextureAtlasSprite sprite,
            final int packedLight,
            final int packedOverlay
    ) {
        final float x1 = Math.min(from.x(), to.x()) * MODEL_SCALE;
        final float y1 = Math.min(from.y(), to.y()) * MODEL_SCALE;
        final float z1 = Math.min(from.z(), to.z()) * MODEL_SCALE;
        final float x2 = Math.max(from.x(), to.x()) * MODEL_SCALE;
        final float y2 = Math.max(from.y(), to.y()) * MODEL_SCALE;
        final float z2 = Math.max(from.z(), to.z()) * MODEL_SCALE;
        if (x1 == x2 || y1 == y2 || z1 == z2) {
            return;
        }

        quad(buffer, pose, sprite, x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2, 0.0F, 1.0F, 0.0F, packedLight, packedOverlay);
        quad(buffer, pose, sprite, x1, y1, z2, x2, y1, z2, x2, y1, z1, x1, y1, z1, 0.0F, -1.0F, 0.0F, packedLight, packedOverlay);
        quad(buffer, pose, sprite, x1, y1, z1, x2, y1, z1, x2, y2, z1, x1, y2, z1, 0.0F, 0.0F, -1.0F, packedLight, packedOverlay);
        quad(buffer, pose, sprite, x1, y2, z2, x2, y2, z2, x2, y1, z2, x1, y1, z2, 0.0F, 0.0F, 1.0F, packedLight, packedOverlay);
        quad(buffer, pose, sprite, x1, y1, z2, x1, y1, z1, x1, y2, z1, x1, y2, z2, -1.0F, 0.0F, 0.0F, packedLight, packedOverlay);
        quad(buffer, pose, sprite, x2, y1, z1, x2, y1, z2, x2, y2, z2, x2, y2, z1, 1.0F, 0.0F, 0.0F, packedLight, packedOverlay);
    }

    private static void quad(
            final VertexConsumer buffer,
            final PoseStack.Pose pose,
            final TextureAtlasSprite sprite,
            final float x1,
            final float y1,
            final float z1,
            final float x2,
            final float y2,
            final float z2,
            final float x3,
            final float y3,
            final float z3,
            final float x4,
            final float y4,
            final float z4,
            final float normalX,
            final float normalY,
            final float normalZ,
            final int packedLight,
            final int packedOverlay
    ) {
        vertex(buffer, pose, sprite, x1, y1, z1, 0.0F, 0.0F, normalX, normalY, normalZ, packedLight, packedOverlay);
        vertex(buffer, pose, sprite, x2, y2, z2, 16.0F, 0.0F, normalX, normalY, normalZ, packedLight, packedOverlay);
        vertex(buffer, pose, sprite, x3, y3, z3, 16.0F, 16.0F, normalX, normalY, normalZ, packedLight, packedOverlay);
        vertex(buffer, pose, sprite, x4, y4, z4, 0.0F, 16.0F, normalX, normalY, normalZ, packedLight, packedOverlay);
    }

    private static void vertex(
            final VertexConsumer buffer,
            final PoseStack.Pose pose,
            final TextureAtlasSprite sprite,
            final float x,
            final float y,
            final float z,
            final float u,
            final float v,
            final float normalX,
            final float normalY,
            final float normalZ,
            final int packedLight,
            final int packedOverlay
    ) {
        buffer.addVertex(pose, x, y, z)
                .setColor(-1)
                .setUv(sprite.getU(u / 16.0F), sprite.getV(v / 16.0F))
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, normalX, normalY, normalZ);
    }

    public static final class State extends BlockEntityRenderState {
        private List<CasingType.Element> elements = List.of();
        private List<CombustionHeaterType.Element> combustionElements = List.of();
        private List<CondenserType.Element> condenserElements = List.of();
        private List<CasingType.Element> installedElements = List.of();
        @Nullable
        private TextureAtlasSprite sprite;
        @Nullable
        private TextureAtlasSprite combustionBodySprite;
        @Nullable
        private TextureAtlasSprite combustionTopSprite;
        @Nullable
        private TextureAtlasSprite condenserBodySprite;
        @Nullable
        private TextureAtlasSprite condenserPartSprite;
        @Nullable
        private TextureAtlasSprite installedSprite;
        private final ItemStackRenderState machine = new ItemStackRenderState();
    }
}
