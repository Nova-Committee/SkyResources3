package committee.nova.mods.skyresources3.common.block;

import committee.nova.mods.skyresources3.common.block.entity.StandaloneMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class StandaloneMachineBlock extends Block implements EntityBlock {
    private final StandaloneMachineBlockEntity.MachineKind kind;

    public StandaloneMachineBlock(
            final StandaloneMachineBlockEntity.MachineKind kind,
            final Properties properties
    ) {
        super(properties);
        this.kind = kind;
    }

    public StandaloneMachineBlockEntity.MachineKind kind() {
        return this.kind;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.defaultBlockState();
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new StandaloneMachineBlockEntity(pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(
            final LevelReader level,
            final BlockPos pos,
            final BlockState state,
            final boolean includeData
    ) {
        if (level.getBlockEntity(pos) instanceof StandaloneMachineBlockEntity machine) {
            return machine.asItemStack();
        }
        return super.getCloneItemStack(level, pos, state, includeData);
    }

    @Override
    public ItemStack getCloneItemStack(
            final LevelReader level,
            final BlockPos pos,
            final BlockState state,
            final boolean includeData,
            final Player player
    ) {
        if (level.getBlockEntity(pos) instanceof StandaloneMachineBlockEntity machine) {
            return machine.asItemStack();
        }
        return super.getCloneItemStack(level, pos, state, includeData);
    }
}
