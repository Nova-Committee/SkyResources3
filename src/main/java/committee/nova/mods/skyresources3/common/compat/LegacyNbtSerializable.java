package committee.nova.mods.skyresources3.common.compat;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface LegacyNbtSerializable {
    void deserialize(final ValueInput input);

    CompoundTag serialize(final HolderLookup.Provider registries);
}
