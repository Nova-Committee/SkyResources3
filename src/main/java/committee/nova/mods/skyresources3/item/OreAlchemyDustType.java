package committee.nova.mods.skyresources3.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;

public record OreAlchemyDustType(TagKey<Item> sourceTag, int rarity) {
    public static final Codec<OreAlchemyDustType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.ITEM).fieldOf("source_tag").forGetter(OreAlchemyDustType::sourceTag),
            ExtraCodecs.POSITIVE_INT.fieldOf("rarity").forGetter(OreAlchemyDustType::rarity)
    ).apply(instance, OreAlchemyDustType::new));

    public boolean isAvailable(final HolderLookup.Provider registries) {
        return ItemTagAvailability.hasEntries(registries, this.sourceTag);
    }
}
