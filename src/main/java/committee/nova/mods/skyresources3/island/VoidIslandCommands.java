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
    private static final int MAX_ISLAND_RESET_RADIUS = ISLAND_SPACING / 2 - 1;
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
                .executes(context -> showGuide(context.getSource()))
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
                .then(Commands.literal("leave").executes(context -> leaveIsland(context.getSource())))
                .then(Commands.literal("disband").executes(context -> disbandIsland(context.getSource())));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> teamNode() {
        return Commands.literal("team")
                .then(Commands.literal("create").executes(context -> rejectManualTeamCreate(context.getSource())))
                .then(Commands.literal("invite")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .executes(context -> invitePlayer(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "player")
                                ))))
                .then(Commands.literal("accept").executes(context -> acceptInvite(context.getSource())))
                .then(Commands.literal("leave").executes(context -> leaveIsland(context.getSource())))
                .then(Commands.literal("disband").executes(context -> disbandIsland(context.getSource())))
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
                .then(Commands.literal("home").executes(context -> teleportHome(context.getSource())))
                .then(Commands.literal("info").executes(context -> showInfo(context.getSource())));
    }

    private static int createIsland(final CommandSourceStack source, final String typeName) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel storageLevel = source.getServer().overworld();
        final ServerLevel islandLevel = VoidIslandWorld.getOrOverworld(source.getServer());
        PlayerIdentitySavedData.get(storageLevel).remember(player);
        final IslandSavedData islands = IslandSavedData.get(storageLevel);
        if (islands.getIsland(player.getUUID()).isPresent()) {
            source.sendFailure(Component.translatable("message.skyresources3.island.already_exists"));
            return 0;
        }
        if (islands.getIslandFor(player.getUUID()).isPresent()) {
            source.sendFailure(Component.translatable("message.skyresources3.island.create.member_blocked"));
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
        teleportTo(player, islandLevel, island.home());
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
        final IslandSavedData.IslandRecord island = getAccessibleIsland(player.getUUID(), islands).orElse(null);
        if (island == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.missing"));
            return 0;
        }

        final ServerLevel targetLevel = source.getServer().getLevel(island.dimension());
        if (targetLevel == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.dimension_missing"));
            return 0;
        }

        teleportTo(player, targetLevel, island.home());
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
            teleportTo(player, fallbackLevel, spawn);
        } else {
            VoidIslandWorld.ensureSpawnPlatform(spawnLevel);
            spawn = VoidIslandWorld.spawnHome();
            teleportTo(player, spawnLevel, spawn);
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
        final ServerLevel level = source.getServer().overworld();
        final PlayerIdentitySavedData identities = PlayerIdentitySavedData.get(level);
        identities.remember(player);
        final IslandSavedData islands = IslandSavedData.get(level);
        final ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(targetName);
        if (target != null) {
            identities.remember(target);
        }
        if (target != null && player.getUUID().equals(target.getUUID())) {
            source.sendFailure(Component.translatable("message.skyresources3.island.visit.self"));
            return 0;
        }

        final VisitTarget visitTarget = resolveVisitTarget(targetName, target, identities, islands).orElse(null);
        if (visitTarget == null) {
            source.sendFailure(Component.translatable(
                    "message.skyresources3.island.visit.target_no_island",
                    targetName
            ));
            return 0;
        }
        final ServerLevel targetLevel = source.getServer().getLevel(visitTarget.island().dimension());
        if (targetLevel == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.dimension_missing"));
            return 0;
        }

        teleportTo(player, targetLevel, visitTarget.island().home());
        source.sendSuccess(
                () -> Component.translatable(
                        "message.skyresources3.island.visit.success",
                        visitTarget.name(),
                        formatPosition(visitTarget.island().home())
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
        final ServerLevel level = source.getServer().overworld();
        final PlayerIdentitySavedData identities = PlayerIdentitySavedData.get(level);
        identities.remember(player);
        final CommandTarget target = resolveKnownPlayer(source, level, targetName);
        if (target == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.trust.target_missing", targetName));
            return 0;
        }
        if (player.getUUID().equals(target.uuid())) {
            source.sendFailure(Component.translatable("message.skyresources3.island.trust.self"));
            return 0;
        }

        final IslandSavedData islands = IslandSavedData.get(level);
        final IslandSavedData.IslandRecord island = getOwnedIslandForTrust(source, player, islands).orElse(null);
        if (island == null) {
            return 0;
        }

        if (island.includes(target.uuid())) {
            source.sendFailure(Component.translatable(
                    "message.skyresources3.island.trust.already_member",
                    target.name()
            ));
            return 0;
        }
        if (island.isTrustedVisitor(target.uuid())) {
            source.sendFailure(Component.translatable(
                    "message.skyresources3.island.trust.already_trusted",
                    target.name()
            ));
            return 0;
        }

        islands.trustVisitor(
                player.getUUID(),
                target.uuid(),
                target.name()
        );
        source.sendSuccess(
                () -> Component.translatable(
                        "message.skyresources3.island.trust.success",
                        target.name()
                ),
                false
        );
        target.sendSystemMessage("message.skyresources3.island.trust.received", player.getName().getString());
        return 1;
    }

    private static int untrustVisitor(final CommandSourceStack source, final String targetName)
            throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel level = source.getServer().overworld();
        PlayerIdentitySavedData.get(level).remember(player);
        final IslandSavedData islands = IslandSavedData.get(level);
        final IslandSavedData.IslandRecord island = getOwnedIslandForTrust(source, player, islands).orElse(null);
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
        final IslandSavedData.IslandRecord island = getOwnedIslandForTrust(source, player, islands).orElse(null);
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
        if (islands.getIsland(player.getUUID()).isEmpty()) {
            if (islands.getIslandFor(player.getUUID()).isPresent()) {
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
        final IslandSavedData.IslandRecord island = islands.getIsland(player.getUUID()).orElse(null);
        if (island == null) {
            if (islands.getIslandFor(player.getUUID()).isPresent()) {
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
        clearIslandResetArea(targetLevel, center);
        template.build(targetLevel, center);
        islands.updateIslandType(player.getUUID(), template.id());
        teleportTo(player, targetLevel, island.home());
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
        final IslandSavedData.IslandRecord island = getAccessibleIsland(player.getUUID(), islands).orElse(null);
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
                        formatPosition(island.home()),
                        island.playerNames(),
                        island.invites().size()
                ),
                false
        );
        return 1;
    }

    private static int rejectManualTeamCreate(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        source.getPlayerOrException();
        source.sendFailure(Component.translatable("message.skyresources3.island.team_alias.create_disabled"));
        return 0;
    }

    private static int invitePlayer(final CommandSourceStack source, final String targetName)
            throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel level = source.getServer().overworld();
        final PlayerIdentitySavedData identities = PlayerIdentitySavedData.get(level);
        identities.remember(player);
        final CommandTarget target = resolveKnownPlayer(source, level, targetName);
        if (target == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.invite.target_missing", targetName));
            return 0;
        }
        if (player.getUUID().equals(target.uuid())) {
            source.sendFailure(Component.translatable("message.skyresources3.island.invite.self"));
            return 0;
        }

        final IslandSavedData islands = IslandSavedData.get(level);
        final IslandSavedData.IslandRecord ownerIsland = islands.getIsland(player.getUUID()).orElse(null);
        if (ownerIsland == null) {
            if (islands.getIslandFor(player.getUUID()).isPresent()) {
                source.sendFailure(Component.translatable("message.skyresources3.island.invite.not_owner"));
                return 0;
            }
            source.sendFailure(Component.translatable("message.skyresources3.island.invite.no_island"));
            return 0;
        }
        if (islands.getIsland(target.uuid()).isPresent()) {
            source.sendFailure(Component.translatable("message.skyresources3.island.invite.target_has_island", targetName));
            return 0;
        }
        if (islands.getIslandFor(target.uuid()).isPresent()) {
            source.sendFailure(Component.translatable("message.skyresources3.island.invite.target_in_island", targetName));
            return 0;
        }
        if (islands.getPendingInvitation(target.uuid()).isPresent()) {
            source.sendFailure(Component.translatable(
                    "message.skyresources3.island.invite.target_pending",
                    targetName
            ));
            return 0;
        }

        islands.invite(
                player.getUUID(),
                target.uuid(),
                target.name()
        );
        source.sendSuccess(
                () -> Component.translatable("message.skyresources3.island.invite.sent", target.name()),
                false
        );
        target.sendSystemMessage("message.skyresources3.island.invite.received", player.getName().getString());
        return 1;
    }

    private static int acceptInvite(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel level = source.getServer().overworld();
        PlayerIdentitySavedData.get(level).remember(player);
        final IslandSavedData islands = IslandSavedData.get(level);
        if (islands.getIsland(player.getUUID()).isPresent()) {
            source.sendFailure(Component.translatable("message.skyresources3.island.accept.has_island"));
            return 0;
        }
        if (islands.getIslandFor(player.getUUID()).isPresent()) {
            source.sendFailure(Component.translatable("message.skyresources3.island.already_joined"));
            return 0;
        }

        final IslandSavedData.IslandRecord pendingIsland = islands.getPendingInvitation(player.getUUID()).orElse(null);
        if (pendingIsland == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.accept.missing"));
            return 0;
        }
        final ServerLevel islandLevel = source.getServer().getLevel(pendingIsland.dimension());
        if (islandLevel == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.dimension_missing"));
            return 0;
        }

        final IslandSavedData.IslandRecord island = islands.acceptInvitation(
                player.getUUID(),
                player.getName().getString()
        ).orElse(null);
        if (island == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.accept.missing"));
            return 0;
        }

        islands.untrustVisitor(island.owner(), player.getUUID());
        teleportTo(player, islandLevel, island.home());
        source.sendSuccess(
                () -> Component.translatable(
                        "message.skyresources3.island.accept.success",
                        island.ownerName(),
                        formatPosition(island.home())
                ),
                false
        );
        return 1;
    }

    private static int leaveIsland(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final ServerLevel level = source.getServer().overworld();
        final IslandSavedData islands = IslandSavedData.get(level);
        final IslandSavedData.IslandRecord island = islands.getIslandFor(player.getUUID()).orElse(null);
        if (island == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.leave.not_in_island"));
            return 0;
        }
        if (island.isOwner(player.getUUID())) {
            source.sendFailure(Component.translatable("message.skyresources3.island.leave.owner"));
            return 0;
        }

        islands.leaveIsland(player.getUUID());
        islands.untrustVisitor(island.owner(), player.getUUID());
        teleportToInitialSpawn(player);
        source.sendSuccess(
                () -> Component.translatable("message.skyresources3.island.leave.success"),
                false
        );
        return 1;
    }

    private static int disbandIsland(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        final ServerPlayer player = source.getPlayerOrException();
        final IslandSavedData islands = IslandSavedData.get(source.getServer().overworld());
        final IslandSavedData.IslandRecord island = islands.getIsland(player.getUUID()).orElse(null);
        if (island == null) {
            source.sendFailure(Component.translatable("message.skyresources3.island.disband.not_owner"));
            return 0;
        }

        islands.deleteIsland(player.getUUID());
        teleportToInitialSpawn(player);
        for (final UUID memberId : island.members().keySet()) {
            final ServerPlayer member = source.getServer().getPlayerList().getPlayer(memberId);
            if (member != null) {
                teleportToInitialSpawn(member);
            }
        }
        source.sendSuccess(
                () -> Component.translatable("message.skyresources3.island.disband.success"),
                false
        );
        return 1;
    }

    private static int showGuide(final CommandSourceStack source) throws CommandSyntaxException {
        if (!Config.enableVoidIslandFeatures) {
            return disabled(source);
        }

        source.getPlayerOrException();
        sendGuide(source);
        return 1;
    }

    private static void sendGuide(final CommandSourceStack source) {
        source.sendSuccess(
                () -> Component.translatable(
                        "message.skyresources3.island.guide.create",
                        String.join(", ", IslandTemplate.ids())
                ),
                false
        );
        source.sendSuccess(
                () -> Component.translatable("message.skyresources3.island.guide.join"),
                false
        );
    }

    private static int disabled(final CommandSourceStack source) {
        source.sendFailure(Component.translatable("message.skyresources3.island.disabled"));
        return 0;
    }

    private static Optional<IslandSavedData.IslandRecord> getAccessibleIsland(
            final UUID player,
            final IslandSavedData islands
    ) {
        return islands.getIslandFor(player);
    }

    private static Optional<VisitTarget> resolveVisitTarget(
            final String targetName,
            final ServerPlayer onlineTarget,
            final PlayerIdentitySavedData identities,
            final IslandSavedData islands
    ) {
        if (onlineTarget != null) {
            return getAccessibleIsland(onlineTarget.getUUID(), islands)
                    .map(island -> new VisitTarget(onlineTarget.getName().getString(), island));
        }

        return islands.findIslandByOwnerName(targetName)
                .map(island -> new VisitTarget(island.ownerName(), island))
                .or(() -> islands.findIslandByPlayerName(targetName)
                        .map(island -> new VisitTarget(targetName, island)))
                .or(() -> identities.findByName(targetName)
                        .flatMap(identity -> getAccessibleIsland(identity.uuid(), islands)
                                .map(island -> new VisitTarget(identity.name(), island))));
    }

    private record VisitTarget(String name, IslandSavedData.IslandRecord island) {
    }

    private static CommandTarget resolveKnownPlayer(
            final CommandSourceStack source,
            final ServerLevel level,
            final String targetName
    ) {
        final ServerPlayer onlineTarget = source.getServer().getPlayerList().getPlayerByName(targetName);
        final PlayerIdentitySavedData identities = PlayerIdentitySavedData.get(level);
        if (onlineTarget != null) {
            identities.remember(onlineTarget);
            return new CommandTarget(
                    onlineTarget.getUUID(),
                    onlineTarget.getName().getString(),
                    onlineTarget
            );
        }
        return identities.findByName(targetName)
                .map(identity -> new CommandTarget(identity.uuid(), identity.name(), null))
                .orElse(null);
    }

    private record CommandTarget(UUID uuid, String name, ServerPlayer onlinePlayer) {
        private void sendSystemMessage(final String key, final String actorName) {
            if (this.onlinePlayer != null) {
                this.onlinePlayer.sendSystemMessage(Component.translatable(key, actorName));
            }
        }
    }

    private static Optional<IslandSavedData.IslandRecord> getOwnedIslandForTrust(
            final CommandSourceStack source,
            final ServerPlayer player,
            final IslandSavedData islands
    ) {
        final Optional<IslandSavedData.IslandRecord> island = islands.getIsland(player.getUUID());
        if (island.isPresent()) {
            return island;
        }
        if (islands.getIslandFor(player.getUUID()).isPresent()) {
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

    private static void clearIslandResetArea(final ServerLevel level, final BlockPos center) {
        final int resetRadius = resetClearRadius();
        for (int x = -resetRadius; x <= resetRadius; x++) {
            for (int y = STARTER_RESET_MIN_Y_OFFSET; y <= STARTER_RESET_MAX_Y_OFFSET; y++) {
                for (int z = -resetRadius; z <= resetRadius; z++) {
                    level.setBlock(center.offset(x, y, z), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    private static int resetClearRadius() {
        return Math.max(STARTER_RESET_RADIUS, Math.min(Config.islandProtectionRadius, MAX_ISLAND_RESET_RADIUS));
    }

    private static void teleportToInitialSpawn(final ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel currentLevel)) {
            return;
        }

        VoidIslandWorld.getInitialSpawnLevel(currentLevel.getServer()).ifPresent(spawnLevel -> {
            VoidIslandWorld.ensureInitialSpawnPlatform(spawnLevel);
            teleportTo(player, spawnLevel, VoidIslandWorld.spawnHome());
            VoidIslandPlayerEvents.sendGuide(player);
        });
    }

    static void teleportTo(final ServerPlayer player, final ServerLevel level, final BlockPos home) {
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
