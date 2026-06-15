package committee.nova.mods.skyresources3.init.integration.jei;

import committee.nova.mods.skyresources3.common.recipe.SkyResourcesProcessRecipe;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;

final class SkyResourcesJeiRecipeMaps {
    private static final String JEI_INTERNAL_CLASS = "mezz.jei.common.Internal";
    private static final String SYNCED_RECIPES_METHOD = "getClientSyncedRecipes";

    static RecipeMap clientSyncedRecipes() {
        try {
            final Class<?> internalClass = Class.forName(JEI_INTERNAL_CLASS);
            final Object recipes = internalClass.getMethod(SYNCED_RECIPES_METHOD).invoke(null);
            if (recipes instanceof RecipeMap recipeMap) {
                return recipeMap;
            }
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
                 | LinkageError ignored) {
            return RecipeMap.EMPTY;
        }
        return RecipeMap.EMPTY;
    }

    static List<SkyResourcesProcessRecipe> processRecipes(final RecipeMap recipeMap, final String process) {
        return recipes(recipeMap, committee.nova.mods.skyresources3.init.registry.ModRecipeTypes.PROCESS_TYPE.get()).stream()
                .filter(recipe -> recipe.process().equals(process))
                .toList();
    }

    static <I extends RecipeInput, T extends Recipe<I>> List<T> recipes(
            final RecipeMap recipeMap,
            final RecipeType<T> type
    ) {
        return recipeMap.byType(type).stream()
                .map(RecipeHolder::value)
                .toList();
    }

    private SkyResourcesJeiRecipeMaps() {
    }
}
