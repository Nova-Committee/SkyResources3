package committee.nova.mods.skyresources3.block;

import committee.nova.mods.skyresources3.block.entity.MachineCasingBlockEntity;
import committee.nova.mods.skyresources3.menu.MachineCasingMenu;
import committee.nova.mods.skyresources3.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class MachineCasingBlock extends Block implements EntityBlock {
    public MachineCasingBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.defaultBlockState();
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new MachineCasingBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            final Level level,
            final BlockState state,
            final BlockEntityType<T> blockEntityType
    ) {
        if (level.isClientSide() || blockEntityType != ModBlockEntityTypes.MACHINE_CASING.get()) {
            return null;
        }
        return (tickLevel, tickPos, tickState, blockEntity) -> {
            if (tickLevel instanceof ServerLevel serverLevel && blockEntity instanceof MachineCasingBlockEntity casing) {
                casing.serverTick(serverLevel);
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
        if (!(level.getBlockEntity(pos) instanceof MachineCasingBlockEntity casing)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!casing.hasHeater() && casing.canInstallMachine(stack)) {
            return casing.installHeater(stack, player) ? InteractionResult.SUCCESS_SERVER : InteractionResult.PASS;
        }
        return this.openMenu(pos, player, casing);
    }

    @Override
    protected InteractionResult useWithoutItem(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Player player,
            final BlockHitResult hitResult
    ) {
        if (!level.mayInteract(player, pos)) {
            return InteractionResult.PASS;
        }
        if (!(level.getBlockEntity(pos) instanceof MachineCasingBlockEntity casing)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown() && casing.hasHeater()) {
            final ItemStack removed = casing.removeHeater();
            if (!player.addItem(removed)) {
                Containers.dropItemStack(level, player.getX(), player.getY(), player.getZ(), removed);
            }
            return InteractionResult.SUCCESS_SERVER;
        }
        return this.openMenu(pos, player, casing);
    }

    private InteractionResult openMenu(
            final BlockPos pos,
            final Player player,
            final MachineCasingBlockEntity casing
    ) {
        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) -> new MachineCasingMenu(containerId, inventory, casing),
                        Component.translatable("container.skyresources3.machine_casing")
                ),
                buffer -> MachineCasingMenu.writeClientSideData(buffer, pos)
        );
        return InteractionResult.SUCCESS_SERVER;
    }
}
