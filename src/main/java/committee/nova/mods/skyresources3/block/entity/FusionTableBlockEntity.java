package committee.nova.mods.skyresources3.block.entity;

import committee.nova.mods.skyresources3.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class FusionTableBlockEntity extends BlockEntity {
    public FusionTableBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.FUSION_TABLE.get(), pos, blockState);
    }
}
