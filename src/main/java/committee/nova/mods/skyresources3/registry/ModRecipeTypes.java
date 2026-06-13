package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.recipe.CrucibleRecipe;
import committee.nova.mods.skyresources3.recipe.SkyResourcesProcessRecipe;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeTypes {
    private static final Identifier PROCESS_ID = Identifier.fromNamespaceAndPath(Skyresources3.MODID, "process");
    private static final Identifier CRUCIBLE_ID = Identifier.fromNamespaceAndPath(Skyresources3.MODID, "crucible");
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Skyresources3.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Skyresources3.MODID);

    public static final Supplier<RecipeType<SkyResourcesProcessRecipe>> PROCESS_TYPE =
            RECIPE_TYPES.register("process", () -> RecipeType.simple(PROCESS_ID));
    public static final Supplier<RecipeType<CrucibleRecipe>> CRUCIBLE_TYPE =
            RECIPE_TYPES.register("crucible", () -> RecipeType.simple(CRUCIBLE_ID));
    public static final Supplier<RecipeSerializer<SkyResourcesProcessRecipe>> PROCESS_SERIALIZER =
            RECIPE_SERIALIZERS.register("process", SkyResourcesProcessRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<CrucibleRecipe>> CRUCIBLE_SERIALIZER =
            RECIPE_SERIALIZERS.register("crucible", CrucibleRecipe.Serializer::new);

    public static void register(final IEventBus modEventBus) {
        RECIPE_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
    }

    private ModRecipeTypes() {
    }
}
