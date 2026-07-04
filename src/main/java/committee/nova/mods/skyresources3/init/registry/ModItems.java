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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
            ForgeRegistries.ITEMS,
            Skyresources3.MODID
    );

    public static final RegistryObject<BlockItem> COMPRESSED_COAL_BLOCK = blockItem("compressed_coal_block", ModBlocks.COMPRESSED_COAL_BLOCK);
    public static final RegistryObject<BlockItem> COAL_INFUSED_BLOCK = blockItem("coal_infused_block", ModBlocks.COAL_INFUSED_BLOCK);
    public static final RegistryObject<BlockItem> SANDY_NETHERRACK = blockItem("sandy_netherrack", ModBlocks.SANDY_NETHERRACK);
    public static final RegistryObject<BlockItem> PETRIFIED_WOOD = blockItem("petrified_wood", ModBlocks.PETRIFIED_WOOD);
    public static final RegistryObject<BlockItem> PETRIFIED_PLANKS = blockItem("petrified_planks", ModBlocks.PETRIFIED_PLANKS);
    public static final RegistryObject<BlockItem> MAGMAFIED_STONE = blockItem("magmafied_stone", ModBlocks.MAGMAFIED_STONE);
    public static final RegistryObject<BlockItem> HEAVY_SNOW = blockItem("heavy_snow", ModBlocks.HEAVY_SNOW);
    public static final RegistryObject<BlockItem> BLAZE_POWDER_BLOCK =
            blockItem("blaze_powder_block", ModBlocks.BLAZE_POWDER_BLOCK);
    public static final RegistryObject<BlockItem> DARK_MATTER_BLOCK = blockItem("dark_matter_block", ModBlocks.DARK_MATTER_BLOCK);
    public static final RegistryObject<BlockItem> LIGHT_MATTER_BLOCK = blockItem("light_matter_block", ModBlocks.LIGHT_MATTER_BLOCK);
    public static final RegistryObject<BlockItem> ALCHEMICAL_GLASS = blockItem("alchemical_glass", ModBlocks.ALCHEMICAL_GLASS);
    public static final RegistryObject<BlockItem> FUSION_TABLE = blockItem("fusion_table", ModBlocks.FUSION_TABLE);
    public static final RegistryObject<BlockItem> DIRT_FURNACE = blockItem("dirt_furnace", ModBlocks.DIRT_FURNACE);
    public static final RegistryObject<BlockItem> QUICK_DROPPER = blockItem("quick_dropper", ModBlocks.QUICK_DROPPER);
    public static final RegistryObject<BlockItem> DARK_MATTER_WARPER =
            blockItem("dark_matter_warper", ModBlocks.DARK_MATTER_WARPER);
    public static final RegistryObject<BlockItem> END_PORTAL_CORE = blockItem("end_portal_core", ModBlocks.END_PORTAL_CORE);
    public static final RegistryObject<BlockItem> SILVERFISH_DISRUPTOR =
            blockItem("silverfish_disruptor", ModBlocks.SILVERFISH_DISRUPTOR);
    public static final RegistryObject<BlockItem> FLUID_DROPPER = blockItem("fluid_dropper", ModBlocks.FLUID_DROPPER);
    public static final RegistryObject<BlockItem> CRUCIBLE = blockItem("crucible", ModBlocks.CRUCIBLE);
    public static final RegistryObject<BlockItem> CRUCIBLE_INSERTER =
            blockItem("crucible_inserter", ModBlocks.CRUCIBLE_INSERTER);
    public static final RegistryObject<BlockItem> ROCK_CRUSHER = blockItem("rock_crusher", ModBlocks.ROCK_CRUSHER);
    public static final RegistryObject<BlockItem> ROCK_CLEANER = blockItem("rock_cleaner", ModBlocks.ROCK_CLEANER);
    public static final RegistryObject<BlockItem> AQUEOUS_CONCENTRATOR =
            blockItem("aqueous_concentrator", ModBlocks.AQUEOUS_CONCENTRATOR);
    public static final RegistryObject<BlockItem> AQUEOUS_DECONCENTRATOR =
            blockItem("aqueous_deconcentrator", ModBlocks.AQUEOUS_DECONCENTRATOR);
    public static final RegistryObject<BlockItem> WILDLIFE_ATTRACTOR =
            blockItem("wildlife_attractor", ModBlocks.WILDLIFE_ATTRACTOR);
    public static final RegistryObject<MachineCasingItem> MACHINE_CASING = registerItem(
            "machine_casing",
            properties -> new MachineCasingItem(ModBlocks.MACHINE_CASING.get(), properties)
    );
    public static final RegistryObject<CombustionHeaterItem> COMBUSTION_HEATER = registerItem(
            "combustion_heater",
            properties -> new CombustionHeaterItem(ModBlocks.COMBUSTION_HEATER.get(), properties)
    );
    public static final RegistryObject<HeatProviderItem> HEAT_PROVIDER = registerItem(
            "heat_provider",
            properties -> new HeatProviderItem(ModBlocks.HEAT_PROVIDER.get(), properties)
    );
    public static final RegistryObject<CondenserItem> CONDENSER = registerItem(
            "condenser",
            properties -> new CondenserItem(ModBlocks.CONDENSER.get(), properties)
    );
    public static final RegistryObject<BlockItem> COMBUSTION_COLLECTOR =
            blockItem("combustion_collector", ModBlocks.COMBUSTION_COLLECTOR);
    public static final RegistryObject<BlockItem> COMBUSTION_CONTROLLER =
            blockItem("combustion_controller", ModBlocks.COMBUSTION_CONTROLLER);
    public static final RegistryObject<BlockItem> MINI_FREEZER = blockItem("mini_freezer", ModBlocks.MINI_FREEZER);
    public static final RegistryObject<BlockItem> IRON_FREEZER = blockItem("iron_freezer", ModBlocks.IRON_FREEZER);
    public static final RegistryObject<BlockItem> LIGHT_FREEZER = blockItem("light_freezer", ModBlocks.LIGHT_FREEZER);
    public static final RegistryObject<BlockItem> LIFE_INFUSER = blockItem("life_infuser", ModBlocks.LIFE_INFUSER);
    public static final RegistryObject<BlockItem> LIFE_INJECTOR = blockItem("life_injector", ModBlocks.LIFE_INJECTOR);
    public static final RegistryObject<BlockItem> CACTUS_FRUIT_NEEDLE =
            blockItem("cactus_fruit_needle", ModBlocks.CACTUS_FRUIT_NEEDLE);
    public static final RegistryObject<BlockItem> DRY_CACTUS = blockItem("dry_cactus", ModBlocks.DRY_CACTUS);

    public static final RegistryObject<Item> CACTUS_FRUIT = registerSimpleItem(
            "cactus_fruit",
            new Item.Properties().food(food(3, 0.4F))
    );
    public static final RegistryObject<Item> FLESHY_SNOW_NUGGET = registerSimpleItem(
            "fleshy_snow_nugget",
            new Item.Properties().food(food(4, 0.3F))
    );
    public static final RegistryObject<Item> CACTUS_NEEDLE = registerSimpleItem("cactus_needle");
    public static final RegistryObject<Item> CRYSTAL_SHARD = registerSimpleItem("crystal_shard");
    public static final RegistryObject<Item> PRIMUS_ALCHEMICAL_DUST = registerSimpleItem("primus_alchemical_dust");
    public static final RegistryObject<Item> SECUNDUS_ALCHEMICAL_DUST = registerSimpleItem("secundus_alchemical_dust");
    public static final RegistryObject<Item> TERTIUS_ALCHEMICAL_DUST = registerSimpleItem("tertius_alchemical_dust");
    public static final RegistryObject<Item> QUARTUS_ALCHEMICAL_DUST = registerSimpleItem("quartus_alchemical_dust");
    public static final RegistryObject<OreAlchemyDustItem> ORE_ALCHEMICAL_DUST =
            registerItem("ore_alchemical_dust", OreAlchemyDustItem::new);
    public static final RegistryObject<DirtyGemItem> DIRTY_GEM =
            registerItem("dirty_gem", DirtyGemItem::new);
    public static final RegistryObject<Item> ALCHEMICAL_COAL = registerSimpleItem("alchemical_coal");
    public static final RegistryObject<Item> WOODEN_HEAT_COMPONENT = registerSimpleItem("wooden_heat_component");
    public static final RegistryObject<Item> STONE_ALCHEMY_COMPONENT = registerSimpleItem("stone_alchemy_component");
    public static final RegistryObject<Item> ALCHEMICAL_GOLD_INGOT = registerSimpleItem("alchemical_gold_ingot");
    public static final RegistryObject<Item> ALCHEMICAL_IRON_INGOT = registerSimpleItem("alchemical_iron_ingot");
    public static final RegistryObject<Item> ALCHEMICAL_GOLD_NEEDLE = registerSimpleItem("alchemical_gold_needle");
    public static final RegistryObject<Item> ALCHEMICAL_DIAMOND = registerSimpleItem("alchemical_diamond");
    public static final RegistryObject<InstantBonemealItem> PLANT_MATTER = registerItem(
            "plant_matter",
            properties -> new InstantBonemealItem(properties, () -> Config.plantMatterBonemealCapability)
    );
    public static final RegistryObject<Item> ADVANCED_POWER_COMPONENT = registerSimpleItem("advanced_power_component");
    public static final RegistryObject<Item> FROZEN_IRON_COOLING_COMPONENT = registerSimpleItem("frozen_iron_cooling_component");
    public static final RegistryObject<Item> DARK_MATTER = registerSimpleItem("dark_matter");
    public static final RegistryObject<InstantBonemealItem> ENRICHED_BONEMEAL = registerItem(
            "enriched_bonemeal",
            properties -> new InstantBonemealItem(properties, () -> true)
    );
    public static final RegistryObject<Item> LIGHT_MATTER = registerSimpleItem("light_matter");
    public static final RegistryObject<Item> SAWDUST = registerSimpleItem("sawdust");
    public static final RegistryObject<Item> QUARTZ_AMPLIFICATION_COMPONENT =
            registerSimpleItem("quartz_amplification_component");
    public static final RegistryObject<Item> CRUSHED_STONE = registerSimpleItem("crushed_stone");
    public static final RegistryObject<Item> RADIOACTIVE_MIX = registerSimpleItem("radioactive_mix");
    public static final RegistryObject<Item> FROZEN_IRON_INGOT = registerSimpleItem("frozen_iron_ingot");
    public static final RegistryObject<Item> CRUSHED_NETHERRACK = registerSimpleItem("crushed_netherrack");
    public static final RegistryObject<WaterExtractorItem> WATER_EXTRACTOR =
            registerItem("water_extractor", WaterExtractorItem::new);
    public static final RegistryObject<SurvivalistFishingRodItem> SURVIVALIST_FISHING_ROD =
            registerItem("survivalist_fishing_rod", SurvivalistFishingRodItem::new);
    public static final RegistryObject<HeavySnowballItem> HEAVY_SNOWBALL = registerItem(
            "heavy_snowball",
            properties -> new HeavySnowballItem(properties.stacksTo(8), HeavySnowball::new, HeavySnowball::new)
    );
    public static final RegistryObject<HeavySnowballItem> HEAVY_EXPLOSIVE_SNOWBALL = registerItem(
            "heavy_explosive_snowball",
            properties -> new HeavySnowballItem(
                    properties.stacksTo(8),
                    HeavyExplosiveSnowball::new,
                    HeavyExplosiveSnowball::new
            )
    );
    public static final RegistryObject<CuttingKnifeItem> CACTUS_CUTTING_KNIFE =
            cuttingKnife("cactus_cutting_knife", 2, 5.0F, 2.5F, 5);
    public static final RegistryObject<CuttingKnifeItem> STONE_CUTTING_KNIFE =
            cuttingKnife("stone_cutting_knife", 91, 4.0F, 2.5F, 5);
    public static final RegistryObject<CuttingKnifeItem> IRON_CUTTING_KNIFE =
            cuttingKnife("iron_cutting_knife", 175, 6.0F, 3.5F, 14);
    public static final RegistryObject<CuttingKnifeItem> DIAMOND_CUTTING_KNIFE =
            cuttingKnife("diamond_cutting_knife", 1092, 8.0F, 4.5F, 10);
    public static final RegistryObject<RockGrinderItem> STONE_GRINDER =
            rockGrinder("stone_grinder", 104, 4.0F, 3.5F, 5);
    public static final RegistryObject<RockGrinderItem> IRON_GRINDER =
            rockGrinder("iron_grinder", 200, 6.0F, 4.5F, 14);
    public static final RegistryObject<RockGrinderItem> DIAMOND_GRINDER =
            rockGrinder("diamond_grinder", 1248, 8.0F, 5.5F, 10);
    public static final RegistryObject<InfusionStoneItem> SANDSTONE_INFUSION_STONE =
            infusionStone("sandstone_infusion_stone", 100);
    public static final RegistryObject<InfusionStoneItem> RED_SANDSTONE_INFUSION_STONE =
            infusionStone("red_sandstone_infusion_stone", 80);
    public static final RegistryObject<InfusionStoneItem> ALCHEMICAL_INFUSION_STONE =
            infusionStone("alchemical_infusion_stone", 1500);
    public static final RegistryObject<HealthGemItem> HEALTH_GEM = registerItem("health_gem", HealthGemItem::new);
    public static final RegistryObject<BucketItem> CRYSTAL_FLUID_BUCKET = registerItem(
            "crystal_fluid_bucket",
            properties -> new BucketItem(
                    ModFluids.CRYSTAL_FLUID.get(),
                    properties.craftRemainder(Items.BUCKET).stacksTo(1)
            )
    );
    public static void register(final IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    private static RegistryObject<BlockItem> blockItem(final String name, final RegistryObject<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Item> RegistryObject<T> registerItem(
            final String name,
            final Function<Item.Properties, T> factory
    ) {
        return ITEMS.register(name, () -> factory.apply(new Item.Properties()));
    }

    private static RegistryObject<Item> registerSimpleItem(final String name) {
        return registerSimpleItem(name, new Item.Properties());
    }

    private static RegistryObject<Item> registerSimpleItem(final String name, final Item.Properties properties) {
        return ITEMS.register(name, () -> new Item(properties));
    }

    private static RegistryObject<CuttingKnifeItem> cuttingKnife(
            final String name,
            final int durability,
            final float miningSpeed,
            final float attackDamage,
            final int enchantmentValue
    ) {
        return registerItem(
                name,
                properties -> new CuttingKnifeItem(properties, durability, miningSpeed, attackDamage, enchantmentValue)
        );
    }

    private static RegistryObject<RockGrinderItem> rockGrinder(
            final String name,
            final int durability,
            final float miningSpeed,
            final float attackDamage,
            final int enchantmentValue
    ) {
        return registerItem(
                name,
                properties -> new RockGrinderItem(properties, durability, miningSpeed, attackDamage, enchantmentValue)
        );
    }

    private static RegistryObject<InfusionStoneItem> infusionStone(final String name, final int durability) {
        return registerItem(name, properties -> new InfusionStoneItem(properties, durability));
    }

    private static FoodProperties food(final int nutrition, final float saturationModifier) {
        return new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationMod(saturationModifier)
                .build();
    }

    private ModItems() {
    }
}
