package committee.nova.mods.skyresources3.common.block.entity;

import com.mojang.serialization.Codec;
import committee.nova.mods.skyresources3.common.item.MachineCasingItem;
import committee.nova.mods.skyresources3.common.item.CombustionHeaterItem;
import committee.nova.mods.skyresources3.common.item.CondenserItem;
import committee.nova.mods.skyresources3.common.item.HeatProviderItem;
import committee.nova.mods.skyresources3.core.machine.CombustionRecipeLogic;
import committee.nova.mods.skyresources3.core.machine.CombustionHeaterType;
import committee.nova.mods.skyresources3.core.machine.CondenserType;
import committee.nova.mods.skyresources3.core.machine.CasingType;
import committee.nova.mods.skyresources3.core.machine.HeatProviderType;
import committee.nova.mods.skyresources3.common.recipe.CondenserRecipe;
import committee.nova.mods.skyresources3.common.recipe.CondenserRecipes;
import committee.nova.mods.skyresources3.common.recipe.SkyResourcesProcessRecipe;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import committee.nova.mods.skyresources3.common.compat.ValueOutput;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemResource;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemStacksResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.transaction.TransactionContext;
import net.minecraftforge.items.IItemHandler;

public final class MachineCasingBlockEntity extends BlockEntity {
    public static final int FUEL_SLOT = 0;
    public static final int SLOT_COUNT = 1;
    public static final int MACHINE_MODE_NONE = 0;
    public static final int MACHINE_MODE_COMBUSTION_HEATER = 1;
    public static final int MACHINE_MODE_HEAT_PROVIDER = 2;
    public static final int MACHINE_MODE_CONDENSER = 3;
    public static final ResourceLocation DEFAULT_CASING_TYPE = ModDataPackRegistries.casingTypeId(ModDataPackRegistries.IRON);
    private static final Codec<ItemStack> HEATER_CODEC = ItemStack.CODEC;
    private static final String CASING_TYPE_KEY = "casing_type";
    private static final String COMBUSTION_HEATER_TYPE_KEY = "combustion_heater_type";
    private static final String HEAT_PROVIDER_TYPE_KEY = "heat_provider_type";
    private static final String CONDENSER_TYPE_KEY = "condenser_type";
    private static final String FUEL_KEY = "fuel";
    private static final String HEATER_KEY = "heater";
    private static final String CURRENT_HEAT_KEY = "current_heat";
    private static final String ITEM_HEAT_KEY = "item_heat";
    private static final String ITEM_HEAT_MAX_KEY = "item_heat_max";
    private static final String HEAT_PER_TICK_KEY = "heat_per_tick";
    private static final String POWERED_KEY = "powered";
    private static final String CONDENSER_TIME_KEY = "condenser_time";
    private static final String CONDENSER_MAX_TIME_KEY = "condenser_max_time";
    private static final String CONDENSER_CATALYST_LEFT_KEY = "condenser_catalyst_left";
    private static final String CONDENSER_CATALYST_KEY = "condenser_catalyst";
    private static final String CONDENSER_RECIPE_HASH_KEY = "condenser_recipe_hash";

