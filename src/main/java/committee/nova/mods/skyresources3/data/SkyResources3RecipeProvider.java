package committee.nova.mods.skyresources3.data;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public final class SkyResources3RecipeProvider extends RecipeProvider {
    private SkyResources3RecipeProvider(final HolderLookup.Provider lookupProvider, final RecipeOutput output) {
        super(lookupProvider, output);
    }

    @Override
    protected void buildRecipes() {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COMPRESSED_COAL_BLOCK.get())
                .define('C', Blocks.COAL_BLOCK)
                .define('X', Items.COAL)
                .pattern("CCC")
                .pattern("CXC")
                .pattern("CCC")
                .unlockedBy("has_coal_block", has(Blocks.COAL_BLOCK))
                .save(this.output);

        this.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SANDY_NETHERRACK.get(), 4)
                .define('S', Blocks.SAND)
                .define('W', Items.NETHER_WART)
                .define('N', Blocks.NETHERRACK)
                .pattern("SW")
                .pattern("NS")
                .unlockedBy("has_netherrack", has(Blocks.NETHERRACK))
                .save(this.output);

        this.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PETRIFIED_PLANKS.get(), 4)
                .requires(ModBlocks.PETRIFIED_WOOD.get())
                .unlockedBy("has_petrified_wood", has(ModBlocks.PETRIFIED_WOOD.get()))
                .save(this.output, "skyresources3:petrified_planks_from_petrified_wood");

        this.shaped(RecipeCategory.MISC, ModItems.PLANT_MATTER.get(), 3)
                .define('X', ModItems.CACTUS_FRUIT.get())
                .pattern(" X ")
                .pattern("XXX")
                .pattern(" X ")
                .unlockedBy("has_cactus_fruit", has(ModItems.CACTUS_FRUIT.get()))
                .save(this.output, "skyresources3:plant_matter_from_cactus_fruit");

        this.shaped(RecipeCategory.FOOD, ModItems.FLESHY_SNOW_NUGGET.get(), 3)
                .define('S', Items.SNOWBALL)
                .define('F', Items.ROTTEN_FLESH)
                .pattern("SS")
                .pattern("SF")
                .unlockedBy("has_rotten_flesh", has(Items.ROTTEN_FLESH))
                .save(this.output);

        this.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.HEAVY_SNOW.get())
                .define('X', ModItems.HEAVY_SNOWBALL.get())
                .pattern("XX")
                .pattern("XX")
                .unlockedBy("has_heavy_snowball", has(ModItems.HEAVY_SNOWBALL.get()))
                .save(this.output);

        this.shapeless(RecipeCategory.MISC, ModItems.HEAVY_EXPLOSIVE_SNOWBALL.get(), 3)
                .requires(ModItems.HEAVY_SNOWBALL.get())
                .requires(ModItems.HEAVY_SNOWBALL.get())
                .requires(ModItems.HEAVY_SNOWBALL.get())
                .requires(Items.GUNPOWDER)
                .unlockedBy("has_heavy_snowball", has(ModItems.HEAVY_SNOWBALL.get()))
                .save(this.output);

        this.shaped(RecipeCategory.TOOLS, ModItems.CACTUS_CUTTING_KNIFE.get())
                .define('#', ModItems.CACTUS_NEEDLE.get())
                .pattern(" #")
                .pattern("# ")
                .unlockedBy("has_cactus_needle", has(ModItems.CACTUS_NEEDLE.get()))
                .save(this.output);

        this.cuttingKnife(ModItems.STONE_CUTTING_KNIFE.get(), Blocks.COBBLESTONE, "has_cobblestone");
        this.cuttingKnife(ModItems.IRON_CUTTING_KNIFE.get(), Items.IRON_INGOT, "has_iron_ingot");
        this.cuttingKnife(ModItems.DIAMOND_CUTTING_KNIFE.get(), Items.DIAMOND, "has_diamond");
        this.grinder(ModItems.STONE_GRINDER.get(), Blocks.COBBLESTONE, "has_cobblestone");
        this.grinder(ModItems.IRON_GRINDER.get(), Items.IRON_INGOT, "has_iron_ingot");
        this.grinder(ModItems.DIAMOND_GRINDER.get(), Items.DIAMOND, "has_diamond");

        this.shaped(RecipeCategory.MISC, ModItems.SANDSTONE_INFUSION_STONE.get())
                .define('X', ModItems.CACTUS_NEEDLE.get())
                .define('Y', Blocks.SANDSTONE)
                .pattern("X")
                .pattern("Y")
                .unlockedBy("has_cactus_needle", has(ModItems.CACTUS_NEEDLE.get()))
                .save(this.output);

        this.shaped(RecipeCategory.MISC, ModItems.RED_SANDSTONE_INFUSION_STONE.get())
                .define('X', ModItems.CACTUS_NEEDLE.get())
                .define('Y', Blocks.RED_SANDSTONE)
                .pattern("X")
                .pattern("Y")
                .unlockedBy("has_cactus_needle", has(ModItems.CACTUS_NEEDLE.get()))
                .save(this.output);

        this.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DARK_MATTER_BLOCK.get())
                .define('X', ModItems.DARK_MATTER.get())
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .unlockedBy("has_dark_matter", has(ModItems.DARK_MATTER.get()))
                .save(this.output);

        this.shapeless(RecipeCategory.MISC, ModItems.DARK_MATTER.get(), 9)
                .requires(ModBlocks.DARK_MATTER_BLOCK.get())
                .unlockedBy("has_dark_matter_block", has(ModBlocks.DARK_MATTER_BLOCK.get()))
                .save(this.output, "skyresources3:dark_matter_from_block");

        this.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIGHT_MATTER_BLOCK.get())
                .define('X', ModItems.LIGHT_MATTER.get())
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .unlockedBy("has_light_matter", has(ModItems.LIGHT_MATTER.get()))
                .save(this.output);

        this.shapeless(RecipeCategory.MISC, ModItems.LIGHT_MATTER.get(), 9)
                .requires(ModBlocks.LIGHT_MATTER_BLOCK.get())
                .unlockedBy("has_light_matter_block", has(ModBlocks.LIGHT_MATTER_BLOCK.get()))
                .save(this.output, "skyresources3:light_matter_from_block");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.PETRIFIED_WOOD.get()),
                        RecipeCategory.MISC,
                        Items.CHARCOAL,
                        0.1F,
                        200
                )
                .unlockedBy("has_petrified_wood", has(ModBlocks.PETRIFIED_WOOD.get()))
                .save(this.output, id("charcoal_from_petrified_wood"));
    }

    private void cuttingKnife(final ItemLike result, final ItemLike material, final String unlockName) {
        this.shaped(RecipeCategory.TOOLS, result)
                .define('#', material)
                .define('X', Items.STICK)
                .pattern("#  ")
                .pattern("#X ")
                .pattern(" #X")
                .unlockedBy(unlockName, has(material))
                .save(this.output);
    }

    private void grinder(final ItemLike result, final ItemLike material, final String unlockName) {
        this.shaped(RecipeCategory.TOOLS, result)
                .define('#', material)
                .define('X', Items.STICK)
                .pattern("#  ")
                .pattern(" # ")
                .pattern("  X")
                .unlockedBy(unlockName, has(material))
                .save(this.output);
    }

    private static ResourceKey<Recipe<?>> id(final String path) {
        return ResourceKey.create(
                Registries.RECIPE,
                Identifier.fromNamespaceAndPath(Skyresources3.MODID, path)
        );
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(
                final HolderLookup.Provider lookupProvider,
                final RecipeOutput output
        ) {
            return new SkyResources3RecipeProvider(lookupProvider, output);
        }

        @Override
        public String getName() {
            return "SkyResources3 Recipes";
        }
    }
}
