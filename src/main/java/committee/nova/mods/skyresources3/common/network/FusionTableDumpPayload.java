package committee.nova.mods.skyresources3.common.network;

import committee.nova.mods.skyresources3.common.block.entity.FusionTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record FusionTableDumpPayload(BlockPos pos) {
    static void encode(final FusionTableDumpPayload payload, final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(payload.pos());
    }

    static FusionTableDumpPayload decode(final FriendlyByteBuf buffer) {
        return new FusionTableDumpPayload(buffer.readBlockPos());
    }

    static void handle(final FusionTableDumpPayload payload, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            if (player == null
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
        });
        context.setPacketHandled(true);
    }
}
