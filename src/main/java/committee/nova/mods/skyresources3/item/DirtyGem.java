package committee.nova.mods.skyresources3.item;

public enum DirtyGem {
    EMERALD("emerald", "emerald", "Emerald", 0xFF12DB3A, 0.015F, "minecraft:stone", ""),
    DIAMOND("diamond", "diamond", "Diamond", 0xFF6BFFFD, 0.033F, "minecraft:stone", ""),
    RUBY("ruby", "ruby", "Ruby", 0xFFFA1E1E, 0.015F, "minecraft:stone", ""),
    SAPPHIRE("sapphire", "sapphire", "Sapphire", 0xFF1E46FA, 0.015F, "minecraft:stone", ""),
    PERIDOT("peridot", "peridot", "Peridot", 0xFF1CB800, 0.015F, "minecraft:stone", ""),
    RED_GARNET("red_garnet", "redGarnet", "Red Garnet", 0xFFC90014, 0.015F, "minecraft:stone", ""),
    YELLOW_GARNET("yellow_garnet", "yellowGarnet", "Yellow Garnet", 0xFFF7FF0F, 0.015F, "minecraft:stone", ""),
    APATITE("apatite", "apatite", "Apatite", 0xFF2B95FF, 0.600F, "minecraft:stone", ""),
    AMBER("amber", "amber", "Amber", 0xFFF5CC53, 0.021F, "minecraft:stone", ""),
    LEPIDOLITE("lepidolite", "lepidolite", "Lepidolite", 0xFF57008A, 0.021F, "minecraft:netherrack", ""),
    MALACHITE("malachite", "malachite", "Malachite", 0xFF23AD00, 0.021F, "minecraft:netherrack", ""),
    ONYX("onyx", "onyx", "Onyx", 0xFF3D3D3D, 0.021F, "minecraft:stone", ""),
    MOLDAVITE("moldavite", "moldavite", "Moldavite", 0xFFADFF99, 0.021F, "minecraft:netherrack", ""),
    AGATE("agate", "agate", "Agate", 0xFFFF63FF, 0.021F, "minecraft:stone", ""),
    OPAL("opal", "opal", "Opal", 0xFFDEDEDE, 0.021F, "minecraft:stone", ""),
    AMETHYST("amethyst", "amethyst", "Amethyst", 0xFF780078, 0.018F, "minecraft:stone", ""),
    JASPER("jasper", "jasper", "Jasper", 0xFF874800, 0.018F, "minecraft:netherrack", ""),
    AQUAMARINE("aquamarine", "aquamarine", "Aquamarine", 0xFF36E7FF, 0.018F, "minecraft:stone", ""),
    HELIODOR("heliodor", "heliodor", "Heliodor", 0xFFFFFF7D, 0.018F, "minecraft:stone", ""),
    TURQUOISE("turquoise", "turquoise", "Turquoise", 0xFF2EF2C8, 0.018F, "minecraft:netherrack", ""),
    MOONSTONE("moonstone", "moonstone", "Moonstone", 0xFF016A8A, 0.018F, "minecraft:netherrack", ""),
    MORGANITE("morganite", "morganite", "Morganite", 0xFFFA61FF, 0.018F, "minecraft:stone", ""),
    CARNELIAN("carnelian", "carnelian", "Carnelian", 0xFF630606, 0.018F, "minecraft:netherrack", ""),
    BERYL("beryl", "beryl", "Beryl", 0xFF46E334, 0.015F, "minecraft:stone", ""),
    GOLDEN_BERYL("golden_beryl", "goldenBeryl", "Golden Beryl", 0xFFD6AE2B, 0.015F, "minecraft:netherrack", ""),
    CITRINE("citrine", "citrine", "Citrine", 0xFF871616, 0.015F, "minecraft:netherrack", ""),
    INDICOLITE("indicolite", "indicolite", "Indicolite", 0xFF39E6BD, 0.015F, "minecraft:stone", ""),
    GARNET("garnet", "garnet", "Garnet", 0xFFFF9999, 0.015F, "minecraft:stone", ""),
    TOPAZ("topaz", "topaz", "Topaz", 0xFFFFD399, 0.015F, "minecraft:stone", ""),
    AMETRINE("ametrine", "ametrine", "Ametrine", 0xFFA300BF, 0.015F, "minecraft:netherrack", ""),
    TANZANITE("tanzanite", "tanzanite", "Tanzanite", 0xFF00076E, 0.015F, "minecraft:netherrack", ""),
    VIOLET_SAPPHIRE("violet_sapphire", "violetSapphire", "Violet Sapphire", 0xFF451287, 0.012F, "minecraft:netherrack", ""),
    ALEXANDRITE("alexandrite", "alexandrite", "Alexandrite", 0xFFE3E3E3, 0.012F, "minecraft:netherrack", ""),
    BLUE_TOPAZ("blue_topaz", "blueTopaz", "Blue Topaz", 0xFF1000C4, 0.012F, "minecraft:netherrack", ""),
    SPINEL("spinel", "spinel", "Spinel", 0xFF750000, 0.012F, "minecraft:netherrack", ""),
    IOLITE("iolite", "iolite", "Iolite", 0xFF9502CF, 0.012F, "minecraft:stone", ""),
    BLACK_DIAMOND("black_diamond", "blackDiamond", "Black Diamond", 0xFF262626, 0.009F, "minecraft:netherrack", ""),
    CHAOS("chaos", "chaos", "Chaos", 0xFFFFE6FB, 0.009F, "minecraft:stone", ""),
    ENDER_ESSENCE("ender_essence", "enderEssence", "Ender Essence", 0xFF356E19, 0.009F, "minecraft:end_stone", ""),
    DARK("dark", "dark", "Dark Gem", 0xFF242424, 0.27F, "minecraft:stone", ""),
    QUARTZ("quartz", "quartz", "Quartz", 0xFFFFFFFF, 0.42F, "minecraft:netherrack", ""),
    LAPIS("lapis", "lapis", "Lapis Lazuli", 0xFF075BBA, 0.54F, "minecraft:stone", ""),
    QUARTZ_BLACK("quartz_black", "quartzBlack", "Black Quartz", 0xFF171717, 0.36F, "minecraft:stone", ""),
    CERTUS("certus", "certus", "Certus Quartz", 0xFFB0F4F7, 0.48F, "minecraft:stone", "crystalCertusQuartz");

    private final String id;
    private final String legacyName;
    private final String displayName;
    private final int legacyColor;
    private final float legacyRarity;
    private final String legacyParentBlockId;
    private final String legacyOreOverride;

    DirtyGem(
            final String id,
            final String legacyName,
            final String displayName,
            final int legacyColor,
            final float legacyRarity,
            final String legacyParentBlockId,
            final String legacyOreOverride
    ) {
        this.id = id;
        this.legacyName = legacyName;
        this.displayName = displayName;
        this.legacyColor = legacyColor;
        this.legacyRarity = legacyRarity;
        this.legacyParentBlockId = legacyParentBlockId;
        this.legacyOreOverride = legacyOreOverride;
    }

    public String id() {
        return this.id;
    }

    public String itemId() {
        return this.id + "_dirty_gem";
    }

    public String legacyName() {
        return this.legacyName;
    }

    public String displayName() {
        return this.displayName;
    }

    public int legacyColor() {
        return this.legacyColor;
    }

    public float legacyRarity() {
        return this.legacyRarity;
    }

    public String legacyParentBlockId() {
        return this.legacyParentBlockId;
    }

    public String legacyOreOverride() {
        return this.legacyOreOverride;
    }
}
