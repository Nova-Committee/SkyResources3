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
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class TeamSavedData extends SavedData {
    private static final String DATA_ID = Skyresources3.MODID + "_teams";
    private static final Codec<TeamSavedData> CODEC = Codec.unboundedMap(
                    UUIDUtil.STRING_CODEC,
                    TeamRecord.CODEC
            )
            .optionalFieldOf("teams", Map.of())
            .xmap(TeamSavedData::new, data -> Map.copyOf(data.teams))
            .codec();
    private static final SavedDataType<TeamSavedData> TYPE = new SavedDataType<>(
            DATA_ID,
            TeamSavedData::new,
            CODEC
    );

    private final Map<UUID, TeamRecord> teams;

    public TeamSavedData() {
        this(Map.of());
    }

    private TeamSavedData(final Map<UUID, TeamRecord> teams) {
        this.teams = new HashMap<>(teams);
    }

    public static TeamSavedData get(final ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public Optional<TeamRecord> getOwnedTeam(final UUID owner) {
        return Optional.ofNullable(this.teams.get(owner));
    }

    public Optional<TeamRecord> getTeamFor(final UUID player) {
        return this.teams.values()
                .stream()
                .filter(team -> team.includes(player))
                .findFirst();
    }

    public Optional<TeamRecord> getPendingInvitation(final UUID player) {
        return this.teams.values()
                .stream()
                .filter(team -> team.hasInvite(player))
                .findFirst();
    }

    public TeamRecord createTeam(final UUID owner, final String ownerName) {
        final TeamRecord existing = this.teams.get(owner);
        if (existing != null) {
            return existing;
        }

        final TeamRecord team = new TeamRecord(owner, ownerName, Map.of(), Map.of());
        this.teams.put(owner, team);
        this.setDirty();
        return team;
    }

    public TeamRecord invite(
            final UUID owner,
            final String ownerName,
            final UUID target,
            final String targetName
    ) {
        final TeamRecord team = this.createTeam(owner, ownerName);
        final TeamRecord updated = team.withInvite(target, targetName);
        this.teams.put(owner, updated);
        this.setDirty();
        return updated;
    }

    public Optional<TeamRecord> acceptInvitation(final UUID player, final String playerName) {
        for (final Map.Entry<UUID, TeamRecord> entry : this.teams.entrySet()) {
            final TeamRecord team = entry.getValue();
            if (team.hasInvite(player)) {
                final TeamRecord updated = team.acceptInvite(player, playerName);
                this.teams.put(entry.getKey(), updated);
                this.setDirty();
                return Optional.of(updated);
            }
        }
        return Optional.empty();
    }

    public boolean leave(final UUID player) {
        for (final Map.Entry<UUID, TeamRecord> entry : this.teams.entrySet()) {
            final TeamRecord team = entry.getValue();
            if (team.members().containsKey(player)) {
                this.teams.put(entry.getKey(), team.withoutMember(player));
                this.setDirty();
                return true;
            }
        }
        return false;
    }

    public boolean disband(final UUID owner) {
        final boolean removed = this.teams.remove(owner) != null;
        if (removed) {
            this.setDirty();
        }
        return removed;
    }

    public record TeamRecord(
            UUID owner,
            String ownerName,
            Map<UUID, String> members,
            Map<UUID, String> invites
    ) {
        private static final Codec<TeamRecord> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.STRING_CODEC.fieldOf("owner").forGetter(TeamRecord::owner),
                Codec.STRING.fieldOf("owner_name").forGetter(TeamRecord::ownerName),
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.STRING)
                        .optionalFieldOf("members", Map.of())
                        .forGetter(TeamRecord::members),
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.STRING)
                        .optionalFieldOf("invites", Map.of())
                        .forGetter(TeamRecord::invites)
        ).apply(instance, TeamRecord::new));

        public TeamRecord {
            members = Map.copyOf(members);
            invites = Map.copyOf(invites);
        }

        public boolean isOwner(final UUID player) {
            return this.owner.equals(player);
        }

        public boolean includes(final UUID player) {
            return this.isOwner(player) || this.members.containsKey(player);
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

        private TeamRecord withInvite(final UUID target, final String targetName) {
            final Map<UUID, String> updatedInvites = new HashMap<>(this.invites);
            updatedInvites.put(target, targetName);
            return new TeamRecord(this.owner, this.ownerName, this.members, updatedInvites);
        }

        private TeamRecord acceptInvite(final UUID player, final String playerName) {
            final Map<UUID, String> updatedInvites = new HashMap<>(this.invites);
            updatedInvites.remove(player);
            final Map<UUID, String> updatedMembers = new HashMap<>(this.members);
            updatedMembers.put(player, playerName);
            return new TeamRecord(this.owner, this.ownerName, updatedMembers, updatedInvites);
        }

        private TeamRecord withoutMember(final UUID player) {
            final Map<UUID, String> updatedMembers = new HashMap<>(this.members);
            updatedMembers.remove(player);
            return new TeamRecord(this.owner, this.ownerName, updatedMembers, this.invites);
        }
    }
}
