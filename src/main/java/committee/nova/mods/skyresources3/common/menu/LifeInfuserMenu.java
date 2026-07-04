package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.common.block.entity.LifeInfuserBlockEntity;
import committee.nova.mods.skyresources3.common.item.HealthGemItem;
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
import net.minecraft.server.level.ServerLevel;

public final class LifeInfuserMenu extends SkyResourcesMenu {
    public static final int GEM_SLOT_X = 100;
    public static final int GEM_SLOT_Y = 25;
    public static final int INPUT_SLOT_X = 59;
    public static final int INPUT_SLOT_Y = 25;
    private static final int PLAYER_INVENTORY_Y = 84;
    private static final int PLAYER_SLOT_START = LifeInfuserBlockEntity.SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;

    private final BlockPos blockPos;
    private final ContainerLevelAccess access;
    private final DataSlot storedHealth;
    private final DataSlot validMultiblock;

    public LifeInfuserMenu(final int containerId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(containerId, playerInventory, readClientData(playerInventory, data));
    }

    public LifeInfuserMenu(
            final int containerId,
            final Inventory playerInventory,
            final LifeInfuserBlockEntity blockEntity
    ) {
        this(
                containerId,
                playerInventory,
                new LifeInfuserClientData(
                        blockEntity.getBlockPos(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos())
                )
        );
    }

    private LifeInfuserMenu(
            final int containerId,
            final Inventory playerInventory,
            final LifeInfuserClientData data
    ) {
        super(ModMenuTypes.LIFE_INFUSER.get(), containerId);
        this.blockPos = data.pos();
        this.access = data.access();

        final LifeInfuserContainer container = new LifeInfuserContainer(data.blockEntity());
        this.addSlot(new LifeInfuserSlot(
                container,
                LifeInfuserBlockEntity.GEM_SLOT,
                GEM_SLOT_X,
                GEM_SLOT_Y
        ));
        this.addSlot(new LifeInfuserSlot(
                container,
                LifeInfuserBlockEntity.INPUT_SLOT,
                INPUT_SLOT_X,
                INPUT_SLOT_Y
        ));
        this.addStandardInventorySlots(playerInventory, 8, PLAYER_INVENTORY_Y);
        this.storedHealth = this.addDataSlot(storedHealthSlot(data.blockEntity()));
        this.validMultiblock = this.addDataSlot(validMultiblockSlot(data.blockEntity()));
    }

    public static void writeClientSideData(final FriendlyByteBuf buffer, final BlockPos pos) {
        buffer.writeBlockPos(pos);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public int storedHealth() {
        return this.storedHealth.get();
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
        if (index < LifeInfuserBlockEntity.SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.getItem() instanceof HealthGemItem) {
            if (!this.moveItemStackTo(
                    stack,
                    LifeInfuserBlockEntity.GEM_SLOT,
                    LifeInfuserBlockEntity.GEM_SLOT + 1,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(
                stack,
                LifeInfuserBlockEntity.INPUT_SLOT,
                LifeInfuserBlockEntity.INPUT_SLOT + 1,
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
        return stillValid(this.access, player, ModBlocks.LIFE_INFUSER.get());
    }

    private static LifeInfuserClientData readClientData(
            final Inventory playerInventory,
            final FriendlyByteBuf buffer
    ) {
        final BlockPos pos = buffer.readBlockPos();
        return new LifeInfuserClientData(
                pos,
                null,
                ContainerLevelAccess.create(playerInventory.player.level(), pos)
        );
    }

    private static DataSlot storedHealthSlot(@Nullable final LifeInfuserBlockEntity blockEntity) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return HealthGemItem.getHealthInjected(blockEntity.getStackInSlot(LifeInfuserBlockEntity.GEM_SLOT));
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private static DataSlot validMultiblockSlot(@Nullable final LifeInfuserBlockEntity blockEntity) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getLevel() instanceof ServerLevel serverLevel
                        && blockEntity.hasValidMultiblock(serverLevel) ? 1 : 0;
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private record LifeInfuserClientData(
            BlockPos pos,
            @Nullable LifeInfuserBlockEntity blockEntity,
            ContainerLevelAccess access
    ) {
    }

    private static final class LifeInfuserSlot extends Slot {
        private LifeInfuserSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }
    }

    private static final class LifeInfuserContainer implements Container {
        @Nullable
        private final LifeInfuserBlockEntity blockEntity;
        private final NonNullList<ItemStack> localStacks =
                NonNullList.withSize(LifeInfuserBlockEntity.SLOT_COUNT, ItemStack.EMPTY);

        private LifeInfuserContainer(@Nullable final LifeInfuserBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public int getContainerSize() {
            return LifeInfuserBlockEntity.SLOT_COUNT;
        }

        @Override
        public boolean isEmpty() {
            return this.getItem(LifeInfuserBlockEntity.GEM_SLOT).isEmpty()
                    && this.getItem(LifeInfuserBlockEntity.INPUT_SLOT).isEmpty();
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
            if (this.blockEntity != null) {
                return this.blockEntity.mayPlaceInSlot(slot, stack);
            }
            return switch (slot) {
                case LifeInfuserBlockEntity.GEM_SLOT -> stack.getItem() instanceof HealthGemItem;
                case LifeInfuserBlockEntity.INPUT_SLOT -> !stack.isEmpty()
                        && !(stack.getItem() instanceof HealthGemItem);
                default -> false;
            };
        }

        @Override
        public void clearContent() {
            this.setItem(LifeInfuserBlockEntity.GEM_SLOT, ItemStack.EMPTY);
            this.setItem(LifeInfuserBlockEntity.INPUT_SLOT, ItemStack.EMPTY);
        }
    }
}
