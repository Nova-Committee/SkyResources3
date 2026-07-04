package committee.nova.mods.skyresources3.init.registry;

import committee.nova.mods.skyresources3.common.item.WaterExtractorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public final class ModCapabilities {
    public static void register(final RegisterCapabilitiesEvent event) {
    }

    private static final class WaterExtractorFluidHandler extends FluidHandlerItemStack {
        private WaterExtractorFluidHandler(final ItemStack stack, final int capacity) {
            super(stack, Math.max(0, capacity));
        }

        @Override
        public boolean canFillFluidType(final FluidStack fluid) {
            return fluid.isEmpty() || fluid.getFluid() == Fluids.WATER;
        }
    }

    private ModCapabilities() {
    }
}
