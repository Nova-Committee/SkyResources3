package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Skyresources3;
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
    public static final DeferredItem<Item> PLANT_MATTER = ITEMS.registerSimpleItem("plant_matter");
    public static final DeferredItem<Item> DARK_MATTER = ITEMS.registerSimpleItem("dark_matter");
    public static final DeferredItem<Item> LIGHT_MATTER = ITEMS.registerSimpleItem("light_matter");
    public static final DeferredItem<Item> SAWDUST = ITEMS.registerSimpleItem("sawdust");
    public static final DeferredItem<Item> CRUSHED_STONE = ITEMS.registerSimpleItem("crushed_stone");
    public static final DeferredItem<Item> CRUSHED_NETHERRACK = ITEMS.registerSimpleItem("crushed_netherrack");
    public static final DeferredItem<Item> HEAVY_SNOWBALL = ITEMS.registerSimpleItem(
            "heavy_snowball",
            () -> new Item.Properties().stacksTo(16)
    );
    public static final DeferredItem<Item> HEAVY_EXPLOSIVE_SNOWBALL = ITEMS.registerSimpleItem(
            "heavy_explosive_snowball",
            () -> new Item.Properties().stacksTo(16)
    );
    public static final DeferredItem<Item> CACTUS_CUTTING_KNIFE = singleStackItem("cactus_cutting_knife");
    public static final DeferredItem<Item> STONE_CUTTING_KNIFE = singleStackItem("stone_cutting_knife");
    public static final DeferredItem<Item> IRON_CUTTING_KNIFE = singleStackItem("iron_cutting_knife");
    public static final DeferredItem<Item> DIAMOND_CUTTING_KNIFE = singleStackItem("diamond_cutting_knife");
    public static final DeferredItem<Item> STONE_GRINDER = singleStackItem("stone_grinder");
    public static final DeferredItem<Item> IRON_GRINDER = singleStackItem("iron_grinder");
    public static final DeferredItem<Item> DIAMOND_GRINDER = singleStackItem("diamond_grinder");
    public static final DeferredItem<Item> SANDSTONE_INFUSION_STONE = singleStackItem("sandstone_infusion_stone");
    public static final DeferredItem<Item> RED_SANDSTONE_INFUSION_STONE = singleStackItem("red_sandstone_infusion_stone");
    public static final DeferredItem<Item> ALCHEMICAL_INFUSION_STONE = singleStackItem("alchemical_infusion_stone");

    public static void register(final IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    private static DeferredItem<BlockItem> blockItem(final String name, final DeferredBlock<? extends Block> block) {
        return ITEMS.registerSimpleBlockItem(name, block);
    }

    private static DeferredItem<Item> singleStackItem(final String name) {
        return ITEMS.registerSimpleItem(name, () -> new Item.Properties().stacksTo(1));
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
