package committee.nova.mods.skyresources3.block;

import committee.nova.mods.skyresources3.block.entity.FusionTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class FusionTableBlock extends Block implements EntityBlock {
    public FusionTableBlock(final Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new FusionTableBlockEntity(pos, state);
    }
}
