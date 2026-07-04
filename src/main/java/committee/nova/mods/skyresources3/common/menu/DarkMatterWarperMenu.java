package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.common.block.entity.DarkMatterWarperBlockEntity;
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

public final class DarkMatterWarperMenu extends SkyResourcesMenu {
    public static final int SLOT_X = 80;
    public static final int SLOT_Y = 53;
    private static final int PLAYER_INVENTORY_Y = 84;
    private static final int PLAYER_SLOT_START = DarkMatterWarperBlockEntity.SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;

    private final BlockPos blockPos;
    private final ContainerLevelAccess access;
    private final DataSlot burnTime;
    private final DataSlot maxBurnTime;

    public DarkMatterWarperMenu(final int containerId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(containerId, playerInventory, readClientData(playerInventory, data));
    }

    public DarkMatterWarperMenu(
            final int containerId,
            final Inventory playerInventory,
            final DarkMatterWarperBlockEntity blockEntity
    ) {
        this(
                containerId,
                playerInventory,
                new ClientData(
                        blockEntity.getBlockPos(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos()),
                        blockEntity.getBurnTime(),
                        blockEntity.getMaxBurnTime()
                )
        );
    }

    private DarkMatterWarperMenu(final int containerId, final Inventory playerInventory, final ClientData data) {
        super(ModMenuTypes.DARK_MATTER_WARPER.get(), containerId);
        this.blockPos = data.pos();
        this.access = data.access();

        this.addSlot(new FuelSlot(new WarperContainer(data.blockEntity()), 0, SLOT_X, SLOT_Y));
        this.addStandardInventorySlots(playerInventory, 8, PLAYER_INVENTORY_Y);

        this.burnTime = this.addDataSlot(createBurnTimeSlot(data.blockEntity(), data.burnTime()));
        this.maxBurnTime = this.addDataSlot(createMaxBurnTimeSlot(data.blockEntity(), data.maxBurnTime()));
    }

    public static void writeClientSideData(
            final FriendlyByteBuf buffer,
            final BlockPos pos,
            final DarkMatterWarperBlockEntity blockEntity
    ) {
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(blockEntity.getBurnTime());
        buffer.writeVarInt(blockEntity.getMaxBurnTime());
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public int getBurnTime() {
        return this.burnTime.get();
    }

    public int getMaxBurnTime() {
        return this.maxBurnTime.get();
    }

    public float getFuelRatio() {
        final int max = this.getMaxBurnTime();
        if (max <= 0) {
            return 0.0F;
        }
        return Math.min(1.0F, this.getBurnTime() / (float) max);
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
        if (index < DarkMatterWarperBlockEntity.SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (DarkMatterWarperBlockEntity.isFuel(stack)
                && !this.moveItemStackTo(stack, 0, DarkMatterWarperBlockEntity.SLOT_COUNT, false)) {
            return ItemStack.EMPTY;
        } else if (!DarkMatterWarperBlockEntity.isFuel(stack)) {
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
        return stillValid(this.access, player, ModBlocks.DARK_MATTER_WARPER.get());
    }

    private static ClientData readClientData(
            final Inventory playerInventory,
            final FriendlyByteBuf buffer
    ) {
        final BlockPos pos = buffer.readBlockPos();
        final int burnTime = buffer.readVarInt();
        final int maxBurnTime = buffer.readVarInt();
        return new ClientData(
                pos,
                null,
                ContainerLevelAccess.create(playerInventory.player.level(), pos),
                burnTime,
                maxBurnTime
        );
    }

    private static DataSlot createBurnTimeSlot(
            @Nullable final DarkMatterWarperBlockEntity blockEntity,
            final int initialValue
    ) {
        if (blockEntity == null) {
            final DataSlot slot = DataSlot.standalone();
            slot.set(initialValue);
            return slot;
        }
        return new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getBurnTime();
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private static DataSlot createMaxBurnTimeSlot(
            @Nullable final DarkMatterWarperBlockEntity blockEntity,
            final int initialValue
    ) {
        if (blockEntity == null) {
            final DataSlot slot = DataSlot.standalone();
            slot.set(initialValue);
            return slot;
        }
        return new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getMaxBurnTime();
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private record ClientData(
            BlockPos pos,
            @Nullable DarkMatterWarperBlockEntity blockEntity,
            ContainerLevelAccess access,
            int burnTime,
            int maxBurnTime
    ) {
    }

    private static final class FuelSlot extends Slot {
        private FuelSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }
    }

    private static final class WarperContainer implements Container {
        @Nullable
        private final DarkMatterWarperBlockEntity blockEntity;
        private final NonNullList<ItemStack> localStacks =
                NonNullList.withSize(DarkMatterWarperBlockEntity.SLOT_COUNT, ItemStack.EMPTY);

        private WarperContainer(@Nullable final DarkMatterWarperBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public int getContainerSize() {
            return DarkMatterWarperBlockEntity.SLOT_COUNT;
        }

        @Override
        public boolean isEmpty() {
            return this.getItem(0).isEmpty();
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
                this.localStacks.set(slot, DarkMatterWarperBlockEntity.isFuel(stack) ? stack.copy() : ItemStack.EMPTY);
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
                    ? slot == 0 && DarkMatterWarperBlockEntity.isFuel(stack)
                    : this.blockEntity.mayPlaceInSlot(slot, stack);
        }

        @Override
        public void clearContent() {
            this.setItem(0, ItemStack.EMPTY);
        }
    }
}
