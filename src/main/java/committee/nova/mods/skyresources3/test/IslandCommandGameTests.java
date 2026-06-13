package committee.nova.mods.skyresources3.test;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.island.IslandSavedData;
import committee.nova.mods.skyresources3.island.IslandTemplate;
import committee.nova.mods.skyresources3.island.VoidIslandWorld;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class IslandCommandGameTests {
    private static final String SAND_TEMPLATE_ID = "sand";

    @SuppressWarnings("removal")
    public static void createInfoAndReset(final GameTestHelper helper) {
        final boolean originalVoidIslandFeatures = Config.enableVoidIslandFeatures;
        Config.enableVoidIslandFeatures = true;

        try {
            final ServerPlayer player = helper.makeMockServerPlayerInLevel();
            assertCommandSucceeds(helper, player, "island create");

            final IslandSavedData islands = IslandSavedData.get(helper.getLevel().getServer().overworld());
            final IslandSavedData.IslandRecord created = getIslandOrFail(helper, islands, player);
            helper.assertValueEqual(
                    IslandTemplate.DEFAULT_ID,
                    created.type(),
                    "Created island should use the default type"
            );

            assertCommandSucceeds(helper, player, "island info");
            assertCommandSucceeds(helper, player, "island reset " + SAND_TEMPLATE_ID + " confirm");

            final IslandSavedData.IslandRecord reset = getIslandOrFail(helper, islands, player);
            helper.assertValueEqual(SAND_TEMPLATE_ID, reset.type(), "Reset should persist the requested island type");
            helper.succeed();
        } finally {
            Config.enableVoidIslandFeatures = originalVoidIslandFeatures;
        }
    }

    @SuppressWarnings("removal")
    public static void spawnGeneratesConfiguredPlatform(final GameTestHelper helper) {
        final boolean originalVoidIslandFeatures = Config.enableVoidIslandFeatures;
        final int originalSpawnPlatformRadius = Config.voidIslandSpawnPlatformRadius;
        final Block originalSpawnPlatformBlock = Config.voidIslandSpawnPlatformBlock;
        Config.enableVoidIslandFeatures = true;
        Config.voidIslandSpawnPlatformRadius = 1;
        Config.voidIslandSpawnPlatformBlock = Blocks.COBBLESTONE;

        try {
            final ServerLevel spawnLevel = VoidIslandWorld.get(helper.getLevel().getServer()).orElse(null);
            final ServerPlayer player = helper.makeMockServerPlayerInLevel();
            assertCommandSucceeds(helper, player, "island spawn");

            final BlockPos spawnHome = VoidIslandWorld.spawnHome();
            if (spawnLevel == null) {
                helper.assertValueEqual(
                        helper.getLevel().dimension(),
                        player.level().dimension(),
                        "Player should stay in the fallback level when the void island level is unavailable"
                );
            } else {
                helper.assertValueEqual(
                        spawnHome,
                        player.blockPosition(),
                        "Player should be teleported to void spawn home"
                );
            }

            final ServerLevel platformLevel = spawnLevel == null ? helper.getLevel() : spawnLevel;
            VoidIslandWorld.ensureSpawnPlatform(platformLevel);
            helper.assertTrue(
                    platformLevel.getBlockState(VoidIslandWorld.spawnPlatformCenter()).is(Blocks.COBBLESTONE),
                    "Spawn platform should use the configured block"
            );
            helper.assertTrue(
                    platformLevel.getBlockState(VoidIslandWorld.spawnPlatformCenter().offset(1, 0, 1))
                            .is(Blocks.COBBLESTONE),
                    "Spawn platform should use the configured radius"
            );
            helper.succeed();
        } finally {
            Config.enableVoidIslandFeatures = originalVoidIslandFeatures;
            Config.voidIslandSpawnPlatformRadius = originalSpawnPlatformRadius;
            Config.voidIslandSpawnPlatformBlock = originalSpawnPlatformBlock;
        }
    }

    private static IslandSavedData.IslandRecord getIslandOrFail(
            final GameTestHelper helper,
            final IslandSavedData islands,
            final ServerPlayer player
    ) {
        final IslandSavedData.IslandRecord island = islands.getIsland(player.getUUID()).orElse(null);
        if (island == null) {
            helper.fail("Expected island record for mock player");
        }
        return island;
    }

    private static void assertCommandSucceeds(
            final GameTestHelper helper,
            final ServerPlayer player,
            final String command
    ) {
        final int result = runCommand(helper, player, command);
        helper.assertTrue(result > 0, "/" + command + " should return a positive result");
    }

    private static int runCommand(final GameTestHelper helper, final ServerPlayer player, final String command) {
        final MinecraftServer server = helper.getLevel().getServer();
        final CommandSourceStack source = player.createCommandSourceStack();
        try {
            return server.getCommands().getDispatcher().execute(command, source);
        } catch (final CommandSyntaxException exception) {
            helper.fail("Command failed: /" + command + " - " + exception.getMessage());
            return 0;
        }
    }

    private IslandCommandGameTests() {
    }
}
