package committee.nova.mods.skyresources3.machine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import committee.nova.mods.skyresources3.Skyresources3;
import java.util.List;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;

public record CondenserType(
        String translationKey,
        Identifier texture,
        float speed,
        float efficiency,
        List<CasingType.Element> elements
) {
    public static final List<CasingType.Element> DEFAULT_ELEMENTS = List.of(
            element(3.0F, 2.0F, 3.0F, 13.0F, 5.0F, 13.0F),
            element(2.0F, 5.0F, 2.0F, 14.0F, 8.0F, 14.0F),
            element(4.0F, 8.0F, 4.0F, 12.0F, 13.0F, 12.0F)
    );
    public static final Codec<CondenserType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("translation_key").forGetter(CondenserType::translationKey),
            Identifier.CODEC.fieldOf("texture").forGetter(CondenserType::texture),
            Codec.FLOAT.fieldOf("speed").forGetter(CondenserType::speed),
            Codec.FLOAT.fieldOf("efficiency").forGetter(CondenserType::efficiency),
            CasingType.Element.CODEC.listOf().optionalFieldOf("elements", DEFAULT_ELEMENTS).forGetter(CondenserType::elements)
    ).apply(instance, CondenserType::new));
    private static final CondenserType FALLBACK = new CondenserType(
            "block.skyresources3.condenser.iron",
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "block/iron_machine"),
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

    private static CasingType.Element element(
            final float fromX,
            final float fromY,
            final float fromZ,
            final float toX,
            final float toY,
            final float toZ
    ) {
        return new CasingType.Element(
                new Vector3f(fromX, fromY, fromZ),
                new Vector3f(toX, toY, toZ)
        );
    }
}
