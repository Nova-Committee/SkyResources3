package committee.nova.mods.skyresources3.common.compat.transfer.energy;

import net.minecraftforge.energy.IEnergyStorage;

public interface EnergyHandler extends IEnergyStorage {
    int getAmountAsInt();

    int getCapacityAsInt();

    void set(final int amount);
}
