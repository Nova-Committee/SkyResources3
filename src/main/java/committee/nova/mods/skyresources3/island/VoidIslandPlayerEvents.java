package committee.nova.mods.skyresources3.island;

import committee.nova.mods.skyresources3.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

public final class VoidIslandPlayerEvents {
    public static void onServerStarted(final ServerStartedEvent event) {
        if (!Config.enableVoidIslandFeatures) {
            return;
        }

        VoidIslandWorld.getInitialSpawnLevel(event.getServer())
                .ifPresent(VoidIslandWorld::ensureInitialSpawnPlatform);
    }

    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (!Config.enableVoidIslandFeatures || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!(player.level() instanceof ServerLevel currentLevel)) {
            return;
        }

        final ServerLevel storageLevel = currentLevel.getServer().overworld();
        final IslandSavedData islands = IslandSavedData.get(storageLevel);
        final TeamSavedData teams = TeamSavedData.get(storageLevel);
        if (islands.getIsland(player.getUUID()).isPresent() || teams.getTeamFor(player.getUUID()).isPresent()) {
            return;
        }

        VoidIslandWorld.getInitialSpawnLevel(currentLevel.getServer()).ifPresent(spawnLevel -> {
            VoidIslandWorld.ensureInitialSpawnPlatform(spawnLevel);
            VoidIslandCommands.teleportTo(player, spawnLevel, VoidIslandWorld.spawnHome());
            sendGuide(player);
        });
    }

    static void sendGuide(final ServerPlayer player) {
        player.sendSystemMessage(Component.translatable(
                "message.skyresources3.island.guide.create",
                String.join(", ", IslandTemplate.ids())
        ));
        player.sendSystemMessage(Component.translatable("message.skyresources3.island.guide.join"));
    }

    private VoidIslandPlayerEvents() {
    }
}
