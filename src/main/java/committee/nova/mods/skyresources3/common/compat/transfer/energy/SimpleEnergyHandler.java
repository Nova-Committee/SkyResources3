package committee.nova.mods.skyresources3.common.compat.transfer.energy;

import committee.nova.mods.skyresources3.common.compat.LegacyNbtSerializable;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.EnergyStorage;

public class SimpleEnergyHandler extends EnergyStorage implements EnergyHandler, LegacyNbtSerializable {
    private static final String ENERGY_KEY = "energy";

    public SimpleEnergyHandler(final int capacity, final int maxReceive, final int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    @Override
    public int receiveEnergy(final int maxReceive, final boolean simulate) {
        final int previous = this.energy;
        final int received = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && previous != this.energy) {
            this.onEnergyChanged(previous);
        }
        return received;
    }

    @Override
    public int extractEnergy(final int maxExtract, final boolean simulate) {
        final int previous = this.energy;
        final int extracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate && previous != this.energy) {
            this.onEnergyChanged(previous);
        }
        return extracted;
    }

    @Override
    public int getAmountAsInt() {
        return this.energy;
    }

    @Override
    public int getCapacityAsInt() {
        return this.capacity;
    }

    @Override
    public void set(final int amount) {
        final int previous = this.energy;
        this.energy = Math.max(0, Math.min(this.capacity, amount));
        if (previous != this.energy) {
            this.onEnergyChanged(previous);
        }
    }

    @Override
    public void deserialize(final ValueInput input) {
        this.set(input.getIntOr(ENERGY_KEY, 0));
    }

    @Override
    public CompoundTag serialize(final HolderLookup.Provider registries) {
        final CompoundTag tag = new CompoundTag();
        tag.putInt(ENERGY_KEY, this.energy);
        return tag;
    }

    protected void onEnergyChanged(final int previousAmount) {
    }
}
