package committee.nova.mods.skyresources3.common.compat.transfer.fluid;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public final class FluidResource {
    public static final FluidResource EMPTY = new FluidResource(FluidStack.EMPTY);

    private final FluidStack stack;

    private FluidResource(final FluidStack stack) {
        this.stack = stack.isEmpty() ? FluidStack.EMPTY : stack.copyWithAmount(1);
    }

    public static FluidResource of(final Fluid fluid) {
        return new FluidResource(new FluidStack(fluid, 1));
    }

    public static FluidResource of(final FluidStack stack) {
        return stack.isEmpty() ? EMPTY : new FluidResource(stack);
    }

    public boolean isEmpty() {
        return this.stack.isEmpty();
    }

    public Fluid getFluid() {
        return this.stack.getFluid();
    }

    public FluidStack toStack(final int amount) {
        return this.stack.isEmpty() ? FluidStack.EMPTY : this.stack.copyWithAmount(amount);
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof FluidResource other
                && FluidStack.isSameFluidSameComponents(this.stack, other.stack);
    }

    @Override
    public int hashCode() {
        return FluidStack.hashFluidAndComponents(this.stack);
    }
}
