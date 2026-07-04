package committee.nova.mods.skyresources3.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

final class BonemealGrowth {
    private static final int MAX_GROWTH_ATTEMPTS = 100;

    static boolean isValidTarget(final Level level, final BlockPos target) {
        final BlockState blockState = level.getBlockState(target);
        return blockState.getBlock() instanceof BonemealableBlock bonemealableBlock
                && bonemealableBlock.isValidBonemealTarget(level, target, blockState, level.isClientSide());
    }

    static void growUntilStable(final ServerLevel level, final BlockPos target) {
        for (int tries = 0; tries < MAX_GROWTH_ATTEMPTS; tries++) {
            final BlockState blockState = level.getBlockState(target);
            if (!(blockState.getBlock() instanceof BonemealableBlock bonemealableBlock)
                    || !bonemealableBlock.isValidBonemealTarget(level, target, blockState, false)) {
                return;
            }
            bonemealableBlock.performBonemeal(level, level.random, target, blockState);
        }
    }

    private BonemealGrowth() {
    }
}
