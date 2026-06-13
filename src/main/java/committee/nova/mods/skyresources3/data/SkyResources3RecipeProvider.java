package committee.nova.mods.skyresources3.data;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.recipe.ProcessIngredient;
import committee.nova.mods.skyresources3.recipe.ProcessRecipes;
import committee.nova.mods.skyresources3.recipe.SkyResourcesProcessRecipe;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModItems;
import java.util.List;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

        this.shaped(RecipeCategory.TOOLS, ModItems.SURVIVALIST_FISHING_ROD.get())
                .define('X', Items.STICK)
                .define('Y', Items.STRING)
                .pattern(" X")
                .pattern("XY")
                .unlockedBy("has_string", has(Items.STRING))
                .save(this.output);

        this.shaped(RecipeCategory.TOOLS, ModItems.WATER_EXTRACTOR.get())
                .define('X', ItemTags.PLANKS)
                .pattern("XXX")
                .pattern(" XX")
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(this.output);

        this.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.HEAVY_SNOW.get())
                .define('X', ModItems.HEAVY_SNOWBALL.get())
                .pattern("XX")
                .pattern("XX")
                .unlockedBy("has_heavy_snowball", has(ModItems.HEAVY_SNOWBALL.get()))
                .save(this.output);

        this.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.COAL_INFUSED_BLOCK.get())
                .define('X', ModItems.ALCHEMICAL_COAL.get())
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .unlockedBy("has_alchemical_coal", has(ModItems.ALCHEMICAL_COAL.get()))
                .save(this.output);

        this.shapeless(RecipeCategory.MISC, ModItems.ALCHEMICAL_COAL.get(), 9)
                .requires(ModBlocks.COAL_INFUSED_BLOCK.get())
                .unlockedBy("has_coal_infused_block", has(ModBlocks.COAL_INFUSED_BLOCK.get()))
                .save(this.output, "skyresources3:alchemical_coal_from_block");

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

        this.shaped(RecipeCategory.DECORATIONS, ModBlocks.CACTUS_FRUIT_NEEDLE.get())
                .define('X', ModItems.CACTUS_FRUIT.get())
                .define('Y', ModItems.CACTUS_NEEDLE.get())
                .pattern("X")
                .pattern("Y")
                .unlockedBy("has_cactus_fruit", has(ModItems.CACTUS_FRUIT.get()))
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

        this.shaped(RecipeCategory.MISC, ModItems.ALCHEMICAL_GOLD_NEEDLE.get())
                .define('X', ModItems.ALCHEMICAL_GOLD_INGOT.get())
                .pattern("X")
                .pattern("X")
                .unlockedBy("has_alchemical_gold_ingot", has(ModItems.ALCHEMICAL_GOLD_INGOT.get()))
                .save(this.output);

        this.shaped(RecipeCategory.MISC, ModItems.ALCHEMICAL_INFUSION_STONE.get())
                .define('X', ModItems.ALCHEMICAL_GOLD_NEEDLE.get())
                .define('Y', ModItems.ALCHEMICAL_DIAMOND.get())
                .pattern("X")
                .pattern("Y")
                .unlockedBy("has_alchemical_gold_needle", has(ModItems.ALCHEMICAL_GOLD_NEEDLE.get()))
                .save(this.output);

        this.shapeless(RecipeCategory.MISC, ModItems.ENRICHED_BONEMEAL.get(), 4)
                .requires(Items.ROTTEN_FLESH)
                .requires(Items.BONE_MEAL)
                .requires(Items.BONE_MEAL)
                .requires(Items.BONE_MEAL)
                .unlockedBy("has_rotten_flesh", has(Items.ROTTEN_FLESH))
                .save(this.output);

        this.shaped(RecipeCategory.MISC, ModItems.FROZEN_IRON_COOLING_COMPONENT.get())
                .define('X', ModItems.FROZEN_IRON_INGOT.get())
                .define('Y', Items.GLOWSTONE_DUST)
                .define('Z', Items.LAPIS_LAZULI)
                .pattern("XZX")
                .pattern("XYX")
                .pattern("XYX")
                .unlockedBy("has_frozen_iron_ingot", has(ModItems.FROZEN_IRON_INGOT.get()))
                .save(this.output);

        this.shaped(RecipeCategory.MISC, ModItems.QUARTZ_AMPLIFICATION_COMPONENT.get())
                .define('X', Items.QUARTZ)
                .define('Y', Items.LAPIS_LAZULI)
                .define('Z', Items.GLOWSTONE_DUST)
                .pattern("XYX")
                .pattern("XZX")
                .pattern("XZX")
                .unlockedBy("has_quartz", has(Items.QUARTZ))
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

        this.shaped(RecipeCategory.DECORATIONS, ModBlocks.LIFE_INFUSER.get())
                .define('X', ItemTags.LOGS)
                .define('Y', ModItems.ALCHEMICAL_INFUSION_STONE.get())
                .pattern("XXX")
                .pattern(" X ")
                .pattern(" Y ")
                .unlockedBy("has_alchemical_infusion_stone", has(ModItems.ALCHEMICAL_INFUSION_STONE.get()))
                .save(this.output);

        this.shaped(RecipeCategory.DECORATIONS, ModBlocks.LIFE_INJECTOR.get())
                .define('X', ItemTags.LOGS)
                .define('Y', Items.DIAMOND_SWORD)
                .pattern(" Y ")
                .pattern(" X ")
                .pattern("XXX")
                .unlockedBy("has_diamond_sword", has(Items.DIAMOND_SWORD))
                .save(this.output);

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModItems.PLANT_MATTER.get()),
                        RecipeCategory.MISC,
                        Items.CHARCOAL,
                        0.1F,
                        200
                )
                .unlockedBy("has_plant_matter", has(ModItems.PLANT_MATTER.get()))
                .save(this.output, id("charcoal_from_plant_matter"));

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.PETRIFIED_WOOD.get()),
                        RecipeCategory.MISC,
                        Items.CHARCOAL,
                        0.1F,
                        200
                )
                .unlockedBy("has_petrified_wood", has(ModBlocks.PETRIFIED_WOOD.get()))
                .save(this.output, id("charcoal_from_petrified_wood"));

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.DRY_CACTUS.get()),
                        RecipeCategory.MISC,
                        Items.LIGHT_GRAY_DYE,
                        0.2F,
                        200
                )
                .unlockedBy("has_dry_cactus", has(ModBlocks.DRY_CACTUS.get()))
                .save(this.output, id("light_gray_dye_from_dry_cactus"));

        this.buildProcessRecipes();
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

    private void buildProcessRecipes() {
        this.buildFreezerRecipes();
        this.buildRockGrinderRecipes();
        this.buildKnifeRecipes();
        this.buildInfusionRecipes();
        this.buildCombustionRecipes();
        this.buildFusionRecipes();
    }

    private void buildFreezerRecipes() {
        this.processRecipe(
                ProcessRecipes.FREEZER,
                "heavy_snowball",
                40.0F,
                ModItems.HEAVY_SNOWBALL.get(),
                1,
                input(Items.SNOWBALL, 4)
        );
        this.processRecipe(
                ProcessRecipes.FREEZER,
                "coarse_dirt",
                800.0F,
                Blocks.COARSE_DIRT,
                1,
                input(ModBlocks.HEAVY_SNOW.get())
        );
        this.processRecipe(
                ProcessRecipes.FREEZER,
                "frozen_iron_ingot",
                3000.0F,
                ModItems.FROZEN_IRON_INGOT.get(),
                1,
                input(Items.IRON_INGOT)
        );
        this.processRecipe(
                ProcessRecipes.FREEZER,
                "soul_sand",
                1500.0F,
                Blocks.SOUL_SAND,
                1,
                input(ModBlocks.SANDY_NETHERRACK.get())
        );
    }

    private void buildRockGrinderRecipes() {
        this.processRecipe(
                ProcessRecipes.ROCK_GRINDER,
                "gravel",
                1.0F,
                Blocks.GRAVEL,
                1,
                input(Blocks.COBBLESTONE)
        );
        this.processRecipe(ProcessRecipes.ROCK_GRINDER, "sand", 1.0F, Blocks.SAND, 1, input(Blocks.GRAVEL));
        this.processRecipe(ProcessRecipes.ROCK_GRINDER, "flint", 0.3F, Items.FLINT, 1, input(Blocks.GRAVEL));
        this.processRecipe(
                ProcessRecipes.ROCK_GRINDER,
                "crushed_stone",
                0.44F,
                ModItems.CRUSHED_STONE.get(),
                1,
                input(Blocks.STONE)
        );
        this.processRecipe(
                ProcessRecipes.ROCK_GRINDER,
                "crushed_netherrack",
                0.44F,
                ModItems.CRUSHED_NETHERRACK.get(),
                1,
                input(Blocks.NETHERRACK)
        );
        this.processRecipe(
                ProcessRecipes.ROCK_GRINDER,
                "sawdust",
                1.5F,
                ModItems.SAWDUST.get(),
                1,
                input(ItemTags.LOGS)
        );
    }

    private void buildKnifeRecipes() {
        this.processRecipe(
                ProcessRecipes.KNIFE,
                "cactus_fruit",
                0.0F,
                ModItems.CACTUS_FRUIT.get(),
                2,
                input(Blocks.CACTUS)
        );
        this.processRecipe(ProcessRecipes.KNIFE, "melon_slice", 0.0F, Items.MELON_SLICE, 9, input(Blocks.MELON));
        this.processRecipe(ProcessRecipes.KNIFE, "oak_planks", 0.0F, Blocks.OAK_PLANKS, 6, input(Blocks.OAK_LOG));
        this.processRecipe(
                ProcessRecipes.KNIFE,
                "spruce_planks",
                0.0F,
                Blocks.SPRUCE_PLANKS,
                6,
                input(Blocks.SPRUCE_LOG)
        );
        this.processRecipe(ProcessRecipes.KNIFE, "birch_planks", 0.0F, Blocks.BIRCH_PLANKS, 6, input(Blocks.BIRCH_LOG));
        this.processRecipe(
                ProcessRecipes.KNIFE,
                "jungle_planks",
                0.0F,
                Blocks.JUNGLE_PLANKS,
                6,
                input(Blocks.JUNGLE_LOG)
        );
        this.processRecipe(
                ProcessRecipes.KNIFE,
                "acacia_planks",
                0.0F,
                Blocks.ACACIA_PLANKS,
                6,
                input(Blocks.ACACIA_LOG)
        );
        this.processRecipe(
                ProcessRecipes.KNIFE,
                "dark_oak_planks",
                0.0F,
                Blocks.DARK_OAK_PLANKS,
                6,
                input(Blocks.DARK_OAK_LOG)
        );
        this.processRecipe(ProcessRecipes.KNIFE, "sticks_from_planks", 0.0F, Items.STICK, 6, input(ItemTags.PLANKS));
        this.processRecipe(
                ProcessRecipes.KNIFE,
                "petrified_planks",
                0.0F,
                ModBlocks.PETRIFIED_PLANKS.get(),
                6,
                input(ModBlocks.PETRIFIED_WOOD.get())
        );
        this.processRecipe(
                ProcessRecipes.KNIFE,
                "sticks_from_petrified_planks",
                0.0F,
                Items.STICK,
                6,
                input(ModBlocks.PETRIFIED_PLANKS.get())
        );
    }

    private void buildInfusionRecipes() {
        this.infusionRecipe(
                "acacia_sapling",
                10,
                Blocks.ACACIA_SAPLING,
                1,
                input(ModItems.PRIMUS_ALCHEMICAL_DUST.get(), 10),
                input(ModBlocks.CACTUS_FRUIT_NEEDLE.get())
        );
        this.infusionRecipe("oak_sapling", 10, Blocks.OAK_SAPLING, 1, input(Items.APPLE, 4), input(ItemTags.SAPLINGS));
        this.infusionRecipe(
                "coarse_dirt",
                15,
                Blocks.COARSE_DIRT,
                1,
                input(ModItems.CACTUS_FRUIT.get(), 4),
                input(Blocks.RED_SAND)
        );
        this.infusionRecipe(
                "cactus",
                8,
                Blocks.CACTUS,
                3,
                input(ModItems.PRIMUS_ALCHEMICAL_DUST.get(), 6),
                input(Blocks.CACTUS)
        );
        this.infusionRecipe(
                "dead_bush",
                10,
                Blocks.DEAD_BUSH,
                1,
                input(Items.ROTTEN_FLESH, 4),
                input(ItemTags.SAPLINGS)
        );
        this.infusionRecipe(
                "grass_block",
                14,
                Blocks.GRASS_BLOCK,
                1,
                input(Items.WHEAT_SEEDS, 4),
                input(Blocks.DIRT)
        );
        this.infusionRecipe(
                "mycelium",
                16,
                Blocks.MYCELIUM,
                1,
                input(Blocks.BROWN_MUSHROOM, 4),
                input(Blocks.DIRT)
        );
        this.infusionRecipe("apple", 10, Items.APPLE, 1, input(Items.SUGAR, 3), input(Blocks.HAY_BLOCK));
        this.infusionRecipe(
                "jungle_sapling",
                19,
                Blocks.JUNGLE_SAPLING,
                1,
                input(Items.COCOA_BEANS, 10),
                input(ItemTags.SAPLINGS)
        );
        this.infusionRecipe(
                "birch_sapling",
                19,
                Blocks.BIRCH_SAPLING,
                1,
                input(Items.BONE_MEAL, 10),
                input(ItemTags.SAPLINGS)
        );
        this.infusionRecipe(
                "sugar_cane",
                17,
                Items.SUGAR_CANE,
                1,
                input(Items.GLISTERING_MELON_SLICE, 3),
                input(Blocks.PUMPKIN)
        );
        this.infusionRecipe(
                "chorus_flower",
                19,
                Blocks.CHORUS_FLOWER,
                1,
                input(Items.POPPED_CHORUS_FRUIT, 3),
                input(Blocks.RED_MUSHROOM)
        );
        this.infusionRecipe(
                "chorus_fruit",
                12,
                Items.CHORUS_FRUIT,
                1,
                input(Items.ENDER_EYE, 4),
                input(Blocks.MELON)
        );
        this.grassTargetInfusionRecipes("nether_wart", 12, Items.NETHER_WART, 1, input(Items.SPIDER_EYE, 4));
        this.grassTargetInfusionRecipes("red_mushroom", 12, Blocks.RED_MUSHROOM, 1, input(Items.RED_DYE, 8));
        this.grassTargetInfusionRecipes("brown_mushroom", 12, Blocks.BROWN_MUSHROOM, 1, input(Items.COCOA_BEANS, 8));
        this.infusionRecipe(
                "health_gem",
                15,
                ModItems.HEALTH_GEM.get(),
                1,
                input(ModItems.ALCHEMICAL_DIAMOND.get()),
                input(Blocks.CHORUS_FLOWER)
        );
    }

    private void grassTargetInfusionRecipes(
            final String name,
            final int healthCost,
            final ItemLike output,
            final int outputCount,
            final ProcessIngredient ingredient
    ) {
        this.infusionRecipe(
                name + "_from_short_grass",
                healthCost,
                output,
                outputCount,
                ingredient,
                input(Blocks.SHORT_GRASS)
        );
        this.infusionRecipe(name + "_from_fern", healthCost, output, outputCount, ingredient, input(Blocks.FERN));
        this.infusionRecipe(
                name + "_from_tall_grass",
                healthCost,
                output,
                outputCount,
                ingredient,
                input(Blocks.TALL_GRASS)
        );
        this.infusionRecipe(
                name + "_from_large_fern",
                healthCost,
                output,
                outputCount,
                ingredient,
                input(Blocks.LARGE_FERN)
        );
    }

    private void infusionRecipe(
            final String name,
            final int healthCost,
            final ItemLike output,
            final int outputCount,
            final ProcessIngredient ingredient,
            final ProcessIngredient target
    ) {
        this.processRecipe(ProcessRecipes.INFUSION, name, (float) healthCost, output, outputCount, ingredient, target);
    }

    private void buildCombustionRecipes() {
        this.combustionRecipe("coal", 170.0F, Items.COAL, 1, input(Items.CHARCOAL));
        this.combustionRecipe("blaze_powder", 75.0F, Items.BLAZE_POWDER, 3, input(Items.GUNPOWDER));
        this.combustionRecipe("gunpowder", 1120.0F, Items.GUNPOWDER, 3, input(Items.FLINT));
        this.combustionRecipe("diamond", 1600.0F, Items.DIAMOND, 1, input(ModBlocks.COMPRESSED_COAL_BLOCK.get()));
        this.combustionRecipe(
                "red_sand",
                200.0F,
                Blocks.RED_SAND,
                12,
                input(Blocks.SAND, 12),
                input(Items.RED_DYE)
        );
        this.combustionRecipe(
                "dry_cactus",
                400.0F,
                ModBlocks.DRY_CACTUS.get(),
                1,
                input(Blocks.BONE_BLOCK),
                input(Items.LIGHT_GRAY_DYE, 8),
                input(ModItems.PLANT_MATTER.get(), 8)
        );
        this.combustionRecipe(
                "redstone",
                880.0F,
                Items.REDSTONE,
                4,
                input(Items.GUNPOWDER, 2),
                input(Items.BLAZE_POWDER, 2)
        );
        this.combustionRecipe(
                "wheat_seeds",
                50.0F,
                Items.WHEAT_SEEDS,
                1,
                input(Blocks.DEAD_BUSH),
                input(Items.FLINT, 2)
        );
        this.combustionRecipe("dirt", 100.0F, Blocks.DIRT, 1, input(ModItems.PLANT_MATTER.get(), 4));
        this.combustionRecipe(
                "slime_ball",
                200.0F,
                Items.SLIME_BALL,
                1,
                input(ModItems.PLANT_MATTER.get(), 8),
                input(Items.SNOWBALL)
        );
        this.combustionRecipe(
                "poisonous_potato",
                650.0F,
                Items.POISONOUS_POTATO,
                4,
                input(Items.POTATO, 4),
                input(Items.ROTTEN_FLESH)
        );
        this.combustionRecipe(
                "radioactive_mix",
                1400.0F,
                ModItems.RADIOACTIVE_MIX.get(),
                6,
                input(Items.POISONOUS_POTATO),
                input(Items.SPIDER_EYE, 2),
                input(Items.GUNPOWDER, 4)
        );
        this.combustionRecipe(
                "prismarine_shard",
                1900.0F,
                Items.PRISMARINE_SHARD,
                4,
                input(Items.QUARTZ, 4),
                input(Blocks.MOSSY_COBBLESTONE)
        );
        this.combustionRecipe(
                "prismarine_crystals",
                1440.0F,
                Items.PRISMARINE_CRYSTALS,
                4,
                input(Items.QUARTZ),
                input(Blocks.GLASS, 3)
        );
        this.combustionRecipe(
                "netherrack",
                920.0F,
                Blocks.NETHERRACK,
                8,
                input(Blocks.COBBLESTONE, 8),
                input(Items.BLAZE_POWDER, 3)
        );
        this.combustionRecipe(
                "dark_matter",
                2900.0F,
                ModItems.DARK_MATTER.get(),
                1,
                input(Blocks.SOUL_SAND, 5),
                input(ModBlocks.COMPRESSED_COAL_BLOCK.get(), 3),
                input(Items.GOLD_INGOT),
                input(Items.IRON_INGOT, 2),
                input(Items.BRICK, 4)
        );
        this.combustionRecipe(
                "light_matter",
                3400.0F,
                ModItems.LIGHT_MATTER.get(),
                1,
                input(ModBlocks.HEAVY_SNOW.get(), 5),
                input(ModItems.FROZEN_IRON_INGOT.get(), 4),
                input(ModItems.ALCHEMICAL_GOLD_INGOT.get(), 4),
                input(Blocks.END_STONE, 3)
        );
        this.combustionRecipe(
                "glowstone_dust",
                1700.0F,
                Items.GLOWSTONE_DUST,
                5,
                input(Items.REDSTONE, 4),
                input(Items.BLAZE_POWDER, 2)
        );
        this.combustionRecipe(
                "end_stone",
                1800.0F,
                Blocks.END_STONE,
                1,
                input(Blocks.DIORITE, 6),
                input(Items.SUGAR, 2),
                input(Items.ENDER_PEARL, 4),
                input(Items.QUARTZ, 2),
                input(Blocks.BONE_BLOCK, 4)
        );
        this.combustionRecipe(
                "primus_alchemical_dust",
                335.0F,
                ModItems.PRIMUS_ALCHEMICAL_DUST.get(),
                5,
                input(Items.GUNPOWDER, 3),
                input(Items.BLAZE_POWDER, 2),
                input(Items.CHARCOAL)
        );
    }

    private void combustionRecipe(
            final String name,
            final float heat,
            final ItemLike output,
            final int outputCount,
            final ProcessIngredient... inputs
    ) {
        this.processRecipe(ProcessRecipes.COMBUSTION, name, heat, output, outputCount, inputs);
    }

    private void buildFusionRecipes() {
        this.fusionRecipe(
                "secundus_alchemical_dust",
                0.0025F,
                ModItems.SECUNDUS_ALCHEMICAL_DUST.get(),
                5,
                input(Items.REDSTONE, 2),
                input(Items.BLAZE_POWDER, 2),
                input(ModItems.ALCHEMICAL_IRON_INGOT.get())
        );
        this.fusionRecipe(
                "tertius_alchemical_dust",
                0.004F,
                ModItems.TERTIUS_ALCHEMICAL_DUST.get(),
                5,
                input(Items.GLOWSTONE_DUST, 2),
                input(Items.LAPIS_LAZULI, 2),
                input(ModItems.ALCHEMICAL_GOLD_INGOT.get())
        );
        this.fusionRecipe(
                "quartus_alchemical_dust",
                0.035F,
                ModItems.QUARTUS_ALCHEMICAL_DUST.get(),
                5,
                input(ModItems.DARK_MATTER.get()),
                input(ModItems.LIGHT_MATTER.get()),
                input(ModItems.ALCHEMICAL_COAL.get(), 6),
                input(Items.EMERALD, 2),
                input(ModItems.ALCHEMICAL_DIAMOND.get(), 6)
        );
        this.fusionRecipe(
                "alchemical_coal",
                0.0015F,
                ModItems.ALCHEMICAL_COAL.get(),
                1,
                input(Items.COAL),
                input(Items.GUNPOWDER, 3)
        );
        this.fusionRecipe(
                "alchemical_iron_ingot",
                0.002F,
                ModItems.ALCHEMICAL_IRON_INGOT.get(),
                1,
                input(Items.IRON_INGOT),
                input(Items.BLAZE_POWDER, 3)
        );
        this.fusionRecipe(
                "alchemical_gold_ingot",
                0.005F,
                ModItems.ALCHEMICAL_GOLD_INGOT.get(),
                1,
                input(Items.GOLD_INGOT),
                input(Items.GLOWSTONE_DUST, 3)
        );
        this.fusionRecipe(
                "alchemical_diamond",
                0.03F,
                ModItems.ALCHEMICAL_DIAMOND.get(),
                1,
                input(Items.DIAMOND),
                input(Items.REDSTONE, 8)
        );
        this.fusionRecipe(
                "dark_oak_sapling",
                0.0015F,
                Blocks.DARK_OAK_SAPLING,
                1,
                input(Items.NETHER_WART),
                input(Items.GUNPOWDER, 2),
                input(Items.ROTTEN_FLESH)
        );
        this.fusionRecipe(
                "magmafied_stone",
                0.009F,
                ModBlocks.MAGMAFIED_STONE.get(),
                1,
                input(Blocks.MAGMA_BLOCK),
                input(Blocks.STONE),
                input(ModItems.ALCHEMICAL_COAL.get(), 2)
        );
        this.fusionRecipe(
                "alchemical_glass",
                0.004F,
                ModBlocks.ALCHEMICAL_GLASS.get(),
                1,
                input(Blocks.SAND),
                input(ModItems.CRYSTAL_SHARD.get()),
                input(Items.PRISMARINE_CRYSTALS)
        );
        this.fusionRecipe(
                "petrified_wood",
                0.001F,
                ModBlocks.PETRIFIED_WOOD.get(),
                1,
                input(ItemTags.LOGS),
                input(Items.ROTTEN_FLESH),
                input(Items.COAL)
        );
        this.fusionRecipe(
                "dirt_from_soul_sand",
                0.0012F,
                Blocks.DIRT,
                1,
                input(Blocks.SOUL_SAND),
                input(ModItems.ENRICHED_BONEMEAL.get(), 3)
        );
        this.fusionRecipe(
                "crystal_shard_from_glass",
                0.001F,
                ModItems.CRYSTAL_SHARD.get(),
                2,
                input(Blocks.GLASS)
        );
        this.fusionRecipe(
                "crystal_shard_from_alchemical_glass",
                0.006F,
                ModItems.CRYSTAL_SHARD.get(),
                16,
                input(ModBlocks.ALCHEMICAL_GLASS.get())
        );
        this.fusionRecipe(
                "dirt_from_plant_matter",
                0.008F,
                Blocks.DIRT,
                1,
                input(ModItems.PLANT_MATTER.get(), 6)
        );
    }

    private void fusionRecipe(
            final String name,
            final float catalystUse,
            final ItemLike output,
            final int outputCount,
            final ProcessIngredient... inputs
    ) {
        this.processRecipe(ProcessRecipes.FUSION, name, catalystUse, output, outputCount, inputs);
    }

    private void processRecipe(
            final String process,
            final String name,
            final float parameter,
            final ItemLike output,
            final int outputCount,
            final ProcessIngredient... inputs
    ) {
        this.output.accept(
                id("process/" + process + "/" + name),
                new SkyResourcesProcessRecipe(
                        "",
                        process,
                        List.of(inputs),
                        List.of(new ItemStack(output, outputCount)),
                        parameter
                ),
                null
        );
    }

    private static ProcessIngredient input(final ItemLike item) {
        return input(item, 1);
    }

    private static ProcessIngredient input(final ItemLike item, final int count) {
        return new ProcessIngredient(Ingredient.of(item), count);
    }

    private ProcessIngredient input(final TagKey<Item> tag) {
        return input(tag, 1);
    }

    private ProcessIngredient input(final TagKey<Item> tag, final int count) {
        return new ProcessIngredient(this.tag(tag), count);
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
