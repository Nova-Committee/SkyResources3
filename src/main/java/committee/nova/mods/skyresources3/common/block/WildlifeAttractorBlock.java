package committee.nova.mods.skyresources3.common.block;

import committee.nova.mods.skyresources3.common.block.entity.WildlifeAttractorBlockEntity;
import committee.nova.mods.skyresources3.common.menu.WildlifeAttractorMenu;
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
import committee.nova.mods.skyresources3.common.compat.transfer.fluid.FluidUtil;
import org.jetbrains.annotations.Nullable;

public final class WildlifeAttractorBlock extends Block implements EntityBlock {
    public WildlifeAttractorBlock(final Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new WildlifeAttractorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            final Level level,
            final BlockState state,
            final BlockEntityType<T> blockEntityType
    ) {
        if (level.isClientSide() || blockEntityType != ModBlockEntityTypes.WILDLIFE_ATTRACTOR.get()) {
            return null;
        }
        return (tickLevel, tickPos, tickState, blockEntity) -> {
            if (tickLevel instanceof ServerLevel serverLevel && blockEntity instanceof WildlifeAttractorBlockEntity attractor) {
                attractor.serverTick(serverLevel);
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
        if (!(level.getBlockEntity(pos) instanceof WildlifeAttractorBlockEntity attractor)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!stack.isEmpty() && FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection())) {
            return InteractionResult.SUCCESS;
        }
        return this.openMenu(pos, player, attractor);
    }

    private InteractionResult openMenu(
            final BlockPos pos,
            final Player player,
            final WildlifeAttractorBlockEntity attractor
    ) {
        net.minecraftforge.network.NetworkHooks.openScreen((net.minecraft.server.level.ServerPlayer) player,
                new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) -> new WildlifeAttractorMenu(containerId, inventory, attractor),
                        Component.translatable("container.skyresources.wildlife_attractor")
                ),
                buffer -> WildlifeAttractorMenu.writeClientSideData(buffer, pos)
        );
        return InteractionResult.SUCCESS;
    }
}
