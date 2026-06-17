package committee.nova.mods.skyresources3.init.integration.jei;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.item.CombustionHeaterItem;
import committee.nova.mods.skyresources3.common.item.CondenserItem;
import committee.nova.mods.skyresources3.common.item.HeatProviderItem;
import committee.nova.mods.skyresources3.common.recipe.CondenserRecipe;
import committee.nova.mods.skyresources3.common.recipe.ProcessRecipes;
import committee.nova.mods.skyresources3.common.recipe.CrucibleRecipe;
import committee.nova.mods.skyresources3.common.recipe.SkyResourcesProcessRecipe;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import committee.nova.mods.skyresources3.init.registry.ModRecipeTypes;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.block.Blocks;

@JeiPlugin
public final class SkyResourcesJeiPlugin implements IModPlugin {
    private static IJeiRuntime runtime;

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(Skyresources3.MODID, "jei");
    }

    @Override
    public void registerCategories(final IRecipeCategoryRegistration registration) {
        final IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                processCategory(
                        guiHelper,
                        SkyResourcesJeiRecipeTypes.PROCESS_COMBUSTION,
                        "combustion",
                        CombustionHeaterItem.forType(ModDataPackRegistries.WOODEN_COMBUSTION_HEATER),
                        "jei.skyresources.process.heat",
                        ProcessRecipeJeiCategory.ParameterMode.INTEGER
                ),
                processCategory(
                        guiHelper,
                        SkyResourcesJeiRecipeTypes.PROCESS_FREEZER,
                        "freezer",
                        new ItemStack(ModItems.MINI_FREEZER.get()),
                        "jei.skyresources.process.ticks",
                        ProcessRecipeJeiCategory.ParameterMode.INTEGER
                ),
                processCategory(
                        guiHelper,
                        SkyResourcesJeiRecipeTypes.PROCESS_FUSION,
                        "fusion",
                        new ItemStack(ModItems.FUSION_TABLE.get()),
                        "jei.skyresources.process.catalyst_use",
                        ProcessRecipeJeiCategory.ParameterMode.FUSION_CATALYST_PERCENT
                ),
                processCategory(
                        guiHelper,
                        SkyResourcesJeiRecipeTypes.PROCESS_INFUSION,
                        "infusion",
                        new ItemStack(ModItems.ALCHEMICAL_INFUSION_STONE.get()),
                        "jei.skyresources.process.health",
                        ProcessRecipeJeiCategory.ParameterMode.INTEGER
                ),
                processCategory(
                        guiHelper,
                        SkyResourcesJeiRecipeTypes.PROCESS_KNIFE,
                        "knife",
                        new ItemStack(ModItems.CACTUS_CUTTING_KNIFE.get()),
                        "",
                        ProcessRecipeJeiCategory.ParameterMode.NONE
                ),
                processCategory(
                        guiHelper,
                        SkyResourcesJeiRecipeTypes.PROCESS_ROCK_GRINDER,
                        "rock_grinder",
                        new ItemStack(ModItems.STONE_GRINDER.get()),
                        "jei.skyresources.process.chance",
                        ProcessRecipeJeiCategory.ParameterMode.PERCENT
                ),
                processCategory(
                        guiHelper,
                        SkyResourcesJeiRecipeTypes.PROCESS_CAULDRON_CLEAN,
                        "cauldron_clean",
                        new ItemStack(Blocks.CAULDRON),
                        "jei.skyresources.process.chance",
                        ProcessRecipeJeiCategory.ParameterMode.PERCENT
                ),
                new CrucibleRecipeJeiCategory(guiHelper),
                new CondenserRecipeJeiCategory(guiHelper),
                new HeatSourceJeiCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(final IRecipeRegistration registration) {
        final RecipeMap syncedRecipes = SkyResourcesJeiRecipeMaps.clientSyncedRecipes();
        final List<SkyResourcesProcessRecipe> combustionRecipes = this.addProcessRecipes(
                registration,
                syncedRecipes,
                SkyResourcesJeiRecipeTypes.PROCESS_COMBUSTION,
                ProcessRecipes.COMBUSTION
        );
        this.addProcessRecipes(registration, syncedRecipes, SkyResourcesJeiRecipeTypes.PROCESS_FREEZER, ProcessRecipes.FREEZER);
        final List<SkyResourcesProcessRecipe> fusionRecipes = this.addProcessRecipes(
                registration,
                syncedRecipes,
                SkyResourcesJeiRecipeTypes.PROCESS_FUSION,
                ProcessRecipes.FUSION
        );
        final List<SkyResourcesProcessRecipe> infusionRecipes = this.addProcessRecipes(
                registration,
                syncedRecipes,
                SkyResourcesJeiRecipeTypes.PROCESS_INFUSION,
                ProcessRecipes.INFUSION
        );
        this.addProcessRecipes(registration, syncedRecipes, SkyResourcesJeiRecipeTypes.PROCESS_KNIFE, ProcessRecipes.KNIFE);
        this.addProcessRecipes(registration, syncedRecipes, SkyResourcesJeiRecipeTypes.PROCESS_ROCK_GRINDER, ProcessRecipes.ROCK_GRINDER);
        this.addProcessRecipes(registration, syncedRecipes, SkyResourcesJeiRecipeTypes.PROCESS_CAULDRON_CLEAN, ProcessRecipes.CAULDRON_CLEAN);
        final List<CrucibleRecipe> crucibleRecipes =
                SkyResourcesJeiRecipeMaps.recipes(syncedRecipes, ModRecipeTypes.CRUCIBLE_TYPE.get());
        final List<CondenserRecipe> condenserRecipes =
                SkyResourcesJeiRecipeMaps.recipes(syncedRecipes, ModRecipeTypes.CONDENSER_TYPE.get());
        registration.addRecipes(
                SkyResourcesJeiRecipeTypes.CRUCIBLE,
                crucibleRecipes
        );
        registration.addRecipes(
                SkyResourcesJeiRecipeTypes.CONDENSER,
                condenserRecipes
        );
        registration.addRecipes(SkyResourcesJeiRecipeTypes.HEAT_SOURCES, HeatSourceJeiRecipe.recipes());
        JeiSourceDescriptions.registerIngredientInfo(
                registration,
                combustionRecipes,
                fusionRecipes,
                infusionRecipes,
                condenserRecipes
        );
    }

    @Override
    public void registerRecipeCatalysts(final IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(
                SkyResourcesJeiRecipeTypes.PROCESS_COMBUSTION,
                combustionHeaters()
        );
        registration.addCraftingStation(SkyResourcesJeiRecipeTypes.PROCESS_COMBUSTION, ModItems.COMBUSTION_CONTROLLER.get());
        registration.addCraftingStation(
                SkyResourcesJeiRecipeTypes.PROCESS_FREEZER,
                ModItems.MINI_FREEZER.get(),
                ModItems.IRON_FREEZER.get(),
                ModItems.LIGHT_FREEZER.get()
        );
        registration.addCraftingStation(SkyResourcesJeiRecipeTypes.PROCESS_FUSION, ModItems.FUSION_TABLE.get());
        registration.addCraftingStation(
                SkyResourcesJeiRecipeTypes.PROCESS_INFUSION,
                ModItems.SANDSTONE_INFUSION_STONE.get(),
                ModItems.RED_SANDSTONE_INFUSION_STONE.get(),
                ModItems.ALCHEMICAL_INFUSION_STONE.get(),
                ModItems.LIFE_INFUSER.get()
        );
        registration.addCraftingStation(
                SkyResourcesJeiRecipeTypes.PROCESS_KNIFE,
                ModItems.CACTUS_CUTTING_KNIFE.get(),
                ModItems.STONE_CUTTING_KNIFE.get(),
                ModItems.IRON_CUTTING_KNIFE.get(),
                ModItems.DIAMOND_CUTTING_KNIFE.get()
        );
        registration.addCraftingStation(
                SkyResourcesJeiRecipeTypes.PROCESS_ROCK_GRINDER,
                ModItems.STONE_GRINDER.get(),
                ModItems.IRON_GRINDER.get(),
                ModItems.DIAMOND_GRINDER.get(),
                ModItems.ROCK_CRUSHER.get()
        );
        registration.addCraftingStation(
                SkyResourcesJeiRecipeTypes.PROCESS_CAULDRON_CLEAN,
                Blocks.CAULDRON,
                ModItems.ROCK_CLEANER.get()
        );
        registration.addCraftingStation(SkyResourcesJeiRecipeTypes.CRUCIBLE, ModItems.CRUCIBLE.get());
        registration.addCraftingStation(SkyResourcesJeiRecipeTypes.CONDENSER, condensers());
        registration.addCraftingStation(
                SkyResourcesJeiRecipeTypes.HEAT_SOURCES,
                Items.LAVA_BUCKET,
                Blocks.TORCH,
                Blocks.MAGMA_BLOCK
        );
        registration.addCraftingStation(SkyResourcesJeiRecipeTypes.HEAT_SOURCES, heatProviders());
    }

    @Override
    public void onRuntimeAvailable(final IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
    }

    public static boolean openGuideRecipe(final String target, final ItemStack icon) {
        final IJeiRuntime jeiRuntime = runtime;
        if (jeiRuntime == null) {
            return false;
        }
        if (target == null || target.isBlank()) {
            return openItemRecipe(jeiRuntime, icon);
        }
        final List<IRecipeType<?>> recipeTypes = SkyResourcesJeiRecipeTypes.byGuideTarget(target);
        if (recipeTypes.isEmpty()) {
            return openItemRecipe(jeiRuntime, icon);
        }
        final IRecipesGui recipesGui = jeiRuntime.getRecipesGui();
        recipesGui.showTypes(recipeTypes);
        if (recipesGui.getParentScreen().isPresent()) {
            return true;
        }
        return openItemRecipe(jeiRuntime, icon);
    }

    private List<SkyResourcesProcessRecipe> addProcessRecipes(
            final IRecipeRegistration registration,
            final RecipeMap syncedRecipes,
            final IRecipeType<SkyResourcesProcessRecipe> recipeType,
            final String process
    ) {
        final List<SkyResourcesProcessRecipe> recipes = SkyResourcesJeiRecipeMaps.processRecipes(syncedRecipes, process);
        registration.addRecipes(recipeType, recipes);
        return recipes;
    }

    private static ProcessRecipeJeiCategory processCategory(
            final IGuiHelper guiHelper,
            final IRecipeType<SkyResourcesProcessRecipe> recipeType,
            final String translationSuffix,
            final ItemStack icon,
            final String parameterKey,
            final ProcessRecipeJeiCategory.ParameterMode parameterMode
    ) {
        return new ProcessRecipeJeiCategory(
                guiHelper,
                recipeType,
                Component.translatable("jei.skyresources.category.process." + translationSuffix),
                icon,
                parameterKey,
                parameterMode
        );
    }

    private static boolean openItemRecipe(final IJeiRuntime jeiRuntime, final ItemStack icon) {
        if (icon == null || icon.isEmpty()) {
            return false;
        }
        final IFocus<ItemStack> focus = jeiRuntime.getJeiHelpers()
                .getFocusFactory()
                .createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, icon.copy());
        final IRecipesGui recipesGui = jeiRuntime.getRecipesGui();
        recipesGui.show(focus);
        return recipesGui.getParentScreen().isPresent();
    }

    private static ItemStack[] combustionHeaters() {
        return ModDataPackRegistries.BUILTIN_COMBUSTION_HEATER_TYPES.stream()
                .map(CombustionHeaterItem::forType)
                .toArray(ItemStack[]::new);
    }

    private static ItemStack[] heatProviders() {
        return ModDataPackRegistries.BUILTIN_HEAT_PROVIDER_TYPES.stream()
                .map(HeatProviderItem::forType)
                .toArray(ItemStack[]::new);
    }

    private static ItemStack[] condensers() {
        return ModDataPackRegistries.BUILTIN_CONDENSER_TYPES.stream()
                .map(CondenserItem::forType)
                .toArray(ItemStack[]::new);
    }
}
