package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.common.block.entity.EndPortalCoreBlockEntity;
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

public final class EndPortalCoreMenu extends SkyResourcesMenu {
    public static final int SLOT_X = 80;
    public static final int SLOT_Y = 53;
    private static final int PLAYER_INVENTORY_Y = 84;
    private static final int PLAYER_SLOT_START = EndPortalCoreBlockEntity.SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;

    private final BlockPos blockPos;
    private final ContainerLevelAccess access;
    private final DataSlot validMultiblock;
    private final DataSlot validTier2;

    public EndPortalCoreMenu(final int containerId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(containerId, playerInventory, readClientData(playerInventory, data));
    }

    public EndPortalCoreMenu(
            final int containerId,
            final Inventory playerInventory,
            final EndPortalCoreBlockEntity blockEntity
    ) {
        this(
                containerId,
                playerInventory,
                new ClientData(
                        blockEntity.getBlockPos(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos()),
                        blockEntity.hasValidMultiblock(),
                        blockEntity.hasValidMultiblockTier2()
                )
        );
    }

    private EndPortalCoreMenu(final int containerId, final Inventory playerInventory, final ClientData data) {
        super(ModMenuTypes.END_PORTAL_CORE.get(), containerId);
        this.blockPos = data.pos();
        this.access = data.access();

        this.addSlot(new EyeSlot(new CoreContainer(data.blockEntity()), 0, SLOT_X, SLOT_Y));
        this.addStandardInventorySlots(playerInventory, 8, PLAYER_INVENTORY_Y);

        this.validMultiblock = this.addDataSlot(createBooleanSlot(
                data.blockEntity(),
                data.validMultiblock(),
                EndPortalCoreBlockEntity::hasValidMultiblock
        ));
        this.validTier2 = this.addDataSlot(createBooleanSlot(
                data.blockEntity(),
                data.validTier2(),
                EndPortalCoreBlockEntity::hasValidMultiblockTier2
        ));
    }

    public static void writeClientSideData(
            final FriendlyByteBuf buffer,
            final BlockPos pos,
            final EndPortalCoreBlockEntity blockEntity
    ) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(blockEntity.hasValidMultiblock());
        buffer.writeBoolean(blockEntity.hasValidMultiblockTier2());
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public boolean hasValidMultiblock() {
        return this.validMultiblock.get() != 0;
    }

    public boolean hasValidTier2() {
        return this.validTier2.get() != 0;
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
        if (index < EndPortalCoreBlockEntity.SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (EndPortalCoreBlockEntity.isFuel(stack)
                && !this.moveItemStackTo(stack, 0, EndPortalCoreBlockEntity.SLOT_COUNT, false)) {
            return ItemStack.EMPTY;
        } else if (!EndPortalCoreBlockEntity.isFuel(stack)) {
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
        return stillValid(this.access, player, ModBlocks.END_PORTAL_CORE.get());
    }

    private static ClientData readClientData(final Inventory playerInventory, final FriendlyByteBuf buffer) {
        final BlockPos pos = buffer.readBlockPos();
        final boolean validMultiblock = buffer.readBoolean();
        final boolean validTier2 = buffer.readBoolean();
        return new ClientData(
                pos,
                null,
                ContainerLevelAccess.create(playerInventory.player.level(), pos),
                validMultiblock,
                validTier2
        );
    }

    private static DataSlot createBooleanSlot(
            @Nullable final EndPortalCoreBlockEntity blockEntity,
            final boolean initialValue,
            final CoreStatusGetter getter
    ) {
        if (blockEntity == null) {
            final DataSlot slot = DataSlot.standalone();
            slot.set(initialValue ? 1 : 0);
            return slot;
        }
        return new DataSlot() {
            @Override
            public int get() {
                return getter.get(blockEntity) ? 1 : 0;
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private interface CoreStatusGetter {
        boolean get(EndPortalCoreBlockEntity blockEntity);
    }

    private record ClientData(
            BlockPos pos,
            @Nullable EndPortalCoreBlockEntity blockEntity,
            ContainerLevelAccess access,
            boolean validMultiblock,
            boolean validTier2
    ) {
    }

    private static final class EyeSlot extends Slot {
        private EyeSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }
    }

    private static final class CoreContainer implements Container {
        @Nullable
        private final EndPortalCoreBlockEntity blockEntity;
        private final NonNullList<ItemStack> localStacks =
                NonNullList.withSize(EndPortalCoreBlockEntity.SLOT_COUNT, ItemStack.EMPTY);

        private CoreContainer(@Nullable final EndPortalCoreBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public int getContainerSize() {
            return EndPortalCoreBlockEntity.SLOT_COUNT;
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
                this.localStacks.set(slot, EndPortalCoreBlockEntity.isFuel(stack) ? stack.copy() : ItemStack.EMPTY);
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
                    ? slot == 0 && EndPortalCoreBlockEntity.isFuel(stack)
                    : this.blockEntity.mayPlaceInSlot(slot, stack);
        }

        @Override
        public void clearContent() {
            this.setItem(0, ItemStack.EMPTY);
        }
    }
}
