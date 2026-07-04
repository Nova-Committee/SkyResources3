package committee.nova.mods.skyresources3.init.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public final class SkyResources3DataMapProvider implements DataProvider {
    public SkyResources3DataMapProvider(final PackOutput output) {
    }

    @Override
    public CompletableFuture<?> run(final CachedOutput output) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "SkyResources3 data maps";
    }
}
