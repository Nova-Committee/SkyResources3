package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.block.entity.AqueousMachineBlockEntity;
import committee.nova.mods.skyresources3.block.entity.CrucibleBlockEntity;
import committee.nova.mods.skyresources3.block.entity.CrucibleInserterBlockEntity;
import committee.nova.mods.skyresources3.block.entity.CombustionCollectorBlockEntity;
import committee.nova.mods.skyresources3.block.entity.CombustionControllerBlockEntity;
import committee.nova.mods.skyresources3.block.entity.DarkMatterWarperBlockEntity;
import committee.nova.mods.skyresources3.block.entity.DirtFurnaceBlockEntity;
import committee.nova.mods.skyresources3.block.entity.EndPortalCoreBlockEntity;
import committee.nova.mods.skyresources3.block.entity.FluidDropperBlockEntity;
import committee.nova.mods.skyresources3.block.entity.FusionTableBlockEntity;
import committee.nova.mods.skyresources3.block.entity.FreezerBlockEntity;
import committee.nova.mods.skyresources3.block.entity.LifeInfuserBlockEntity;
import committee.nova.mods.skyresources3.block.entity.LifeInjectorBlockEntity;
import committee.nova.mods.skyresources3.block.entity.MachineCasingBlockEntity;
import committee.nova.mods.skyresources3.block.entity.QuickDropperBlockEntity;
import committee.nova.mods.skyresources3.block.entity.RockCleanerBlockEntity;
import committee.nova.mods.skyresources3.block.entity.RockCrusherBlockEntity;
import net.minecraft.world.level.block.Block;
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
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DarkMatterWarperBlockEntity>>
            DARK_MATTER_WARPER =
            BLOCK_ENTITY_TYPES.register(
                    "dark_matter_warper",
                    () -> new BlockEntityType<>(
                            DarkMatterWarperBlockEntity::new,
                            ModBlocks.DARK_MATTER_WARPER.get()
                    )
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EndPortalCoreBlockEntity>>
            END_PORTAL_CORE =
            BLOCK_ENTITY_TYPES.register(
                    "end_portal_core",
                    () -> new BlockEntityType<>(
                            EndPortalCoreBlockEntity::new,
                            ModBlocks.END_PORTAL_CORE.get()
                    )
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDropperBlockEntity>> FLUID_DROPPER =
            BLOCK_ENTITY_TYPES.register(
                    "fluid_dropper",
                    () -> new BlockEntityType<>(FluidDropperBlockEntity::new, ModBlocks.FLUID_DROPPER.get())
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleBlockEntity>> CRUCIBLE =
            BLOCK_ENTITY_TYPES.register(
                    "crucible",
                    () -> new BlockEntityType<>(CrucibleBlockEntity::new, ModBlocks.CRUCIBLE.get())
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrucibleInserterBlockEntity>>
            CRUCIBLE_INSERTER =
            BLOCK_ENTITY_TYPES.register(
                    "crucible_inserter",
                    () -> new BlockEntityType<>(CrucibleInserterBlockEntity::new, ModBlocks.CRUCIBLE_INSERTER.get())
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RockCrusherBlockEntity>> ROCK_CRUSHER =
            BLOCK_ENTITY_TYPES.register(
                    "rock_crusher",
                    () -> new BlockEntityType<>(RockCrusherBlockEntity::new, ModBlocks.ROCK_CRUSHER.get())
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RockCleanerBlockEntity>> ROCK_CLEANER =
            BLOCK_ENTITY_TYPES.register(
                    "rock_cleaner",
                    () -> new BlockEntityType<>(RockCleanerBlockEntity::new, ModBlocks.ROCK_CLEANER.get())
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AqueousMachineBlockEntity>>
            AQUEOUS_MACHINE =
            BLOCK_ENTITY_TYPES.register(
                    "aqueous_machine",
                    () -> new BlockEntityType<>(
                            AqueousMachineBlockEntity::new,
                            ModBlocks.AQUEOUS_CONCENTRATOR.get(),
                            ModBlocks.AQUEOUS_DECONCENTRATOR.get()
                    )
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MachineCasingBlockEntity>> MACHINE_CASING =
            BLOCK_ENTITY_TYPES.register(
                    "machine_casing",
                    () -> new BlockEntityType<>(
                            MachineCasingBlockEntity::new,
                            ModBlocks.MACHINE_CASINGS.values().stream()
                                    .map(holder -> (Block) holder.get())
                                    .toArray(Block[]::new)
                    )
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CombustionCollectorBlockEntity>>
            COMBUSTION_COLLECTOR =
            BLOCK_ENTITY_TYPES.register(
                    "combustion_collector",
                    () -> new BlockEntityType<>(
                            CombustionCollectorBlockEntity::new,
                            ModBlocks.COMBUSTION_COLLECTOR.get()
                    )
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CombustionControllerBlockEntity>>
            COMBUSTION_CONTROLLER =
            BLOCK_ENTITY_TYPES.register(
                    "combustion_controller",
                    () -> new BlockEntityType<>(
                            CombustionControllerBlockEntity::new,
                            ModBlocks.COMBUSTION_CONTROLLER.get()
                    )
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
