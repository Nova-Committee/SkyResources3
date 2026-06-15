package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.common.block.entity.LifeInjectorBlockEntity;
import committee.nova.mods.skyresources3.common.item.HealthGemItem;
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

public final class LifeInjectorMenu extends AbstractContainerMenu {
    public static final int GEM_SLOT_X = 80;
    public static final int GEM_SLOT_Y = 53;
    private static final int PLAYER_INVENTORY_Y = 84;
    private static final int PLAYER_SLOT_START = LifeInjectorBlockEntity.SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;

    private final BlockPos blockPos;
    private final ContainerLevelAccess access;
    private final DataSlot storedHealth;

    public LifeInjectorMenu(final int containerId, final Inventory playerInventory, final RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, readClientData(playerInventory, data));
    }

    public LifeInjectorMenu(
            final int containerId,
            final Inventory playerInventory,
            final LifeInjectorBlockEntity blockEntity
    ) {
        this(
                containerId,
                playerInventory,
                new LifeInjectorClientData(
                        blockEntity.getBlockPos(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos())
                )
        );
    }

    private LifeInjectorMenu(
            final int containerId,
            final Inventory playerInventory,
            final LifeInjectorClientData data
    ) {
        super(ModMenuTypes.LIFE_INJECTOR.get(), containerId);
        this.blockPos = data.pos();
        this.access = data.access();

        this.addSlot(new LifeInjectorSlot(
                new LifeInjectorContainer(data.blockEntity()),
                LifeInjectorBlockEntity.GEM_SLOT,
                GEM_SLOT_X,
                GEM_SLOT_Y
        ));
        this.addStandardInventorySlots(playerInventory, 8, PLAYER_INVENTORY_Y);
        this.storedHealth = this.addDataSlot(storedHealthSlot(data.blockEntity()));
    }

    public static void writeClientSideData(final RegistryFriendlyByteBuf buffer, final BlockPos pos) {
        buffer.writeBlockPos(pos);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public int storedHealth() {
        return this.storedHealth.get();
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
        if (index < LifeInjectorBlockEntity.SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, 0, LifeInjectorBlockEntity.SLOT_COUNT, false)) {
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
        return stillValid(this.access, player, ModBlocks.LIFE_INJECTOR.get());
    }

    private static LifeInjectorClientData readClientData(
            final Inventory playerInventory,
            final RegistryFriendlyByteBuf buffer
    ) {
        final BlockPos pos = buffer.readBlockPos();
        return new LifeInjectorClientData(
                pos,
                null,
                ContainerLevelAccess.create(playerInventory.player.level(), pos)
        );
    }

    private static DataSlot storedHealthSlot(@Nullable final LifeInjectorBlockEntity blockEntity) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return HealthGemItem.getHealthInjected(blockEntity.getStackInSlot(LifeInjectorBlockEntity.GEM_SLOT));
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private record LifeInjectorClientData(
            BlockPos pos,
            @Nullable LifeInjectorBlockEntity blockEntity,
            ContainerLevelAccess access
    ) {
    }

    private static final class LifeInjectorSlot extends Slot {
        private LifeInjectorSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }
    }

    private static final class LifeInjectorContainer implements Container {
        @Nullable
        private final LifeInjectorBlockEntity blockEntity;
        private final NonNullList<ItemStack> localStacks =
                NonNullList.withSize(LifeInjectorBlockEntity.SLOT_COUNT, ItemStack.EMPTY);

        private LifeInjectorContainer(@Nullable final LifeInjectorBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public int getContainerSize() {
            return LifeInjectorBlockEntity.SLOT_COUNT;
        }

        @Override
        public boolean isEmpty() {
            return this.getItem(LifeInjectorBlockEntity.GEM_SLOT).isEmpty();
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
                this.localStacks.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
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
                    ? slot == LifeInjectorBlockEntity.GEM_SLOT && stack.getItem() instanceof HealthGemItem
                    : this.blockEntity.mayPlaceInSlot(slot, stack);
        }

        @Override
        public void clearContent() {
            this.setItem(LifeInjectorBlockEntity.GEM_SLOT, ItemStack.EMPTY);
        }
    }
}
