package committee.nova.mods.skyresources3.init.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.block.entity.AqueousMachineBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.CrucibleBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.CrucibleInserterBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.CombustionCollectorBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.CombustionControllerBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.DarkMatterWarperBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.DirtFurnaceBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.EndPortalCoreBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.FluidDropperBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.FusionTableBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.FreezerBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.LifeInfuserBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.LifeInjectorBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.MachineCasingBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.QuickDropperBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.RockCleanerBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.RockCrusherBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.StandaloneMachineBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.WildlifeAttractorBlockEntity;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public final class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
            Registries.BLOCK_ENTITY_TYPE,
            Skyresources3.MODID
    );

    public static final RegistryObject<BlockEntityType<LifeInjectorBlockEntity>> LIFE_INJECTOR =
            BLOCK_ENTITY_TYPES.register(
                    "life_injector",
                    () -> type(LifeInjectorBlockEntity::new, ModBlocks.LIFE_INJECTOR.get())
            );
    public static final RegistryObject<BlockEntityType<LifeInfuserBlockEntity>> LIFE_INFUSER =
            BLOCK_ENTITY_TYPES.register(
                    "life_infuser",
                    () -> type(LifeInfuserBlockEntity::new, ModBlocks.LIFE_INFUSER.get())
            );
    public static final RegistryObject<BlockEntityType<FusionTableBlockEntity>> FUSION_TABLE =
            BLOCK_ENTITY_TYPES.register(
                    "fusion_table",
                    () -> type(FusionTableBlockEntity::new, ModBlocks.FUSION_TABLE.get())
            );
    public static final RegistryObject<BlockEntityType<DirtFurnaceBlockEntity>> DIRT_FURNACE =
            BLOCK_ENTITY_TYPES.register(
                    "dirt_furnace",
                    () -> type(DirtFurnaceBlockEntity::new, ModBlocks.DIRT_FURNACE.get())
            );
    public static final RegistryObject<BlockEntityType<QuickDropperBlockEntity>> QUICK_DROPPER =
            BLOCK_ENTITY_TYPES.register(
                    "quick_dropper",
                    () -> type(QuickDropperBlockEntity::new, ModBlocks.QUICK_DROPPER.get())
            );
    public static final RegistryObject<BlockEntityType<DarkMatterWarperBlockEntity>>
            DARK_MATTER_WARPER =
            BLOCK_ENTITY_TYPES.register(
                    "dark_matter_warper",
                    () -> type(
                            DarkMatterWarperBlockEntity::new,
                            ModBlocks.DARK_MATTER_WARPER.get()
                    )
            );
    public static final RegistryObject<BlockEntityType<EndPortalCoreBlockEntity>>
            END_PORTAL_CORE =
            BLOCK_ENTITY_TYPES.register(
                    "end_portal_core",
                    () -> type(
                            EndPortalCoreBlockEntity::new,
                            ModBlocks.END_PORTAL_CORE.get()
                    )
            );
    public static final RegistryObject<BlockEntityType<FluidDropperBlockEntity>> FLUID_DROPPER =
            BLOCK_ENTITY_TYPES.register(
                    "fluid_dropper",
                    () -> type(FluidDropperBlockEntity::new, ModBlocks.FLUID_DROPPER.get())
            );
    public static final RegistryObject<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE =
            BLOCK_ENTITY_TYPES.register(
                    "crucible",
                    () -> type(CrucibleBlockEntity::new, ModBlocks.CRUCIBLE.get())
            );
    public static final RegistryObject<BlockEntityType<CrucibleInserterBlockEntity>>
            CRUCIBLE_INSERTER =
            BLOCK_ENTITY_TYPES.register(
                    "crucible_inserter",
                    () -> type(CrucibleInserterBlockEntity::new, ModBlocks.CRUCIBLE_INSERTER.get())
            );
    public static final RegistryObject<BlockEntityType<RockCrusherBlockEntity>> ROCK_CRUSHER =
            BLOCK_ENTITY_TYPES.register(
                    "rock_crusher",
                    () -> type(RockCrusherBlockEntity::new, ModBlocks.ROCK_CRUSHER.get())
            );
    public static final RegistryObject<BlockEntityType<RockCleanerBlockEntity>> ROCK_CLEANER =
            BLOCK_ENTITY_TYPES.register(
                    "rock_cleaner",
                    () -> type(RockCleanerBlockEntity::new, ModBlocks.ROCK_CLEANER.get())
            );
    public static final RegistryObject<BlockEntityType<AqueousMachineBlockEntity>>
            AQUEOUS_MACHINE =
            BLOCK_ENTITY_TYPES.register(
                    "aqueous_machine",
                    () -> type(
                            AqueousMachineBlockEntity::new,
                            ModBlocks.AQUEOUS_CONCENTRATOR.get(),
                            ModBlocks.AQUEOUS_DECONCENTRATOR.get()
                    )
            );
    public static final RegistryObject<BlockEntityType<WildlifeAttractorBlockEntity>>
            WILDLIFE_ATTRACTOR =
            BLOCK_ENTITY_TYPES.register(
                    "wildlife_attractor",
                    () -> type(
                            WildlifeAttractorBlockEntity::new,
                            ModBlocks.WILDLIFE_ATTRACTOR.get()
                    )
            );
    public static final RegistryObject<BlockEntityType<MachineCasingBlockEntity>> MACHINE_CASING =
            BLOCK_ENTITY_TYPES.register(
                    "machine_casing",
                    () -> type(
                            MachineCasingBlockEntity::new,
                            ModBlocks.MACHINE_CASING.get()
                    )
            );
    public static final RegistryObject<BlockEntityType<StandaloneMachineBlockEntity>>
            STANDALONE_MACHINE =
            BLOCK_ENTITY_TYPES.register(
                    "standalone_machine",
                    () -> type(
                            StandaloneMachineBlockEntity::new,
                            ModBlocks.COMBUSTION_HEATER.get(),
                            ModBlocks.HEAT_PROVIDER.get(),
                            ModBlocks.CONDENSER.get()
                    )
            );
    public static final RegistryObject<BlockEntityType<CombustionCollectorBlockEntity>>
            COMBUSTION_COLLECTOR =
            BLOCK_ENTITY_TYPES.register(
                    "combustion_collector",
                    () -> type(
                            CombustionCollectorBlockEntity::new,
                            ModBlocks.COMBUSTION_COLLECTOR.get()
                    )
            );
    public static final RegistryObject<BlockEntityType<CombustionControllerBlockEntity>>
            COMBUSTION_CONTROLLER =
            BLOCK_ENTITY_TYPES.register(
                    "combustion_controller",
                    () -> type(
                            CombustionControllerBlockEntity::new,
                            ModBlocks.COMBUSTION_CONTROLLER.get()
                    )
            );
    public static final RegistryObject<BlockEntityType<FreezerBlockEntity>> FREEZER =
            BLOCK_ENTITY_TYPES.register(
                    "freezer",
                    () -> type(
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

    private static <T extends BlockEntity> BlockEntityType<T> type(
            final BlockEntityType.BlockEntitySupplier<? extends T> factory,
            final Block... blocks
    ) {
        return new BlockEntityType<>(factory, Set.of(blocks), null);
    }
}
