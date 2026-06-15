package committee.nova.mods.skyresources3.init.integration.kubejs;

import committee.nova.mods.skyresources3.common.item.DirtyGemType;
import committee.nova.mods.skyresources3.common.item.OreAlchemyDustType;
import committee.nova.mods.skyresources3.core.machine.CasingType;
import committee.nova.mods.skyresources3.core.machine.CombustionHeaterType;
import committee.nova.mods.skyresources3.core.machine.CondenserType;
import committee.nova.mods.skyresources3.core.machine.HeatProviderType;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.ServerRegistryRegistry;

public final class SkyResourcesKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void registerServerRegistries(final ServerRegistryRegistry registry) {
        registry.register(ModDataPackRegistries.CASING_TYPES, CasingType.CODEC, CasingType.class);
        registry.register(
                ModDataPackRegistries.COMBUSTION_HEATER_TYPES,
                CombustionHeaterType.CODEC,
                CombustionHeaterType.class
        );
        registry.register(ModDataPackRegistries.HEAT_PROVIDER_TYPES, HeatProviderType.CODEC, HeatProviderType.class);
        registry.register(ModDataPackRegistries.CONDENSER_TYPES, CondenserType.CODEC, CondenserType.class);
        registry.register(
                ModDataPackRegistries.ORE_ALCHEMY_DUST_TYPES,
                OreAlchemyDustType.CODEC,
                OreAlchemyDustType.class
        );
        registry.register(ModDataPackRegistries.DIRTY_GEM_TYPES, DirtyGemType.CODEC, DirtyGemType.class);
    }
}
