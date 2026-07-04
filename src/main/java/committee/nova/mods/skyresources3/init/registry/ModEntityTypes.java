package committee.nova.mods.skyresources3.init.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.entity.HeavyExplosiveSnowball;
import committee.nova.mods.skyresources3.common.entity.HeavySnowball;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public final class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Skyresources3.MODID);

    public static final RegistryObject<EntityType<HeavySnowball>> HEAVY_SNOWBALL =
            ENTITY_TYPES.register(
                    "heavy_snowball",
                    () -> EntityType.Builder.<HeavySnowball>of(HeavySnowball::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build(Skyresources3.MODID + ":heavy_snowball")
            );
    public static final RegistryObject<EntityType<HeavyExplosiveSnowball>> HEAVY_EXPLOSIVE_SNOWBALL =
            ENTITY_TYPES.register(
                    "heavy_explosive_snowball",
                    () -> EntityType.Builder.<HeavyExplosiveSnowball>of(HeavyExplosiveSnowball::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build(Skyresources3.MODID + ":heavy_explosive_snowball")
            );

    public static void register(final IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }

    private ModEntityTypes() {
    }
}
