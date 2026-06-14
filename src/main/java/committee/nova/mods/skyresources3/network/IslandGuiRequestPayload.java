package committee.nova.mods.skyresources3.network;

import committee.nova.mods.skyresources3.Skyresources3;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record IslandGuiRequestPayload() implements CustomPacketPayload {
    public static final Type<IslandGuiRequestPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "island_gui_request")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, IslandGuiRequestPayload> STREAM_CODEC =
            StreamCodec.unit(new IslandGuiRequestPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static void handle(final IslandGuiRequestPayload payload, final IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            IslandGuiStatePayload.sendTo(player);
        }
    }
}
