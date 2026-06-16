package committee.nova.mods.skyresources3.init.integration.jade;

import java.util.Locale;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.JadeIds;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

final class SkyResourcesProbeComponentProvider implements IBlockComponentProvider {
    static final SkyResourcesProbeComponentProvider INSTANCE = new SkyResourcesProbeComponentProvider();

    @Override
    public void appendTooltip(
            final ITooltip tooltip,
            final BlockAccessor accessor,
            final IPluginConfig config
    ) {
        final Optional<SkyResourcesProbeData> data = SkyResourcesProbeDataProvider.INSTANCE.decodeFromData(accessor);
        data.ifPresent(probeData -> appendData(tooltip, probeData));
    }

    @Override
    public Identifier getUid() {
        return SkyResourcesJadePlugin.PROBE_DATA;
    }

    private static void appendData(final ITooltip tooltip, final SkyResourcesProbeData data) {
        replaceObjectName(tooltip, data);
        appendHeatRequirement(tooltip, data.heatRequirementState());
        if (data.heatSourceValue() != SkyResourcesProbeData.VALUE_NONE) {
            tooltip.add(Component.translatable("jade.skyresources.heat_source_value", data.heatSourceValue())
                    .withStyle(ChatFormatting.YELLOW));
        }
        if (data.currentHeat() != SkyResourcesProbeData.VALUE_NONE) {
            tooltip.add(Component.translatable("jade.skyresources.machine_heat", data.currentHeat(), data.maxHeat())
                    .withStyle(ChatFormatting.GOLD));
            if (data.heatPerTick() > 0) {
                tooltip.add(Component.translatable("jade.skyresources.heat_per_tick", data.heatPerTick())
                        .withStyle(ChatFormatting.YELLOW));
            }
        }
        appendCondenser(tooltip, data);
        appendMultiblock(tooltip, data.multiblockState());
    }

    private static void replaceObjectName(final ITooltip tooltip, final SkyResourcesProbeData data) {
        final Component objectName = data.objectName();
        if (objectName != null) {
            tooltip.replace(JadeIds.CORE_OBJECT_NAME, IThemeHelper.get().title(objectName));
        }
    }

    private static void appendHeatRequirement(final ITooltip tooltip, final int state) {
        if (state == SkyResourcesProbeData.STATE_NONE) {
            return;
        }
        tooltip.add(Component.translatable(state == SkyResourcesProbeData.STATE_VALID
                        ? "jade.skyresources.heat_source.valid"
                        : "jade.skyresources.heat_source.invalid")
                .withStyle(state == SkyResourcesProbeData.STATE_VALID ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
    }

    private static void appendMultiblock(final ITooltip tooltip, final int state) {
        if (state == SkyResourcesProbeData.STATE_NONE) {
            return;
        }
        final boolean valid = state != SkyResourcesProbeData.STATE_INVALID;
        tooltip.add(Component.translatable(valid
                        ? "jade.skyresources.multiblock.valid"
                        : "jade.skyresources.multiblock.invalid")
                .withStyle(valid ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
        if (state == SkyResourcesProbeData.STATE_VALID_TIER2
                || state == SkyResourcesProbeData.STATE_VALID_TIER2_MISSING) {
            tooltip.add(Component.translatable(state == SkyResourcesProbeData.STATE_VALID_TIER2
                            ? "jade.skyresources.multiblock.tier2_valid"
                            : "jade.skyresources.multiblock.tier2_invalid")
                    .withStyle(state == SkyResourcesProbeData.STATE_VALID_TIER2
                            ? ChatFormatting.GREEN
                            : ChatFormatting.YELLOW));
        }
    }

    private static void appendCondenser(final ITooltip tooltip, final SkyResourcesProbeData data) {
        if (data.condenserProgress() == SkyResourcesProbeData.VALUE_NONE) {
            return;
        }
        if (data.condenserMaxProgress() > 0) {
            tooltip.add(Component.translatable(
                            "jade.skyresources.condenser_progress",
                            data.condenserProgress(),
                            data.condenserMaxProgress()
                    )
                    .withStyle(ChatFormatting.AQUA));
        } else {
            tooltip.add(Component.translatable("jade.skyresources.condenser_idle")
                    .withStyle(ChatFormatting.GRAY));
        }
        if (data.condenserCatalystPercent() != SkyResourcesProbeData.VALUE_NONE) {
            tooltip.add(Component.translatable(
                            "jade.skyresources.condenser_catalyst",
                            data.condenserCatalystPercent()
                    )
                    .withStyle(ChatFormatting.GREEN));
        }
        if (data.condenserExpectedOutputValue() > 0) {
            tooltip.add(Component.translatable(
                            "jade.skyresources.condenser_expected",
                            formatExpectedOutput(data.condenserExpectedOutputValue())
                    )
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }

    private static String formatExpectedOutput(final int scaledValue) {
        final float value = scaledValue / (float) SkyResourcesProbeData.EXPECTED_OUTPUT_SCALE;
        final String suffix = scaledValue == Integer.MAX_VALUE ? "+" : "";
        if (value >= 100.0F) {
            return String.format(Locale.ROOT, "%.0f%s", value, suffix);
        }
        if (value >= 10.0F) {
            return String.format(Locale.ROOT, "%.1f%s", value, suffix);
        }
        return String.format(Locale.ROOT, "%.2f%s", value, suffix);
    }

    private SkyResourcesProbeComponentProvider() {
    }
}
