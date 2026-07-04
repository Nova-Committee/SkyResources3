package committee.nova.mods.skyresources3.common.block;

import committee.nova.mods.skyresources3.util.HeatSources;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class BlazePowderBlock extends Block {
    private static final int CHECK_INTERVAL_TICKS = 10;
    private static final int CHANCE_BOUND = 1000;

    public BlazePowderBlock(final Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final BlockState oldState,
            final boolean movedByPiston
    ) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && !oldState.is(this)) {
            this.scheduleNextTick(level, pos);
        }
    }

    @Override
    public void tick(
            final BlockState state,
            final ServerLevel level,
            final BlockPos pos,
            final RandomSource random
    ) {
        final int heat = HeatSources.getHeatSourceValue(level, pos.below());
        if (heat > 0 && random.nextInt(CHANCE_BOUND) <= heat) {
            level.setBlock(pos, Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
            return;
        }
        this.scheduleNextTick(level, pos);
    }

    private void scheduleNextTick(final Level level, final BlockPos pos) {
        level.scheduleTick(pos, this, CHECK_INTERVAL_TICKS);
    }
}
