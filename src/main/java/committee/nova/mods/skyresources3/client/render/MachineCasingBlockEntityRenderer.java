package committee.nova.mods.skyresources3.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.block.entity.MachineCasingBlockEntity;
import committee.nova.mods.skyresources3.core.machine.CasingType;
import committee.nova.mods.skyresources3.core.machine.CombustionHeaterType;
import committee.nova.mods.skyresources3.core.machine.CondenserType;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class MachineCasingBlockEntityRenderer
        implements BlockEntityRenderer<MachineCasingBlockEntity, MachineCasingBlockEntityRenderer.State> {
    private static final float MODEL_SCALE = 1.0F / 16.0F;
    private static final Identifier IRON_MACHINE_TEXTURE = texture(Skyresources3.MODID, "block/iron_machine");
    private static final Identifier DARK_MATTER_TEXTURE = texture(Skyresources3.MODID, "block/dark_matter_block");
    private static final Identifier LIGHT_MATTER_TEXTURE = texture(Skyresources3.MODID, "block/light_matter_block");
    private static final Identifier OAK_LOG_TEXTURE = texture("minecraft", "block/oak_log");
    private static final Identifier STONE_TEXTURE = texture("minecraft", "block/stone");
    private static final Identifier COBBLESTONE_TEXTURE = texture("minecraft", "block/cobblestone");
    private static final Identifier NETHER_BRICKS_TEXTURE = texture("minecraft", "block/nether_bricks");
    private static final Identifier END_STONE_TEXTURE = texture("minecraft", "block/end_stone");
    private static final String FALLBACK_CASING_TRANSLATION_KEY = "block.skyresources.machine_casing.iron";
    private static final String FALLBACK_COMBUSTION_TRANSLATION_KEY = "block.skyresources.combustion_heater.iron";
    private static final String FALLBACK_HEAT_PROVIDER_TRANSLATION_KEY = "block.skyresources.heat_provider.iron";
    private static final String FALLBACK_CONDENSER_TRANSLATION_KEY = "block.skyresources.condenser.iron";
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
        state.sprite = this.sprite(casingTexture(
                blockEntity.casingTypeId(),
                casingType.texture(),
                casingType.translationKey()
        ));

        state.machine.clear();
        state.combustionElements = List.of();
        state.combustionBodySprite = null;
        state.combustionTopSprite = null;
        state.condenserElements = List.of();
        state.condenserBodySprite = null;
        state.condenserPartSprite = null;
        state.installedElements = List.of();
        state.installedBodySprite = null;
        state.installedPartSprite = null;
        if (blockEntity.combustionHeaterTypeId() != null) {
            final Identifier heaterTypeId = blockEntity.combustionHeaterTypeId();
            final CombustionHeaterType heaterType = blockEntity.combustionHeaterType();
            state.combustionElements = heaterType.elements();
            state.combustionBodySprite = this.sprite(machineBodyTexture(
                    heaterTypeId,
                    heaterType.bodyTexture(),
                    heaterType.translationKey(),
                    FALLBACK_COMBUSTION_TRANSLATION_KEY
            ));
            state.combustionTopSprite = this.sprite(heaterType.topTexture());
            return;
        }
        if (blockEntity.heatProviderTypeId() != null) {
            final Identifier providerTypeId = blockEntity.heatProviderTypeId();
            final HeatProviderType providerType = blockEntity.heatProviderType();
            state.installedElements = providerType.elements();
            state.installedBodySprite = this.sprite(machineBodyTexture(
                    providerTypeId,
                    providerType.texture(),
                    providerType.translationKey(),
                    FALLBACK_HEAT_PROVIDER_TRANSLATION_KEY
            ));
            state.installedPartSprite = this.sprite(providerType.partTexture());
            return;
        }
        if (blockEntity.condenserTypeId() != null) {
            final Identifier condenserTypeId = blockEntity.condenserTypeId();
            final CondenserType condenserType = blockEntity.condenserType();
            state.condenserElements = condenserType.elements();
            state.condenserBodySprite = this.sprite(machineBodyTexture(
                    condenserTypeId,
                    condenserType.texture(),
                    condenserType.translationKey(),
                    FALLBACK_CONDENSER_TRANSLATION_KEY
            ));
            state.condenserPartSprite = this.sprite(condenserType.partTexture());
            return;
        }

        final ItemStack machine = blockEntity.heater();
        if (!machine.isEmpty()) {
            this.itemModelResolver.updateForTopItem(
                    state.machine,
                    machine,
                    ItemDisplayContext.NONE,
                    blockEntity.getLevel(),
                    null,
                    blockEntity.getBlockPos().hashCode()
            );
        }
    }

    private TextureAtlasSprite sprite(final Identifier texture) {
        return this.materials.get(new Material(TextureAtlas.LOCATION_BLOCKS, texture));
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
            final List<HeatProviderType.Element> elements = state.installedElements;
            final TextureAtlasSprite bodySprite = state.installedBodySprite;
            final TextureAtlasSprite partSprite = state.installedPartSprite;
            if (bodySprite != null && partSprite != null) {
                final int lightCoords = state.lightCoords;
                submitter.submitCustomGeometry(
                        poseStack,
                        RenderTypes.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS),
                        (pose, buffer) -> {
                            for (final HeatProviderType.Element element : elements) {
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

    private static Identifier casingTexture(
            final Identifier typeId,
            final Identifier resolvedTexture,
            final String translationKey
    ) {
        if (!shouldUseBuiltinFallback(typeId, resolvedTexture, translationKey, FALLBACK_CASING_TRANSLATION_KEY)) {
            return resolvedTexture;
        }
        return switch (typeId.getPath()) {
            case "wooden" -> OAK_LOG_TEXTURE;
            case "stone" -> COBBLESTONE_TEXTURE;
            case "nether_brick" -> NETHER_BRICKS_TEXTURE;
            case "end_stone" -> END_STONE_TEXTURE;
            case "dark_matter" -> DARK_MATTER_TEXTURE;
            case "light_matter" -> LIGHT_MATTER_TEXTURE;
            default -> resolvedTexture;
        };
    }

    private static Identifier machineBodyTexture(
            final Identifier typeId,
            final Identifier resolvedTexture,
            final String translationKey,
            final String fallbackTranslationKey
    ) {
        if (!shouldUseBuiltinFallback(typeId, resolvedTexture, translationKey, fallbackTranslationKey)) {
            return resolvedTexture;
        }
        return switch (typeId.getPath()) {
            case "wooden" -> OAK_LOG_TEXTURE;
            case "stone" -> STONE_TEXTURE;
            case "nether_brick" -> NETHER_BRICKS_TEXTURE;
            case "end_stone" -> END_STONE_TEXTURE;
            case "dark_matter" -> DARK_MATTER_TEXTURE;
            case "light_matter" -> LIGHT_MATTER_TEXTURE;
            default -> resolvedTexture;
        };
    }

    private static boolean shouldUseBuiltinFallback(
            final Identifier typeId,
            final Identifier resolvedTexture,
            final String translationKey,
            final String fallbackTranslationKey
    ) {
        return IRON_MACHINE_TEXTURE.equals(resolvedTexture)
                && fallbackTranslationKey.equals(translationKey)
                && Skyresources3.MODID.equals(typeId.getNamespace())
                && !"iron".equals(typeId.getPath());
    }

    private static Identifier texture(final String namespace, final String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static final class State extends BlockEntityRenderState {
        private List<CasingType.Element> elements = List.of();
        private List<CombustionHeaterType.Element> combustionElements = List.of();
        private List<CondenserType.Element> condenserElements = List.of();
        private List<HeatProviderType.Element> installedElements = List.of();
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
        private TextureAtlasSprite installedBodySprite;
        @Nullable
        private TextureAtlasSprite installedPartSprite;
        private final ItemStackRenderState machine = new ItemStackRenderState();
    }
}
