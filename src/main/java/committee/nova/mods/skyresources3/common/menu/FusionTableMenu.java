package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.common.block.entity.FusionTableBlockEntity;
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

public final class FusionTableMenu extends SkyResourcesMenu {
    private static final int MACHINE_SLOT_COUNT = FusionTableBlockEntity.SLOT_COUNT;
    private static final int PLAYER_SLOT_START = MACHINE_SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;
    private static final int DATA_SCALE = 1_000;

    private final BlockPos blockPos;
    private final ContainerLevelAccess access;
    private final NonNullList<ItemStack> filters;
    private final DataSlot progress;
    private final DataSlot currentYield;
    private final DataSlot catalystYieldPercent;
    private final DataSlot catalystLeft;

    public FusionTableMenu(final int containerId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(containerId, playerInventory, readClientData(playerInventory, data));
    }

    public FusionTableMenu(
            final int containerId,
            final Inventory playerInventory,
            final FusionTableBlockEntity blockEntity
    ) {
        this(
                containerId,
                playerInventory,
                new FusionTableClientData(
                        blockEntity.getBlockPos(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos()),
                        copyFilters(blockEntity)
                )
        );
    }

    private FusionTableMenu(
            final int containerId,
            final Inventory playerInventory,
            final FusionTableClientData data
    ) {
        super(ModMenuTypes.FUSION_TABLE.get(), containerId);
        this.blockPos = data.pos();
        this.access = data.access();
        this.filters = data.filters();

        final FusionTableContainer container = new FusionTableContainer(data.blockEntity());
        this.addMachineSlots(container);
        this.addStandardInventorySlots(playerInventory, 8, 99);

        this.progress = this.addDataSlot(createDataSlot(data.blockEntity(), 0));
        this.currentYield = this.addDataSlot(createDataSlot(data.blockEntity(), 1));
        this.catalystYieldPercent = this.addDataSlot(createDataSlot(data.blockEntity(), 2));
        this.catalystLeft = this.addDataSlot(createDataSlot(data.blockEntity(), 3));
    }

