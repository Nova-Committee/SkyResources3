package committee.nova.mods.skyresources3.init.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.entity.HeavyExplosiveSnowball;
import committee.nova.mods.skyresources3.common.entity.HeavySnowball;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntityTypes {
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(Skyresources3.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<HeavySnowball>> HEAVY_SNOWBALL =
            ENTITY_TYPES.registerEntityType(
                    "heavy_snowball",
                    HeavySnowball::new,
                    MobCategory.MISC,
                    builder -> builder.noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
            );
    public static final DeferredHolder<EntityType<?>, EntityType<HeavyExplosiveSnowball>> HEAVY_EXPLOSIVE_SNOWBALL =
            ENTITY_TYPES.registerEntityType(
                    "heavy_explosive_snowball",
                    HeavyExplosiveSnowball::new,
                    MobCategory.MISC,
                    builder -> builder.noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
            );

    public static void register(final IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }

    private ModEntityTypes() {
    }
}
