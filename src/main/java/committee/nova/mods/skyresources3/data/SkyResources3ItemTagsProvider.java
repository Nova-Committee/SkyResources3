package committee.nova.mods.skyresources3.data;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.registry.ModItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

public final class SkyResources3ItemTagsProvider extends ItemTagsProvider {
    public SkyResources3ItemTagsProvider(
            final PackOutput output,
            final CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(output, lookupProvider, Skyresources3.MODID);
    }

    @Override
    protected void addTags(final HolderLookup.Provider lookupProvider) {
        this.tag(ItemTags.PLANKS).add(ModItems.PETRIFIED_PLANKS.get());
    }
}
