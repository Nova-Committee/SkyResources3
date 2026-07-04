package committee.nova.mods.skyresources3.init.integration.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

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

    void write(final CompoundTag tag) {
        if (this.objectName != null) {
            tag.putString("object_name", Component.Serializer.toJson(this.objectName));
        }
        tag.putInt("heat_requirement_state", this.heatRequirementState);
        tag.putInt("heat_source_value", this.heatSourceValue);
        tag.putInt("current_heat", this.currentHeat);
        tag.putInt("max_heat", this.maxHeat);
        tag.putInt("heat_per_tick", this.heatPerTick);
        tag.putInt("multiblock_state", this.multiblockState);
        tag.putInt("condenser_progress", this.condenserProgress);
        tag.putInt("condenser_max_progress", this.condenserMaxProgress);
        tag.putInt("condenser_catalyst_percent", this.condenserCatalystPercent);
        tag.putInt("condenser_expected_output_value", this.condenserExpectedOutputValue);
    }

    static SkyResourcesProbeData read(final CompoundTag tag) {
        final Component objectName = tag.contains("object_name")
                ? Component.Serializer.fromJson(tag.getString("object_name"))
                : null;
        return new SkyResourcesProbeData(
                objectName,
                tag.getInt("heat_requirement_state"),
                tag.getInt("heat_source_value"),
                tag.getInt("current_heat"),
                tag.getInt("max_heat"),
                tag.getInt("heat_per_tick"),
                tag.getInt("multiblock_state"),
                tag.getInt("condenser_progress"),
                tag.getInt("condenser_max_progress"),
                tag.getInt("condenser_catalyst_percent"),
                tag.getInt("condenser_expected_output_value")
        );
    }
}
