package committee.nova.mods.skyresources3.init.registry;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.entity.HeavyExplosiveSnowball;
import committee.nova.mods.skyresources3.common.entity.HeavySnowball;
import committee.nova.mods.skyresources3.common.item.CombustionHeaterItem;
import committee.nova.mods.skyresources3.common.item.CondenserItem;
import committee.nova.mods.skyresources3.common.item.CuttingKnifeItem;
import committee.nova.mods.skyresources3.common.item.DirtyGemItem;
import committee.nova.mods.skyresources3.common.item.HealthGemItem;
import committee.nova.mods.skyresources3.common.item.HeatProviderItem;
import committee.nova.mods.skyresources3.common.item.HeavySnowballItem;
import committee.nova.mods.skyresources3.common.item.InstantBonemealItem;
import committee.nova.mods.skyresources3.common.item.InfusionStoneItem;
import committee.nova.mods.skyresources3.common.item.MachineCasingItem;
import committee.nova.mods.skyresources3.common.item.OreAlchemyDustItem;
import committee.nova.mods.skyresources3.common.item.RockGrinderItem;
import committee.nova.mods.skyresources3.common.item.SurvivalistFishingRodItem;
import committee.nova.mods.skyresources3.common.item.WaterExtractorItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Skyresources3.MODID);

    public static final DeferredItem<BlockItem> COMPRESSED_COAL_BLOCK = blockItem("compressed_coal_block", ModBlocks.COMPRESSED_COAL_BLOCK);
    public static final DeferredItem<BlockItem> COAL_INFUSED_BLOCK = blockItem("coal_infused_block", ModBlocks.COAL_INFUSED_BLOCK);
    public static final DeferredItem<BlockItem> SANDY_NETHERRACK = blockItem("sandy_netherrack", ModBlocks.SANDY_NETHERRACK);
    public static final DeferredItem<BlockItem> PETRIFIED_WOOD = blockItem("petrified_wood", ModBlocks.PETRIFIED_WOOD);
    public static final DeferredItem<BlockItem> PETRIFIED_PLANKS = blockItem("petrified_planks", ModBlocks.PETRIFIED_PLANKS);
    public static final DeferredItem<BlockItem> MAGMAFIED_STONE = blockItem("magmafied_stone", ModBlocks.MAGMAFIED_STONE);
    public static final DeferredItem<BlockItem> HEAVY_SNOW = blockItem("heavy_snow", ModBlocks.HEAVY_SNOW);
    public static final DeferredItem<BlockItem> BLAZE_POWDER_BLOCK =
            blockItem("blaze_powder_block", ModBlocks.BLAZE_POWDER_BLOCK);
    public static final DeferredItem<BlockItem> DARK_MATTER_BLOCK = blockItem("dark_matter_block", ModBlocks.DARK_MATTER_BLOCK);
    public static final DeferredItem<BlockItem> LIGHT_MATTER_BLOCK = blockItem("light_matter_block", ModBlocks.LIGHT_MATTER_BLOCK);
    public static final DeferredItem<BlockItem> ALCHEMICAL_GLASS = blockItem("alchemical_glass", ModBlocks.ALCHEMICAL_GLASS);
    public static final DeferredItem<BlockItem> FUSION_TABLE = blockItem("fusion_table", ModBlocks.FUSION_TABLE);
    public static final DeferredItem<BlockItem> DIRT_FURNACE = blockItem("dirt_furnace", ModBlocks.DIRT_FURNACE);
    public static final DeferredItem<BlockItem> QUICK_DROPPER = blockItem("quick_dropper", ModBlocks.QUICK_DROPPER);
    public static final DeferredItem<BlockItem> DARK_MATTER_WARPER =
            blockItem("dark_matter_warper", ModBlocks.DARK_MATTER_WARPER);
    public static final DeferredItem<BlockItem> END_PORTAL_CORE = blockItem("end_portal_core", ModBlocks.END_PORTAL_CORE);
    public static final DeferredItem<BlockItem> SILVERFISH_DISRUPTOR =
            blockItem("silverfish_disruptor", ModBlocks.SILVERFISH_DISRUPTOR);
    public static final DeferredItem<BlockItem> FLUID_DROPPER = blockItem("fluid_dropper", ModBlocks.FLUID_DROPPER);
    public static final DeferredItem<BlockItem> CRUCIBLE = blockItem("crucible", ModBlocks.CRUCIBLE);
    public static final DeferredItem<BlockItem> CRUCIBLE_INSERTER =
            blockItem("crucible_inserter", ModBlocks.CRUCIBLE_INSERTER);
    public static final DeferredItem<BlockItem> ROCK_CRUSHER = blockItem("rock_crusher", ModBlocks.ROCK_CRUSHER);
    public static final DeferredItem<BlockItem> ROCK_CLEANER = blockItem("rock_cleaner", ModBlocks.ROCK_CLEANER);
    public static final DeferredItem<BlockItem> AQUEOUS_CONCENTRATOR =
            blockItem("aqueous_concentrator", ModBlocks.AQUEOUS_CONCENTRATOR);
    public static final DeferredItem<BlockItem> AQUEOUS_DECONCENTRATOR =
            blockItem("aqueous_deconcentrator", ModBlocks.AQUEOUS_DECONCENTRATOR);
    public static final DeferredItem<BlockItem> WILDLIFE_ATTRACTOR =
            blockItem("wildlife_attractor", ModBlocks.WILDLIFE_ATTRACTOR);
    public static final DeferredItem<MachineCasingItem> MACHINE_CASING = ITEMS.registerItem(
            "machine_casing",
            properties -> new MachineCasingItem(ModBlocks.MACHINE_CASING.get(), properties)
    );
    public static final DeferredItem<CombustionHeaterItem> COMBUSTION_HEATER = ITEMS.registerItem(
            "combustion_heater",
            properties -> new CombustionHeaterItem(ModBlocks.COMBUSTION_HEATER.get(), properties)
    );
    public static final DeferredItem<HeatProviderItem> HEAT_PROVIDER = ITEMS.registerItem(
            "heat_provider",
            properties -> new HeatProviderItem(ModBlocks.HEAT_PROVIDER.get(), properties)
    );
    public static final DeferredItem<CondenserItem> CONDENSER = ITEMS.registerItem(
            "condenser",
            properties -> new CondenserItem(ModBlocks.CONDENSER.get(), properties)
    );
    public static final DeferredItem<BlockItem> COMBUSTION_COLLECTOR =
            blockItem("combustion_collector", ModBlocks.COMBUSTION_COLLECTOR);
    public static final DeferredItem<BlockItem> COMBUSTION_CONTROLLER =
            blockItem("combustion_controller", ModBlocks.COMBUSTION_CONTROLLER);
    public static final DeferredItem<BlockItem> MINI_FREEZER = blockItem("mini_freezer", ModBlocks.MINI_FREEZER);
    public static final DeferredItem<BlockItem> IRON_FREEZER = blockItem("iron_freezer", ModBlocks.IRON_FREEZER);
    public static final DeferredItem<BlockItem> LIGHT_FREEZER = blockItem("light_freezer", ModBlocks.LIGHT_FREEZER);
    public static final DeferredItem<BlockItem> LIFE_INFUSER = blockItem("life_infuser", ModBlocks.LIFE_INFUSER);
    public static final DeferredItem<BlockItem> LIFE_INJECTOR = blockItem("life_injector", ModBlocks.LIFE_INJECTOR);
    public static final DeferredItem<BlockItem> CACTUS_FRUIT_NEEDLE =
            blockItem("cactus_fruit_needle", ModBlocks.CACTUS_FRUIT_NEEDLE);
    public static final DeferredItem<BlockItem> DRY_CACTUS = blockItem("dry_cactus", ModBlocks.DRY_CACTUS);

    public static final DeferredItem<Item> CACTUS_FRUIT = ITEMS.registerSimpleItem(
            "cactus_fruit",
            new Item.Properties().food(food(3, 0.4F))
    );
    public static final DeferredItem<Item> FLESHY_SNOW_NUGGET = ITEMS.registerSimpleItem(
            "fleshy_snow_nugget",
            new Item.Properties().food(food(4, 0.3F))
    );
    public static final DeferredItem<Item> CACTUS_NEEDLE = ITEMS.registerSimpleItem("cactus_needle");
    public static final DeferredItem<Item> CRYSTAL_SHARD = ITEMS.registerSimpleItem("crystal_shard");
    public static final DeferredItem<Item> PRIMUS_ALCHEMICAL_DUST = ITEMS.registerSimpleItem("primus_alchemical_dust");
    public static final DeferredItem<Item> SECUNDUS_ALCHEMICAL_DUST = ITEMS.registerSimpleItem("secundus_alchemical_dust");
    public static final DeferredItem<Item> TERTIUS_ALCHEMICAL_DUST = ITEMS.registerSimpleItem("tertius_alchemical_dust");
    public static final DeferredItem<Item> QUARTUS_ALCHEMICAL_DUST = ITEMS.registerSimpleItem("quartus_alchemical_dust");
    public static final DeferredItem<OreAlchemyDustItem> ORE_ALCHEMICAL_DUST =
            ITEMS.registerItem("ore_alchemical_dust", OreAlchemyDustItem::new);
    public static final DeferredItem<DirtyGemItem> DIRTY_GEM =
            ITEMS.registerItem("dirty_gem", DirtyGemItem::new);
    public static final DeferredItem<Item> ALCHEMICAL_COAL = ITEMS.registerSimpleItem("alchemical_coal");
    public static final DeferredItem<Item> WOODEN_HEAT_COMPONENT = ITEMS.registerSimpleItem("wooden_heat_component");
    public static final DeferredItem<Item> STONE_ALCHEMY_COMPONENT = ITEMS.registerSimpleItem("stone_alchemy_component");
    public static final DeferredItem<Item> ALCHEMICAL_GOLD_INGOT = ITEMS.registerSimpleItem("alchemical_gold_ingot");
    public static final DeferredItem<Item> ALCHEMICAL_IRON_INGOT = ITEMS.registerSimpleItem("alchemical_iron_ingot");
    public static final DeferredItem<Item> ALCHEMICAL_GOLD_NEEDLE = ITEMS.registerSimpleItem("alchemical_gold_needle");
    public static final DeferredItem<Item> ALCHEMICAL_DIAMOND = ITEMS.registerSimpleItem("alchemical_diamond");
    public static final DeferredItem<InstantBonemealItem> PLANT_MATTER = ITEMS.registerItem(
            "plant_matter",
            properties -> new InstantBonemealItem(properties, () -> Config.plantMatterBonemealCapability)
    );
    public static final DeferredItem<Item> ADVANCED_POWER_COMPONENT = ITEMS.registerSimpleItem("advanced_power_component");
    public static final DeferredItem<Item> FROZEN_IRON_COOLING_COMPONENT = ITEMS.registerSimpleItem("frozen_iron_cooling_component");
    public static final DeferredItem<Item> DARK_MATTER = ITEMS.registerSimpleItem("dark_matter");
    public static final DeferredItem<InstantBonemealItem> ENRICHED_BONEMEAL = ITEMS.registerItem(
            "enriched_bonemeal",
            properties -> new InstantBonemealItem(properties, () -> true)
    );
    public static final DeferredItem<Item> LIGHT_MATTER = ITEMS.registerSimpleItem("light_matter");
    public static final DeferredItem<Item> SAWDUST = ITEMS.registerSimpleItem("sawdust");
    public static final DeferredItem<Item> QUARTZ_AMPLIFICATION_COMPONENT =
            ITEMS.registerSimpleItem("quartz_amplification_component");
    public static final DeferredItem<Item> CRUSHED_STONE = ITEMS.registerSimpleItem("crushed_stone");
    public static final DeferredItem<Item> RADIOACTIVE_MIX = ITEMS.registerSimpleItem("radioactive_mix");
    public static final DeferredItem<Item> FROZEN_IRON_INGOT = ITEMS.registerSimpleItem("frozen_iron_ingot");
    public static final DeferredItem<Item> CRUSHED_NETHERRACK = ITEMS.registerSimpleItem("crushed_netherrack");
    public static final DeferredItem<WaterExtractorItem> WATER_EXTRACTOR =
            ITEMS.registerItem("water_extractor", WaterExtractorItem::new);
    public static final DeferredItem<SurvivalistFishingRodItem> SURVIVALIST_FISHING_ROD =
            ITEMS.registerItem("survivalist_fishing_rod", SurvivalistFishingRodItem::new);
    public static final DeferredItem<HeavySnowballItem> HEAVY_SNOWBALL = ITEMS.registerItem(
            "heavy_snowball",
            properties -> new HeavySnowballItem(properties.stacksTo(8), HeavySnowball::new, HeavySnowball::new)
    );
    public static final DeferredItem<HeavySnowballItem> HEAVY_EXPLOSIVE_SNOWBALL = ITEMS.registerItem(
            "heavy_explosive_snowball",
            properties -> new HeavySnowballItem(
                    properties.stacksTo(8),
                    HeavyExplosiveSnowball::new,
                    HeavyExplosiveSnowball::new
            )
    );
    public static final DeferredItem<CuttingKnifeItem> CACTUS_CUTTING_KNIFE =
            cuttingKnife("cactus_cutting_knife", 2, 5.0F, 2.5F, 5);
    public static final DeferredItem<CuttingKnifeItem> STONE_CUTTING_KNIFE =
            cuttingKnife("stone_cutting_knife", 91, 4.0F, 2.5F, 5);
    public static final DeferredItem<CuttingKnifeItem> IRON_CUTTING_KNIFE =
            cuttingKnife("iron_cutting_knife", 175, 6.0F, 3.5F, 14);
    public static final DeferredItem<CuttingKnifeItem> DIAMOND_CUTTING_KNIFE =
            cuttingKnife("diamond_cutting_knife", 1092, 8.0F, 4.5F, 10);
    public static final DeferredItem<RockGrinderItem> STONE_GRINDER =
            rockGrinder("stone_grinder", 104, 4.0F, 3.5F, 5);
    public static final DeferredItem<RockGrinderItem> IRON_GRINDER =
            rockGrinder("iron_grinder", 200, 6.0F, 4.5F, 14);
    public static final DeferredItem<RockGrinderItem> DIAMOND_GRINDER =
            rockGrinder("diamond_grinder", 1248, 8.0F, 5.5F, 10);
    public static final DeferredItem<InfusionStoneItem> SANDSTONE_INFUSION_STONE =
            infusionStone("sandstone_infusion_stone", 100);
    public static final DeferredItem<InfusionStoneItem> RED_SANDSTONE_INFUSION_STONE =
            infusionStone("red_sandstone_infusion_stone", 80);
    public static final DeferredItem<InfusionStoneItem> ALCHEMICAL_INFUSION_STONE =
            infusionStone("alchemical_infusion_stone", 1500);
    public static final DeferredItem<HealthGemItem> HEALTH_GEM = ITEMS.registerItem("health_gem", HealthGemItem::new);
    public static final DeferredItem<BucketItem> CRYSTAL_FLUID_BUCKET = ITEMS.registerItem(
            "crystal_fluid_bucket",
            properties -> new BucketItem(
                    ModFluids.CRYSTAL_FLUID.get(),
                    properties.craftRemainder(Items.BUCKET).stacksTo(1)
            )
    );
    public static void register(final IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    private static DeferredItem<BlockItem> blockItem(final String name, final DeferredBlock<? extends Block> block) {
        return ITEMS.registerSimpleBlockItem(name, block);
    }

    private static DeferredItem<CuttingKnifeItem> cuttingKnife(
            final String name,
            final int durability,
            final float miningSpeed,
            final float attackDamage,
            final int enchantmentValue
    ) {
        return ITEMS.registerItem(
                name,
                properties -> new CuttingKnifeItem(properties, durability, miningSpeed, attackDamage, enchantmentValue)
        );
    }

    private static DeferredItem<RockGrinderItem> rockGrinder(
            final String name,
            final int durability,
            final float miningSpeed,
            final float attackDamage,
            final int enchantmentValue
    ) {
        return ITEMS.registerItem(
                name,
                properties -> new RockGrinderItem(properties, durability, miningSpeed, attackDamage, enchantmentValue)
        );
    }

    private static DeferredItem<InfusionStoneItem> infusionStone(final String name, final int durability) {
        return ITEMS.registerItem(name, properties -> new InfusionStoneItem(properties, durability));
    }

    private static FoodProperties food(final int nutrition, final float saturationModifier) {
        return new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationModifier(saturationModifier)
                .build();
    }

    private ModItems() {
    }
}
