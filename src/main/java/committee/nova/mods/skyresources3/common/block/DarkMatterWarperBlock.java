package committee.nova.mods.skyresources3.common.block;

import committee.nova.mods.skyresources3.common.block.entity.DarkMatterWarperBlockEntity;
import committee.nova.mods.skyresources3.common.menu.DarkMatterWarperMenu;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class DarkMatterWarperBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Block.box(2.0D, 2.0D, 2.0D, 14.0D, 14.0D, 14.0D);

    public DarkMatterWarperBlock(final Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new DarkMatterWarperBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            final Level level,
            final BlockState state,
            final BlockEntityType<T> blockEntityType
    ) {
        if (level.isClientSide() || blockEntityType != ModBlockEntityTypes.DARK_MATTER_WARPER.get()) {
            return null;
        }
        return (tickLevel, tickPos, tickState, blockEntity) -> {
            if (tickLevel instanceof ServerLevel serverLevel && blockEntity instanceof DarkMatterWarperBlockEntity warper) {
                warper.serverTick(serverLevel);
            }
        };
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
        if (!(level.getBlockEntity(pos) instanceof DarkMatterWarperBlockEntity warper)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) -> new DarkMatterWarperMenu(containerId, inventory, warper),
                        Component.translatable("container.skyresources.dark_matter_warper")
                ),
                buffer -> DarkMatterWarperMenu.writeClientSideData(buffer, pos, warper)
        );
        return InteractionResult.SUCCESS;
    }
}
