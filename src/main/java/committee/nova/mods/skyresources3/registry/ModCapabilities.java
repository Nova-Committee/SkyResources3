package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.item.WaterExtractorItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler;

public final class ModCapabilities {
    public static void register(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModBlockEntityTypes.FUSION_TABLE.get(),
                (fusionTable, direction) -> fusionTable.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModBlockEntityTypes.DIRT_FURNACE.get(),
                (dirtFurnace, direction) -> dirtFurnace.getItemHandler(direction)
        );
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModBlockEntityTypes.FREEZER.get(),
                (freezer, direction) -> freezer.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModBlockEntityTypes.QUICK_DROPPER.get(),
                (quickDropper, direction) -> quickDropper.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModBlockEntityTypes.DARK_MATTER_WARPER.get(),
                (darkMatterWarper, direction) -> darkMatterWarper.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModBlockEntityTypes.CRUCIBLE_INSERTER.get(),
                (crucibleInserter, direction) -> crucibleInserter.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModBlockEntityTypes.ROCK_CRUSHER.get(),
                (rockCrusher, direction) -> rockCrusher.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModBlockEntityTypes.ROCK_CLEANER.get(),
                (rockCleaner, direction) -> rockCleaner.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModBlockEntityTypes.AQUEOUS_MACHINE.get(),
                (aqueousMachine, direction) -> aqueousMachine.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModBlockEntityTypes.MACHINE_CASING.get(),
                (machineCasing, direction) -> machineCasing.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModBlockEntityTypes.COMBUSTION_COLLECTOR.get(),
                (combustionCollector, direction) -> combustionCollector.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ModBlockEntityTypes.COMBUSTION_CONTROLLER.get(),
                (combustionController, direction) -> combustionController.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ModBlockEntityTypes.ROCK_CRUSHER.get(),
                (rockCrusher, direction) -> rockCrusher.getEnergyHandler()
        );
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ModBlockEntityTypes.ROCK_CLEANER.get(),
                (rockCleaner, direction) -> rockCleaner.getEnergyHandler()
        );
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ModBlockEntityTypes.AQUEOUS_MACHINE.get(),
                (aqueousMachine, direction) -> aqueousMachine.getEnergyHandler()
        );
        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                ModBlockEntityTypes.FLUID_DROPPER.get(),
                (fluidDropper, direction) -> fluidDropper.getFluidHandler()
        );
        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                ModBlockEntityTypes.CRUCIBLE.get(),
                (crucible, direction) -> crucible.getFluidHandler()
        );
        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                ModBlockEntityTypes.ROCK_CLEANER.get(),
                (rockCleaner, direction) -> rockCleaner.getFluidHandler()
        );
        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                ModBlockEntityTypes.AQUEOUS_MACHINE.get(),
                (aqueousMachine, direction) -> aqueousMachine.getFluidHandler()
        );
        event.registerItem(
                Capabilities.Fluid.ITEM,
                (stack, context) -> context == null
                        ? null
                        : new WaterExtractorFluidHandler(
                                context,
                                ModDataComponents.WATER_EXTRACTOR_FLUID.get(),
                                WaterExtractorItem.getCapacity()
                        ),
                ModItems.WATER_EXTRACTOR.get()
        );
    }

    private static final class WaterExtractorFluidHandler extends ItemAccessFluidHandler {
        private WaterExtractorFluidHandler(
                final ItemAccess itemAccess,
                final DataComponentType<SimpleFluidContent> component,
                final int capacity
        ) {
            super(itemAccess, component, Math.max(0, capacity));
        }

        @Override
        public boolean isValid(final int index, final FluidResource resource) {
            return super.isValid(index, resource) && (resource.isEmpty() || resource.getFluid() == Fluids.WATER);
        }
    }

    private ModCapabilities() {
    }
}
