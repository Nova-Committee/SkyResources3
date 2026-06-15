package committee.nova.mods.skyresources3.init.data;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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
        this.tag(commonItemTag("ores/iron")).add(Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE);
        this.tag(commonItemTag("ores/gold")).add(Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE, Items.NETHER_GOLD_ORE);
        this.tag(commonItemTag("ores/copper")).add(Items.COPPER_ORE, Items.DEEPSLATE_COPPER_ORE);
        this.tag(commonItemTag("gems/emerald")).add(Items.EMERALD);
        this.tag(commonItemTag("gems/diamond")).add(Items.DIAMOND);
        this.tag(commonItemTag("gems/quartz")).add(Items.QUARTZ);
        this.tag(commonItemTag("gems/lapis")).add(Items.LAPIS_LAZULI);
    }

    private static TagKey<Item> commonItemTag(final String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
    }
}
