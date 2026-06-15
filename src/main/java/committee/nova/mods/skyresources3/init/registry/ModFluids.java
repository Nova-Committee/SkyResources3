package committee.nova.mods.skyresources3.init.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, Skyresources3.MODID);

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> CRYSTAL_FLUID = FLUIDS.register(
            "crystal_fluid",
            () -> new BaseFlowingFluid.Source(crystalFluidProperties())
    );
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_CRYSTAL_FLUID = FLUIDS.register(
            "flowing_crystal_fluid",
            () -> new BaseFlowingFluid.Flowing(crystalFluidProperties())
    );

    public static void register(final IEventBus modEventBus) {
        FLUIDS.register(modEventBus);
    }

    private static BaseFlowingFluid.Properties crystalFluidProperties() {
        return new BaseFlowingFluid.Properties(
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
