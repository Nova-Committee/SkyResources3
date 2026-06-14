package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
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
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Identifier>>
            CASING_TYPE = DATA_COMPONENT_TYPES.registerComponentType(
                    "casing_type",
                    builder -> builder
                            .persistent(Identifier.CODEC)
                            .networkSynchronized(Identifier.STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Identifier>>
            COMBUSTION_HEATER_TYPE = DATA_COMPONENT_TYPES.registerComponentType(
                    "combustion_heater_type",
                    builder -> builder
                            .persistent(Identifier.CODEC)
                            .networkSynchronized(Identifier.STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Identifier>>
            HEAT_PROVIDER_TYPE = DATA_COMPONENT_TYPES.registerComponentType(
                    "heat_provider_type",
                    builder -> builder
                            .persistent(Identifier.CODEC)
                            .networkSynchronized(Identifier.STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Identifier>>
            CONDENSER_TYPE = DATA_COMPONENT_TYPES.registerComponentType(
                    "condenser_type",
                    builder -> builder
                            .persistent(Identifier.CODEC)
                            .networkSynchronized(Identifier.STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Identifier>>
            ORE_ALCHEMY_DUST_TYPE = DATA_COMPONENT_TYPES.registerComponentType(
                    "ore_alchemy_dust_type",
                    builder -> builder
                            .persistent(Identifier.CODEC)
                            .networkSynchronized(Identifier.STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Identifier>>
            DIRTY_GEM_TYPE = DATA_COMPONENT_TYPES.registerComponentType(
                    "dirty_gem_type",
                    builder -> builder
                            .persistent(Identifier.CODEC)
                            .networkSynchronized(Identifier.STREAM_CODEC)
            );

    public static void register(final IEventBus modEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
    }

    private ModDataComponents() {
    }
}
