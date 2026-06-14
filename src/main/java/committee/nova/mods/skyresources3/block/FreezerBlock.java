package committee.nova.mods.skyresources3.block;

import committee.nova.mods.skyresources3.block.entity.FreezerBlockEntity;
import committee.nova.mods.skyresources3.menu.FreezerMenu;
import committee.nova.mods.skyresources3.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class FreezerBlock extends Block implements EntityBlock {
    public static final Property<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final Property<FreezerPart> PART = EnumProperty.create("part", FreezerPart.class);
    private static final VoxelShape MINI_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    private final Tier tier;

    public FreezerBlock(final Tier tier, final Properties properties) {
        super(properties);
        this.tier = tier;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, net.minecraft.core.Direction.NORTH)
                .setValue(PART, FreezerPart.BOTTOM));
    }

    public Tier tier() {
        return this.tier;
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new FreezerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            final Level level,
            final BlockState state,
            final BlockEntityType<T> blockEntityType
    ) {
        if (level.isClientSide() || blockEntityType != ModBlockEntityTypes.FREEZER.get()) {
            return null;
        }
        return (tickLevel, tickPos, tickState, blockEntity) -> {
            if (tickLevel instanceof ServerLevel serverLevel && blockEntity instanceof FreezerBlockEntity freezer) {
                freezer.serverTick(serverLevel);
            }
        };
    }

    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(PART, FreezerPart.BOTTOM);
    }

    @Override
    protected VoxelShape getShape(
            final BlockState state,
            final net.minecraft.world.level.BlockGetter level,
            final BlockPos pos,
            final CollisionContext context
    ) {
        return this.tier == Tier.MINI ? MINI_SHAPE : super.getShape(state, level, pos, context);
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
        return this.openMenu(level, state, pos, player, hitResult, stack);
    }

    @Override
    protected InteractionResult useWithoutItem(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Player player,
            final BlockHitResult hitResult
    ) {
        return this.openMenu(level, state, pos, player, hitResult, ItemStack.EMPTY);
    }

    @Override
    public void setPlacedBy(
            final Level level,
            final BlockPos pos,
            final BlockState state,
            final LivingEntity placer,
            final ItemStack stack
    ) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()
                && this.tier.requiresMultiblock()
                && level.getBlockState(pos.above()).is(this)
                && level.getBlockState(pos.above()).getValue(PART) == FreezerPart.BOTTOM) {
            level.setBlock(pos.above(), level.getBlockState(pos.above()).setValue(PART, FreezerPart.TOP), 3);
        }
    }

    @Override
    public BlockState playerWillDestroy(
            final Level level,
            final BlockPos pos,
            final BlockState state,
            final Player player
    ) {
        if (!level.isClientSide()
                && state.getValue(PART) == FreezerPart.BOTTOM
                && level.getBlockEntity(pos) instanceof FreezerBlockEntity freezer) {
            freezer.dropContents();
        }
        return super.playerWillDestroy(level, pos, state, player);
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
        builder.add(FACING, PART);
    }

    private InteractionResult openMenu(
            final Level level,
            final BlockState state,
            final BlockPos pos,
            final Player player,
            final BlockHitResult hitResult,
            final ItemStack stack
    ) {
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, hitResult.getDirection(), stack)) {
            return InteractionResult.PASS;
        }
        final BlockPos controllerPos = state.getValue(PART) == FreezerPart.TOP ? pos.below() : pos;
        if (!(level.getBlockEntity(controllerPos) instanceof FreezerBlockEntity freezer)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) -> new FreezerMenu(containerId, inventory, freezer),
                        Component.translatable(freezer.getTier().containerTranslationKey())
                ),
                buffer -> FreezerMenu.writeClientSideData(buffer, controllerPos, freezer)
        );
        return InteractionResult.SUCCESS_SERVER;
    }

    public enum FreezerPart implements StringRepresentable {
        TOP("top"),
        BOTTOM("bottom");

        private final String serializedName;

        FreezerPart(final String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public String getSerializedName() {
            return this.serializedName;
        }
    }

    public enum Tier {
        MINI("mini_freezer", 2, 0.25F, false),
        IRON("iron_freezer", 6, 1.0F, true),
        LIGHT("light_freezer", 10, 100.0F, true);

        private final String id;
        private final int slotCount;
        private final float speed;
        private final boolean requiresMultiblock;

        Tier(final String id, final int slotCount, final float speed, final boolean requiresMultiblock) {
            this.id = id;
            this.slotCount = slotCount;
            this.speed = speed;
            this.requiresMultiblock = requiresMultiblock;
        }

        public String id() {
            return this.id;
        }

        public int slotCount() {
            return this.slotCount;
        }

        public int inputCount() {
            return this.slotCount / 2;
        }

        public float speed() {
            return this.speed;
        }

        public boolean requiresMultiblock() {
            return this.requiresMultiblock;
        }

        public String containerTranslationKey() {
            return "container.skyresources." + this.id;
        }
    }
}
