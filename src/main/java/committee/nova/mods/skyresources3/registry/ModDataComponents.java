package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Skyresources3.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>>
            WATER_EXTRACTOR_FLUID = DATA_COMPONENT_TYPES.registerComponentType(
                    "water_extractor_fluid",
                    builder -> builder
                            .persistent(SimpleFluidContent.CODEC)
                            .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
            );

    public static void register(final IEventBus modEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
    }

    private ModDataComponents() {
    }
}
