package committee.nova.mods.skyresources3.data;

import committee.nova.mods.skyresources3.registry.ModItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

public final class SkyResources3DataMapProvider extends DataMapProvider {
    private static final int ALCHEMICAL_COAL_BURN_TIME = 3000;
    private static final int COAL_INFUSED_BLOCK_BURN_TIME = 30000;
    private static final int COMPRESSED_COAL_BLOCK_BURN_TIME = 128000;

    public SkyResources3DataMapProvider(
            final PackOutput output,
            final CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(output, lookupProvider);
    }

    @Override
    protected void gather(final HolderLookup.Provider provider) {
        this.builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(ModItems.ALCHEMICAL_COAL, new FurnaceFuel(ALCHEMICAL_COAL_BURN_TIME), false)
                .add(ModItems.COAL_INFUSED_BLOCK, new FurnaceFuel(COAL_INFUSED_BLOCK_BURN_TIME), false)
                .add(ModItems.COMPRESSED_COAL_BLOCK, new FurnaceFuel(COMPRESSED_COAL_BLOCK_BURN_TIME), false);
    }
}
