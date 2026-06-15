package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.common.block.entity.RockCleanerBlockEntity;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import committee.nova.mods.skyresources3.init.registry.ModMenuTypes;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class RockCleanerMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT_X = 55;
    public static final int INPUT_SLOT_Y = 49;
    public static final int OUTPUT_SLOT_X = 109;
    public static final int FIRST_OUTPUT_SLOT_Y = 31;
    public static final int SLOT_SPACING = 18;
    private static final int PLAYER_INVENTORY_Y = 107;
    private static final int PLAYER_SLOT_START = RockCleanerBlockEntity.SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;

    private final BlockPos blockPos;
    private final ContainerLevelAccess access;
    private final DataSlot progress;
    private final DataSlot energyLow;
    private final DataSlot energyHigh;
    private final DataSlot water;

    public RockCleanerMenu(final int containerId, final Inventory playerInventory, final RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, readClientData(playerInventory, data));
    }

    public RockCleanerMenu(
            final int containerId,
            final Inventory playerInventory,
            final RockCleanerBlockEntity blockEntity
    ) {
        this(
                containerId,
                playerInventory,
                new RockCleanerClientData(
                        blockEntity.getBlockPos(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos())
                )
        );
    }

    private RockCleanerMenu(
            final int containerId,
            final Inventory playerInventory,
            final RockCleanerClientData data
    ) {
        super(ModMenuTypes.ROCK_CLEANER.get(), containerId);
        this.blockPos = data.pos();
        this.access = data.access();

        final RockCleanerContainer container = new RockCleanerContainer(data.blockEntity());
        this.addMachineSlots(container);
        this.addStandardInventorySlots(playerInventory, 8, PLAYER_INVENTORY_Y);

        this.progress = this.addDataSlot(createProgressSlot(data.blockEntity()));
        this.energyLow = this.addDataSlot(createEnergySlot(data.blockEntity(), false));
        this.energyHigh = this.addDataSlot(createEnergySlot(data.blockEntity(), true));
        this.water = this.addDataSlot(createWaterSlot(data.blockEntity()));
    }

    public static void writeClientSideData(final RegistryFriendlyByteBuf buffer, final BlockPos pos) {
        buffer.writeBlockPos(pos);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public float getProgressRatio() {
        return Math.min(1.0F, this.progress.get() / (float) RockCleanerBlockEntity.MAX_PROGRESS);
    }

    public int getEnergyStored() {
        return (this.energyHigh.get() << 16) | (this.energyLow.get() & 0xFFFF);
    }

    public int getMaxEnergyStored() {
        return RockCleanerBlockEntity.ENERGY_CAPACITY;
    }

    public float getEnergyRatio() {
        return Math.min(1.0F, this.getEnergyStored() / (float) this.getMaxEnergyStored());
    }

    public int getWaterStored() {
        return this.water.get();
    }

    public int getMaxWaterStored() {
        return RockCleanerBlockEntity.WATER_CAPACITY;
    }

    public float getWaterRatio() {
        return Math.min(1.0F, this.getWaterStored() / (float) this.getMaxWaterStored());
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        if (index < 0 || index >= this.slots.size()) {
            return ItemStack.EMPTY;
        }

        final Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        final ItemStack stack = slot.getItem();
        final ItemStack original = stack.copy();
        if (index < RockCleanerBlockEntity.SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(
                stack,
                RockCleanerBlockEntity.INPUT_SLOT,
                RockCleanerBlockEntity.INPUT_SLOT + 1,
                false
        )) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(final Player player) {
        return stillValid(this.access, player, ModBlocks.ROCK_CLEANER.get());
    }

    private void addMachineSlots(final Container container) {
        this.addSlot(new RockCleanerInputSlot(container, RockCleanerBlockEntity.INPUT_SLOT, INPUT_SLOT_X, INPUT_SLOT_Y));
        for (int output = 0; output < RockCleanerBlockEntity.OUTPUT_SLOT_COUNT; output++) {
            this.addSlot(new RockCleanerOutputSlot(
                    container,
                    RockCleanerBlockEntity.FIRST_OUTPUT_SLOT + output,
                    OUTPUT_SLOT_X,
                    FIRST_OUTPUT_SLOT_Y + output * SLOT_SPACING
            ));
        }
    }

    private static DataSlot createProgressSlot(@Nullable final RockCleanerBlockEntity blockEntity) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getProgress();
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private static DataSlot createEnergySlot(
            @Nullable final RockCleanerBlockEntity blockEntity,
            final boolean highBits
    ) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                final int energy = blockEntity.getEnergyStored();
                return highBits ? energy >>> 16 : energy & 0xFFFF;
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private static DataSlot createWaterSlot(@Nullable final RockCleanerBlockEntity blockEntity) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getWaterStored();
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private static RockCleanerClientData readClientData(
            final Inventory playerInventory,
            final RegistryFriendlyByteBuf buffer
    ) {
        final BlockPos pos = buffer.readBlockPos();
        return new RockCleanerClientData(
                pos,
                null,
                ContainerLevelAccess.create(playerInventory.player.level(), pos)
        );
    }

    private record RockCleanerClientData(
            BlockPos pos,
            @Nullable RockCleanerBlockEntity blockEntity,
            ContainerLevelAccess access
    ) {
    }

    private static class RockCleanerSlot extends Slot {
        RockCleanerSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }
    }

    private static final class RockCleanerInputSlot extends RockCleanerSlot {
        private RockCleanerInputSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }
    }

    private static final class RockCleanerOutputSlot extends RockCleanerSlot {
        private RockCleanerOutputSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return false;
        }
    }

    private static final class RockCleanerContainer implements Container {
        @Nullable
        private final RockCleanerBlockEntity blockEntity;
        private final NonNullList<ItemStack> localStacks =
                NonNullList.withSize(RockCleanerBlockEntity.SLOT_COUNT, ItemStack.EMPTY);

        private RockCleanerContainer(@Nullable final RockCleanerBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public int getContainerSize() {
            return RockCleanerBlockEntity.SLOT_COUNT;
        }

        @Override
        public boolean isEmpty() {
            for (int slot = 0; slot < this.getContainerSize(); slot++) {
                if (!this.getItem(slot).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getItem(final int slot) {
            if (this.blockEntity == null) {
                return this.localStacks.get(slot);
            }
            return this.blockEntity.getStackInSlot(slot);
        }

        @Override
        public ItemStack removeItem(final int slot, final int amount) {
            if (this.blockEntity == null) {
                return ContainerHelper.removeItem(this.localStacks, slot, amount);
            }
            return this.blockEntity.removeStack(slot, amount);
        }

        @Override
        public ItemStack removeItemNoUpdate(final int slot) {
            if (this.blockEntity == null) {
                return ContainerHelper.takeItem(this.localStacks, slot);
            }
            return this.blockEntity.removeStackNoUpdate(slot);
        }

        @Override
        public void setItem(final int slot, final ItemStack stack) {
            if (this.blockEntity == null) {
                this.localStacks.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
                return;
            }
            this.blockEntity.setStackInSlot(slot, stack);
        }

        @Override
        public void setChanged() {
            if (this.blockEntity != null) {
                this.blockEntity.setChanged();
            }
        }

        @Override
        public boolean stillValid(final Player player) {
            return this.blockEntity == null || Container.stillValidBlockEntity(this.blockEntity, player);
        }

        @Override
        public boolean canPlaceItem(final int slot, final ItemStack stack) {
            return this.blockEntity == null
                    ? slot == RockCleanerBlockEntity.INPUT_SLOT && !stack.isEmpty()
                    : this.blockEntity.mayPlaceInSlot(slot, stack);
        }

        @Override
        public void clearContent() {
            for (int slot = 0; slot < this.getContainerSize(); slot++) {
                this.setItem(slot, ItemStack.EMPTY);
            }
        }
    }
}