    public static void writeClientSideData(
            final FriendlyByteBuf buffer,
            final BlockPos pos,
            final FusionTableBlockEntity blockEntity
    ) {
        buffer.writeBlockPos(pos);
        for (int index = 0; index < FusionTableBlockEntity.INPUT_SLOT_COUNT; index++) {
            buffer.writeItem(blockEntity.getFilterStack(index));
        }
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public ItemStack getFilterStack(final int index) {
        if (index < 0 || index >= this.filters.size()) {
            return ItemStack.EMPTY;
        }
        return this.filters.get(index);
    }

    public float getProgressRatio() {
        return Math.min(1.0F, this.progress.get() / (float) FusionTableBlockEntity.MAX_PROGRESS);
    }

    public float getCurrentYieldRatio() {
        return Math.min(1.0F, this.currentYield.get() / (float) DATA_SCALE);
    }

    public int getCatalystYieldPercent() {
        return this.catalystYieldPercent.get();
    }

    public float getCatalystLeftRatio() {
        return Math.min(1.0F, this.catalystLeft.get() / (float) DATA_SCALE);
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
        if (index < MACHINE_SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (FusionTableBlockEntity.isCatalyst(stack)) {
            if (!this.moveItemStackTo(
                    stack,
                    FusionTableBlockEntity.CATALYST_SLOT,
                    FusionTableBlockEntity.CATALYST_SLOT + 1,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(
                stack,
                FusionTableBlockEntity.FIRST_INPUT_SLOT,
                FusionTableBlockEntity.OUTPUT_SLOT,
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
        return stillValid(this.access, player, ModBlocks.FUSION_TABLE.get());
    }

    private void addMachineSlots(final Container container) {
        this.addSlot(new FusionTableSlot(container, FusionTableBlockEntity.CATALYST_SLOT, 116, 74));
        for (int index = 0; index < FusionTableBlockEntity.INPUT_SLOT_COUNT; index++) {
            this.addSlot(new FusionInputSlot(
                    container,
                    FusionTableBlockEntity.FIRST_INPUT_SLOT + index,
                    8 + index * 18,
                    34,
                    index
            ));
        }
        this.addSlot(new FusionOutputSlot(container, FusionTableBlockEntity.OUTPUT_SLOT, 80, 74));
    }

    private void setFilter(final int index, final ItemStack stack) {
        if (index < 0 || index >= this.filters.size()) {
            return;
        }
        this.filters.set(index, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
    }

    private static DataSlot createDataSlot(@Nullable final FusionTableBlockEntity blockEntity, final int index) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return switch (index) {
                    case 0 -> blockEntity.getProgress();
                    case 1 -> scale(blockEntity.getCurrentYield(), DATA_SCALE);
                    case 2 -> scale(blockEntity.getCurrentCatalystYield(), 100);
                    case 3 -> scale(blockEntity.getCurrentCatalystLeft(), DATA_SCALE);
                    default -> 0;
                };
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private static int scale(final double value, final int scale) {
        if (value <= 0.0D) {
            return 0;
        }
        return Math.min(Short.MAX_VALUE, (int) Math.round(value * scale));
    }

    private static FusionTableClientData readClientData(
            final Inventory playerInventory,
            final FriendlyByteBuf buffer
    ) {
        final BlockPos pos = buffer.readBlockPos();
        final NonNullList<ItemStack> filters = NonNullList.withSize(
                FusionTableBlockEntity.INPUT_SLOT_COUNT,
                ItemStack.EMPTY
        );
        for (int index = 0; index < filters.size(); index++) {
            filters.set(index, buffer.readItem());
        }
        return new FusionTableClientData(
                pos,
                null,
                ContainerLevelAccess.create(playerInventory.player.level(), pos),
                filters
        );
    }

    private static NonNullList<ItemStack> copyFilters(final FusionTableBlockEntity blockEntity) {
        final NonNullList<ItemStack> filters = NonNullList.withSize(
                FusionTableBlockEntity.INPUT_SLOT_COUNT,
                ItemStack.EMPTY
        );
        for (int index = 0; index < filters.size(); index++) {
            filters.set(index, blockEntity.getFilterStack(index));
        }
        return filters;
    }

    private record FusionTableClientData(
            BlockPos pos,
            @Nullable FusionTableBlockEntity blockEntity,
            ContainerLevelAccess access,
            NonNullList<ItemStack> filters
    ) {
    }

    private final class FusionInputSlot extends FusionTableSlot {
        private final int filterIndex;

        private FusionInputSlot(
                final Container container,
                final int slot,
                final int x,
                final int y,
                final int filterIndex
        ) {
            super(container, slot, x, y);
            this.filterIndex = filterIndex;
        }

        @Override
        public void setByPlayer(final ItemStack newStack) {
            FusionTableMenu.this.setFilter(this.filterIndex, newStack);
            super.setByPlayer(newStack);
        }

        @Override
        public ItemStack remove(final int amount) {
            final ItemStack removed = super.remove(amount);
            if (this.getItem().isEmpty()) {
                FusionTableMenu.this.setFilter(this.filterIndex, ItemStack.EMPTY);
            }
            return removed;
        }
    }

    private static class FusionTableSlot extends Slot {
        FusionTableSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }
    }

    private static final class FusionOutputSlot extends FusionTableSlot {
        private FusionOutputSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return false;
        }
    }

    private static final class FusionTableContainer implements Container {
        @Nullable
        private final FusionTableBlockEntity blockEntity;
        private final NonNullList<ItemStack> localStacks = NonNullList.withSize(
                FusionTableBlockEntity.SLOT_COUNT,
                ItemStack.EMPTY
        );

        private FusionTableContainer(@Nullable final FusionTableBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public int getContainerSize() {
            return FusionTableBlockEntity.SLOT_COUNT;
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
                    ? slot == FusionTableBlockEntity.CATALYST_SLOT && FusionTableBlockEntity.isCatalyst(stack)
                    || slot >= FusionTableBlockEntity.FIRST_INPUT_SLOT && slot < FusionTableBlockEntity.OUTPUT_SLOT
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
