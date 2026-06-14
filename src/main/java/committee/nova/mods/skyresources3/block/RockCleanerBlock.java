package committee.nova.mods.skyresources3.block;

import com.mojang.serialization.MapCodec;
import committee.nova.mods.skyresources3.block.entity.RockCleanerBlockEntity;
import committee.nova.mods.skyresources3.menu.RockCleanerMenu;
import committee.nova.mods.skyresources3.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.jetbrains.annotations.Nullable;

public final class RockCleanerBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<RockCleanerBlock> CODEC = simpleCodec(RockCleanerBlock::new);

    public RockCleanerBlock(final BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<RockCleanerBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new RockCleanerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            final Level level,
            final BlockState state,
            final BlockEntityType<T> blockEntityType
    ) {
        if (level.isClientSide() || blockEntityType != ModBlockEntityTypes.ROCK_CLEANER.get()) {
            return null;
        }
        return (tickLevel, tickPos, tickState, blockEntity) -> {
            if (tickLevel instanceof ServerLevel serverLevel && blockEntity instanceof RockCleanerBlockEntity cleaner) {
                cleaner.serverTick(serverLevel);
            }
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(final BlockState state, final Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(final BlockState state, final Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
        if (!(level.getBlockEntity(pos) instanceof RockCleanerBlockEntity cleaner)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!stack.isEmpty() && FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection())) {
            return InteractionResult.SUCCESS_SERVER;
        }
        return this.openMenu(pos, player, cleaner);
    }

    @Override
    protected InteractionResult useWithoutItem(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Player player,
            final BlockHitResult hitResult
    ) {
        if (!level.mayInteract(player, pos)
                || !player.mayUseItemAt(pos, hitResult.getDirection(), ItemStack.EMPTY)) {
            return InteractionResult.PASS;
        }
        if (!(level.getBlockEntity(pos) instanceof RockCleanerBlockEntity cleaner)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        return this.openMenu(pos, player, cleaner);
    }

    private InteractionResult openMenu(
            final BlockPos pos,
            final Player player,
            final RockCleanerBlockEntity cleaner
    ) {
        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) -> new RockCleanerMenu(containerId, inventory, cleaner),
                        Component.translatable("container.skyresources.rock_cleaner")
                ),
                buffer -> RockCleanerMenu.writeClientSideData(buffer, pos)
        );
        return InteractionResult.SUCCESS_SERVER;
    }
}
