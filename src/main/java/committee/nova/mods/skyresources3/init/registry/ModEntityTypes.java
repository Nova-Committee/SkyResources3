package committee.nova.mods.skyresources3.init.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.entity.HeavyExplosiveSnowball;
import committee.nova.mods.skyresources3.common.entity.HeavySnowball;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Skyresources3.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<HeavySnowball>> HEAVY_SNOWBALL =
            ENTITY_TYPES.register(
                    "heavy_snowball",
                    id -> EntityType.Builder.<HeavySnowball>of(HeavySnowball::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build(id.toString())
            );
    public static final DeferredHolder<EntityType<?>, EntityType<HeavyExplosiveSnowball>> HEAVY_EXPLOSIVE_SNOWBALL =
            ENTITY_TYPES.register(
                    "heavy_explosive_snowball",
                    id -> EntityType.Builder.<HeavyExplosiveSnowball>of(HeavyExplosiveSnowball::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build(id.toString())
            );

    public static void register(final IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }

    private ModEntityTypes() {
    }
}
