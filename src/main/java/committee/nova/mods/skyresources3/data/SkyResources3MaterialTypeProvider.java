package committee.nova.mods.skyresources3.data;

import com.google.gson.JsonObject;
import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.registry.ModDataPackRegistries;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

public final class SkyResources3MaterialTypeProvider implements DataProvider {
    private static final List<DustSeed> DUSTS = List.of(
            new DustSeed("iron", 3),
            new DustSeed("gold", 5),
            new DustSeed("copper", 1),
            new DustSeed("tin", 3),
            new DustSeed("silver", 4),
            new DustSeed("zinc", 2),
            new DustSeed("nickel", 5),
            new DustSeed("platinum", 7),
            new DustSeed("aluminum", 4),
            new DustSeed("lead", 4),
            new DustSeed("cobalt", 6),
            new DustSeed("ardite", 6),
            new DustSeed("osmium", 6),
            new DustSeed("draconium", 9),
            new DustSeed("titanium", 6),
            new DustSeed("tungsten", 6),
            new DustSeed("chrome", 8),
            new DustSeed("iridium", 11),
            new DustSeed("boron", 5),
            new DustSeed("lithium", 7),
            new DustSeed("magnesium", 5),
            new DustSeed("mithril", 9),
            new DustSeed("yellorium", 6),
            new DustSeed("uranium", 6),
            new DustSeed("thorium", 7)
    );
    private static final List<GemSeed> GEMS = List.of(
            new GemSeed("emerald", 0.015F),
            new GemSeed("diamond", 0.033F),
            new GemSeed("ruby", 0.015F),
            new GemSeed("sapphire", 0.015F),
            new GemSeed("peridot", 0.015F),
            new GemSeed("red_garnet", 0.015F),
            new GemSeed("yellow_garnet", 0.015F),
            new GemSeed("apatite", 0.600F),
            new GemSeed("amber", 0.021F),
            new GemSeed("lepidolite", 0.021F),
            new GemSeed("malachite", 0.021F),
            new GemSeed("onyx", 0.021F),
            new GemSeed("moldavite", 0.021F),
            new GemSeed("agate", 0.021F),
            new GemSeed("opal", 0.021F),
            new GemSeed("amethyst", 0.018F),
            new GemSeed("jasper", 0.018F),
            new GemSeed("aquamarine", 0.018F),
            new GemSeed("heliodor", 0.018F),
            new GemSeed("turquoise", 0.018F),
            new GemSeed("moonstone", 0.018F),
            new GemSeed("morganite", 0.018F),
            new GemSeed("carnelian", 0.018F),
            new GemSeed("beryl", 0.015F),
            new GemSeed("golden_beryl", 0.015F),
            new GemSeed("citrine", 0.015F),
            new GemSeed("indicolite", 0.015F),
            new GemSeed("garnet", 0.015F),
            new GemSeed("topaz", 0.015F),
            new GemSeed("ametrine", 0.015F),
            new GemSeed("tanzanite", 0.015F),
            new GemSeed("violet_sapphire", 0.012F),
            new GemSeed("alexandrite", 0.012F),
            new GemSeed("blue_topaz", 0.012F),
            new GemSeed("spinel", 0.012F),
            new GemSeed("iolite", 0.012F),
            new GemSeed("black_diamond", 0.009F),
            new GemSeed("chaos", 0.009F),
            new GemSeed("ender_essence", 0.009F),
            new GemSeed("dark", 0.27F),
            new GemSeed("quartz", 0.42F),
            new GemSeed("lapis", 0.54F),
            new GemSeed("quartz_black", 0.36F),
            new GemSeed("certus", 0.48F)
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
        return DataProvider.saveStable(output, json, this.dustTypePathProvider.json(typeId(dust.id)));
    }

    private CompletableFuture<?> saveGem(final CachedOutput output, final GemSeed gem) {
        final JsonObject json = new JsonObject();
        json.addProperty("source_tag", "c:gems/" + gem.id);
        json.addProperty("rarity", gem.rarity);
        return DataProvider.saveStable(output, json, this.gemTypePathProvider.json(typeId(gem.id)));
    }

    private static Identifier typeId(final String path) {
        return Identifier.fromNamespaceAndPath(Skyresources3.MODID, path);
    }

    private record DustSeed(String id, int rarity) {
    }

    private record GemSeed(String id, float rarity) {
    }
}
