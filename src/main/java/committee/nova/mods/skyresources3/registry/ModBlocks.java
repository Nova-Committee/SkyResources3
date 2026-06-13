package committee.nova.mods.skyresources3.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.block.CactusFruitNeedleBlock;
import committee.nova.mods.skyresources3.block.DryCactusBlock;
import committee.nova.mods.skyresources3.block.FusionTableBlock;
import committee.nova.mods.skyresources3.block.LifeInfuserBlock;
import committee.nova.mods.skyresources3.block.LifeInjectorBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
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
    public static final DeferredBlock<Block> MAGMAFIED_STONE = BLOCKS.registerSimpleBlock(
            "magmafied_stone",
            () -> stone(1.5F, 6.0F)
    );
    public static final DeferredBlock<Block> HEAVY_SNOW = BLOCKS.registerSimpleBlock(
            "heavy_snow",
            () -> properties(0.3F, 0.3F, SoundType.SNOW)
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
    public static final DeferredBlock<FusionTableBlock> FUSION_TABLE = BLOCKS.registerBlock(
            "fusion_table",
            FusionTableBlock::new,
            () -> machineWood(3.0F, 10.0F)
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

    private ModBlocks() {
    }
}
