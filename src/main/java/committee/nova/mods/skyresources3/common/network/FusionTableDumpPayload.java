package committee.nova.mods.skyresources3.common.network;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.block.entity.FusionTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FusionTableDumpPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<FusionTableDumpPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Skyresources3.MODID, "fusion_table_dump")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, FusionTableDumpPayload> STREAM_CODEC =
            StreamCodec.composite(BlockPos.STREAM_CODEC, FusionTableDumpPayload::pos, FusionTableDumpPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static void handle(final FusionTableDumpPayload payload, final IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)
                || !(player.level() instanceof ServerLevel level)
                || !level.isLoaded(payload.pos())
                || player.distanceToSqr(
                        payload.pos().getX() + 0.5D,
                        payload.pos().getY() + 0.5D,
                        payload.pos().getZ() + 0.5D
                ) > 64.0D
                || !(level.getBlockEntity(payload.pos()) instanceof FusionTableBlockEntity fusionTable)) {
            return;
        }
        fusionTable.clearStoredCatalyst();
    }
}
