package committee.nova.mods.skyresources3.common.block.entity;

import committee.nova.mods.skyresources3.common.item.HealthGemItem;
import committee.nova.mods.skyresources3.common.recipe.InfusionRecipes;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import committee.nova.mods.skyresources3.common.compat.ValueOutput;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemResource;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemStacksResourceHandler;

public final class LifeInfuserBlockEntity extends BlockEntity {
    public static final int GEM_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int SLOT_COUNT = 2;
    private static final String ITEMS_KEY = "items";
    private static final String GEM_KEY = "gem";
    private static final String INPUT_KEY = "input";

    private final LifeInfuserItemHandler items = new LifeInfuserItemHandler(this);
    private boolean powered;

    public LifeInfuserBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.LIFE_INFUSER.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(final net.minecraft.nbt.CompoundTag tag, final net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        final ValueInput input = new ValueInput(tag, registries);
        input.readChild(ITEMS_KEY, this.items);
        this.loadLegacyStack(input, GEM_KEY, GEM_SLOT);
        this.loadLegacyStack(input, INPUT_KEY, INPUT_SLOT);
        this.powered = false;
    }

    @Override
    protected void saveAdditional(final net.minecraft.nbt.CompoundTag tag, final net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        final ValueOutput output = new ValueOutput(tag, registries);
        output.putChild(ITEMS_KEY, this.items);
    }

    public boolean canInsertGem(final ItemStack stack) {
        return this.items.stack(GEM_SLOT).isEmpty() && this.mayPlaceInSlot(GEM_SLOT, stack);
    }

    public boolean canInsertInput(final ItemStack stack) {
        return this.items.stack(INPUT_SLOT).isEmpty() && this.mayPlaceInSlot(INPUT_SLOT, stack);
    }

    public void insertGem(final ItemStack stack) {
        this.items.setStack(GEM_SLOT, stack.copyWithCount(1));
    }

    public void insertInput(final ItemStack stack) {
        this.items.setStack(INPUT_SLOT, stack.copy());
    }

    public boolean hasGem() {
        return !this.items.stack(GEM_SLOT).isEmpty();
    }

    public boolean hasInput() {
        return !this.items.stack(INPUT_SLOT).isEmpty();
    }

    public ItemStack removeGem() {
        return this.removeStackNoUpdate(GEM_SLOT);
    }

    public ItemStack removeInput() {
        return this.removeStackNoUpdate(INPUT_SLOT);
    }

    public ItemStack getStackInSlot(final int slot) {
        return this.items.stack(slot);
    }

    public void setStackInSlot(final int slot, final ItemStack stack) {
        if (!isMachineSlot(slot)) {
            return;
        }
        if (stack.isEmpty()) {
            this.setMachineStack(slot, ItemStack.EMPTY);
            return;
        }
        if (this.mayPlaceInSlot(slot, stack)) {
            this.setMachineStack(slot, slot == GEM_SLOT ? stack.copyWithCount(1) : stack.copy());
        }
    }

