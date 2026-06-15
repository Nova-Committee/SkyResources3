package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.common.block.entity.WildlifeAttractorBlockEntity;
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

public final class WildlifeAttractorMenu extends AbstractContainerMenu {
    public static final int SLOT_X = 80;
    public static final int SLOT_Y = 59;
    private static final int PLAYER_INVENTORY_Y = 107;
    private static final int PLAYER_SLOT_START = WildlifeAttractorBlockEntity.SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;

    private final BlockPos blockPos;
    private final ContainerLevelAccess access;
    private final DataSlot matterLeft;
    private final DataSlot energyLow;
    private final DataSlot energyHigh;
    private final DataSlot water;

    public WildlifeAttractorMenu(
            final int containerId,
            final Inventory playerInventory,
            final RegistryFriendlyByteBuf data
    ) {
        this(containerId, playerInventory, readClientData(playerInventory, data));
    }

    public WildlifeAttractorMenu(
            final int containerId,
            final Inventory playerInventory,
            final WildlifeAttractorBlockEntity blockEntity
    ) {
        this(
                containerId,
                playerInventory,
                new ClientData(
                        blockEntity.getBlockPos(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos())
                )
        );
    }

    private WildlifeAttractorMenu(final int containerId, final Inventory playerInventory, final ClientData data) {
        super(ModMenuTypes.WILDLIFE_ATTRACTOR.get(), containerId);
        this.blockPos = data.pos();
        this.access = data.access();

        this.addSlot(new MatterSlot(new WildlifeContainer(data.blockEntity()), 0, SLOT_X, SLOT_Y));
        this.addStandardInventorySlots(playerInventory, 8, PLAYER_INVENTORY_Y);

        this.matterLeft = this.addDataSlot(createMatterSlot(data.blockEntity()));
        this.energyLow = this.addDataSlot(createEnergySlot(data.blockEntity(), false));
        this.energyHigh = this.addDataSlot(createEnergySlot(data.blockEntity(), true));
        this.water = this.addDataSlot(createWaterSlot(data.blockEntity()));
    }

    public static void writeClientSideData(final RegistryFriendlyByteBuf buffer, final BlockPos pos) {
        buffer.writeBlockPos(pos);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public int getMatterLeft() {
        return this.matterLeft.get();
    }

    public int getMaxMatterLeft() {
        return Math.max(1, Config.wildlifeAttractorMatterTime);
    }

    public float getMatterRatio() {
        return Math.min(1.0F, this.getMatterLeft() / (float) this.getMaxMatterLeft());
    }

    public int getEnergyStored() {
        return (this.energyHigh.get() << 16) | (this.energyLow.get() & 0xFFFF);
    }

    public int getMaxEnergyStored() {
        return WildlifeAttractorBlockEntity.ENERGY_CAPACITY;
    }

    public float getEnergyRatio() {
        return Math.min(1.0F, this.getEnergyStored() / (float) this.getMaxEnergyStored());
    }

    public int getWaterStored() {
        return this.water.get();
    }

    public int getMaxWaterStored() {
        return Math.max(1, Config.wildlifeAttractorWaterCapacity);
    }

    public float getWaterRatio() {
        return Math.min(1.0F, this.getWaterStored() / (float) this.getMaxWaterStored());
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
        if (index < WildlifeAttractorBlockEntity.SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (WildlifeAttractorBlockEntity.isPlantMatter(stack)
                && !this.moveItemStackTo(stack, 0, WildlifeAttractorBlockEntity.SLOT_COUNT, false)) {
            return ItemStack.EMPTY;
        } else if (!WildlifeAttractorBlockEntity.isPlantMatter(stack)) {
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
        return stillValid(this.access, player, ModBlocks.WILDLIFE_ATTRACTOR.get());
    }

    private static ClientData readClientData(final Inventory playerInventory, final RegistryFriendlyByteBuf buffer) {
        final BlockPos pos = buffer.readBlockPos();
        return new ClientData(
                pos,
                null,
                ContainerLevelAccess.create(playerInventory.player.level(), pos)
        );
    }

    private static DataSlot createMatterSlot(@Nullable final WildlifeAttractorBlockEntity blockEntity) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getMatterLeft();
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private static DataSlot createEnergySlot(
            @Nullable final WildlifeAttractorBlockEntity blockEntity,
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

    private static DataSlot createWaterSlot(@Nullable final WildlifeAttractorBlockEntity blockEntity) {
        if (blockEntity == null) {
            return DataSlot.standalone();
        }
        return new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getWaterStored();
            }

            @Override
            public void set(final int value) {
            }
        };
    }

    private record ClientData(
            BlockPos pos,
            @Nullable WildlifeAttractorBlockEntity blockEntity,
            ContainerLevelAccess access
    ) {
    }

    private static final class MatterSlot extends Slot {
        private MatterSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }
    }

    private static final class WildlifeContainer implements Container {
        @Nullable
        private final WildlifeAttractorBlockEntity blockEntity;
        private final NonNullList<ItemStack> localStacks =
                NonNullList.withSize(WildlifeAttractorBlockEntity.SLOT_COUNT, ItemStack.EMPTY);

        private WildlifeContainer(@Nullable final WildlifeAttractorBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public int getContainerSize() {
            return WildlifeAttractorBlockEntity.SLOT_COUNT;
        }

        @Override
        public boolean isEmpty() {
            return this.getItem(WildlifeAttractorBlockEntity.MATTER_SLOT).isEmpty();
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
                this.localStacks.set(
                        slot,
                        WildlifeAttractorBlockEntity.isPlantMatter(stack) ? stack.copy() : ItemStack.EMPTY
                );
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
                    ? slot == WildlifeAttractorBlockEntity.MATTER_SLOT
                    && WildlifeAttractorBlockEntity.isPlantMatter(stack)
                    : this.blockEntity.mayPlaceInSlot(slot, stack);
        }

        @Override
        public void clearContent() {
            this.setItem(WildlifeAttractorBlockEntity.MATTER_SLOT, ItemStack.EMPTY);
        }
    }
}
