package committee.nova.mods.skyresources3.common.compat.transfer.fluid;

import com.mojang.serialization.Codec;
import committee.nova.mods.skyresources3.common.compat.LegacyNbtSerializable;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.transaction.TransactionContext;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidStacksResourceHandler implements ResourceHandler<FluidResource>, LegacyNbtSerializable {
    private static final Codec<List<FluidStack>> STACKS_CODEC = FluidStack.CODEC.listOf();

    protected NonNullList<FluidStack> stacks;
    private final int capacity;

    public FluidStacksResourceHandler(final int size, final int capacity) {
        this.stacks = NonNullList.withSize(size, FluidStack.EMPTY);
        this.capacity = capacity;
    }

    public int size() {
        return this.stacks.size();
    }

    public void setStacks(final NonNullList<FluidStack> stacks) {
        this.stacks = stacks;
    }

    public FluidResource getResource(final int index) {
        return FluidResource.of(this.stacks.get(index));
    }

    public int getAmountAsInt(final int index) {
        return this.stacks.get(index).getAmount();
    }

    public void set(final int index, final FluidResource resource, final int amount) {
        final FluidStack previous = this.stacks.get(index).copy();
        this.stacks.set(index, resource.isEmpty() || amount <= 0 ? FluidStack.EMPTY : resource.toStack(amount));
        this.onContentsChanged(index, previous);
    }

    public boolean isValid(final int index, final FluidResource resource) {
        return !resource.isEmpty();
    }

    protected int getCapacity(final int index, final FluidResource resource) {
        return this.capacity;
    }

    public int insert(
            final int index,
            final FluidResource resource,
            final int amount,
            final TransactionContext transaction
    ) {
        if (!this.isValid(index, resource) || amount <= 0) {
            return 0;
        }
        final FluidStack current = this.stacks.get(index);
        if (!current.isEmpty() && !FluidResource.of(current).equals(resource)) {
            return 0;
        }
        final int inserted = Math.min(amount, Math.max(0, this.getCapacity(index, resource) - current.getAmount()));
        if (inserted > 0 && (transaction == null || !transaction.isSimulation())) {
            this.set(index, resource, current.getAmount() + inserted);
        }
        return inserted;
    }

    public int extract(
            final int index,
            final FluidResource resource,
            final int amount,
            final TransactionContext transaction
    ) {
        if (resource.isEmpty() || amount <= 0) {
            return 0;
        }
        final FluidStack current = this.stacks.get(index);
        if (current.isEmpty() || !FluidResource.of(current).equals(resource)) {
            return 0;
        }
        final int extracted = Math.min(amount, current.getAmount());
        if (extracted > 0 && (transaction == null || !transaction.isSimulation())) {
            this.set(index, resource, current.getAmount() - extracted);
        }
        return extracted;
    }

    @Override
    public int getTanks() {
        return this.stacks.size();
    }

    @Override
    public FluidStack getFluidInTank(final int tank) {
        return this.stacks.get(tank).copy();
    }

    @Override
    public int getTankCapacity(final int tank) {
        return this.getCapacity(tank, this.getResource(tank));
    }

    @Override
    public boolean isFluidValid(final int tank, final FluidStack stack) {
        return this.isValid(tank, FluidResource.of(stack));
    }

    @Override
    public int fill(final FluidStack resource, final FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }
        int remaining = resource.getAmount();
        final FluidResource fluid = FluidResource.of(resource);
        for (int tank = 0; tank < this.stacks.size() && remaining > 0; tank++) {
            remaining -= this.insert(tank, fluid, remaining, action == FluidAction.SIMULATE ? () -> true : null);
        }
        return resource.getAmount() - remaining;
    }

    @Override
    public FluidStack drain(final FluidStack resource, final FluidAction action) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }
        final int drained = this.extract(0, FluidResource.of(resource), resource.getAmount(),
                action == FluidAction.SIMULATE ? () -> true : null);
        return drained <= 0 ? FluidStack.EMPTY : new FluidStack(resource, drained);
    }

    @Override
    public FluidStack drain(final int maxDrain, final FluidAction action) {
        for (int tank = 0; tank < this.stacks.size(); tank++) {
            final FluidStack current = this.stacks.get(tank);
            if (!current.isEmpty()) {
                final int drained = this.extract(tank, FluidResource.of(current), maxDrain,
                        action == FluidAction.SIMULATE ? () -> true : null);
                return drained <= 0 ? FluidStack.EMPTY : new FluidStack(current, drained);
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public void deserialize(final ValueInput input) {
        final List<FluidStack> loaded = input.read("value", STACKS_CODEC).orElse(List.of());
        this.stacks = NonNullList.withSize(loaded.size(), FluidStack.EMPTY);
        for (int index = 0; index < loaded.size(); index++) {
            this.stacks.set(index, loaded.get(index).copy());
        }
    }

    @Override
    public CompoundTag serialize(final HolderLookup.Provider registries) {
        final CompoundTag tag = new CompoundTag();
        new committee.nova.mods.skyresources3.common.compat.ValueOutput(tag, registries)
                .store("value", STACKS_CODEC, this.stacks.stream().map(FluidStack::copy).toList());
        return tag;
    }

    protected void onContentsChanged(final int index, final FluidStack previousContents) {
    }
}
