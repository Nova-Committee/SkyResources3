package committee.nova.mods.skyresources3.common.block;

import committee.nova.mods.skyresources3.common.block.entity.CrucibleInserterBlockEntity;
import committee.nova.mods.skyresources3.common.menu.CrucibleInserterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CrucibleInserterBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.box(4.0D, 0.0D, 4.0D, 12.0D, 10.0D, 12.0D),
            Block.box(0.0D, 0.0D, 0.0D, 1.0D, 10.0D, 1.0D),
            Block.box(15.0D, 0.0D, 0.0D, 16.0D, 10.0D, 1.0D),
            Block.box(0.0D, 0.0D, 15.0D, 1.0D, 10.0D, 16.0D),
            Block.box(15.0D, 0.0D, 15.0D, 16.0D, 10.0D, 16.0D)
    );

    public CrucibleInserterBlock(final Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new CrucibleInserterBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            final ItemStack stack,
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Player player,
            final InteractionHand hand,
            final BlockHitResult hitResult
    ) {
        return BlockInteractionResults.item(this.openMenu(level, pos, player, hitResult, stack));
    }

    @Override
    protected InteractionResult useWithoutItem(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Player player,
            final BlockHitResult hitResult
    ) {
        return this.openMenu(level, pos, player, hitResult, ItemStack.EMPTY);
    }

    @Override
    protected VoxelShape getShape(
            final BlockState state,
            final BlockGetter level,
            final BlockPos pos,
            final CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(
            final BlockState state,
            final BlockGetter level,
            final BlockPos pos,
            final CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(final BlockState state) {
        return RenderShape.MODEL;
    }

    private InteractionResult openMenu(
            final Level level,
            final BlockPos pos,
            final Player player,
            final BlockHitResult hitResult,
            final ItemStack stack
    ) {
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, hitResult.getDirection(), stack)) {
            return InteractionResult.PASS;
        }
        if (!(level.getBlockEntity(pos) instanceof CrucibleInserterBlockEntity inserter)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) ->
                                new CrucibleInserterMenu(containerId, inventory, inserter),
                        Component.translatable("container.skyresources.crucible_inserter")
                ),
                buffer -> CrucibleInserterMenu.writeClientSideData(buffer, pos)
        );
        return InteractionResult.SUCCESS;
    }
}
