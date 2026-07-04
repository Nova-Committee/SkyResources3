package committee.nova.mods.skyresources3.common.compat.transfer.fluid;

import java.util.Objects;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public final class FluidResource {
    public static final FluidResource EMPTY = new FluidResource(FluidStack.EMPTY);

    private final FluidStack stack;

    private FluidResource(final FluidStack stack) {
        this.stack = stack.isEmpty() ? FluidStack.EMPTY : new FluidStack(stack, 1);
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
        return this.stack.isEmpty() ? FluidStack.EMPTY : new FluidStack(this.stack, amount);
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof FluidResource other
                && this.stack.isFluidStackIdentical(other.stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.stack.getFluid(), this.stack.getTag());
    }
}
