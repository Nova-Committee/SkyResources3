package committee.nova.mods.skyresources3.block;

import committee.nova.mods.skyresources3.block.entity.LifeInfuserBlockEntity;
import committee.nova.mods.skyresources3.item.HealthGemItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;

public final class LifeInfuserBlock extends Block implements EntityBlock {
    public LifeInfuserBlock(final Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new LifeInfuserBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useItemOn(
            final ItemStack stack,
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Player player,
            final InteractionHand hand,
            final BlockHitResult hitResult
    ) {
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, hitResult.getDirection(), stack)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return !stack.isEmpty() ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        if (!(level.getBlockEntity(pos) instanceof LifeInfuserBlockEntity lifeInfuser)) {
            return InteractionResult.PASS;
        }

        if (stack.getItem() instanceof HealthGemItem) {
            if (!lifeInfuser.canInsertGem(stack)) {
                return InteractionResult.PASS;
            }
            lifeInfuser.insertGem(stack);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS_SERVER;
        }

        if (!lifeInfuser.canInsertInput(stack)) {
            return InteractionResult.PASS;
        }
        lifeInfuser.insertInput(stack);
        if (!player.isCreative()) {
            stack.setCount(0);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected InteractionResult useWithoutItem(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Player player,
            final BlockHitResult hitResult
    ) {
        if (!(level.getBlockEntity(pos) instanceof LifeInfuserBlockEntity lifeInfuser)) {
            return InteractionResult.PASS;
        }
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, hitResult.getDirection(), ItemStack.EMPTY)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        final ItemStack removed = lifeInfuser.hasInput() ? lifeInfuser.removeInput() : lifeInfuser.removeGem();
        if (removed.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!player.addItem(removed)) {
            Block.popResource(level, pos.above(), removed);
        }
        return InteractionResult.SUCCESS_SERVER;
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
        if (!level.isClientSide()
                && level instanceof ServerLevel serverLevel
                && level.getBlockEntity(pos) instanceof LifeInfuserBlockEntity lifeInfuser) {
            lifeInfuser.updatePowered(serverLevel, level.hasNeighborSignal(pos));
        }
    }

    @Override
    public BlockState playerWillDestroy(
            final Level level,
            final BlockPos pos,
            final BlockState state,
            final Player player
    ) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel
                && level.getBlockEntity(pos) instanceof LifeInfuserBlockEntity lifeInfuser) {
            lifeInfuser.dropContents(serverLevel);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
