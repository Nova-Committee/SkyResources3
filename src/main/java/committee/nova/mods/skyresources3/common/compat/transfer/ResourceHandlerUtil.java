package committee.nova.mods.skyresources3.common.compat.transfer;

import committee.nova.mods.skyresources3.common.compat.transfer.fluid.FluidResource;
import java.util.function.Predicate;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public final class ResourceHandlerUtil {
    public static void moveFirst(
            final IFluidHandler from,
            final IFluidHandler to,
            final Predicate<FluidResource> filter,
            final int maxAmount,
            final Object ignored
    ) {
        if (maxAmount <= 0) {
            return;
        }
        for (int tank = 0; tank < from.getTanks(); tank++) {
            final FluidStack candidate = from.drain(maxAmount, FluidAction.SIMULATE);
            if (candidate.isEmpty() || !filter.test(FluidResource.of(candidate))) {
                continue;
            }
            final int accepted = to.fill(candidate, FluidAction.SIMULATE);
            if (accepted <= 0) {
                continue;
            }
            final FluidStack drained = from.drain(new FluidStack(candidate, accepted), FluidAction.EXECUTE);
            if (!drained.isEmpty()) {
                to.fill(drained, FluidAction.EXECUTE);
            }
            return;
        }
    }

    private ResourceHandlerUtil() {
    }
}
