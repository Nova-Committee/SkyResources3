package committee.nova.mods.skyresources3.init.data;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.data.tags.ItemTagsProvider;

public final class SkyResources3ItemTagsProvider extends ItemTagsProvider {
    private static final String PRIMARY_COMMON_NAMESPACE = "forge";
    private static final String LEGACY_COMMON_NAMESPACE = "c";

    public SkyResources3ItemTagsProvider(
            final PackOutput output,
            final CompletableFuture<HolderLookup.Provider> lookupProvider,
            final CompletableFuture<TagsProvider.TagLookup<Block>> blockTags
    ) {
        super(output, lookupProvider, blockTags, Skyresources3.MODID, null);
    }

    @Override
    protected void addTags(final HolderLookup.Provider lookupProvider) {
        this.tag(ItemTags.PLANKS).add(ModItems.PETRIFIED_PLANKS.get());
        this.addCommonItemTag("ores/iron", Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE);
        this.addCommonItemTag("ores/gold", Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE, Items.NETHER_GOLD_ORE);
        this.addCommonItemTag("ores/copper", Items.COPPER_ORE, Items.DEEPSLATE_COPPER_ORE);
        this.addCommonItemTag("gems/emerald", Items.EMERALD);
        this.addCommonItemTag("gems/diamond", Items.DIAMOND);
        this.addCommonItemTag("gems/quartz", Items.QUARTZ);
        this.addCommonItemTag("gems/lapis", Items.LAPIS_LAZULI);
    }

    private void addCommonItemTag(final String path, final Item... entries) {
        this.tag(commonItemTag(PRIMARY_COMMON_NAMESPACE, path)).add(entries);
        this.tag(commonItemTag(LEGACY_COMMON_NAMESPACE, path)).add(entries);
    }

    private static TagKey<Item> commonItemTag(final String namespace, final String path) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(namespace, path));
    }
}
