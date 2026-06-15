package committee.nova.mods.skyresources3.core.island;

import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;

public enum IslandTemplate {
    GRASS("grass") {
        @Override
        public void build(final ServerLevel level, final BlockPos center) {
            fillPlatform(level, center, Blocks.GRASS_BLOCK.defaultBlockState());
            set(level, center.offset(2, 1, 2), Blocks.OAK_SAPLING.defaultBlockState());
        }
    },
    SAND("sand") {
        @Override
        public void build(final ServerLevel level, final BlockPos center) {
            fillPlatform(level, center, Blocks.SAND.defaultBlockState());
            set(level, center.offset(-1, 1, 1), Blocks.CACTUS.defaultBlockState());
            set(level, center.offset(-1, 2, 1), Blocks.CACTUS.defaultBlockState());
            set(level, center.offset(-1, 3, 1), Blocks.CACTUS.defaultBlockState());
        }
    },
    SNOW("snow") {
        @Override
        public void build(final ServerLevel level, final BlockPos center) {
            fillPlatform(level, center, Blocks.SNOW_BLOCK.defaultBlockState());
            set(level, center.offset(-1, 0, -1), Blocks.PACKED_ICE.defaultBlockState());
            set(level, center.offset(1, 0, 1), Blocks.ICE.defaultBlockState());
            set(level, center.offset(2, 1, 2), Blocks.PUMPKIN.defaultBlockState());
        }
    },
    WOOD("wood") {
        @Override
        public void build(final ServerLevel level, final BlockPos center) {
            fillPlatform(level, center, Blocks.OAK_PLANKS.defaultBlockState());
            set(level, center.offset(-2, 1, -2), Blocks.OAK_LOG.defaultBlockState());
            set(level, center.offset(-2, 2, -2), Blocks.OAK_LOG.defaultBlockState());
            set(level, center.offset(2, 1, 2), Blocks.OAK_SAPLING.defaultBlockState());
        }
    },
    GOG("gog") {
        @Override
        public void build(final ServerLevel level, final BlockPos center) {
            fillPlatform(level, center, Blocks.GRASS_BLOCK.defaultBlockState());
            set(level, center.offset(-2, 1, -2), Blocks.DANDELION.defaultBlockState());
            set(level, center.offset(2, 1, 2), Blocks.POPPY.defaultBlockState());
            set(level, center.offset(0, 1, 2), Blocks.OAK_SAPLING.defaultBlockState());
        }
    },
    MAGMA("magma") {
        @Override
        public void build(final ServerLevel level, final BlockPos center) {
            final BlockState petrifiedWood = ModBlocks.PETRIFIED_WOOD.get().defaultBlockState();
            final BlockState magmafiedStone = ModBlocks.MAGMAFIED_STONE.get().defaultBlockState();
            final BlockState crystalFluid = ModBlocks.CRYSTAL_FLUID.get().defaultBlockState();
            final BlockState soulSand = Blocks.SOUL_SAND.defaultBlockState();
            final BlockState netherWart = Blocks.NETHER_WART.defaultBlockState().setValue(NetherWartBlock.AGE, 3);

            set(level, center.east().south(), soulSand);
            set(level, center.south(), soulSand);
            set(level, center.west().south(), crystalFluid);
            set(level, center, soulSand);
            set(level, center.west(), soulSand);
            set(level, center.east().north(), soulSand);
            set(level, center.north(), soulSand);
            set(level, center.east(), magmafiedStone);
            set(level, center.west().north(), magmafiedStone);

            set(level, center.east().south().above(), petrifiedWood);
            set(level, center.east().south().above(2), petrifiedWood);
            set(level, center.east().south(2).above(2), petrifiedWood);
            set(level, center.east().south().above(3), petrifiedWood);
            set(level, center.south().above(4), petrifiedWood);
            set(level, center.west().above(), petrifiedWood);
            set(level, center.west(2), petrifiedWood);
            set(level, center.west(2).below(), petrifiedWood);
            set(level, center.west(2).below(2), petrifiedWood);
            set(level, center.west().north().above(), petrifiedWood);
            set(level, center.east().north().above(), netherWart);
        }
    };

    public static final String DEFAULT_ID = "grass";
    private static final int PLATFORM_RADIUS = 2;

    private final String id;

    IslandTemplate(final String id) {
        this.id = id;
    }

    public static IslandTemplate defaultTemplate() {
        return GRASS;
    }

    public static Optional<IslandTemplate> byId(final String rawId) {
        final String id = rawId.toLowerCase(Locale.ROOT);
        if ("default".equals(id)) {
            return Optional.of(defaultTemplate());
        }
        if ("garden_of_glass".equals(id) || "gardenofglass".equals(id)) {
            return Optional.of(GOG);
        }
        return Arrays.stream(values())
                .filter(template -> template.id.equals(id))
                .findFirst();
    }

    public static List<String> ids() {
        return Arrays.stream(values())
                .map(IslandTemplate::id)
                .toList();
    }

    public String id() {
        return this.id;
    }

    public BlockPos home(final BlockPos center) {
        return center.above();
    }

    public abstract void build(ServerLevel level, BlockPos center);

    protected static void fillPlatform(
            final ServerLevel level,
            final BlockPos center,
            final BlockState state
    ) {
        for (int x = -PLATFORM_RADIUS; x <= PLATFORM_RADIUS; x++) {
            for (int z = -PLATFORM_RADIUS; z <= PLATFORM_RADIUS; z++) {
                set(level, center.offset(x, 0, z), state);
            }
        }
    }

    protected static void set(final ServerLevel level, final BlockPos pos, final BlockState state) {
        level.setBlock(pos, state, Block.UPDATE_ALL);
    }
}
