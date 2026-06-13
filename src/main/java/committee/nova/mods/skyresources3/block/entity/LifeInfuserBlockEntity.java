package committee.nova.mods.skyresources3.block.entity;

import committee.nova.mods.skyresources3.item.HealthGemItem;
import committee.nova.mods.skyresources3.recipe.InfusionRecipes;
import committee.nova.mods.skyresources3.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class LifeInfuserBlockEntity extends BlockEntity {
    public static final int GEM_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int SLOT_COUNT = 2;
    private static final String GEM_KEY = "gem";
    private static final String INPUT_KEY = "input";

    private ItemStack gem = ItemStack.EMPTY;
    private ItemStack input = ItemStack.EMPTY;
    private boolean powered;

    public LifeInfuserBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.LIFE_INFUSER.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        this.gem = input.read(GEM_KEY, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.input = input.read(INPUT_KEY, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.powered = false;
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.store(GEM_KEY, ItemStack.OPTIONAL_CODEC, this.gem);
        output.store(INPUT_KEY, ItemStack.OPTIONAL_CODEC, this.input);
    }

    public boolean canInsertGem(final ItemStack stack) {
        return this.gem.isEmpty() && stack.getItem() instanceof HealthGemItem;
    }

    public boolean canInsertInput(final ItemStack stack) {
        return this.input.isEmpty() && !stack.isEmpty() && !(stack.getItem() instanceof HealthGemItem);
    }

    public void insertGem(final ItemStack stack) {
        this.gem = stack.copyWithCount(1);
        this.setChanged();
    }

    public void insertInput(final ItemStack stack) {
        this.input = stack.copy();
        this.setChanged();
    }

    public boolean hasGem() {
        return !this.gem.isEmpty();
    }

    public boolean hasInput() {
        return !this.input.isEmpty();
    }

    public ItemStack removeGem() {
        final ItemStack removed = this.gem;
        this.gem = ItemStack.EMPTY;
        this.setChanged();
        return removed;
    }

    public ItemStack removeInput() {
        final ItemStack removed = this.input;
        this.input = ItemStack.EMPTY;
        this.setChanged();
        return removed;
    }

    public ItemStack getStackInSlot(final int slot) {
        return switch (slot) {
            case GEM_SLOT -> this.gem;
            case INPUT_SLOT -> this.input;
            default -> ItemStack.EMPTY;
        };
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
        final ItemStack removed = this.getStackInSlot(slot);
        this.setMachineStack(slot, ItemStack.EMPTY);
        return removed;
    }

    public boolean mayPlaceInSlot(final int slot, final ItemStack stack) {
        return switch (slot) {
            case GEM_SLOT -> stack.getItem() instanceof HealthGemItem;
            case INPUT_SLOT -> !stack.isEmpty() && !(stack.getItem() instanceof HealthGemItem);
            default -> false;
        };
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
        final Optional<InfusionRecipes.Match> recipe = InfusionRecipes.find(
                level,
                level.getBlockState(targetPos),
                this.input
        );
        if (recipe.isEmpty() || !HealthGemItem.canConsumeStoredHealth(this.gem, recipe.get().healthCost())) {
            return;
        }
        if (!level.destroyBlock(targetPos, false)) {
            return;
        }

        HealthGemItem.consumeStoredHealth(this.gem, recipe.get().healthCost());
        this.input.shrink(recipe.get().ingredientCount());
        if (this.input.isEmpty()) {
            this.input = ItemStack.EMPTY;
        }
        Block.popResource(level, targetPos, recipe.get().createOutput());
        this.setChanged();
    }

    private void setMachineStack(final int slot, final ItemStack stack) {
        if (slot == GEM_SLOT) {
            this.gem = stack;
        } else if (slot == INPUT_SLOT) {
            this.input = stack;
        }
        this.setChanged();
    }

    private static boolean isMachineSlot(final int slot) {
        return slot >= 0 && slot < SLOT_COUNT;
    }
}
