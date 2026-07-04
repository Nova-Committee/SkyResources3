package committee.nova.mods.skyresources3.common.compat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;

public final class ValueInput {
    private final CompoundTag tag;
    private final HolderLookup.Provider registries;

    public ValueInput(final CompoundTag tag) {
        this(tag, null);
    }

    public ValueInput(final CompoundTag tag, final HolderLookup.Provider registries) {
        this.tag = tag;
        this.registries = registries;
    }

    public HolderLookup.Provider registries() {
        return this.registries;
    }

    public CompoundTag tag() {
        return this.tag;
    }

    public void readChild(final String key, final LegacyNbtSerializable target) {
        if (this.tag.contains(key)) {
            target.deserialize(new ValueInput(this.tag.getCompound(key), this.registries));
        }
    }

    public <T> Optional<T> read(final String key, final Codec<T> codec) {
        if (!this.tag.contains(key)) {
            return Optional.empty();
        }
        final Tag value = this.tag.get(key);
        if (value == null) {
            return Optional.empty();
        }
        return codec.parse(this.ops(), value).result();
    }

    private DynamicOps<Tag> ops() {
        return this.registries == null ? NbtOps.INSTANCE : RegistryOps.create(NbtOps.INSTANCE, this.registries);
    }

    public boolean getBooleanOr(final String key, final boolean fallback) {
        return this.tag.contains(key) ? this.tag.getBoolean(key) : fallback;
    }

    public double getDoubleOr(final String key, final double fallback) {
        return this.tag.contains(key) ? this.tag.getDouble(key) : fallback;
    }

    public float getFloatOr(final String key, final float fallback) {
        return this.tag.contains(key) ? this.tag.getFloat(key) : fallback;
    }

    public int getIntOr(final String key, final int fallback) {
        return this.tag.contains(key) ? this.tag.getInt(key) : fallback;
    }

    public String getStringOr(final String key, final String fallback) {
        return this.tag.contains(key) ? this.tag.getString(key) : fallback;
    }
}
