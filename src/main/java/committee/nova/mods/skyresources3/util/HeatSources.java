package committee.nova.mods.skyresources3.util;

import committee.nova.mods.skyresources3.common.block.entity.MachineCasingBlockEntity;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class HeatSources {
    private static final int SOURCE_LAVA_VALUE = 6;
    private static final int FLOWING_LAVA_VALUE = 4;
    private static final List<BlockHeatSource> BLOCK_HEAT_SOURCES = List.of(
            new BlockHeatSource(Blocks.FIRE, 8),
            new BlockHeatSource(Blocks.SOUL_FIRE, 8),
            new BlockHeatSource(Blocks.TORCH, 1),
            new BlockHeatSource(Blocks.WALL_TORCH, 1),
            new BlockHeatSource(Blocks.SOUL_TORCH, 1),
            new BlockHeatSource(Blocks.SOUL_WALL_TORCH, 1),
            new BlockHeatSource(Blocks.OBSIDIAN, 3),
            new BlockHeatSource(Blocks.MAGMA_BLOCK, 9)
    );
    private static final Map<Block, Integer> HEAT_VALUES = BLOCK_HEAT_SOURCES.stream()
            .collect(Collectors.toUnmodifiableMap(BlockHeatSource::block, BlockHeatSource::heat));

    public static int getHeatSourceValue(final Level level, final BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof MachineCasingBlockEntity casing) {
            return casing.heatSourceValue();
        }
        final BlockState state = level.getBlockState(pos);
        if (state.getFluidState().is(FluidTags.LAVA)) {
            return state.getFluidState().isSource() ? SOURCE_LAVA_VALUE : FLOWING_LAVA_VALUE;
        }
        return HEAT_VALUES.getOrDefault(state.getBlock(), 0);
    }

    public static int sourceLavaValue() {
        return SOURCE_LAVA_VALUE;
    }

    public static int flowingLavaValue() {
        return FLOWING_LAVA_VALUE;
    }

    public static List<BlockHeatSource> blockHeatSources() {
        return BLOCK_HEAT_SOURCES;
    }

    public record BlockHeatSource(Block block, int heat) {
    }

    private HeatSources() {
    }
}
