package committee.nova.mods.skyresources3.menu;

import committee.nova.mods.skyresources3.block.MachineCasingBlock;
import committee.nova.mods.skyresources3.block.entity.MachineCasingBlockEntity;
import committee.nova.mods.skyresources3.registry.ModMenuTypes;
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

public final class MachineCasingMenu extends AbstractContainerMenu {
    public static final int FUEL_SLOT_X = 80;
    public static final int FUEL_SLOT_Y = 53;
    private static final int PLAYER_INVENTORY_Y = 84;
    private static final int PLAYER_SLOT_START = MachineCasingBlockEntity.SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;

    private final BlockPos blockPos;
    private final ContainerLevelAccess access;
    private final DataSlot currentHeat;
    private final DataSlot maxHeat;
    private final DataSlot heatPerTick;
    private final DataSlot hasHeater;
    private final DataSlot validMultiblock;

    public MachineCasingMenu(final int containerId, final Inventory playerInventory, final RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, readClientData(playerInventory, data));
    }

    public MachineCasingMenu(
            final int containerId,
            final Inventory playerInventory,
            final MachineCasingBlockEntity blockEntity
    ) {
        this(
                containerId,
                playerInventory,
                new MachineCasingClientData(
                        blockEntity.getBlockPos(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos())
                )
        );
    }

    private MachineCasingMenu(
            final int containerId,
            final Inventory playerInventory,
            final MachineCasingClientData data
    ) {
        super(ModMenuTypes.MACHINE_CASING.get(), containerId);
        this.blockPos = data.pos();
        this.access = data.access();

        this.addSlot(new MachineCasingFuelSlot(
                new MachineCasingContainer(data.blockEntity()),
                MachineCasingBlockEntity.FUEL_SLOT,
                FUEL_SLOT_X,
                FUEL_SLOT_Y
        ));
        this.addStandardInventorySlots(playerInventory, 8, PLAYER_INVENTORY_Y);

        this.currentHeat = this.addDataSlot(currentHeatSlot(data.blockEntity()));
        this.maxHeat = this.addDataSlot(maxHeatSlot(data.blockEntity()));
        this.heatPerTick = this.addDataSlot(heatPerTickSlot(data.blockEntity()));
        this.hasHeater = this.addDataSlot(hasHeaterSlot(data.blockEntity()));
        this.validMultiblock = this.addDataSlot(validMultiblockSlot(playerInventory, data));
    }

    public static void writeClientSideData(final RegistryFriendlyByteBuf buffer, final BlockPos pos) {
        buffer.writeBlockPos(pos);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public int currentHeat() {
        return this.currentHeat.get();
    }

    public int maxHeat() {
        return this.maxHeat.get();
    }

    public int heatPerTick() {
        return this.heatPerTick.get();
    }

    public boolean hasHeater() {
        return this.hasHeater.get() > 0;
    }

    public boolean hasValidMultiblock() {
        return this.validMultiblock.get() > 0;
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
        if (index < MachineCasingBlockEntity.SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(
                stack,
                MachineCasingBlockEntity.FUEL_SLOT,
                MachineCasingBlockEntity.FUEL_SLOT + 1,
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
        return this.access.evaluate((level, pos) ->
                level.getBlockState(pos).getBlock() instanceof MachineCasingBlock
                        && player.distanceToSqr(
                        pos.getX() + 0.5D,
                        pos.getY() + 0.5D,
                        pos.getZ() + 0.5D
                ) <= 64.0D, true);
    }

    private static DataSlot currentHeatSlot(@Nullable final MachineCasingBlockEntity blockEntity) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return blockEntity.currentHeat();
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private static DataSlot maxHeatSlot(@Nullable final MachineCasingBlockEntity blockEntity) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return blockEntity.maxHeat();
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private static DataSlot heatPerTickSlot(@Nullable final MachineCasingBlockEntity blockEntity) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return blockEntity.heatPerTick();
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private static DataSlot hasHeaterSlot(@Nullable final MachineCasingBlockEntity blockEntity) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return blockEntity.hasHeater() ? 1 : 0;
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private static DataSlot validMultiblockSlot(
            final Inventory playerInventory,
            final MachineCasingClientData data
    ) {
        if (data.blockEntity() == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return data.blockEntity().hasValidMultiblock(playerInventory.player.level()) ? 1 : 0;
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private static MachineCasingClientData readClientData(
            final Inventory playerInventory,
            final RegistryFriendlyByteBuf buffer
    ) {
        final BlockPos pos = buffer.readBlockPos();
        return new MachineCasingClientData(
                pos,
                null,
                ContainerLevelAccess.create(playerInventory.player.level(), pos)
        );
    }

    private record MachineCasingClientData(
            BlockPos pos,
            @Nullable MachineCasingBlockEntity blockEntity,
            ContainerLevelAccess access
    ) {
    }

    private static final class MachineCasingFuelSlot extends Slot {
        private MachineCasingFuelSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }
    }

    private static final class MachineCasingContainer implements Container {
        @Nullable
        private final MachineCasingBlockEntity blockEntity;
        private final NonNullList<ItemStack> localStacks =
                NonNullList.withSize(MachineCasingBlockEntity.SLOT_COUNT, ItemStack.EMPTY);

        private MachineCasingContainer(@Nullable final MachineCasingBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public int getContainerSize() {
            return MachineCasingBlockEntity.SLOT_COUNT;
        }

        @Override
        public boolean isEmpty() {
            return this.getItem(MachineCasingBlockEntity.FUEL_SLOT).isEmpty();
        }

        @Override
        public ItemStack getItem(final int slot) {
            return this.blockEntity == null ? this.localStacks.get(slot) : this.blockEntity.getStackInSlot(slot);
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
            return this.blockEntity == null || this.blockEntity.mayPlaceInSlot(slot, stack);
        }

        @Override
        public void clearContent() {
            this.setItem(MachineCasingBlockEntity.FUEL_SLOT, ItemStack.EMPTY);
        }
    }
}
