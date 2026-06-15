package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.common.block.entity.QuickDropperBlockEntity;
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
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class QuickDropperMenu extends AbstractContainerMenu {
    public static final int SLOT_X = 80;
    public static final int SLOT_Y = 53;
    private static final int PLAYER_INVENTORY_Y = 84;
    private static final int PLAYER_SLOT_START = QuickDropperBlockEntity.SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;

    private final BlockPos blockPos;
    private final ContainerLevelAccess access;

    public QuickDropperMenu(final int containerId, final Inventory playerInventory, final RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, readClientData(playerInventory, data));
    }

    public QuickDropperMenu(
            final int containerId,
            final Inventory playerInventory,
            final QuickDropperBlockEntity blockEntity
    ) {
        this(
                containerId,
                playerInventory,
                new QuickDropperClientData(
                        blockEntity.getBlockPos(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos())
                )
        );
    }

    private QuickDropperMenu(
            final int containerId,
            final Inventory playerInventory,
            final QuickDropperClientData data
    ) {
        super(ModMenuTypes.QUICK_DROPPER.get(), containerId);
        this.blockPos = data.pos();
        this.access = data.access();

        this.addSlot(new QuickDropperSlot(new QuickDropperContainer(data.blockEntity()), 0, SLOT_X, SLOT_Y));
        this.addStandardInventorySlots(playerInventory, 8, PLAYER_INVENTORY_Y);
    }

    public static void writeClientSideData(final RegistryFriendlyByteBuf buffer, final BlockPos pos) {
        buffer.writeBlockPos(pos);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
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
        if (index < QuickDropperBlockEntity.SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, 0, QuickDropperBlockEntity.SLOT_COUNT, false)) {
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
        return stillValid(this.access, player, ModBlocks.QUICK_DROPPER.get());
    }

    private static QuickDropperClientData readClientData(
            final Inventory playerInventory,
            final RegistryFriendlyByteBuf buffer
    ) {
        final BlockPos pos = buffer.readBlockPos();
        return new QuickDropperClientData(
                pos,
                null,
                ContainerLevelAccess.create(playerInventory.player.level(), pos)
        );
    }

    private record QuickDropperClientData(
            BlockPos pos,
            @Nullable QuickDropperBlockEntity blockEntity,
            ContainerLevelAccess access
    ) {
    }

    private static final class QuickDropperSlot extends Slot {
        private QuickDropperSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }
    }

    private static final class QuickDropperContainer implements Container {
        @Nullable
        private final QuickDropperBlockEntity blockEntity;
        private final NonNullList<ItemStack> localStacks =
                NonNullList.withSize(QuickDropperBlockEntity.SLOT_COUNT, ItemStack.EMPTY);

        private QuickDropperContainer(@Nullable final QuickDropperBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public int getContainerSize() {
            return QuickDropperBlockEntity.SLOT_COUNT;
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
                    ? slot == 0 && !stack.isEmpty()
                    : this.blockEntity.mayPlaceInSlot(slot, stack);
        }

        @Override
        public void clearContent() {
            this.setItem(0, ItemStack.EMPTY);
        }
    }
}
