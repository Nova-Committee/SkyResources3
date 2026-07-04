package committee.nova.mods.skyresources3.common.network;

import committee.nova.mods.skyresources3.Skyresources3;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetworking {
    private static final String NETWORK_VERSION = "1";
    private static int messageId;

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Skyresources3.MODID, "main"),
            () -> NETWORK_VERSION,
            NETWORK_VERSION::equals,
            NETWORK_VERSION::equals
    );

    public static void register() {
        CHANNEL.registerMessage(
                nextMessageId(),
                FusionTableDumpPayload.class,
                FusionTableDumpPayload::encode,
                FusionTableDumpPayload::decode,
                FusionTableDumpPayload::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(
                nextMessageId(),
                IslandGuiRequestPayload.class,
                IslandGuiRequestPayload::encode,
                IslandGuiRequestPayload::decode,
                IslandGuiRequestPayload::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(
                nextMessageId(),
                IslandGuiActionPayload.class,
                IslandGuiActionPayload::encode,
                IslandGuiActionPayload::decode,
                IslandGuiActionPayload::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(
                nextMessageId(),
                IslandGuiStatePayload.class,
                IslandGuiStatePayload::encode,
                IslandGuiStatePayload::decode,
                IslandGuiStatePayload::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }

    public static void sendToServer(final Object message) {
        CHANNEL.sendToServer(message);
    }

    public static void sendToPlayer(final ServerPlayer player, final Object message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    private static int nextMessageId() {
        return messageId++;
    }

    private ModNetworking() {
    }
}
