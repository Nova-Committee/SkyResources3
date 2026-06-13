package committee.nova.mods.skyresources3.block.entity;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.block.CombustionControllerBlock;
import committee.nova.mods.skyresources3.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;

public final class CombustionControllerBlockEntity extends AbstractCombustionInventoryBlockEntity {
    private static final String COOLDOWN_KEY = "cooldown";

    private int cooldownTicks;

    public CombustionControllerBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.COMBUSTION_CONTROLLER.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        this.cooldownTicks = input.getIntOr(COOLDOWN_KEY, 0);
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(COOLDOWN_KEY, this.cooldownTicks);
    }

    public void serverTick(final ServerLevel level) {
        if (this.cooldownTicks > 0) {
            this.cooldownTicks--;
            this.setChanged();
            return;
        }
        if (level.hasNeighborSignal(this.worldPosition)) {
            return;
        }

        final BlockPos chamber = this.chamberPos();
        if (!(level.getBlockEntity(chamber.below()) instanceof MachineCasingBlockEntity casing)
                || !casing.isChamber(chamber)) {
            return;
        }
        if (this.craftFirstMatchingFilter(level, casing)) {
            this.cooldownTicks = Math.max(0, Config.combustionControllerTicks);
            this.setChanged();
        }
    }

    @Override
    protected int slotCapacity(final int slot, final ItemResource resource) {
        return resource.isEmpty() ? 0 : 1;
    }

    private boolean matchesFilter(final ItemStack output) {
        if (output.isEmpty()) {
            return false;
        }
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            final ItemStack filter = this.getStackInSlot(slot);
            if (!filter.isEmpty() && ItemStack.isSameItemSameComponents(filter, output)) {
                return true;
            }
        }
        return false;
    }

    private boolean craftFirstMatchingFilter(final ServerLevel level, final MachineCasingBlockEntity casing) {
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            final ItemStack filter = this.getStackInSlot(slot);
            if (!filter.isEmpty() && casing.craftSingleForController(
                    level,
                    output -> ItemStack.isSameItemSameComponents(filter, output)
            )) {
                return true;
            }
        }
        return false;
    }

    private BlockPos chamberPos() {
        return this.worldPosition.relative(this.getBlockState().getValue(CombustionControllerBlock.FACING).getOpposite());
    }
}
