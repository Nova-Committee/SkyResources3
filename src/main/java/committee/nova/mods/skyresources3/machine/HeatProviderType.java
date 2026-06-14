package committee.nova.mods.skyresources3.machine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import committee.nova.mods.skyresources3.Skyresources3;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public record HeatProviderType(
        String translationKey,
        Identifier texture,
        float speed,
        float efficiency,
        MachineFuel fuel,
        List<CasingType.Element> elements
) {
    public static final List<CasingType.Element> DEFAULT_ELEMENTS = List.of(
            element(3.0F, 2.0F, 3.0F, 13.0F, 4.0F, 13.0F),
            element(4.0F, 4.0F, 4.0F, 12.0F, 12.0F, 12.0F),
            element(5.0F, 12.0F, 5.0F, 11.0F, 14.0F, 11.0F)
    );
    public static final Codec<HeatProviderType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("translation_key").forGetter(HeatProviderType::translationKey),
            Identifier.CODEC.fieldOf("texture").forGetter(HeatProviderType::texture),
            Codec.FLOAT.fieldOf("speed").forGetter(HeatProviderType::speed),
            Codec.FLOAT.fieldOf("efficiency").forGetter(HeatProviderType::efficiency),
            MachineFuel.CODEC.fieldOf("fuel").forGetter(HeatProviderType::fuel),
            CasingType.Element.CODEC.listOf().optionalFieldOf("elements", DEFAULT_ELEMENTS).forGetter(HeatProviderType::elements)
    ).apply(instance, HeatProviderType::new));
    private static final HeatProviderType FALLBACK = new HeatProviderType(
            "block.skyresources.heat_provider.iron",
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "block/iron_machine"),
            1.0F,
            1.2F,
            MachineFuel.furnace(),
            DEFAULT_ELEMENTS
    );

    public HeatProviderType {
        elements = List.copyOf(elements);
    }

    public static HeatProviderType fallback() {
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
