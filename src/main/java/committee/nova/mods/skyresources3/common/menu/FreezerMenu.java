package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.common.block.entity.FreezerBlockEntity;
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

public final class FreezerMenu extends SkyResourcesMenu {
    public static final int SLOT_START_X = 53;
    public static final int INPUT_SLOT_Y = 22;
    public static final int OUTPUT_SLOT_Y = 40;
    public static final int SLOT_SPACING = 18;
    private static final int PLAYER_INVENTORY_Y = 92;
    private static final int PROGRESS_SCALE = 1_000;

    private final BlockPos blockPos;
    private final ContainerLevelAccess access;
    private final int slotCount;
    private final int inputCount;
    private final float speed;
    private final boolean requiresMultiblock;
    private final DataSlot validMultiblock;
    private final NonNullList<DataSlot> progressSlots;

    public FreezerMenu(final int containerId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(containerId, playerInventory, readClientData(playerInventory, data));
    }

    public FreezerMenu(final int containerId, final Inventory playerInventory, final FreezerBlockEntity blockEntity) {
        this(
                containerId,
                playerInventory,
                new FreezerClientData(
                        blockEntity.getBlockPos(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos()),
                        blockEntity.getSlotCount(),
                        blockEntity.getInputCount(),
                        blockEntity.getSpeed(),
                        blockEntity.requiresMultiblock()
                )
        );
    }

    private FreezerMenu(final int containerId, final Inventory playerInventory, final FreezerClientData data) {
        super(ModMenuTypes.FREEZER.get(), containerId);
        this.blockPos = data.pos();
        this.access = data.access();
        this.slotCount = data.slotCount();
        this.inputCount = data.inputCount();
        this.speed = data.speed();
        this.requiresMultiblock = data.requiresMultiblock();
        this.progressSlots = NonNullList.withSize(this.inputCount, DataSlot.standalone());

        final FreezerContainer container = new FreezerContainer(data.blockEntity(), this.slotCount);
        this.addMachineSlots(container);
        this.addStandardInventorySlots(playerInventory, 8, PLAYER_INVENTORY_Y);

        this.validMultiblock = this.addDataSlot(createValidMultiblockSlot(data.blockEntity()));
        for (int inputSlot = 0; inputSlot < this.inputCount; inputSlot++) {
            final DataSlot progress = this.addDataSlot(createProgressSlot(data.blockEntity(), inputSlot));
            this.progressSlots.set(inputSlot, progress);
        }
    }

    public static void writeClientSideData(
            final FriendlyByteBuf buffer,
            final BlockPos pos,
            final FreezerBlockEntity blockEntity
    ) {
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(blockEntity.getSlotCount());
        buffer.writeVarInt(blockEntity.getInputCount());
        buffer.writeFloat(blockEntity.getSpeed());
        buffer.writeBoolean(blockEntity.requiresMultiblock());
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public int getInputCount() {
        return this.inputCount;
    }

    public float getSpeed() {
        return this.speed;
    }

    public boolean requiresMultiblock() {
        return this.requiresMultiblock;
    }

    public boolean hasValidMultiblock() {
        return this.validMultiblock.get() > 0;
    }

    public float getProgressRatio(final int inputSlot) {
        if (inputSlot < 0 || inputSlot >= this.progressSlots.size()) {
            return 0.0F;
        }
        return Math.min(1.0F, this.progressSlots.get(inputSlot).get() / (float) PROGRESS_SCALE);
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
        if (index < this.slotCount) {
            if (!this.moveItemStackTo(stack, this.slotCount, this.slotCount + 36, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, 0, this.inputCount, false)) {
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
            return isFreezerBlock(block)
                    && player.distanceToSqr(
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D
            ) <= 64.0D;
        }, true);
    }

    private void addMachineSlots(final Container container) {
        for (int inputSlot = 0; inputSlot < this.inputCount; inputSlot++) {
            this.addSlot(new FreezerInputSlot(
                    container,
                    inputSlot,
                    SLOT_START_X + inputSlot * SLOT_SPACING,
                    INPUT_SLOT_Y
            ));
        }
        for (int inputSlot = 0; inputSlot < this.inputCount; inputSlot++) {
            this.addSlot(new FreezerOutputSlot(
                    container,
                    inputSlot + this.inputCount,
                    SLOT_START_X + inputSlot * SLOT_SPACING,
                    OUTPUT_SLOT_Y
            ));
        }
    }

    private static boolean isFreezerBlock(final Block block) {
        return block == ModBlocks.MINI_FREEZER.get()
                || block == ModBlocks.IRON_FREEZER.get()
                || block == ModBlocks.LIGHT_FREEZER.get();
    }

    private static DataSlot createValidMultiblockSlot(@Nullable final FreezerBlockEntity blockEntity) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return blockEntity.hasValidMultiblock() ? 1 : 0;
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private static DataSlot createProgressSlot(
            @Nullable final FreezerBlockEntity blockEntity,
            final int inputSlot
    ) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getProgressRatioScaled(inputSlot);
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private static FreezerClientData readClientData(
            final Inventory playerInventory,
            final FriendlyByteBuf buffer
    ) {
        final BlockPos pos = buffer.readBlockPos();
        final int slotCount = buffer.readVarInt();
        final int inputCount = buffer.readVarInt();
        final float speed = buffer.readFloat();
        final boolean requiresMultiblock = buffer.readBoolean();
        return new FreezerClientData(
                pos,
                null,
                ContainerLevelAccess.create(playerInventory.player.level(), pos),
                slotCount,
                inputCount,
                speed,
                requiresMultiblock
        );
    }

    private record FreezerClientData(
            BlockPos pos,
            @Nullable FreezerBlockEntity blockEntity,
            ContainerLevelAccess access,
            int slotCount,
            int inputCount,
            float speed,
            boolean requiresMultiblock
    ) {
    }

    private static class FreezerSlot extends Slot {
        FreezerSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }
    }

    private static final class FreezerInputSlot extends FreezerSlot {
        private FreezerInputSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }
    }

    private static final class FreezerOutputSlot extends FreezerSlot {
        private FreezerOutputSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return false;
        }
    }

    private static final class FreezerContainer implements Container {
        @Nullable
        private final FreezerBlockEntity blockEntity;
        private final NonNullList<ItemStack> localStacks;

        private FreezerContainer(@Nullable final FreezerBlockEntity blockEntity, final int slotCount) {
            this.blockEntity = blockEntity;
            this.localStacks = NonNullList.withSize(slotCount, ItemStack.EMPTY);
        }

        @Override
        public int getContainerSize() {
            return this.localStacks.size();
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
            if (this.blockEntity == null) {
                return slot >= 0 && slot < this.getContainerSize() / 2;
            }
            return this.blockEntity.mayPlaceInSlot(slot, stack);
        }

        @Override
        public void clearContent() {
            for (int slot = 0; slot < this.getContainerSize(); slot++) {
                this.setItem(slot, ItemStack.EMPTY);
            }
        }
    }
}
