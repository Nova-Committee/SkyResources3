package committee.nova.mods.skyresources3.block.entity;

import committee.nova.mods.skyresources3.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class CrucibleInserterBlockEntity extends BlockEntity implements Container {
    public static final int SLOT_COUNT = 1;
    private static final String ITEMS_KEY = "items";
    private static final int SLOT = 0;

    private final CrucibleInserterItemHandler items = new CrucibleInserterItemHandler(this);

    public CrucibleInserterBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.CRUCIBLE_INSERTER.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        input.readChild(ITEMS_KEY, this.items);
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(ITEMS_KEY, this.items);
    }

    @Override
    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        this.dropContents();
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return this.items;
    }

    public void dropContents() {
        if (this.level == null) {
            return;
        }
        Containers.dropContents(this.level, this.worldPosition, new SimpleContainer(this.getItem(SLOT).copy()));
        this.setItem(SLOT, ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        return this.getItem(SLOT).isEmpty();
    }

    @Override
    public ItemStack getItem(final int slot) {
        return slot == SLOT ? this.items.stack(SLOT) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(final int slot, final int amount) {
        if (slot != SLOT || amount <= 0) {
            return ItemStack.EMPTY;
        }
        final ItemStack stack = this.items.stack(SLOT);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = stack.split(amount);
        if (stack.isEmpty()) {
            this.items.setStack(SLOT, ItemStack.EMPTY);
        }
        this.setChanged();
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(final int slot) {
        if (slot != SLOT) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.items.stack(SLOT);
        this.items.setStack(SLOT, ItemStack.EMPTY);
        return removed;
    }

    @Override
    public void setItem(final int slot, final ItemStack stack) {
        this.items.setStack(slot, stack);
    }

    @Override
    public boolean stillValid(final Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(final int slot, final ItemStack stack) {
        return slot == SLOT && !stack.isEmpty();
    }

    @Override
    public void clearContent() {
        this.setItem(SLOT, ItemStack.EMPTY);
    }

    private static final class CrucibleInserterItemHandler extends ItemStacksResourceHandler {
        private final CrucibleInserterBlockEntity owner;

        private CrucibleInserterItemHandler(final CrucibleInserterBlockEntity owner) {
            super(SLOT_COUNT);
            this.owner = owner;
        }

        @Override
        public void deserialize(final ValueInput input) {
            super.deserialize(input);
            if (this.size() != SLOT_COUNT) {
                this.setStacks(NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY));
            }
        }

        @Override
        public boolean isValid(final int index, final ItemResource resource) {
            return index == SLOT && !resource.isEmpty();
        }

        @Override
        public int extract(
                final int index,
                final ItemResource resource,
                final int amount,
                final TransactionContext transaction
        ) {
            return 0;
        }

        @Override
        protected void onContentsChanged(final int index, final ItemStack previousContents) {
            this.owner.setChanged();
        }

        private ItemStack stack(final int slot) {
            return slot == SLOT ? this.stacks.get(SLOT) : ItemStack.EMPTY;
        }

        private void setStack(final int slot, final ItemStack stack) {
            if (slot != SLOT) {
                return;
            }
            this.stacks.set(SLOT, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
            this.owner.setChanged();
        }
    }
}
