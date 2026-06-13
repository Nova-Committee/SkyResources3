package committee.nova.mods.skyresources3.island;

import committee.nova.mods.skyresources3.Skyresources3;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class VoidIslandWorld {
    public static final int ISLAND_Y = 192;
    public static final ResourceKey<Level> LEVEL = ResourceKey.create(
            Registries.DIMENSION,
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "void_island")
    );

    private static final int SPAWN_PLATFORM_RADIUS = 2;
    private static final BlockPos SPAWN_PLATFORM_CENTER = new BlockPos(0, ISLAND_Y, 0);

    public static Optional<ServerLevel> get(final MinecraftServer server) {
        return Optional.ofNullable(server.getLevel(LEVEL));
    }

    public static ServerLevel getOrOverworld(final MinecraftServer server) {
        return get(server).orElseGet(server::overworld);
    }

    public static BlockPos spawnHome() {
        return SPAWN_PLATFORM_CENTER.above();
    }

    public static void ensureSpawnPlatform(final ServerLevel level) {
        for (int x = -SPAWN_PLATFORM_RADIUS; x <= SPAWN_PLATFORM_RADIUS; x++) {
            for (int z = -SPAWN_PLATFORM_RADIUS; z <= SPAWN_PLATFORM_RADIUS; z++) {
                level.setBlock(
                        SPAWN_PLATFORM_CENTER.offset(x, 0, z),
                        Blocks.GRASS_BLOCK.defaultBlockState(),
                        Block.UPDATE_ALL
                );
            }
        }
        level.setBlock(SPAWN_PLATFORM_CENTER.above(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(SPAWN_PLATFORM_CENTER.above(2), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
    }

    private VoidIslandWorld() {
    }
}
