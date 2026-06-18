package committee.nova.mods.skyresources3.init.integration.jei;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.core.guide.GuideRecipeTargets;
import committee.nova.mods.skyresources3.common.recipe.CondenserRecipe;
import committee.nova.mods.skyresources3.common.recipe.CrucibleRecipe;
import committee.nova.mods.skyresources3.common.recipe.SkyResourcesProcessRecipe;
import java.util.List;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;

final class SkyResourcesJeiRecipeTypes {
    static final RecipeType<SkyResourcesProcessRecipe> PROCESS_COMBUSTION = process("combustion");
    static final RecipeType<SkyResourcesProcessRecipe> PROCESS_FREEZER = process("freezer");
    static final RecipeType<SkyResourcesProcessRecipe> PROCESS_FUSION = process("fusion");
    static final RecipeType<SkyResourcesProcessRecipe> PROCESS_INFUSION = process("infusion");
    static final RecipeType<SkyResourcesProcessRecipe> PROCESS_KNIFE = process("knife");
    static final RecipeType<SkyResourcesProcessRecipe> PROCESS_ROCK_GRINDER = process("rockgrinder");
    static final RecipeType<SkyResourcesProcessRecipe> PROCESS_CAULDRON_CLEAN = process("cauldronclean");
    static final RecipeType<CrucibleRecipe> CRUCIBLE =
            new RecipeType<>(id("crucible"), CrucibleRecipe.class);
    static final RecipeType<CondenserRecipe> CONDENSER =
            new RecipeType<>(id("condenser"), CondenserRecipe.class);
    static final RecipeType<HeatSourceJeiRecipe> HEAT_SOURCES =
            new RecipeType<>(id("heat_sources"), HeatSourceJeiRecipe.class);

    static List<RecipeType<?>> byGuideTarget(final String target) {
        return switch (target) {
            case GuideRecipeTargets.PROCESS_COMBUSTION -> List.of(PROCESS_COMBUSTION);
            case GuideRecipeTargets.PROCESS_FREEZER -> List.of(PROCESS_FREEZER);
            case GuideRecipeTargets.PROCESS_FUSION -> List.of(PROCESS_FUSION);
            case GuideRecipeTargets.PROCESS_INFUSION -> List.of(PROCESS_INFUSION);
            case GuideRecipeTargets.PROCESS_KNIFE -> List.of(PROCESS_KNIFE);
            case GuideRecipeTargets.PROCESS_ROCK_GRINDER -> List.of(PROCESS_ROCK_GRINDER);
            case GuideRecipeTargets.PROCESS_CAULDRON_CLEAN -> List.of(PROCESS_CAULDRON_CLEAN);
            case GuideRecipeTargets.CRUCIBLE -> List.of(CRUCIBLE);
            case GuideRecipeTargets.CONDENSER -> List.of(CONDENSER);
            case GuideRecipeTargets.HEAT_SOURCES -> List.of(HEAT_SOURCES);
            default -> List.of();
        };
    }

    private static RecipeType<SkyResourcesProcessRecipe> process(final String process) {
        return new RecipeType<>(id("process/" + process), SkyResourcesProcessRecipe.class);
    }

    private static ResourceLocation id(final String path) {
        return ResourceLocation.fromNamespaceAndPath(Skyresources3.MODID, path);
    }

    private SkyResourcesJeiRecipeTypes() {
    }
}
