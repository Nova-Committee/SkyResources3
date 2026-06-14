package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.machine.CombustionHeaterType;
import committee.nova.mods.skyresources3.machine.CondenserType;
import committee.nova.mods.skyresources3.machine.CasingType;
import committee.nova.mods.skyresources3.machine.HeatProviderType;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public final class ModDataPackRegistries {
    public static final ResourceKey<Registry<CasingType>> CASING_TYPES =
            ResourceKey.createRegistryKey(id("casing_type"));
    public static final ResourceKey<Registry<CombustionHeaterType>> COMBUSTION_HEATER_TYPES =
            ResourceKey.createRegistryKey(id("combustion_heater_type"));
    public static final ResourceKey<Registry<HeatProviderType>> HEAT_PROVIDER_TYPES =
            ResourceKey.createRegistryKey(id("heat_provider_type"));
    public static final ResourceKey<Registry<CondenserType>> CONDENSER_TYPES =
            ResourceKey.createRegistryKey(id("condenser_type"));
    public static final ResourceKey<CasingType> WOODEN = casingTypeKey("wooden");
    public static final ResourceKey<CasingType> STONE = casingTypeKey("stone");
    public static final ResourceKey<CasingType> IRON = casingTypeKey("iron");
    public static final ResourceKey<CasingType> NETHER_BRICK = casingTypeKey("nether_brick");
    public static final ResourceKey<CasingType> END_STONE = casingTypeKey("end_stone");
    public static final ResourceKey<CasingType> DARK_MATTER = casingTypeKey("dark_matter");
    public static final ResourceKey<CasingType> LIGHT_MATTER = casingTypeKey("light_matter");
    public static final List<ResourceKey<CasingType>> BUILTIN_CASING_TYPES = List.of(
            WOODEN,
            STONE,
            IRON,
            NETHER_BRICK,
            END_STONE,
            DARK_MATTER,
            LIGHT_MATTER
    );
    public static final ResourceKey<CombustionHeaterType> WOODEN_COMBUSTION_HEATER =
            combustionHeaterTypeKey("wooden");
    public static final ResourceKey<CombustionHeaterType> STONE_COMBUSTION_HEATER =
            combustionHeaterTypeKey("stone");
    public static final ResourceKey<CombustionHeaterType> IRON_COMBUSTION_HEATER =
            combustionHeaterTypeKey("iron");
    public static final ResourceKey<CombustionHeaterType> NETHER_BRICK_COMBUSTION_HEATER =
            combustionHeaterTypeKey("nether_brick");
    public static final ResourceKey<CombustionHeaterType> END_STONE_COMBUSTION_HEATER =
            combustionHeaterTypeKey("end_stone");
    public static final ResourceKey<CombustionHeaterType> DARK_MATTER_COMBUSTION_HEATER =
            combustionHeaterTypeKey("dark_matter");
    public static final ResourceKey<CombustionHeaterType> LIGHT_MATTER_COMBUSTION_HEATER =
            combustionHeaterTypeKey("light_matter");
    public static final List<ResourceKey<CombustionHeaterType>> BUILTIN_COMBUSTION_HEATER_TYPES = List.of(
            WOODEN_COMBUSTION_HEATER,
            STONE_COMBUSTION_HEATER,
            IRON_COMBUSTION_HEATER,
            NETHER_BRICK_COMBUSTION_HEATER,
            END_STONE_COMBUSTION_HEATER,
            DARK_MATTER_COMBUSTION_HEATER,
            LIGHT_MATTER_COMBUSTION_HEATER
    );
    public static final ResourceKey<HeatProviderType> WOODEN_HEAT_PROVIDER =
            heatProviderTypeKey("wooden");
    public static final ResourceKey<HeatProviderType> STONE_HEAT_PROVIDER =
            heatProviderTypeKey("stone");
    public static final ResourceKey<HeatProviderType> IRON_HEAT_PROVIDER =
            heatProviderTypeKey("iron");
    public static final ResourceKey<HeatProviderType> NETHER_BRICK_HEAT_PROVIDER =
            heatProviderTypeKey("nether_brick");
    public static final ResourceKey<HeatProviderType> END_STONE_HEAT_PROVIDER =
            heatProviderTypeKey("end_stone");
    public static final ResourceKey<HeatProviderType> DARK_MATTER_HEAT_PROVIDER =
            heatProviderTypeKey("dark_matter");
    public static final ResourceKey<HeatProviderType> LIGHT_MATTER_HEAT_PROVIDER =
            heatProviderTypeKey("light_matter");
    public static final List<ResourceKey<HeatProviderType>> BUILTIN_HEAT_PROVIDER_TYPES = List.of(
            WOODEN_HEAT_PROVIDER,
            STONE_HEAT_PROVIDER,
            IRON_HEAT_PROVIDER,
            NETHER_BRICK_HEAT_PROVIDER,
            END_STONE_HEAT_PROVIDER,
            DARK_MATTER_HEAT_PROVIDER,
            LIGHT_MATTER_HEAT_PROVIDER
    );
    public static final ResourceKey<CondenserType> WOODEN_CONDENSER =
            condenserTypeKey("wooden");
    public static final ResourceKey<CondenserType> STONE_CONDENSER =
            condenserTypeKey("stone");
    public static final ResourceKey<CondenserType> IRON_CONDENSER =
            condenserTypeKey("iron");
    public static final ResourceKey<CondenserType> NETHER_BRICK_CONDENSER =
            condenserTypeKey("nether_brick");
    public static final ResourceKey<CondenserType> END_STONE_CONDENSER =
            condenserTypeKey("end_stone");
    public static final ResourceKey<CondenserType> DARK_MATTER_CONDENSER =
            condenserTypeKey("dark_matter");
    public static final ResourceKey<CondenserType> LIGHT_MATTER_CONDENSER =
            condenserTypeKey("light_matter");
    public static final List<ResourceKey<CondenserType>> BUILTIN_CONDENSER_TYPES = List.of(
            WOODEN_CONDENSER,
            STONE_CONDENSER,
            IRON_CONDENSER,
            NETHER_BRICK_CONDENSER,
            END_STONE_CONDENSER,
            DARK_MATTER_CONDENSER,
            LIGHT_MATTER_CONDENSER
    );

    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(CASING_TYPES, CasingType.CODEC, CasingType.CODEC);
        event.dataPackRegistry(COMBUSTION_HEATER_TYPES, CombustionHeaterType.CODEC, CombustionHeaterType.CODEC);
        event.dataPackRegistry(HEAT_PROVIDER_TYPES, HeatProviderType.CODEC, HeatProviderType.CODEC);
        event.dataPackRegistry(CONDENSER_TYPES, CondenserType.CODEC, CondenserType.CODEC);
    }

    public static ResourceKey<CasingType> casingTypeKey(final String path) {
        return casingTypeKey(id(path));
    }

    public static ResourceKey<CasingType> casingTypeKey(final Identifier id) {
        return ResourceKey.create(CASING_TYPES, id);
    }

    public static Identifier casingTypeId(final ResourceKey<CasingType> key) {
        return key.identifier();
    }

    public static ResourceKey<CombustionHeaterType> combustionHeaterTypeKey(final String path) {
        return combustionHeaterTypeKey(id(path));
    }

    public static ResourceKey<CombustionHeaterType> combustionHeaterTypeKey(final Identifier id) {
        return ResourceKey.create(COMBUSTION_HEATER_TYPES, id);
    }

    public static Identifier combustionHeaterTypeId(final ResourceKey<CombustionHeaterType> key) {
        return key.identifier();
    }

    public static ResourceKey<HeatProviderType> heatProviderTypeKey(final String path) {
        return heatProviderTypeKey(id(path));
    }

    public static ResourceKey<HeatProviderType> heatProviderTypeKey(final Identifier id) {
        return ResourceKey.create(HEAT_PROVIDER_TYPES, id);
    }

    public static Identifier heatProviderTypeId(final ResourceKey<HeatProviderType> key) {
        return key.identifier();
    }

    public static ResourceKey<CondenserType> condenserTypeKey(final String path) {
        return condenserTypeKey(id(path));
    }

    public static ResourceKey<CondenserType> condenserTypeKey(final Identifier id) {
        return ResourceKey.create(CONDENSER_TYPES, id);
    }

    public static Identifier condenserTypeId(final ResourceKey<CondenserType> key) {
        return key.identifier();
    }

    private static Identifier id(final String path) {
        return Identifier.fromNamespaceAndPath(Skyresources3.MODID, path);
    }

    private ModDataPackRegistries() {
    }
}
