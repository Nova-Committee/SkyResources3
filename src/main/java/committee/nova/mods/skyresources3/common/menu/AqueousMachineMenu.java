package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.common.block.entity.AqueousMachineBlockEntity;
import committee.nova.mods.skyresources3.core.machine.AqueousMachineMode;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import committee.nova.mods.skyresources3.init.registry.ModMenuTypes;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class AqueousMachineMenu extends SkyResourcesMenu {
    public static final int INPUT_SLOT_X = 55;
    public static final int INPUT_SLOT_Y = 49;
    public static final int OUTPUT_SLOT_X = 109;
    public static final int OUTPUT_SLOT_Y = 49;
    private static final int PLAYER_INVENTORY_Y = 107;
    private static final int PLAYER_SLOT_START = AqueousMachineBlockEntity.SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;

    private final BlockPos blockPos;
    private final ContainerLevelAccess access;
    private final AqueousMachineMode mode;
    private final DataSlot progress;
    private final DataSlot energyLow;
    private final DataSlot energyHigh;
    private final DataSlot water;

    public AqueousMachineMenu(final int containerId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(containerId, playerInventory, readClientData(playerInventory, data));
    }

    public AqueousMachineMenu(
            final int containerId,
            final Inventory playerInventory,
            final AqueousMachineBlockEntity blockEntity
    ) {
        this(
                containerId,
                playerInventory,
                new AqueousClientData(
                        blockEntity.getBlockPos(),
                        blockEntity.getMode(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos())
                )
        );
    }

    private AqueousMachineMenu(
            final int containerId,
            final Inventory playerInventory,
            final AqueousClientData data
    ) {
        super(ModMenuTypes.AQUEOUS_MACHINE.get(), containerId);
        this.blockPos = data.pos();
        this.access = data.access();
        this.mode = data.mode();

        final AqueousContainer container = new AqueousContainer(data.blockEntity());
        this.addMachineSlots(container);
        this.addStandardInventorySlots(playerInventory, 8, PLAYER_INVENTORY_Y);

        this.progress = this.addDataSlot(createProgressSlot(data.blockEntity()));
        this.energyLow = this.addDataSlot(createEnergySlot(data.blockEntity(), false));
        this.energyHigh = this.addDataSlot(createEnergySlot(data.blockEntity(), true));
        this.water = this.addDataSlot(createWaterSlot(data.blockEntity()));
    }

    public static void writeClientSideData(
            final FriendlyByteBuf buffer,
            final BlockPos pos,
            final AqueousMachineMode mode
    ) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(mode.isConcentrator());
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public String getModeTranslationKey() {
        return this.mode.modeTranslationKey();
    }

    public float getProgressRatio() {
        return Math.min(1.0F, this.progress.get() / (float) AqueousMachineBlockEntity.MAX_PROGRESS);
    }

    public int getSpeed() {
        return this.mode.isConcentrator()
                ? Config.aqueousConcentratorSpeed
                : Config.aqueousDeconcentratorSpeed;
    }

    public int getEnergyStored() {
        return (this.energyHigh.get() << 16) | (this.energyLow.get() & 0xFFFF);
    }

    public int getMaxEnergyStored() {
        return AqueousMachineBlockEntity.ENERGY_CAPACITY;
    }

    public float getEnergyRatio() {
        return Math.min(1.0F, this.getEnergyStored() / (float) this.getMaxEnergyStored());
    }

    public int getWaterStored() {
        return this.water.get();
    }

    public int getMaxWaterStored() {
        return AqueousMachineBlockEntity.WATER_CAPACITY;
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
        if (index < AqueousMachineBlockEntity.SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(
                stack,
                AqueousMachineBlockEntity.INPUT_SLOT,
                AqueousMachineBlockEntity.INPUT_SLOT + 1,
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
        return this.access.evaluate((level, pos) -> {
            final Block block = level.getBlockState(pos).getBlock();
            return isAqueousBlock(block)
                    && player.distanceToSqr(
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D
            ) <= 64.0D;
        }, true);
    }

    private void addMachineSlots(final Container container) {
        this.addSlot(new AqueousInputSlot(container, AqueousMachineBlockEntity.INPUT_SLOT, INPUT_SLOT_X, INPUT_SLOT_Y));
        this.addSlot(new AqueousOutputSlot(container, AqueousMachineBlockEntity.OUTPUT_SLOT, OUTPUT_SLOT_X, OUTPUT_SLOT_Y));
    }

    private static boolean isAqueousBlock(final Block block) {
        return block == ModBlocks.AQUEOUS_CONCENTRATOR.get()
                || block == ModBlocks.AQUEOUS_DECONCENTRATOR.get();
    }

    private static DataSlot createProgressSlot(@Nullable final AqueousMachineBlockEntity blockEntity) {
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
            @Nullable final AqueousMachineBlockEntity blockEntity,
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

    private static DataSlot createWaterSlot(@Nullable final AqueousMachineBlockEntity blockEntity) {
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

    private static AqueousClientData readClientData(
            final Inventory playerInventory,
            final FriendlyByteBuf buffer
    ) {
        final BlockPos pos = buffer.readBlockPos();
        final AqueousMachineMode mode = buffer.readBoolean()
                ? AqueousMachineMode.CONCENTRATOR
                : AqueousMachineMode.DECONCENTRATOR;
        return new AqueousClientData(
                pos,
                mode,
                null,
                ContainerLevelAccess.create(playerInventory.player.level(), pos)
        );
    }

    private record AqueousClientData(
            BlockPos pos,
            AqueousMachineMode mode,
            @Nullable AqueousMachineBlockEntity blockEntity,
            ContainerLevelAccess access
    ) {
    }

    private static class AqueousSlot extends Slot {
        AqueousSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }
    }

    private static final class AqueousInputSlot extends AqueousSlot {
        private AqueousInputSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }
    }

    private static final class AqueousOutputSlot extends AqueousSlot {
        private AqueousOutputSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return false;
        }
    }

    private static final class AqueousContainer implements Container {
        @Nullable
        private final AqueousMachineBlockEntity blockEntity;
        private final NonNullList<ItemStack> localStacks =
                NonNullList.withSize(AqueousMachineBlockEntity.SLOT_COUNT, ItemStack.EMPTY);

        private AqueousContainer(@Nullable final AqueousMachineBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public int getContainerSize() {
            return AqueousMachineBlockEntity.SLOT_COUNT;
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
                    ? slot == AqueousMachineBlockEntity.INPUT_SLOT && !stack.isEmpty()
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
