package committee.nova.mods.skyresources3.init.registry;

import committee.nova.mods.skyresources3.common.item.WaterExtractorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

public final class ModCapabilities {
    public static void register(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.FUSION_TABLE.get(),
                (fusionTable, direction) -> fusionTable.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.LIFE_INJECTOR.get(),
                (lifeInjector, direction) -> lifeInjector.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.LIFE_INFUSER.get(),
                (lifeInfuser, direction) -> lifeInfuser.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.DIRT_FURNACE.get(),
                (dirtFurnace, direction) -> dirtFurnace.getItemHandler(direction)
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.FREEZER.get(),
                (freezer, direction) -> freezer.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.QUICK_DROPPER.get(),
                (quickDropper, direction) -> quickDropper.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.DARK_MATTER_WARPER.get(),
                (darkMatterWarper, direction) -> darkMatterWarper.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.END_PORTAL_CORE.get(),
                (endPortalCore, direction) -> endPortalCore.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.CRUCIBLE_INSERTER.get(),
                (crucibleInserter, direction) -> crucibleInserter.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.ROCK_CRUSHER.get(),
                (rockCrusher, direction) -> rockCrusher.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.ROCK_CLEANER.get(),
                (rockCleaner, direction) -> rockCleaner.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.AQUEOUS_MACHINE.get(),
                (aqueousMachine, direction) -> aqueousMachine.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.WILDLIFE_ATTRACTOR.get(),
                (wildlifeAttractor, direction) -> wildlifeAttractor.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.MACHINE_CASING.get(),
                (machineCasing, direction) -> machineCasing.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.COMBUSTION_COLLECTOR.get(),
                (combustionCollector, direction) -> combustionCollector.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntityTypes.COMBUSTION_CONTROLLER.get(),
                (combustionController, direction) -> combustionController.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntityTypes.ROCK_CRUSHER.get(),
                (rockCrusher, direction) -> rockCrusher.getEnergyHandler()
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntityTypes.ROCK_CLEANER.get(),
                (rockCleaner, direction) -> rockCleaner.getEnergyHandler()
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntityTypes.AQUEOUS_MACHINE.get(),
                (aqueousMachine, direction) -> aqueousMachine.getEnergyHandler()
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntityTypes.WILDLIFE_ATTRACTOR.get(),
                (wildlifeAttractor, direction) -> wildlifeAttractor.getEnergyHandler()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntityTypes.FLUID_DROPPER.get(),
                (fluidDropper, direction) -> fluidDropper.getFluidHandler()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntityTypes.CRUCIBLE.get(),
                (crucible, direction) -> crucible.getFluidHandler()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntityTypes.ROCK_CLEANER.get(),
                (rockCleaner, direction) -> rockCleaner.getFluidHandler()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntityTypes.AQUEOUS_MACHINE.get(),
                (aqueousMachine, direction) -> aqueousMachine.getFluidHandler()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntityTypes.WILDLIFE_ATTRACTOR.get(),
                (wildlifeAttractor, direction) -> wildlifeAttractor.getFluidHandler()
        );
        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (stack, context) -> new WaterExtractorFluidHandler(stack, WaterExtractorItem.getCapacity()),
                ModItems.WATER_EXTRACTOR.get()
        );
    }

    private static final class WaterExtractorFluidHandler extends FluidHandlerItemStack {
        private WaterExtractorFluidHandler(final ItemStack stack, final int capacity) {
            super(ModDataComponents.WATER_EXTRACTOR_FLUID::get, stack, Math.max(0, capacity));
        }

        @Override
        public boolean canFillFluidType(final FluidStack fluid) {
            return fluid.isEmpty() || fluid.getFluid() == Fluids.WATER;
        }
    }

    private ModCapabilities() {
    }
}
