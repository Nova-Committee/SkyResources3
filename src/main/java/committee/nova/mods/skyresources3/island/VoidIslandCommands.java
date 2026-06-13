package committee.nova.mods.skyresources3.island;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.Skyresources3;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class VoidIslandCommands {
    private static final int ISLAND_ORIGIN = 8192;
    private static final int ISLAND_SPACING = 512;
    private static final int ISLANDS_PER_ROW = 256;
    private static final int STARTER_RESET_RADIUS = 3;
    private static final int STARTER_RESET_MIN_Y_OFFSET = -1;
    private static final int STARTER_RESET_MAX_Y_OFFSET = 4;
    private static final BlockPos TEMPORARY_SPAWN_COLUMN = new BlockPos(0, 0, 0);
    private static final Set<Relative> NO_RELATIVE_MOVEMENT = Set.of();

    public static void register(final RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(Skyresources3.MODID).then(islandNode()).then(teamNode()));
        event.getDispatcher().register(islandNode());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> islandNode() {
        return Commands.literal("island")
                .then(Commands.literal("create")
                        .executes(context -> createIsland(context.getSource(), IslandTemplate.DEFAULT_ID))
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests(VoidIslandCommands::suggestIslandTemplates)
                                .executes(context -> createIsland(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "type")
                                ))))
                .then(Commands.literal("home").executes(context -> teleportHome(context.getSource())))
                .then(Commands.literal("spawn").executes(context -> teleportSpawn(context.getSource())))
                .then(Commands.literal("visit")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .executes(context -> visitIsland(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "player")
                                ))))
                .then(Commands.literal("trust")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .executes(context -> trustVisitor(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "player")
                                ))))
                .then(Commands.literal("untrust")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(VoidIslandCommands::suggestTrustedVisitors)
                                .executes(context -> untrustVisitor(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "player")
                                ))))
                .then(Commands.literal("trusted").executes(context -> listTrustedVisitors(context.getSource())))
                .then(Commands.literal("reset")
                        .executes(context -> requestReset(context.getSource(), null))
                        .then(Commands.literal("confirm").executes(context -> resetIsland(context.getSource(), null)))
                        .then(Commands.argument("type", StringArgumentType.word())
                                .suggests(VoidIslandCommands::suggestIslandTemplates)
                                .executes(context -> requestReset(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "type")
                                ))
                                .then(Commands.literal("confirm").executes(context -> resetIsland(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "type")
                                )))))
                .then(Commands.literal("info").executes(context -> showInfo(context.getSource())))
                .then(Commands.literal("invite")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .executes(context -> invitePlayer(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "player")
                                ))))
                .then(Commands.literal("trust")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .executes(context -> trustVisitor(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "player")
                                ))))
                .then(Commands.literal("untrust")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(VoidIslandCommands::suggestTrustedVisitors)
                                .executes(context -> untrustVisitor(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "player")
                                ))))
                .then(Commands.literal("trusted").executes(context -> listTrustedVisitors(context.getSource())))
                .then(Commands.literal("accept").executes(context -> acceptInvite(context.getSource())))
                .then(Commands.literal("leave").executes(context -> leaveTeam(context.getSource())));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> teamNode() {
        return Commands.literal("team")
                .then(Commands.literal("create").executes(context -> createTeam(context.getSource())))
                .then(Commands.literal("invite")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .executes(context -> invitePlayer(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "player")
                                ))))
                .then(Commands.literal("accept").executes(context -> acceptInvite(context.getSource())))
                .then(Commands.literal("leave").executes(context -> leaveTeam(context.getSource())))
                .then(Commands.literal("disband").executes(context -> disbandTeam(context.getSource())))
                .then(Commands.literal("home").executes(context -> teleportTeamHome(context.getSource())))
                .then(Commands.literal("info").executes(context -> showTeamInfo(context.getSource())));
    }

    private static int createIsland(final CommandSourceStack source, final String typeName) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel storageLevel = source.getServer().overworld();
        final ServerLevel islandLevel = VoidIslandWorld.getOrOverworld(source.getServer());
        final IslandSavedData islands = IslandSavedData.get(storageLevel);
        final TeamSavedData teams = TeamSavedData.get(storageLevel);
        final Optional<TeamSavedData.TeamRecord> team = teams.getTeamFor(player.getUUID());
        if (team.isPresent() && !team.get().isOwner(player.getUUID())) {
            source.sendFailure(Component.translatable("message.skyresources3.island.create.member_blocked"));
            return 0;
        }
        if (islands.getIsland(player.getUUID()).isPresent()) {
            source.sendFailure(Component.translatable("message.skyresources3.island.already_exists"));
            return 0;
        }

        final IslandTemplate template = parseTemplate(source, typeName).orElse(null);
        if (template == null) {
            return 0;
        }

        final BlockPos center = nextIslandCenter(islands.islandCount());
        template.build(islandLevel, center);
        final IslandSavedData.IslandRecord island = islands.createIsland(
                player.getUUID(),
                player.getName().getString(),
                islandLevel.dimension(),
                template.home(center),
                template.id()
        );
        teleport(player, islandLevel, island.home());
        source.sendSuccess(
                () -> Component.translatable(
                        "message.skyresources3.island.created",
                        template.id(),
                        formatPosition(island.home())
                ),
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
        final TeamSavedData teams = TeamSavedData.get(source.getServer().overworld());
        final IslandSavedData.IslandRecord island = getAccessibleIsland(player.getUUID(), islands, teams).orElse(null);
        if (island == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.missing"));
            return 0;
        }

        final ServerLevel targetLevel = source.getServer().getLevel(island.dimension());
        if (targetLevel == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.dimension_missing"));
            return 0;
        }

        teleport(player, targetLevel, island.home());
        source.sendSuccess(
                () -> Component.translatable("message.skyresources3.island.home", formatPosition(island.home())),
                false
        );
        return 1;
    }

    private static int teleportSpawn(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel spawnLevel = VoidIslandWorld.get(source.getServer()).orElse(null);
        final BlockPos spawn;
        if (spawnLevel == null) {
            final ServerLevel fallbackLevel = source.getServer().overworld();
            spawn = fallbackLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, TEMPORARY_SPAWN_COLUMN);
            teleport(player, fallbackLevel, spawn);
        } else {
            VoidIslandWorld.ensureSpawnPlatform(spawnLevel);
            spawn = VoidIslandWorld.spawnHome();
            teleport(player, spawnLevel, spawn);
        }

        source.sendSuccess(
                () -> Component.translatable("message.skyresources3.island.spawn", formatPosition(spawn)),
                false
        );
        return 1;
    }

    private static int visitIsland(final CommandSourceStack source, final String targetName)
            throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(targetName);
        if (target == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.visit.target_missing", targetName));
            return 0;
        }
        if (player.getUUID().equals(target.getUUID())) {
            source.sendFailure(Component.translatable("message.skyresources3.island.visit.self"));
            return 0;
        }

        final ServerLevel level = source.getServer().overworld();
        final IslandSavedData islands = IslandSavedData.get(level);
        final TeamSavedData teams = TeamSavedData.get(level);
        final IslandSavedData.IslandRecord island = getAccessibleIsland(target.getUUID(), islands, teams).orElse(null);
        if (island == null) {
            source.sendFailure(Component.translatable(
                    "message.skyresources3.island.visit.target_no_island",
                    target.getName().getString()
            ));
            return 0;
        }

        final ServerLevel targetLevel = source.getServer().getLevel(island.dimension());
        if (targetLevel == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.dimension_missing"));
            return 0;
        }

        teleport(player, targetLevel, island.home());
        source.sendSuccess(
                () -> Component.translatable(
                        "message.skyresources3.island.visit.success",
                        target.getName().getString(),
                        formatPosition(island.home())
                ),
                false
        );
        return 1;
    }

    private static int trustVisitor(final CommandSourceStack source, final String targetName)
            throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(targetName);
        if (target == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.trust.target_missing", targetName));
            return 0;
        }
        if (player.getUUID().equals(target.getUUID())) {
            source.sendFailure(Component.translatable("message.skyresources3.island.trust.self"));
            return 0;
        }

        final ServerLevel level = source.getServer().overworld();
        final IslandSavedData islands = IslandSavedData.get(level);
        final TeamSavedData teams = TeamSavedData.get(level);
        final IslandSavedData.IslandRecord island = getOwnedIslandForTrust(source, player, islands, teams).orElse(null);
        if (island == null) {
            return 0;
        }

        if (teams.getOwnedTeam(player.getUUID()).map(team -> team.includes(target.getUUID())).orElse(false)) {
            source.sendFailure(Component.translatable(
                    "message.skyresources3.island.trust.already_member",
                    target.getName().getString()
            ));
            return 0;
        }
        if (island.isTrustedVisitor(target.getUUID())) {
            source.sendFailure(Component.translatable(
                    "message.skyresources3.island.trust.already_trusted",
                    target.getName().getString()
            ));
            return 0;
        }

        islands.trustVisitor(
                player.getUUID(),
                target.getUUID(),
                target.getName().getString()
        );
        source.sendSuccess(
                () -> Component.translatable(
                        "message.skyresources3.island.trust.success",
                        target.getName().getString()
                ),
                false
        );
        target.sendSystemMessage(Component.translatable(
                "message.skyresources3.island.trust.received",
                player.getName().getString()
        ));
        return 1;
    }

    private static int untrustVisitor(final CommandSourceStack source, final String targetName)
            throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel level = source.getServer().overworld();
        final IslandSavedData islands = IslandSavedData.get(level);
        final TeamSavedData teams = TeamSavedData.get(level);
        final IslandSavedData.IslandRecord island = getOwnedIslandForTrust(source, player, islands, teams).orElse(null);
        if (island == null) {
            return 0;
        }

        final String removedName = islands.untrustVisitor(player.getUUID(), targetName).orElse(null);
        if (removedName == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.untrust.missing", targetName));
            return 0;
        }

        source.sendSuccess(
                () -> Component.translatable("message.skyresources3.island.untrust.success", removedName),
                false
        );
        return 1;
    }

    private static int listTrustedVisitors(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel level = source.getServer().overworld();
        final IslandSavedData islands = IslandSavedData.get(level);
        final TeamSavedData teams = TeamSavedData.get(level);
        final IslandSavedData.IslandRecord island = getOwnedIslandForTrust(source, player, islands, teams).orElse(null);
        if (island == null) {
            return 0;
        }

        if (!island.hasTrustedVisitors()) {
            source.sendSuccess(
                    () -> Component.translatable("message.skyresources3.island.trusted.empty"),
                    false
            );
            return 1;
        }

        source.sendSuccess(
                () -> Component.translatable(
                        "message.skyresources3.island.trusted.list",
                        island.trustedVisitorNames()
                ),
                false
        );
        return 1;
    }

    private static int requestReset(final CommandSourceStack source, final String typeName) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel level = source.getServer().overworld();
        final IslandSavedData islands = IslandSavedData.get(level);
        final TeamSavedData teams = TeamSavedData.get(level);
        if (islands.getIsland(player.getUUID()).isEmpty()) {
            if (teams.getTeamFor(player.getUUID()).isPresent()) {
                source.sendFailure(Component.translatable("message.skyresources3.island.reset.not_owner"));
                return 0;
            }
            source.sendFailure(Component.translatable("message.skyresources3.island.missing"));
            return 0;
        }

        final IslandSavedData.IslandRecord island = islands.getIsland(player.getUUID()).orElseThrow();
        final IslandTemplate template = getResetTemplate(source, island, typeName).orElse(null);
        if (template == null) {
            return 0;
        }

        final String confirmCommand = typeName == null
                ? "/island reset confirm"
                : "/island reset " + template.id() + " confirm";
        source.sendSuccess(
                () -> Component.translatable(
                        "message.skyresources3.island.reset.confirm",
                        template.id(),
                        confirmCommand
                ),
                false
        );
        return 1;
    }

    private static int resetIsland(final CommandSourceStack source, final String typeName) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel level = source.getServer().overworld();
        final IslandSavedData islands = IslandSavedData.get(level);
        final TeamSavedData teams = TeamSavedData.get(level);
        final IslandSavedData.IslandRecord island = islands.getIsland(player.getUUID()).orElse(null);
        if (island == null) {
            if (teams.getTeamFor(player.getUUID()).isPresent()) {
                source.sendFailure(Component.translatable("message.skyresources3.island.reset.not_owner"));
                return 0;
            }
            source.sendFailure(Component.translatable("message.skyresources3.island.missing"));
            return 0;
        }

        final IslandTemplate template = getResetTemplate(source, island, typeName).orElse(null);
        if (template == null) {
            return 0;
        }

        final ServerLevel targetLevel = source.getServer().getLevel(island.dimension());
        if (targetLevel == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.dimension_missing"));
            return 0;
        }

        final BlockPos center = island.home().below();
        clearStarterIslandArea(targetLevel, center);
        template.build(targetLevel, center);
        islands.updateIslandType(player.getUUID(), template.id());
        teleport(player, targetLevel, island.home());
        source.sendSuccess(
                () -> Component.translatable(
                        "message.skyresources3.island.reset.done",
                        template.id(),
                        formatPosition(island.home())
                ),
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
        final TeamSavedData teams = TeamSavedData.get(source.getServer().overworld());
        final IslandSavedData.IslandRecord island = getAccessibleIsland(player.getUUID(), islands, teams).orElse(null);
        if (island == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.missing"));
            return 0;
        }

        source.sendSuccess(
                () -> Component.translatable(
                        "message.skyresources3.island.info",
                        island.ownerName(),
                        island.type(),
                        island.dimension().identifier().toString(),
                        formatPosition(island.home())
                ),
                false
        );
        return 1;
    }

    private static int createTeam(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel level = source.getServer().overworld();
        final IslandSavedData islands = IslandSavedData.get(level);
        final TeamSavedData teams = TeamSavedData.get(level);
        if (teams.getTeamFor(player.getUUID()).isPresent()) {
            source.sendFailure(Component.translatable("message.skyresources3.team.already_in_team"));
            return 0;
        }
        if (islands.getIsland(player.getUUID()).isEmpty()) {
            source.sendFailure(Component.translatable("message.skyresources3.team.create.no_island"));
            return 0;
        }

        teams.createTeam(player.getUUID(), player.getName().getString());
        source.sendSuccess(
                () -> Component.translatable("message.skyresources3.team.created"),
                false
        );
        return 1;
    }

    private static int invitePlayer(final CommandSourceStack source, final String targetName)
            throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel level = source.getServer().overworld();
        final ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(targetName);
        if (target == null) {
            source.sendFailure(Component.translatable("message.skyresources3.team.invite.target_missing", targetName));
            return 0;
        }
        if (player.getUUID().equals(target.getUUID())) {
            source.sendFailure(Component.translatable("message.skyresources3.team.invite.self"));
            return 0;
        }

        final IslandSavedData islands = IslandSavedData.get(level);
        final TeamSavedData teams = TeamSavedData.get(level);
        final Optional<TeamSavedData.TeamRecord> inviterTeam = teams.getTeamFor(player.getUUID());
        if (inviterTeam.isPresent() && !inviterTeam.get().isOwner(player.getUUID())) {
            source.sendFailure(Component.translatable("message.skyresources3.team.invite.not_owner"));
            return 0;
        }
        if (islands.getIsland(player.getUUID()).isEmpty()) {
            source.sendFailure(Component.translatable("message.skyresources3.team.invite.no_island"));
            return 0;
        }
        if (islands.getIsland(target.getUUID()).isPresent()) {
            source.sendFailure(Component.translatable("message.skyresources3.team.invite.target_has_island", targetName));
            return 0;
        }
        if (teams.getTeamFor(target.getUUID()).isPresent()) {
            source.sendFailure(Component.translatable("message.skyresources3.team.invite.target_in_team", targetName));
            return 0;
        }
        if (teams.getPendingInvitation(target.getUUID()).isPresent()) {
            source.sendFailure(Component.translatable(
                    "message.skyresources3.team.invite.target_pending",
                    targetName
            ));
            return 0;
        }

        teams.invite(
                player.getUUID(),
                player.getName().getString(),
                target.getUUID(),
                target.getName().getString()
        );
        source.sendSuccess(
                () -> Component.translatable("message.skyresources3.team.invite.sent", target.getName().getString()),
                false
        );
        target.sendSystemMessage(Component.translatable(
                "message.skyresources3.team.invite.received",
                player.getName().getString()
        ));
        return 1;
    }

    private static int acceptInvite(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel level = source.getServer().overworld();
        final IslandSavedData islands = IslandSavedData.get(level);
        final TeamSavedData teams = TeamSavedData.get(level);
        if (islands.getIsland(player.getUUID()).isPresent()) {
            source.sendFailure(Component.translatable("message.skyresources3.team.accept.has_island"));
            return 0;
        }
        if (teams.getTeamFor(player.getUUID()).isPresent()) {
            source.sendFailure(Component.translatable("message.skyresources3.team.already_in_team"));
            return 0;
        }

        final TeamSavedData.TeamRecord team = teams.acceptInvitation(
                player.getUUID(),
                player.getName().getString()
        ).orElse(null);
        if (team == null) {
            source.sendFailure(Component.translatable("message.skyresources3.team.accept.missing"));
            return 0;
        }

        source.sendSuccess(
                () -> Component.translatable("message.skyresources3.team.accept.success", team.ownerName()),
                false
        );
        return 1;
    }

    private static int leaveTeam(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final TeamSavedData teams = TeamSavedData.get(source.getServer().overworld());
        final TeamSavedData.TeamRecord team = teams.getTeamFor(player.getUUID()).orElse(null);
        if (team == null) {
            source.sendFailure(Component.translatable("message.skyresources3.team.leave.not_in_team"));
            return 0;
        }
        if (team.isOwner(player.getUUID())) {
            source.sendFailure(Component.translatable("message.skyresources3.team.leave.owner"));
            return 0;
        }

        teams.leave(player.getUUID());
        source.sendSuccess(
                () -> Component.translatable("message.skyresources3.team.leave.success"),
                false
        );
        return 1;
    }

    private static int disbandTeam(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final TeamSavedData teams = TeamSavedData.get(source.getServer().overworld());
        if (teams.getOwnedTeam(player.getUUID()).isEmpty()) {
            source.sendFailure(Component.translatable("message.skyresources3.team.disband.not_owner"));
            return 0;
        }

        teams.disband(player.getUUID());
        source.sendSuccess(
                () -> Component.translatable("message.skyresources3.team.disband.success"),
                false
        );
        return 1;
    }

    private static int teleportTeamHome(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final IslandSavedData islands = IslandSavedData.get(source.getServer().overworld());
        final TeamSavedData teams = TeamSavedData.get(source.getServer().overworld());
        final TeamSavedData.TeamRecord team = teams.getTeamFor(player.getUUID()).orElse(null);
        if (team == null) {
            source.sendFailure(Component.translatable("message.skyresources3.team.leave.not_in_team"));
            return 0;
        }

        final IslandSavedData.IslandRecord island = islands.getIsland(team.owner()).orElse(null);
        if (island == null) {
            source.sendFailure(Component.translatable("message.skyresources3.team.home.no_island"));
            return 0;
        }
        final ServerLevel targetLevel = source.getServer().getLevel(island.dimension());
        if (targetLevel == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.dimension_missing"));
            return 0;
        }

        teleport(player, targetLevel, island.home());
        source.sendSuccess(
                () -> Component.translatable("message.skyresources3.team.home.success", formatPosition(island.home())),
                false
        );
        return 1;
    }

    private static int showTeamInfo(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final TeamSavedData teams = TeamSavedData.get(source.getServer().overworld());
        final TeamSavedData.TeamRecord team = teams.getTeamFor(player.getUUID()).orElse(null);
        if (team == null) {
            source.sendFailure(Component.translatable("message.skyresources3.team.leave.not_in_team"));
            return 0;
        }

        source.sendSuccess(
                () -> Component.translatable(
                        "message.skyresources3.team.info",
                        team.ownerName(),
                        team.playerCount(),
                        team.playerNames(),
                        team.invites().size()
                ),
                false
        );
        return 1;
    }

    private static int disabled(final CommandSourceStack source) {
        source.sendFailure(Component.translatable("message.skyresources3.island.disabled"));
        return 0;
    }

    private static Optional<IslandSavedData.IslandRecord> getAccessibleIsland(
            final UUID player,
            final IslandSavedData islands,
            final TeamSavedData teams
    ) {
        final Optional<IslandSavedData.IslandRecord> ownIsland = islands.getIsland(player);
        if (ownIsland.isPresent()) {
            return ownIsland;
        }
        return teams.getTeamFor(player).flatMap(team -> islands.getIsland(team.owner()));
    }

    private static Optional<IslandSavedData.IslandRecord> getOwnedIslandForTrust(
            final CommandSourceStack source,
            final ServerPlayer player,
            final IslandSavedData islands,
            final TeamSavedData teams
    ) {
        final Optional<IslandSavedData.IslandRecord> island = islands.getIsland(player.getUUID());
        if (island.isPresent()) {
            return island;
        }
        if (teams.getTeamFor(player.getUUID()).isPresent()) {
            source.sendFailure(Component.translatable("message.skyresources3.island.trust.not_owner"));
            return Optional.empty();
        }
        source.sendFailure(Component.translatable("message.skyresources3.island.missing"));
        return Optional.empty();
    }

    private static BlockPos nextIslandCenter(final int islandCount) {
        final int x = ISLAND_ORIGIN + islandCount % ISLANDS_PER_ROW * ISLAND_SPACING;
        final int z = ISLAND_ORIGIN + islandCount / ISLANDS_PER_ROW * ISLAND_SPACING;
        return new BlockPos(x, VoidIslandWorld.ISLAND_Y, z);
    }

    private static void clearStarterIslandArea(final ServerLevel level, final BlockPos center) {
        for (int x = -STARTER_RESET_RADIUS; x <= STARTER_RESET_RADIUS; x++) {
            for (int y = STARTER_RESET_MIN_Y_OFFSET; y <= STARTER_RESET_MAX_Y_OFFSET; y++) {
                for (int z = -STARTER_RESET_RADIUS; z <= STARTER_RESET_RADIUS; z++) {
                    level.setBlock(center.offset(x, y, z), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
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

    private static CompletableFuture<Suggestions> suggestIslandTemplates(
            final CommandContext<CommandSourceStack> context,
            final SuggestionsBuilder builder
    ) {
        IslandTemplate.ids().forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestTrustedVisitors(
            final CommandContext<CommandSourceStack> context,
            final SuggestionsBuilder builder
    ) {
        try {
            final ServerPlayer player = context.getSource().getPlayerOrException();
            final IslandSavedData islands = IslandSavedData.get(context.getSource().getServer().overworld());
            islands.getIsland(player.getUUID())
                    .map(IslandSavedData.IslandRecord::trustedVisitors)
                    .ifPresent(visitors -> visitors.values().forEach(builder::suggest));
        } catch (final CommandSyntaxException ignored) {
            // Suggestions are optional for non-player command sources.
        }
        return builder.buildFuture();
    }

    private static Optional<IslandTemplate> getResetTemplate(
            final CommandSourceStack source,
            final IslandSavedData.IslandRecord island,
            final String typeName
    ) {
        if (typeName == null) {
            return Optional.of(IslandTemplate.byId(island.type()).orElse(IslandTemplate.defaultTemplate()));
        }
        return parseTemplate(source, typeName);
    }

    private static Optional<IslandTemplate> parseTemplate(final CommandSourceStack source, final String typeName) {
        final Optional<IslandTemplate> template = IslandTemplate.byId(typeName);
        if (template.isEmpty()) {
            source.sendFailure(Component.translatable(
                    "message.skyresources3.island.type.invalid",
                    typeName,
                    String.join(", ", IslandTemplate.ids())
            ));
        }
        return template;
    }

    private VoidIslandCommands() {
    }
}
