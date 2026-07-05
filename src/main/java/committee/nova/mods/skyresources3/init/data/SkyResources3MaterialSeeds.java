package committee.nova.mods.skyresources3.init.data;

import committee.nova.mods.skyresources3.common.item.DirtyGemType;
import committee.nova.mods.skyresources3.common.item.OreAlchemyDustType;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class SkyResources3MaterialSeeds {
    static final String PRIMARY_COMMON_NAMESPACE = "forge";
    static final String ORE_TAG_PREFIX = "ores/";
    static final String GEM_TAG_PREFIX = "gems/";

    private static final List<DustSeed> DUSTS = List.of(
            new DustSeed("iron", 3, 0xD8AF93, Blocks.STONE, true, item(Items.ROTTEN_FLESH)),
            new DustSeed("gold", 5, 0xF5D566, Blocks.STONE, true, item(Items.WHEAT)),
            new DustSeed("copper", 1, 0xC87541, Blocks.STONE, true, item(Items.PUMPKIN_SEEDS)),
            new DustSeed("tin", 3, 0xD6DCE6, Blocks.STONE, true, item(Items.BONE)),
            new DustSeed("silver", 4, 0xC7D1DD, Blocks.STONE, true, item(Items.SUGAR)),
            new DustSeed("zinc", 2, 0xB5C0C6, Blocks.STONE, true, item(Items.WHEAT)),
            new DustSeed("nickel", 5, 0xB9A96F, Blocks.STONE, true, item(Items.IRON_INGOT)),
            new DustSeed("platinum", 7, 0xB5DDE5, Blocks.STONE, true, item(Items.GOLD_INGOT)),
            new DustSeed("aluminum", 4, 0xD5D8DB, Blocks.STONE, true, item(ModItems.ENRICHED_BONEMEAL::get)),
            new DustSeed("lead", 4, 0x5D6470, Blocks.STONE, true, item(Blocks.CLAY)),
            new DustSeed("cobalt", 6, 0x3865D8, Blocks.NETHERRACK, true, item(Items.BROWN_DYE)),
            new DustSeed("ardite", 6, 0xD06A28, Blocks.NETHERRACK, true, item(Items.MAGMA_CREAM)),
            new DustSeed("osmium", 6, 0x7FAFC6, Blocks.STONE, true, item(Items.CLAY_BALL)),
            new DustSeed("draconium", 9, 0xAE65FF, Blocks.END_STONE, false, null),
            new DustSeed("titanium", 6, 0xB9C3CC, Blocks.STONE, true, item(Items.CHARCOAL)),
            new DustSeed("tungsten", 6, 0x5B6470, Blocks.END_STONE, true, item(Blocks.OBSIDIAN)),
            new DustSeed("chrome", 8, 0xC9D2D7, Blocks.STONE, true, item(Items.SUGAR)),
            new DustSeed("iridium", 11, 0xE5E8FF, Blocks.STONE, true, item(ModItems.FROZEN_IRON_INGOT::get)),
            new DustSeed("boron", 5, 0x3F3648, Blocks.STONE, true, item(Blocks.SOUL_SAND)),
            new DustSeed("lithium", 7, 0xEEE2DC, Blocks.STONE, true, item(Items.PRISMARINE_SHARD)),
            new DustSeed("magnesium", 5, 0xF0F0DC, Blocks.STONE, true, item(ModItems.ENRICHED_BONEMEAL::get)),
            new DustSeed("mithril", 9, 0x5ED7E8, Blocks.STONE, true, item(Items.DIAMOND)),
            new DustSeed("yellorium", 6, 0xD9E84F, Blocks.STONE, false, null),
            new DustSeed("uranium", 6, 0x74B84A, Blocks.STONE, false, null),
            new DustSeed("thorium", 7, 0x6E9566, Blocks.STONE, false, null)
    );

    private static final List<GemSeed> GEMS = List.of(
            new GemSeed("emerald", 0.015F, 0x12DB3A, Blocks.STONE, item(Items.EMERALD)),
            new GemSeed("diamond", 0.033F, 0x6BFFFD, Blocks.STONE, item(Items.DIAMOND)),
            new GemSeed("ruby", 0.015F, 0xFA1E1E, Blocks.STONE, null),
            new GemSeed("sapphire", 0.015F, 0x1E46FA, Blocks.STONE, null),
            new GemSeed("peridot", 0.015F, 0x1CB800, Blocks.STONE, null),
            new GemSeed("red_garnet", 0.015F, 0xC90014, Blocks.STONE, null),
            new GemSeed("yellow_garnet", 0.015F, 0xF7FF0F, Blocks.STONE, null),
            new GemSeed("apatite", 0.600F, 0x2B95FF, Blocks.STONE, null),
            new GemSeed("amber", 0.021F, 0xF5CC53, Blocks.STONE, null),
            new GemSeed("lepidolite", 0.021F, 0x57008A, Blocks.NETHERRACK, null),
            new GemSeed("malachite", 0.021F, 0x23AD00, Blocks.NETHERRACK, null),
            new GemSeed("onyx", 0.021F, 0x3D3D3D, Blocks.STONE, null),
            new GemSeed("moldavite", 0.021F, 0xADFF99, Blocks.NETHERRACK, null),
            new GemSeed("agate", 0.021F, 0xFF63FF, Blocks.STONE, null),
            new GemSeed("opal", 0.021F, 0xDEDEDE, Blocks.STONE, null),
            new GemSeed("amethyst", 0.018F, 0x780078, Blocks.STONE, null),
            new GemSeed("jasper", 0.018F, 0x874800, Blocks.NETHERRACK, null),
            new GemSeed("aquamarine", 0.018F, 0x36E7FF, Blocks.STONE, null),
            new GemSeed("heliodor", 0.018F, 0xFFFF7D, Blocks.STONE, null),
            new GemSeed("turquoise", 0.018F, 0x2EF2C8, Blocks.NETHERRACK, null),
            new GemSeed("moonstone", 0.018F, 0x016A8A, Blocks.NETHERRACK, null),
            new GemSeed("morganite", 0.018F, 0xFA61FF, Blocks.STONE, null),
            new GemSeed("carnelian", 0.018F, 0x630606, Blocks.NETHERRACK, null),
            new GemSeed("beryl", 0.015F, 0x46E334, Blocks.STONE, null),
            new GemSeed("golden_beryl", 0.015F, 0xD6AE2B, Blocks.NETHERRACK, null),
            new GemSeed("citrine", 0.015F, 0x871616, Blocks.NETHERRACK, null),
            new GemSeed("indicolite", 0.015F, 0x39E6BD, Blocks.STONE, null),
            new GemSeed("garnet", 0.015F, 0xFF9999, Blocks.STONE, null),
            new GemSeed("topaz", 0.015F, 0xFFD399, Blocks.STONE, null),
            new GemSeed("ametrine", 0.015F, 0xA300BF, Blocks.NETHERRACK, null),
            new GemSeed("tanzanite", 0.015F, 0x00076E, Blocks.NETHERRACK, null),
            new GemSeed("violet_sapphire", 0.012F, 0x451287, Blocks.NETHERRACK, null),
            new GemSeed("alexandrite", 0.012F, 0xE3E3E3, Blocks.NETHERRACK, null),
            new GemSeed("blue_topaz", 0.012F, 0x1000C4, Blocks.NETHERRACK, null),
            new GemSeed("spinel", 0.012F, 0x750000, Blocks.NETHERRACK, null),
            new GemSeed("iolite", 0.012F, 0x9502CF, Blocks.STONE, null),
            new GemSeed("black_diamond", 0.009F, 0x262626, Blocks.NETHERRACK, null),
            new GemSeed("chaos", 0.009F, 0xFFE6FB, Blocks.STONE, null),
            new GemSeed("ender_essence", 0.009F, 0x356E19, Blocks.END_STONE, null),
            new GemSeed("dark", 0.27F, 0x242424, Blocks.STONE, null),
            new GemSeed("quartz", 0.42F, 0xFFFFFF, Blocks.NETHERRACK, item(Items.QUARTZ)),
            new GemSeed("lapis", 0.54F, 0x075BBA, Blocks.STONE, item(Items.LAPIS_LAZULI)),
            new GemSeed("quartz_black", 0.36F, 0x171717, Blocks.STONE, null),
            new GemSeed("certus", 0.48F, 0xB0F4F7, Blocks.STONE, null)
    );
    private static final Map<ResourceLocation, Integer> ORE_ALCHEMY_DUST_COLORS = DUSTS.stream()
            .collect(Collectors.toUnmodifiableMap(
                    dust -> ModDataPackRegistries.oreAlchemyDustTypeId(dust.key()),
                    DustSeed::color
            ));
    private static final Map<ResourceLocation, Integer> DIRTY_GEM_COLORS = GEMS.stream()
            .collect(Collectors.toUnmodifiableMap(
                    gem -> ModDataPackRegistries.dirtyGemTypeId(gem.key()),
                    GemSeed::color
            ));

    static List<DustSeed> dusts() {
        return DUSTS;
    }

    static List<GemSeed> gems() {
        return GEMS;
    }

    public static List<ResourceKey<OreAlchemyDustType>> oreAlchemyDustKeys() {
        return DUSTS.stream()
                .map(DustSeed::key)
                .collect(Collectors.toUnmodifiableList());
    }

    public static List<ResourceKey<DirtyGemType>> dirtyGemKeys() {
        return GEMS.stream()
                .map(GemSeed::key)
                .collect(Collectors.toUnmodifiableList());
    }

    public static int oreAlchemyDustColor(final ResourceLocation typeId) {
        return ORE_ALCHEMY_DUST_COLORS.getOrDefault(typeId, OreAlchemyDustType.DEFAULT_COLOR);
    }

    public static int dirtyGemColor(final ResourceLocation typeId) {
        return DIRTY_GEM_COLORS.getOrDefault(typeId, DirtyGemType.DEFAULT_COLOR);
    }

    static String commonItemTag(final String path) {
        return PRIMARY_COMMON_NAMESPACE + ":" + path;
    }

    private static Supplier<ItemLike> item(final ItemLike item) {
        return () -> item;
    }

    private static Supplier<ItemLike> item(final Supplier<? extends ItemLike> item) {
        return item::get;
    }

    record DustSeed(
            String id,
            int rarity,
            int color,
            Block sourceBlock,
            boolean automatic,
            Supplier<ItemLike> fusionComponent
    ) {
        ResourceKey<OreAlchemyDustType> key() {
            return ModDataPackRegistries.oreAlchemyDustTypeKey(this.id);
        }

        String sourceTagPath() {
            return ORE_TAG_PREFIX + this.id;
        }

        boolean hasFusionRecipe() {
            return this.automatic && this.fusionComponent != null;
        }
    }

    record GemSeed(
            String id,
            float rarity,
            int color,
            Block sourceBlock,
            Supplier<ItemLike> cleanOutput
    ) {
        ResourceKey<DirtyGemType> key() {
            return ModDataPackRegistries.dirtyGemTypeKey(this.id);
        }

        String sourceTagPath() {
            return GEM_TAG_PREFIX + this.id;
        }

        boolean hasCleanOutput() {
            return this.cleanOutput != null;
        }
    }

    private SkyResources3MaterialSeeds() {
    }
}
