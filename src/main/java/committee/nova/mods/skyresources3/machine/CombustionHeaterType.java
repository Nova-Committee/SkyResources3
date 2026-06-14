package committee.nova.mods.skyresources3.machine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import committee.nova.mods.skyresources3.Skyresources3;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public record CombustionHeaterType(
        String translationKey,
        Identifier bodyTexture,
        Identifier topTexture,
        float speed,
        float efficiency,
        Fuel fuel,
        List<Element> elements
) {
    public static final List<Element> DEFAULT_ELEMENTS = List.of(
            element(2.0F, 2.0F, 2.0F, 14.0F, 14.0F, 14.0F, TextureSlot.BODY),
            element(2.0F, 13.0F, 2.0F, 14.0F, 14.0F, 14.0F, TextureSlot.TOP)
    );
    public static final Codec<CombustionHeaterType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("translation_key").forGetter(CombustionHeaterType::translationKey),
            Identifier.CODEC.fieldOf("body_texture").forGetter(CombustionHeaterType::bodyTexture),
            Identifier.CODEC.fieldOf("top_texture").forGetter(CombustionHeaterType::topTexture),
            Codec.FLOAT.fieldOf("speed").forGetter(CombustionHeaterType::speed),
            Codec.FLOAT.fieldOf("efficiency").forGetter(CombustionHeaterType::efficiency),
            Fuel.CODEC.fieldOf("fuel").forGetter(CombustionHeaterType::fuel),
            Element.CODEC.listOf().optionalFieldOf("elements", DEFAULT_ELEMENTS).forGetter(CombustionHeaterType::elements)
    ).apply(instance, CombustionHeaterType::new));
    private static final CombustionHeaterType FALLBACK = new CombustionHeaterType(
            "block.skyresources3.combustion_heater.iron",
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "block/iron_machine"),
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "block/combustion"),
            1.0F,
            1.2F,
            Fuel.furnace(),
            DEFAULT_ELEMENTS
    );

    public CombustionHeaterType {
        elements = List.copyOf(elements);
    }

    public static CombustionHeaterType fallback() {
        return FALLBACK;
    }

    public float heatPerTick() {
        return this.speed * 10.0F;
    }

    public boolean isValidFuel(final ItemStack stack, final Level level) {
        return this.fuel.isValid(stack, level);
    }

    public float fuelHeat(final ItemStack stack, final Level level, final float combinedEfficiency) {
        return this.fuel.heat(stack, level, combinedEfficiency);
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

    public record Fuel(FuelKind kind, Optional<Identifier> item, int rate) {
        public static final Codec<Fuel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FuelKind.CODEC.fieldOf("kind").forGetter(Fuel::kind),
                Identifier.CODEC.optionalFieldOf("item").forGetter(Fuel::item),
                Codec.INT.optionalFieldOf("rate", 1).forGetter(Fuel::rate)
        ).apply(instance, Fuel::new));

        public static Fuel furnace() {
            return new Fuel(FuelKind.FURNACE, Optional.empty(), 1);
        }

        private boolean isValid(final ItemStack stack, final Level level) {
            if (stack.isEmpty()) {
                return false;
            }
            if (this.kind == FuelKind.FURNACE) {
                return stack.getBurnTime(RecipeType.SMELTING, level.fuelValues()) > 0;
            }
            return this.item.map(id -> stack.is(BuiltInRegistries.ITEM.getValue(id))).orElse(false);
        }

        private float heat(final ItemStack stack, final Level level, final float combinedEfficiency) {
            if (!this.isValid(stack, level)) {
                return 0.0F;
            }
            if (this.kind == FuelKind.FURNACE) {
                return stack.getBurnTime(RecipeType.SMELTING, level.fuelValues()) * combinedEfficiency;
            }
            return this.rate * combinedEfficiency;
        }
    }

    public enum TextureSlot implements StringRepresentable {
        BODY("body"),
        TOP("top");

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

    public enum FuelKind implements StringRepresentable {
        FURNACE("furnace"),
        ITEM("item");

        public static final Codec<FuelKind> CODEC = StringRepresentable.fromEnum(FuelKind::values);
        private final String serializedName;

        FuelKind(final String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public String getSerializedName() {
            return this.serializedName;
        }
    }
}
