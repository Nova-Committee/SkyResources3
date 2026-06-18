package committee.nova.mods.skyresources3.common.block.entity;

import committee.nova.mods.skyresources3.common.item.HealthGemItem;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import committee.nova.mods.skyresources3.common.compat.ValueOutput;
import net.minecraft.world.phys.AABB;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemResource;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemStacksResourceHandler;

public final class LifeInjectorBlockEntity extends BlockEntity {
    public static final int GEM_SLOT = 0;
    public static final int SLOT_COUNT = 1;
    private static final String ITEMS_KEY = "items";
    private static final String GEM_KEY = "gem";
    private static final String COOLDOWN_KEY = "cooldown";
    private static final int TRANSFER_INTERVAL_TICKS = 60;
    private static final int MAX_HEALTH_PER_ENTITY = 2;

    private final LifeInjectorItemHandler items = new LifeInjectorItemHandler(this);
    private int cooldown;

    public LifeInjectorBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.LIFE_INJECTOR.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(final net.minecraft.nbt.CompoundTag tag, final net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        final ValueInput input = new ValueInput(tag, registries);
        input.readChild(ITEMS_KEY, this.items);
        if (this.items.stack(GEM_SLOT).isEmpty()) {
            input.read(GEM_KEY, ItemStack.OPTIONAL_CODEC)
                    .filter(stack -> !stack.isEmpty())
                    .ifPresent(stack -> this.items.setStack(GEM_SLOT, stack));
        }
        this.cooldown = input.getIntOr(COOLDOWN_KEY, 0);
    }

    @Override
    protected void saveAdditional(final net.minecraft.nbt.CompoundTag tag, final net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        final ValueOutput output = new ValueOutput(tag, registries);
        output.putChild(ITEMS_KEY, this.items);
        output.putInt(COOLDOWN_KEY, this.cooldown);
    }

    public boolean hasGem() {
        return !this.items.stack(GEM_SLOT).isEmpty();
    }

    public boolean canInsertGem(final ItemStack stack) {
        return this.items.stack(GEM_SLOT).isEmpty() && this.mayPlaceInSlot(GEM_SLOT, stack);
    }

    public void insertGem(final ItemStack stack) {
        this.items.setStack(GEM_SLOT, stack.copyWithCount(1));
    }

    public ItemStack removeGem() {
        return this.removeStackNoUpdate(GEM_SLOT);
    }

    public ItemStack getStackInSlot(final int slot) {
        return this.items.stack(slot);
    }

    public void setStackInSlot(final int slot, final ItemStack stack) {
        if (slot != GEM_SLOT) {
            return;
        }
        if (stack.isEmpty()) {
            this.items.setStack(GEM_SLOT, ItemStack.EMPTY);
            return;
        }
        if (this.mayPlaceInSlot(slot, stack)) {
            this.items.setStack(GEM_SLOT, stack.copyWithCount(1));
        }
    }

    public ItemStack removeStack(final int slot, final int amount) {
        if (slot != GEM_SLOT || amount <= 0) {
            return ItemStack.EMPTY;
        }
        final ItemStack stack = this.items.stack(GEM_SLOT);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = stack.split(amount);
        if (stack.isEmpty()) {
            this.items.setStack(GEM_SLOT, ItemStack.EMPTY);
        } else {
            this.setChanged();
        }
        return removed;
    }

    public ItemStack removeStackNoUpdate(final int slot) {
        if (slot != GEM_SLOT) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.items.stack(GEM_SLOT);
        this.items.setStack(GEM_SLOT, ItemStack.EMPTY);
        return removed;
    }

    public boolean mayPlaceInSlot(final int slot, final ItemStack stack) {
        return slot == GEM_SLOT && stack.getItem() instanceof HealthGemItem;
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return this.items;
    }

    public void serverTick(final ServerLevel level) {
        if (this.cooldown > 0) {
            this.cooldown--;
            return;
        }

        final AABB area = new AABB(
                this.worldPosition.getX(),
                this.worldPosition.getY() + 1,
                this.worldPosition.getZ(),
                this.worldPosition.getX() + 1,
                this.worldPosition.getY() + 2,
                this.worldPosition.getZ() + 1
        );
        boolean changed = false;
        for (final LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAlive)) {
            changed |= this.tryDrainEntity(level, entity);
        }

        this.cooldown = TRANSFER_INTERVAL_TICKS;
        if (changed) {
            this.setChanged();
        }
    }

    private boolean tryDrainEntity(final ServerLevel level, final LivingEntity entity) {
        if (entity.getMaxHealth() <= 0.0F) {
            return false;
        }

        final int health = Math.min((int) Math.floor(entity.getHealth()), MAX_HEALTH_PER_ENTITY);
        final ItemStack gem = this.items.stack(GEM_SLOT);
        if (!HealthGemItem.canReceiveHealth(gem, health)) {
            return false;
        }
        if (!entity.hurt(level.damageSources().generic(), health)) {
            return false;
        }
        return HealthGemItem.addStoredHealth(gem, health);
    }

    private static final class LifeInjectorItemHandler extends ItemStacksResourceHandler {
        private final LifeInjectorBlockEntity owner;

        private LifeInjectorItemHandler(final LifeInjectorBlockEntity owner) {
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
            return index == GEM_SLOT && this.owner.mayPlaceInSlot(index, resource.toStack());
        }

        @Override
        protected int getCapacity(final int index, final ItemResource resource) {
            return index == GEM_SLOT && !resource.isEmpty() ? 1 : 0;
        }

        @Override
        protected void onContentsChanged(final int index, final ItemStack previousContents) {
            this.owner.setChanged();
        }

        private ItemStack stack(final int slot) {
            return slot == GEM_SLOT ? this.stacks.get(GEM_SLOT) : ItemStack.EMPTY;
        }

        private void setStack(final int slot, final ItemStack stack) {
            if (slot != GEM_SLOT) {
                return;
            }
            this.stacks.set(GEM_SLOT, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
            this.owner.setChanged();
        }
    }
}
