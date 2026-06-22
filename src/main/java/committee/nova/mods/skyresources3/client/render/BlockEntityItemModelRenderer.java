package committee.nova.mods.skyresources3.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

final class BlockEntityItemModelRenderer {
    private BlockEntityItemModelRenderer() {
    }

    static void render(
            final ItemRenderer itemRenderer,
            final BlockEntity blockEntity,
            final ItemStack stack,
            final PoseStack poseStack,
            final MultiBufferSource bufferSource,
            final int packedLight,
            final int packedOverlay,
            final int seedOffset
    ) {
        if (stack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.NONE,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                blockEntity.getLevel(),
                Long.hashCode(blockEntity.getBlockPos().asLong()) + seedOffset
        );
        poseStack.popPose();
    }
}
