package committee.nova.mods.skyresources3.init.data;

import committee.nova.mods.skyresources3.Skyresources3;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Skyresources3.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class SkyResources3Data {
    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput output = generator.getPackOutput();
        final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        final boolean includeServer = event.includeServer();

        generator.addProvider(includeServer, new SkyResources3DataMapProvider(output));
        generator.addProvider(includeServer, new SkyResources3MaterialTypeProvider(output));
        generator.addProvider(includeServer, new SkyResources3RecipeProvider(output));
        generator.addProvider(includeServer, new SkyResources3IntegrationRecipeProvider(output));

        final SkyResources3BlockTagsProvider blockTags = generator.addProvider(
                includeServer,
                new SkyResources3BlockTagsProvider(output, lookupProvider)
        );
        generator.addProvider(
                includeServer,
                new SkyResources3ItemTagsProvider(output, lookupProvider, blockTags.contentsGetter())
        );

        generator.addProvider(includeServer, new LootTableProvider(
                output,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(
                        SkyResources3BlockLootProvider::new,
                        LootContextParamSets.BLOCK
                ))
        ));
    }

    private SkyResources3Data() {
    }
}
