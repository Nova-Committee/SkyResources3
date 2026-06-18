package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.common.block.entity.CrucibleInserterBlockEntity;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import committee.nova.mods.skyresources3.init.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class CrucibleInserterMenu extends SkyResourcesMenu {
    public static final int SLOT_X = 80;
    public static final int SLOT_Y = 53;
    private static final int PLAYER_INVENTORY_Y = 84;
    private static final int PLAYER_SLOT_START = CrucibleInserterBlockEntity.SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + 36;

    private final BlockPos blockPos;
    private final ContainerLevelAccess access;

    public CrucibleInserterMenu(
            final int containerId,
            final Inventory playerInventory,
            final RegistryFriendlyByteBuf data
    ) {
        this(containerId, playerInventory, readClientData(playerInventory, data));
    }

    public CrucibleInserterMenu(
            final int containerId,
            final Inventory playerInventory,
            final CrucibleInserterBlockEntity blockEntity
    ) {
        this(
                containerId,
                playerInventory,
                new CrucibleInserterClientData(
                        blockEntity.getBlockPos(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos())
                )
        );
    }

    private CrucibleInserterMenu(
            final int containerId,
            final Inventory playerInventory,
            final CrucibleInserterClientData data
    ) {
        super(ModMenuTypes.CRUCIBLE_INSERTER.get(), containerId);
        this.blockPos = data.pos();
        this.access = data.access();

        this.addSlot(new CrucibleInserterSlot(data.container(), 0, SLOT_X, SLOT_Y));
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
        if (index < CrucibleInserterBlockEntity.SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, 0, CrucibleInserterBlockEntity.SLOT_COUNT, false)) {
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
        return stillValid(this.access, player, ModBlocks.CRUCIBLE_INSERTER.get());
    }

    private static CrucibleInserterClientData readClientData(
            final Inventory playerInventory,
            final RegistryFriendlyByteBuf buffer
    ) {
        final BlockPos pos = buffer.readBlockPos();
        return new CrucibleInserterClientData(
                pos,
                new SimpleContainer(CrucibleInserterBlockEntity.SLOT_COUNT),
                ContainerLevelAccess.create(playerInventory.player.level(), pos)
        );
    }

    private record CrucibleInserterClientData(
            BlockPos pos,
            Container container,
            ContainerLevelAccess access
    ) {
    }

    private static final class CrucibleInserterSlot extends Slot {
        private CrucibleInserterSlot(final Container container, final int slot, final int x, final int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(final ItemStack stack) {
            return this.container.canPlaceItem(this.getContainerSlot(), stack);
        }
    }
}
