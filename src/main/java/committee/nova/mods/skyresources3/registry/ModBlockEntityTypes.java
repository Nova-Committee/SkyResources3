package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.block.entity.LifeInjectorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
            Registries.BLOCK_ENTITY_TYPE,
            Skyresources3.MODID
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LifeInjectorBlockEntity>> LIFE_INJECTOR =
            BLOCK_ENTITY_TYPES.register(
                    "life_injector",
                    () -> new BlockEntityType<>(LifeInjectorBlockEntity::new, ModBlocks.LIFE_INJECTOR.get())
            );

    public static void register(final IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }

    private ModBlockEntityTypes() {
    }
}
