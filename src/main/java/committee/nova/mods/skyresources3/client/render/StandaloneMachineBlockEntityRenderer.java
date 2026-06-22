package committee.nova.mods.skyresources3.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import committee.nova.mods.skyresources3.common.block.entity.StandaloneMachineBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;

public final class StandaloneMachineBlockEntityRenderer implements BlockEntityRenderer<StandaloneMachineBlockEntity> {
    private final ItemRenderer itemRenderer;

    public StandaloneMachineBlockEntityRenderer(final BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
            final StandaloneMachineBlockEntity blockEntity,
            final float partialTick,
            final PoseStack poseStack,
            final MultiBufferSource bufferSource,
            final int packedLight,
            final int packedOverlay
    ) {
        BlockEntityItemModelRenderer.render(
                this.itemRenderer,
                blockEntity,
                blockEntity.asItemStack(),
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay,
                0
        );
    }
}
