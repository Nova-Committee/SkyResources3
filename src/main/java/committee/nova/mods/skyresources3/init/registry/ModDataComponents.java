package committee.nova.mods.skyresources3.init.registry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class ModDataComponents {
    public static final String CASING_TYPE = "casing_type";
    public static final String COMBUSTION_HEATER_TYPE = "combustion_heater_type";
    public static final String HEAT_PROVIDER_TYPE = "heat_provider_type";
    public static final String CONDENSER_TYPE = "condenser_type";
    public static final String MACHINE_TYPE = "machine_type";
    public static final String ORE_ALCHEMY_DUST_TYPE = "ore_alchemy_dust_type";
    public static final String DIRTY_GEM_TYPE = "dirty_gem_type";
    public static final String WATER_EXTRACTOR_WATER = "water";

    public static void setResource(final ItemStack stack, final String key, final ResourceLocation value) {
        stack.getOrCreateTag().putString(key, value.toString());
    }

    public static ResourceLocation getResource(
            final ItemStack stack,
            final String key,
            final ResourceLocation fallback
    ) {
        final CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(key)) {
            return fallback;
        }
        final ResourceLocation parsed = ResourceLocation.tryParse(tag.getString(key));
        return parsed == null ? fallback : parsed;
    }

    public static void setBlockEntityResource(final ItemStack stack, final String key, final ResourceLocation value) {
        stack.getOrCreateTagElement("BlockEntityTag").putString(key, value.toString());
    }

    public static void setInt(final ItemStack stack, final String key, final int value) {
        stack.getOrCreateTag().putInt(key, value);
    }

    public static int getInt(final ItemStack stack, final String key, final int fallback) {
        final CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(key) ? tag.getInt(key) : fallback;
    }

    private ModDataComponents() {
    }
}
