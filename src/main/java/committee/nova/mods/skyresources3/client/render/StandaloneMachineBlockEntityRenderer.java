package committee.nova.mods.skyresources3.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import committee.nova.mods.skyresources3.common.block.entity.StandaloneMachineBlockEntity;
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
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public final class StandaloneMachineBlockEntityRenderer
        implements BlockEntityRenderer<StandaloneMachineBlockEntity, StandaloneMachineBlockEntityRenderer.State> {
    private static final String FALLBACK_COMBUSTION_TRANSLATION_KEY = "block.skyresources.combustion_heater.iron";
    private static final String FALLBACK_HEAT_PROVIDER_TRANSLATION_KEY = "block.skyresources.heat_provider.iron";
    private static final String FALLBACK_CONDENSER_TRANSLATION_KEY = "block.skyresources.condenser.iron";

    private final MaterialSet materials;

    public StandaloneMachineBlockEntityRenderer(final BlockEntityRendererProvider.Context context) {
        this.materials = context.materials();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
            final StandaloneMachineBlockEntity blockEntity,
            final State state,
            final float partialTick,
            final Vec3 cameraPos,
            final ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        state.combustionElements = List.of();
        state.combustionBodySprite = null;
        state.combustionTopSprite = null;
        state.heatProviderElements = List.of();
        state.heatProviderBodySprite = null;
        state.heatProviderPartSprite = null;
        state.condenserElements = List.of();
        state.condenserBodySprite = null;
        state.condenserPartSprite = null;

        switch (blockEntity.kind()) {
            case COMBUSTION_HEATER -> {
                final CombustionHeaterType type = blockEntity.combustionHeaterType();
                state.combustionElements = type.elements();
                state.combustionBodySprite = this.sprite(MachineCasingBlockEntityRenderer.machineBodyTexture(
                        blockEntity.typeId(),
                        type.bodyTexture(),
                        type.translationKey(),
                        FALLBACK_COMBUSTION_TRANSLATION_KEY
                ));
                state.combustionTopSprite = this.sprite(type.topTexture());
            }
            case HEAT_PROVIDER -> {
                final HeatProviderType type = blockEntity.heatProviderType();
                state.heatProviderElements = type.elements();
                state.heatProviderBodySprite = this.sprite(MachineCasingBlockEntityRenderer.machineBodyTexture(
                        blockEntity.typeId(),
                        type.texture(),
                        type.translationKey(),
                        FALLBACK_HEAT_PROVIDER_TRANSLATION_KEY
                ));
                state.heatProviderPartSprite = this.sprite(type.partTexture());
            }
            case CONDENSER -> {
                final CondenserType type = blockEntity.condenserType();
                state.condenserElements = type.elements();
                state.condenserBodySprite = this.sprite(MachineCasingBlockEntityRenderer.machineBodyTexture(
                        blockEntity.typeId(),
                        type.texture(),
                        type.translationKey(),
                        FALLBACK_CONDENSER_TRANSLATION_KEY
                ));
                state.condenserPartSprite = this.sprite(type.partTexture());
            }
        }
    }

    @Override
    public void submit(
            final State state,
            final PoseStack poseStack,
            final SubmitNodeCollector submitter,
            final CameraRenderState cameraState
    ) {
        if (!state.combustionElements.isEmpty()
                && state.combustionBodySprite != null
                && state.combustionTopSprite != null) {
            final List<CombustionHeaterType.Element> elements = state.combustionElements;
            final TextureAtlasSprite bodySprite = state.combustionBodySprite;
            final TextureAtlasSprite topSprite = state.combustionTopSprite;
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
                            MachineCasingBlockEntityRenderer.renderElement(
                                    element.from(),
                                    element.to(),
                                    pose,
                                    buffer,
                                    elementSprite,
                                    lightCoords,
                                    OverlayTexture.NO_OVERLAY
                            );
                        }
                    }
            );
        }

        if (!state.heatProviderElements.isEmpty()
                && state.heatProviderBodySprite != null
                && state.heatProviderPartSprite != null) {
            final List<HeatProviderType.Element> elements = state.heatProviderElements;
            final TextureAtlasSprite bodySprite = state.heatProviderBodySprite;
            final TextureAtlasSprite partSprite = state.heatProviderPartSprite;
            final int lightCoords = state.lightCoords;
            submitter.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS),
                    (pose, buffer) -> {
                        for (final HeatProviderType.Element element : elements) {
                            switch (element.texture()) {
                                case BODY -> MachineCasingBlockEntityRenderer.renderElement(
                                        element.from(),
                                        element.to(),
                                        pose,
                                        buffer,
                                        bodySprite,
                                        lightCoords,
                                        OverlayTexture.NO_OVERLAY
                                );
                                case PART -> MachineCasingBlockEntityRenderer.renderTopFace(
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

        if (!state.condenserElements.isEmpty()
                && state.condenserBodySprite != null
                && state.condenserPartSprite != null) {
            final List<CondenserType.Element> elements = state.condenserElements;
            final TextureAtlasSprite bodySprite = state.condenserBodySprite;
            final TextureAtlasSprite partSprite = state.condenserPartSprite;
            final int lightCoords = state.lightCoords;
            submitter.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS),
                    (pose, buffer) -> {
                        for (final CondenserType.Element element : elements) {
                            switch (element.texture()) {
                                case BODY -> MachineCasingBlockEntityRenderer.renderElement(
                                        element.from(),
                                        element.to(),
                                        pose,
                                        buffer,
                                        bodySprite,
                                        lightCoords,
                                        OverlayTexture.NO_OVERLAY
                                );
                                case PART -> MachineCasingBlockEntityRenderer.renderTopFace(
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

    private TextureAtlasSprite sprite(final Identifier texture) {
        return this.materials.get(new Material(TextureAtlas.LOCATION_BLOCKS, texture));
    }

    public static final class State extends BlockEntityRenderState {
        private List<CombustionHeaterType.Element> combustionElements = List.of();
        private List<HeatProviderType.Element> heatProviderElements = List.of();
        private List<CondenserType.Element> condenserElements = List.of();
        @Nullable
        private TextureAtlasSprite combustionBodySprite;
        @Nullable
        private TextureAtlasSprite combustionTopSprite;
        @Nullable
        private TextureAtlasSprite heatProviderBodySprite;
        @Nullable
        private TextureAtlasSprite heatProviderPartSprite;
        @Nullable
        private TextureAtlasSprite condenserBodySprite;
        @Nullable
        private TextureAtlasSprite condenserPartSprite;
    }
}
