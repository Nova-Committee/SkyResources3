package committee.nova.mods.skyresources3.integration.jade;

import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

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
        appendHeatRequirement(tooltip, data.heatRequirementState());
        if (data.heatSourceValue() != SkyResourcesProbeData.VALUE_NONE) {
            tooltip.add(Component.translatable("jade.skyresources3.heat_source_value", data.heatSourceValue())
                    .withStyle(ChatFormatting.YELLOW));
        }
        if (data.currentHeat() != SkyResourcesProbeData.VALUE_NONE) {
            tooltip.add(Component.translatable("jade.skyresources3.machine_heat", data.currentHeat(), data.maxHeat())
                    .withStyle(ChatFormatting.GOLD));
            if (data.heatPerTick() > 0) {
                tooltip.add(Component.translatable("jade.skyresources3.heat_per_tick", data.heatPerTick())
                        .withStyle(ChatFormatting.YELLOW));
            }
        }
        appendMultiblock(tooltip, data.multiblockState());
    }

    private static void appendHeatRequirement(final ITooltip tooltip, final int state) {
        if (state == SkyResourcesProbeData.STATE_NONE) {
            return;
        }
        tooltip.add(Component.translatable(state == SkyResourcesProbeData.STATE_VALID
                        ? "jade.skyresources3.heat_source.valid"
                        : "jade.skyresources3.heat_source.invalid")
                .withStyle(state == SkyResourcesProbeData.STATE_VALID ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
    }

    private static void appendMultiblock(final ITooltip tooltip, final int state) {
        if (state == SkyResourcesProbeData.STATE_NONE) {
            return;
        }
        final boolean valid = state != SkyResourcesProbeData.STATE_INVALID;
        tooltip.add(Component.translatable(valid
                        ? "jade.skyresources3.multiblock.valid"
                        : "jade.skyresources3.multiblock.invalid")
                .withStyle(valid ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
        if (state == SkyResourcesProbeData.STATE_VALID_TIER2
                || state == SkyResourcesProbeData.STATE_VALID_TIER2_MISSING) {
            tooltip.add(Component.translatable(state == SkyResourcesProbeData.STATE_VALID_TIER2
                            ? "jade.skyresources3.multiblock.tier2_valid"
                            : "jade.skyresources3.multiblock.tier2_invalid")
                    .withStyle(state == SkyResourcesProbeData.STATE_VALID_TIER2
                            ? ChatFormatting.GREEN
                            : ChatFormatting.YELLOW));
        }
    }

    private SkyResourcesProbeComponentProvider() {
    }
}
