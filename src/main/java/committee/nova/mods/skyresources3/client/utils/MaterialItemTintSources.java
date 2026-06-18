package committee.nova.mods.skyresources3.client.utils;

import committee.nova.mods.skyresources3.common.item.DirtyGemItem;
import committee.nova.mods.skyresources3.common.item.DirtyGemType;
import committee.nova.mods.skyresources3.common.item.OreAlchemyDustItem;
import committee.nova.mods.skyresources3.common.item.OreAlchemyDustType;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import java.util.function.ToIntFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;

public final class MaterialItemTintSources {
    public static int oreAlchemyDustColor(final ItemStack stack) {
        return colorFromRegistry(
                Minecraft.getInstance().level,
                ModDataPackRegistries.ORE_ALCHEMY_DUST_TYPES,
                ModDataPackRegistries.oreAlchemyDustTypeKey(OreAlchemyDustItem.oreAlchemyDustTypeId(stack)),
                OreAlchemyDustType::color,
                OreAlchemyDustType.DEFAULT_COLOR
        );
    }

    public static int dirtyGemColor(final ItemStack stack) {
        return colorFromRegistry(
                Minecraft.getInstance().level,
                ModDataPackRegistries.DIRTY_GEM_TYPES,
                ModDataPackRegistries.dirtyGemTypeKey(DirtyGemItem.dirtyGemTypeId(stack)),
                DirtyGemType::color,
                DirtyGemType.DEFAULT_COLOR
        );
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
            return FastColor.ARGB32.opaque(defaultColor);
        }
        return lookupLevel.registryAccess()
                .lookup(registryKey)
                .flatMap(registry -> registry.get(typeKey))
                .map(reference -> FastColor.ARGB32.opaque(colorGetter.applyAsInt(reference.value())))
                .orElseGet(() -> FastColor.ARGB32.opaque(defaultColor));
    }

    private MaterialItemTintSources() {
    }
}
