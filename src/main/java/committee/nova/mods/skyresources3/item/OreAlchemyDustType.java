package committee.nova.mods.skyresources3.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;

public record OreAlchemyDustType(TagKey<Item> sourceTag, int rarity, int color) {
    public static final int DEFAULT_COLOR = 0xFFFFFFFF;

    public static final Codec<OreAlchemyDustType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.ITEM).fieldOf("source_tag").forGetter(OreAlchemyDustType::sourceTag),
            ExtraCodecs.POSITIVE_INT.fieldOf("rarity").forGetter(OreAlchemyDustType::rarity),
            ExtraCodecs.STRING_RGB_COLOR.optionalFieldOf("color", DEFAULT_COLOR).forGetter(OreAlchemyDustType::color)
    ).apply(instance, OreAlchemyDustType::new));

    public OreAlchemyDustType {
        color = ARGB.opaque(color);
    }

    public boolean isAvailable(final HolderLookup.Provider registries) {
        return ItemTagAvailability.hasEntries(registries, this.sourceTag);
    }
}
