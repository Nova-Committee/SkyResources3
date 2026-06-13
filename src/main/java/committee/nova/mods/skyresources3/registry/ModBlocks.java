package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.block.AqueousConcentratorBlock;
import committee.nova.mods.skyresources3.block.AqueousDeconcentratorBlock;
import committee.nova.mods.skyresources3.block.BlazePowderBlock;
import committee.nova.mods.skyresources3.block.CactusFruitNeedleBlock;
import committee.nova.mods.skyresources3.block.CombustionCollectorBlock;
import committee.nova.mods.skyresources3.block.CombustionControllerBlock;
import committee.nova.mods.skyresources3.block.CrucibleBlock;
import committee.nova.mods.skyresources3.block.CrucibleInserterBlock;
import committee.nova.mods.skyresources3.block.DarkMatterWarperBlock;
import committee.nova.mods.skyresources3.block.DirtFurnaceBlock;
import committee.nova.mods.skyresources3.block.DryCactusBlock;
import committee.nova.mods.skyresources3.block.EndPortalCoreBlock;
import committee.nova.mods.skyresources3.block.FluidDropperBlock;
import committee.nova.mods.skyresources3.block.FusionTableBlock;
import committee.nova.mods.skyresources3.block.FreezerBlock;
import committee.nova.mods.skyresources3.block.LifeInfuserBlock;
import committee.nova.mods.skyresources3.block.LifeInjectorBlock;
import committee.nova.mods.skyresources3.block.MachineCasingBlock;
import committee.nova.mods.skyresources3.block.MagmafiedStoneBlock;
import committee.nova.mods.skyresources3.block.QuickDropperBlock;
import committee.nova.mods.skyresources3.block.RockCleanerBlock;
import committee.nova.mods.skyresources3.block.RockCrusherBlock;
import committee.nova.mods.skyresources3.block.SilverfishDisruptorBlock;
import committee.nova.mods.skyresources3.block.WildlifeAttractorBlock;
import committee.nova.mods.skyresources3.machine.MachineVariant;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Skyresources3.MODID);

    public static final DeferredBlock<Block> COMPRESSED_COAL_BLOCK = BLOCKS.registerSimpleBlock(
            "compressed_coal_block",
            () -> stone(6.0F, 6.0F)
    );
    public static final DeferredBlock<Block> COAL_INFUSED_BLOCK = BLOCKS.registerSimpleBlock(
            "coal_infused_block",
            () -> stone(5.0F, 6.0F)
    );
    public static final DeferredBlock<Block> SANDY_NETHERRACK = BLOCKS.registerSimpleBlock(
            "sandy_netherrack",
            () -> stone(0.4F, 0.4F)
    );
    public static final DeferredBlock<Block> PETRIFIED_WOOD = BLOCKS.registerSimpleBlock(
            "petrified_wood",
            () -> stone(2.0F, 10.0F)
    );
    public static final DeferredBlock<Block> PETRIFIED_PLANKS = BLOCKS.registerSimpleBlock(
            "petrified_planks",
            () -> properties(2.0F, 6.0F, SoundType.WOOD)
    );
    public static final DeferredBlock<MagmafiedStoneBlock> MAGMAFIED_STONE = BLOCKS.registerBlock(
            "magmafied_stone",
            MagmafiedStoneBlock::new,
            () -> stone(1.5F, 6.0F)
    );
    public static final DeferredBlock<Block> HEAVY_SNOW = BLOCKS.registerSimpleBlock(
            "heavy_snow",
            () -> properties(0.3F, 0.3F, SoundType.SNOW)
    );
    public static final DeferredBlock<BlazePowderBlock> BLAZE_POWDER_BLOCK = BLOCKS.registerBlock(
            "blaze_powder_block",
            BlazePowderBlock::new,
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(0.5F, 0.5F)
                    .sound(SoundType.GRAVEL)
    );
    public static final DeferredBlock<Block> DARK_MATTER_BLOCK = BLOCKS.registerSimpleBlock(
            "dark_matter_block",
            () -> stone(5.0F, 10.0F)
    );
    public static final DeferredBlock<Block> LIGHT_MATTER_BLOCK = BLOCKS.registerSimpleBlock(
            "light_matter_block",
            () -> stone(5.0F, 10.0F)
    );
    public static final DeferredBlock<Block> ALCHEMICAL_GLASS = BLOCKS.registerSimpleBlock(
            "alchemical_glass",
            () -> properties(0.3F, 0.3F, SoundType.GLASS).noOcclusion()
    );
    public static final DeferredBlock<LiquidBlock> CRYSTAL_FLUID = BLOCKS.registerBlock(
            "crystal_fluid",
            properties -> new LiquidBlock(ModFluids.CRYSTAL_FLUID.get(), properties),
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .replaceable()
                    .noCollision()
                    .strength(100.0F)
                    .liquid()
                    .pushReaction(PushReaction.DESTROY)
    );
    public static final DeferredBlock<FusionTableBlock> FUSION_TABLE = BLOCKS.registerBlock(
            "fusion_table",
            FusionTableBlock::new,
            () -> machineWood(3.0F, 10.0F)
    );
    public static final DeferredBlock<DirtFurnaceBlock> DIRT_FURNACE = BLOCKS.registerBlock(
            "dirt_furnace",
            DirtFurnaceBlock::new,
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.5F, 0.5F)
                    .sound(SoundType.GRAVEL)
    );
    public static final DeferredBlock<QuickDropperBlock> QUICK_DROPPER = BLOCKS.registerBlock(
            "quick_dropper",
            QuickDropperBlock::new,
            () -> properties(6.0F, 12.0F, SoundType.METAL)
    );
    public static final DeferredBlock<DarkMatterWarperBlock> DARK_MATTER_WARPER = BLOCKS.registerBlock(
            "dark_matter_warper",
            DarkMatterWarperBlock::new,
            () -> properties(8.0F, 12.0F, SoundType.METAL).noOcclusion()
    );
    public static final DeferredBlock<EndPortalCoreBlock> END_PORTAL_CORE = BLOCKS.registerBlock(
            "end_portal_core",
            EndPortalCoreBlock::new,
            () -> properties(6.0F, 12.0F, SoundType.METAL).noOcclusion()
    );
    public static final DeferredBlock<SilverfishDisruptorBlock> SILVERFISH_DISRUPTOR = BLOCKS.registerBlock(
            "silverfish_disruptor",
            SilverfishDisruptorBlock::new,
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(0.5F, 0.5F)
                    .sound(SoundType.GLASS)
                    .noOcclusion()
    );
    public static final DeferredBlock<FluidDropperBlock> FLUID_DROPPER = BLOCKS.registerBlock(
            "fluid_dropper",
            FluidDropperBlock::new,
            () -> properties(2.0F, 12.0F, SoundType.STONE)
    );
    public static final DeferredBlock<CrucibleBlock> CRUCIBLE = BLOCKS.registerBlock(
            "crucible",
            CrucibleBlock::new,
            () -> properties(2.0F, 12.0F, SoundType.STONE).noOcclusion()
    );
    public static final DeferredBlock<CrucibleInserterBlock> CRUCIBLE_INSERTER = BLOCKS.registerBlock(
            "crucible_inserter",
            CrucibleInserterBlock::new,
            () -> properties(2.0F, 12.0F, SoundType.STONE).noOcclusion()
    );
    public static final DeferredBlock<RockCrusherBlock> ROCK_CRUSHER = BLOCKS.registerBlock(
            "rock_crusher",
            RockCrusherBlock::new,
            () -> properties(6.0F, 12.0F, SoundType.METAL)
    );
    public static final DeferredBlock<RockCleanerBlock> ROCK_CLEANER = BLOCKS.registerBlock(
            "rock_cleaner",
            RockCleanerBlock::new,
            () -> properties(6.0F, 12.0F, SoundType.METAL)
    );
    public static final DeferredBlock<AqueousConcentratorBlock> AQUEOUS_CONCENTRATOR = BLOCKS.registerBlock(
            "aqueous_concentrator",
            AqueousConcentratorBlock::new,
            () -> properties(2.0F, 12.0F, SoundType.METAL)
    );
    public static final DeferredBlock<AqueousDeconcentratorBlock> AQUEOUS_DECONCENTRATOR = BLOCKS.registerBlock(
            "aqueous_deconcentrator",
            AqueousDeconcentratorBlock::new,
            () -> properties(2.0F, 12.0F, SoundType.METAL)
    );
    public static final DeferredBlock<WildlifeAttractorBlock> WILDLIFE_ATTRACTOR = BLOCKS.registerBlock(
            "wildlife_attractor",
            WildlifeAttractorBlock::new,
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GRASS)
                    .strength(2.0F, 12.0F)
                    .sound(SoundType.GRASS)
    );
    public static final Map<MachineVariant, DeferredBlock<MachineCasingBlock>> MACHINE_CASINGS =
            registerMachineCasings();
    public static final DeferredBlock<CombustionCollectorBlock> COMBUSTION_COLLECTOR = BLOCKS.registerBlock(
            "combustion_collector",
            CombustionCollectorBlock::new,
            () -> properties(6.0F, 12.0F, SoundType.METAL)
    );
    public static final DeferredBlock<CombustionControllerBlock> COMBUSTION_CONTROLLER = BLOCKS.registerBlock(
            "combustion_controller",
            CombustionControllerBlock::new,
            () -> properties(6.0F, 12.0F, SoundType.METAL)
    );
    public static final DeferredBlock<FreezerBlock> MINI_FREEZER = BLOCKS.registerBlock(
            "mini_freezer",
            properties -> new FreezerBlock(FreezerBlock.Tier.MINI, properties),
            () -> properties(0.5F, 0.5F, SoundType.SNOW).noOcclusion()
    );
    public static final DeferredBlock<FreezerBlock> IRON_FREEZER = BLOCKS.registerBlock(
            "iron_freezer",
            properties -> new FreezerBlock(FreezerBlock.Tier.IRON, properties),
            () -> properties(2.0F, 2.0F, SoundType.METAL).noOcclusion()
    );
    public static final DeferredBlock<FreezerBlock> LIGHT_FREEZER = BLOCKS.registerBlock(
            "light_freezer",
            properties -> new FreezerBlock(FreezerBlock.Tier.LIGHT, properties),
            () -> properties(8.0F, 12.0F, SoundType.METAL).noOcclusion()
    );
    public static final DeferredBlock<LifeInfuserBlock> LIFE_INFUSER = BLOCKS.registerBlock(
            "life_infuser",
            LifeInfuserBlock::new,
            () -> machineWood()
    );
    public static final DeferredBlock<LifeInjectorBlock> LIFE_INJECTOR = BLOCKS.registerBlock(
            "life_injector",
            LifeInjectorBlock::new,
            () -> machineWood()
    );
    public static final DeferredBlock<CactusFruitNeedleBlock> CACTUS_FRUIT_NEEDLE = BLOCKS.registerBlock(
            "cactus_fruit_needle",
            CactusFruitNeedleBlock::new,
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.5F, 0.5F)
                    .sound(SoundType.GRASS)
                    .noOcclusion()
    );
    public static final DeferredBlock<DryCactusBlock> DRY_CACTUS = BLOCKS.registerBlock(
            "dry_cactus",
            DryCactusBlock::new,
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.5F, 0.5F)
                    .sound(SoundType.GRASS)
                    .noOcclusion()
    );

    public static void register(final IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
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

    private static Map<MachineVariant, DeferredBlock<MachineCasingBlock>> registerMachineCasings() {
        final EnumMap<MachineVariant, DeferredBlock<MachineCasingBlock>> casings = new EnumMap<>(MachineVariant.class);
        for (final MachineVariant variant : MachineVariant.values()) {
            casings.put(variant, BLOCKS.registerBlock(
                    variant.registryName("machine_casing"),
                    properties -> new MachineCasingBlock(variant, properties),
                    () -> casingProperties(variant)
            ));
        }
        return Collections.unmodifiableMap(casings);
    }

    private static BlockBehaviour.Properties casingProperties(final MachineVariant variant) {
        final SoundType sound = variant == MachineVariant.WOODEN ? SoundType.WOOD : SoundType.METAL;
        return BlockBehaviour.Properties.of()
                .mapColor(variant == MachineVariant.WOODEN ? MapColor.WOOD : MapColor.STONE)
                .strength(2.0F, 12.0F)
                .sound(sound);
    }

    private ModBlocks() {
    }
}
