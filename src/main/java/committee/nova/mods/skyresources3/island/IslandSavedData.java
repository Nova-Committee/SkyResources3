package committee.nova.mods.skyresources3.island;

import committee.nova.mods.skyresources3.Skyresources3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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

    public int islandCount() {
        return this.islands.size();
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
        final IslandRecord island = new IslandRecord(owner, ownerName, dimension, home, type);
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
                type
        );
        this.islands.put(owner, updated);
        this.setDirty();
        return Optional.of(updated);
    }

    public record IslandRecord(
            UUID owner,
            String ownerName,
            ResourceKey<Level> dimension,
            BlockPos home,
            String type
    ) {
        private static final Codec<IslandRecord> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.STRING_CODEC.fieldOf("owner").forGetter(IslandRecord::owner),
                Codec.STRING.fieldOf("owner_name").forGetter(IslandRecord::ownerName),
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(IslandRecord::dimension),
                BlockPos.CODEC.fieldOf("home").forGetter(IslandRecord::home),
                Codec.STRING.optionalFieldOf("type", IslandTemplate.DEFAULT_ID).forGetter(IslandRecord::type)
        ).apply(instance, IslandRecord::new));

        public boolean isWithinHorizontalRange(final BlockPos pos, final int horizontalRadius) {
            final BlockPos center = this.home.below();
            return Math.abs(pos.getX() - center.getX()) <= horizontalRadius
                    && Math.abs(pos.getZ() - center.getZ()) <= horizontalRadius;
        }
    }
}
