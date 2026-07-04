package committee.nova.mods.skyresources3.init.integration.jei;

import committee.nova.mods.skyresources3.common.recipe.SkyResourcesProcessRecipe;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

final class SkyResourcesJeiRecipeMaps {
    static List<SkyResourcesProcessRecipe> processRecipes(final String process) {
        return recipes(committee.nova.mods.skyresources3.init.registry.ModRecipeTypes.PROCESS_TYPE.get()).stream()
                .filter(recipe -> recipe.process().equals(process))
                .toList();
    }

    static <I extends Container, T extends Recipe<I>> List<T> recipes(
            final RecipeType<T> type
    ) {
        final RecipeManager recipeManager = Minecraft.getInstance().level == null
                ? null
                : Minecraft.getInstance().level.getRecipeManager();
        if (recipeManager == null) {
            return List.of();
        }
        return recipeManager.getAllRecipesFor(type);
    }

    private SkyResourcesJeiRecipeMaps() {
    }
}
