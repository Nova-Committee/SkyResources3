package committee.nova.mods.skyresources3.island;

import committee.nova.mods.skyresources3.Config;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class IslandProtectionEvents {
    public static final int PROTECTION_RADIUS = 128;

    public static void onBlockBreak(final BlockEvent.BreakEvent event) {
        if (event.isCanceled() || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (event.getPlayer() instanceof ServerPlayer player && denyIfProtected(player, level, event.getPos())) {
            event.setCanceled(true);
        }
    }

    public static void onBlockPlace(final BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        final Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer player && denyIfProtected(player, level, event.getPos())) {
            event.setCanceled(true);
        }
    }

    public static void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (denyIfProtected(player, level, event.getPos())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    private static boolean denyIfProtected(
            final ServerPlayer player,
            final ServerLevel level,
            final BlockPos pos
    ) {
        if (!Config.enableVoidIslandFeatures) {
            return false;
        }

        final ServerLevel storageLevel = level.getServer().overworld();
        final IslandSavedData islands = IslandSavedData.get(storageLevel);
        final TeamSavedData teams = TeamSavedData.get(storageLevel);
        final Optional<IslandSavedData.IslandRecord> island =
                islands.findIslandAt(level.dimension(), pos, PROTECTION_RADIUS);
        if (island.isEmpty() || canModify(player.getUUID(), island.get(), teams)) {
            return false;
        }

        player.displayClientMessage(
                Component.translatable("message.skyresources3.island.protection.denied", island.get().ownerName()),
                true
        );
        return true;
    }

    private static boolean canModify(
            final UUID player,
            final IslandSavedData.IslandRecord island,
            final TeamSavedData teams
    ) {
        if (island.owner().equals(player)) {
            return true;
        }
        return teams.getOwnedTeam(island.owner())
                .map(team -> team.includes(player))
                .orElse(false);
    }

    private IslandProtectionEvents() {
    }
}
