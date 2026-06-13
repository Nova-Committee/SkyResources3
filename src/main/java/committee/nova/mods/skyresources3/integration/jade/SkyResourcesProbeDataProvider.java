package committee.nova.mods.skyresources3.integration.jade;

import committee.nova.mods.skyresources3.block.FreezerBlock;
import committee.nova.mods.skyresources3.block.entity.CrucibleBlockEntity;
import committee.nova.mods.skyresources3.block.entity.EndPortalCoreBlockEntity;
import committee.nova.mods.skyresources3.block.entity.FreezerBlockEntity;
import committee.nova.mods.skyresources3.block.entity.LifeInfuserBlockEntity;
import committee.nova.mods.skyresources3.block.entity.MachineCasingBlockEntity;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.util.HeatSources;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.StreamServerDataProvider;

final class SkyResourcesProbeDataProvider implements StreamServerDataProvider<BlockAccessor, SkyResourcesProbeData> {
    static final SkyResourcesProbeDataProvider INSTANCE = new SkyResourcesProbeDataProvider();

    @Override
    public @Nullable SkyResourcesProbeData streamData(final BlockAccessor accessor) {
        final Level level = accessor.getLevel();
        final BlockPos pos = accessor.getPosition();
        final BlockState state = accessor.getBlockState();
        final BlockEntity blockEntity = accessor.getBlockEntity();

        SkyResourcesProbeData data = SkyResourcesProbeData.empty();
        if (requiresHeatBelow(state, blockEntity)) {
            data = data.withHeatRequirement(HeatSources.getHeatSourceValue(level, pos.below()) > 0);
        }

        final int heatValue = HeatSources.getHeatSourceValue(level, pos);
        if (heatValue > 0) {
            data = data.withHeatSourceValue(heatValue);
        }

        if (blockEntity instanceof MachineCasingBlockEntity casing) {
            if (casing.usesCombustionChamber()) {
                data = data.withMachineHeat(casing.currentHeat(), casing.maxHeat(), casing.heatPerTick())
                        .withMultiblock(SkyResourcesProbeData.state(casing.hasValidMultiblock(level)));
            }
        } else if (blockEntity instanceof LifeInfuserBlockEntity lifeInfuser && level instanceof ServerLevel serverLevel) {
            data = data.withMultiblock(SkyResourcesProbeData.state(lifeInfuser.hasValidMultiblock(serverLevel)));
        } else if (blockEntity instanceof EndPortalCoreBlockEntity endPortalCore) {
            final boolean valid = endPortalCore.hasValidMultiblock();
            final int multiblockState = endPortalState(endPortalCore, valid);
            data = data.withMultiblock(multiblockState);
        } else {
            data = data.withMultiblock(freezerState(level, state, pos));
        }

        return data.hasAnyValue() ? data : null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SkyResourcesProbeData> streamCodec() {
        return SkyResourcesProbeData.STREAM_CODEC;
    }

    @Override
    public Identifier getUid() {
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

    private SkyResourcesProbeDataProvider() {
    }
}
