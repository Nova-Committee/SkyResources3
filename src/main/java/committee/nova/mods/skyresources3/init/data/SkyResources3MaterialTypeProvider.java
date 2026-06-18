package committee.nova.mods.skyresources3.init.data;

import com.google.gson.JsonObject;
import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public final class SkyResources3MaterialTypeProvider implements DataProvider {
    private static final List<DustSeed> DUSTS = List.of(
            new DustSeed("iron", 3, 0xD8AF93),
            new DustSeed("gold", 5, 0xF5D566),
            new DustSeed("copper", 1, 0xC87541),
            new DustSeed("tin", 3, 0xD6DCE6),
            new DustSeed("silver", 4, 0xC7D1DD),
            new DustSeed("zinc", 2, 0xB5C0C6),
            new DustSeed("nickel", 5, 0xB9A96F),
            new DustSeed("platinum", 7, 0xB5DDE5),
            new DustSeed("aluminum", 4, 0xD5D8DB),
            new DustSeed("lead", 4, 0x5D6470),
            new DustSeed("cobalt", 6, 0x3865D8),
            new DustSeed("ardite", 6, 0xD06A28),
            new DustSeed("osmium", 6, 0x7FAFC6),
            new DustSeed("draconium", 9, 0xAE65FF),
            new DustSeed("titanium", 6, 0xB9C3CC),
            new DustSeed("tungsten", 6, 0x5B6470),
            new DustSeed("chrome", 8, 0xC9D2D7),
            new DustSeed("iridium", 11, 0xE5E8FF),
            new DustSeed("boron", 5, 0x3F3648),
            new DustSeed("lithium", 7, 0xEEE2DC),
            new DustSeed("magnesium", 5, 0xF0F0DC),
            new DustSeed("mithril", 9, 0x5ED7E8),
            new DustSeed("yellorium", 6, 0xD9E84F),
            new DustSeed("uranium", 6, 0x74B84A),
            new DustSeed("thorium", 7, 0x6E9566)
    );
    private static final List<GemSeed> GEMS = List.of(
            new GemSeed("emerald", 0.015F, 0x12DB3A),
            new GemSeed("diamond", 0.033F, 0x6BFFFD),
            new GemSeed("ruby", 0.015F, 0xFA1E1E),
            new GemSeed("sapphire", 0.015F, 0x1E46FA),
            new GemSeed("peridot", 0.015F, 0x1CB800),
            new GemSeed("red_garnet", 0.015F, 0xC90014),
            new GemSeed("yellow_garnet", 0.015F, 0xF7FF0F),
            new GemSeed("apatite", 0.600F, 0x2B95FF),
            new GemSeed("amber", 0.021F, 0xF5CC53),
            new GemSeed("lepidolite", 0.021F, 0x57008A),
            new GemSeed("malachite", 0.021F, 0x23AD00),
            new GemSeed("onyx", 0.021F, 0x3D3D3D),
            new GemSeed("moldavite", 0.021F, 0xADFF99),
            new GemSeed("agate", 0.021F, 0xFF63FF),
            new GemSeed("opal", 0.021F, 0xDEDEDE),
            new GemSeed("amethyst", 0.018F, 0x780078),
            new GemSeed("jasper", 0.018F, 0x874800),
            new GemSeed("aquamarine", 0.018F, 0x36E7FF),
            new GemSeed("heliodor", 0.018F, 0xFFFF7D),
            new GemSeed("turquoise", 0.018F, 0x2EF2C8),
            new GemSeed("moonstone", 0.018F, 0x016A8A),
            new GemSeed("morganite", 0.018F, 0xFA61FF),
            new GemSeed("carnelian", 0.018F, 0x630606),
            new GemSeed("beryl", 0.015F, 0x46E334),
            new GemSeed("golden_beryl", 0.015F, 0xD6AE2B),
            new GemSeed("citrine", 0.015F, 0x871616),
            new GemSeed("indicolite", 0.015F, 0x39E6BD),
            new GemSeed("garnet", 0.015F, 0xFF9999),
            new GemSeed("topaz", 0.015F, 0xFFD399),
            new GemSeed("ametrine", 0.015F, 0xA300BF),
            new GemSeed("tanzanite", 0.015F, 0x00076E),
            new GemSeed("violet_sapphire", 0.012F, 0x451287),
            new GemSeed("alexandrite", 0.012F, 0xE3E3E3),
            new GemSeed("blue_topaz", 0.012F, 0x1000C4),
            new GemSeed("spinel", 0.012F, 0x750000),
            new GemSeed("iolite", 0.012F, 0x9502CF),
            new GemSeed("black_diamond", 0.009F, 0x262626),
            new GemSeed("chaos", 0.009F, 0xFFE6FB),
            new GemSeed("ender_essence", 0.009F, 0x356E19),
            new GemSeed("dark", 0.27F, 0x242424),
            new GemSeed("quartz", 0.42F, 0xFFFFFF),
            new GemSeed("lapis", 0.54F, 0x075BBA),
            new GemSeed("quartz_black", 0.36F, 0x171717),
            new GemSeed("certus", 0.48F, 0xB0F4F7)
    );

    private final PackOutput.PathProvider dustTypePathProvider;
    private final PackOutput.PathProvider gemTypePathProvider;

    public SkyResources3MaterialTypeProvider(final PackOutput output) {
        this.dustTypePathProvider = output.createRegistryElementsPathProvider(
                ModDataPackRegistries.ORE_ALCHEMY_DUST_TYPES
        );
        this.gemTypePathProvider = output.createRegistryElementsPathProvider(ModDataPackRegistries.DIRTY_GEM_TYPES);
    }

    @Override
    public CompletableFuture<?> run(final CachedOutput output) {
        final CompletableFuture<?>[] futures = new CompletableFuture<?>[DUSTS.size() + GEMS.size()];
        int index = 0;
        for (final DustSeed dust : DUSTS) {
            futures[index++] = this.saveDust(output, dust);
        }
        for (final GemSeed gem : GEMS) {
            futures[index++] = this.saveGem(output, gem);
        }
        return CompletableFuture.allOf(futures);
    }

    @Override
    public String getName() {
        return "SkyResources3 Material Types";
    }

    private CompletableFuture<?> saveDust(final CachedOutput output, final DustSeed dust) {
        final JsonObject json = new JsonObject();
        json.addProperty("source_tag", "c:ores/" + dust.id);
        json.addProperty("rarity", dust.rarity);
        json.addProperty("color", color(dust.color));
        return DataProvider.saveStable(output, json, this.dustTypePathProvider.json(typeId(dust.id)));
    }

    private CompletableFuture<?> saveGem(final CachedOutput output, final GemSeed gem) {
        final JsonObject json = new JsonObject();
        json.addProperty("source_tag", "c:gems/" + gem.id);
        json.addProperty("rarity", gem.rarity);
        json.addProperty("color", color(gem.color));
        return DataProvider.saveStable(output, json, this.gemTypePathProvider.json(typeId(gem.id)));
    }

    private static ResourceLocation typeId(final String path) {
        return ResourceLocation.fromNamespaceAndPath(Skyresources3.MODID, path);
    }

    private static String color(final int rgb) {
        return String.format(Locale.ROOT, "#%06X", rgb & 0xFFFFFF);
    }

    private record DustSeed(String id, int rarity, int color) {
    }

    private record GemSeed(String id, float rarity, int color) {
    }
}
