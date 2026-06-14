package committee.nova.mods.skyresources3.block;

import committee.nova.mods.skyresources3.block.entity.LifeInjectorBlockEntity;
import committee.nova.mods.skyresources3.item.HealthGemItem;
import committee.nova.mods.skyresources3.menu.LifeInjectorMenu;
import committee.nova.mods.skyresources3.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
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

public final class LifeInjectorBlock extends Block implements EntityBlock {
    public LifeInjectorBlock(final Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new LifeInjectorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            final Level level,
            final BlockState state,
            final BlockEntityType<T> blockEntityType
    ) {
        if (level.isClientSide() || blockEntityType != ModBlockEntityTypes.LIFE_INJECTOR.get()) {
            return null;
        }
        return (tickLevel, tickPos, tickState, blockEntity) -> {
            if (tickLevel instanceof ServerLevel serverLevel
                    && blockEntity instanceof LifeInjectorBlockEntity lifeInjector) {
                lifeInjector.serverTick(serverLevel);
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
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, hitResult.getDirection(), stack)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return player.isShiftKeyDown() || stack.getItem() instanceof HealthGemItem
                    ? InteractionResult.SUCCESS
                    : InteractionResult.PASS;
        }
        if (!(level.getBlockEntity(pos) instanceof LifeInjectorBlockEntity lifeInjector)) {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown() && lifeInjector.hasGem()) {
            return returnGemToPlayer(level, pos, player, lifeInjector);
        }
        if (!lifeInjector.canInsertGem(stack)) {
            return InteractionResult.PASS;
        }
        lifeInjector.insertGem(stack);
        if (!player.isCreative()) {
            stack.shrink(1);
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
        if (!(level.getBlockEntity(pos) instanceof LifeInjectorBlockEntity lifeInjector)) {
            return InteractionResult.PASS;
        }
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, hitResult.getDirection(), ItemStack.EMPTY)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown() && lifeInjector.hasGem()) {
            return returnGemToPlayer(level, pos, player, lifeInjector);
        }
        return this.openMenu(pos, player, lifeInjector);
    }

    @Override
    public BlockState playerWillDestroy(
            final Level level,
            final BlockPos pos,
            final BlockState state,
            final Player player
    ) {
        if (!level.isClientSide()
                && level.getBlockEntity(pos) instanceof LifeInjectorBlockEntity lifeInjector
                && lifeInjector.hasGem()) {
            Containers.dropContents(level, pos, new SimpleContainer(lifeInjector.removeGem()));
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    private static InteractionResult returnGemToPlayer(
            final Level level,
            final BlockPos pos,
            final Player player,
            final LifeInjectorBlockEntity lifeInjector
    ) {
        final ItemStack removed = lifeInjector.removeGem();
        if (!player.addItem(removed)) {
            Block.popResource(level, pos.above(), removed);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    private InteractionResult openMenu(
            final BlockPos pos,
            final Player player,
            final LifeInjectorBlockEntity lifeInjector
    ) {
        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) -> new LifeInjectorMenu(
                                containerId,
                                inventory,
                                lifeInjector
                        ),
                        Component.translatable("container.skyresources.life_injector")
                ),
                buffer -> LifeInjectorMenu.writeClientSideData(buffer, pos)
        );
        return InteractionResult.SUCCESS_SERVER;
    }
}
