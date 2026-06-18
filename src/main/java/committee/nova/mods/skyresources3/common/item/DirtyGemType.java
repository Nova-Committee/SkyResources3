package committee.nova.mods.skyresources3.common.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;

public record DirtyGemType(TagKey<Item> sourceTag, float rarity, int color) {
    public static final int DEFAULT_COLOR = 0xFFFFFFFF;

    public static final Codec<DirtyGemType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.ITEM).fieldOf("source_tag").forGetter(DirtyGemType::sourceTag),
            Codec.FLOAT.fieldOf("rarity").forGetter(DirtyGemType::rarity),
            ItemColorCodecs.RGB_COLOR.optionalFieldOf("color", DEFAULT_COLOR).forGetter(DirtyGemType::color)
    ).apply(instance, DirtyGemType::new));

    public DirtyGemType {
        color = FastColor.ARGB32.opaque(color);
    }

    public boolean isAvailable(final HolderLookup.Provider registries) {
        return ItemTagAvailability.hasEntries(registries, this.sourceTag);
    }
}
