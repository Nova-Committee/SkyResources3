package committee.nova.mods.skyresources3.block.entity;

import com.mojang.serialization.Codec;
import committee.nova.mods.skyresources3.block.MachineCasingBlock;
import committee.nova.mods.skyresources3.item.CombustionHeaterItem;
import committee.nova.mods.skyresources3.item.CondenserItem;
import committee.nova.mods.skyresources3.item.HeatProviderItem;
import committee.nova.mods.skyresources3.machine.CombustionRecipeLogic;
import committee.nova.mods.skyresources3.machine.MachineVariant;
import committee.nova.mods.skyresources3.recipe.SkyResourcesProcessRecipe;
import committee.nova.mods.skyresources3.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public final class MachineCasingBlockEntity extends BlockEntity {
    public static final int FUEL_SLOT = 0;
    public static final int SLOT_COUNT = 1;
    public static final int MACHINE_MODE_NONE = 0;
    public static final int MACHINE_MODE_COMBUSTION_HEATER = 1;
    public static final int MACHINE_MODE_HEAT_PROVIDER = 2;
    public static final int MACHINE_MODE_CONDENSER = 3;
    private static final Codec<ItemStack> HEATER_CODEC = ItemStack.OPTIONAL_CODEC;
    private static final String FUEL_KEY = "fuel";
    private static final String HEATER_KEY = "heater";
    private static final String CURRENT_HEAT_KEY = "current_heat";
    private static final String ITEM_HEAT_KEY = "item_heat";
    private static final String ITEM_HEAT_MAX_KEY = "item_heat_max";
    private static final String HEAT_PER_TICK_KEY = "heat_per_tick";
    private static final String POWERED_KEY = "powered";

    private final FuelItemHandler fuelItems = new FuelItemHandler(this);
    private ItemStack heater = ItemStack.EMPTY;
    private float currentHeat;
    private float itemHeat;
    private float itemHeatMax;
    private float heatPerTick;
    private boolean powered;

    public MachineCasingBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.MACHINE_CASING.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        input.readChild(FUEL_KEY, this.fuelItems);
        this.heater = input.read(HEATER_KEY, HEATER_CODEC).orElse(ItemStack.EMPTY);
        this.currentHeat = input.getFloatOr(CURRENT_HEAT_KEY, 0.0F);
        this.itemHeat = input.getFloatOr(ITEM_HEAT_KEY, 0.0F);
        this.itemHeatMax = input.getFloatOr(ITEM_HEAT_MAX_KEY, 0.0F);
        this.heatPerTick = input.getFloatOr(HEAT_PER_TICK_KEY, 0.0F);
        this.powered = input.getBooleanOr(POWERED_KEY, false);
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(FUEL_KEY, this.fuelItems);
        output.store(HEATER_KEY, HEATER_CODEC, this.heater);
        output.putFloat(CURRENT_HEAT_KEY, this.currentHeat);
        output.putFloat(ITEM_HEAT_KEY, this.itemHeat);
        output.putFloat(ITEM_HEAT_MAX_KEY, this.itemHeatMax);
        output.putFloat(HEAT_PER_TICK_KEY, this.heatPerTick);
        output.putBoolean(POWERED_KEY, this.powered);
    }

    @Override
    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        this.dropContents();
    }

    public void serverTick(final ServerLevel level) {
        if (this.heater.getItem() instanceof CombustionHeaterItem heaterItem) {
            this.heatUp(level, heaterItem.variant());
            this.tryCraftFromPulse(level, heaterItem.variant());
            if (!this.hasValidMultiblock(level) && this.currentHeat > 0.0F) {
                this.currentHeat = Math.max(0.0F, this.currentHeat - 1.0F);
            }
        } else if (this.heater.getItem() instanceof HeatProviderItem heatProvider) {
            this.provideHeat(level, heatProvider.variant());
        } else {
            this.currentHeat = 0.0F;
            this.itemHeat = 0.0F;
            this.itemHeatMax = 0.0F;
            this.heatPerTick = 0.0F;
            this.powered = level.hasNeighborSignal(this.worldPosition);
        }
        this.setChanged();
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return this.fuelItems;
    }

    public MachineVariant casingVariant() {
        if (this.getBlockState().getBlock() instanceof MachineCasingBlock casingBlock) {
            return casingBlock.variant();
        }
        return MachineVariant.IRON;
    }

    public ItemStack heater() {
        return this.heater.copy();
    }

    public boolean hasHeater() {
        return !this.heater.isEmpty();
    }

    public boolean canInstallMachine(final ItemStack stack) {
        return stack.getItem() instanceof CombustionHeaterItem
                || stack.getItem() instanceof HeatProviderItem
                || stack.getItem() instanceof CondenserItem;
    }

    public boolean installHeater(final ItemStack stack, final Player player) {
        if (this.hasHeater() || !this.canInstallMachine(stack)) {
            return false;
        }
        this.heater = stack.copyWithCount(1);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        this.setChanged();
        return true;
    }

    public ItemStack removeHeater() {
        final ItemStack removed = this.heater;
        this.heater = ItemStack.EMPTY;
        this.currentHeat = 0.0F;
        this.itemHeat = 0.0F;
        this.itemHeatMax = 0.0F;
        this.heatPerTick = 0.0F;
        this.setChanged();
        return removed;
    }

    public ItemStack getStackInSlot(final int slot) {
        return slot == FUEL_SLOT ? this.fuelItems.stack(slot) : ItemStack.EMPTY;
    }

    public void setStackInSlot(final int slot, final ItemStack stack) {
        if (slot != FUEL_SLOT) {
            return;
        }
        this.fuelItems.setStack(slot, stack);
    }

    public ItemStack removeStack(final int slot, final int amount) {
        if (slot != FUEL_SLOT || amount <= 0) {
            return ItemStack.EMPTY;
        }
        final ItemStack stack = this.fuelItems.stack(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = stack.split(amount);
        if (stack.isEmpty()) {
            this.fuelItems.setStack(slot, ItemStack.EMPTY);
        }
        this.setChanged();
        return removed;
    }

    public ItemStack removeStackNoUpdate(final int slot) {
        if (slot != FUEL_SLOT) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.fuelItems.stack(slot);
        this.fuelItems.setStack(slot, ItemStack.EMPTY);
        return removed;
    }

    public boolean mayPlaceInSlot(final int slot, final ItemStack stack) {
        return slot == FUEL_SLOT && this.isValidFuel(stack);
    }

    public int currentHeat() {
        if (this.heater.getItem() instanceof HeatProviderItem) {
            return this.heatSourceValue();
        }
        return Math.round(this.currentHeat);
    }

    public int maxHeat() {
        if (this.heater.getItem() instanceof HeatProviderItem heatProvider) {
            return Math.round(heatProvider.variant().heatPerTick());
        }
        return this.casingVariant().maxHeat();
    }

    public int heatPerTick() {
        return Math.round(this.heatPerTick);
    }

    public boolean hasValidMultiblock(final Level level) {
        final BlockPos chamber = this.worldPosition.above();
        return level.getBlockState(chamber).isAir()
                && this.isStructureBlockValid(level, chamber, this.worldPosition.west().above())
                && this.isStructureBlockValid(level, chamber, this.worldPosition.east().above())
                && this.isStructureBlockValid(level, chamber, this.worldPosition.north().above())
                && this.isStructureBlockValid(level, chamber, this.worldPosition.south().above())
                && this.isStructureBlockValid(level, chamber, this.worldPosition.above(2));
    }

    public boolean usesCombustionChamber() {
        return this.heater.getItem() instanceof CombustionHeaterItem;
    }

    public int installedMachineMode() {
        if (this.heater.getItem() instanceof CombustionHeaterItem) {
            return MACHINE_MODE_COMBUSTION_HEATER;
        }
        if (this.heater.getItem() instanceof HeatProviderItem) {
            return MACHINE_MODE_HEAT_PROVIDER;
        }
        if (this.heater.getItem() instanceof CondenserItem) {
            return MACHINE_MODE_CONDENSER;
        }
        return MACHINE_MODE_NONE;
    }

    public int heatSourceValue() {
        if (!(this.heater.getItem() instanceof HeatProviderItem heatProvider) || this.powered || this.itemHeat <= 0.0F) {
            return 0;
        }
        return Math.round(heatProvider.variant().heatPerTick());
    }

    public boolean isChamber(final BlockPos chamber) {
        return this.worldPosition.above().equals(chamber);
    }

    public boolean craftSingleForController(
            final ServerLevel level,
            final Predicate<ItemStack> outputFilter
    ) {
        if (!(this.heater.getItem() instanceof CombustionHeaterItem heaterItem) || !this.hasValidMultiblock(level)) {
            return false;
        }
        return this.craftChamberItems(level, heaterItem.variant(), outputFilter, 1);
    }

    public void dropContents() {
        if (this.level == null) {
            return;
        }
        final SimpleContainer container = new SimpleContainer(2);
        container.setItem(0, this.fuelItems.stack(FUEL_SLOT).copy());
        container.setItem(1, this.heater.copy());
        Containers.dropContents(this.level, this.worldPosition, container);
    }

    private void heatUp(final ServerLevel level, final MachineVariant heaterVariant) {
        final MachineVariant casingVariant = this.casingVariant();
        final float maxHeat = casingVariant.maxHeat();
        if (this.currentHeat < maxHeat && this.itemHeat <= 0.0F) {
            this.consumeFuel(level, heaterVariant);
        }
        if (this.currentHeat >= maxHeat || this.itemHeat <= 0.0F || this.heatPerTick <= 0.0F) {
            return;
        }
        final float added = Math.min(Math.min(maxHeat - this.currentHeat, this.heatPerTick), this.itemHeat);
        if (added <= 0.0F) {
            return;
        }
        this.currentHeat += added;
        this.itemHeat -= added;
    }

    private void consumeFuel(final ServerLevel level, final MachineVariant heaterVariant) {
        final ItemStack fuel = this.fuelItems.stack(FUEL_SLOT);
        if (!heaterVariant.isValidFuel(fuel, level)) {
            this.itemHeat = 0.0F;
            this.itemHeatMax = 0.0F;
            this.heatPerTick = 0.0F;
            return;
        }

        this.heatPerTick = heaterVariant.heatPerTick();
        this.itemHeat = heaterVariant.fuelHeat(fuel, level, this.combinedEfficiency(heaterVariant));
        this.itemHeatMax = this.itemHeat;
        final Item fuelItem = fuel.getItem();
        fuel.shrink(1);
        if (fuel.isEmpty()) {
            this.fuelItems.setStack(FUEL_SLOT, fuelItem.getCraftingRemainder());
        }
    }

    private void provideHeat(final ServerLevel level, final MachineVariant providerVariant) {
        this.powered = level.hasNeighborSignal(this.worldPosition);
        if (this.powered) {
            this.currentHeat = 0.0F;
            this.heatPerTick = 0.0F;
            return;
        }

        if (this.itemHeat <= 0.0F) {
            this.consumeFuel(level, providerVariant);
        }
        if (this.itemHeat <= 0.0F) {
            this.currentHeat = 0.0F;
            this.heatPerTick = 0.0F;
            this.itemHeatMax = 0.0F;
            return;
        }

        this.heatPerTick = providerVariant.heatPerTick();
        this.currentHeat = this.heatPerTick;
        this.itemHeat = Math.max(0.0F, this.itemHeat - 1.0F);
    }

    private void tryCraftFromPulse(final ServerLevel level, final MachineVariant heaterVariant) {
        final boolean currentlyPowered = level.hasNeighborSignal(this.worldPosition);
        if (!currentlyPowered || this.powered || !this.hasValidMultiblock(level)) {
            this.powered = currentlyPowered;
            return;
        }
        this.powered = true;
        this.craftChamberItems(level, heaterVariant, output -> true, Integer.MAX_VALUE);
    }

    private boolean craftChamberItems(
            final ServerLevel level,
            final MachineVariant heaterVariant,
            final Predicate<ItemStack> outputFilter,
            final int maxCrafts
    ) {
        final BlockPos chamber = this.worldPosition.above();
        final List<ItemEntity> entities = CombustionRecipeLogic.itemEntities(level, chamber);
        if (entities.isEmpty()) {
            return false;
        }

        final List<ItemStack> stacks = CombustionRecipeLogic.aggregate(entities);
        final Optional<RecipeHolder<SkyResourcesProcessRecipe>> holder =
                CombustionRecipeLogic.findRecipe(level, stacks, this.currentHeat, outputFilter);
        if (holder.isEmpty()) {
            return false;
        }

        entities.forEach(ItemEntity::discard);
        final SkyResourcesProcessRecipe recipe = holder.get().value();
        this.playCombustionEffects(level, chamber, heaterVariant);
        int crafts = 0;
        while (crafts < maxCrafts
                && this.currentHeat >= recipe.parameter()
                && CombustionRecipeLogic.canCraft(recipe, stacks)) {
            CombustionRecipeLogic.consumeInputs(recipe, stacks);
            this.currentHeat = this.reducedHeat(heaterVariant);
            crafts++;
            for (final ItemStack output : recipe.outputs()) {
                this.routeOutput(level, chamber, output);
            }
        }
        for (final ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, chamber.getX() + 0.5D, chamber.getY() + 0.5D, chamber.getZ() + 0.5D, stack);
            }
        }
        return crafts > 0;
    }

    private void playCombustionEffects(
            final ServerLevel level,
            final BlockPos chamber,
            final MachineVariant heaterVariant
    ) {
        level.sendParticles(
                ParticleTypes.EXPLOSION,
                chamber.getX() + 0.5D,
                chamber.getY() + 0.5D,
                chamber.getZ() + 0.5D,
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );
        if (heaterVariant.speed() < 2.0F) {
            level.playSound(
                    null,
                    chamber,
                    SoundEvents.GENERIC_EXPLODE.value(),
                    SoundSource.BLOCKS,
                    4.0F,
                    (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F
            );
        }
    }

    private float reducedHeat(final MachineVariant heaterVariant) {
        final float multiplier = 1.0F - (1.0F / (1.5F + 0.8F * this.combinedEfficiency(heaterVariant)));
        return this.currentHeat * multiplier;
    }

    private float combinedEfficiency(final MachineVariant heaterVariant) {
        return heaterVariant.efficiency() * this.casingVariant().efficiency();
    }

    private void routeOutput(final ServerLevel level, final BlockPos chamber, final ItemStack output) {
        ItemStack remaining = output.copy();
        final CombustionCollectorBlockEntity collector = this.findCollector(level, chamber);
        if (collector != null) {
            remaining = collector.insertOutput(remaining);
        }
        if (!remaining.isEmpty()) {
            Containers.dropItemStack(
                    level,
                    chamber.getX() + 0.5D,
                    chamber.getY() + 0.5D,
                    chamber.getZ() + 0.5D,
                    remaining
            );
        }
    }

    private CombustionCollectorBlockEntity findCollector(final ServerLevel level, final BlockPos chamber) {
        final BlockPos[] positions = {
                chamber.west(),
                chamber.east(),
                chamber.north(),
                chamber.south(),
                chamber.above()
        };
        for (final BlockPos pos : positions) {
            if (level.getBlockEntity(pos) instanceof CombustionCollectorBlockEntity collector) {
                return collector;
            }
        }
        return null;
    }

    private boolean isValidFuel(final ItemStack stack) {
        final MachineVariant variant = this.installedFuelVariant();
        if (variant == null || this.level == null) {
            return false;
        }
        return variant.isValidFuel(stack, this.level);
    }

    private MachineVariant installedFuelVariant() {
        if (this.heater.getItem() instanceof CombustionHeaterItem heaterItem) {
            return heaterItem.variant();
        }
        if (this.heater.getItem() instanceof HeatProviderItem heatProvider) {
            return heatProvider.variant();
        }
        return null;
    }

    private boolean isStructureBlockValid(final Level level, final BlockPos chamber, final BlockPos pos) {
        final BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return false;
        }
        final Direction face = faceToward(pos, chamber);
        if (face == null || !state.isFaceSturdy(level, pos, face)) {
            return false;
        }
        if (this.casingVariant() == MachineVariant.WOODEN) {
            return state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS) || isGlass(state);
        }
        if (this.casingVariant() == MachineVariant.STONE) {
            return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(Blocks.COBBLESTONE) || isGlass(state);
        }
        return isMetalStoneOrGlass(state);
    }

    private static boolean isMetalStoneOrGlass(final BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(Blocks.COBBLESTONE)
                || state.is(Blocks.IRON_BLOCK)
                || state.is(Blocks.COPPER_BLOCK)
                || state.is(Blocks.NETHER_BRICKS)
                || state.is(Blocks.END_STONE)
                || state.is(ModBlocks.DARK_MATTER_BLOCK.get())
                || state.is(ModBlocks.LIGHT_MATTER_BLOCK.get())
                || state.is(ModBlocks.COMBUSTION_COLLECTOR.get())
                || state.is(ModBlocks.COMBUSTION_CONTROLLER.get())
                || isGlass(state);
    }

    private static boolean isGlass(final BlockState state) {
        return state.is(Blocks.GLASS) || state.is(Blocks.TINTED_GLASS) || state.is(ModBlocks.ALCHEMICAL_GLASS.get());
    }

    private static Direction faceToward(final BlockPos from, final BlockPos to) {
        final int dx = to.getX() - from.getX();
        if (dx > 0) {
            return Direction.EAST;
        }
        if (dx < 0) {
            return Direction.WEST;
        }
        final int dy = to.getY() - from.getY();
        if (dy > 0) {
            return Direction.UP;
        }
        if (dy < 0) {
            return Direction.DOWN;
        }
        final int dz = to.getZ() - from.getZ();
        if (dz > 0) {
            return Direction.SOUTH;
        }
        return Direction.NORTH;
    }

    private static final class FuelItemHandler extends ItemStacksResourceHandler {
        private final MachineCasingBlockEntity owner;

        private FuelItemHandler(final MachineCasingBlockEntity owner) {
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
            return index == FUEL_SLOT && this.owner.isValidFuel(resource.toStack());
        }

        @Override
        protected void onContentsChanged(final int index, final ItemStack previousContents) {
            this.owner.setChanged();
        }

        private ItemStack stack(final int slot) {
            return this.stacks.get(slot);
        }

        private void setStack(final int slot, final ItemStack stack) {
            this.stacks.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
            this.owner.setChanged();
        }
    }
}
