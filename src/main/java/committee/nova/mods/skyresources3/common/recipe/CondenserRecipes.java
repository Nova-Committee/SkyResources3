package committee.nova.mods.skyresources3.common.recipe;

import committee.nova.mods.skyresources3.init.registry.ModRecipeTypes;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public final class CondenserRecipes {
    public static Optional<CondenserRecipe> find(
            final ServerLevel level,
            final ItemStack catalyst,
            final CondenserRecipe.Source source
    ) {
        return level.getRecipeManager().getRecipeFor(
                ModRecipeTypes.CONDENSER_TYPE.get(),
                new CondenserRecipeInput(catalyst, source),
                level
        );
    }

    public static boolean hasCatalyst(final ServerLevel level, final ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.CONDENSER_TYPE.get())
                .stream()
                .anyMatch(recipe -> recipe.isCatalyst(stack));
    }

    private CondenserRecipes() {
    }
}
