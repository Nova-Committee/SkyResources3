package committee.nova.mods.skyresources3.common.block;

import committee.nova.mods.skyresources3.common.block.entity.LifeInfuserBlockEntity;
import committee.nova.mods.skyresources3.common.item.HealthGemItem;
import committee.nova.mods.skyresources3.common.menu.LifeInfuserMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
    public InteractionResult use(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Player player,
            final InteractionHand hand,
            final BlockHitResult hitResult
    ) {
        final ItemStack stack = player.getItemInHand(hand);
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, hitResult.getDirection(), stack)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return !stack.isEmpty() || player.isShiftKeyDown() ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        if (!(level.getBlockEntity(pos) instanceof LifeInfuserBlockEntity lifeInfuser)) {
            return InteractionResult.PASS;
        }

        if (stack.isEmpty()) {
            if (player.isShiftKeyDown()) {
                final ItemStack removed = lifeInfuser.hasInput() ? lifeInfuser.removeInput() : lifeInfuser.removeGem();
                if (!removed.isEmpty()) {
                    if (!player.addItem(removed)) {
                        Block.popResource(level, pos.above(), removed);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            return this.openMenu(pos, player, lifeInfuser);
        }

        if (stack.getItem() instanceof HealthGemItem) {
            if (!lifeInfuser.canInsertGem(stack)) {
                return InteractionResult.PASS;
            }
            lifeInfuser.insertGem(stack);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        if (!lifeInfuser.canInsertInput(stack)) {
            return InteractionResult.PASS;
        }
        lifeInfuser.insertInput(stack);
        if (!player.isCreative()) {
            stack.setCount(0);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Block neighborBlock,
            final BlockPos neighborPos,
            final boolean movedByPiston
    ) {
        if (!level.isClientSide()
                && level instanceof ServerLevel serverLevel
                && level.getBlockEntity(pos) instanceof LifeInfuserBlockEntity lifeInfuser) {
            lifeInfuser.updatePowered(serverLevel, level.hasNeighborSignal(pos));
        }
    }

    @Override
    public void playerWillDestroy(
            final Level level,
            final BlockPos pos,
            final BlockState state,
            final Player player
    ) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel
                && level.getBlockEntity(pos) instanceof LifeInfuserBlockEntity lifeInfuser) {
            lifeInfuser.dropContents(serverLevel);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    private InteractionResult openMenu(
            final BlockPos pos,
            final Player player,
            final LifeInfuserBlockEntity lifeInfuser
    ) {
        net.minecraftforge.network.NetworkHooks.openScreen((net.minecraft.server.level.ServerPlayer) player,
                new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) -> new LifeInfuserMenu(
                                containerId,
                                inventory,
                                lifeInfuser
                        ),
                        Component.translatable("container.skyresources.life_infuser")
                ),
                buffer -> LifeInfuserMenu.writeClientSideData(buffer, pos)
        );
        return InteractionResult.SUCCESS;
    }
}
