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
            final FluidResource resource,
            final Player player,
            final Level level,
            final InteractionHand hand,
            final BlockPos pos
    ) {
        final FluidStack stack = resource.toStack(FluidType.BUCKET_VOLUME);
        if (stack.isEmpty()) {
            return false;
        }
        return net.minecraftforge.fluids.FluidUtil.tryPlaceFluid(
                player,
                level,
                hand,
                pos,
                new SingleUseFluidHandler(stack.copy()),
                stack
        );
    }

    private FluidUtil() {
    }

    private static final class SingleUseFluidHandler implements IFluidHandler {
        private FluidStack fluid;

        private SingleUseFluidHandler(final FluidStack fluid) {
            this.fluid = fluid;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(final int tank) {
            return tank == 0 ? this.fluid.copy() : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(final int tank) {
            return tank == 0 ? this.fluid.getAmount() : 0;
        }

        @Override
        public boolean isFluidValid(final int tank, final FluidStack stack) {
            return tank == 0 && !stack.isEmpty() && (this.fluid.isEmpty() || stack.isFluidEqual(this.fluid));
        }

        @Override
        public int fill(final FluidStack resource, final FluidAction action) {
            return 0;
        }

        @Override
        public FluidStack drain(final FluidStack resource, final FluidAction action) {
            if (resource.isEmpty() || this.fluid.isEmpty() || !resource.isFluidEqual(this.fluid)) {
                return FluidStack.EMPTY;
            }
            return this.drain(resource.getAmount(), action);
        }

        @Override
        public FluidStack drain(final int maxDrain, final FluidAction action) {
            if (maxDrain <= 0 || this.fluid.isEmpty()) {
                return FluidStack.EMPTY;
            }
            final int drainedAmount = Math.min(maxDrain, this.fluid.getAmount());
            final FluidStack drained = new FluidStack(this.fluid, drainedAmount);
            if (action.execute()) {
                this.fluid.shrink(drainedAmount);
                if (this.fluid.getAmount() <= 0) {
                    this.fluid = FluidStack.EMPTY;
                }
            }
            return drained;
        }
    }
}
