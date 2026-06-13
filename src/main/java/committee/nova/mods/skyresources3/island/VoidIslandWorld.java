package committee.nova.mods.skyresources3.island;

import committee.nova.mods.skyresources3.Config;
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
import net.minecraft.world.level.block.state.BlockState;

public final class VoidIslandWorld {
    public static final int ISLAND_Y = 192;
    public static final ResourceKey<Level> LEVEL = ResourceKey.create(
            Registries.DIMENSION,
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "void_island")
    );

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

    public static BlockPos spawnPlatformCenter() {
        return SPAWN_PLATFORM_CENTER;
    }

    public static void ensureSpawnPlatform(final ServerLevel level) {
        final int radius = Math.max(0, Config.voidIslandSpawnPlatformRadius);
        final BlockState platformBlock = Config.voidIslandSpawnPlatformBlock.defaultBlockState();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                level.setBlock(
                        SPAWN_PLATFORM_CENTER.offset(x, 0, z),
                        platformBlock,
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
