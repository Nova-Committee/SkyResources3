package committee.nova.mods.skyresources3.common.network;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.core.island.IslandSavedData;
import committee.nova.mods.skyresources3.core.island.IslandTemplate;
import committee.nova.mods.skyresources3.core.island.PlayerIdentitySavedData;
import committee.nova.mods.skyresources3.core.island.VoidIslandWorld;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public record IslandGuiStatePayload(
        boolean enabled,
        boolean hasIsland,
        boolean owner,
        boolean member,
        boolean hasPendingInvite,
        String pendingInviteOwner,
        String ownerName,
        String islandType,
        String dimension,
        String home,
        List<String> players,
        List<String> invites,
        List<String> trustedVisitors,
        List<String> templates
) {
    private static final int MAX_TEXT_LENGTH = 128;
    private static final int MAX_LIST_SIZE = 64;
    private static Consumer<IslandGuiStatePayload> clientHandler = payload -> {
    };

    public IslandGuiStatePayload {
        pendingInviteOwner = clean(pendingInviteOwner);
        ownerName = clean(ownerName);
        islandType = clean(islandType);
        dimension = clean(dimension);
        home = clean(home);
        players = List.copyOf(players);
        invites = List.copyOf(invites);
        trustedVisitors = List.copyOf(trustedVisitors);
        templates = List.copyOf(templates);
    }

    public static void setClientHandler(final Consumer<IslandGuiStatePayload> handler) {
        clientHandler = handler == null ? payload -> {
        } : handler;
    }

    public static void sendTo(final ServerPlayer player) {
        ModNetworking.sendToPlayer(player, from(player));
    }

    public static IslandGuiStatePayload from(final ServerPlayer player) {
        final List<String> templates = IslandTemplate.ids();
        if (!(player.level() instanceof ServerLevel currentLevel)) {
            return empty(Config.enableVoidIslandFeatures, templates);
        }
        if (!VoidIslandWorld.areFeaturesEnabled(currentLevel.getServer())) {
            return empty(false, templates);
        }
        final ServerLevel level = currentLevel.getServer().overworld();
        PlayerIdentitySavedData.get(level).remember(player);
        final IslandSavedData islands = IslandSavedData.get(level);
        final IslandSavedData.IslandRecord island = islands.getIslandFor(player.getUUID()).orElse(null);
        final IslandSavedData.IslandRecord pendingInvite = islands.getPendingInvitation(player.getUUID()).orElse(null);
        if (island == null) {
            return new IslandGuiStatePayload(
                    true,
                    false,
                    false,
                    false,
                    pendingInvite != null,
                    pendingInvite == null ? "" : pendingInvite.ownerName(),
                    "",
                    "",
                    "",
                    "",
                    List.of(),
                    List.of(),
                    List.of(),
                    templates
            );
        }

        final List<String> players = new ArrayList<>();
        players.add(island.ownerName());
        island.members().values()
                .stream()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .forEach(players::add);
        final List<String> invites = island.invites().values()
                .stream()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
        final List<String> trustedVisitors = island.trustedVisitors().values()
                .stream()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
        return new IslandGuiStatePayload(
                true,
                true,
                island.isOwner(player.getUUID()),
                island.isMember(player.getUUID()),
                pendingInvite != null,
                pendingInvite == null ? "" : pendingInvite.ownerName(),
                island.ownerName(),
                island.type(),
                island.dimension().location().toString(),
                formatPosition(island.home()),
                players,
                invites,
                trustedVisitors,
                templates
        );
    }

    static void handle(final IslandGuiStatePayload payload, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> clientHandler.accept(payload));
        context.setPacketHandled(true);
    }

    private static IslandGuiStatePayload empty(final boolean enabled, final List<String> templates) {
        return new IslandGuiStatePayload(
                enabled,
                false,
                false,
                false,
                false,
                "",
                "",
                "",
                "",
                "",
                List.of(),
                List.of(),
                List.of(),
                templates
        );
    }

    static void encode(final IslandGuiStatePayload payload, final FriendlyByteBuf buffer) {
        buffer.writeBoolean(payload.enabled);
        buffer.writeBoolean(payload.hasIsland);
        buffer.writeBoolean(payload.owner);
        buffer.writeBoolean(payload.member);
        buffer.writeBoolean(payload.hasPendingInvite);
        buffer.writeUtf(payload.pendingInviteOwner, MAX_TEXT_LENGTH);
        buffer.writeUtf(payload.ownerName, MAX_TEXT_LENGTH);
        buffer.writeUtf(payload.islandType, MAX_TEXT_LENGTH);
        buffer.writeUtf(payload.dimension, MAX_TEXT_LENGTH);
        buffer.writeUtf(payload.home, MAX_TEXT_LENGTH);
        writeList(buffer, payload.players);
        writeList(buffer, payload.invites);
        writeList(buffer, payload.trustedVisitors);
        writeList(buffer, payload.templates);
    }

    static IslandGuiStatePayload decode(final FriendlyByteBuf buffer) {
        return new IslandGuiStatePayload(
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readUtf(MAX_TEXT_LENGTH),
                buffer.readUtf(MAX_TEXT_LENGTH),
                buffer.readUtf(MAX_TEXT_LENGTH),
                buffer.readUtf(MAX_TEXT_LENGTH),
                buffer.readUtf(MAX_TEXT_LENGTH),
                readList(buffer),
                readList(buffer),
                readList(buffer),
                readList(buffer)
        );
    }

    private static void writeList(final FriendlyByteBuf buffer, final List<String> values) {
        buffer.writeVarInt(Math.min(values.size(), MAX_LIST_SIZE));
        values.stream()
                .limit(MAX_LIST_SIZE)
                .map(IslandGuiStatePayload::clean)
                .forEach(value -> buffer.writeUtf(value, MAX_TEXT_LENGTH));
    }

    private static List<String> readList(final FriendlyByteBuf buffer) {
        final int size = Math.max(0, Math.min(buffer.readVarInt(), MAX_LIST_SIZE));
        final List<String> values = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            values.add(buffer.readUtf(MAX_TEXT_LENGTH));
        }
        return values;
    }

    private static String clean(final String value) {
        return value == null ? "" : value;
    }

    private static String formatPosition(final BlockPos pos) {
        return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }
}
