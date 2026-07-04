package committee.nova.mods.skyresources3.common.compat.transfer.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
            final FluidResource resource,
            final Player player,
            final Level level,
            final InteractionHand hand,
            final BlockPos pos
    ) {
        return net.minecraftforge.fluids.FluidUtil.tryPlaceFluid(
                player,
                level,
                hand,
                pos,
                ItemStack.EMPTY,
                resource.toStack(net.minecraftforge.fluids.FluidType.BUCKET_VOLUME)
        ).isSuccess();
    }

    private FluidUtil() {
    }
}
