package committee.nova.mods.skyresources3.gametest;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.recipe.ProcessRecipes;
import committee.nova.mods.skyresources3.common.recipe.SkyResourcesProcessRecipe;
import committee.nova.mods.skyresources3.init.registry.ModRecipeTypes;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Skyresources3.MODID)
@PrefixGameTestTemplate(false)
public final class Forge1201MigrationGameTests {
    private static final ResourceLocation MENRIL_BERRIES_RECIPE_ID = new ResourceLocation(
            Skyresources3.MODID,
            "process/infusion/integrated_dynamics_menril_berries"
    );
    private static final ResourceLocation MENRIL_SAPLING_RECIPE_ID = new ResourceLocation(
            Skyresources3.MODID,
            "process/infusion/integrated_dynamics_menril_sapling"
    );

    @GameTest(template = "empty")
    public static void knifeRecipeJsonStillLoads(final GameTestHelper helper) {
        final Optional<SkyResourcesProcessRecipe> recipe = ProcessRecipes.find(
                helper.getLevel(),
                ProcessRecipes.KNIFE,
                List.of(new ItemStack(Items.OAK_LOG))
        );
        helper.assertTrue(recipe.isPresent(), "Expected the oak log knife recipe to load on Forge 1.20.1");

        final ItemStack result = recipe.orElseThrow().getResultItem(helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.OAK_PLANKS), "Expected the oak log knife recipe to output oak planks");
        helper.assertTrue(result.getCount() == 6, "Expected the oak log knife recipe to output six planks");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void integrationRecipesRespectForgeConditions(final GameTestHelper helper) {
        final boolean integratedDynamicsLoaded = ModList.get().isLoaded("integrateddynamics");
        final Set<ResourceLocation> recipeIds = helper.getLevel()
                .getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.PROCESS_TYPE.get())
                .stream()
                .map(SkyResourcesProcessRecipe::getId)
                .collect(Collectors.toSet());

        assertConditionalRecipe(helper, recipeIds, MENRIL_BERRIES_RECIPE_ID, integratedDynamicsLoaded);
        assertConditionalRecipe(helper, recipeIds, MENRIL_SAPLING_RECIPE_ID, integratedDynamicsLoaded);
        helper.succeed();
    }

    private static void assertConditionalRecipe(
            final GameTestHelper helper,
            final Set<ResourceLocation> recipeIds,
            final ResourceLocation recipeId,
            final boolean shouldBePresent
    ) {
        final boolean present = recipeIds.contains(recipeId);
        helper.assertTrue(
                present == shouldBePresent,
                "Conditional recipe presence mismatch for " + recipeId + ": expected "
                        + shouldBePresent + " but was " + present
        );
    }

    private Forge1201MigrationGameTests() {
    }
}
