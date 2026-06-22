package committee.nova.mods.skyresources3.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import committee.nova.mods.skyresources3.common.block.entity.MachineCasingBlockEntity;
import committee.nova.mods.skyresources3.common.item.CombustionHeaterItem;
import committee.nova.mods.skyresources3.common.item.CondenserItem;
import committee.nova.mods.skyresources3.common.item.HeatProviderItem;
import committee.nova.mods.skyresources3.common.item.MachineCasingItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class MachineCasingBlockEntityRenderer implements BlockEntityRenderer<MachineCasingBlockEntity> {
    private final ItemRenderer itemRenderer;

    public MachineCasingBlockEntityRenderer(final BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
            final MachineCasingBlockEntity blockEntity,
            final float partialTick,
            final PoseStack poseStack,
            final MultiBufferSource bufferSource,
            final int packedLight,
            final int packedOverlay
    ) {
        BlockEntityItemModelRenderer.render(
                this.itemRenderer,
                blockEntity,
                MachineCasingItem.forType(blockEntity.casingTypeId()),
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay,
                0
        );
        BlockEntityItemModelRenderer.render(
                this.itemRenderer,
                blockEntity,
                installedMachineStack(blockEntity),
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay,
                1
        );
    }

    private static ItemStack installedMachineStack(final MachineCasingBlockEntity blockEntity) {
        final ResourceLocation combustionHeaterTypeId = blockEntity.combustionHeaterTypeId();
        if (combustionHeaterTypeId != null) {
            return CombustionHeaterItem.forType(combustionHeaterTypeId);
        }
        final ResourceLocation heatProviderTypeId = blockEntity.heatProviderTypeId();
        if (heatProviderTypeId != null) {
            return HeatProviderItem.forType(heatProviderTypeId);
        }
        final ResourceLocation condenserTypeId = blockEntity.condenserTypeId();
        if (condenserTypeId != null) {
            return CondenserItem.forType(condenserTypeId);
        }
        return ItemStack.EMPTY;
    }
}
