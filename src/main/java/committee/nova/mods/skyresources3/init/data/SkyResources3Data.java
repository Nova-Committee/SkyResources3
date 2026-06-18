package committee.nova.mods.skyresources3.init.data;

import committee.nova.mods.skyresources3.Skyresources3;
import java.util.List;
import java.util.Set;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Skyresources3.MODID)
public final class SkyResources3Data {
    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        event.createProvider(SkyResources3DataMapProvider::new);
        event.createProvider(SkyResources3MaterialTypeProvider::new);
        event.createProvider(SkyResources3RecipeProvider::new);
        event.createProvider(SkyResources3IntegrationRecipeProvider::new);
        event.createBlockAndItemTags(SkyResources3BlockTagsProvider::new, SkyResources3ItemTagsProvider::new);
        event.createProvider((output, lookupProvider) -> new LootTableProvider(
                output,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(
                        SkyResources3BlockLootProvider::new,
                        LootContextParamSets.BLOCK
                )),
                lookupProvider
        ));
    }

    private SkyResources3Data() {
    }
}
