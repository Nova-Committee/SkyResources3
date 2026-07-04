package committee.nova.mods.skyresources3.common.block;

import committee.nova.mods.skyresources3.common.block.entity.LifeInjectorBlockEntity;
import committee.nova.mods.skyresources3.common.item.HealthGemItem;
import committee.nova.mods.skyresources3.common.menu.LifeInjectorMenu;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
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
        if (stack.isEmpty()) {
            return this.openMenu(pos, player, lifeInjector);
        }
        if (!lifeInjector.canInsertGem(stack)) {
            return InteractionResult.PASS;
        }
        lifeInjector.insertGem(stack);
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void playerWillDestroy(
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
        super.playerWillDestroy(level, pos, state, player);
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
        return InteractionResult.SUCCESS;
    }

    private InteractionResult openMenu(
            final BlockPos pos,
            final Player player,
            final LifeInjectorBlockEntity lifeInjector
    ) {
        net.minecraftforge.network.NetworkHooks.openScreen((net.minecraft.server.level.ServerPlayer) player,
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
        return InteractionResult.SUCCESS;
    }
}
