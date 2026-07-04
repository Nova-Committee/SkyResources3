package committee.nova.mods.skyresources3.test;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.authlib.GameProfile;
import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.core.island.IslandSavedData;
import committee.nova.mods.skyresources3.core.island.IslandTemplate;
import committee.nova.mods.skyresources3.core.island.PlayerIdentitySavedData;
import committee.nova.mods.skyresources3.core.island.VoidIslandWorld;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
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
            final ServerPlayer player = GameTestAssertions.makeMockServerPlayer(helper, GameType.CREATIVE);
            assertCommandSucceeds(helper, player, "island create");

            final IslandSavedData islands = IslandSavedData.get(helper.getLevel().getServer().overworld());
            final IslandSavedData.IslandRecord created = getIslandOrFail(helper, islands, player);
            GameTestAssertions.assertValueEqual(
                    helper,
                    IslandTemplate.DEFAULT_ID,
                    created.type(),
                    "Created island should use the default type"
            );
            helper.assertTrue(created.includes(player.getUUID()), "Created island should include its owner");
            GameTestAssertions.assertValueEqual(
                    helper,
                    1,
                    created.playerCount(),
                    "Solo island should start with one player"
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
            GameTestAssertions.assertValueEqual(
                    helper,
                    SAND_TEMPLATE_ID,
                    reset.type(),
                    "Reset should persist the requested island type"
            );
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
    public static void initialSpawnPlatformUsesBedrock(final GameTestHelper helper) {
        final int originalSpawnPlatformRadius = Config.voidIslandSpawnPlatformRadius;
        final Block originalSpawnPlatformBlock = Config.voidIslandSpawnPlatformBlock;
        Config.voidIslandSpawnPlatformRadius = 1;
        Config.voidIslandSpawnPlatformBlock = Blocks.COBBLESTONE;

        try {
            final ServerLevel level = helper.getLevel();
            VoidIslandWorld.ensureInitialSpawnPlatform(level);
            helper.assertTrue(
                    level.getBlockState(VoidIslandWorld.spawnPlatformCenter()).is(Blocks.BEDROCK),
                    "Initial spawn platform should always use bedrock"
            );
            helper.assertTrue(
                    level.getBlockState(VoidIslandWorld.spawnPlatformCenter().offset(1, 0, 1)).is(Blocks.BEDROCK),
                    "Initial spawn platform should keep the configured radius"
            );
            helper.succeed();
        } finally {
            Config.voidIslandSpawnPlatformRadius = originalSpawnPlatformRadius;
            Config.voidIslandSpawnPlatformBlock = originalSpawnPlatformBlock;
        }
    }

    @SuppressWarnings("removal")
    public static void voidIslandFeatureDefaultFollowsEmptyFlatWorld(final GameTestHelper helper) {
        final boolean originalVoidIslandFeatures = Config.enableVoidIslandFeatures;

        try {
            final MinecraftServer server = helper.getLevel().getServer();
            Config.enableVoidIslandFeatures = false;
            GameTestAssertions.assertValueEqual(
                    helper,
                    VoidIslandWorld.getInitialSpawnLevel(server).isPresent(),
                    VoidIslandWorld.areFeaturesEnabled(server),
                    "Void island preset worlds should enable island features even when the config is disabled"
            );

            Config.enableVoidIslandFeatures = true;
            helper.assertTrue(
                    VoidIslandWorld.areFeaturesEnabled(server),
                    "Config-enabled void island features should remain enabled"
            );
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
            final ServerPlayer player = GameTestAssertions.makeMockServerPlayer(helper, GameType.CREATIVE);
            assertCommandSucceeds(helper, player, "island spawn");

            final BlockPos spawnHome = VoidIslandWorld.spawnHome();
            if (spawnLevel == null) {
                GameTestAssertions.assertValueEqual(
                        helper,
                        helper.getLevel().dimension(),
                        player.level().dimension(),
                        "Player should stay in the fallback level when the void island level is unavailable"
                );
            } else {
                GameTestAssertions.assertValueEqual(
                        helper,
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

            GameTestAssertions.assertValueEqual(
                    helper,
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
    public static void visitTeleportsToOfflineSavedIsland(final GameTestHelper helper) {
        final boolean originalVoidIslandFeatures = Config.enableVoidIslandFeatures;
        Config.enableVoidIslandFeatures = true;

        try {
            final String ownerName = "offline_owner";
            final String memberName = "offline_member";
            final UUID owner = UUID.randomUUID();
            final UUID member = UUID.randomUUID();
            final BlockPos home = new BlockPos(12288, VoidIslandWorld.ISLAND_Y + 1, 12288);
            final IslandSavedData islands = IslandSavedData.get(helper.getLevel().getServer().overworld());
            islands.createIsland(owner, ownerName, helper.getLevel().dimension(), home, IslandTemplate.DEFAULT_ID);
            islands.invite(owner, member, memberName);
            islands.acceptInvitation(member, memberName);

            final ServerPlayer ownerVisitor = makeNamedMockServerPlayerInLevel(helper, "offline_visit_guest");
            assertCommandSucceeds(helper, ownerVisitor, "island visit OFFLINE_OWNER");
            GameTestAssertions.assertValueEqual(
                    helper,
                    home,
                    ownerVisitor.blockPosition(),
                    "Offline owner name lookup should teleport to the saved island"
            );

            final ServerPlayer memberVisitor = makeNamedMockServerPlayerInLevel(helper, "offline_member_guest");
            assertCommandSucceeds(helper, memberVisitor, "island visit OFFLINE_MEMBER");
            GameTestAssertions.assertValueEqual(
                    helper,
                    home,
                    memberVisitor.blockPosition(),
                    "Offline island member name lookup should teleport to that island"
            );
            helper.succeed();
        } finally {
            Config.enableVoidIslandFeatures = originalVoidIslandFeatures;
        }
    }

    @SuppressWarnings("removal")
    public static void magmaIslandPlacesCrystalFluid(final GameTestHelper helper) {
        final boolean originalVoidIslandFeatures = Config.enableVoidIslandFeatures;
        Config.enableVoidIslandFeatures = true;

        try {
            final ServerPlayer player = makeNamedMockServerPlayerInLevel(helper, "magma_owner");
            assertCommandSucceeds(helper, player, "island create magma");

            final IslandSavedData islands = IslandSavedData.get(helper.getLevel().getServer().overworld());
            final IslandSavedData.IslandRecord island = getIslandOrFail(helper, islands, player);
            final ServerLevel targetLevel = helper.getLevel().getServer().getLevel(island.dimension());
            if (targetLevel == null) {
                helper.fail("Expected target level for magma island");
            }

            final BlockPos center = island.home().below();
            helper.assertTrue(
                    targetLevel.getBlockState(center.west().south()).is(ModBlocks.CRYSTAL_FLUID.get()),
                    "Magma island should place Crystal Fluid at the legacy VIC position"
            );
            helper.succeed();
        } finally {
            Config.enableVoidIslandFeatures = originalVoidIslandFeatures;
        }
    }

    @SuppressWarnings("removal")
    public static void starterTemplatesUseLegacyLayeredStructures(final GameTestHelper helper) {
        final boolean originalVoidIslandFeatures = Config.enableVoidIslandFeatures;
        Config.enableVoidIslandFeatures = true;

        try {
            final IslandSavedData.IslandRecord grass = createIsland(helper, "legacy_grass_owner", "grass");
            final ServerLevel grassLevel = islandLevel(helper, grass);
            final BlockPos grassCenter = grass.home().below();
            assertBlock(helper, grassLevel, grassCenter, Blocks.GRASS_BLOCK, "Grass island top layer");
            assertBlock(helper, grassLevel, grassCenter.below(), Blocks.BEDROCK, "Grass island bottom layer");
            assertBlock(helper, grassLevel, grassCenter.above(), Blocks.OAK_LOG, "Grass island tree trunk");
            assertBlock(helper, grassLevel, grassCenter.above(5), Blocks.OAK_LEAVES, "Grass island tree canopy");

            final IslandSavedData.IslandRecord sand = createIsland(helper, "legacy_sand_owner", "sand");
            final ServerLevel sandLevel = islandLevel(helper, sand);
            final BlockPos sandCenter = sand.home().below();
            assertBlock(helper, sandLevel, sandCenter, Blocks.RED_SAND, "Sand island top layer");
            assertBlock(helper, sandLevel, sandCenter.below(), Blocks.BEDROCK, "Sand island bottom layer");
            assertBlock(helper, sandLevel, sandCenter.offset(-1, 1, 1), Blocks.CACTUS, "Sand island cactus");

            final IslandSavedData.IslandRecord snow = createIsland(helper, "legacy_snow_owner", "snow");
            final ServerLevel snowLevel = islandLevel(helper, snow);
            final BlockPos snowCenter = snow.home().below();
            assertBlock(helper, snowLevel, snowCenter, Blocks.SNOW_BLOCK, "Snow island top layer");
            assertBlock(helper, snowLevel, snowCenter.below(), Blocks.BEDROCK, "Snow island bottom layer");
            assertBlock(helper, snowLevel, snowCenter.above(), Blocks.SNOW, "Snow island snow cover");
            assertBlock(helper, snowLevel, snowCenter.offset(-1, 1, 1), Blocks.PUMPKIN, "Snow island pumpkin");

            final IslandSavedData.IslandRecord wood = createIsland(helper, "legacy_wood_owner", "wood");
            final ServerLevel woodLevel = islandLevel(helper, wood);
            final BlockPos woodCenter = wood.home().below();
            assertBlock(helper, woodLevel, woodCenter, Blocks.WATER, "Wood island center water");
            assertBlock(helper, woodLevel, woodCenter.below(), Blocks.BEDROCK, "Wood island bottom layer");
            assertBlock(helper, woodLevel, woodCenter.east(), Blocks.DARK_OAK_PLANKS, "Wood island planks");
            assertBlock(helper, woodLevel, woodCenter.offset(-1, 1, 1), Blocks.TRIPWIRE, "Wood island string");

            helper.succeed();
        } finally {
            Config.enableVoidIslandFeatures = originalVoidIslandFeatures;
        }
    }

    @SuppressWarnings("removal")
    public static void islandInviteHomeLeaveAndDisband(final GameTestHelper helper) {
        final boolean originalVoidIslandFeatures = Config.enableVoidIslandFeatures;
        Config.enableVoidIslandFeatures = true;

        try {
            final ServerPlayer owner = makeNamedMockServerPlayerInLevel(helper, "island_owner");
            final ServerPlayer member = makeNamedMockServerPlayerInLevel(helper, "island_member");
            final ServerPlayer stayingMember = makeNamedMockServerPlayerInLevel(helper, "island_stayer");
            assertCommandSucceeds(helper, owner, "island create");
            assertCommandSucceeds(helper, owner, "island trust " + member.getName().getString());
            assertCommandSucceeds(helper, owner, "island invite " + member.getName().getString());
            assertCommandSucceeds(helper, member, "island accept");
            assertCommandSucceeds(helper, owner, "island invite " + stayingMember.getName().getString());
            assertCommandSucceeds(helper, stayingMember, "island accept");

            final IslandSavedData islands = IslandSavedData.get(helper.getLevel().getServer().overworld());
            IslandSavedData.IslandRecord island = getIslandOrFail(helper, islands, owner);
            helper.assertTrue(island.includes(member.getUUID()), "Accepted player should be an island member");
            helper.assertTrue(
                    island.includes(stayingMember.getUUID()),
                    "Accepted player should stay on the island"
            );
            helper.assertTrue(
                    !island.isTrustedVisitor(member.getUUID()),
                    "Accepting an island invite should remove redundant trusted access"
            );

            assertCommandSucceeds(helper, member, "island home");
            GameTestAssertions.assertValueEqual(
                    helper,
                    island.home(),
                    member.blockPosition(),
                    "Island member /island home should use the owner's island"
            );

            assertCommandSucceeds(helper, member, "island leave");
            helper.assertTrue(
                    islands.getIslandFor(member.getUUID()).isEmpty(),
                    "Island leave should remove the player from the island"
            );
            island = getIslandOrFail(helper, islands, owner);
            helper.assertTrue(
                    !island.isTrustedVisitor(member.getUUID()),
                    "Leaving an island should not leave trusted access behind"
            );
            assertCommandSucceeds(helper, owner, "island invite " + member.getName().getString());
            assertCommandSucceeds(helper, member, "island accept");
            helper.assertTrue(
                    getIslandOrFail(helper, islands, owner).includes(member.getUUID()),
                    "A player who left through /island leave should be able to rejoin the island"
            );
            assertCommandSucceeds(helper, member, "island leave");
            helper.assertTrue(
                    islands.getIslandFor(member.getUUID()).isEmpty(),
                    "Repeated island leave should remove the player from the island"
            );
            assertCommandSucceeds(helper, owner, "island invite " + member.getName().getString());
            assertCommandSucceeds(helper, member, "island accept");
            helper.assertTrue(
                    getIslandOrFail(helper, islands, owner).includes(member.getUUID()),
                    "A player who left again should be able to rejoin the island"
            );
            assertCommandSucceeds(helper, owner, "island disband");
            helper.assertTrue(
                    islands.getIsland(owner.getUUID()).isEmpty(),
                    "Disbanding should abandon the owner's island record"
            );
            helper.assertTrue(
                    islands.getIslandFor(stayingMember.getUUID()).isEmpty(),
                    "Disbanding should remove online members from the island"
            );
            helper.assertTrue(
                    islands.getIslandFor(member.getUUID()).isEmpty(),
                    "Disbanding should remove rejoined online members from the island"
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

    @SuppressWarnings("removal")
    public static void offlineIdentityInviteAndTrust(final GameTestHelper helper) {
        final boolean originalVoidIslandFeatures = Config.enableVoidIslandFeatures;
        Config.enableVoidIslandFeatures = true;

        try {
            final ServerLevel storageLevel = helper.getLevel().getServer().overworld();
            final PlayerIdentitySavedData identities = PlayerIdentitySavedData.get(storageLevel);
            final UUID memberId = UUID.randomUUID();
            final String memberName = "cached_member";
            final UUID trustedId = UUID.randomUUID();
            final String trustedName = "cached_guest";
            identities.remember(memberId, memberName);
            identities.remember(trustedId, trustedName);

            final ServerPlayer owner = makeNamedMockServerPlayerInLevel(helper, "cached_owner");
            assertCommandSucceeds(helper, owner, "island create");
            assertCommandSucceeds(helper, owner, "island invite " + memberName.toUpperCase());

            final IslandSavedData islands = IslandSavedData.get(storageLevel);
            helper.assertTrue(
                    islands.getPendingInvitation(memberId).isPresent(),
                    "Offline cached island invite should persist against the cached UUID"
            );

            final ServerPlayer member = makeNamedMockServerPlayerInLevel(helper, memberId, memberName);
            assertCommandSucceeds(helper, member, "island accept");
            helper.assertTrue(
                    getIslandOrFail(helper, islands, owner).includes(memberId),
                    "Cached offline invite should be accepted by the later online player"
            );

            assertCommandSucceeds(helper, owner, "island trust " + trustedName.toUpperCase());
            final IslandSavedData.IslandRecord island = getIslandOrFail(helper, islands, owner);
            helper.assertTrue(
                    island.isTrustedVisitor(trustedId),
                    "Offline cached trust should persist the cached visitor UUID"
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

    private static IslandSavedData.IslandRecord createIsland(
            final GameTestHelper helper,
            final String playerName,
            final String template
    ) {
        final ServerPlayer player = makeNamedMockServerPlayerInLevel(helper, playerName);
        assertCommandSucceeds(helper, player, "island create " + template);
        final IslandSavedData islands = IslandSavedData.get(helper.getLevel().getServer().overworld());
        return getIslandOrFail(helper, islands, player);
    }

    private static ServerLevel islandLevel(final GameTestHelper helper, final IslandSavedData.IslandRecord island) {
        final ServerLevel level = helper.getLevel().getServer().getLevel(island.dimension());
        if (level == null) {
            helper.fail("Expected target level for island");
        }
        return level;
    }

    private static void assertBlock(
            final GameTestHelper helper,
            final ServerLevel level,
            final BlockPos pos,
            final Block block,
            final String message
    ) {
        helper.assertTrue(level.getBlockState(pos).is(block), message);
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
        return makeNamedMockServerPlayerInLevel(helper, UUID.randomUUID(), name);
    }

    private static ServerPlayer makeNamedMockServerPlayerInLevel(
            final GameTestHelper helper,
            final UUID uuid,
            final String name
    ) {
        return GameTestAssertions.makeNamedMockServerPlayer(helper, uuid, name, GameType.CREATIVE);
    }

    private IslandCommandGameTests() {
    }
}
