package committee.nova.mods.skyresources3.init.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public final class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, Skyresources3.MODID);

    public static final RegistryObject<ForgeFlowingFluid.Source> CRYSTAL_FLUID = FLUIDS.register(
            "crystal_fluid",
            () -> new ForgeFlowingFluid.Source(crystalFluidProperties())
    );
    public static final RegistryObject<ForgeFlowingFluid.Flowing> FLOWING_CRYSTAL_FLUID = FLUIDS.register(
            "flowing_crystal_fluid",
            () -> new ForgeFlowingFluid.Flowing(crystalFluidProperties())
    );

    public static void register(final IEventBus modEventBus) {
        FLUIDS.register(modEventBus);
    }

    private static ForgeFlowingFluid.Properties crystalFluidProperties() {
        return new ForgeFlowingFluid.Properties(
                ModFluidTypes.CRYSTAL_FLUID,
                ModFluids.CRYSTAL_FLUID,
                ModFluids.FLOWING_CRYSTAL_FLUID
        )
                .block(ModBlocks.CRYSTAL_FLUID)
                .bucket(ModItems.CRYSTAL_FLUID_BUCKET);
    }

    private ModFluids() {
    }
}
