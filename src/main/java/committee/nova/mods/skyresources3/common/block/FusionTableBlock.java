package committee.nova.mods.skyresources3.common.block;

import committee.nova.mods.skyresources3.common.block.entity.FusionTableBlockEntity;
import committee.nova.mods.skyresources3.common.menu.FusionTableMenu;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class FusionTableBlock extends Block implements EntityBlock {
    public FusionTableBlock(final Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new FusionTableBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            final Level level,
            final BlockState state,
            final BlockEntityType<T> blockEntityType
    ) {
        if (level.isClientSide() || blockEntityType != ModBlockEntityTypes.FUSION_TABLE.get()) {
            return null;
        }
        return (tickLevel, tickPos, tickState, blockEntity) -> {
            if (tickLevel instanceof ServerLevel serverLevel
                    && blockEntity instanceof FusionTableBlockEntity fusionTable) {
                fusionTable.serverTick(serverLevel);
            }
        };
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
        return this.openMenu(level, pos, player, hitResult, stack);
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
    public BlockState playerWillDestroy(
            final Level level,
            final BlockPos pos,
            final BlockState state,
            final Player player
    ) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof FusionTableBlockEntity fusionTable) {
            fusionTable.dropContents();
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    private InteractionResult openMenu(
            final Level level,
            final BlockPos pos,
            final Player player,
            final BlockHitResult hitResult,
            final ItemStack stack
    ) {
        if (!level.mayInteract(player, pos)
                || !player.mayUseItemAt(pos, hitResult.getDirection(), stack)
                || !(level.getBlockEntity(pos) instanceof FusionTableBlockEntity fusionTable)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) ->
                                new FusionTableMenu(containerId, inventory, fusionTable),
                        Component.translatable("container.skyresources.fusion_table")
                ),
                buffer -> FusionTableMenu.writeClientSideData(buffer, pos, fusionTable)
        );
        return InteractionResult.SUCCESS_SERVER;
    }
}
