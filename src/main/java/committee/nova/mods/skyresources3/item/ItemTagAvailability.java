package committee.nova.mods.skyresources3.item;

import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

final class ItemTagAvailability {
    static boolean hasEntries(final HolderLookup.Provider registries, final TagKey<Item> tag) {
        return registries.lookup(Registries.ITEM)
                .flatMap(items -> items.get(tag))
                .map(ItemTagAvailability::hasAny)
                .orElse(false);
    }

    private static boolean hasAny(final HolderSet.Named<Item> entries) {
        return entries.stream().findAny().isPresent();
    }

    private ItemTagAvailability() {
    }
}
