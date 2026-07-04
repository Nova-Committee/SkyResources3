package committee.nova.mods.skyresources3.common.block.entity;

import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import committee.nova.mods.skyresources3.common.compat.ValueOutput;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemResource;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemStacksResourceHandler;

public final class QuickDropperBlockEntity extends BlockEntity {
    public static final int SLOT_COUNT = 1;
    private static final String ITEMS_KEY = "items";
    private static final int SLOT = 0;

    private final QuickDropperItemHandler items = new QuickDropperItemHandler(this);

    public QuickDropperBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.QUICK_DROPPER.get(), pos, blockState);
    }

    @Override
    public void load(final net.minecraft.nbt.CompoundTag tag) {
        super.load(tag);
        final ValueInput input = new ValueInput(tag);
        input.readChild(ITEMS_KEY, this.items);
    }

    @Override
    protected void saveAdditional(final net.minecraft.nbt.CompoundTag tag) {
        super.saveAdditional(tag);
        final ValueOutput output = new ValueOutput(tag);
        output.putChild(ITEMS_KEY, this.items);
    }

    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        this.dropContents();
    }

    public void serverTick(final ServerLevel level) {
        final ItemStack stack = this.items.stack(SLOT);
        if (stack.isEmpty() || level.hasNeighborSignal(this.worldPosition) || this.isBlockedBelow(level)) {
            return;
        }

        final BlockPos below = this.worldPosition.below();
        final ItemEntity entity = new ItemEntity(
                level,
                below.getX() + 0.5D,
                below.getY() + 0.5D,
                below.getZ() + 0.5D,
                stack.copy()
        );
        entity.setDeltaMovement(0.0D, 0.0D, 0.0D);
        if (level.addFreshEntity(entity)) {
            this.items.setStack(SLOT, ItemStack.EMPTY);
        }
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return this.items;
    }

    public ItemStack getStackInSlot(final int slot) {
        return slot == SLOT ? this.items.stack(SLOT) : ItemStack.EMPTY;
    }

    public void setStackInSlot(final int slot, final ItemStack stack) {
        if (slot != SLOT) {
            return;
        }
        this.items.setStack(SLOT, stack);
    }

    public ItemStack removeStack(final int slot, final int amount) {
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

    public ItemStack removeStackNoUpdate(final int slot) {
        if (slot != SLOT) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.items.stack(SLOT);
        this.items.setStack(SLOT, ItemStack.EMPTY);
        return removed;
    }

    public boolean mayPlaceInSlot(final int slot, final ItemStack stack) {
        return slot == SLOT && !stack.isEmpty();
    }

    public void dropContents() {
        if (this.level == null) {
            return;
        }
        Containers.dropContents(this.level, this.worldPosition, new SimpleContainer(this.items.stack(SLOT).copy()));
        this.items.setStack(SLOT, ItemStack.EMPTY);
    }

    private boolean isBlockedBelow(final ServerLevel level) {
        final BlockPos below = this.worldPosition.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    private static final class QuickDropperItemHandler extends ItemStacksResourceHandler {
        private final QuickDropperBlockEntity owner;

        private QuickDropperItemHandler(final QuickDropperBlockEntity owner) {
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
