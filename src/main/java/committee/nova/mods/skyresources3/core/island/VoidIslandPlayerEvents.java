package committee.nova.mods.skyresources3.core.island;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

public final class VoidIslandPlayerEvents {
    public static void onServerStarted(final ServerStartedEvent event) {
        if (!VoidIslandWorld.areFeaturesEnabled(event.getServer())) {
            return;
        }

        VoidIslandWorld.getInitialSpawnLevel(event.getServer())
                .ifPresent(VoidIslandWorld::ensureInitialSpawnPlatform);
    }

    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!(player.level() instanceof ServerLevel currentLevel)) {
            return;
        }

        if (!VoidIslandWorld.areFeaturesEnabled(currentLevel.getServer())) {
            return;
        }

        final ServerLevel storageLevel = currentLevel.getServer().overworld();
        final IslandSavedData islands = IslandSavedData.get(storageLevel);
        if (islands.getIslandFor(player.getUUID()).isPresent()) {
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
                "message.skyresources.island.guide.create",
                String.join(", ", IslandTemplate.ids())
        ));
        player.sendSystemMessage(Component.translatable("message.skyresources.island.guide.join"));
    }

    private VoidIslandPlayerEvents() {
    }
}
