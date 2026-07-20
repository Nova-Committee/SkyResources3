package committee.nova.mods.skyresources3.common.compat.transfer.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;

public final class FluidUtil {
    public static boolean interactWithFluidHandler(
            final Player player,
            final InteractionHand hand,
            final Level level,
            final BlockPos pos,
            final Direction direction
    ) {
        return net.minecraftforge.fluids.FluidUtil.interactWithFluidHandler(player, hand, level, pos, direction);
    }

    public static boolean tryPlaceFluid(
            final IFluidHandler source,
            final Player player,
            final Level level,
            final InteractionHand hand,
            final BlockPos pos
    ) {
        final FluidStack stack = source.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
        if (stack.getAmount() < FluidType.BUCKET_VOLUME) {
            return false;
        }
        stack.setAmount(FluidType.BUCKET_VOLUME);
        return net.minecraftforge.fluids.FluidUtil.tryPlaceFluid(
                player,
                level,
                hand,
                pos,
                source,
                stack
        );
    }

    private FluidUtil() {
    }
}
