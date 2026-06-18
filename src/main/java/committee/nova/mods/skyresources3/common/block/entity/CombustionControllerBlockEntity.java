package committee.nova.mods.skyresources3.common.block.entity;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.common.block.CombustionControllerBlock;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import committee.nova.mods.skyresources3.common.compat.ValueOutput;
import committee.nova.mods.skyresources3.common.compat.transfer.EmptyResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemResource;

public final class CombustionControllerBlockEntity extends AbstractCombustionInventoryBlockEntity {
    private static final String COOLDOWN_KEY = "cooldown";
    private static final ResourceHandler<ItemResource> EMPTY_ITEM_HANDLER = EmptyResourceHandler.instance();

    private int cooldownTicks;

    public CombustionControllerBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.COMBUSTION_CONTROLLER.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(final net.minecraft.nbt.CompoundTag tag, final net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        final ValueInput input = new ValueInput(tag, registries);
        this.cooldownTicks = input.getIntOr(COOLDOWN_KEY, 0);
    }

    @Override
    protected void saveAdditional(final net.minecraft.nbt.CompoundTag tag, final net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        final ValueOutput output = new ValueOutput(tag, registries);
        output.putInt(COOLDOWN_KEY, this.cooldownTicks);
    }

    @Override
    public ResourceHandler<ItemResource> getItemHandler() {
        return EMPTY_ITEM_HANDLER;
    }

    @Override
    public void dropContents() {
        // Controller filters are ghost entries and must not drop as real items.
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
