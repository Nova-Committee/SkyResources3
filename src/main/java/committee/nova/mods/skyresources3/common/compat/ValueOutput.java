package committee.nova.mods.skyresources3.common.compat;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;

public final class ValueOutput {
    private final CompoundTag tag;
    private final HolderLookup.Provider registries;

    public ValueOutput(final CompoundTag tag, final HolderLookup.Provider registries) {
        this.tag = tag;
        this.registries = registries;
    }

    public CompoundTag tag() {
        return this.tag;
    }

    public void putChild(final String key, final LegacyNbtSerializable source) {
        this.tag.put(key, source.serialize(this.registries));
    }

    public void putBoolean(final String key, final boolean value) {
        this.tag.putBoolean(key, value);
    }

    public void putDouble(final String key, final double value) {
        this.tag.putDouble(key, value);
    }

    public void putFloat(final String key, final float value) {
        this.tag.putFloat(key, value);
    }

    public void putInt(final String key, final int value) {
        this.tag.putInt(key, value);
    }

    public void putString(final String key, final String value) {
        this.tag.putString(key, value);
    }

    public <T> void store(final String key, final Codec<T> codec, final T value) {
        codec.encodeStart(RegistryOps.create(NbtOps.INSTANCE, this.registries), value)
                .result()
                .ifPresent(tagValue -> this.putTag(key, tagValue));
    }

    private void putTag(final String key, final Tag tagValue) {
        this.tag.put(key, tagValue);
    }
}
