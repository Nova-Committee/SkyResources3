package committee.nova.mods.skyresources3.integration.jade;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

record SkyResourcesProbeData(
        int heatRequirementState,
        int heatSourceValue,
        int currentHeat,
        int maxHeat,
        int heatPerTick,
        int multiblockState
) {
    static final int STATE_NONE = 0;
    static final int STATE_INVALID = 1;
    static final int STATE_VALID = 2;
    static final int STATE_VALID_TIER2 = 3;
    static final int STATE_VALID_TIER2_MISSING = 4;
    static final int VALUE_NONE = -1;
    static final StreamCodec<RegistryFriendlyByteBuf, SkyResourcesProbeData> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    SkyResourcesProbeData::heatRequirementState,
                    ByteBufCodecs.VAR_INT,
                    SkyResourcesProbeData::heatSourceValue,
                    ByteBufCodecs.VAR_INT,
                    SkyResourcesProbeData::currentHeat,
                    ByteBufCodecs.VAR_INT,
                    SkyResourcesProbeData::maxHeat,
                    ByteBufCodecs.VAR_INT,
                    SkyResourcesProbeData::heatPerTick,
                    ByteBufCodecs.VAR_INT,
                    SkyResourcesProbeData::multiblockState,
                    SkyResourcesProbeData::new
            );

    static SkyResourcesProbeData empty() {
        return new SkyResourcesProbeData(STATE_NONE, VALUE_NONE, VALUE_NONE, 0, 0, STATE_NONE);
    }

    SkyResourcesProbeData withHeatRequirement(final boolean valid) {
        return new SkyResourcesProbeData(
                state(valid),
                this.heatSourceValue,
                this.currentHeat,
                this.maxHeat,
                this.heatPerTick,
                this.multiblockState
        );
    }

    SkyResourcesProbeData withHeatSourceValue(final int value) {
        return new SkyResourcesProbeData(
                this.heatRequirementState,
                value,
                this.currentHeat,
                this.maxHeat,
                this.heatPerTick,
                this.multiblockState
        );
    }

    SkyResourcesProbeData withMachineHeat(final int current, final int max, final int perTick) {
        return new SkyResourcesProbeData(
                this.heatRequirementState,
                this.heatSourceValue,
                current,
                max,
                perTick,
                this.multiblockState
        );
    }

    SkyResourcesProbeData withMultiblock(final int state) {
        return new SkyResourcesProbeData(
                this.heatRequirementState,
                this.heatSourceValue,
                this.currentHeat,
                this.maxHeat,
                this.heatPerTick,
                state
        );
    }

    boolean hasAnyValue() {
        return this.heatRequirementState != STATE_NONE
                || this.heatSourceValue != VALUE_NONE
                || this.currentHeat != VALUE_NONE
                || this.multiblockState != STATE_NONE;
    }

    static int state(final boolean valid) {
        return valid ? STATE_VALID : STATE_INVALID;
    }
}