    private final FuelItemHandler fuelItems = new FuelItemHandler(this);
    private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> this.fuelItems);
    private ResourceLocation casingTypeId = DEFAULT_CASING_TYPE;
    @Nullable
    private ResourceLocation combustionHeaterTypeId;
    @Nullable
    private ResourceLocation heatProviderTypeId;
    @Nullable
    private ResourceLocation condenserTypeId;
    private ItemStack heater = ItemStack.EMPTY;
    private float currentHeat;
    private float itemHeat;
    private float itemHeatMax;
    private float heatPerTick;
    private int condenserTime;
    private int condenserMaxTime;
    private float condenserCatalystLeft;
    private ItemStack condenserCatalyst = ItemStack.EMPTY;
    private int condenserRecipeHash;
    private boolean powered;

    public MachineCasingBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.MACHINE_CASING.get(), pos, blockState);
    }

    @Override
    public void load(final net.minecraft.nbt.CompoundTag tag) {
        super.load(tag);
        final ValueInput input = new ValueInput(tag);
        this.casingTypeId = input.read(CASING_TYPE_KEY, ResourceLocation.CODEC).orElse(DEFAULT_CASING_TYPE);
        this.combustionHeaterTypeId = input.read(COMBUSTION_HEATER_TYPE_KEY, ResourceLocation.CODEC).orElse(null);
        this.heatProviderTypeId = input.read(HEAT_PROVIDER_TYPE_KEY, ResourceLocation.CODEC).orElse(null);
        this.condenserTypeId = input.read(CONDENSER_TYPE_KEY, ResourceLocation.CODEC).orElse(null);
        input.readChild(FUEL_KEY, this.fuelItems);
        this.heater = input.read(HEATER_KEY, HEATER_CODEC).orElse(ItemStack.EMPTY);
        this.migrateInstalledMachineStack();
        this.currentHeat = input.getFloatOr(CURRENT_HEAT_KEY, 0.0F);
        this.itemHeat = input.getFloatOr(ITEM_HEAT_KEY, 0.0F);
        this.itemHeatMax = input.getFloatOr(ITEM_HEAT_MAX_KEY, 0.0F);
        this.heatPerTick = input.getFloatOr(HEAT_PER_TICK_KEY, 0.0F);
        this.powered = input.getBooleanOr(POWERED_KEY, false);
        this.condenserTime = input.getIntOr(CONDENSER_TIME_KEY, 0);
        this.condenserMaxTime = input.getIntOr(CONDENSER_MAX_TIME_KEY, 0);
        this.condenserCatalystLeft = input.getFloatOr(CONDENSER_CATALYST_LEFT_KEY, 0.0F);
        this.condenserCatalyst = input.read(CONDENSER_CATALYST_KEY, HEATER_CODEC).orElse(ItemStack.EMPTY);
        this.condenserRecipeHash = input.getIntOr(CONDENSER_RECIPE_HASH_KEY, 0);
    }

    @Override
    protected void saveAdditional(final net.minecraft.nbt.CompoundTag tag) {
        super.saveAdditional(tag);
        final ValueOutput output = new ValueOutput(tag);
        output.store(CASING_TYPE_KEY, ResourceLocation.CODEC, this.casingTypeId);
        if (this.combustionHeaterTypeId != null) {
            output.store(COMBUSTION_HEATER_TYPE_KEY, ResourceLocation.CODEC, this.combustionHeaterTypeId);
        }
        if (this.heatProviderTypeId != null) {
            output.store(HEAT_PROVIDER_TYPE_KEY, ResourceLocation.CODEC, this.heatProviderTypeId);
        }
        if (this.condenserTypeId != null) {
            output.store(CONDENSER_TYPE_KEY, ResourceLocation.CODEC, this.condenserTypeId);
        }
        output.putChild(FUEL_KEY, this.fuelItems);
        output.store(HEATER_KEY, HEATER_CODEC, this.heater);
        output.putFloat(CURRENT_HEAT_KEY, this.currentHeat);
        output.putFloat(ITEM_HEAT_KEY, this.itemHeat);
        output.putFloat(ITEM_HEAT_MAX_KEY, this.itemHeatMax);
        output.putFloat(HEAT_PER_TICK_KEY, this.heatPerTick);
        output.putBoolean(POWERED_KEY, this.powered);
        output.putInt(CONDENSER_TIME_KEY, this.condenserTime);
        output.putInt(CONDENSER_MAX_TIME_KEY, this.condenserMaxTime);
        output.putFloat(CONDENSER_CATALYST_LEFT_KEY, this.condenserCatalystLeft);
        output.store(CONDENSER_CATALYST_KEY, HEATER_CODEC, this.condenserCatalyst);
        output.putInt(CONDENSER_RECIPE_HASH_KEY, this.condenserRecipeHash);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        this.dropContents();
    }

    public void serverTick(final ServerLevel level) {
        if (this.hasCombustionHeater()) {
            final CombustionHeaterType heaterType = this.combustionHeaterType();
            this.heatUp(level, heaterType);
            this.tryCraftFromPulse(level, heaterType);
            if (!this.hasValidMultiblock(level) && this.currentHeat > 0.0F) {
                this.currentHeat = Math.max(0.0F, this.currentHeat - 1.0F);
            }
        } else if (this.heatProviderTypeId != null) {
            this.provideHeat(level, this.heatProviderType());
        } else if (this.condenserTypeId != null) {
            this.condense(level, this.condenserType());
        } else {
            this.currentHeat = 0.0F;
            this.itemHeat = 0.0F;
            this.itemHeatMax = 0.0F;
            this.heatPerTick = 0.0F;
            this.powered = level.hasNeighborSignal(this.worldPosition);
            this.clearCondenserRuntime();
        }
        this.setChanged();
    }

    @Override
    public <T> LazyOptional<T> getCapability(final Capability<T> capability, @Nullable final Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return this.itemCapability.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.itemCapability.invalidate();
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return this.fuelItems;
    }

    public ResourceLocation casingTypeId() {
        return this.casingTypeId;
    }

    public void setCasingType(final ResourceKey<CasingType> casingTypeKey) {
        this.setCasingType(ModDataPackRegistries.casingTypeId(casingTypeKey));
    }

    public void setCasingType(final ResourceLocation casingTypeId) {
        if (this.casingTypeId.equals(casingTypeId)) {
            return;
        }
        this.casingTypeId = casingTypeId;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public CasingType casingType() {
        if (this.level == null) {
            return CasingType.fallback();
        }
        return this.level.registryAccess()
                .lookup(ModDataPackRegistries.CASING_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.casingTypeKey(this.casingTypeId)))
                .map(reference -> reference.value())
                .orElse(CasingType.fallback());
    }

    @Nullable
    public ResourceLocation combustionHeaterTypeId() {
        return this.combustionHeaterTypeId;
    }

    @Nullable
    public ResourceLocation heatProviderTypeId() {
        return this.heatProviderTypeId;
    }

    @Nullable
    public ResourceLocation condenserTypeId() {
        return this.condenserTypeId;
    }

    public CombustionHeaterType combustionHeaterType() {
        if (this.level == null || this.combustionHeaterTypeId == null) {
            return CombustionHeaterType.fallback();
        }
        return this.level.registryAccess()
                .lookup(ModDataPackRegistries.COMBUSTION_HEATER_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.combustionHeaterTypeKey(this.combustionHeaterTypeId)))
                .map(reference -> reference.value())
                .orElse(CombustionHeaterType.fallback());
    }

    public HeatProviderType heatProviderType() {
        if (this.level == null || this.heatProviderTypeId == null) {
            return HeatProviderType.fallback();
        }
        return this.level.registryAccess()
                .lookup(ModDataPackRegistries.HEAT_PROVIDER_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.heatProviderTypeKey(this.heatProviderTypeId)))
                .map(reference -> reference.value())
                .orElse(HeatProviderType.fallback());
    }

    public CondenserType condenserType() {
        if (this.level == null || this.condenserTypeId == null) {
            return CondenserType.fallback();
        }
        return this.level.registryAccess()
                .lookup(ModDataPackRegistries.CONDENSER_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.condenserTypeKey(this.condenserTypeId)))
                .map(reference -> reference.value())
                .orElse(CondenserType.fallback());
    }

    public ItemStack heater() {
        return this.heater.copy();
    }

    public boolean hasHeater() {
        return this.hasCombustionHeater()
                || this.heatProviderTypeId != null
                || this.condenserTypeId != null
                || !this.heater.isEmpty();
    }

    public Component menuTitle() {
        final Component casingName = Component.translatable(this.casingType().translationKey());
        final Component machineName = this.installedMachineName();
        if (machineName == null) {
            return casingName;
        }
        return Component.translatable("container.skyresources.machine_casing.with_machine", casingName, machineName);
    }

    private boolean hasCombustionHeater() {
        return this.combustionHeaterTypeId != null;
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
        if (stack.getItem() instanceof CombustionHeaterItem) {
            this.combustionHeaterTypeId = CombustionHeaterItem.combustionHeaterTypeId(stack);
            this.heatProviderTypeId = null;
            this.condenserTypeId = null;
            this.heater = ItemStack.EMPTY;
        } else if (stack.getItem() instanceof HeatProviderItem) {
            this.combustionHeaterTypeId = null;
            this.heatProviderTypeId = HeatProviderItem.heatProviderTypeId(stack);
            this.condenserTypeId = null;
            this.heater = ItemStack.EMPTY;
        } else if (stack.getItem() instanceof CondenserItem) {
            this.combustionHeaterTypeId = null;
            this.heatProviderTypeId = null;
            this.condenserTypeId = CondenserItem.condenserTypeId(stack);
            this.heater = ItemStack.EMPTY;
        } else {
            this.heater = copyWithCount(stack, 1);
        }
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        this.setChangedAndUpdate();
        return true;
    }

    public ItemStack removeHeater() {
        final ItemStack removed;
        if (this.combustionHeaterTypeId != null) {
            removed = CombustionHeaterItem.forType(this.combustionHeaterTypeId);
            this.combustionHeaterTypeId = null;
        } else if (this.heatProviderTypeId != null) {
            removed = HeatProviderItem.forType(this.heatProviderTypeId);
            this.heatProviderTypeId = null;
        } else if (this.condenserTypeId != null) {
            removed = CondenserItem.forType(this.condenserTypeId);
            this.condenserTypeId = null;
        } else {
            removed = this.heater;
            this.heater = ItemStack.EMPTY;
        }
        this.currentHeat = 0.0F;
        this.itemHeat = 0.0F;
        this.itemHeatMax = 0.0F;
        this.heatPerTick = 0.0F;
        this.clearCondenserRuntime();
        this.setChangedAndUpdate();
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
        if (this.heatProviderTypeId != null) {
            return this.heatSourceValue();
        }
        return Math.round(this.currentHeat);
    }

    public int maxHeat() {
        if (this.heatProviderTypeId != null) {
            return Math.round(this.heatProviderType().heatPerTick());
        }
        return this.casingType().maxHeat();
    }

    public int heatPerTick() {
        return Math.round(this.heatPerTick);
    }

    public int installedSpeedPercent() {
        if (this.combustionHeaterTypeId != null) {
            return percent(this.combustionHeaterType().speed());
        }
        if (this.heatProviderTypeId != null) {
            return percent(this.heatProviderType().speed());
        }
        if (this.condenserTypeId != null) {
            return percent(this.condenserType().speed());
        }
        return 0;
    }

    public int installedEfficiencyPercent() {
        if (this.combustionHeaterTypeId != null) {
            return percent(this.combinedEfficiency(this.combustionHeaterType()));
        }
        if (this.heatProviderTypeId != null) {
            return percent(this.combinedEfficiency(this.heatProviderType()));
        }
        if (this.condenserTypeId != null) {
            return percent(this.combinedEfficiency(this.condenserType()));
        }
        return percent(this.casingType().efficiency());
    }

    public int condenserProgress() {
        return this.condenserTime;
    }

    public int condenserMaxProgress() {
        return this.condenserMaxTime;
    }

    public float condenserCatalystLeft() {
        if (this.condenserCatalystLeft > 0.0F && !this.condenserCatalyst.isEmpty()) {
            return this.condenserCatalystLeft;
        }
        if (this.condenserTypeId != null && !this.fuelItems.stack(FUEL_SLOT).isEmpty()) {
            return 1.0F;
        }
        return 0.0F;
    }

    public float condenserExpectedOutputValue() {
        if (!(this.level instanceof ServerLevel serverLevel) || this.condenserTypeId == null) {
            return 0.0F;
        }
        final Optional<CondenserRecipe.Source> source =
                this.condenserSource(serverLevel, this.worldPosition.above());
        if (source.isEmpty()) {
            return 0.0F;
        }
        final ItemStack catalyst = this.activeCondenserCatalyst();
        final Optional<CondenserRecipe> holder =
                CondenserRecipes.find(serverLevel, catalyst, source.get());
        if (holder.isEmpty()) {
            return 0.0F;
        }

        final CondenserRecipe recipe = holder.get();
        final float catalystLeft = this.condenserCatalystLeft();
        if (catalystLeft <= 0.0F) {
            return 0.0F;
        }

        final double parameter = Math.max(1.0D, recipe.parameter());
        final double speed = Math.max(0.001D, this.condenserType().speed());
        final double efficiency = Math.max(0.001D, this.combinedEfficiency(this.condenserType()));
        return (float) (
                2400.0D * speed * efficiency / Math.pow(parameter, 1.3D)
                        * catalystLeft
                        * recipe.output().getCount()
        );
    }

    public boolean hasValidMultiblock(final Level level) {
        if (!this.hasCombustionHeater()) {
            return false;
        }
        final BlockPos chamber = this.worldPosition.above();
        final CasingType.StructureRule structureRule = this.combustionHeaterType().structureRule();
        return level.getBlockState(chamber).isAir()
                && this.isStructureBlockValid(level, chamber, this.worldPosition.west().above(), structureRule)
                && this.isStructureBlockValid(level, chamber, this.worldPosition.east().above(), structureRule)
                && this.isStructureBlockValid(level, chamber, this.worldPosition.north().above(), structureRule)
                && this.isStructureBlockValid(level, chamber, this.worldPosition.south().above(), structureRule)
                && this.isStructureBlockValid(level, chamber, this.worldPosition.above(2), structureRule);
    }

    public boolean usesCombustionChamber() {
        return this.hasCombustionHeater();
    }

    public int installedMachineMode() {
        if (this.hasCombustionHeater()) {
            return MACHINE_MODE_COMBUSTION_HEATER;
        }
        if (this.heatProviderTypeId != null) {
            return MACHINE_MODE_HEAT_PROVIDER;
        }
        if (this.condenserTypeId != null) {
            return MACHINE_MODE_CONDENSER;
        }
        return MACHINE_MODE_NONE;
    }

    public int heatSourceValue() {
        if (this.heatProviderTypeId == null || this.powered || this.itemHeat <= 0.0F) {
            return 0;
        }
        return Math.round(this.heatProviderType().heatPerTick());
    }

    public boolean isChamber(final BlockPos chamber) {
        return this.worldPosition.above().equals(chamber);
    }

    public boolean craftSingleForController(
            final ServerLevel level,
            final Predicate<ItemStack> outputFilter
    ) {
        if (!this.hasCombustionHeater() || !this.hasValidMultiblock(level)) {
            return false;
        }
        return this.craftChamberItems(level, this.combustionHeaterType(), outputFilter, 1);
    }

    public void dropContents() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        final SimpleContainer container = new SimpleContainer(3);
        container.setItem(0, MachineCasingItem.forType(this.casingTypeId));
        container.setItem(1, this.fuelItems.stack(FUEL_SLOT).copy());
        container.setItem(2, this.installedMachineStack());
        Containers.dropContents(this.level, this.worldPosition, container);
    }

    private void condense(final ServerLevel level, final CondenserType condenserType) {
        this.currentHeat = 0.0F;
        this.itemHeat = 0.0F;
        this.itemHeatMax = 0.0F;
        this.heatPerTick = 0.0F;
        this.powered = level.hasNeighborSignal(this.worldPosition);
        if (this.powered) {
            return;
        }

        final BlockPos sourcePos = this.worldPosition.above();
        final Optional<CondenserRecipe.Source> source = this.condenserSource(level, sourcePos);
        if (source.isEmpty()) {
            this.resetCondenserProgress();
            return;
        }

        final ItemStack catalyst = this.activeCondenserCatalyst();
        final Optional<CondenserRecipe> holder = CondenserRecipes.find(level, catalyst, source.get());
        if (holder.isEmpty()) {
            this.resetCondenserProgress();
            return;
        }

        final CondenserRecipe recipe = holder.get();
        final int recipeHash = recipe.runtimeKeyHash();
        if (this.condenserRecipeHash != recipeHash) {
            this.condenserTime = 0;
            this.condenserRecipeHash = recipeHash;
        }
        this.condenserMaxTime = this.condenserMaxTime(recipe, condenserType);

        if (this.condenserCatalystLeft <= 0.0F && !this.consumeCondenserCatalyst(recipe)) {
            this.resetCondenserProgress();
            return;
        }

        if (this.condenserTime < this.condenserMaxTime) {
            this.condenserTime++;
            this.condenserCatalystLeft = Math.max(
                    0.0F,
                    this.condenserCatalystLeft - this.condenserCatalystDrainPerTick(recipe, condenserType)
            );
            this.playCondenserEffects(level);
            if (this.condenserTime < this.condenserMaxTime) {
                return;
            }
        }

        if (!this.routeCondenserOutput(level, recipe.output())) {
            return;
        }

        level.setBlock(sourcePos, Blocks.AIR.defaultBlockState(), 3);
        this.clearAdjacentFlowingFluids(level, sourcePos);
        this.condenserTime = 0;
        if (this.condenserCatalystLeft <= 0.0F) {
            this.condenserCatalyst = ItemStack.EMPTY;
        }
    }

    private Optional<CondenserRecipe.Source> condenserSource(final ServerLevel level, final BlockPos sourcePos) {
        final BlockState state = level.getBlockState(sourcePos);
        final FluidState fluidState = state.getFluidState();
        if (!fluidState.isEmpty() && fluidState.isSource()) {
            final ResourceLocation id = BuiltInRegistries.FLUID.getKey(fluidState.getType());
            return Optional.of(CondenserRecipe.Source.fluid(id));
        }
        if (state.isAir()) {
            return Optional.empty();
        }
        return Optional.of(CondenserRecipe.Source.block(BuiltInRegistries.BLOCK.getKey(state.getBlock())));
    }

    private ItemStack activeCondenserCatalyst() {
        if (this.condenserCatalystLeft > 0.0F && !this.condenserCatalyst.isEmpty()) {
            return this.condenserCatalyst;
        }
        return this.fuelItems.stack(FUEL_SLOT);
    }

    private boolean consumeCondenserCatalyst(final CondenserRecipe recipe) {
        final ItemStack slotStack = this.fuelItems.stack(FUEL_SLOT);
        if (!recipe.isCatalyst(slotStack)) {
            return false;
        }

        final ItemStack nextCatalyst = copyWithCount(slotStack, 1);
        if (!ItemStack.isSameItemSameTags(this.condenserCatalyst, nextCatalyst)) {
            this.condenserTime = 0;
        }
        this.condenserCatalyst = nextCatalyst;
        this.condenserCatalystLeft = 1.0F;
        slotStack.shrink(1);
        if (slotStack.isEmpty()) {
            this.fuelItems.setStack(FUEL_SLOT, ItemStack.EMPTY);
        }
        return true;
    }

    private int condenserMaxTime(final CondenserRecipe recipe, final CondenserType condenserType) {
        return Math.max(1, Math.round(recipe.parameter() / Math.max(0.001F, condenserType.speed())));
    }

    private float condenserCatalystDrainPerTick(
            final CondenserRecipe recipe,
            final CondenserType condenserType
    ) {
        final double parameter = Math.max(1.0D, recipe.parameter());
        final double efficiency = Math.max(0.001D, this.combinedEfficiency(condenserType));
        return (float) (Math.pow(parameter, 1.3D) / 50.0D / (2400.0D * parameter / 50.0D * efficiency));
    }

    private boolean routeCondenserOutput(final ServerLevel level, final ItemStack output) {
        final BlockPos outputPos = this.worldPosition.below();
        final BlockEntity outputBlockEntity = level.getBlockEntity(outputPos);
        final IItemHandler handler = outputBlockEntity == null
                ? null
                : outputBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).orElse(null);
        if (handler == null) {
            Containers.dropItemStack(
                    level,
                    outputPos.getX() + 0.5D,
                    outputPos.getY() + 0.5D,
                    outputPos.getZ() + 0.5D,
                    output.copy()
            );
            return true;
        }

        ItemStack simulatedRemainder = output.copy();
        for (int slot = 0; slot < handler.getSlots() && !simulatedRemainder.isEmpty(); slot++) {
            simulatedRemainder = handler.insertItem(slot, simulatedRemainder, true);
        }
        if (!simulatedRemainder.isEmpty()) {
            return false;
        }

        ItemStack remainder = output.copy();
        for (int slot = 0; slot < handler.getSlots() && !remainder.isEmpty(); slot++) {
            remainder = handler.insertItem(slot, remainder, false);
        }
        return remainder.isEmpty();
    }

    private void clearAdjacentFlowingFluids(final ServerLevel level, final BlockPos sourcePos) {
        for (final Direction direction : Direction.Plane.HORIZONTAL) {
            final BlockPos neighbor = sourcePos.relative(direction);
            final FluidState fluidState = level.getFluidState(neighbor);
            if (!fluidState.isEmpty() && !fluidState.isSource()) {
                level.setBlock(neighbor, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    private void playCondenserEffects(final ServerLevel level) {
        if (level.getGameTime() % 10L != 0L) {
            return;
        }
        level.sendParticles(
                ParticleTypes.SMOKE,
                this.worldPosition.getX() + 0.5D,
                this.worldPosition.getY() + 1.1D,
                this.worldPosition.getZ() + 0.5D,
                2,
                0.2D,
                0.1D,
                0.2D,
                0.01D
        );
    }

    private void heatUp(final ServerLevel level, final CombustionHeaterType heaterType) {
        final float maxHeat = this.casingType().maxHeat();
        if (this.currentHeat < maxHeat && this.itemHeat <= 0.0F) {
            this.consumeFuel(level, heaterType);
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

    private void consumeFuel(final ServerLevel level, final CombustionHeaterType heaterType) {
        final ItemStack fuel = this.fuelItems.stack(FUEL_SLOT);
        if (!heaterType.isValidFuel(fuel, level)) {
            this.itemHeat = 0.0F;
            this.itemHeatMax = 0.0F;
            this.heatPerTick = 0.0F;
            return;
        }

        this.heatPerTick = heaterType.heatPerTick();
        this.itemHeat = heaterType.fuelHeat(fuel, level, this.combinedEfficiency(heaterType));
        this.itemHeatMax = this.itemHeat;
        final Item fuelItem = fuel.getItem();
        fuel.shrink(1);
        if (fuel.isEmpty()) {
            this.fuelItems.setStack(FUEL_SLOT, craftingRemainder(fuelItem));
        }
    }

    private void consumeFuel(final ServerLevel level, final HeatProviderType providerType) {
        final ItemStack fuel = this.fuelItems.stack(FUEL_SLOT);
        if (!providerType.isValidFuel(fuel, level)) {
            this.itemHeat = 0.0F;
            this.itemHeatMax = 0.0F;
            this.heatPerTick = 0.0F;
            return;
        }

        this.heatPerTick = providerType.heatPerTick();
        this.itemHeat = providerType.fuelHeat(fuel, level, this.combinedEfficiency(providerType));
        this.itemHeatMax = this.itemHeat;
        final Item fuelItem = fuel.getItem();
        fuel.shrink(1);
        if (fuel.isEmpty()) {
            this.fuelItems.setStack(FUEL_SLOT, craftingRemainder(fuelItem));
        }
    }

    private static ItemStack craftingRemainder(final Item item) {
        return item.hasCraftingRemainingItem()
                ? new ItemStack(item.getCraftingRemainingItem())
                : ItemStack.EMPTY;
    }

    private void provideHeat(final ServerLevel level, final HeatProviderType providerType) {
        this.powered = level.hasNeighborSignal(this.worldPosition);
        if (this.powered) {
            this.currentHeat = 0.0F;
            this.heatPerTick = 0.0F;
            return;
        }

        if (this.itemHeat <= 0.0F) {
            this.consumeFuel(level, providerType);
        }
        if (this.itemHeat <= 0.0F) {
            this.currentHeat = 0.0F;
            this.heatPerTick = 0.0F;
            this.itemHeatMax = 0.0F;
            return;
        }

        this.heatPerTick = providerType.heatPerTick();
        this.currentHeat = this.heatPerTick;
        this.itemHeat = Math.max(0.0F, this.itemHeat - 1.0F);
    }

    private void tryCraftFromPulse(final ServerLevel level, final CombustionHeaterType heaterType) {
        final boolean currentlyPowered = level.hasNeighborSignal(this.worldPosition);
        if (!currentlyPowered || this.powered || !this.hasValidMultiblock(level)) {
            this.powered = currentlyPowered;
            return;
        }
        this.powered = true;
        this.craftChamberItems(level, heaterType, output -> true, Integer.MAX_VALUE);
    }

    private boolean craftChamberItems(
            final ServerLevel level,
            final CombustionHeaterType heaterType,
            final Predicate<ItemStack> outputFilter,
            final int maxCrafts
    ) {
        final BlockPos chamber = this.worldPosition.above();
        final List<ItemEntity> entities = CombustionRecipeLogic.itemEntities(level, chamber);
        if (entities.isEmpty()) {
            return false;
        }

        final List<ItemStack> stacks = CombustionRecipeLogic.aggregate(entities);
        final Optional<SkyResourcesProcessRecipe> holder =
                CombustionRecipeLogic.findRecipe(level, stacks, this.currentHeat, outputFilter);
        if (holder.isEmpty()) {
            return false;
        }

        entities.forEach(ItemEntity::discard);
        final SkyResourcesProcessRecipe recipe = holder.get();
        this.playCombustionEffects(level, chamber, heaterType);
        int crafts = 0;
        while (crafts < maxCrafts
                && this.currentHeat >= recipe.parameter()
                && CombustionRecipeLogic.canCraft(recipe, stacks)) {
            CombustionRecipeLogic.consumeInputs(recipe, stacks);
            this.currentHeat = this.reducedHeat(heaterType);
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
            final CombustionHeaterType heaterType
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
        if (heaterType.speed() < 2.0F) {
            level.playSound(
                    null,
                    chamber,
                    SoundEvents.GENERIC_EXPLODE,
                    SoundSource.BLOCKS,
                    4.0F,
                    (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F
            );
        }
    }

    private float reducedHeat(final CombustionHeaterType heaterType) {
        final float multiplier = 1.0F - (1.0F / (1.5F + 0.8F * this.combinedEfficiency(heaterType)));
        return this.currentHeat * multiplier;
    }

    private float combinedEfficiency(final CombustionHeaterType heaterType) {
        return heaterType.efficiency() * this.casingType().efficiency();
    }

    private float combinedEfficiency(final HeatProviderType providerType) {
        return providerType.efficiency() * this.casingType().efficiency();
    }

    private static ItemStack copyWithCount(final ItemStack stack, final int count) {
        final ItemStack copy = stack.copy();
        copy.setCount(count);
        return copy;
    }

    private float combinedEfficiency(final CondenserType condenserType) {
        return condenserType.efficiency() * this.casingType().efficiency();
    }

    private static int percent(final float value) {
        return Math.round(value * 100.0F);
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
        if (this.condenserTypeId != null) {
            return this.isValidCondenserCatalyst(stack);
        }
        if (this.hasCombustionHeater()) {
            return this.level != null && this.combustionHeaterType().isValidFuel(stack, this.level);
        }
        if (this.heatProviderTypeId == null || this.level == null) {
            return false;
        }
        return this.heatProviderType().isValidFuel(stack, this.level);
    }

    private boolean isValidCondenserCatalyst(final ItemStack stack) {
        if (stack.is(ModItems.ORE_ALCHEMICAL_DUST.get())) {
            return true;
        }
        if (this.level instanceof ServerLevel serverLevel) {
            return CondenserRecipes.hasCatalyst(serverLevel, stack);
        }
        return false;
    }

    private ItemStack installedMachineStack() {
        if (this.combustionHeaterTypeId != null) {
            return CombustionHeaterItem.forType(this.combustionHeaterTypeId);
        }
        if (this.heatProviderTypeId != null) {
            return HeatProviderItem.forType(this.heatProviderTypeId);
        }
        if (this.condenserTypeId != null) {
            return CondenserItem.forType(this.condenserTypeId);
        }
        return this.heater.copy();
    }

    @Nullable
    private Component installedMachineName() {
        if (this.combustionHeaterTypeId != null) {
            return Component.translatable(this.combustionHeaterType().translationKey());
        }
        if (this.heatProviderTypeId != null) {
            return Component.translatable(this.heatProviderType().translationKey());
        }
        if (this.condenserTypeId != null) {
            return Component.translatable(this.condenserType().translationKey());
        }
        if (!this.heater.isEmpty()) {
            return this.heater.getHoverName();
        }
        return null;
    }

    private void migrateInstalledMachineStack() {
        if (this.heater.isEmpty()
                || this.combustionHeaterTypeId != null
                || this.heatProviderTypeId != null
                || this.condenserTypeId != null) {
            return;
        }
        if (this.heater.getItem() instanceof CombustionHeaterItem) {
            this.combustionHeaterTypeId = CombustionHeaterItem.combustionHeaterTypeId(this.heater);
            this.heater = ItemStack.EMPTY;
        } else if (this.heater.getItem() instanceof HeatProviderItem) {
            this.heatProviderTypeId = HeatProviderItem.heatProviderTypeId(this.heater);
            this.heater = ItemStack.EMPTY;
        } else if (this.heater.getItem() instanceof CondenserItem) {
            this.condenserTypeId = CondenserItem.condenserTypeId(this.heater);
            this.heater = ItemStack.EMPTY;
        }
    }

    private void setChangedAndUpdate() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    private void resetCondenserProgress() {
        this.condenserTime = 0;
        this.condenserMaxTime = 0;
        this.condenserRecipeHash = 0;
    }

    private void clearCondenserRuntime() {
        this.resetCondenserProgress();
        this.condenserCatalystLeft = 0.0F;
        this.condenserCatalyst = ItemStack.EMPTY;
    }

    private boolean isStructureBlockValid(
            final Level level,
            final BlockPos chamber,
            final BlockPos pos,
            final CasingType.StructureRule structureRule
    ) {
        final BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return false;
        }
        final Direction face = faceToward(pos, chamber);
        if (face == null || !state.isFaceSturdy(level, pos, face)) {
            return false;
        }
        final boolean topBlock = pos.equals(chamber.above());
        return switch (structureRule) {
            case WOOD -> state.is(BlockTags.LOGS)
                    || state.is(BlockTags.PLANKS)
                    || isGlass(state)
                    || topBlock && state.is(BlockTags.WOODEN_TRAPDOORS);
            case STONE -> state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(Blocks.COBBLESTONE) || isGlass(state);
            case METAL -> isMetalStoneOrGlass(state) || topBlock && isMetalTopAutomation(state);
        };
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

    private static boolean isMetalTopAutomation(final BlockState state) {
        return state.is(Blocks.IRON_TRAPDOOR) || state.is(ModBlocks.QUICK_DROPPER.get());
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
            return this.stacks.get(slot);
        }

        private void setStack(final int slot, final ItemStack stack) {
            this.stacks.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
            this.owner.setChanged();
        }
    }
}
