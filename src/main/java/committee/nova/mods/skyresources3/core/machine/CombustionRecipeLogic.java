package committee.nova.mods.skyresources3.core.machine;

import committee.nova.mods.skyresources3.common.recipe.ProcessIngredient;
import committee.nova.mods.skyresources3.common.recipe.ProcessRecipes;
import committee.nova.mods.skyresources3.common.recipe.SkyResourcesProcessRecipe;
import committee.nova.mods.skyresources3.init.registry.ModRecipeTypes;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public final class CombustionRecipeLogic {
    public static List<ItemEntity> itemEntities(final ServerLevel level, final BlockPos chamberPos) {
        return level.getEntitiesOfClass(ItemEntity.class, new AABB(chamberPos));
    }

    public static List<ItemStack> aggregate(final List<ItemEntity> entities) {
        final List<ItemStack> stacks = new java.util.ArrayList<>();
        for (final ItemEntity entity : entities) {
            addStack(stacks, entity.getItem());
        }
        return stacks;
    }

    public static Optional<SkyResourcesProcessRecipe> findRecipe(
            final ServerLevel level,
            final List<ItemStack> stacks,
            final float heat,
            final Predicate<ItemStack> outputFilter
    ) {
        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.PROCESS_TYPE.get())
                .stream()
                .filter(recipe -> recipe.process().equals(ProcessRecipes.COMBUSTION))
                .filter(recipe -> recipe.parameter() <= heat)
                .filter(recipe -> recipe.outputs().stream().findFirst().filter(outputFilter).isPresent())
                .filter(recipe -> canCraft(recipe, stacks))
                .max(Comparator.comparingInt(CombustionRecipeLogic::inputEntryCount)
                        .thenComparingInt(CombustionRecipeLogic::inputItemCount));
    }

    public static boolean canCraft(final SkyResourcesProcessRecipe recipe, final List<ItemStack> stacks) {
        final List<ItemStack> remaining = stacks.stream().map(ItemStack::copy).collect(java.util.stream.Collectors.toList());
        for (final ProcessIngredient ingredient : recipe.inputs()) {
            if (!consumeIngredient(remaining, ingredient)) {
                return false;
            }
        }
        return true;
    }

    public static void consumeInputs(final SkyResourcesProcessRecipe recipe, final List<ItemStack> stacks) {
        for (final ProcessIngredient ingredient : recipe.inputs()) {
            consumeIngredient(stacks, ingredient);
        }
        stacks.removeIf(ItemStack::isEmpty);
    }

    private static void addStack(final List<ItemStack> stacks, final ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        for (final ItemStack current : stacks) {
            if (ItemStack.isSameItemSameTags(current, stack)) {
                current.grow(stack.getCount());
                return;
            }
        }
        stacks.add(stack.copy());
    }

    private static boolean consumeIngredient(final List<ItemStack> stacks, final ProcessIngredient ingredient) {
        for (final ItemStack stack : stacks) {
            if (ingredient.matches(stack)) {
                stack.shrink(ingredient.count());
                return true;
            }
        }
        return false;
    }

    private static int inputEntryCount(final SkyResourcesProcessRecipe recipe) {
        return recipe.inputs().size();
    }

    private static int inputItemCount(final SkyResourcesProcessRecipe recipe) {
        return recipe.inputs().stream().mapToInt(ProcessIngredient::count).sum();
    }

    private CombustionRecipeLogic() {
    }
}
