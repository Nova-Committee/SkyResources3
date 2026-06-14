package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.item.DirtyGemItem;
import committee.nova.mods.skyresources3.item.DirtyGemType;
import committee.nova.mods.skyresources3.item.OreAlchemyDustItem;
import committee.nova.mods.skyresources3.item.OreAlchemyDustType;
import committee.nova.mods.skyresources3.registry.ModDataPackRegistries;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.ToIntFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class MaterialItemTintSources {
    public static final Identifier ORE_ALCHEMY_DUST =
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "ore_alchemy_dust_color");
    public static final Identifier DIRTY_GEM =
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "dirty_gem_color");

    public record OreAlchemyDustColor(int defaultColor) implements ItemTintSource {
        public static final MapCodec<OreAlchemyDustColor> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ExtraCodecs.STRING_RGB_COLOR
                                .optionalFieldOf("default", OreAlchemyDustType.DEFAULT_COLOR)
                                .forGetter(OreAlchemyDustColor::defaultColor)
                ).apply(instance, OreAlchemyDustColor::new)
        );

        public OreAlchemyDustColor {
            defaultColor = ARGB.opaque(defaultColor);
        }

        @Override
        public int calculate(
                final ItemStack stack,
                @Nullable final ClientLevel level,
                @Nullable final LivingEntity entity
        ) {
            return colorFromRegistry(
                    level,
                    ModDataPackRegistries.ORE_ALCHEMY_DUST_TYPES,
                    ModDataPackRegistries.oreAlchemyDustTypeKey(OreAlchemyDustItem.oreAlchemyDustTypeId(stack)),
                    OreAlchemyDustType::color,
                    this.defaultColor
            );
        }

        @Override
        public MapCodec<OreAlchemyDustColor> type() {
            return MAP_CODEC;
        }
    }

    public record DirtyGemColor(int defaultColor) implements ItemTintSource {
        public static final MapCodec<DirtyGemColor> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ExtraCodecs.STRING_RGB_COLOR
                                .optionalFieldOf("default", DirtyGemType.DEFAULT_COLOR)
                                .forGetter(DirtyGemColor::defaultColor)
                ).apply(instance, DirtyGemColor::new)
        );

        public DirtyGemColor {
            defaultColor = ARGB.opaque(defaultColor);
        }

        @Override
        public int calculate(
                final ItemStack stack,
                @Nullable final ClientLevel level,
                @Nullable final LivingEntity entity
        ) {
            return colorFromRegistry(
                    level,
                    ModDataPackRegistries.DIRTY_GEM_TYPES,
                    ModDataPackRegistries.dirtyGemTypeKey(DirtyGemItem.dirtyGemTypeId(stack)),
                    DirtyGemType::color,
                    this.defaultColor
            );
        }

        @Override
        public MapCodec<DirtyGemColor> type() {
            return MAP_CODEC;
        }
    }

    private static <T> int colorFromRegistry(
            @Nullable final ClientLevel level,
            final ResourceKey<Registry<T>> registryKey,
            final ResourceKey<T> typeKey,
            final ToIntFunction<T> colorGetter,
            final int defaultColor
    ) {
        final ClientLevel lookupLevel = level != null ? level : Minecraft.getInstance().level;
        if (lookupLevel == null) {
            return ARGB.opaque(defaultColor);
        }
        return lookupLevel.registryAccess()
                .lookup(registryKey)
                .flatMap(registry -> registry.get(typeKey))
                .map(reference -> ARGB.opaque(colorGetter.applyAsInt(reference.value())))
                .orElseGet(() -> ARGB.opaque(defaultColor));
    }

    private MaterialItemTintSources() {
    }
}
