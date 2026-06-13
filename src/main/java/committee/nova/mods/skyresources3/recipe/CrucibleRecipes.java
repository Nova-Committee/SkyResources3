package committee.nova.mods.skyresources3.recipe;

import committee.nova.mods.skyresources3.registry.ModRecipeTypes;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public final class CrucibleRecipes {
    public static Optional<RecipeHolder<CrucibleRecipe>> find(final ServerLevel level, final ItemStack input) {
        if (input.isEmpty()) {
            return Optional.empty();
        }
        return level.recipeAccess().getRecipeFor(
                ModRecipeTypes.CRUCIBLE_TYPE.get(),
                new SingleRecipeInput(input.copy()),
                level
        );
    }

    private CrucibleRecipes() {
    }
}
