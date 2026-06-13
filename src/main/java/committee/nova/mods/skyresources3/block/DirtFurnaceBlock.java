package committee.nova.mods.skyresources3.block;

import com.mojang.serialization.MapCodec;
import committee.nova.mods.skyresources3.block.entity.DirtFurnaceBlockEntity;
import committee.nova.mods.skyresources3.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class DirtFurnaceBlock extends AbstractFurnaceBlock {
    public static final MapCodec<DirtFurnaceBlock> CODEC = simpleCodec(DirtFurnaceBlock::new);

    public DirtFurnaceBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<DirtFurnaceBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new DirtFurnaceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            final Level level,
            final BlockState state,
            final BlockEntityType<T> blockEntityType
    ) {
        if (level.isClientSide() || blockEntityType != ModBlockEntityTypes.DIRT_FURNACE.get()) {
            return null;
        }
        return (tickLevel, tickPos, tickState, blockEntity) -> {
            if (tickLevel instanceof ServerLevel serverLevel
                    && blockEntity instanceof DirtFurnaceBlockEntity dirtFurnace) {
                dirtFurnace.serverTick(serverLevel, tickPos, tickState);
            }
        };
    }

    @Override
    protected void openContainer(final Level level, final BlockPos pos, final Player player) {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DirtFurnaceBlockEntity dirtFurnace) {
            player.openMenu((MenuProvider) dirtFurnace);
            player.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
    }

    @Override
    public void animateTick(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final RandomSource random
    ) {
        if (!state.getValue(LIT)) {
            return;
        }

        final double x = pos.getX() + 0.5D;
        final double y = pos.getY();
        final double z = pos.getZ() + 0.5D;
        if (random.nextDouble() < 0.1D) {
            level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }

        final Direction direction = state.getValue(FACING);
        final Direction.Axis axis = direction.getAxis();
        final double offset = 0.52D;
        final double sideOffset = random.nextDouble() * 0.6D - 0.3D;
        final double particleX = axis == Direction.Axis.X ? direction.getStepX() * offset : sideOffset;
        final double particleY = random.nextDouble() * 6.0D / 16.0D;
        final double particleZ = axis == Direction.Axis.Z ? direction.getStepZ() * offset : sideOffset;
        level.addParticle(ParticleTypes.SMOKE, x + particleX, y + particleY, z + particleZ, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.FLAME, x + particleX, y + particleY, z + particleZ, 0.0D, 0.0D, 0.0D);
    }
}
