package committee.nova.mods.skyresources3.core.machine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import committee.nova.mods.skyresources3.Skyresources3;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.joml.Vector3f;

public record CondenserType(
        String translationKey,
        ResourceLocation texture,
        ResourceLocation partTexture,
        float speed,
        float efficiency,
        List<Element> elements
) {
    public static final ResourceLocation DEFAULT_PART_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Skyresources3.MODID, "block/condenser");
    public static final List<Element> DEFAULT_ELEMENTS = List.of(
            element(2.0F, 2.0F, 2.0F, 14.0F, 14.0F, 14.0F, TextureSlot.BODY),
            element(2.0F, 13.0F, 2.0F, 14.0F, 14.0F, 14.0F, TextureSlot.PART)
    );
    public static final Codec<CondenserType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("translation_key").forGetter(CondenserType::translationKey),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(CondenserType::texture),
            ResourceLocation.CODEC.optionalFieldOf("part_texture", DEFAULT_PART_TEXTURE).forGetter(CondenserType::partTexture),
            Codec.FLOAT.fieldOf("speed").forGetter(CondenserType::speed),
            Codec.FLOAT.fieldOf("efficiency").forGetter(CondenserType::efficiency),
            Element.CODEC.listOf().optionalFieldOf("elements", DEFAULT_ELEMENTS).forGetter(CondenserType::elements)
    ).apply(instance, CondenserType::new));
    private static final CondenserType FALLBACK = new CondenserType(
            "block.skyresources.condenser.iron",
            ResourceLocation.fromNamespaceAndPath(Skyresources3.MODID, "block/iron_machine"),
            DEFAULT_PART_TEXTURE,
            1.0F,
            1.2F,
            DEFAULT_ELEMENTS
    );

    public CondenserType {
        elements = List.copyOf(elements);
    }

    public static CondenserType fallback() {
        return FALLBACK;
    }

    private static Element element(
            final float fromX,
            final float fromY,
            final float fromZ,
            final float toX,
            final float toY,
            final float toZ,
            final TextureSlot texture
    ) {
        return new Element(new Vector3f(fromX, fromY, fromZ), new Vector3f(toX, toY, toZ), texture);
    }

    public record Element(Vector3f from, Vector3f to, TextureSlot texture) {
        public static final Codec<Element> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.VECTOR3F.fieldOf("from").forGetter(Element::from),
                ExtraCodecs.VECTOR3F.fieldOf("to").forGetter(Element::to),
                TextureSlot.CODEC.optionalFieldOf("texture", TextureSlot.BODY).forGetter(Element::texture)
        ).apply(instance, (from, to, texture) -> new Element(new Vector3f(from), new Vector3f(to), texture)));

        public Element {
            from = new Vector3f(from);
            to = new Vector3f(to);
        }
    }

    public enum TextureSlot implements StringRepresentable {
        BODY("body"),
        PART("part");

        public static final Codec<TextureSlot> CODEC = StringRepresentable.fromEnum(TextureSlot::values);
        private final String serializedName;

        TextureSlot(final String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public String getSerializedName() {
            return this.serializedName;
        }
    }
}
