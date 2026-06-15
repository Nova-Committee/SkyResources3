package committee.nova.mods.skyresources3.init.integration.jei;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.core.guide.GuideRecipeTargets;
import committee.nova.mods.skyresources3.common.recipe.CondenserRecipe;
import committee.nova.mods.skyresources3.common.recipe.CrucibleRecipe;
import committee.nova.mods.skyresources3.common.recipe.SkyResourcesProcessRecipe;
import java.util.List;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.resources.Identifier;

final class SkyResourcesJeiRecipeTypes {
    static final IRecipeType<SkyResourcesProcessRecipe> PROCESS_COMBUSTION = process("combustion");
    static final IRecipeType<SkyResourcesProcessRecipe> PROCESS_FREEZER = process("freezer");
    static final IRecipeType<SkyResourcesProcessRecipe> PROCESS_FUSION = process("fusion");
    static final IRecipeType<SkyResourcesProcessRecipe> PROCESS_INFUSION = process("infusion");
    static final IRecipeType<SkyResourcesProcessRecipe> PROCESS_KNIFE = process("knife");
    static final IRecipeType<SkyResourcesProcessRecipe> PROCESS_ROCK_GRINDER = process("rockgrinder");
    static final IRecipeType<SkyResourcesProcessRecipe> PROCESS_CAULDRON_CLEAN = process("cauldronclean");
    static final IRecipeType<CrucibleRecipe> CRUCIBLE =
            IRecipeType.create(id("crucible"), CrucibleRecipe.class);
    static final IRecipeType<CondenserRecipe> CONDENSER =
            IRecipeType.create(id("condenser"), CondenserRecipe.class);
    static final IRecipeType<HeatSourceJeiRecipe> HEAT_SOURCES =
            IRecipeType.create(id("heat_sources"), HeatSourceJeiRecipe.class);

    static List<IRecipeType<?>> byGuideTarget(final String target) {
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

    private static IRecipeType<SkyResourcesProcessRecipe> process(final String process) {
        return IRecipeType.create(id("process/" + process), SkyResourcesProcessRecipe.class);
    }

    private static Identifier id(final String path) {
        return Identifier.fromNamespaceAndPath(Skyresources3.MODID, path);
    }

    private SkyResourcesJeiRecipeTypes() {
    }
}
