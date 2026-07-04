package committee.nova.mods.skyresources3.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record IslandGuiRequestPayload() {
    static void encode(final IslandGuiRequestPayload payload, final FriendlyByteBuf buffer) {
    }

    static IslandGuiRequestPayload decode(final FriendlyByteBuf buffer) {
        return new IslandGuiRequestPayload();
    }

    static void handle(final IslandGuiRequestPayload payload, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            if (player != null) {
                IslandGuiStatePayload.sendTo(player);
            }
        });
        context.setPacketHandled(true);
    }
}
