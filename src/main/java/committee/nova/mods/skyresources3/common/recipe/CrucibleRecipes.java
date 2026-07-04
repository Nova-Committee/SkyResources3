package committee.nova.mods.skyresources3.common.recipe;

import committee.nova.mods.skyresources3.init.registry.ModRecipeTypes;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public final class CrucibleRecipes {
    public static Optional<CrucibleRecipe> find(final ServerLevel level, final ItemStack input) {
        if (input.isEmpty()) {
            return Optional.empty();
        }
        return level.getRecipeManager().getRecipeFor(
                ModRecipeTypes.CRUCIBLE_TYPE.get(),
                new SimpleContainer(input.copy()),
                level
        );
    }

    private CrucibleRecipes() {
    }
}
