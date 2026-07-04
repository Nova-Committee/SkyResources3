package committee.nova.mods.skyresources3.init.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.block.AqueousConcentratorBlock;
import committee.nova.mods.skyresources3.common.block.AqueousDeconcentratorBlock;
import committee.nova.mods.skyresources3.common.block.BlazePowderBlock;
import committee.nova.mods.skyresources3.common.block.CactusFruitNeedleBlock;
import committee.nova.mods.skyresources3.common.block.CombustionCollectorBlock;
import committee.nova.mods.skyresources3.common.block.CombustionControllerBlock;
import committee.nova.mods.skyresources3.common.block.CrucibleBlock;
import committee.nova.mods.skyresources3.common.block.CrucibleInserterBlock;
import committee.nova.mods.skyresources3.common.block.DarkMatterWarperBlock;
import committee.nova.mods.skyresources3.common.block.DirtFurnaceBlock;
import committee.nova.mods.skyresources3.common.block.DryCactusBlock;
import committee.nova.mods.skyresources3.common.block.EndPortalCoreBlock;
import committee.nova.mods.skyresources3.common.block.FluidDropperBlock;
import committee.nova.mods.skyresources3.common.block.FusionTableBlock;
import committee.nova.mods.skyresources3.common.block.FreezerBlock;
import committee.nova.mods.skyresources3.common.block.LifeInfuserBlock;
import committee.nova.mods.skyresources3.common.block.LifeInjectorBlock;
import committee.nova.mods.skyresources3.common.block.MachineCasingBlock;
import committee.nova.mods.skyresources3.common.block.MagmafiedStoneBlock;
import committee.nova.mods.skyresources3.common.block.QuickDropperBlock;
import committee.nova.mods.skyresources3.common.block.RockCleanerBlock;
import committee.nova.mods.skyresources3.common.block.RockCrusherBlock;
import committee.nova.mods.skyresources3.common.block.SilverfishDisruptorBlock;
import committee.nova.mods.skyresources3.common.block.StandaloneMachineBlock;
import committee.nova.mods.skyresources3.common.block.WildlifeAttractorBlock;
import committee.nova.mods.skyresources3.common.block.entity.StandaloneMachineBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
            ForgeRegistries.BLOCKS,
            Skyresources3.MODID
    );

    public static final RegistryObject<Block> COMPRESSED_COAL_BLOCK = registerSimpleBlock(
            "compressed_coal_block",
            stone(6.0F, 6.0F)
    );
    public static final RegistryObject<Block> COAL_INFUSED_BLOCK = registerSimpleBlock(
            "coal_infused_block",
            stone(5.0F, 6.0F)
    );
    public static final RegistryObject<Block> SANDY_NETHERRACK = registerSimpleBlock(
            "sandy_netherrack",
            stone(0.4F, 0.4F)
    );
    public static final RegistryObject<Block> PETRIFIED_WOOD = registerSimpleBlock(
            "petrified_wood",
            stone(2.0F, 10.0F)
    );
    public static final RegistryObject<Block> PETRIFIED_PLANKS = registerSimpleBlock(
            "petrified_planks",
            properties(2.0F, 6.0F, SoundType.WOOD)
    );
    public static final RegistryObject<MagmafiedStoneBlock> MAGMAFIED_STONE = registerBlock(
            "magmafied_stone",
            MagmafiedStoneBlock::new,
            stone(1.5F, 6.0F)
    );
    public static final RegistryObject<Block> HEAVY_SNOW = registerSimpleBlock(
            "heavy_snow",
            properties(0.3F, 0.3F, SoundType.SNOW)
    );
    public static final RegistryObject<BlazePowderBlock> BLAZE_POWDER_BLOCK = registerBlock(
            "blaze_powder_block",
            BlazePowderBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(0.5F, 0.5F)
                    .sound(SoundType.GRAVEL)
    );
    public static final RegistryObject<Block> DARK_MATTER_BLOCK = registerSimpleBlock(
            "dark_matter_block",
            stone(5.0F, 10.0F)
    );
    public static final RegistryObject<Block> LIGHT_MATTER_BLOCK = registerSimpleBlock(
            "light_matter_block",
            stone(5.0F, 10.0F)
    );
    public static final RegistryObject<Block> ALCHEMICAL_GLASS = registerSimpleBlock(
            "alchemical_glass",
            properties(0.3F, 0.3F, SoundType.GLASS).noOcclusion()
    );
    public static final RegistryObject<LiquidBlock> CRYSTAL_FLUID = registerBlock(
            "crystal_fluid",
            properties -> new LiquidBlock(ModFluids.CRYSTAL_FLUID.get(), properties),
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .replaceable()
                    .noCollission()
                    .strength(100.0F)
                    .liquid()
                    .pushReaction(PushReaction.DESTROY)
    );
    public static final RegistryObject<FusionTableBlock> FUSION_TABLE = registerBlock(
            "fusion_table",
            FusionTableBlock::new,
            machineWood(3.0F, 10.0F)
    );
    public static final RegistryObject<DirtFurnaceBlock> DIRT_FURNACE = registerBlock(
            "dirt_furnace",
            DirtFurnaceBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.5F, 0.5F)
                    .sound(SoundType.GRAVEL)
    );
    public static final RegistryObject<QuickDropperBlock> QUICK_DROPPER = registerBlock(
            "quick_dropper",
            QuickDropperBlock::new,
            properties(6.0F, 12.0F, SoundType.METAL)
    );
    public static final RegistryObject<DarkMatterWarperBlock> DARK_MATTER_WARPER = registerBlock(
            "dark_matter_warper",
            DarkMatterWarperBlock::new,
            properties(8.0F, 12.0F, SoundType.METAL).noOcclusion()
    );
    public static final RegistryObject<EndPortalCoreBlock> END_PORTAL_CORE = registerBlock(
            "end_portal_core",
            EndPortalCoreBlock::new,
            properties(6.0F, 12.0F, SoundType.METAL).noOcclusion()
    );
    public static final RegistryObject<SilverfishDisruptorBlock> SILVERFISH_DISRUPTOR = registerBlock(
            "silverfish_disruptor",
            SilverfishDisruptorBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(0.5F, 0.5F)
                    .sound(SoundType.GLASS)
                    .noOcclusion()
    );
    public static final RegistryObject<FluidDropperBlock> FLUID_DROPPER = registerBlock(
            "fluid_dropper",
            FluidDropperBlock::new,
            properties(2.0F, 12.0F, SoundType.STONE)
    );
    public static final RegistryObject<CrucibleBlock> CRUCIBLE = registerBlock(
            "crucible",
            CrucibleBlock::new,
            properties(2.0F, 12.0F, SoundType.STONE).noOcclusion()
    );
    public static final RegistryObject<CrucibleInserterBlock> CRUCIBLE_INSERTER = registerBlock(
            "crucible_inserter",
            CrucibleInserterBlock::new,
            properties(2.0F, 12.0F, SoundType.STONE).noOcclusion()
    );
    public static final RegistryObject<RockCrusherBlock> ROCK_CRUSHER = registerBlock(
            "rock_crusher",
            RockCrusherBlock::new,
            properties(6.0F, 12.0F, SoundType.METAL)
    );
    public static final RegistryObject<RockCleanerBlock> ROCK_CLEANER = registerBlock(
            "rock_cleaner",
            RockCleanerBlock::new,
            properties(6.0F, 12.0F, SoundType.METAL)
    );
    public static final RegistryObject<AqueousConcentratorBlock> AQUEOUS_CONCENTRATOR = registerBlock(
            "aqueous_concentrator",
            AqueousConcentratorBlock::new,
            properties(2.0F, 12.0F, SoundType.METAL)
    );
    public static final RegistryObject<AqueousDeconcentratorBlock> AQUEOUS_DECONCENTRATOR = registerBlock(
            "aqueous_deconcentrator",
            AqueousDeconcentratorBlock::new,
            properties(2.0F, 12.0F, SoundType.METAL)
    );
    public static final RegistryObject<WildlifeAttractorBlock> WILDLIFE_ATTRACTOR = registerBlock(
            "wildlife_attractor",
            WildlifeAttractorBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GRASS)
                    .strength(2.0F, 12.0F)
                    .sound(SoundType.GRASS)
    );
    public static final RegistryObject<MachineCasingBlock> MACHINE_CASING = registerBlock(
            "machine_casing",
            MachineCasingBlock::new,
            properties(2.0F, 12.0F, SoundType.METAL).noOcclusion()
    );
    public static final RegistryObject<StandaloneMachineBlock> COMBUSTION_HEATER = registerBlock(
            "combustion_heater",
            properties -> new StandaloneMachineBlock(
                    StandaloneMachineBlockEntity.MachineKind.COMBUSTION_HEATER,
                    properties
            ),
            properties(2.0F, 12.0F, SoundType.METAL).noOcclusion()
    );
    public static final RegistryObject<StandaloneMachineBlock> HEAT_PROVIDER = registerBlock(
            "heat_provider",
            properties -> new StandaloneMachineBlock(
                    StandaloneMachineBlockEntity.MachineKind.HEAT_PROVIDER,
                    properties
            ),
            properties(2.0F, 12.0F, SoundType.METAL).noOcclusion()
    );
    public static final RegistryObject<StandaloneMachineBlock> CONDENSER = registerBlock(
            "condenser",
            properties -> new StandaloneMachineBlock(
                    StandaloneMachineBlockEntity.MachineKind.CONDENSER,
                    properties
            ),
            properties(2.0F, 12.0F, SoundType.METAL).noOcclusion()
    );
    public static final RegistryObject<CombustionCollectorBlock> COMBUSTION_COLLECTOR = registerBlock(
            "combustion_collector",
            CombustionCollectorBlock::new,
            properties(6.0F, 12.0F, SoundType.METAL)
    );
    public static final RegistryObject<CombustionControllerBlock> COMBUSTION_CONTROLLER = registerBlock(
            "combustion_controller",
            CombustionControllerBlock::new,
            properties(6.0F, 12.0F, SoundType.METAL)
    );
    public static final RegistryObject<FreezerBlock> MINI_FREEZER = registerBlock(
            "mini_freezer",
            properties -> new FreezerBlock(FreezerBlock.Tier.MINI, properties),
            properties(0.5F, 0.5F, SoundType.SNOW).noOcclusion()
    );
    public static final RegistryObject<FreezerBlock> IRON_FREEZER = registerBlock(
            "iron_freezer",
            properties -> new FreezerBlock(FreezerBlock.Tier.IRON, properties),
            properties(2.0F, 2.0F, SoundType.METAL).noOcclusion()
    );
    public static final RegistryObject<FreezerBlock> LIGHT_FREEZER = registerBlock(
            "light_freezer",
            properties -> new FreezerBlock(FreezerBlock.Tier.LIGHT, properties),
            properties(8.0F, 12.0F, SoundType.METAL).noOcclusion()
    );
    public static final RegistryObject<LifeInfuserBlock> LIFE_INFUSER = registerBlock(
            "life_infuser",
            LifeInfuserBlock::new,
            machineWood()
    );
    public static final RegistryObject<LifeInjectorBlock> LIFE_INJECTOR = registerBlock(
            "life_injector",
            LifeInjectorBlock::new,
            machineWood()
    );
    public static final RegistryObject<CactusFruitNeedleBlock> CACTUS_FRUIT_NEEDLE = registerBlock(
            "cactus_fruit_needle",
            CactusFruitNeedleBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.5F, 0.5F)
                    .sound(SoundType.GRASS)
                    .noOcclusion()
    );
    public static final RegistryObject<DryCactusBlock> DRY_CACTUS = registerBlock(
            "dry_cactus",
            DryCactusBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.5F, 0.5F)
                    .sound(SoundType.GRASS)
                    .noOcclusion()
    );

    public static void register(final IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }


    private static RegistryObject<Block> registerSimpleBlock(
            final String name,
            final BlockBehaviour.Properties properties
    ) {
        return BLOCKS.register(name, () -> new Block(properties));
    }

    private static <T extends Block> RegistryObject<T> registerBlock(
            final String name,
            final Function<BlockBehaviour.Properties, T> factory,
            final BlockBehaviour.Properties properties
    ) {
        return BLOCKS.register(name, () -> factory.apply(properties));
    }
    private static BlockBehaviour.Properties stone(final float destroyTime, final float explosionResistance) {
        return properties(destroyTime, explosionResistance, SoundType.STONE);
    }

    private static BlockBehaviour.Properties properties(
            final float destroyTime,
            final float explosionResistance,
            final SoundType soundType
    ) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(destroyTime, explosionResistance)
                .sound(soundType);
    }

    private static BlockBehaviour.Properties machineWood() {
        return machineWood(6.0F, 12.0F);
    }

    private static BlockBehaviour.Properties machineWood(final float destroyTime, final float explosionResistance) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(destroyTime, explosionResistance)
                .sound(SoundType.WOOD)
                .noOcclusion();
    }

    private ModBlocks() {
    }
}
