package committee.nova.mods.skyresources3.block.entity;

import committee.nova.mods.skyresources3.item.HealthGemItem;
import committee.nova.mods.skyresources3.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

public final class LifeInjectorBlockEntity extends BlockEntity {
    public static final int GEM_SLOT = 0;
    public static final int SLOT_COUNT = 1;
    private static final String GEM_KEY = "gem";
    private static final String COOLDOWN_KEY = "cooldown";
    private static final int TRANSFER_INTERVAL_TICKS = 60;
    private static final int MAX_HEALTH_PER_ENTITY = 2;

    private ItemStack gem = ItemStack.EMPTY;
    private int cooldown;

    public LifeInjectorBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.LIFE_INJECTOR.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        this.gem = input.read(GEM_KEY, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.cooldown = input.getIntOr(COOLDOWN_KEY, 0);
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.store(GEM_KEY, ItemStack.OPTIONAL_CODEC, this.gem);
        output.putInt(COOLDOWN_KEY, this.cooldown);
    }

    public boolean hasGem() {
        return !this.gem.isEmpty();
    }

    public boolean canInsertGem(final ItemStack stack) {
        return this.gem.isEmpty() && stack.getItem() instanceof HealthGemItem;
    }

    public void insertGem(final ItemStack stack) {
        this.gem = stack.copyWithCount(1);
        this.setChanged();
    }

    public ItemStack removeGem() {
        final ItemStack removed = this.gem;
        this.gem = ItemStack.EMPTY;
        this.setChanged();
        return removed;
    }

    public ItemStack getStackInSlot(final int slot) {
        return slot == GEM_SLOT ? this.gem : ItemStack.EMPTY;
    }

    public void setStackInSlot(final int slot, final ItemStack stack) {
        if (slot != GEM_SLOT) {
            return;
        }
        if (stack.isEmpty()) {
            this.gem = ItemStack.EMPTY;
            this.setChanged();
            return;
        }
        if (stack.getItem() instanceof HealthGemItem) {
            this.gem = stack.copyWithCount(1);
            this.setChanged();
        }
    }

    public ItemStack removeStack(final int slot, final int amount) {
        if (slot != GEM_SLOT || amount <= 0 || this.gem.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.gem.split(amount);
        if (this.gem.isEmpty()) {
            this.gem = ItemStack.EMPTY;
        }
        this.setChanged();
        return removed;
    }

    public ItemStack removeStackNoUpdate(final int slot) {
        if (slot != GEM_SLOT) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.gem;
        this.gem = ItemStack.EMPTY;
        this.setChanged();
        return removed;
    }

    public boolean mayPlaceInSlot(final int slot, final ItemStack stack) {
        return slot == GEM_SLOT && stack.getItem() instanceof HealthGemItem;
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
        if (!HealthGemItem.canReceiveHealth(this.gem, health)) {
            return false;
        }
        if (!entity.hurtServer(level, level.damageSources().generic(), health)) {
            return false;
        }
        return HealthGemItem.addStoredHealth(this.gem, health);
    }
}
