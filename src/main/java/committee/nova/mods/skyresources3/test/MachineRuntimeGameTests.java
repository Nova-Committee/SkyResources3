package committee.nova.mods.skyresources3.test;

import java.util.function.Function;
import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.common.block.CombustionControllerBlock;
import committee.nova.mods.skyresources3.common.block.entity.AqueousMachineBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.CombustionCollectorBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.CombustionControllerBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.CrucibleBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.CrucibleInserterBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.DarkMatterWarperBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.DirtFurnaceBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.EndPortalCoreBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.FluidDropperBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.FreezerBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.FusionTableBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.LifeInfuserBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.LifeInjectorBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.MachineCasingBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.QuickDropperBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.RockCleanerBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.RockCrusherBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.StandaloneMachineBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.WildlifeAttractorBlockEntity;
import committee.nova.mods.skyresources3.common.item.CombustionHeaterItem;
import committee.nova.mods.skyresources3.common.item.CondenserItem;
import committee.nova.mods.skyresources3.common.item.DirtyGemItem;
import committee.nova.mods.skyresources3.common.item.HeatProviderItem;
import committee.nova.mods.skyresources3.common.item.OreAlchemyDustItem;
import committee.nova.mods.skyresources3.common.menu.CombustionControllerMenu;
import committee.nova.mods.skyresources3.core.machine.CasingType;
import committee.nova.mods.skyresources3.core.machine.CombustionHeaterType;
import committee.nova.mods.skyresources3.init.event.MachineCasingEvents;
import committee.nova.mods.skyresources3.init.data.SkyResources3MaterialSeeds;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import committee.nova.mods.skyresources3.init.registry.ModCreativeTabs;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import committee.nova.mods.skyresources3.init.registry.ModFluids;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemResource;
import committee.nova.mods.skyresources3.common.compat.transfer.transaction.Transaction;

public final class MachineRuntimeGameTests {
    private static final BlockPos CASING_POS = new BlockPos(2, 1, 2);
    private static final BlockPos SOURCE_POS = CASING_POS.above();
    private static final BlockPos OUTPUT_POS = CASING_POS.below();
    private static final BlockPos CHAMBER_POS = CASING_POS.above();
    private static final BlockPos CONTROLLER_POS = CHAMBER_POS.north();
    private static final BlockPos COLLECTOR_POS = CHAMBER_POS.east();
    private static final BlockPos REDSTONE_POS = CASING_POS.west();
    private static final BlockPos FLUID_DROPPER_POS = new BlockPos(6, 2, 2);
    private static final BlockPos FLUID_SOURCE_POS = FLUID_DROPPER_POS.below();
    private static final BlockPos CRUCIBLE_POS = FLUID_DROPPER_POS.above();
    private static final double ITEM_ASSERT_RADIUS = 2.0D;
    private static final int EXPECTED_OUTPUT_COUNT = 1;
    private static final int DIRT_RECIPE_HEAT = 100;
    private static final int RED_SAND_RECIPE_HEAT = 200;
    private static final int PRIMUS_ALCHEMICAL_DUST_RECIPE_HEAT = 335;
    private static final int PRIMUS_ALCHEMICAL_DUST_OUTPUT_COUNT = 5;

    public static void combustionHeaterEmbedsAsTypeId(final GameTestHelper helper) {
        helper.killAllEntities();
        helper.setBlock(CASING_POS, ModBlocks.MACHINE_CASING.get());
        final MachineCasingBlockEntity casing = machineCasingAt(helper, CASING_POS);
        final Player player = GameTestAssertions.makeMockPlayer(helper, GameType.CREATIVE);
        final ItemStack heater = CombustionHeaterItem.forType(ModDataPackRegistries.IRON_COMBUSTION_HEATER);

        helper.assertTrue(casing.installHeater(heater, player), "Combustion heater should install");
        helper.assertTrue(casing.heater().isEmpty(), "Embedded combustion heater should not be stored as an item");
        helper.assertTrue(
                ModDataPackRegistries.combustionHeaterTypeId(ModDataPackRegistries.IRON_COMBUSTION_HEATER)
                        .equals(casing.combustionHeaterTypeId()),
                "Embedded combustion heater type should be persisted on the casing"
        );

        final ItemStack removed = casing.removeHeater();
        helper.assertTrue(removed.is(ModItems.COMBUSTION_HEATER.get()), "Removed heater should be the single heater block");
        helper.assertTrue(
                ModDataPackRegistries.combustionHeaterTypeId(ModDataPackRegistries.IRON_COMBUSTION_HEATER)
                        .equals(CombustionHeaterItem.combustionHeaterTypeId(removed)),
                "Removed heater should keep its type component"
        );
        helper.succeed();
    }

