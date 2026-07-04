package committee.nova.mods.skyresources3.common.recipe;

import committee.nova.mods.skyresources3.init.registry.ModRecipeTypes;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public final class ProcessRecipes {
    public static final String COMBUSTION = "combustion";
    public static final String FREEZER = "freezer";
    public static final String FUSION = "fusion";
    public static final String INFUSION = "infusion";
    public static final String KNIFE = "knife";
    public static final String ROCK_GRINDER = "rockgrinder";
    public static final String CAULDRON_CLEAN = "cauldronclean";

    public static Optional<SkyResourcesProcessRecipe> find(
            final ServerLevel level,
            final String process,
            final List<ItemStack> items
    ) {
        return find(level, process, items, Float.MAX_VALUE);
    }

    public static Optional<SkyResourcesProcessRecipe> find(
            final ServerLevel level,
            final String process,
            final List<ItemStack> items,
            final float parameter
    ) {
        return level.getRecipeManager().getRecipeFor(
                ModRecipeTypes.PROCESS_TYPE.get(),
                new ProcessRecipeInput(process, items, parameter),
                level
        );
    }

    public static List<SkyResourcesProcessRecipe> findAll(
            final ServerLevel level,
            final String process,
            final List<ItemStack> items
    ) {
        final ProcessRecipeInput input = new ProcessRecipeInput(process, items, Float.MAX_VALUE);
        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.PROCESS_TYPE.get())
                .stream()
                .filter(recipe -> recipe.matches(input, level))
                .toList();
    }

    private ProcessRecipes() {
    }
}
