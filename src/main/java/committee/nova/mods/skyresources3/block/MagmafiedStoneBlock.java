package committee.nova.mods.skyresources3.block;

import committee.nova.mods.skyresources3.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;

public final class MagmafiedStoneBlock extends Block {
    private static final int CHECK_INTERVAL_TICKS = 10;
    private static final int COBBLESTONE_CHANCE_BOUND = 800;
    private static final int COBBLESTONE_CHANCE = 30;
    private static final int COBBLESTONE_LIFESPAN = 600;
    private static final int SMOKE_PARTICLES = 8;

    public MagmafiedStoneBlock(final Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(
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
    protected void neighborChanged(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Block neighborBlock,
            final Orientation orientation,
            final boolean movedByPiston
    ) {
        if (!level.isClientSide()) {
            this.scheduleNextTick(level, pos);
        }
    }

    @Override
    protected void tick(
            final BlockState state,
            final ServerLevel level,
            final BlockPos pos,
            final RandomSource random
    ) {
        boolean success = false;
        for (final Direction direction : Direction.values()) {
            final BlockPos fluidPos = pos.relative(direction);
            if (level.getBlockState(fluidPos).is(ModBlocks.CRYSTAL_FLUID.get())
                    && random.nextInt(COBBLESTONE_CHANCE_BOUND) < COBBLESTONE_CHANCE) {
                this.spawnCobblestone(level, fluidPos);
                success = true;
            }
        }
        if (success) {
            this.playExtinguishEffects(level, pos, random);
        }
        this.scheduleNextTick(level, pos);
    }

    private void spawnCobblestone(final ServerLevel level, final BlockPos pos) {
        final ItemEntity entity = new ItemEntity(
                level,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                new ItemStack(Items.COBBLESTONE)
        );
        entity.lifespan = COBBLESTONE_LIFESPAN;
        entity.setDeltaMovement(0.0D, 0.0D, 0.0D);
        level.addFreshEntity(entity);
    }

    private void playExtinguishEffects(final ServerLevel level, final BlockPos pos, final RandomSource random) {
        level.playSound(
                null,
                pos,
                SoundEvents.LAVA_EXTINGUISH,
                SoundSource.BLOCKS,
                0.5F,
                2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F
        );
        for (int i = 0; i < SMOKE_PARTICLES; i++) {
            level.sendParticles(
                    ParticleTypes.LARGE_SMOKE,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + 1.2D,
                    pos.getZ() + random.nextDouble(),
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );
        }
    }

    private void scheduleNextTick(final Level level, final BlockPos pos) {
        level.scheduleTick(pos, this, CHECK_INTERVAL_TICKS);
    }
}
