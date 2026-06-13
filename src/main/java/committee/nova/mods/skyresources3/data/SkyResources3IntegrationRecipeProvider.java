package committee.nova.mods.skyresources3.data;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.recipe.ProcessRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

public final class SkyResources3IntegrationRecipeProvider implements DataProvider {
    private static final String INTEGRATED_DYNAMICS = "integrateddynamics";
    private static final int MENRIL_HEALTH_COST = 12;

    private final PackOutput.PathProvider recipePathProvider;

    public SkyResources3IntegrationRecipeProvider(final PackOutput output) {
        this.recipePathProvider = output.createRegistryElementsPathProvider(Registries.RECIPE);
    }

    @Override
    public CompletableFuture<?> run(final CachedOutput output) {
        return CompletableFuture.allOf(
                this.saveInfusionRecipe(
                        output,
                        "integrated_dynamics_menril_berries",
                        "integrateddynamics:menril_berries",
                        input("minecraft:light_blue_dye", 4),
                        input("minecraft:red_mushroom")
                ),
                this.saveInfusionRecipe(
                        output,
                        "integrated_dynamics_menril_sapling",
                        "integrateddynamics:menril_sapling",
                        input("integrateddynamics:menril_berries", 4),
                        input("minecraft:birch_sapling")
                )
        );
    }

    @Override
    public String getName() {
        return "SkyResources3 Integration Recipes";
    }

    private CompletableFuture<?> saveInfusionRecipe(
            final CachedOutput output,
            final String name,
            final String result,
            final JsonObject... inputs
    ) {
        final Identifier id = Identifier.fromNamespaceAndPath(
                Skyresources3.MODID,
                "process/infusion/" + name
        );
        return DataProvider.saveStable(
                output,
                infusionRecipe(result, inputs),
                this.recipePathProvider.json(id)
        );
    }

    private static JsonObject infusionRecipe(final String result, final JsonObject... inputs) {
        final JsonObject recipe = new JsonObject();
        recipe.add("neoforge:conditions", modLoadedConditions());
        recipe.addProperty("type", Skyresources3.MODID + ":process");
        recipe.addProperty("process", ProcessRecipes.INFUSION);
        recipe.add("inputs", inputs(inputs));
        recipe.add("outputs", outputs(result));
        recipe.addProperty("parameter", (float) MENRIL_HEALTH_COST);
        return recipe;
    }

    private static JsonArray modLoadedConditions() {
        final JsonObject condition = new JsonObject();
        condition.addProperty("type", "neoforge:mod_loaded");
        condition.addProperty("modid", INTEGRATED_DYNAMICS);

        final JsonArray conditions = new JsonArray();
        conditions.add(condition);
        return conditions;
    }

    private static JsonArray inputs(final JsonObject... inputs) {
        final JsonArray json = new JsonArray();
        for (final JsonObject input : inputs) {
            json.add(input);
        }
        return json;
    }

    private static JsonArray outputs(final String result) {
        final JsonObject output = new JsonObject();
        output.addProperty("id", result);
        output.addProperty("count", 1);

        final JsonArray outputs = new JsonArray();
        outputs.add(output);
        return outputs;
    }

    private static JsonObject input(final String ingredient) {
        return input(ingredient, 1);
    }

    private static JsonObject input(final String ingredient, final int count) {
        final JsonObject input = new JsonObject();
        input.addProperty("ingredient", ingredient);
        if (count != 1) {
            input.addProperty("count", count);
        }
        return input;
    }
}
