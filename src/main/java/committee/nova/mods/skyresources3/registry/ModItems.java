package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.entity.HeavyExplosiveSnowball;
import committee.nova.mods.skyresources3.entity.HeavySnowball;
import committee.nova.mods.skyresources3.item.HealthGemItem;
import committee.nova.mods.skyresources3.item.HeavySnowballItem;
import committee.nova.mods.skyresources3.item.InstantBonemealItem;
import committee.nova.mods.skyresources3.item.SurvivalistFishingRodItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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
    public static final DeferredItem<BlockItem> DARK_MATTER_BLOCK = blockItem("dark_matter_block", ModBlocks.DARK_MATTER_BLOCK);
    public static final DeferredItem<BlockItem> LIGHT_MATTER_BLOCK = blockItem("light_matter_block", ModBlocks.LIGHT_MATTER_BLOCK);
    public static final DeferredItem<BlockItem> ALCHEMICAL_GLASS = blockItem("alchemical_glass", ModBlocks.ALCHEMICAL_GLASS);

    public static final DeferredItem<Item> CACTUS_FRUIT = ITEMS.registerSimpleItem(
            "cactus_fruit",
            () -> new Item.Properties().food(food(3, 0.4F))
    );
    public static final DeferredItem<Item> FLESHY_SNOW_NUGGET = ITEMS.registerSimpleItem(
            "fleshy_snow_nugget",
            () -> new Item.Properties().food(food(4, 0.3F))
    );
    public static final DeferredItem<Item> CACTUS_NEEDLE = ITEMS.registerSimpleItem("cactus_needle");
    public static final DeferredItem<Item> CRYSTAL_SHARD = ITEMS.registerSimpleItem("crystal_shard");
    public static final DeferredItem<Item> PRIMUS_ALCHEMICAL_DUST = ITEMS.registerSimpleItem("primus_alchemical_dust");
    public static final DeferredItem<Item> SECUNDUS_ALCHEMICAL_DUST = ITEMS.registerSimpleItem("secundus_alchemical_dust");
    public static final DeferredItem<Item> TERTIUS_ALCHEMICAL_DUST = ITEMS.registerSimpleItem("tertius_alchemical_dust");
    public static final DeferredItem<Item> QUARTUS_ALCHEMICAL_DUST = ITEMS.registerSimpleItem("quartus_alchemical_dust");
    public static final DeferredItem<Item> ALCHEMICAL_COAL = ITEMS.registerSimpleItem("alchemical_coal");
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
    public static final DeferredItem<Item> CACTUS_CUTTING_KNIFE = durableItem("cactus_cutting_knife", 2);
    public static final DeferredItem<Item> STONE_CUTTING_KNIFE = durableItem("stone_cutting_knife", 91);
    public static final DeferredItem<Item> IRON_CUTTING_KNIFE = durableItem("iron_cutting_knife", 175);
    public static final DeferredItem<Item> DIAMOND_CUTTING_KNIFE = durableItem("diamond_cutting_knife", 1092);
    public static final DeferredItem<Item> STONE_GRINDER = durableItem("stone_grinder", 104);
    public static final DeferredItem<Item> IRON_GRINDER = durableItem("iron_grinder", 200);
    public static final DeferredItem<Item> DIAMOND_GRINDER = durableItem("diamond_grinder", 1248);
    public static final DeferredItem<Item> SANDSTONE_INFUSION_STONE = nonRepairableDurableItem("sandstone_infusion_stone", 100);
    public static final DeferredItem<Item> RED_SANDSTONE_INFUSION_STONE = nonRepairableDurableItem("red_sandstone_infusion_stone", 80);
    public static final DeferredItem<Item> ALCHEMICAL_INFUSION_STONE = nonRepairableDurableItem("alchemical_infusion_stone", 1500);
    public static final DeferredItem<HealthGemItem> HEALTH_GEM = ITEMS.registerItem("health_gem", HealthGemItem::new);

    public static void register(final IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    private static DeferredItem<BlockItem> blockItem(final String name, final DeferredBlock<? extends Block> block) {
        return ITEMS.registerSimpleBlockItem(name, block);
    }

    private static DeferredItem<Item> durableItem(final String name, final int durability) {
        return ITEMS.registerItem(name, Item::new, properties -> properties.durability(durability));
    }

    private static DeferredItem<Item> nonRepairableDurableItem(final String name, final int durability) {
        return ITEMS.registerItem(name, Item::new, properties -> properties.durability(durability).setNoCombineRepair());
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
