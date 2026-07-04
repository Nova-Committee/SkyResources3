package committee.nova.mods.skyresources3.init.integration.jade;

import committee.nova.mods.skyresources3.common.block.FreezerBlock;
import committee.nova.mods.skyresources3.common.block.entity.CrucibleBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.EndPortalCoreBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.FreezerBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.LifeInfuserBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.MachineCasingBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.StandaloneMachineBlockEntity;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import committee.nova.mods.skyresources3.util.HeatSources;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

final class SkyResourcesProbeDataProvider implements IServerDataProvider<BlockAccessor> {
    static final SkyResourcesProbeDataProvider INSTANCE = new SkyResourcesProbeDataProvider();

    @Override
    public void appendServerData(final CompoundTag tag, final BlockAccessor accessor) {
        final SkyResourcesProbeData data = probeData(accessor);
        if (data.hasAnyValue()) {
            data.write(tag);
        }
    }

    static SkyResourcesProbeData probeDataForTest(final BlockAccessor accessor) {
        return probeData(accessor);
    }

    private static SkyResourcesProbeData probeData(final BlockAccessor accessor) {
        final Level level = accessor.getLevel();
        final BlockPos pos = accessor.getPosition();
        final BlockState state = accessor.getBlockState();
        final BlockEntity blockEntity = accessor.getBlockEntity();

        SkyResourcesProbeData data = SkyResourcesProbeData.empty();
        if (requiresHeatBelow(state, blockEntity)) {
            data = data.withHeatRequirement(HeatSources.getHeatSourceValue(level, pos.below()) > 0);
        }

        final boolean heatProviderCasing = blockEntity instanceof MachineCasingBlockEntity casing
                && casing.installedMachineMode() == MachineCasingBlockEntity.MACHINE_MODE_HEAT_PROVIDER;
        final int heatValue = HeatSources.getHeatSourceValue(level, pos);
        if (heatValue > 0 && !heatProviderCasing) {
            data = data.withHeatSourceValue(heatValue);
        }

        if (blockEntity instanceof MachineCasingBlockEntity casing) {
            data = data.withObjectName(casing.menuTitle());
            if (casing.usesCombustionChamber()) {
                data = data.withMachineHeat(casing.currentHeat(), casing.maxHeat(), casing.heatPerTick())
                        .withMultiblock(SkyResourcesProbeData.state(casing.hasValidMultiblock(level)));
            } else if (casing.installedMachineMode() == MachineCasingBlockEntity.MACHINE_MODE_HEAT_PROVIDER) {
                data = data.withMachineHeat(casing.currentHeat(), casing.maxHeat(), casing.heatPerTick());
            } else if (casing.installedMachineMode() == MachineCasingBlockEntity.MACHINE_MODE_CONDENSER) {
                data = data.withCondenser(
                        casing.condenserProgress(),
                        casing.condenserMaxProgress(),
                        scale(casing.condenserCatalystLeft(), 100),
                        scale(casing.condenserExpectedOutputValue(), SkyResourcesProbeData.EXPECTED_OUTPUT_SCALE)
                );
            }
        } else if (blockEntity instanceof StandaloneMachineBlockEntity machine) {
            data = data.withObjectName(machine.displayName());
        } else if (blockEntity instanceof LifeInfuserBlockEntity lifeInfuser && level instanceof ServerLevel serverLevel) {
            data = data.withMultiblock(SkyResourcesProbeData.state(lifeInfuser.hasValidMultiblock(serverLevel)));
        } else if (blockEntity instanceof EndPortalCoreBlockEntity endPortalCore) {
            final boolean valid = endPortalCore.hasValidMultiblock();
            final int multiblockState = endPortalState(endPortalCore, valid);
            data = data.withMultiblock(multiblockState);
        } else {
            data = data.withMultiblock(freezerState(level, state, pos));
        }

        return data;
    }

    @Override
    public ResourceLocation getUid() {
        return SkyResourcesJadePlugin.PROBE_DATA;
    }

    private static boolean requiresHeatBelow(final BlockState state, final @Nullable BlockEntity blockEntity) {
        return blockEntity instanceof CrucibleBlockEntity || state.is(ModBlocks.BLAZE_POWDER_BLOCK.get());
    }

    private static int freezerState(final Level level, final BlockState state, final BlockPos pos) {
        if (!(state.getBlock() instanceof FreezerBlock freezerBlock) || !freezerBlock.tier().requiresMultiblock()) {
            return SkyResourcesProbeData.STATE_NONE;
        }
        final BlockPos controllerPos = state.getValue(FreezerBlock.PART) == FreezerBlock.FreezerPart.TOP
                ? pos.below()
                : pos;
        if (level.getBlockEntity(controllerPos) instanceof FreezerBlockEntity freezer) {
            return SkyResourcesProbeData.state(freezer.hasValidMultiblock());
        }
        return SkyResourcesProbeData.STATE_INVALID;
    }

    private static int endPortalState(final EndPortalCoreBlockEntity endPortalCore, final boolean valid) {
        if (!valid) {
            return SkyResourcesProbeData.STATE_INVALID;
        }
        return endPortalCore.hasValidMultiblockTier2()
                ? SkyResourcesProbeData.STATE_VALID_TIER2
                : SkyResourcesProbeData.STATE_VALID_TIER2_MISSING;
    }

    private static int scale(final double value, final int scale) {
        if (value <= 0.0D) {
            return 0;
        }
        final double scaled = value * scale;
        if (scaled >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) Math.round(scaled);
    }

    private SkyResourcesProbeDataProvider() {
    }
}