    public ItemStack removeStack(final int slot, final int amount) {
        if (!isMachineSlot(slot) || amount <= 0) {
            return ItemStack.EMPTY;
        }
        final ItemStack stack = this.getStackInSlot(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = stack.split(amount);
        if (stack.isEmpty()) {
            this.setMachineStack(slot, ItemStack.EMPTY);
        } else {
            this.setChanged();
        }
        return removed;
    }

    public ItemStack removeStackNoUpdate(final int slot) {
        if (!isMachineSlot(slot)) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.items.stack(slot);
        this.items.setStack(slot, ItemStack.EMPTY);
        return removed;
    }

    public boolean mayPlaceInSlot(final int slot, final ItemStack stack) {
        return switch (slot) {
            case GEM_SLOT -> stack.getItem() instanceof HealthGemItem;
            case INPUT_SLOT -> !stack.isEmpty() && !(stack.getItem() instanceof HealthGemItem);
            default -> false;
        };
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return this.items;
    }

    public void dropContents(final ServerLevel level) {
        Containers.dropContents(level, this.worldPosition, new SimpleContainer(this.removeGem(), this.removeInput()));
    }

    public void updatePowered(final ServerLevel level, final boolean powered) {
        if (this.powered == powered) {
            return;
        }
        this.powered = powered;
        this.setChanged();
        if (powered) {
            this.tryCraft(level);
        }
    }

    public boolean hasValidMultiblock(final ServerLevel level) {
        final BlockPos[] pillars = {
                this.worldPosition.north().west(),
                this.worldPosition.north().east(),
                this.worldPosition.south().west(),
                this.worldPosition.south().east()
        };
        for (final BlockPos pillar : pillars) {
            if (!level.getBlockState(pillar).is(BlockTags.LOGS)
                    || !level.getBlockState(pillar.below()).is(BlockTags.LOGS)) {
                return false;
            }
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                final BlockPos checkPos = this.worldPosition.offset(x, 1, z);
                final BlockState state = level.getBlockState(checkPos);
                if (x == 0 && z == 0) {
                    if (!state.is(ModBlocks.DARK_MATTER_BLOCK.get())) {
                        return false;
                    }
                } else if (!state.is(BlockTags.LEAVES)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void tryCraft(final ServerLevel level) {
        if (!this.hasValidMultiblock(level)) {
            return;
        }

        final BlockPos targetPos = this.worldPosition.below();
        final ItemStack inputStack = this.items.stack(INPUT_SLOT);
        final ItemStack gemStack = this.items.stack(GEM_SLOT);
        final Optional<InfusionRecipes.Match> recipe = InfusionRecipes.find(
                level,
                level.getBlockState(targetPos),
                inputStack
        );
        if (recipe.isEmpty() || !HealthGemItem.canConsumeStoredHealth(gemStack, recipe.get().healthCost())) {
            return;
        }
        if (!level.destroyBlock(targetPos, false)) {
            return;
        }

        HealthGemItem.consumeStoredHealth(gemStack, recipe.get().healthCost());
        inputStack.shrink(recipe.get().ingredientCount());
        if (inputStack.isEmpty()) {
            this.items.setStack(INPUT_SLOT, ItemStack.EMPTY);
        }
        Block.popResource(level, targetPos, recipe.get().createOutput());
        this.setChanged();
    }

    private void loadLegacyStack(final ValueInput input, final String key, final int slot) {
        if (!this.items.stack(slot).isEmpty()) {
            return;
        }
        input.read(key, ItemStack.OPTIONAL_CODEC)
                .filter(stack -> !stack.isEmpty())
                .ifPresent(stack -> this.items.setStack(slot, stack));
    }

    private void setMachineStack(final int slot, final ItemStack stack) {
        this.items.setStack(slot, stack);
    }

    private static boolean isMachineSlot(final int slot) {
        return slot >= 0 && slot < SLOT_COUNT;
    }

    private static final class LifeInfuserItemHandler extends ItemStacksResourceHandler {
        private final LifeInfuserBlockEntity owner;

        private LifeInfuserItemHandler(final LifeInfuserBlockEntity owner) {
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
            return isMachineSlot(index) && this.owner.mayPlaceInSlot(index, resource.toStack());
        }

        @Override
        protected int getCapacity(final int index, final ItemResource resource) {
            if (!isMachineSlot(index) || resource.isEmpty()) {
                return 0;
            }
            if (index == GEM_SLOT) {
                return 1;
            }
            return resource.toStack().getMaxStackSize();
        }

        @Override
        protected void onContentsChanged(final int index, final ItemStack previousContents) {
            this.owner.setChanged();
        }

        private ItemStack stack(final int slot) {
            return isMachineSlot(slot) ? this.stacks.get(slot) : ItemStack.EMPTY;
        }

        private void setStack(final int slot, final ItemStack stack) {
            if (!isMachineSlot(slot)) {
                return;
            }
            final ItemStack stored = stack.isEmpty()
                    ? ItemStack.EMPTY
                    : stack.copyWithCount(Math.min(stack.getCount(), this.slotCapacity(slot, stack)));
            this.stacks.set(slot, stored);
            this.owner.setChanged();
        }

        private int slotCapacity(final int slot, final ItemStack stack) {
            if (slot == GEM_SLOT) {
                return 1;
            }
            return stack.isEmpty() ? 0 : stack.getMaxStackSize();
        }
    }
}
