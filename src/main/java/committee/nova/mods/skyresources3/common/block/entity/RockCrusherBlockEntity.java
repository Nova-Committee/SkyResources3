package committee.nova.mods.skyresources3.common.block.entity;

import com.mojang.serialization.Codec;
import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.common.recipe.ProcessRecipes;
import committee.nova.mods.skyresources3.common.recipe.SkyResourcesProcessRecipe;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import committee.nova.mods.skyresources3.common.compat.ValueOutput;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.energy.EnergyHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.energy.SimpleEnergyHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemResource;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemStacksResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.transaction.TransactionContext;

public final class RockCrusherBlockEntity extends BlockEntity {
    public static final int INPUT_SLOT = 0;
    public static final int FIRST_OUTPUT_SLOT = 1;
    public static final int OUTPUT_SLOT_COUNT = 3;
    public static final int SLOT_COUNT = 4;
    public static final int MAX_PROGRESS = 100;
    public static final int ENERGY_CAPACITY = 100_000;
    private static final int MAX_ENERGY_INSERT = 2_000;
    private static final int MAX_ENERGY_EXTRACT = 0;
    private static final float OUTPUT_BONUS = 1.2F;
    private static final Codec<List<ItemStack>> BUFFER_CODEC = ItemStack.CODEC.listOf();
    private static final String ITEMS_KEY = "items";
    private static final String ENERGY_KEY = "energy";
    private static final String PROGRESS_KEY = "progress";
    private static final String BUFFER_KEY = "buffer";

    private final RockCrusherItemHandler items = new RockCrusherItemHandler(this);
    private final RockCrusherEnergyHandler energy = new RockCrusherEnergyHandler(this);
    private final NonNullList<ItemStack> bufferStacks = NonNullList.create();
    private float progress;

