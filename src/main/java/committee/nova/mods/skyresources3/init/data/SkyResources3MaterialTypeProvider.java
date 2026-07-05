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
    private final PackOutput.PathProvider dustTypePathProvider;
    private final PackOutput.PathProvider gemTypePathProvider;

    public SkyResources3MaterialTypeProvider(final PackOutput output) {
        this.dustTypePathProvider = output.createPathProvider(
                PackOutput.Target.DATA_PACK,
                ModDataPackRegistries.ORE_ALCHEMY_DUST_TYPES.location().getPath()
        );
        this.gemTypePathProvider = output.createPathProvider(
                PackOutput.Target.DATA_PACK,
                ModDataPackRegistries.DIRTY_GEM_TYPES.location().getPath()
        );
    }

    @Override
    public CompletableFuture<?> run(final CachedOutput output) {
        final List<SkyResources3MaterialSeeds.DustSeed> dusts = SkyResources3MaterialSeeds.dusts();
        final List<SkyResources3MaterialSeeds.GemSeed> gems = SkyResources3MaterialSeeds.gems();
        final CompletableFuture<?>[] futures = new CompletableFuture<?>[dusts.size() + gems.size()];
        int index = 0;
        for (final SkyResources3MaterialSeeds.DustSeed dust : dusts) {
            futures[index++] = this.saveDust(output, dust);
        }
        for (final SkyResources3MaterialSeeds.GemSeed gem : gems) {
            futures[index++] = this.saveGem(output, gem);
        }
        return CompletableFuture.allOf(futures);
    }

    @Override
    public String getName() {
        return "SkyResources3 Material Types";
    }

    private CompletableFuture<?> saveDust(final CachedOutput output, final SkyResources3MaterialSeeds.DustSeed dust) {
        final JsonObject json = new JsonObject();
        json.addProperty("source_tag", SkyResources3MaterialSeeds.commonItemTag(dust.sourceTagPath()));
        json.addProperty("rarity", dust.rarity());
        json.addProperty("color", color(dust.color()));
        return DataProvider.saveStable(output, json, this.dustTypePathProvider.json(typeId(dust.id())));
    }

    private CompletableFuture<?> saveGem(final CachedOutput output, final SkyResources3MaterialSeeds.GemSeed gem) {
        final JsonObject json = new JsonObject();
        json.addProperty("source_tag", SkyResources3MaterialSeeds.commonItemTag(gem.sourceTagPath()));
        json.addProperty("rarity", gem.rarity());
        json.addProperty("color", color(gem.color()));
        return DataProvider.saveStable(output, json, this.gemTypePathProvider.json(typeId(gem.id())));
    }

    private static ResourceLocation typeId(final String path) {
        return new ResourceLocation(Skyresources3.MODID, path);
    }

    private static String color(final int rgb) {
        return String.format(Locale.ROOT, "#%06X", rgb & 0xFFFFFF);
    }

}
