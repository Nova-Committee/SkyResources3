package committee.nova.mods.skyresources3.island;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.Skyresources3;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class VoidIslandCommands {
    private static final int ISLAND_ORIGIN = 8192;
    private static final int ISLAND_SPACING = 512;
    private static final int ISLAND_Y = 192;
    private static final int ISLANDS_PER_ROW = 256;
    private static final Set<Relative> NO_RELATIVE_MOVEMENT = Set.of();

    public static void register(final RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(Skyresources3.MODID).then(islandNode()));
        event.getDispatcher().register(islandNode());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> islandNode() {
        return Commands.literal("island")
                .then(Commands.literal("create").executes(context -> createIsland(context.getSource())))
                .then(Commands.literal("home").executes(context -> teleportHome(context.getSource())))
                .then(Commands.literal("info").executes(context -> showInfo(context.getSource())));
    }

    private static int createIsland(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel islandLevel = source.getServer().overworld();
        final IslandSavedData islands = IslandSavedData.get(islandLevel);
        if (islands.getIsland(player.getUUID()).isPresent()) {
            source.sendFailure(Component.literal("You already have an island."));
            return 0;
        }

        final BlockPos center = nextIslandCenter(islands.islandCount());
        buildStarterIsland(islandLevel, center);
        final IslandSavedData.IslandRecord island = islands.createIsland(
                player.getUUID(),
                player.getName().getString(),
                islandLevel.dimension(),
                center.above()
        );
        teleport(player, islandLevel, island.home());
        source.sendSuccess(
                () -> Component.literal("Created island and teleported to " + formatPosition(island.home()) + "."),
                false
        );
        return 1;
    }

    private static int teleportHome(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final IslandSavedData islands = IslandSavedData.get(source.getServer().overworld());
        final IslandSavedData.IslandRecord island = islands.getIsland(player.getUUID()).orElse(null);
        if (island == null) {
            source.sendFailure(Component.literal("You do not have an island yet. Use /island create first."));
            return 0;
        }

        final ServerLevel targetLevel = source.getServer().getLevel(island.dimension());
        if (targetLevel == null) {
            source.sendFailure(Component.literal("The island dimension is not available."));
            return 0;
        }

        teleport(player, targetLevel, island.home());
        source.sendSuccess(
                () -> Component.literal("Teleported to island home " + formatPosition(island.home()) + "."),
                false
        );
        return 1;
    }

    private static int showInfo(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final IslandSavedData islands = IslandSavedData.get(source.getServer().overworld());
        final IslandSavedData.IslandRecord island = islands.getIsland(player.getUUID()).orElse(null);
        if (island == null) {
            source.sendFailure(Component.literal("You do not have an island yet. Use /island create first."));
            return 0;
        }

        source.sendSuccess(
                () -> Component.literal("Island owner: " + island.ownerName()
                        + ", dimension: " + island.dimension().identifier()
                        + ", home: " + formatPosition(island.home())),
                false
        );
        return 1;
    }

    private static int disabled(final CommandSourceStack source) {
        source.sendFailure(Component.literal("Void island features are disabled in the config."));
        return 0;
    }

    private static BlockPos nextIslandCenter(final int islandCount) {
        final int x = ISLAND_ORIGIN + islandCount % ISLANDS_PER_ROW * ISLAND_SPACING;
        final int z = ISLAND_ORIGIN + islandCount / ISLANDS_PER_ROW * ISLAND_SPACING;
        return new BlockPos(x, ISLAND_Y, z);
    }

    private static void buildStarterIsland(final ServerLevel level, final BlockPos center) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                level.setBlock(
                        center.offset(x, 0, z),
                        Blocks.GRASS_BLOCK.defaultBlockState(),
                        Block.UPDATE_ALL
                );
            }
        }
        level.setBlock(center.offset(2, 1, 2), Blocks.OAK_SAPLING.defaultBlockState(), Block.UPDATE_ALL);
    }

    private static void teleport(final ServerPlayer player, final ServerLevel level, final BlockPos home) {
        player.teleportTo(
                level,
                home.getX() + 0.5D,
                home.getY(),
                home.getZ() + 0.5D,
                NO_RELATIVE_MOVEMENT,
                player.getYRot(),
                player.getXRot(),
                false
        );
    }

    private static String formatPosition(final BlockPos pos) {
        return pos.getX() + " " + pos.getY() + " " + pos.getZ();
    }

    private VoidIslandCommands() {
    }
}
