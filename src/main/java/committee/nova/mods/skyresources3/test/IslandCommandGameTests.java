package committee.nova.mods.skyresources3.test;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.authlib.GameProfile;
import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.island.IslandSavedData;
import committee.nova.mods.skyresources3.island.IslandTemplate;
import committee.nova.mods.skyresources3.island.TeamSavedData;
import committee.nova.mods.skyresources3.island.VoidIslandWorld;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class IslandCommandGameTests {
    private static final String SAND_TEMPLATE_ID = "sand";

    @SuppressWarnings("removal")
    public static void createInfoAndReset(final GameTestHelper helper) {
        final boolean originalVoidIslandFeatures = Config.enableVoidIslandFeatures;
        final int originalIslandProtectionRadius = Config.islandProtectionRadius;
        Config.enableVoidIslandFeatures = true;
        Config.islandProtectionRadius = 4;

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

            final ServerLevel targetLevel = helper.getLevel().getServer().getLevel(created.dimension());
            if (targetLevel == null) {
                helper.fail("Expected target level for created island");
            }
            final BlockPos center = created.home().below();
            final BlockPos protectedResidue = center.offset(Config.islandProtectionRadius, 2, 0);
            final BlockPos outsideResidue = center.offset(Config.islandProtectionRadius + 1, 2, 0);
            targetLevel.setBlock(protectedResidue, Blocks.DIAMOND_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
            targetLevel.setBlock(outsideResidue, Blocks.GOLD_BLOCK.defaultBlockState(), Block.UPDATE_ALL);

            assertCommandSucceeds(helper, player, "island info");
            assertCommandSucceeds(helper, player, "island reset " + SAND_TEMPLATE_ID + " confirm");

            final IslandSavedData.IslandRecord reset = getIslandOrFail(helper, islands, player);
            helper.assertValueEqual(SAND_TEMPLATE_ID, reset.type(), "Reset should persist the requested island type");
            helper.assertTrue(
                    targetLevel.getBlockState(protectedResidue).isAir(),
                    "Reset should clear residue inside the island protection radius"
            );
            helper.assertTrue(
                    targetLevel.getBlockState(outsideResidue).is(Blocks.GOLD_BLOCK),
                    "Reset should not clear blocks outside the island protection radius"
            );
            helper.succeed();
        } finally {
            Config.enableVoidIslandFeatures = originalVoidIslandFeatures;
            Config.islandProtectionRadius = originalIslandProtectionRadius;
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

    @SuppressWarnings("removal")
    public static void visitTeleportsToOnlinePlayerIsland(final GameTestHelper helper) {
        final boolean originalVoidIslandFeatures = Config.enableVoidIslandFeatures;
        Config.enableVoidIslandFeatures = true;

        try {
            final ServerPlayer owner = makeNamedMockServerPlayerInLevel(helper, "visit_owner");
            final ServerPlayer visitor = makeNamedMockServerPlayerInLevel(helper, "visit_guest");
            assertCommandSucceeds(helper, owner, "island create");

            final IslandSavedData islands = IslandSavedData.get(helper.getLevel().getServer().overworld());
            final IslandSavedData.IslandRecord island = getIslandOrFail(helper, islands, owner);
            assertCommandSucceeds(helper, visitor, "island visit " + owner.getName().getString());

            helper.assertValueEqual(
                    island.home(),
                    visitor.blockPosition(),
                    "Visitor should be teleported to the target player's island home"
            );
            helper.succeed();
        } finally {
            Config.enableVoidIslandFeatures = originalVoidIslandFeatures;
        }
    }

    @SuppressWarnings("removal")
    public static void teamInviteHomeLeaveAndDisband(final GameTestHelper helper) {
        final boolean originalVoidIslandFeatures = Config.enableVoidIslandFeatures;
        Config.enableVoidIslandFeatures = true;

        try {
            final ServerPlayer owner = makeNamedMockServerPlayerInLevel(helper, "team_owner");
            final ServerPlayer member = makeNamedMockServerPlayerInLevel(helper, "team_member");
            assertCommandSucceeds(helper, owner, "island create");
            assertCommandSucceeds(helper, owner, "island invite " + member.getName().getString());
            assertCommandSucceeds(helper, member, "island accept");

            final IslandSavedData islands = IslandSavedData.get(helper.getLevel().getServer().overworld());
            final TeamSavedData teams = TeamSavedData.get(helper.getLevel().getServer().overworld());
            final IslandSavedData.IslandRecord island = getIslandOrFail(helper, islands, owner);
            final TeamSavedData.TeamRecord team = getTeamOrFail(helper, teams, owner);
            helper.assertTrue(team.includes(member.getUUID()), "Accepted player should be a team member");

            assertCommandSucceeds(helper, member, "island home");
            helper.assertValueEqual(
                    island.home(),
                    member.blockPosition(),
                    "Team member /island home should use the owner's island"
            );
            assertCommandSucceeds(helper, member, "skyresources3 team home");
            helper.assertValueEqual(
                    island.home(),
                    member.blockPosition(),
                    "Team member /skyresources3 team home should use the owner's island"
            );

            assertCommandSucceeds(helper, member, "island leave");
            helper.assertTrue(
                    teams.getTeamFor(member.getUUID()).isEmpty(),
                    "Leaving should remove the player from the team"
            );
            assertCommandSucceeds(helper, owner, "skyresources3 team disband");
            helper.assertTrue(
                    teams.getOwnedTeam(owner.getUUID()).isEmpty(),
                    "Disbanding should remove the owner's team"
            );
            helper.succeed();
        } finally {
            Config.enableVoidIslandFeatures = originalVoidIslandFeatures;
        }
    }

    @SuppressWarnings("removal")
    public static void trustListAndUntrustVisitor(final GameTestHelper helper) {
        final boolean originalVoidIslandFeatures = Config.enableVoidIslandFeatures;
        Config.enableVoidIslandFeatures = true;

        try {
            final ServerPlayer owner = makeNamedMockServerPlayerInLevel(helper, "trust_owner");
            final ServerPlayer visitor = makeNamedMockServerPlayerInLevel(helper, "trust_guest");
            assertCommandSucceeds(helper, owner, "island create");
            assertCommandSucceeds(helper, owner, "island trust " + visitor.getName().getString());

            final IslandSavedData islands = IslandSavedData.get(helper.getLevel().getServer().overworld());
            IslandSavedData.IslandRecord island = getIslandOrFail(helper, islands, owner);
            helper.assertTrue(
                    island.isTrustedVisitor(visitor.getUUID()),
                    "Trust command should persist the visitor UUID"
            );
            assertCommandSucceeds(helper, owner, "island trusted");
            assertCommandSucceeds(helper, owner, "island untrust " + visitor.getName().getString());

            island = getIslandOrFail(helper, islands, owner);
            helper.assertTrue(
                    !island.isTrustedVisitor(visitor.getUUID()),
                    "Untrust command should remove the visitor UUID"
            );
            helper.succeed();
        } finally {
            Config.enableVoidIslandFeatures = originalVoidIslandFeatures;
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

    private static TeamSavedData.TeamRecord getTeamOrFail(
            final GameTestHelper helper,
            final TeamSavedData teams,
            final ServerPlayer owner
    ) {
        final TeamSavedData.TeamRecord team = teams.getOwnedTeam(owner.getUUID()).orElse(null);
        if (team == null) {
            helper.fail("Expected team record for mock player");
        }
        return team;
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

    private static ServerPlayer makeNamedMockServerPlayerInLevel(final GameTestHelper helper, final String name) {
        final ServerLevel level = helper.getLevel();
        final GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        final CommonListenerCookie cookie = CommonListenerCookie.createInitial(profile, false);
        final ServerPlayer player = new NamedMockServerPlayer(
                level.getServer(),
                level,
                cookie.gameProfile(),
                cookie.clientInformation()
        );
        final Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        level.getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
        return player;
    }

    private static final class NamedMockServerPlayer extends ServerPlayer {
        private NamedMockServerPlayer(
                final MinecraftServer server,
                final ServerLevel level,
                final GameProfile gameProfile,
                final net.minecraft.server.level.ClientInformation clientInformation
        ) {
            super(server, level, gameProfile, clientInformation);
        }

        @Override
        public GameType gameMode() {
            return GameType.CREATIVE;
        }
    }

    private IslandCommandGameTests() {
    }
}
