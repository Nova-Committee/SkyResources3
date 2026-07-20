package committee.nova.mods.skyresources3.common.compat.transfer.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public final class FluidUtil {
    public static boolean interactWithFluidHandler(
            final Player player,
            final InteractionHand hand,
            final Level level,
            final BlockPos pos,
            final Direction direction
    ) {
        return net.neoforged.neoforge.fluids.FluidUtil.interactWithFluidHandler(player, hand, level, pos, direction);
    }

    public static boolean tryPlaceFluid(
            final FluidResource resource,
            final Player player,
            final Level level,
            final InteractionHand hand,
            final BlockPos pos
    ) {
        return net.neoforged.neoforge.fluids.FluidUtil.tryPlaceFluid(
                player,
                level,
                hand,
                pos,
                ItemStack.EMPTY,
                resource.toStack(net.neoforged.neoforge.fluids.FluidType.BUCKET_VOLUME)
        ).isSuccess();
    }

    public static FluidStack tryPlaceFluid(
            final IFluidHandler source,
            final Player player,
            final Level level,
            final InteractionHand hand,
            final BlockPos pos
    ) {
        if (source == null) {
            return FluidStack.EMPTY;
        }

        for (int tank = 0; tank < source.getTanks(); tank++) {
            final FluidStack stored = source.getFluidInTank(tank);
            if (stored.isEmpty() || stored.getAmount() < FluidType.BUCKET_VOLUME) {
                continue;
            }

            final FluidStack bucket = stored.copyWithAmount(FluidType.BUCKET_VOLUME);
            if (net.neoforged.neoforge.fluids.FluidUtil.tryPlaceFluid(
                    player,
                    level,
                    hand,
                    pos,
                    source,
                    bucket
            )) {
                return bucket;
            }
        }
        return FluidStack.EMPTY;
    }

    private FluidUtil() {
    }
}
