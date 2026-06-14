package committee.nova.mods.skyresources3.island;

import committee.nova.mods.skyresources3.Skyresources3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class IslandSavedData extends SavedData {
    private static final String DATA_ID = Skyresources3.MODID + "_islands";
    private static final Codec<IslandSavedData> CODEC = Codec.unboundedMap(
                    UUIDUtil.STRING_CODEC,
                    IslandRecord.CODEC
            )
            .optionalFieldOf("islands", Map.of())
            .xmap(IslandSavedData::new, data -> Map.copyOf(data.islands))
            .codec();
    private static final SavedDataType<IslandSavedData> TYPE = new SavedDataType<>(
            DATA_ID,
            IslandSavedData::new,
            CODEC
    );

    private final Map<UUID, IslandRecord> islands;

    public IslandSavedData() {
        this(Map.of());
    }

    private IslandSavedData(final Map<UUID, IslandRecord> islands) {
        this.islands = new HashMap<>(islands);
    }

    public static IslandSavedData get(final ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public Optional<IslandRecord> getIsland(final UUID owner) {
        return Optional.ofNullable(this.islands.get(owner));
    }

    public Optional<IslandRecord> getIslandFor(final UUID player) {
        final Optional<IslandRecord> ownedIsland = this.getIsland(player);
        if (ownedIsland.isPresent()) {
            return ownedIsland;
        }
        return this.islands.values()
                .stream()
                .filter(island -> island.isMember(player))
                .findFirst();
    }

    public Optional<IslandRecord> findIslandByOwnerName(final String ownerName) {
        return this.islands.values()
                .stream()
                .filter(island -> island.ownerName().equalsIgnoreCase(ownerName))
                .findFirst();
    }

    public Optional<IslandRecord> findIslandByPlayerName(final String playerName) {
        return this.islands.values()
                .stream()
                .filter(island -> island.includesName(playerName))
                .findFirst();
    }

    public Optional<IslandRecord> getPendingInvitation(final UUID player) {
        return this.islands.values()
                .stream()
                .filter(island -> island.hasInvite(player))
                .findFirst();
    }

    public int islandCount() {
        return this.islands.size();
    }

    public boolean deleteIsland(final UUID owner) {
        final boolean removed = this.islands.remove(owner) != null;
        if (removed) {
            this.setDirty();
        }
        return removed;
    }

    public Optional<IslandRecord> findIslandAt(
            final ResourceKey<Level> dimension,
            final BlockPos pos,
            final int horizontalRadius
    ) {
        return this.islands.values()
                .stream()
                .filter(island -> island.dimension().equals(dimension))
                .filter(island -> island.isWithinHorizontalRange(pos, horizontalRadius))
                .findFirst();
    }

    public IslandRecord createIsland(
            final UUID owner,
            final String ownerName,
            final ResourceKey<Level> dimension,
            final BlockPos home,
            final String type
    ) {
        final IslandRecord island = new IslandRecord(owner, ownerName, dimension, home, type, Map.of(), Map.of(), Map.of());
        this.islands.put(owner, island);
        this.setDirty();
        return island;
    }

    public Optional<IslandRecord> updateIslandType(final UUID owner, final String type) {
        final IslandRecord island = this.islands.get(owner);
        if (island == null) {
            return Optional.empty();
        }

        final IslandRecord updated = new IslandRecord(
                island.owner(),
                island.ownerName(),
                island.dimension(),
                island.home(),
                type,
                island.members(),
                island.invites(),
                island.trustedVisitors()
        );
        this.islands.put(owner, updated);
        this.setDirty();
        return Optional.of(updated);
    }

    public Optional<IslandRecord> trustVisitor(
            final UUID owner,
            final UUID visitor,
            final String visitorName
    ) {
        final IslandRecord island = this.islands.get(owner);
        if (island == null) {
            return Optional.empty();
        }

        final IslandRecord updated = island.withTrustedVisitor(visitor, visitorName);
        this.islands.put(owner, updated);
        this.setDirty();
        return Optional.of(updated);
    }

    public Optional<String> untrustVisitor(final UUID owner, final String visitorName) {
        final IslandRecord island = this.islands.get(owner);
        if (island == null) {
            return Optional.empty();
        }

        final UUID visitor = island.findTrustedVisitor(visitorName).orElse(null);
        if (visitor == null) {
            return Optional.empty();
        }

        final String storedName = island.trustedVisitors().get(visitor);
        this.islands.put(owner, island.withoutTrustedVisitor(visitor));
        this.setDirty();
        return Optional.of(storedName);
    }

    public boolean untrustVisitor(final UUID owner, final UUID visitor) {
        final IslandRecord island = this.islands.get(owner);
        if (island == null || !island.isTrustedVisitor(visitor)) {
            return false;
        }

        this.islands.put(owner, island.withoutTrustedVisitor(visitor));
        this.setDirty();
        return true;
    }

    public Optional<IslandRecord> invite(
            final UUID owner,
            final UUID target,
            final String targetName
    ) {
        final IslandRecord island = this.islands.get(owner);
        if (island == null) {
            return Optional.empty();
        }

        final IslandRecord updated = island.withInvite(target, targetName);
        this.islands.put(owner, updated);
        this.setDirty();
        return Optional.of(updated);
    }

    public Optional<IslandRecord> acceptInvitation(final UUID player, final String playerName) {
        for (final Map.Entry<UUID, IslandRecord> entry : this.islands.entrySet()) {
            final IslandRecord island = entry.getValue();
            if (island.hasInvite(player)) {
                final IslandRecord updated = island.acceptInvite(player, playerName);
                this.islands.put(entry.getKey(), updated);
                this.setDirty();
                return Optional.of(updated);
            }
        }
        return Optional.empty();
    }

    public Optional<IslandRecord> leaveIsland(final UUID player) {
        for (final Map.Entry<UUID, IslandRecord> entry : this.islands.entrySet()) {
            final IslandRecord island = entry.getValue();
            if (island.isMember(player)) {
                final IslandRecord updated = island.withoutMember(player);
                this.islands.put(entry.getKey(), updated);
                this.setDirty();
                return Optional.of(updated);
            }
        }
        return Optional.empty();
    }

    public record IslandRecord(
            UUID owner,
            String ownerName,
            ResourceKey<Level> dimension,
            BlockPos home,
            String type,
            Map<UUID, String> members,
            Map<UUID, String> invites,
            Map<UUID, String> trustedVisitors
    ) {
        private static final Codec<IslandRecord> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.STRING_CODEC.fieldOf("owner").forGetter(IslandRecord::owner),
                Codec.STRING.fieldOf("owner_name").forGetter(IslandRecord::ownerName),
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(IslandRecord::dimension),
                BlockPos.CODEC.fieldOf("home").forGetter(IslandRecord::home),
                Codec.STRING.optionalFieldOf("type", IslandTemplate.DEFAULT_ID).forGetter(IslandRecord::type),
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.STRING)
                        .optionalFieldOf("members", Map.of())
                        .forGetter(IslandRecord::members),
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.STRING)
                        .optionalFieldOf("invites", Map.of())
                        .forGetter(IslandRecord::invites),
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.STRING)
                        .optionalFieldOf("trusted_visitors", Map.of())
                        .forGetter(IslandRecord::trustedVisitors)
        ).apply(instance, IslandRecord::new));

        public IslandRecord {
            members = Map.copyOf(members);
            invites = Map.copyOf(invites);
            trustedVisitors = Map.copyOf(trustedVisitors);
        }

        public boolean isOwner(final UUID player) {
            return this.owner.equals(player);
        }

        public boolean isMember(final UUID player) {
            return this.members.containsKey(player);
        }

        public boolean includes(final UUID player) {
            return this.isOwner(player) || this.isMember(player);
        }

        public boolean includesName(final String playerName) {
            return this.ownerName.equalsIgnoreCase(playerName)
                    || this.members.values().stream().anyMatch(name -> name.equalsIgnoreCase(playerName));
        }

        public boolean hasInvite(final UUID player) {
            return this.invites.containsKey(player);
        }

        public int playerCount() {
            return this.members.size() + 1;
        }

        public String playerNames() {
            return Stream.concat(Stream.of(this.ownerName), this.members.values().stream())
                    .collect(Collectors.joining(", "));
        }

        public boolean isWithinHorizontalRange(final BlockPos pos, final int horizontalRadius) {
            final BlockPos center = this.home.below();
            return Math.abs(pos.getX() - center.getX()) <= horizontalRadius
                    && Math.abs(pos.getZ() - center.getZ()) <= horizontalRadius;
        }

        public boolean isTrustedVisitor(final UUID player) {
            return this.trustedVisitors.containsKey(player);
        }

        public boolean hasTrustedVisitors() {
            return !this.trustedVisitors.isEmpty();
        }

        public String trustedVisitorNames() {
            return this.trustedVisitors.values()
                    .stream()
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.joining(", "));
        }

        public Optional<UUID> findTrustedVisitor(final String visitorName) {
            return this.trustedVisitors.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().equalsIgnoreCase(visitorName))
                    .map(Map.Entry::getKey)
                    .findFirst();
        }

        private IslandRecord withTrustedVisitor(final UUID visitor, final String visitorName) {
            final Map<UUID, String> updatedVisitors = new HashMap<>(this.trustedVisitors);
            updatedVisitors.put(visitor, visitorName);
            return new IslandRecord(
                    this.owner,
                    this.ownerName,
                    this.dimension,
                    this.home,
                    this.type,
                    this.members,
                    this.invites,
                    updatedVisitors
            );
        }

        private IslandRecord withoutTrustedVisitor(final UUID visitor) {
            final Map<UUID, String> updatedVisitors = new HashMap<>(this.trustedVisitors);
            updatedVisitors.remove(visitor);
            return new IslandRecord(
                    this.owner,
                    this.ownerName,
                    this.dimension,
                    this.home,
                    this.type,
                    this.members,
                    this.invites,
                    updatedVisitors
            );
        }

        private IslandRecord withInvite(final UUID target, final String targetName) {
            final Map<UUID, String> updatedInvites = new HashMap<>(this.invites);
            updatedInvites.put(target, targetName);
            return new IslandRecord(
                    this.owner,
                    this.ownerName,
                    this.dimension,
                    this.home,
                    this.type,
                    this.members,
                    updatedInvites,
                    this.trustedVisitors
            );
        }

        private IslandRecord acceptInvite(final UUID player, final String playerName) {
            final Map<UUID, String> updatedInvites = new HashMap<>(this.invites);
            updatedInvites.remove(player);
            final Map<UUID, String> updatedMembers = new HashMap<>(this.members);
            updatedMembers.put(player, playerName);
            return new IslandRecord(
                    this.owner,
                    this.ownerName,
                    this.dimension,
                    this.home,
                    this.type,
                    updatedMembers,
                    updatedInvites,
                    this.trustedVisitors
            );
        }

        private IslandRecord withoutMember(final UUID player) {
            final Map<UUID, String> updatedMembers = new HashMap<>(this.members);
            updatedMembers.remove(player);
            return new IslandRecord(
                    this.owner,
                    this.ownerName,
                    this.dimension,
                    this.home,
                    this.type,
                    updatedMembers,
                    this.invites,
                    this.trustedVisitors
            );
        }
    }
}
