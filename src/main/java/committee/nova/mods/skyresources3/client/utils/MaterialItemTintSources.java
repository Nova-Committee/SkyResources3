package committee.nova.mods.skyresources3.client.utils;

import committee.nova.mods.skyresources3.common.item.DirtyGemItem;
import committee.nova.mods.skyresources3.common.item.DirtyGemType;
import committee.nova.mods.skyresources3.common.item.OreAlchemyDustItem;
import committee.nova.mods.skyresources3.common.item.OreAlchemyDustType;
import committee.nova.mods.skyresources3.init.data.SkyResources3MaterialSeeds;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import java.util.function.ToIntFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;

public final class MaterialItemTintSources {
    public static int oreAlchemyDustColor(final ItemStack stack) {
        final ResourceLocation typeId = OreAlchemyDustItem.oreAlchemyDustTypeId(stack);
        return colorFromRegistry(
                Minecraft.getInstance().level,
                ModDataPackRegistries.ORE_ALCHEMY_DUST_TYPES,
                ModDataPackRegistries.oreAlchemyDustTypeKey(typeId),
                OreAlchemyDustType::color,
                SkyResources3MaterialSeeds.oreAlchemyDustColor(typeId)
        );
    }

    public static int dirtyGemColor(final ItemStack stack) {
        final ResourceLocation typeId = DirtyGemItem.dirtyGemTypeId(stack);
        return colorFromRegistry(
                Minecraft.getInstance().level,
                ModDataPackRegistries.DIRTY_GEM_TYPES,
                ModDataPackRegistries.dirtyGemTypeKey(typeId),
                DirtyGemType::color,
                SkyResources3MaterialSeeds.dirtyGemColor(typeId)
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
            return opaque(defaultColor);
        }
        return lookupLevel.registryAccess()
                .lookup(registryKey)
                .flatMap(registry -> registry.get(typeKey))
                .map(reference -> opaque(colorGetter.applyAsInt(reference.value())))
                .orElseGet(() -> opaque(defaultColor));
    }

    private static int opaque(final int color) {
        return FastColor.ARGB32.color(
                255,
                FastColor.ARGB32.red(color),
                FastColor.ARGB32.green(color),
                FastColor.ARGB32.blue(color)
        );
    }

    private MaterialItemTintSources() {
    }
}
