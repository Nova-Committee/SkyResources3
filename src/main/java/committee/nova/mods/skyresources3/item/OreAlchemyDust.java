package committee.nova.mods.skyresources3.item;

public enum OreAlchemyDust {
    IRON("iron", "Iron", 3, "minecraft:stone", true),
    GOLD("gold", "Gold", 5, "minecraft:stone", true),
    COPPER("copper", "Copper", 1, "minecraft:stone", true),
    TIN("tin", "Tin", 3, "minecraft:stone", true),
    SILVER("silver", "Silver", 4, "minecraft:stone", true),
    ZINC("zinc", "Zinc", 2, "minecraft:stone", true),
    NICKEL("nickel", "Nickel", 5, "minecraft:stone", true),
    PLATINUM("platinum", "Platinum", 7, "minecraft:stone", true),
    ALUMINUM("aluminum", "Aluminum", 4, "minecraft:stone", true),
    LEAD("lead", "Lead", 4, "minecraft:stone", true),
    COBALT("cobalt", "Cobalt", 6, "minecraft:netherrack", true),
    ARDITE("ardite", "Ardite", 6, "minecraft:netherrack", true),
    OSMIUM("osmium", "Osmium", 6, "minecraft:stone", true),
    DRACONIUM("draconium", "Draconium", 9, "minecraft:end_stone", false),
    TITANIUM("titanium", "Titanium", 6, "minecraft:stone", true),
    TUNGSTEN("tungsten", "Tungsten", 6, "minecraft:end_stone", true),
    CHROME("chrome", "Chrome", 8, "minecraft:stone", true),
    IRIDIUM("iridium", "Iridium", 11, "minecraft:stone", true),
    BORON("boron", "Boron", 5, "minecraft:stone", true),
    LITHIUM("lithium", "Lithium", 7, "minecraft:stone", true),
    MAGNESIUM("magnesium", "Magnesium", 5, "minecraft:stone", true),
    MITHRIL("mithril", "Mithril", 9, "minecraft:stone", true),
    YELLORIUM("yellorium", "Yellorium", 6, "minecraft:stone", false),
    URANIUM("uranium", "Uranium", 6, "minecraft:stone", false),
    THORIUM("thorium", "Thorium", 7, "minecraft:stone", false);

    private final String id;
    private final String displayName;
    private final int legacyRarity;
    private final String legacyParentBlockId;
    private final boolean automatic;

    OreAlchemyDust(
            final String id,
            final String displayName,
            final int legacyRarity,
            final String legacyParentBlockId,
            final boolean automatic
    ) {
        this.id = id;
        this.displayName = displayName;
        this.legacyRarity = legacyRarity;
        this.legacyParentBlockId = legacyParentBlockId;
        this.automatic = automatic;
    }

    public String id() {
        return this.id;
    }

    public String itemId() {
        return this.id + "_ore_alchemical_dust";
    }

    public String displayName() {
        return this.displayName;
    }

    public int legacyRarity() {
        return this.legacyRarity;
    }

    public String legacyParentBlockId() {
        return this.legacyParentBlockId;
    }

    public boolean automatic() {
        return this.automatic;
    }
}
