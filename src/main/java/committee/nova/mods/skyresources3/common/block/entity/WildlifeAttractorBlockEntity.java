package committee.nova.mods.skyresources3.common.block.entity;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class WildlifeAttractorBlockEntity extends BlockEntity {
    public static final int MATTER_SLOT = 0;
    public static final int SLOT_COUNT = 1;
    public static final int ENERGY_CAPACITY = 100_000;
    private static final int DEFAULT_WATER_CAPACITY = 4_000;
    private static final int TANK = 0;
    private static final int MAX_ENERGY_INSERT = 2_000;
    private static final int MAX_ENERGY_EXTRACT = 0;
    private static final int SPAWN_CHANCE = 600;
    private static final FluidResource WATER = FluidResource.of(Fluids.WATER);
    private static final String ITEMS_KEY = "items";
    private static final String ENERGY_KEY = "energy";
    private static final String FLUIDS_KEY = "fluids";
    private static final String MATTER_LEFT_KEY = "matter_left";

    private final WildlifeItemHandler items = new WildlifeItemHandler(this);
    private final WildlifeEnergyHandler energy = new WildlifeEnergyHandler(this);
    private final WildlifeFluidHandler fluids = new WildlifeFluidHandler(this);
    private int matterLeft;

    public WildlifeAttractorBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.WILDLIFE_ATTRACTOR.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        input.readChild(ITEMS_KEY, this.items);
        input.readChild(ENERGY_KEY, this.energy);
        input.readChild(FLUIDS_KEY, this.fluids);
        this.matterLeft = input.getIntOr(MATTER_LEFT_KEY, 0);
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(ITEMS_KEY, this.items);
        output.putChild(ENERGY_KEY, this.energy);
        output.putChild(FLUIDS_KEY, this.fluids);
        output.putInt(MATTER_LEFT_KEY, this.matterLeft);
    }

    @Override
    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        this.dropContents();
    }

    public void serverTick(final ServerLevel level) {
        if (level.hasNeighborSignal(this.worldPosition)) {
            return;
        }

        this.refuelMatter();
        final int waterUsage = Math.max(0, Config.wildlifeAttractorWaterUsage);
        final int powerUsage = Math.max(0, Config.wildlifeAttractorPowerUsage);
        if (this.matterLeft <= 0
                || !this.hasEnoughWater(waterUsage)
                || this.energy.getAmountAsInt() < powerUsage) {
            return;
        }

        this.spawnRandomAnimal(level);
        this.drainWater(waterUsage);
        this.consumeEnergy(powerUsage);
        this.matterLeft--;
        this.setChanged();
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return this.items;
    }

    public EnergyHandler getEnergyHandler() {
        return this.energy;
    }

    public ResourceHandler<FluidResource> getFluidHandler() {
        return this.fluids;
    }

    public ItemStack getStackInSlot(final int slot) {
        return slot == MATTER_SLOT ? this.items.stack(MATTER_SLOT) : ItemStack.EMPTY;
    }

    public void setStackInSlot(final int slot, final ItemStack stack) {
        if (slot == MATTER_SLOT) {
            this.items.setStack(MATTER_SLOT, stack);
        }
    }

    public ItemStack removeStack(final int slot, final int amount) {
        if (slot != MATTER_SLOT || amount <= 0) {
            return ItemStack.EMPTY;
        }
        final ItemStack stack = this.items.stack(MATTER_SLOT);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = stack.split(amount);
        if (stack.isEmpty()) {
            this.items.setStack(MATTER_SLOT, ItemStack.EMPTY);
        }
        this.setChanged();
        return removed;
    }

    public ItemStack removeStackNoUpdate(final int slot) {
        if (slot != MATTER_SLOT) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.items.stack(MATTER_SLOT);
        this.items.setStack(MATTER_SLOT, ItemStack.EMPTY);
        return removed;
    }

    public boolean mayPlaceInSlot(final int slot, final ItemStack stack) {
        return slot == MATTER_SLOT && isPlantMatter(stack);
    }

    public int getMatterLeft() {
        return Math.max(0, this.matterLeft);
    }

    public int getEnergyStored() {
        return this.energy.getAmountAsInt();
    }

    public int getWaterStored() {
        return this.fluids.getAmountAsInt(TANK);
    }

    public int getMaxWaterStored() {
        return Math.max(1, Config.wildlifeAttractorWaterCapacity);
    }

    public void dropContents() {
        if (this.level == null) {
            return;
        }
        Containers.dropContents(this.level, this.worldPosition, new SimpleContainer(this.items.stack(MATTER_SLOT).copy()));
        this.items.setStack(MATTER_SLOT, ItemStack.EMPTY);
    }

    public static boolean isPlantMatter(final ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModItems.PLANT_MATTER.get());
    }

    private void refuelMatter() {
        if (this.matterLeft > 0) {
            return;
        }
        final ItemStack stack = this.items.stack(MATTER_SLOT);
        if (!isPlantMatter(stack)) {
            return;
        }
        stack.shrink(1);
        if (stack.isEmpty()) {
            this.items.setStack(MATTER_SLOT, ItemStack.EMPTY);
        }
        this.matterLeft = Math.max(0, Config.wildlifeAttractorMatterTime);
        this.setChanged();
    }

    private boolean hasEnoughWater(final int amount) {
        return amount <= 0
                || (this.fluids.getResource(TANK).equals(WATER)
                && this.fluids.getAmountAsInt(TANK) >= amount);
    }

    private void drainWater(final int amount) {
        if (amount <= 0) {
            return;
        }
        final int remaining = this.fluids.getAmountAsInt(TANK) - amount;
        if (remaining <= 0) {
            this.fluids.set(TANK, FluidResource.EMPTY, 0);
            return;
        }
        this.fluids.set(TANK, WATER, remaining);
    }

    private void consumeEnergy(final int amount) {
        if (amount > 0) {
            this.energy.set(this.energy.getAmountAsInt() - amount);
        }
    }

    private void spawnRandomAnimal(final ServerLevel level) {
        final List<String> animals = Config.wildlifeAttractorAnimalIds;
        if (animals.isEmpty() || level.random.nextInt(SPAWN_CHANCE) != 0) {
            return;
        }

        final String id = animals.get(level.random.nextInt(animals.size()));
        final EntityType<?> entityType;
        try {
            entityType = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(id));
        } catch (final IllegalArgumentException ignored) {
            return;
        }
        if (entityType == null) {
            return;
        }

        final Entity entity = entityType.create(level, EntitySpawnReason.TRIGGERED);
        if (!(entity instanceof Mob mob)) {
            return;
        }
        mob.setPos(
                this.worldPosition.getX() + 0.5D,
                this.worldPosition.getY() + 1.0D,
                this.worldPosition.getZ() + 0.5D
        );
        mob.setYRot(level.random.nextFloat() * 360.0F);
        mob.finalizeSpawn(
                level,
                level.getCurrentDifficultyAt(mob.blockPosition()),
                EntitySpawnReason.TRIGGERED,
                null
        );
        if (level.noCollision(mob)) {
            level.addFreshEntity(mob);
        }
    }

    private static final class WildlifeItemHandler extends ItemStacksResourceHandler {
        private final WildlifeAttractorBlockEntity owner;

        private WildlifeItemHandler(final WildlifeAttractorBlockEntity owner) {
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
            return index == MATTER_SLOT && isPlantMatter(resource.toStack());
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
            return slot == MATTER_SLOT ? this.stacks.get(MATTER_SLOT) : ItemStack.EMPTY;
        }

        private void setStack(final int slot, final ItemStack stack) {
            if (slot != MATTER_SLOT) {
                return;
            }
            this.stacks.set(MATTER_SLOT, isPlantMatter(stack) ? stack.copy() : ItemStack.EMPTY);
            this.owner.setChanged();
        }
    }

    private static final class WildlifeFluidHandler extends FluidStacksResourceHandler {
        private final WildlifeAttractorBlockEntity owner;

        private WildlifeFluidHandler(final WildlifeAttractorBlockEntity owner) {
            super(1, DEFAULT_WATER_CAPACITY);
            this.owner = owner;
        }

        @Override
        public void deserialize(final ValueInput input) {
            super.deserialize(input);
            if (this.size() != 1) {
                this.setStacks(NonNullList.withSize(1, FluidStack.EMPTY));
            }
        }

        @Override
        public boolean isValid(final int index, final FluidResource resource) {
            return index == TANK && !resource.isEmpty() && resource.getFluid() == Fluids.WATER;
        }

        @Override
        protected int getCapacity(final int index, final FluidResource resource) {
            return this.owner.getMaxWaterStored();
        }

        @Override
        public int extract(
                final int index,
                final FluidResource resource,
                final int amount,
                final TransactionContext transaction
        ) {
            return 0;
        }

        @Override
        protected void onContentsChanged(final int index, final FluidStack previousContents) {
            this.owner.setChanged();
        }
    }

    private static final class WildlifeEnergyHandler extends SimpleEnergyHandler {
        private final WildlifeAttractorBlockEntity owner;

        private WildlifeEnergyHandler(final WildlifeAttractorBlockEntity owner) {
            super(ENERGY_CAPACITY, MAX_ENERGY_INSERT, MAX_ENERGY_EXTRACT);
            this.owner = owner;
        }

        @Override
        protected void onEnergyChanged(final int previousAmount) {
            this.owner.setChanged();
        }
    }
}
