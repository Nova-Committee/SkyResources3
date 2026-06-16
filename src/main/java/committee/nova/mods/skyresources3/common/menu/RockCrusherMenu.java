package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.common.block.entity.RockCrusherBlockEntity;
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

public final class RockCrusherMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT_X = 55;
    public static final int INPUT_SLOT_Y = 53;
    public static final int OUTPUT_SLOT_X = 109;
    public static final int FIRST_OUTPUT_SLOT_Y = 35;
    public static final int SLOT_SPACING = 18;
    private static final int PLAYER_INVENTORY_Y = 107;
    private static final int PLAYER_SLOT_START = RockCrusherBlockEntity.SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;

    private final BlockPos blockPos;
    private final ContainerLevelAccess access;
    private final DataSlot progress;
    private final DataSlot energyLow;
    private final DataSlot energyHigh;

    public RockCrusherMenu(final int containerId, final Inventory playerInventory, final RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, readClientData(playerInventory, data));
    }

    public RockCrusherMenu(
            final int containerId,
            final Inventory playerInventory,
            final RockCrusherBlockEntity blockEntity
    ) {
        this(
                containerId,
                playerInventory,
                new RockCrusherClientData(
                        blockEntity.getBlockPos(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos())
                )
        );
    }

    private RockCrusherMenu(
            final int containerId,
            final Inventory playerInventory,
            final RockCrusherClientData data
    ) {
        super(ModMenuTypes.ROCK_CRUSHER.get(), containerId);
        this.blockPos = data.pos();
        this.access = data.access();

        final RockCrusherContainer container = new RockCrusherContainer(data.blockEntity());
        this.addMachineSlots(container);
        this.addStandardInventorySlots(playerInventory, 8, PLAYER_INVENTORY_Y);

        this.progress = this.addDataSlot(createProgressSlot(data.blockEntity()));
        this.energyLow = this.addDataSlot(createEnergySlot(data.blockEntity(), false));
        this.energyHigh = this.addDataSlot(createEnergySlot(data.blockEntity(), true));
    }

    public static void writeClientSideData(final RegistryFriendlyByteBuf buffer, final BlockPos pos) {
        buffer.writeBlockPos(pos);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public float getProgressRatio() {
        return Math.min(1.0F, this.progress.get() / (float) RockCrusherBlockEntity.MAX_PROGRESS);
    }

    public int getSpeed() {
        return Config.rockCrusherSpeed;
    }

    public int getEnergyStored() {
        return (this.energyHigh.get() << 16) | (this.energyLow.get() & 0xFFFF);
    }

    public int getMaxEnergyStored() {
        return RockCrusherBlockEntity.ENERGY_CAPACITY;
    }

    public float getEnergyRatio() {
        return Math.min(1.0F, this.getEnergyStored() / (float) this.getMaxEnergyStored());
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
        if (index < RockCrusherBlockEntity.SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(
                stack,
                RockCrusherBlockEntity.INPUT_SLOT,
                RockCrusherBlockEntity.INPUT_SLOT + 1,
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
        return stillValid(this.access, player, ModBlocks.ROCK_CRUSHER.get());
    }

    private void addMachineSlots(final Container container) {
        this.addSlot(new RockCrusherInputSlot(container, RockCrusherBlockEntity.INPUT_SLOT, INPUT_SLOT_X, INPUT_SLOT_Y));
        for (int output = 0; output < RockCrusherBlockEntity.OUTPUT_SLOT_COUNT; output++) {
            this.addSlot(new RockCrusherOutputSlot(
                    container,
                    RockCrusherBlockEntity.FIRST_OUTPUT_SLOT + output,
                    OUTPUT_SLOT_X,
                    FIRST_OUTPUT_SLOT_Y + output * SLOT_SPACING
            ));
        }
    }

    private static DataSlot createProgressSlot(@Nullable final RockCrusherBlockEntity blockEntity) {
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
            @Nullable final RockCrusherBlockEntity blockEntity,
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

    private static RockCrusherClientData readClientData(
            final Inventory playerInventory,
            final RegistryFriendlyByteBuf buffer
    ) {
        final BlockPos pos = buffer.readBlockPos();
        return new RockCrusherClientData(
                pos,
                null,
                ContainerLevelAccess.create(playerInventory.player.level(), pos)
        );
    }

    private record RockCrusherClientData(
            BlockPos pos,
            @Nullable RockCrusherBlockEntity blockEntity,
            ContainerLevelAccess access
    ) {
    }

    private static class RockCrusherSlot extends Slot {
        RockCrusherSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }
    }

    private static final class RockCrusherInputSlot extends RockCrusherSlot {
        private RockCrusherInputSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }
    }

    private static final class RockCrusherOutputSlot extends RockCrusherSlot {
        private RockCrusherOutputSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return false;
        }
    }

    private static final class RockCrusherContainer implements Container {
        @Nullable
        private final RockCrusherBlockEntity blockEntity;
        private final NonNullList<ItemStack> localStacks =
                NonNullList.withSize(RockCrusherBlockEntity.SLOT_COUNT, ItemStack.EMPTY);

        private RockCrusherContainer(@Nullable final RockCrusherBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public int getContainerSize() {
            return RockCrusherBlockEntity.SLOT_COUNT;
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
                    ? slot == RockCrusherBlockEntity.INPUT_SLOT && !stack.isEmpty()
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
