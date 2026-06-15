package committee.nova.mods.skyresources3.core.island;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class PlayerIdentityEvents {
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.level() instanceof ServerLevel level) {
                PlayerIdentitySavedData.get(level.getServer().overworld()).remember(player);
            }
        }
    }

    private PlayerIdentityEvents() {
    }
}
