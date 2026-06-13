package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.block.entity.DirtFurnaceBlockEntity;
import committee.nova.mods.skyresources3.block.entity.FusionTableBlockEntity;
import committee.nova.mods.skyresources3.block.entity.FreezerBlockEntity;
import committee.nova.mods.skyresources3.block.entity.LifeInfuserBlockEntity;
import committee.nova.mods.skyresources3.block.entity.LifeInjectorBlockEntity;
import committee.nova.mods.skyresources3.block.entity.QuickDropperBlockEntity;
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
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LifeInfuserBlockEntity>> LIFE_INFUSER =
            BLOCK_ENTITY_TYPES.register(
                    "life_infuser",
                    () -> new BlockEntityType<>(LifeInfuserBlockEntity::new, ModBlocks.LIFE_INFUSER.get())
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FusionTableBlockEntity>> FUSION_TABLE =
            BLOCK_ENTITY_TYPES.register(
                    "fusion_table",
                    () -> new BlockEntityType<>(FusionTableBlockEntity::new, ModBlocks.FUSION_TABLE.get())
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DirtFurnaceBlockEntity>> DIRT_FURNACE =
            BLOCK_ENTITY_TYPES.register(
                    "dirt_furnace",
                    () -> new BlockEntityType<>(DirtFurnaceBlockEntity::new, ModBlocks.DIRT_FURNACE.get())
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuickDropperBlockEntity>> QUICK_DROPPER =
            BLOCK_ENTITY_TYPES.register(
                    "quick_dropper",
                    () -> new BlockEntityType<>(QuickDropperBlockEntity::new, ModBlocks.QUICK_DROPPER.get())
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FreezerBlockEntity>> FREEZER =
            BLOCK_ENTITY_TYPES.register(
                    "freezer",
                    () -> new BlockEntityType<>(
                            FreezerBlockEntity::new,
                            ModBlocks.MINI_FREEZER.get(),
                            ModBlocks.IRON_FREEZER.get(),
                            ModBlocks.LIGHT_FREEZER.get()
                    )
            );

    public static void register(final IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }

    private ModBlockEntityTypes() {
    }
}
