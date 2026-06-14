package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.machine.CasingType;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public final class ModDataPackRegistries {
    public static final ResourceKey<Registry<CasingType>> CASING_TYPES =
            ResourceKey.createRegistryKey(id("casing_type"));
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

    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(CASING_TYPES, CasingType.CODEC, CasingType.CODEC);
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

    private static Identifier id(final String path) {
        return Identifier.fromNamespaceAndPath(Skyresources3.MODID, path);
    }

    private ModDataPackRegistries() {
    }
}
