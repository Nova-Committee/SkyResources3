package committee.nova.mods.skyresources3.common.block.entity;

import committee.nova.mods.skyresources3.common.menu.DirtFurnaceMenu;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemResource;
import committee.nova.mods.skyresources3.common.compat.transfer.item.WorldlyContainerWrapper;
import org.jetbrains.annotations.Nullable;

public final class DirtFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    private static final int FUEL_RATE = 3;
    private static final int COOKING_TOTAL_TIME = 200;

    public DirtFurnaceBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.DIRT_FURNACE.get(), pos, blockState, RecipeType.SMELTING);
        this.setCookingTotalTime(COOKING_TOTAL_TIME);
    }

    public void serverTick(final ServerLevel level, final BlockPos pos, BlockState state) {
        final boolean wasLit = this.isLit();
        boolean changed = false;
        if (wasLit) {
            this.setLitTime(Math.max(0, this.getLitTime() - FUEL_RATE));
        }

        final ItemStack fuel = this.items.get(SLOT_FUEL);
        final ItemStack input = this.items.get(SLOT_INPUT);
        final boolean hasInput = !input.isEmpty();
        final boolean hasFuel = !fuel.isEmpty();
        if (this.isLit() || hasFuel && hasInput) {
            final SingleRecipeInput recipeInput = new SingleRecipeInput(input);
            final RecipeHolder<? extends AbstractCookingRecipe> recipe = hasInput
                    ? getSmeltingRecipe(level, recipeInput)
                    : null;
            final int maxStackSize = this.getMaxStackSize();

            if (!this.isLit() && canBurn(level.registryAccess(), recipe, recipeInput, this.items, maxStackSize)) {
                final int burnDuration = this.getBurnDuration(fuel);
                this.setLitTime(burnDuration);
                this.setLitDuration(burnDuration);
                if (this.isLit()) {
                    changed = true;
                    this.consumeFuel(fuel, hasFuel);
                }
            }

            if (this.isLit() && canBurn(level.registryAccess(), recipe, recipeInput, this.items, maxStackSize)) {
                this.setCookingTimer(this.getCookingTimer() + 1);
                if (this.getCookingTimer() >= this.getCookingTotalTime()) {
                    this.setCookingTimer(0);
                    this.setCookingTotalTime(COOKING_TOTAL_TIME);
                    if (burn(level.registryAccess(), recipe, recipeInput, this.items, maxStackSize)) {
                        this.setRecipeUsed(recipe);
                    }
                    changed = true;
                }
            } else {
                this.setCookingTimer(0);
            }
        } else if (!this.isLit() && this.getCookingTimer() > 0) {
            this.setCookingTimer(Mth.clamp(this.getCookingTimer() - 2, 0, this.getCookingTotalTime()));
        }

        if (wasLit != this.isLit()) {
            changed = true;
            state = state.setValue(AbstractFurnaceBlock.LIT, this.isLit());
            level.setBlock(pos, state, 3);
        }

        if (changed) {
            setChanged(level, pos, state);
        }
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    public ResourceHandler<ItemResource> getItemHandler(@Nullable final Direction direction) {
        return new WorldlyContainerWrapper(this, direction);
    }

    @Override
    public void setItem(final int index, final ItemStack stack) {
        final ItemStack current = this.items.get(index);
        final boolean sameStack = !stack.isEmpty() && ItemStack.isSameItemSameComponents(current, stack);
        super.setItem(index, stack);
        if (index == SLOT_INPUT && !sameStack) {
            this.setCookingTimer(0);
            this.setCookingTotalTime(COOKING_TOTAL_TIME);
            this.setChanged();
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.skyresources.dirt_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(final int containerId, final Inventory inventory) {
        return new DirtFurnaceMenu(containerId, inventory, this, this.dataAccess);
    }

    private void consumeFuel(final ItemStack fuel, final boolean hasFuel) {
        final Item fuelItem = fuel.getItem();
        final ItemStack remainder = fuelItem.hasCraftingRemainingItem()
                ? new ItemStack(fuelItem.getCraftingRemainingItem())
                : ItemStack.EMPTY;
        if (!remainder.isEmpty()) {
            this.items.set(SLOT_FUEL, remainder);
        } else if (hasFuel) {
            fuel.shrink(1);
        }
    }

    private boolean isLit() {
        return this.getLitTime() > 0;
    }

    private int getLitTime() {
        return this.dataAccess.get(DATA_LIT_TIME);
    }

    private void setLitTime(final int value) {
        this.dataAccess.set(DATA_LIT_TIME, value);
    }

    private void setLitDuration(final int value) {
        this.dataAccess.set(DATA_LIT_DURATION, value);
    }

    private int getCookingTimer() {
        return this.dataAccess.get(DATA_COOKING_PROGRESS);
    }

    private void setCookingTimer(final int value) {
        this.dataAccess.set(DATA_COOKING_PROGRESS, value);
    }

    private int getCookingTotalTime() {
        return this.dataAccess.get(DATA_COOKING_TOTAL_TIME);
    }

    private void setCookingTotalTime(final int value) {
        this.dataAccess.set(DATA_COOKING_TOTAL_TIME, value);
    }

    @Nullable
    private static RecipeHolder<? extends AbstractCookingRecipe> getSmeltingRecipe(
            final ServerLevel level,
            final SingleRecipeInput recipeInput
    ) {
        return level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, recipeInput, level)
                .<RecipeHolder<? extends AbstractCookingRecipe>>map(holder -> holder)
                .orElse(null);
    }

    private static boolean canBurn(
            final RegistryAccess registryAccess,
            @Nullable final RecipeHolder<? extends AbstractCookingRecipe> recipe,
            final SingleRecipeInput recipeInput,
            final NonNullList<ItemStack> items,
            final int maxStackSize
    ) {
        if (items.get(SLOT_INPUT).isEmpty() || recipe == null) {
            return false;
        }

        final ItemStack result = recipe.value().assemble(recipeInput, registryAccess);
        if (result.isEmpty()) {
            return false;
        }

        final ItemStack output = items.get(SLOT_RESULT);
        if (output.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(output, result)) {
            return false;
        }
        final int combinedCount = output.getCount() + result.getCount();
        return combinedCount <= maxStackSize && combinedCount <= output.getMaxStackSize()
                || combinedCount <= result.getMaxStackSize();
    }

    private static boolean burn(
            final RegistryAccess registryAccess,
            @Nullable final RecipeHolder<? extends AbstractCookingRecipe> recipe,
            final SingleRecipeInput recipeInput,
            final NonNullList<ItemStack> items,
            final int maxStackSize
    ) {
        if (recipe == null || !canBurn(registryAccess, recipe, recipeInput, items, maxStackSize)) {
            return false;
        }

        final ItemStack input = items.get(SLOT_INPUT);
        final ItemStack result = recipe.value().assemble(recipeInput, registryAccess);
        final ItemStack output = items.get(SLOT_RESULT);
        if (output.isEmpty()) {
            items.set(SLOT_RESULT, result.copy());
        } else if (ItemStack.isSameItemSameComponents(output, result)) {
            output.grow(result.getCount());
        }

        if (input.is(Blocks.WET_SPONGE.asItem()) && !items.get(SLOT_FUEL).isEmpty() && items.get(SLOT_FUEL).is(Items.BUCKET)) {
            items.set(SLOT_FUEL, new ItemStack(Items.WATER_BUCKET));
        }

        input.shrink(1);
        return true;
    }
}
