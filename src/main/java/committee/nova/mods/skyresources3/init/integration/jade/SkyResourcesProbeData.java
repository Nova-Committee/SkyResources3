package committee.nova.mods.skyresources3.init.integration.jade;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.Nullable;

record SkyResourcesProbeData(
        @Nullable Component objectName,
        int heatRequirementState,
        int heatSourceValue,
        int currentHeat,
        int maxHeat,
        int heatPerTick,
        int multiblockState,
        int condenserProgress,
        int condenserMaxProgress,
        int condenserCatalystPercent,
        int condenserExpectedOutputValue
) {
    static final int STATE_NONE = 0;
    static final int STATE_INVALID = 1;
    static final int STATE_VALID = 2;
    static final int STATE_VALID_TIER2 = 3;
    static final int STATE_VALID_TIER2_MISSING = 4;
    static final int VALUE_NONE = -1;
    static final int EXPECTED_OUTPUT_SCALE = 100;
    static final StreamCodec<RegistryFriendlyByteBuf, SkyResourcesProbeData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SkyResourcesProbeData decode(final RegistryFriendlyByteBuf buffer) {
            final Component objectName = ByteBufCodecs.BOOL.decode(buffer)
                    ? ComponentSerialization.STREAM_CODEC.decode(buffer)
                    : null;
            return new SkyResourcesProbeData(
                    objectName,
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer)
            );
        }

        @Override
        public void encode(final RegistryFriendlyByteBuf buffer, final SkyResourcesProbeData value) {
            ByteBufCodecs.BOOL.encode(buffer, value.objectName != null);
            if (value.objectName != null) {
                ComponentSerialization.STREAM_CODEC.encode(buffer, value.objectName);
            }
            ByteBufCodecs.VAR_INT.encode(buffer, value.heatRequirementState);
            ByteBufCodecs.VAR_INT.encode(buffer, value.heatSourceValue);
            ByteBufCodecs.VAR_INT.encode(buffer, value.currentHeat);
            ByteBufCodecs.VAR_INT.encode(buffer, value.maxHeat);
            ByteBufCodecs.VAR_INT.encode(buffer, value.heatPerTick);
            ByteBufCodecs.VAR_INT.encode(buffer, value.multiblockState);
            ByteBufCodecs.VAR_INT.encode(buffer, value.condenserProgress);
            ByteBufCodecs.VAR_INT.encode(buffer, value.condenserMaxProgress);
            ByteBufCodecs.VAR_INT.encode(buffer, value.condenserCatalystPercent);
            ByteBufCodecs.VAR_INT.encode(buffer, value.condenserExpectedOutputValue);
        }
    };

    static SkyResourcesProbeData empty() {
        return new SkyResourcesProbeData(
                null,
                STATE_NONE,
                VALUE_NONE,
                VALUE_NONE,
                0,
                0,
                STATE_NONE,
                VALUE_NONE,
                0,
                VALUE_NONE,
                VALUE_NONE
        );
    }

    SkyResourcesProbeData withObjectName(final Component objectName) {
        return new SkyResourcesProbeData(
                objectName,
                this.heatRequirementState,
                this.heatSourceValue,
                this.currentHeat,
                this.maxHeat,
                this.heatPerTick,
                this.multiblockState,
                this.condenserProgress,
                this.condenserMaxProgress,
                this.condenserCatalystPercent,
                this.condenserExpectedOutputValue
        );
    }

    SkyResourcesProbeData withHeatRequirement(final boolean valid) {
        return new SkyResourcesProbeData(
                this.objectName,
                state(valid),
                this.heatSourceValue,
                this.currentHeat,
                this.maxHeat,
                this.heatPerTick,
                this.multiblockState,
                this.condenserProgress,
                this.condenserMaxProgress,
                this.condenserCatalystPercent,
                this.condenserExpectedOutputValue
        );
    }

    SkyResourcesProbeData withHeatSourceValue(final int value) {
        return new SkyResourcesProbeData(
                this.objectName,
                this.heatRequirementState,
                value,
                this.currentHeat,
                this.maxHeat,
                this.heatPerTick,
                this.multiblockState,
                this.condenserProgress,
                this.condenserMaxProgress,
                this.condenserCatalystPercent,
                this.condenserExpectedOutputValue
        );
    }

    SkyResourcesProbeData withMachineHeat(final int current, final int max, final int perTick) {
        return new SkyResourcesProbeData(
                this.objectName,
                this.heatRequirementState,
                this.heatSourceValue,
                current,
                max,
                perTick,
                this.multiblockState,
                this.condenserProgress,
                this.condenserMaxProgress,
                this.condenserCatalystPercent,
                this.condenserExpectedOutputValue
        );
    }

    SkyResourcesProbeData withMultiblock(final int state) {
        return new SkyResourcesProbeData(
                this.objectName,
                this.heatRequirementState,
                this.heatSourceValue,
                this.currentHeat,
                this.maxHeat,
                this.heatPerTick,
                state,
                this.condenserProgress,
                this.condenserMaxProgress,
                this.condenserCatalystPercent,
                this.condenserExpectedOutputValue
        );
    }

    SkyResourcesProbeData withCondenser(
            final int progress,
            final int maxProgress,
            final int catalystPercent,
            final int expectedOutputValue
    ) {
        return new SkyResourcesProbeData(
                this.objectName,
                this.heatRequirementState,
                this.heatSourceValue,
                this.currentHeat,
                this.maxHeat,
                this.heatPerTick,
                this.multiblockState,
                progress,
                maxProgress,
                catalystPercent,
                expectedOutputValue
        );
    }

    boolean hasAnyValue() {
        return this.objectName != null
                || this.heatRequirementState != STATE_NONE
                || this.heatSourceValue != VALUE_NONE
                || this.currentHeat != VALUE_NONE
                || this.multiblockState != STATE_NONE
                || this.condenserProgress != VALUE_NONE
                || this.condenserCatalystPercent != VALUE_NONE;
    }

    static int state(final boolean valid) {
        return valid ? STATE_VALID : STATE_INVALID;
    }
}
