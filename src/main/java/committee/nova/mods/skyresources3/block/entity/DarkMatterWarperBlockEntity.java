package committee.nova.mods.skyresources3.block.entity;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class DarkMatterWarperBlockEntity extends BlockEntity {
    public static final int SLOT_COUNT = 1;
    public static final int FUEL_SLOT = 0;
    private static final String ITEMS_KEY = "items";
    private static final String BURN_TIME_KEY = "burnTime";
    private static final int EFFECT_RANGE = 4;
    private static final int POWERED_EFFECT_TICKS = 360;
    private static final int POWERED_EFFECT_AMPLIFIER = 0;
    private static final int NO_FUEL_EFFECT_TICKS = 5;
    private static final int NO_FUEL_EFFECT_AMPLIFIER = 2;

    private final WarperItemHandler items = new WarperItemHandler(this);
    private int burnTime;

    public DarkMatterWarperBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.DARK_MATTER_WARPER.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        input.readChild(ITEMS_KEY, this.items);
        this.burnTime = Math.max(0, input.getIntOr(BURN_TIME_KEY, 0));
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(ITEMS_KEY, this.items);
        output.putInt(BURN_TIME_KEY, this.burnTime);
    }

    @Override
    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        this.dropContents();
    }

    public void serverTick(final ServerLevel level) {
        boolean changed = false;
        if (this.burnTime <= 0) {
            changed = this.consumeFuel();
        }

        if (this.burnTime > 0) {
            this.burnTime--;
            changed = true;
            for (final LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, this.effectBounds())) {
                changed |= applyPoweredEffect(level, entity);
            }
        } else if (Config.darkMatterWarperEffectNoFuel) {
            for (final Player player : level.getEntitiesOfClass(Player.class, this.effectBounds())) {
                if (!player.isCreative()) {
                    addBaseEffects(player, NO_FUEL_EFFECT_TICKS, NO_FUEL_EFFECT_AMPLIFIER);
                }
            }
        }

        if (changed) {
            this.setChanged();
        }
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return this.items;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public int getMaxBurnTime() {
        return Math.max(1, Config.darkMatterWarperFuelTime);
    }

    public ItemStack getStackInSlot(final int slot) {
        return slot == FUEL_SLOT ? this.items.stack(FUEL_SLOT) : ItemStack.EMPTY;
    }

    public void setStackInSlot(final int slot, final ItemStack stack) {
        if (slot != FUEL_SLOT) {
            return;
        }
        this.items.setStack(FUEL_SLOT, stack);
    }

    public ItemStack removeStack(final int slot, final int amount) {
        if (slot != FUEL_SLOT || amount <= 0) {
            return ItemStack.EMPTY;
        }
        final ItemStack stack = this.items.stack(FUEL_SLOT);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = stack.split(amount);
        if (stack.isEmpty()) {
            this.items.setStack(FUEL_SLOT, ItemStack.EMPTY);
        }
        this.setChanged();
        return removed;
    }

    public ItemStack removeStackNoUpdate(final int slot) {
        if (slot != FUEL_SLOT) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.items.stack(FUEL_SLOT);
        this.items.setStack(FUEL_SLOT, ItemStack.EMPTY);
        return removed;
    }

    public boolean mayPlaceInSlot(final int slot, final ItemStack stack) {
        return slot == FUEL_SLOT && isFuel(stack);
    }

    public void dropContents() {
        if (this.level == null) {
            return;
        }
        Containers.dropContents(this.level, this.worldPosition, new SimpleContainer(this.items.stack(FUEL_SLOT).copy()));
        this.items.setStack(FUEL_SLOT, ItemStack.EMPTY);
    }

    public static boolean isFuel(final ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModItems.DARK_MATTER.get());
    }

    private boolean consumeFuel() {
        final ItemStack fuel = this.items.stack(FUEL_SLOT);
        if (!isFuel(fuel)) {
            return false;
        }
        fuel.shrink(1);
        this.items.setStack(FUEL_SLOT, fuel);
        this.burnTime = this.getMaxBurnTime();
        return true;
    }

    private AABB effectBounds() {
        return new AABB(this.worldPosition).inflate(EFFECT_RANGE);
    }

    private static boolean applyPoweredEffect(final ServerLevel level, final LivingEntity entity) {
        if (!entity.isAlive()) {
            return false;
        }
        if (entity instanceof Skeleton) {
            return replaceWith(level, entity, EntityType.WITHER_SKELETON, true);
        }
        if (entity instanceof Spider && !(entity instanceof CaveSpider)) {
            return replaceWith(level, entity, EntityType.CAVE_SPIDER, false);
        }
        if (entity instanceof Squid) {
            return replaceWith(level, entity, EntityType.BLAZE, false);
        }
        if (entity instanceof ZombieVillager) {
            final EntityType<? extends Mob> villagerConversion = level.random.nextFloat() < 0.25F
                    ? EntityType.EVOKER
                    : EntityType.VINDICATOR;
            return replaceWith(level, entity, villagerConversion, false);
        }
        if (entity instanceof Player player) {
            if (Config.darkMatterWarperEffectPlayers && !player.isCreative()) {
                addBaseEffects(player, POWERED_EFFECT_TICKS, POWERED_EFFECT_AMPLIFIER);
            }
            return false;
        }
        if (entity instanceof Animal animal) {
            addBaseEffects(animal, POWERED_EFFECT_TICKS, POWERED_EFFECT_AMPLIFIER);
        }
        return false;
    }

    private static <T extends Mob> boolean replaceWith(
            final ServerLevel level,
            final LivingEntity source,
            final EntityType<T> entityType,
            final boolean copyEquipment
    ) {
        final T replacement = entityType.create(level, EntitySpawnReason.CONVERSION);
        if (replacement == null) {
            return false;
        }
        replacement.setPos(source.getX(), source.getY(), source.getZ());
        replacement.setYRot(source.getYRot());
        replacement.setXRot(source.getXRot());
        replacement.finalizeSpawn(
                level,
                level.getCurrentDifficultyAt(replacement.blockPosition()),
                EntitySpawnReason.CONVERSION,
                null
        );
        replacement.setHealth(replacement.getMaxHealth());
        if (copyEquipment) {
            copyEquipment(source, replacement);
        }
        if (!level.addFreshEntity(replacement)) {
            return false;
        }
        source.discard();
        return true;
    }

    private static void copyEquipment(final LivingEntity source, final LivingEntity target) {
        for (final EquipmentSlot slot : EquipmentSlot.VALUES) {
            if (slot == EquipmentSlot.BODY) {
                continue;
            }
            target.setItemSlot(slot, source.getItemBySlot(slot).copy());
        }
    }

    private static void addBaseEffects(final LivingEntity entity, final int ticks, final int amplifier) {
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, ticks, amplifier));
        entity.addEffect(new MobEffectInstance(MobEffects.WITHER, ticks, amplifier));
        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, ticks, amplifier));
        entity.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, ticks, amplifier));
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, ticks, amplifier));
        entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, ticks, amplifier));
    }

    private static final class WarperItemHandler extends ItemStacksResourceHandler {
        private final DarkMatterWarperBlockEntity owner;

        private WarperItemHandler(final DarkMatterWarperBlockEntity owner) {
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
            return index == FUEL_SLOT && isFuel(resource.toStack());
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
            return slot == FUEL_SLOT ? this.stacks.get(FUEL_SLOT) : ItemStack.EMPTY;
        }

        private void setStack(final int slot, final ItemStack stack) {
            if (slot != FUEL_SLOT) {
                return;
            }
            this.stacks.set(FUEL_SLOT, isFuel(stack) ? stack.copy() : ItemStack.EMPTY);
            this.owner.setChanged();
        }
    }
}