    public RockCrusherBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.ROCK_CRUSHER.get(), pos, blockState);
    }

    @Override
    public void load(final net.minecraft.nbt.CompoundTag tag) {
        super.load(tag);
        final ValueInput input = new ValueInput(tag);
        input.readChild(ITEMS_KEY, this.items);
        input.readChild(ENERGY_KEY, this.energy);
        this.progress = input.getFloatOr(PROGRESS_KEY, 0.0F);
        this.bufferStacks.clear();
        input.read(BUFFER_KEY, BUFFER_CODEC)
                .orElse(List.of())
                .stream()
                .filter(stack -> !stack.isEmpty())
                .map(ItemStack::copy)
                .forEach(this.bufferStacks::add);
    }

    @Override
    protected void saveAdditional(final net.minecraft.nbt.CompoundTag tag) {
        super.saveAdditional(tag);
        final ValueOutput output = new ValueOutput(tag);
        output.putChild(ITEMS_KEY, this.items);
        output.putChild(ENERGY_KEY, this.energy);
        output.putFloat(PROGRESS_KEY, this.progress);
        output.store(BUFFER_KEY, BUFFER_CODEC, this.bufferStacks.stream()
                .filter(stack -> !stack.isEmpty())
                .map(ItemStack::copy)
                .toList());
    }

    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        this.dropContents();
    }

    public void serverTick(final ServerLevel level) {
        if (level.hasNeighborSignal(this.worldPosition)) {
            return;
        }

        boolean changed = this.moveBufferedOutputs();
        if (!this.bufferStacks.isEmpty()) {
            if (changed) {
                this.setChanged();
            }
            return;
        }

        final List<SkyResourcesProcessRecipe> recipes = this.matchingRecipes(level);
        if (recipes.isEmpty()) {
            if (this.progress > 0.0F) {
                this.progress = 0.0F;
                this.setChanged();
            }
            return;
        }

        final int powerUsage = Math.max(0, Config.rockCrusherPowerUsage);
        if (this.energy.getAmountAsInt() < powerUsage) {
            return;
        }

        this.consumeEnergy(powerUsage);
        this.progress += Math.max(1, Config.rockCrusherSpeed);
        changed = true;
        if (this.progress >= MAX_PROGRESS) {
            changed |= this.completeRecipe(level, recipes);
        }
        if (changed) {
            this.setChanged();
        }
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return this.items;
    }

    public EnergyHandler getEnergyHandler() {
        return this.energy;
    }

    public ItemStack getStackInSlot(final int slot) {
        return isMachineSlot(slot) ? this.items.stack(slot) : ItemStack.EMPTY;
    }

    public void setStackInSlot(final int slot, final ItemStack stack) {
        if (!isMachineSlot(slot)) {
            return;
        }
        this.items.setStack(slot, stack);
        if (slot == INPUT_SLOT) {
            this.progress = 0.0F;
        }
    }

    public ItemStack removeStack(final int slot, final int amount) {
        if (!isMachineSlot(slot) || amount <= 0) {
            return ItemStack.EMPTY;
        }
        final ItemStack stack = this.items.stack(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = stack.split(amount);
        if (stack.isEmpty()) {
            this.items.setStack(slot, ItemStack.EMPTY);
        }
        if (slot == INPUT_SLOT) {
            this.progress = 0.0F;
        }
        this.setChanged();
        return removed;
    }

    public ItemStack removeStackNoUpdate(final int slot) {
        if (!isMachineSlot(slot)) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.items.stack(slot);
        this.items.setStack(slot, ItemStack.EMPTY);
        if (slot == INPUT_SLOT) {
            this.progress = 0.0F;
        }
        return removed;
    }

    public boolean mayPlaceInSlot(final int slot, final ItemStack stack) {
        return slot == INPUT_SLOT && !stack.isEmpty();
    }

    public int getProgress() {
        return Math.min(MAX_PROGRESS, Math.round(this.progress));
    }

    public int getEnergyStored() {
        return this.energy.getAmountAsInt();
    }

    public int getMaxEnergyStored() {
        return this.energy.getCapacityAsInt();
    }

    public void dropContents() {
        if (this.level == null) {
            return;
        }
        final SimpleContainer container = new SimpleContainer(SLOT_COUNT + this.bufferStacks.size());
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            container.setItem(slot, this.items.stack(slot).copy());
        }
        for (int index = 0; index < this.bufferStacks.size(); index++) {
            container.setItem(SLOT_COUNT + index, this.bufferStacks.get(index).copy());
        }
        Containers.dropContents(this.level, this.worldPosition, container);
        this.bufferStacks.clear();
    }

    private boolean completeRecipe(
            final ServerLevel level,
            final List<SkyResourcesProcessRecipe> recipes
    ) {
        for (final SkyResourcesProcessRecipe recipe : recipes) {
            for (final ItemStack output : recipe.outputs()) {
                this.addChanceOutput(level, output, recipe.parameter() * OUTPUT_BONUS);
            }
        }

        final ItemStack input = this.items.stack(INPUT_SLOT);
        final int consumed = Math.max(1, recipes.get(0).inputs().get(0).count());
        input.shrink(consumed);
        if (input.isEmpty()) {
            this.items.setStack(INPUT_SLOT, ItemStack.EMPTY);
        }
        this.progress = 0.0F;
        return true;
    }

    private void addChanceOutput(final ServerLevel level, final ItemStack output, float chance) {
        if (output.isEmpty() || chance <= 0.0F) {
            return;
        }
        while (chance >= 1.0F) {
            this.bufferStacks.add(output.copy());
            chance -= 1.0F;
        }
        if (chance > 0.0F && level.random.nextFloat() <= chance) {
            this.bufferStacks.add(output.copy());
        }
    }

    private boolean moveBufferedOutputs() {
        boolean changed = false;
        for (int slot = FIRST_OUTPUT_SLOT; slot < SLOT_COUNT && !this.bufferStacks.isEmpty(); slot++) {
            final ItemStack buffered = this.bufferStacks.get(this.bufferStacks.size() - 1);
            if (this.insertIntoOutputSlot(slot, buffered)) {
                changed = true;
            }
            if (buffered.isEmpty()) {
                this.bufferStacks.remove(this.bufferStacks.size() - 1);
            }
        }
        return changed;
    }

    private boolean insertIntoOutputSlot(final int slot, final ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        final ItemStack current = this.items.stack(slot);
        final int moved;
        if (current.isEmpty()) {
            moved = Math.min(stack.getCount(), stack.getMaxStackSize());
            final ItemStack movedStack = stack.copy();
            movedStack.setCount(moved);
            this.items.setStack(slot, movedStack);
            stack.shrink(moved);
            return moved > 0;
        }
        if (!ItemStack.isSameItemSameTags(current, stack)) {
            return false;
        }
        moved = Math.min(stack.getCount(), current.getMaxStackSize() - current.getCount());
        if (moved <= 0) {
            return false;
        }
        current.grow(moved);
        stack.shrink(moved);
        return true;
    }

    private List<SkyResourcesProcessRecipe> matchingRecipes(final ServerLevel level) {
        final ItemStack input = this.items.stack(INPUT_SLOT);
        if (input.isEmpty()) {
            return List.of();
        }
        return ProcessRecipes.findAll(level, ProcessRecipes.ROCK_GRINDER, List.of(input.copy()));
    }

    private void consumeEnergy(final int amount) {
        if (amount <= 0) {
            return;
        }
        this.energy.set(this.energy.getAmountAsInt() - amount);
    }

    private static boolean isMachineSlot(final int slot) {
        return slot >= 0 && slot < SLOT_COUNT;
    }

    private static class StoredItemStacks extends ItemStacksResourceHandler {
        protected final RockCrusherBlockEntity owner;

        StoredItemStacks(final RockCrusherBlockEntity owner) {
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
        protected void onContentsChanged(final int index, final ItemStack previousContents) {
            this.owner.setChanged();
        }

        final ItemStack stack(final int slot) {
            return this.stacks.get(slot);
        }

        final void setStack(final int slot, final ItemStack stack) {
            this.stacks.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
            this.owner.setChanged();
        }
    }

    private static final class RockCrusherItemHandler extends StoredItemStacks {
        private RockCrusherItemHandler(final RockCrusherBlockEntity owner) {
            super(owner);
        }

        @Override
        public boolean isValid(final int index, final ItemResource resource) {
            return index == INPUT_SLOT && !resource.isEmpty();
        }

        @Override
        public int extract(
                final int index,
                final ItemResource resource,
                final int amount,
                final TransactionContext transaction
        ) {
            if (index == INPUT_SLOT) {
                return 0;
            }
            return super.extract(index, resource, amount, transaction);
        }
    }

    private static final class RockCrusherEnergyHandler extends SimpleEnergyHandler {
        private final RockCrusherBlockEntity owner;

        private RockCrusherEnergyHandler(final RockCrusherBlockEntity owner) {
            super(ENERGY_CAPACITY, MAX_ENERGY_INSERT, MAX_ENERGY_EXTRACT);
            this.owner = owner;
        }

        @Override
        protected void onEnergyChanged(final int previousAmount) {
            this.owner.setChanged();
        }
    }
}
