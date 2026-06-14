package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(
            NeoForgeRegistries.FLUID_TYPES,
            Skyresources3.MODID
    );

    public static final DeferredHolder<FluidType, FluidType> CRYSTAL_FLUID = FLUID_TYPES.register(
            "crystal_fluid",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid_type.skyresources.crystal_fluid")
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY))
    );

    public static void register(final IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
    }

    private ModFluidTypes() {
    }
}
