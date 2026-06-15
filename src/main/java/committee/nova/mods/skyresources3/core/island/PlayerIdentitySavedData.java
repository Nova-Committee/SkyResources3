package committee.nova.mods.skyresources3.core.island;

import committee.nova.mods.skyresources3.Skyresources3;
import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class PlayerIdentitySavedData extends SavedData {
    private static final String DATA_ID = Skyresources3.MODID + "_player_identities";
    private static final Codec<PlayerIdentitySavedData> CODEC = Codec.unboundedMap(
                    UUIDUtil.STRING_CODEC,
                    Codec.STRING
            )
            .optionalFieldOf("players", Map.of())
            .xmap(PlayerIdentitySavedData::new, data -> Map.copyOf(data.players))
            .codec();
    private static final SavedDataType<PlayerIdentitySavedData> TYPE = new SavedDataType<>(
            DATA_ID,
            PlayerIdentitySavedData::new,
            CODEC
    );

    private final Map<UUID, String> players;

    public PlayerIdentitySavedData() {
        this(Map.of());
    }

    private PlayerIdentitySavedData(final Map<UUID, String> players) {
        this.players = new HashMap<>(players);
    }

    public static PlayerIdentitySavedData get(final ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public void remember(final ServerPlayer player) {
        this.remember(player.getUUID(), player.getName().getString());
    }

    public void remember(final UUID player, final String playerName) {
        final String previousName = this.players.put(player, playerName);
        if (!playerName.equals(previousName)) {
            this.setDirty();
        }
    }

    public Optional<PlayerIdentity> findByName(final String playerName) {
        PlayerIdentity match = null;
        for (final Map.Entry<UUID, String> entry : this.players.entrySet()) {
            if (!entry.getValue().equalsIgnoreCase(playerName)) {
                continue;
            }
            if (match != null) {
                return Optional.empty();
            }
            match = new PlayerIdentity(entry.getKey(), entry.getValue());
        }
        return Optional.ofNullable(match);
    }

    public record PlayerIdentity(UUID uuid, String name) {
    }
}
