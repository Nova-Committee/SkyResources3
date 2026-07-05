package committee.nova.mods.skyresources3.common.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

final class ItemTagAvailability {
    static boolean hasEntries(final HolderLookup.Provider registries, final TagKey<Item> tag) {
        if (hasEntriesExact(registries, tag)) {
            return true;
        }
        return commonAlias(tag)
                .map(alias -> hasEntriesExact(registries, alias))
                .orElse(false);
    }

    private static boolean hasEntriesExact(final HolderLookup.Provider registries, final TagKey<Item> tag) {
        return registries.lookup(Registries.ITEM)
                .flatMap(items -> items.get(tag))
                .map(ItemTagAvailability::hasAny)
                .orElse(false);
    }

    private static java.util.Optional<TagKey<Item>> commonAlias(final TagKey<Item> tag) {
        final ResourceLocation id = tag.location();
        final String aliasNamespace = switch (id.getNamespace()) {
            case "forge" -> "c";
            case "c" -> "forge";
            default -> null;
        };
        if (aliasNamespace == null) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(TagKey.create(
                Registries.ITEM,
                new ResourceLocation(aliasNamespace, id.getPath())
        ));
    }

    private static boolean hasAny(final HolderSet.Named<Item> entries) {
        return entries.stream().findAny().isPresent();
    }

    private ItemTagAvailability() {
    }
}
