package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.common.block.entity.AbstractCombustionInventoryBlockEntity;
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
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public abstract class AbstractCombustionInventoryMenu extends AbstractContainerMenu {
    public static final int[] SLOT_X = {44, 62, 80, 98, 116};
    public static final int SLOT_Y = 53;
    private static final int PLAYER_INVENTORY_Y = 84;
    private static final int PLAYER_SLOT_START = AbstractCombustionInventoryBlockEntity.SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;

    private final Block validBlock;
    private final BlockPos blockPos;
    private final ContainerLevelAccess access;

    protected AbstractCombustionInventoryMenu(
            final MenuType<?> menuType,
            final Block validBlock,
            final int containerId,
            final Inventory playerInventory,
            final ClientData data
    ) {
        super(menuType, containerId);
        this.validBlock = validBlock;
        this.blockPos = data.pos();
        this.access = data.access();

        final CombustionInventoryContainer container = new CombustionInventoryContainer(data.blockEntity());
        for (int slot = 0; slot < AbstractCombustionInventoryBlockEntity.SLOT_COUNT; slot++) {
            this.addSlot(new CombustionInventorySlot(container, slot, SLOT_X[slot], SLOT_Y));
        }
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
        if (index < AbstractCombustionInventoryBlockEntity.SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, 0, AbstractCombustionInventoryBlockEntity.SLOT_COUNT, false)) {
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
        return stillValid(this.access, player, this.validBlock);
    }

    protected static ClientData readClientData(
            final Inventory playerInventory,
            final RegistryFriendlyByteBuf buffer
    ) {
        final BlockPos pos = buffer.readBlockPos();
        return new ClientData(
                pos,
                null,
                ContainerLevelAccess.create(playerInventory.player.level(), pos)
        );
    }

    protected record ClientData(
            BlockPos pos,
            @Nullable AbstractCombustionInventoryBlockEntity blockEntity,
            ContainerLevelAccess access
    ) {
    }

    private static final class CombustionInventorySlot extends Slot {
        private CombustionInventorySlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }

        @Override
        public int getMaxStackSize(final ItemStack stack) {
            if (this.container instanceof CombustionInventoryContainer combustionContainer) {
                return combustionContainer.getSlotLimit(this.getContainerSlot(), stack);
            }
            return super.getMaxStackSize(stack);
        }
    }

    private static final class CombustionInventoryContainer implements Container {
        @Nullable
        private final AbstractCombustionInventoryBlockEntity blockEntity;
        private final NonNullList<ItemStack> localStacks =
                NonNullList.withSize(AbstractCombustionInventoryBlockEntity.SLOT_COUNT, ItemStack.EMPTY);

        private CombustionInventoryContainer(@Nullable final AbstractCombustionInventoryBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public int getContainerSize() {
            return AbstractCombustionInventoryBlockEntity.SLOT_COUNT;
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
            return this.blockEntity == null
                    ? AbstractCombustionInventoryBlockEntity.isMachineSlot(slot) && !stack.isEmpty()
                    : this.blockEntity.mayPlaceInSlot(slot, stack);
        }

        private int getSlotLimit(final int slot, final ItemStack stack) {
            return this.blockEntity == null ? stack.getMaxStackSize() : this.blockEntity.getSlotLimit(slot, stack);
        }

        @Override
        public void clearContent() {
            for (int slot = 0; slot < this.getContainerSize(); slot++) {
                this.setItem(slot, ItemStack.EMPTY);
            }
        }
    }
}
