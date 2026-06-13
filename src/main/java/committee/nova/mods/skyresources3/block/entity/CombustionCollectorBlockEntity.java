package committee.nova.mods.skyresources3.block.entity;

import committee.nova.mods.skyresources3.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class CombustionCollectorBlockEntity extends AbstractCombustionInventoryBlockEntity {
    public CombustionCollectorBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.COMBUSTION_COLLECTOR.get(), pos, blockState);
    }

    public ItemStack insertOutput(final ItemStack stack) {
        return this.insertIntoInventory(stack);
    }
}
