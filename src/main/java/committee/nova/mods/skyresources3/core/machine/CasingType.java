package committee.nova.mods.skyresources3.core.machine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import committee.nova.mods.skyresources3.Skyresources3;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.joml.Vector3f;

public record CasingType(
        String translationKey,
        Identifier texture,
        int maxHeat,
        float efficiency,
        StructureRule structureRule,
        List<Element> elements
) {
    public static final List<Element> DEFAULT_FRAME_ELEMENTS = List.of(
            element(0.0F, 0.0F, 0.0F, 2.0F, 16.0F, 2.0F),
            element(0.0F, 0.0F, 0.0F, 16.0F, 2.0F, 2.0F),
            element(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 16.0F),
            element(14.0F, 0.0F, 0.0F, 16.0F, 2.0F, 16.0F),
            element(0.0F, 0.0F, 14.0F, 16.0F, 2.0F, 16.0F),
            element(14.0F, 0.0F, 14.0F, 16.0F, 16.0F, 16.0F),
            element(14.0F, 0.0F, 0.0F, 16.0F, 16.0F, 2.0F),
            element(0.0F, 0.0F, 14.0F, 2.0F, 16.0F, 16.0F),
            element(0.0F, 14.0F, 0.0F, 16.0F, 16.0F, 2.0F),
            element(0.0F, 14.0F, 0.0F, 2.0F, 16.0F, 16.0F),
            element(14.0F, 14.0F, 0.0F, 16.0F, 16.0F, 16.0F),
            element(0.0F, 14.0F, 14.0F, 16.0F, 16.0F, 16.0F)
    );
    public static final Codec<CasingType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("translation_key").forGetter(CasingType::translationKey),
            Identifier.CODEC.fieldOf("texture").forGetter(CasingType::texture),
            Codec.INT.fieldOf("max_heat").forGetter(CasingType::maxHeat),
            Codec.FLOAT.fieldOf("efficiency").forGetter(CasingType::efficiency),
            StructureRule.CODEC.fieldOf("structure_rule").forGetter(CasingType::structureRule),
            Element.CODEC.listOf().optionalFieldOf("elements", DEFAULT_FRAME_ELEMENTS).forGetter(CasingType::elements)
    ).apply(instance, CasingType::new));
    private static final CasingType FALLBACK = new CasingType(
            "block.skyresources.machine_casing.iron",
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "block/iron_machine"),
            1538,
            1.2F,
            StructureRule.METAL,
            DEFAULT_FRAME_ELEMENTS
    );

    public CasingType {
        elements = List.copyOf(elements);
    }

    public static CasingType fallback() {
        return FALLBACK;
    }

    private static Element element(
            final float fromX,
            final float fromY,
            final float fromZ,
            final float toX,
            final float toY,
            final float toZ
    ) {
        return new Element(new Vector3f(fromX, fromY, fromZ), new Vector3f(toX, toY, toZ));
    }

    public record Element(Vector3f from, Vector3f to) {
        public static final Codec<Element> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.VECTOR3F.fieldOf("from").forGetter(Element::from),
                ExtraCodecs.VECTOR3F.fieldOf("to").forGetter(Element::to)
        ).apply(instance, (from, to) -> new Element(new Vector3f(from), new Vector3f(to))));

        public Element {
            from = new Vector3f(from);
            to = new Vector3f(to);
        }
    }

    public enum StructureRule implements StringRepresentable {
        WOOD("wood"),
        STONE("stone"),
        METAL("metal");

        public static final Codec<StructureRule> CODEC = StringRepresentable.fromEnum(StructureRule::values);
        private final String serializedName;

        StructureRule(final String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public String getSerializedName() {
            return this.serializedName;
        }
    }
}
