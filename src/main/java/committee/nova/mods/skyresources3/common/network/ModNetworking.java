package committee.nova.mods.skyresources3.common.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModNetworking {
    private static final String NETWORK_VERSION = "1";

    public static void register(final RegisterPayloadHandlersEvent event) {
        event.registrar(NETWORK_VERSION)
                .playToServer(
                        FusionTableDumpPayload.TYPE,
                        FusionTableDumpPayload.STREAM_CODEC,
                        FusionTableDumpPayload::handle
                )
                .playToServer(
                        IslandGuiRequestPayload.TYPE,
                        IslandGuiRequestPayload.STREAM_CODEC,
                        IslandGuiRequestPayload::handle
                )
                .playToServer(
                        IslandGuiActionPayload.TYPE,
                        IslandGuiActionPayload.STREAM_CODEC,
                        IslandGuiActionPayload::handle
                )
                .playToClient(
                        IslandGuiStatePayload.TYPE,
                        IslandGuiStatePayload.STREAM_CODEC,
                        IslandGuiStatePayload::handle
                );
    }

    private ModNetworking() {
    }
}