    public static void shiftRightClickRemovesEmbeddedCombustionHeaterWithHeldItem(final GameTestHelper helper) {
        helper.killAllEntities();
        helper.setBlock(CASING_POS, ModBlocks.MACHINE_CASING.get());
        final MachineCasingBlockEntity casing = machineCasingAt(helper, CASING_POS);
        final Player player = GameTestAssertions.makeMockPlayer(helper, GameType.SURVIVAL);
        final ItemStack installed = CombustionHeaterItem.forType(ModDataPackRegistries.IRON_COMBUSTION_HEATER);

        helper.assertTrue(casing.installHeater(installed, player), "Combustion heater should install");
        player.setShiftKeyDown(true);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIRT));

        final PlayerInteractEvent.RightClickBlock event = new PlayerInteractEvent.RightClickBlock(
                player,
                InteractionHand.MAIN_HAND,
                helper.absolutePos(CASING_POS),
                hitResult(helper, CASING_POS)
        );
        MachineCasingEvents.onRightClickBlock(event);

        helper.assertTrue(event.isCanceled(), "Shift right click should be handled before held item use");
        helper.assertTrue(!casing.hasHeater(), "Machine casing should no longer have an embedded heater");
        helper.assertTrue(
                GameTestAssertions.inventoryContains(
                        player.getInventory(),
                        stack -> stack.is(ModItems.COMBUSTION_HEATER.get())
                                && ModDataPackRegistries.combustionHeaterTypeId(
                                        ModDataPackRegistries.IRON_COMBUSTION_HEATER
                                ).equals(CombustionHeaterItem.combustionHeaterTypeId(stack))
                ),
                "Removed heater should be returned to the player with its type component"
        );
        helper.succeed();
    }

    public static void heatProviderEmbedsAsTypeIdAndProvidesHeat(final GameTestHelper helper) {
        helper.killAllEntities();
        helper.setBlock(CASING_POS, ModBlocks.MACHINE_CASING.get());
        final MachineCasingBlockEntity casing = machineCasingAt(helper, CASING_POS);
        casing.setCasingType(ModDataPackRegistries.IRON);
        final Player player = GameTestAssertions.makeMockPlayer(helper, GameType.CREATIVE);
        final ItemStack provider = HeatProviderItem.forType(ModDataPackRegistries.IRON_HEAT_PROVIDER);

        helper.assertTrue(casing.installHeater(provider, player), "Heat provider should install");
        helper.assertTrue(casing.heater().isEmpty(), "Embedded heat provider should not be stored as an item");
        helper.assertTrue(
                ModDataPackRegistries.heatProviderTypeId(ModDataPackRegistries.IRON_HEAT_PROVIDER)
                        .equals(casing.heatProviderTypeId()),
                "Embedded heat provider type should be persisted on the casing"
        );

        casing.setStackInSlot(MachineCasingBlockEntity.FUEL_SLOT, new ItemStack(Items.COAL));
        casing.serverTick(helper.getLevel());

        GameTestAssertions.assertValueEqual(
                helper,
                10,
                casing.heatSourceValue(),
                "Iron heat provider heat source value"
        );
        final ItemStack removed = casing.removeHeater();
        helper.assertTrue(removed.is(ModItems.HEAT_PROVIDER.get()), "Removed provider should be the single provider block");
        helper.assertTrue(
                ModDataPackRegistries.heatProviderTypeId(ModDataPackRegistries.IRON_HEAT_PROVIDER)
                        .equals(HeatProviderItem.heatProviderTypeId(removed)),
                "Removed provider should keep its type component"
        );
        helper.succeed();
    }

    public static void standaloneMachineBlocksPersistTypeComponents(final GameTestHelper helper) {
        helper.killAllEntities();
        assertStandaloneMachineType(
                helper,
                new BlockPos(1, 1, 1),
                ModBlocks.COMBUSTION_HEATER.get(),
                CombustionHeaterItem.forType(ModDataPackRegistries.DARK_MATTER_COMBUSTION_HEATER),
                ModDataPackRegistries.combustionHeaterTypeId(ModDataPackRegistries.DARK_MATTER_COMBUSTION_HEATER),
                CombustionHeaterItem::combustionHeaterTypeId,
                "Standalone combustion heater"
        );
        assertStandaloneMachineType(
                helper,
                new BlockPos(2, 1, 1),
                ModBlocks.HEAT_PROVIDER.get(),
                HeatProviderItem.forType(ModDataPackRegistries.END_STONE_HEAT_PROVIDER),
                ModDataPackRegistries.heatProviderTypeId(ModDataPackRegistries.END_STONE_HEAT_PROVIDER),
                HeatProviderItem::heatProviderTypeId,
                "Standalone heat provider"
        );
        assertStandaloneMachineType(
                helper,
                new BlockPos(3, 1, 1),
                ModBlocks.CONDENSER.get(),
                CondenserItem.forType(ModDataPackRegistries.LIGHT_MATTER_CONDENSER),
                ModDataPackRegistries.condenserTypeId(ModDataPackRegistries.LIGHT_MATTER_CONDENSER),
                CondenserItem::condenserTypeId,
                "Standalone condenser"
        );
        helper.succeed();
    }

    public static void creativeTabIncludesBuiltInMaterialVariants(final GameTestHelper helper) {
        final var registries = helper.getLevel().registryAccess();
        final CreativeModeTab.ItemDisplayParameters parameters = new CreativeModeTab.ItemDisplayParameters(
                FeatureFlags.DEFAULT_FLAGS,
                false,
                registries
        );
        final CreativeModeTab tab = ModCreativeTabs.MAIN.get();
        tab.buildContents(parameters);

        final long oreDustCount = tab.getDisplayItems().stream()
                .filter(stack -> stack.is(ModItems.ORE_ALCHEMICAL_DUST.get()))
                .map(OreAlchemyDustItem::oreAlchemyDustTypeId)
                .distinct()
                .count();
        final long dirtyGemCount = tab.getDisplayItems().stream()
                .filter(stack -> stack.is(ModItems.DIRTY_GEM.get()))
                .map(DirtyGemItem::dirtyGemTypeId)
                .distinct()
                .count();

        GameTestAssertions.assertValueEqual(
                helper,
                3,
                (int) oreDustCount,
                "Creative tab should only include ore alchemical dust variants with non-empty source tags"
        );
        GameTestAssertions.assertValueEqual(
                helper,
                5,
                (int) dirtyGemCount,
                "Creative tab should only include dirty gem variants with non-empty source tags"
        );
        helper.succeed();
    }

    public static void materialSeedColorsResolveByTypeId(final GameTestHelper helper) {
        GameTestAssertions.assertValueEqual(
                helper,
                0xD8AF93,
                SkyResources3MaterialSeeds.oreAlchemyDustColor(
                        ModDataPackRegistries.oreAlchemyDustTypeId(ModDataPackRegistries.IRON_ORE_ALCHEMY_DUST)
                ),
                "Built-in ore alchemical dust color should resolve from the type id"
        );
        GameTestAssertions.assertValueEqual(
                helper,
                0x12DB3A,
                SkyResources3MaterialSeeds.dirtyGemColor(
                        ModDataPackRegistries.dirtyGemTypeId(ModDataPackRegistries.EMERALD_DIRTY_GEM)
                ),
                "Built-in dirty gem color should resolve from the type id"
        );
        helper.succeed();
    }

    public static void condenserDropsOutputWhenNoHandlerExists(final GameTestHelper helper) {
        helper.killAllEntities();
        final MachineCasingBlockEntity casing = setupCopperCrystalFluidCondenser(helper);

        casing.serverTick(helper.getLevel());

        helper.assertTrue(helper.getBlockState(SOURCE_POS).isAir(), "Condenser should clear the source after output");
        GameTestAssertions.assertDroppedItemCount(
                helper,
                Items.COPPER_INGOT,
                EXPECTED_OUTPUT_COUNT,
                OUTPUT_POS,
                ITEM_ASSERT_RADIUS,
                "Dropped stack count should match condenser output"
        );
        helper.succeed();
    }

    public static void condenserKeepsSourceWhenOutputIsBlocked(final GameTestHelper helper) {
        helper.killAllEntities();
        helper.setBlock(OUTPUT_POS, Blocks.CHEST);
        fillContainer(helper, OUTPUT_POS, new ItemStack(Items.COBBLESTONE, 64));
        final MachineCasingBlockEntity casing = setupCopperCrystalFluidCondenser(helper);

        casing.serverTick(helper.getLevel());

        helper.assertTrue(
                helper.getBlockState(SOURCE_POS).is(ModBlocks.CRYSTAL_FLUID.get()),
                "Condenser should keep the source when output routing fails"
        );
        assertContainerItemCount(helper, OUTPUT_POS, Items.COPPER_INGOT, 0);
        GameTestAssertions.assertDroppedItemCount(
                helper,
                Items.COPPER_INGOT,
                0,
                OUTPUT_POS,
                ITEM_ASSERT_RADIUS,
                "Dropped stack count should match condenser output"
        );
        helper.succeed();
    }

    public static void condenserFuelSlotAcceptsOreAlchemyDustCatalyst(final GameTestHelper helper) {
        helper.killAllEntities();
        final MachineCasingBlockEntity casing = setupCopperCrystalFluidCondenser(helper);
        final ItemStack catalyst = OreAlchemyDustItem.forType(ModDataPackRegistries.COPPER_ORE_ALCHEMY_DUST);
        final ItemStack dataDrivenCatalyst = OreAlchemyDustItem.forType(
                ModDataPackRegistries.oreAlchemyDustTypeKey("tin")
        );

        helper.assertTrue(
                casing.mayPlaceInSlot(MachineCasingBlockEntity.FUEL_SLOT, catalyst),
                "Condenser catalyst slot should accept ore alchemical dust used by condenser recipes"
        );
        helper.assertTrue(
                casing.mayPlaceInSlot(MachineCasingBlockEntity.FUEL_SLOT, dataDrivenCatalyst),
                "Condenser catalyst slot should accept data-driven ore alchemical dust variants"
        );
        helper.assertTrue(
                !casing.mayPlaceInSlot(
                        MachineCasingBlockEntity.FUEL_SLOT,
                        new ItemStack(ModItems.PRIMUS_ALCHEMICAL_DUST.get())
                ),
                "Condenser catalyst slot should reject alchemical dust that is not used by a condenser recipe"
        );
        helper.succeed();
    }

    public static void condenserHopperBelowDoesNotExtractCatalyst(final GameTestHelper helper) {
        helper.killAllEntities();
        helper.setBlock(OUTPUT_POS, Blocks.HOPPER);
        final MachineCasingBlockEntity casing = setupCopperCrystalFluidCondenser(helper);
        final ItemStack catalyst = OreAlchemyDustItem.forType(ModDataPackRegistries.COPPER_ORE_ALCHEMY_DUST);
        casing.setStackInSlot(MachineCasingBlockEntity.FUEL_SLOT, catalyst.copyWithCount(2));

        final ResourceHandler<ItemResource> casingHandler = casing.getItemHandler();
        final int extracted = extract(casingHandler, MachineCasingBlockEntity.FUEL_SLOT, catalyst, 1);

        GameTestAssertions.assertValueEqual(
                helper,
                0,
                extracted,
                "External automation should not extract condenser catalyst"
        );
        GameTestAssertions.assertValueEqual(
                helper,
                2,
                casing.getStackInSlot(MachineCasingBlockEntity.FUEL_SLOT).getCount(),
                "Catalyst stack should remain before condenser processing"
        );

        casing.serverTick(helper.getLevel());

        assertContainerItemCount(helper, OUTPUT_POS, Items.COPPER_INGOT, EXPECTED_OUTPUT_COUNT);
        assertContainerItemCount(helper, OUTPUT_POS, ModItems.ORE_ALCHEMICAL_DUST.get(), 0);
        GameTestAssertions.assertValueEqual(
                helper,
                1,
                casing.getStackInSlot(MachineCasingBlockEntity.FUEL_SLOT).getCount(),
                "Only the condenser itself should consume one catalyst"
        );
        helper.succeed();
    }

    public static void condenserCasingExposesItemCapability(final GameTestHelper helper) {
        helper.killAllEntities();
        helper.setBlock(CASING_POS, ModBlocks.MACHINE_CASING.get());
        final MachineCasingBlockEntity casing = machineCasingAt(helper, CASING_POS);
        casing.setCasingType(ModDataPackRegistries.DARK_MATTER);
        final Player player = GameTestAssertions.makeMockPlayer(helper, GameType.CREATIVE);
        helper.assertTrue(
                casing.installHeater(CondenserItem.forType(ModDataPackRegistries.DARK_MATTER_CONDENSER), player),
                "Condenser should install into the machine casing"
        );

        final IItemHandler handler = casing.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).orElse(null);
        helper.assertTrue(handler != null, "Machine casing should expose an item capability");

        final ItemStack catalyst = OreAlchemyDustItem.forType(ModDataPackRegistries.COPPER_ORE_ALCHEMY_DUST);
        final ItemStack remainder = handler.insertItem(MachineCasingBlockEntity.FUEL_SLOT, catalyst.copy(), false);

        helper.assertTrue(remainder.isEmpty(), "Automation should insert a valid condenser catalyst");
        GameTestAssertions.assertValueEqual(
                helper,
                1,
                casing.getStackInSlot(MachineCasingBlockEntity.FUEL_SLOT).getCount(),
                "Inserted catalyst should land in the condenser fuel slot"
        );
        helper.succeed();
    }

    public static void condenserUsesCasingSourceAboveAndOutputBelow(final GameTestHelper helper) {
        helper.killAllEntities();
        helper.setBlock(OUTPUT_POS, Blocks.CHEST);
        final MachineCasingBlockEntity casing = setupCopperCrystalFluidCondenser(helper);

        GameTestAssertions.assertValueEqual(
                helper,
                MachineCasingBlockEntity.MACHINE_MODE_CONDENSER,
                casing.installedMachineMode(),
                "Casing should enter condenser mode after installing a condenser"
        );
        helper.assertTrue(!casing.usesCombustionChamber(), "Condenser should not require a combustion chamber");
        helper.assertTrue(
                !casing.hasValidMultiblock(helper.getLevel()),
                "Condenser should not depend on the combustion multiblock validation path"
        );

        casing.serverTick(helper.getLevel());

        helper.assertTrue(helper.getBlockState(SOURCE_POS).isAir(), "Condenser should consume the source directly above it");
        assertContainerItemCount(helper, OUTPUT_POS, Items.COPPER_INGOT, EXPECTED_OUTPUT_COUNT);
        helper.succeed();
    }

    public static void fluidDropperPullsFromCrucibleAndPlacesSourceBelow(final GameTestHelper helper) {
        helper.killAllEntities();
        helper.setBlock(FLUID_DROPPER_POS, Blocks.AIR);
        helper.setBlock(CRUCIBLE_POS, Blocks.AIR);
        helper.setBlock(FLUID_SOURCE_POS, Blocks.AIR);
        helper.setBlock(FLUID_DROPPER_POS, ModBlocks.FLUID_DROPPER.get());
        helper.setBlock(CRUCIBLE_POS, ModBlocks.CRUCIBLE.get());

        final CrucibleBlockEntity crucible = crucibleAt(helper, CRUCIBLE_POS);
        final FluidDropperBlockEntity dropper = fluidDropperAt(helper, FLUID_DROPPER_POS);
        final IFluidHandler crucibleHandler =
                crucible.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN).orElse(null);

        helper.assertTrue(crucibleHandler != null, "Crucible should expose a fluid capability");
        GameTestAssertions.assertValueEqual(
                helper,
                1000,
                crucibleHandler.fill(
                        new FluidStack(ModFluids.CRYSTAL_FLUID.get(), 1000),
                        IFluidHandler.FluidAction.EXECUTE
                ),
                "Crucible should accept one bucket of crystal fluid for dropper extraction"
        );

        dropper.serverTick(helper.getLevel());

        helper.assertTrue(
                helper.getBlockState(FLUID_SOURCE_POS).is(ModBlocks.CRYSTAL_FLUID.get()),
                "Fluid dropper should place a crystal fluid source below itself; actual state="
                        + helper.getBlockState(FLUID_SOURCE_POS)
                        + ", crucible amount=" + crucible.getFluidHandler().getFluidInTank(0).getAmount()
                        + ", dropper amount=" + dropper.getFluidHandler().getFluidInTank(0).getAmount()
        );
        GameTestAssertions.assertValueEqual(
                helper,
                0,
                crucible.getFluidHandler().getFluidInTank(0).getAmount(),
                "Crucible should lose the transferred bucket"
        );
        GameTestAssertions.assertValueEqual(
                helper,
                0,
                dropper.getFluidHandler().getFluidInTank(0).getAmount(),
                "Fluid dropper should empty its tank after placing the source"
        );
        helper.succeed();
    }

    public static void forgeCapabilityMatrixCoversMigratedMachines(final GameTestHelper helper) {
        helper.killAllEntities();
        assertCapabilityExposure(
                helper,
                new BlockPos(1, 1, 6),
                ModBlocks.DIRT_FURNACE.get(),
                DirtFurnaceBlockEntity.class,
                true,
                false,
                false,
                "Dirt furnace"
        );
        assertCapabilityExposure(
                helper,
                new BlockPos(2, 1, 6),
                ModBlocks.QUICK_DROPPER.get(),
                QuickDropperBlockEntity.class,
                true,
                false,
                false,
                "Quick dropper"
        );
        assertCapabilityExposure(
                helper,
                new BlockPos(3, 1, 6),
                ModBlocks.CRUCIBLE_INSERTER.get(),
                CrucibleInserterBlockEntity.class,
                true,
                false,
                false,
                "Crucible inserter"
        );
        assertCapabilityExposure(
                helper,
                new BlockPos(4, 1, 6),
                ModBlocks.DARK_MATTER_WARPER.get(),
                DarkMatterWarperBlockEntity.class,
                true,
                false,
                false,
                "Dark matter warper"
        );
        assertCapabilityExposure(
                helper,
                new BlockPos(5, 1, 6),
                ModBlocks.END_PORTAL_CORE.get(),
                EndPortalCoreBlockEntity.class,
                true,
                false,
                false,
                "End portal core"
        );
        assertCapabilityExposure(
                helper,
                new BlockPos(6, 1, 6),
                ModBlocks.COMBUSTION_COLLECTOR.get(),
                CombustionCollectorBlockEntity.class,
                true,
                false,
                false,
                "Combustion collector"
        );
        assertCapabilityExposure(
                helper,
                new BlockPos(7, 1, 6),
                ModBlocks.COMBUSTION_CONTROLLER.get(),
                CombustionControllerBlockEntity.class,
                true,
                false,
                false,
                "Combustion controller"
        );
        assertCapabilityExposure(
                helper,
                new BlockPos(8, 1, 6),
                ModBlocks.MINI_FREEZER.get(),
                FreezerBlockEntity.class,
                true,
                false,
                false,
                "Mini freezer"
        );
        assertCapabilityExposure(
                helper,
                new BlockPos(1, 1, 8),
                ModBlocks.FUSION_TABLE.get(),
                FusionTableBlockEntity.class,
                true,
                false,
                false,
                "Fusion table"
        );
        assertCapabilityExposure(
                helper,
                new BlockPos(2, 1, 8),
                ModBlocks.LIFE_INFUSER.get(),
                LifeInfuserBlockEntity.class,
                true,
                false,
                false,
                "Life infuser"
        );
        assertCapabilityExposure(
                helper,
                new BlockPos(3, 1, 8),
                ModBlocks.LIFE_INJECTOR.get(),
                LifeInjectorBlockEntity.class,
                true,
                false,
                false,
                "Life injector"
        );
        assertCapabilityExposure(
                helper,
                new BlockPos(4, 1, 8),
                ModBlocks.ROCK_CRUSHER.get(),
                RockCrusherBlockEntity.class,
                true,
                false,
                true,
                "Rock crusher"
        );
        assertCapabilityExposure(
                helper,
                new BlockPos(5, 1, 8),
                ModBlocks.ROCK_CLEANER.get(),
                RockCleanerBlockEntity.class,
                true,
                true,
                true,
                "Rock cleaner"
        );
        assertCapabilityExposure(
                helper,
                new BlockPos(6, 1, 8),
                ModBlocks.AQUEOUS_CONCENTRATOR.get(),
                AqueousMachineBlockEntity.class,
                true,
                true,
                true,
                "Aqueous concentrator"
        );
        assertCapabilityExposure(
                helper,
                new BlockPos(7, 1, 8),
                ModBlocks.WILDLIFE_ATTRACTOR.get(),
                WildlifeAttractorBlockEntity.class,
                true,
                true,
                true,
                "Wildlife attractor"
        );
        helper.succeed();
    }

    public static void alchemicalGlassKeepsTransparentBlockProperties(final GameTestHelper helper) {
        helper.killAllEntities();
        final Block block = ModBlocks.ALCHEMICAL_GLASS.get();
        final BlockState state = block.defaultBlockState();

        helper.assertTrue(block instanceof GlassBlock, "Alchemical glass should use the GlassBlock implementation");
        helper.assertTrue(!state.canOcclude(), "Alchemical glass should remain non-occluding");
        helper.assertTrue(
                block.skipRendering(state, state, Direction.NORTH),
                "Alchemical glass should cull faces against adjacent matching glass blocks"
        );
        helper.succeed();
    }

    public static void combustionControllerUsesFilterPriority(final GameTestHelper helper) {
        helper.killAllEntities();
        final CombustionRig rig = setupIronCombustionRig(helper, DIRT_RECIPE_HEAT);
        rig.controller().setStackInSlot(0, new ItemStack(Items.DIRT));
        rig.controller().setStackInSlot(1, new ItemStack(Items.WHEAT_SEEDS));
        spawnItem(helper, CHAMBER_POS, new ItemStack(ModItems.PLANT_MATTER.get(), 4));
        spawnItem(helper, CHAMBER_POS, new ItemStack(Items.DEAD_BUSH));
        spawnItem(helper, CHAMBER_POS, new ItemStack(Items.FLINT, 2));

        rig.controller().serverTick(helper.getLevel());

        assertCollectorItemCount(helper, rig.collector(), Items.DIRT, 1);
        assertCollectorItemCount(helper, rig.collector(), Items.WHEAT_SEEDS, 0);
        helper.assertItemEntityNotPresent(Items.WHEAT_SEEDS, CHAMBER_POS, ITEM_ASSERT_RADIUS);
        helper.succeed();
    }

    public static void combustionControllerWaitsForCooldown(final GameTestHelper helper) {
        helper.killAllEntities();
        final CombustionRig rig = setupIronCombustionRig(helper, RED_SAND_RECIPE_HEAT);
        rig.controller().setStackInSlot(0, new ItemStack(Items.DIRT));
        spawnItem(helper, CHAMBER_POS, new ItemStack(ModItems.PLANT_MATTER.get(), 8));

        rig.controller().serverTick(helper.getLevel());
        assertCollectorItemCount(helper, rig.collector(), Items.DIRT, 1);

        rig.controller().serverTick(helper.getLevel());
        assertCollectorItemCount(helper, rig.collector(), Items.DIRT, 1);
        GameTestAssertions.assertDroppedItemCount(
                helper,
                ModItems.PLANT_MATTER.get(),
                4,
                CHAMBER_POS,
                ITEM_ASSERT_RADIUS,
                "Controller should leave the second craft input until the cooldown expires"
        );

        for (int tick = 0; tick < Config.combustionControllerTicks; tick++) {
            rig.controller().serverTick(helper.getLevel());
        }

        assertCollectorItemCount(helper, rig.collector(), Items.DIRT, 2);
        GameTestAssertions.assertDroppedItemCount(
                helper,
                ModItems.PLANT_MATTER.get(),
                0,
                CHAMBER_POS,
                ITEM_ASSERT_RADIUS,
                "Controller should consume the remaining input after the default cooldown"
        );
        helper.succeed();
    }

    public static void combustionControllerStopsWhenPowered(final GameTestHelper helper) {
        helper.killAllEntities();
        final CombustionRig rig = setupIronCombustionRig(helper, DIRT_RECIPE_HEAT);
        rig.controller().setStackInSlot(0, new ItemStack(Items.DIRT));
        spawnItem(helper, CHAMBER_POS, new ItemStack(ModItems.PLANT_MATTER.get(), 4));
        helper.setBlock(CONTROLLER_POS.west(), Blocks.REDSTONE_BLOCK);

        rig.controller().serverTick(helper.getLevel());

        assertCollectorItemCount(helper, rig.collector(), Items.DIRT, 0);
        GameTestAssertions.assertDroppedItemCount(
                helper,
                ModItems.PLANT_MATTER.get(),
                4,
                CHAMBER_POS,
                ITEM_ASSERT_RADIUS,
                "Powered controller should leave chamber inputs untouched"
        );

        helper.setBlock(CONTROLLER_POS.west(), Blocks.AIR);
        rig.controller().serverTick(helper.getLevel());

        assertCollectorItemCount(helper, rig.collector(), Items.DIRT, 1);
        helper.succeed();
    }

    public static void combustionControllerRequiresBackFacingChamber(final GameTestHelper helper) {
        helper.killAllEntities();
        final CombustionRig rig = setupIronCombustionRig(helper, DIRT_RECIPE_HEAT);
        helper.setBlock(CONTROLLER_POS, ModBlocks.COMBUSTION_CONTROLLER.get()
                .defaultBlockState()
                .setValue(CombustionControllerBlock.FACING, Direction.SOUTH));
        final CombustionControllerBlockEntity controller = combustionControllerAt(helper, CONTROLLER_POS);
        controller.setStackInSlot(0, new ItemStack(Items.DIRT));
        spawnItem(helper, CHAMBER_POS, new ItemStack(ModItems.PLANT_MATTER.get(), 4));

        controller.serverTick(helper.getLevel());

        assertCollectorItemCount(helper, rig.collector(), Items.DIRT, 0);
        GameTestAssertions.assertDroppedItemCount(
                helper,
                ModItems.PLANT_MATTER.get(),
                4,
                CHAMBER_POS,
                ITEM_ASSERT_RADIUS,
                "Controller should not craft when its back is not facing the chamber"
        );

        helper.setBlock(CONTROLLER_POS, ModBlocks.COMBUSTION_CONTROLLER.get()
                .defaultBlockState()
                .setValue(CombustionControllerBlock.FACING, Direction.NORTH));
        final CombustionControllerBlockEntity corrected = combustionControllerAt(helper, CONTROLLER_POS);
        corrected.setStackInSlot(0, new ItemStack(Items.DIRT));
        corrected.serverTick(helper.getLevel());

        assertCollectorItemCount(helper, rig.collector(), Items.DIRT, 1);
        helper.succeed();
    }

    public static void combustionControllerBackFacesChamberFromEverySide(final GameTestHelper helper) {
        for (final Direction side : Direction.Plane.HORIZONTAL) {
            helper.killAllEntities();
            clearCombustionStructure(helper);
            final MachineCasingBlockEntity casing = setupCombustionCasing(
                    helper,
                    ModDataPackRegistries.IRON,
                    ModDataPackRegistries.IRON_COMBUSTION_HEATER
            );
            setCombustionShell(
                    helper,
                    Blocks.IRON_BLOCK.defaultBlockState(),
                    Blocks.IRON_BLOCK.defaultBlockState(),
                    Blocks.IRON_BLOCK.defaultBlockState(),
                    Blocks.IRON_BLOCK.defaultBlockState(),
                    Blocks.IRON_BLOCK.defaultBlockState()
            );
            casing.setStackInSlot(MachineCasingBlockEntity.FUEL_SLOT, new ItemStack(Items.COAL));
            warmCasing(helper, casing, DIRT_RECIPE_HEAT);

            final BlockPos controllerPos = CHAMBER_POS.relative(side);
            final Direction collectorSide = collectorSideFor(side);
            final BlockPos collectorPos = CHAMBER_POS.relative(collectorSide);
            helper.setBlock(collectorPos, ModBlocks.COMBUSTION_COLLECTOR.get());
            final CombustionCollectorBlockEntity collector = combustionCollectorAt(helper, collectorPos);
            helper.setBlock(controllerPos, ModBlocks.COMBUSTION_CONTROLLER.get()
                    .defaultBlockState()
                    .setValue(CombustionControllerBlock.FACING, side));
            final CombustionControllerBlockEntity controller = combustionControllerAt(helper, controllerPos);
            controller.setStackInSlot(0, new ItemStack(Items.DIRT));
            spawnItem(helper, CHAMBER_POS, new ItemStack(ModItems.PLANT_MATTER.get(), 4));

            controller.serverTick(helper.getLevel());

            assertCollectorItemCount(helper, collector, Items.DIRT, 1);
        }
        helper.succeed();
    }

    public static void combustionControllerFilterSlotsAreGhosts(final GameTestHelper helper) {
        helper.killAllEntities();
        helper.setBlock(CONTROLLER_POS, ModBlocks.COMBUSTION_CONTROLLER.get());
        final CombustionControllerBlockEntity controller = combustionControllerAt(helper, CONTROLLER_POS);
        final Player player = GameTestAssertions.makeMockPlayer(helper, GameType.SURVIVAL);
        final CombustionControllerMenu menu = new CombustionControllerMenu(
                0,
                player.getInventory(),
                controller
        );

        menu.setCarried(new ItemStack(Items.DIRT, 7));
        menu.clicked(0, 0, ClickType.PICKUP, player);

        assertControllerFilter(helper, controller, 0, Items.DIRT, 1);
        GameTestAssertions.assertValueEqual(
                helper,
                7,
                menu.getCarried().getCount(),
                "Carried stack should not be consumed"
        );

        menu.setCarried(ItemStack.EMPTY);
        menu.clicked(0, 0, ClickType.PICKUP, player);
        helper.assertTrue(controller.getStackInSlot(0).isEmpty(), "Empty cursor click should clear a ghost filter");

        player.getInventory().setItem(0, new ItemStack(Items.WHEAT_SEEDS, 4));
        menu.quickMoveStack(player, menuSlotIndex(menu, player.getInventory(), 0));

        assertControllerFilter(helper, controller, 0, Items.WHEAT_SEEDS, 1);
        GameTestAssertions.assertValueEqual(
                helper,
                4,
                player.getInventory().getItem(0).getCount(),
                "Shift-clicking from inventory should not move the real stack"
        );
        helper.succeed();
    }

    public static void combustionCollectorDropsOverflow(final GameTestHelper helper) {
        helper.killAllEntities();
        final CombustionRig rig = setupIronCombustionRig(helper, RED_SAND_RECIPE_HEAT);
        rig.collector().setStackInSlot(0, new ItemStack(Blocks.RED_SAND, 60));
        for (int slot = 1; slot < CombustionCollectorBlockEntity.SLOT_COUNT; slot++) {
            rig.collector().setStackInSlot(slot, new ItemStack(Items.COBBLESTONE, 64));
        }
        spawnItem(helper, CHAMBER_POS, new ItemStack(Blocks.SAND, 12));
        spawnItem(helper, CHAMBER_POS, new ItemStack(Items.RED_DYE));

        final boolean crafted = rig.casing().craftSingleForController(helper.getLevel(), output -> true);

        helper.assertTrue(crafted, "Combustion casing should craft the red sand recipe");
        GameTestAssertions.assertValueEqual(
                helper,
                64,
                rig.collector().getStackInSlot(0).getCount(),
                "Collector red sand slot count"
        );
        GameTestAssertions.assertDroppedItemCount(
                helper,
                Blocks.RED_SAND.asItem(),
                8,
                CHAMBER_POS,
                ITEM_ASSERT_RADIUS,
                "Overflowed combustion output should drop in the chamber"
        );
        helper.succeed();
    }

    public static void manualCombustionRoutesOutputsToCollector(final GameTestHelper helper) {
        helper.killAllEntities();
        final CombustionRig rig = setupIronCombustionRig(helper, DIRT_RECIPE_HEAT);
        spawnItem(helper, CHAMBER_POS, new ItemStack(ModItems.PLANT_MATTER.get(), 4));

        helper.setBlock(REDSTONE_POS, Blocks.REDSTONE_BLOCK);
        rig.casing().serverTick(helper.getLevel());

        assertCollectorItemCount(helper, rig.collector(), Items.DIRT, 1);
        GameTestAssertions.assertDroppedItemCount(
                helper,
                Items.DIRT,
                0,
                CHAMBER_POS,
                ITEM_ASSERT_RADIUS,
                "Manual combustion output should route into the combustion collector"
        );
        GameTestAssertions.assertDroppedItemCount(
                helper,
                ModItems.PLANT_MATTER.get(),
                0,
                CHAMBER_POS,
                ITEM_ASSERT_RADIUS,
                "Manual combustion should consume inputs when collector receives the output"
        );
        helper.succeed();
    }

    public static void manualCombustionPrefersMultiInputRecipe(final GameTestHelper helper) {
        helper.killAllEntities();
        final CombustionRig rig = setupIronCombustionRig(helper, PRIMUS_ALCHEMICAL_DUST_RECIPE_HEAT);
        spawnItem(helper, CHAMBER_POS, new ItemStack(Items.GUNPOWDER, 3));
        spawnItem(helper, CHAMBER_POS, new ItemStack(Items.BLAZE_POWDER, 2));
        spawnItem(helper, CHAMBER_POS, new ItemStack(Items.CHARCOAL));

        helper.setBlock(REDSTONE_POS, Blocks.REDSTONE_BLOCK);
        rig.casing().serverTick(helper.getLevel());

        assertCollectorItemCount(
                helper,
                rig.collector(),
                ModItems.PRIMUS_ALCHEMICAL_DUST.get(),
                PRIMUS_ALCHEMICAL_DUST_OUTPUT_COUNT
        );
        assertCollectorItemCount(helper, rig.collector(), Items.BLAZE_POWDER, 0);
        GameTestAssertions.assertDroppedItemCount(
                helper,
                Items.GUNPOWDER,
                0,
                CHAMBER_POS,
                ITEM_ASSERT_RADIUS,
                "Specific multi-input combustion recipe should consume all gunpowder"
        );
        helper.succeed();
    }

    public static void woodAndStoneCombustionHeatersRejectCollector(final GameTestHelper helper) {
        helper.killAllEntities();
        MachineCasingBlockEntity casing = setupCombustionCasing(
                helper,
                ModDataPackRegistries.IRON,
                ModDataPackRegistries.WOODEN_COMBUSTION_HEATER
        );
        setWoodCombustionShell(helper, Blocks.OAK_PLANKS.defaultBlockState());
        helper.setBlock(CHAMBER_POS.west(), ModBlocks.COMBUSTION_COLLECTOR.get());

        helper.assertTrue(
                !casing.hasValidMultiblock(helper.getLevel()),
                "Wooden combustion heater should reject combustion collectors"
        );

        casing.removeHeater();
        casing = setupCombustionCasing(
                helper,
                ModDataPackRegistries.IRON,
                ModDataPackRegistries.STONE_COMBUSTION_HEATER
        );
        setStoneCombustionShell(helper, Blocks.STONE.defaultBlockState());
        helper.setBlock(CHAMBER_POS.west(), ModBlocks.COMBUSTION_COLLECTOR.get());

        helper.assertTrue(
                !casing.hasValidMultiblock(helper.getLevel()),
                "Stone combustion heater should reject combustion collectors"
        );
        helper.succeed();
    }

    public static void woodAndStoneCombustionHeatersRejectSmartController(final GameTestHelper helper) {
        helper.killAllEntities();
        MachineCasingBlockEntity casing = setupCombustionCasing(
                helper,
                ModDataPackRegistries.IRON,
                ModDataPackRegistries.WOODEN_COMBUSTION_HEATER
        );
        setWoodCombustionShell(helper, Blocks.OAK_PLANKS.defaultBlockState());
        helper.setBlock(CHAMBER_POS.west(), ModBlocks.COMBUSTION_CONTROLLER.get()
                .defaultBlockState()
                .setValue(CombustionControllerBlock.FACING, Direction.WEST));

        helper.assertTrue(
                !casing.hasValidMultiblock(helper.getLevel()),
                "Wooden combustion heater should reject smart combustion controllers"
        );

        casing.removeHeater();
        casing = setupCombustionCasing(
                helper,
                ModDataPackRegistries.IRON,
                ModDataPackRegistries.STONE_COMBUSTION_HEATER
        );
        setStoneCombustionShell(helper, Blocks.STONE.defaultBlockState());
        helper.setBlock(CHAMBER_POS.west(), ModBlocks.COMBUSTION_CONTROLLER.get()
                .defaultBlockState()
                .setValue(CombustionControllerBlock.FACING, Direction.WEST));

        helper.assertTrue(
                !casing.hasValidMultiblock(helper.getLevel()),
                "Stone combustion heater should reject smart combustion controllers"
        );
        helper.succeed();
    }

    public static void woodenCombustionHeaterUsesWoodStructure(final GameTestHelper helper) {
        helper.killAllEntities();
        final MachineCasingBlockEntity casing = setupCombustionCasing(
                helper,
                ModDataPackRegistries.IRON,
                ModDataPackRegistries.WOODEN_COMBUSTION_HEATER
        );

        setWoodCombustionShell(helper, Blocks.OAK_TRAPDOOR.defaultBlockState());
        helper.assertTrue(
                casing.hasValidMultiblock(helper.getLevel()),
                "Wooden combustion heater should accept a wooden shell even inside an iron casing"
        );

        helper.setBlock(CHAMBER_POS.west(), Blocks.COBBLESTONE);
        helper.assertTrue(
                !casing.hasValidMultiblock(helper.getLevel()),
                "Wooden combustion heater should reject stone shell blocks"
        );
        helper.succeed();
    }

    public static void stoneCombustionHeaterRejectsAutomationBlocks(final GameTestHelper helper) {
        helper.killAllEntities();
        final MachineCasingBlockEntity casing = setupCombustionCasing(
                helper,
                ModDataPackRegistries.IRON,
                ModDataPackRegistries.STONE_COMBUSTION_HEATER
        );

        setStoneCombustionShell(helper, Blocks.STONE.defaultBlockState());
        helper.assertTrue(
                casing.hasValidMultiblock(helper.getLevel()),
                "Stone combustion heater should accept mixed stone and cobblestone shell blocks"
        );

        helper.setBlock(CHAMBER_POS.east(), ModBlocks.COMBUSTION_COLLECTOR.get());
        helper.assertTrue(
                !casing.hasValidMultiblock(helper.getLevel()),
                "Stone combustion heater should reject optional automation blocks"
        );
        helper.succeed();
    }

    public static void ironCombustionHeaterAcceptsMetalAutomationShell(final GameTestHelper helper) {
        helper.killAllEntities();
        final MachineCasingBlockEntity casing = setupCombustionCasing(
                helper,
                ModDataPackRegistries.WOODEN,
                ModDataPackRegistries.IRON_COMBUSTION_HEATER
        );

        setMetalAutomationShell(helper, ModBlocks.QUICK_DROPPER.get().defaultBlockState());
        helper.assertTrue(
                casing.hasValidMultiblock(helper.getLevel()),
                "Iron combustion heater should accept side automation and a top quick dropper"
        );

        helper.setBlock(CHAMBER_POS.above(), Blocks.IRON_TRAPDOOR);
        helper.assertTrue(
                casing.hasValidMultiblock(helper.getLevel()),
                "Iron combustion heater should accept an iron trapdoor as the top structure block"
        );
        helper.succeed();
    }

    public static void manualCombustionCraftsAfterStructureIsRestored(final GameTestHelper helper) {
        helper.killAllEntities();
        final MachineCasingBlockEntity casing = setupCombustionCasing(
                helper,
                ModDataPackRegistries.IRON,
                ModDataPackRegistries.IRON_COMBUSTION_HEATER
        );
        setStoneCombustionShell(helper, Blocks.STONE.defaultBlockState());
        casing.setStackInSlot(MachineCasingBlockEntity.FUEL_SLOT, new ItemStack(Items.COAL));
        warmCasing(helper, casing, DIRT_RECIPE_HEAT);

        helper.setBlock(CHAMBER_POS.west(), Blocks.AIR);
        spawnItem(helper, CHAMBER_POS, new ItemStack(ModItems.PLANT_MATTER.get(), 4));
        helper.assertTrue(
                !casing.hasValidMultiblock(helper.getLevel()),
                "Combustion structure should be invalid while one shell block is removed"
        );

        helper.setBlock(CHAMBER_POS.west(), Blocks.COBBLESTONE);
        helper.assertTrue(
                casing.hasValidMultiblock(helper.getLevel()),
                "Combustion structure should become valid again after the shell block is restored"
        );
        helper.setBlock(REDSTONE_POS, Blocks.REDSTONE_BLOCK);
        casing.serverTick(helper.getLevel());

        GameTestAssertions.assertDroppedItemCount(
                helper,
                Items.DIRT,
                1,
                CHAMBER_POS,
                ITEM_ASSERT_RADIUS,
                "Manual redstone pulse should craft the restored chamber input into dirt"
        );
        GameTestAssertions.assertDroppedItemCount(
                helper,
                ModItems.PLANT_MATTER.get(),
                0,
                CHAMBER_POS,
                ITEM_ASSERT_RADIUS,
                "Manual combustion should consume the plant matter input"
        );
        helper.succeed();
    }

    private static MachineCasingBlockEntity setupCopperCrystalFluidCondenser(final GameTestHelper helper) {
        helper.setBlock(CASING_POS, ModBlocks.MACHINE_CASING.get());
        helper.setBlock(SOURCE_POS, ModBlocks.CRYSTAL_FLUID.get());

        final MachineCasingBlockEntity casing = machineCasingAt(helper, CASING_POS);
        casing.setCasingType(ModDataPackRegistries.DARK_MATTER);
        final Player player = GameTestAssertions.makeMockPlayer(helper, GameType.CREATIVE);
        final boolean installed = casing.installHeater(
                CondenserItem.forType(ModDataPackRegistries.DARK_MATTER_CONDENSER),
                player
        );
        helper.assertTrue(installed, "Condenser should install into the machine casing");
        helper.assertTrue(
                ModDataPackRegistries.condenserTypeId(ModDataPackRegistries.DARK_MATTER_CONDENSER)
                        .equals(casing.condenserTypeId()),
                "Embedded condenser type should be persisted on the casing"
        );
        casing.setStackInSlot(
                MachineCasingBlockEntity.FUEL_SLOT,
                OreAlchemyDustItem.forType(ModDataPackRegistries.COPPER_ORE_ALCHEMY_DUST)
        );
        return casing;
    }

    private static MachineCasingBlockEntity setupCombustionCasing(
            final GameTestHelper helper,
            final ResourceKey<CasingType> casingType,
            final ResourceKey<CombustionHeaterType> heaterType
    ) {
        helper.setBlock(CASING_POS, ModBlocks.MACHINE_CASING.get());
        final MachineCasingBlockEntity casing = machineCasingAt(helper, CASING_POS);
        casing.setCasingType(casingType);
        final Player player = GameTestAssertions.makeMockPlayer(helper, GameType.CREATIVE);
        final boolean installed = casing.installHeater(CombustionHeaterItem.forType(heaterType), player);
        helper.assertTrue(installed, "Combustion heater should install into the machine casing");
        return casing;
    }

    private static CombustionRig setupIronCombustionRig(final GameTestHelper helper, final int targetHeat) {
        helper.setBlock(CASING_POS, ModBlocks.MACHINE_CASING.get());
        helper.setBlock(CONTROLLER_POS, ModBlocks.COMBUSTION_CONTROLLER.get()
                .defaultBlockState()
                .setValue(CombustionControllerBlock.FACING, Direction.NORTH));
        helper.setBlock(COLLECTOR_POS, ModBlocks.COMBUSTION_COLLECTOR.get());
        helper.setBlock(CHAMBER_POS.west(), Blocks.IRON_BLOCK);
        helper.setBlock(CHAMBER_POS.south(), Blocks.IRON_BLOCK);
        helper.setBlock(CHAMBER_POS.above(), Blocks.IRON_BLOCK);

        final MachineCasingBlockEntity casing = machineCasingAt(helper, CASING_POS);
        casing.setCasingType(ModDataPackRegistries.IRON);
        final Player player = GameTestAssertions.makeMockPlayer(helper, GameType.CREATIVE);
        final boolean installed = casing.installHeater(
                CombustionHeaterItem.forType(ModDataPackRegistries.IRON_COMBUSTION_HEATER),
                player
        );
        helper.assertTrue(installed, "Combustion heater should install into the machine casing");
        casing.setStackInSlot(MachineCasingBlockEntity.FUEL_SLOT, new ItemStack(Items.COAL));
        warmCasing(helper, casing, targetHeat);

        final CombustionControllerBlockEntity controller = combustionControllerAt(helper, CONTROLLER_POS);
        final CombustionCollectorBlockEntity collector = combustionCollectorAt(helper, COLLECTOR_POS);
        return new CombustionRig(casing, controller, collector);
    }

    private static void clearCombustionStructure(final GameTestHelper helper) {
        helper.setBlock(CASING_POS, Blocks.AIR);
        helper.setBlock(CHAMBER_POS, Blocks.AIR);
        helper.setBlock(CHAMBER_POS.above(), Blocks.AIR);
        helper.setBlock(REDSTONE_POS, Blocks.AIR);
        for (final Direction side : Direction.Plane.HORIZONTAL) {
            helper.setBlock(CHAMBER_POS.relative(side), Blocks.AIR);
        }
        helper.killAllEntities();
    }

    private static Direction collectorSideFor(final Direction controllerSide) {
        for (final Direction side : Direction.Plane.HORIZONTAL) {
            if (side != controllerSide) {
                return side;
            }
        }
        throw new IllegalArgumentException("Controller side must be horizontal");
    }

    private static void setWoodCombustionShell(final GameTestHelper helper, final BlockState topState) {
        setCombustionShell(
                helper,
                Blocks.OAK_PLANKS.defaultBlockState(),
                Blocks.SPRUCE_PLANKS.defaultBlockState(),
                Blocks.OAK_LOG.defaultBlockState(),
                Blocks.BIRCH_PLANKS.defaultBlockState(),
                topState
        );
    }

    private static void setStoneCombustionShell(final GameTestHelper helper, final BlockState topState) {
        setCombustionShell(
                helper,
                Blocks.STONE.defaultBlockState(),
                Blocks.COBBLESTONE.defaultBlockState(),
                Blocks.ANDESITE.defaultBlockState(),
                Blocks.COBBLESTONE.defaultBlockState(),
                topState
        );
    }

    private static void setMetalAutomationShell(final GameTestHelper helper, final BlockState topState) {
        setCombustionShell(
                helper,
                ModBlocks.COMBUSTION_CONTROLLER.get()
                        .defaultBlockState()
                        .setValue(CombustionControllerBlock.FACING, Direction.WEST),
                ModBlocks.COMBUSTION_COLLECTOR.get().defaultBlockState(),
                Blocks.STONE.defaultBlockState(),
                Blocks.COBBLESTONE.defaultBlockState(),
                topState
        );
    }

    private static void setCombustionShell(
            final GameTestHelper helper,
            final BlockState west,
            final BlockState east,
            final BlockState north,
            final BlockState south,
            final BlockState top
    ) {
        helper.setBlock(CHAMBER_POS.west(), west);
        helper.setBlock(CHAMBER_POS.east(), east);
        helper.setBlock(CHAMBER_POS.north(), north);
        helper.setBlock(CHAMBER_POS.south(), south);
        helper.setBlock(CHAMBER_POS.above(), top);
    }

    private static void warmCasing(
            final GameTestHelper helper,
            final MachineCasingBlockEntity casing,
            final int targetHeat
    ) {
        for (int tick = 0; tick < 80 && casing.currentHeat() < targetHeat; tick++) {
            casing.serverTick(helper.getLevel());
        }
        helper.assertTrue(casing.currentHeat() >= targetHeat, "Combustion casing should reach recipe heat");
    }

    private static void spawnItem(
            final GameTestHelper helper,
            final BlockPos relativePos,
            final ItemStack stack
    ) {
        final Vec3 position = helper.absoluteVec(Vec3.atCenterOf(relativePos));
        final ItemEntity entity = new ItemEntity(helper.getLevel(), position.x, position.y, position.z, stack.copy());
        helper.getLevel().addFreshEntity(entity);
    }

    private static CombustionControllerBlockEntity combustionControllerAt(
            final GameTestHelper helper,
            final BlockPos relativePos
    ) {
        final BlockEntity blockEntity = blockEntityAt(helper, relativePos);
        helper.assertTrue(
                blockEntity instanceof CombustionControllerBlockEntity,
                "Expected a combustion controller block entity"
        );
        return (CombustionControllerBlockEntity) blockEntity;
    }

    private static CombustionCollectorBlockEntity combustionCollectorAt(
            final GameTestHelper helper,
            final BlockPos relativePos
    ) {
        final BlockEntity blockEntity = blockEntityAt(helper, relativePos);
        helper.assertTrue(
                blockEntity instanceof CombustionCollectorBlockEntity,
                "Expected a combustion collector block entity"
        );
        return (CombustionCollectorBlockEntity) blockEntity;
    }

    private static CrucibleBlockEntity crucibleAt(final GameTestHelper helper, final BlockPos relativePos) {
        final BlockEntity blockEntity = blockEntityAt(helper, relativePos);
        helper.assertTrue(blockEntity instanceof CrucibleBlockEntity, "Expected a crucible block entity");
        return (CrucibleBlockEntity) blockEntity;
    }

    private static FluidDropperBlockEntity fluidDropperAt(final GameTestHelper helper, final BlockPos relativePos) {
        final BlockEntity blockEntity = blockEntityAt(helper, relativePos);
        helper.assertTrue(blockEntity instanceof FluidDropperBlockEntity, "Expected a fluid dropper block entity");
        return (FluidDropperBlockEntity) blockEntity;
    }

    private static void assertCollectorItemCount(
            final GameTestHelper helper,
            final CombustionCollectorBlockEntity collector,
            final Item item,
            final int expectedCount
    ) {
        int actualCount = 0;
        for (int slot = 0; slot < CombustionCollectorBlockEntity.SLOT_COUNT; slot++) {
            final ItemStack stack = collector.getStackInSlot(slot);
            if (stack.is(item)) {
                actualCount += stack.getCount();
            }
        }
        GameTestAssertions.assertValueEqual(helper, expectedCount, actualCount, "Collector stack count should match");
    }

    private static void assertControllerFilter(
            final GameTestHelper helper,
            final CombustionControllerBlockEntity controller,
            final int slot,
            final Item item,
            final int expectedCount
    ) {
        final ItemStack stack = controller.getStackInSlot(slot);
        helper.assertTrue(stack.is(item), "Controller filter item should match");
        GameTestAssertions.assertValueEqual(helper, expectedCount, stack.getCount(), "Controller filter count should match");
    }

    private static int menuSlotIndex(
            final CombustionControllerMenu menu,
            final Container container,
            final int containerSlot
    ) {
        for (int index = 0; index < menu.slots.size(); index++) {
            final Slot slot = menu.slots.get(index);
            if (slot.container == container && slot.getContainerSlot() == containerSlot) {
                return index;
            }
        }
        throw new IllegalStateException("Menu slot not found");
    }

    private static int extract(
            final ResourceHandler<ItemResource> handler,
            final int slot,
            final ItemStack stack,
            final int amount
    ) {
        try (Transaction transaction = Transaction.openRoot()) {
            final int extracted = handler.extract(slot, ItemResource.of(stack), amount, transaction);
            transaction.commit();
            return extracted;
        }
    }

    private static void assertCapabilityExposure(
            final GameTestHelper helper,
            final BlockPos relativePos,
            final Block block,
            final Class<? extends BlockEntity> expectedType,
            final boolean expectItemCapability,
            final boolean expectFluidCapability,
            final boolean expectEnergyCapability,
            final String label
    ) {
        helper.setBlock(relativePos, block);
        final BlockEntity blockEntity = blockEntityAt(helper, relativePos);
        helper.assertTrue(expectedType.isInstance(blockEntity), "Expected " + label + " block entity");
        if (expectItemCapability) {
            helper.assertTrue(
                    blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).isPresent(),
                    label + " should expose an item capability"
            );
        }
        if (expectFluidCapability) {
            helper.assertTrue(
                    blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.UP).isPresent(),
                    label + " should expose a fluid capability"
            );
        }
        if (expectEnergyCapability) {
            final IEnergyStorage energyStorage =
                    blockEntity.getCapability(ForgeCapabilities.ENERGY, Direction.UP).orElse(null);
            helper.assertTrue(energyStorage != null, label + " should expose an energy capability");
            helper.assertTrue(energyStorage.receiveEnergy(100, false) > 0, label + " should accept Forge energy input");
        }
    }

    private static MachineCasingBlockEntity machineCasingAt(final GameTestHelper helper, final BlockPos relativePos) {
        final BlockEntity blockEntity = blockEntityAt(helper, relativePos);
        helper.assertTrue(
                blockEntity instanceof MachineCasingBlockEntity,
                "Expected a machine casing block entity"
        );
        return (MachineCasingBlockEntity) blockEntity;
    }

    private static StandaloneMachineBlockEntity standaloneMachineAt(
            final GameTestHelper helper,
            final BlockPos relativePos
    ) {
        final BlockEntity blockEntity = blockEntityAt(helper, relativePos);
        helper.assertTrue(
                blockEntity instanceof StandaloneMachineBlockEntity,
                "Expected a standalone machine block entity"
        );
        return (StandaloneMachineBlockEntity) blockEntity;
    }

    private static void assertStandaloneMachineType(
            final GameTestHelper helper,
            final BlockPos relativePos,
            final Block block,
            final ItemStack placedStack,
            final ResourceLocation expectedTypeId,
            final Function<ItemStack, ResourceLocation> typeReader,
            final String label
    ) {
        helper.setBlock(relativePos, block);
        final StandaloneMachineBlockEntity machine = standaloneMachineAt(helper, relativePos);
        machine.setTypeId(expectedTypeId);

        GameTestAssertions.assertValueEqual(helper, expectedTypeId, machine.typeId(), label + " block entity type");
        GameTestAssertions.assertValueEqual(
                helper,
                expectedTypeId,
                typeReader.apply(machine.asItemStack()),
                label + " clone stack type"
        );

        helper.getLevel().destroyBlock(helper.absolutePos(relativePos), true);
        final boolean droppedTypedStack = helper.getEntities(EntityType.ITEM, relativePos, ITEM_ASSERT_RADIUS)
                .stream()
                .map(ItemEntity::getItem)
                .anyMatch(stack -> stack.is(placedStack.getItem()) && expectedTypeId.equals(typeReader.apply(stack)));
        helper.assertTrue(droppedTypedStack, label + " drop should keep its type component");
    }

    private static BlockHitResult hitResult(final GameTestHelper helper, final BlockPos relativePos) {
        final BlockPos absolutePos = helper.absolutePos(relativePos);
        return new BlockHitResult(Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
    }

    private static void fillContainer(
            final GameTestHelper helper,
            final BlockPos relativePos,
            final ItemStack stack
    ) {
        final Container container = containerAt(helper, relativePos);
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            container.setItem(slot, stack.copy());
        }
    }

    private static Container containerAt(final GameTestHelper helper, final BlockPos relativePos) {
        final BlockEntity blockEntity = blockEntityAt(helper, relativePos);
        helper.assertTrue(blockEntity instanceof Container, "Expected a container block entity");
        return (Container) blockEntity;
    }

    private static BlockEntity blockEntityAt(final GameTestHelper helper, final BlockPos relativePos) {
        final ServerLevel level = helper.getLevel();
        final BlockEntity blockEntity = level.getBlockEntity(helper.absolutePos(relativePos));
        helper.assertTrue(blockEntity != null, "Expected a block entity");
        return blockEntity;
    }

    private static void assertContainerItemCount(
            final GameTestHelper helper,
            final BlockPos relativePos,
            final Item item,
            final int expectedCount
    ) {
        final Container container = containerAt(helper, relativePos);
        int actualCount = 0;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            final ItemStack stack = container.getItem(slot);
            if (stack.is(item)) {
                actualCount += stack.getCount();
            }
        }
        GameTestAssertions.assertValueEqual(
                helper,
                expectedCount,
                actualCount,
                "Container stack count should match condenser output"
        );
    }

    private record CombustionRig(
            MachineCasingBlockEntity casing,
            CombustionControllerBlockEntity controller,
            CombustionCollectorBlockEntity collector
    ) {
    }

    private MachineRuntimeGameTests() {
    }
}
