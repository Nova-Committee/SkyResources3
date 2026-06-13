package committee.nova.mods.skyresources3.util;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class HeatSources {
    private static final int SOURCE_LAVA_VALUE = 6;
    private static final int FLOWING_LAVA_VALUE = 4;
    private static final Map<Block, Integer> HEAT_VALUES = Map.ofEntries(
            Map.entry(Blocks.FIRE, 8),
            Map.entry(Blocks.SOUL_FIRE, 8),
            Map.entry(Blocks.TORCH, 1),
            Map.entry(Blocks.WALL_TORCH, 1),
            Map.entry(Blocks.SOUL_TORCH, 1),
            Map.entry(Blocks.SOUL_WALL_TORCH, 1),
            Map.entry(Blocks.OBSIDIAN, 3),
            Map.entry(Blocks.MAGMA_BLOCK, 9)
    );

    public static int getHeatSourceValue(final Level level, final BlockPos pos) {
        final BlockState state = level.getBlockState(pos);
        if (state.getFluidState().is(FluidTags.LAVA)) {
            return state.getFluidState().isSource() ? SOURCE_LAVA_VALUE : FLOWING_LAVA_VALUE;
        }
        return HEAT_VALUES.getOrDefault(state.getBlock(), 0);
    }

    private HeatSources() {
    }
}
