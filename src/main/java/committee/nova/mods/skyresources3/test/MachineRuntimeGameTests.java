package committee.nova.mods.skyresources3.test;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.block.CombustionControllerBlock;
import committee.nova.mods.skyresources3.block.entity.CombustionCollectorBlockEntity;
import committee.nova.mods.skyresources3.block.entity.CombustionControllerBlockEntity;
import committee.nova.mods.skyresources3.block.entity.MachineCasingBlockEntity;
import committee.nova.mods.skyresources3.event.MachineCasingEvents;
import committee.nova.mods.skyresources3.item.CombustionHeaterItem;
import committee.nova.mods.skyresources3.item.CondenserItem;
import committee.nova.mods.skyresources3.item.HeatProviderItem;
import committee.nova.mods.skyresources3.item.OreAlchemyDustItem;
import committee.nova.mods.skyresources3.machine.CasingType;
import committee.nova.mods.skyresources3.machine.CombustionHeaterType;
import committee.nova.mods.skyresources3.menu.CombustionControllerMenu;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModDataPackRegistries;
import committee.nova.mods.skyresources3.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class MachineRuntimeGameTests {
    private static final BlockPos CASING_POS = new BlockPos(2, 1, 2);
    private static final BlockPos SOURCE_POS = CASING_POS.above();
    private static final BlockPos OUTPUT_POS = CASING_POS.below();
    private static final BlockPos CHAMBER_POS = CASING_POS.above();
    private static final BlockPos CONTROLLER_POS = CHAMBER_POS.north();
    private static final BlockPos COLLECTOR_POS = CHAMBER_POS.east();
    private static final BlockPos REDSTONE_POS = CASING_POS.west();
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
        final Player player = helper.makeMockPlayer(GameType.CREATIVE);
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
        final Player player = helper.makeMockPlayer(GameType.SURVIVAL);
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
                player.getInventory().contains(stack -> stack.is(ModItems.COMBUSTION_HEATER.get())
                        && ModDataPackRegistries.combustionHeaterTypeId(ModDataPackRegistries.IRON_COMBUSTION_HEATER)
                                .equals(CombustionHeaterItem.combustionHeaterTypeId(stack))),
                "Removed heater should be returned to the player with its type component"
        );
        helper.succeed();
    }

    public static void heatProviderEmbedsAsTypeIdAndProvidesHeat(final GameTestHelper helper) {
        helper.killAllEntities();
        helper.setBlock(CASING_POS, ModBlocks.MACHINE_CASING.get());
        final MachineCasingBlockEntity casing = machineCasingAt(helper, CASING_POS);
        casing.setCasingType(ModDataPackRegistries.IRON);
        final Player player = helper.makeMockPlayer(GameType.CREATIVE);
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

        helper.assertValueEqual(10, casing.heatSourceValue(), "Iron heat provider heat source value");
        final ItemStack removed = casing.removeHeater();
        helper.assertTrue(removed.is(ModItems.HEAT_PROVIDER.get()), "Removed provider should be the single provider block");
        helper.assertTrue(
                ModDataPackRegistries.heatProviderTypeId(ModDataPackRegistries.IRON_HEAT_PROVIDER)
                        .equals(HeatProviderItem.heatProviderTypeId(removed)),
                "Removed provider should keep its type component"
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
        final Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        final CombustionControllerMenu menu = new CombustionControllerMenu(
                0,
                player.getInventory(),
                controller
        );

        menu.setCarried(new ItemStack(Items.DIRT, 7));
        menu.clicked(0, 0, ClickType.PICKUP, player);

        assertControllerFilter(helper, controller, 0, Items.DIRT, 1);
        helper.assertValueEqual(7, menu.getCarried().getCount(), "Carried stack should not be consumed");

        menu.setCarried(ItemStack.EMPTY);
        menu.clicked(0, 0, ClickType.PICKUP, player);
        helper.assertTrue(controller.getStackInSlot(0).isEmpty(), "Empty cursor click should clear a ghost filter");

        player.getInventory().setItem(0, new ItemStack(Items.WHEAT_SEEDS, 4));
        menu.quickMoveStack(player, menuSlotIndex(menu, player.getInventory(), 0));

        assertControllerFilter(helper, controller, 0, Items.WHEAT_SEEDS, 1);
        helper.assertValueEqual(
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
        helper.assertValueEqual(64, rig.collector().getStackInSlot(0).getCount(), "Collector red sand slot count");
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
        final Player player = helper.makeMockPlayer(GameType.CREATIVE);
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
        final Player player = helper.makeMockPlayer(GameType.CREATIVE);
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
        final Player player = helper.makeMockPlayer(GameType.CREATIVE);
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
        helper.assertValueEqual(expectedCount, actualCount, "Collector stack count should match");
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
        helper.assertValueEqual(expectedCount, stack.getCount(), "Controller filter count should match");
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

    private static MachineCasingBlockEntity machineCasingAt(final GameTestHelper helper, final BlockPos relativePos) {
        final BlockEntity blockEntity = blockEntityAt(helper, relativePos);
        helper.assertTrue(
                blockEntity instanceof MachineCasingBlockEntity,
                "Expected a machine casing block entity"
        );
        return (MachineCasingBlockEntity) blockEntity;
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
        helper.assertValueEqual(expectedCount, actualCount, "Container stack count should match condenser output");
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
