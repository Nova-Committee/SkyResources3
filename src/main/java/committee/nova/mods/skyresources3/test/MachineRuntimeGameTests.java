package committee.nova.mods.skyresources3.test;

import committee.nova.mods.skyresources3.block.CombustionControllerBlock;
import committee.nova.mods.skyresources3.block.entity.CombustionCollectorBlockEntity;
import committee.nova.mods.skyresources3.block.entity.CombustionControllerBlockEntity;
import committee.nova.mods.skyresources3.block.entity.MachineCasingBlockEntity;
import committee.nova.mods.skyresources3.item.OreAlchemyDust;
import committee.nova.mods.skyresources3.machine.MachineVariant;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModDataPackRegistries;
import committee.nova.mods.skyresources3.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public final class MachineRuntimeGameTests {
    private static final BlockPos CASING_POS = new BlockPos(2, 1, 2);
    private static final BlockPos SOURCE_POS = CASING_POS.above();
    private static final BlockPos OUTPUT_POS = CASING_POS.below();
    private static final BlockPos CHAMBER_POS = CASING_POS.above();
    private static final BlockPos CONTROLLER_POS = CHAMBER_POS.north();
    private static final BlockPos COLLECTOR_POS = CHAMBER_POS.east();
    private static final double ITEM_ASSERT_RADIUS = 2.0D;
    private static final int EXPECTED_OUTPUT_COUNT = 1;
    private static final int DIRT_RECIPE_HEAT = 100;
    private static final int RED_SAND_RECIPE_HEAT = 200;

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

    private static MachineCasingBlockEntity setupCopperCrystalFluidCondenser(final GameTestHelper helper) {
        helper.setBlock(CASING_POS, ModBlocks.MACHINE_CASING.get());
        helper.setBlock(SOURCE_POS, ModBlocks.CRYSTAL_FLUID.get());

        final MachineCasingBlockEntity casing = machineCasingAt(helper, CASING_POS);
        casing.setCasingType(ModDataPackRegistries.DARK_MATTER);
        final Player player = helper.makeMockPlayer(GameType.CREATIVE);
        final boolean installed = casing.installHeater(
                new ItemStack(ModItems.CONDENSERS.get(MachineVariant.DARK_MATTER).get()),
                player
        );
        helper.assertTrue(installed, "Condenser should install into the machine casing");
        casing.setStackInSlot(
                MachineCasingBlockEntity.FUEL_SLOT,
                new ItemStack(ModItems.ORE_ALCHEMICAL_DUSTS.get(OreAlchemyDust.COPPER).get())
        );
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
                new ItemStack(ModItems.COMBUSTION_HEATERS.get(MachineVariant.IRON).get()),
                player
        );
        helper.assertTrue(installed, "Combustion heater should install into the machine casing");
        casing.setStackInSlot(MachineCasingBlockEntity.FUEL_SLOT, new ItemStack(Items.COAL));
        warmCasing(helper, casing, targetHeat);

        final CombustionControllerBlockEntity controller = combustionControllerAt(helper, CONTROLLER_POS);
        final CombustionCollectorBlockEntity collector = combustionCollectorAt(helper, COLLECTOR_POS);
        return new CombustionRig(casing, controller, collector);
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

    private static MachineCasingBlockEntity machineCasingAt(final GameTestHelper helper, final BlockPos relativePos) {
        final BlockEntity blockEntity = blockEntityAt(helper, relativePos);
        helper.assertTrue(
                blockEntity instanceof MachineCasingBlockEntity,
                "Expected a machine casing block entity"
        );
        return (MachineCasingBlockEntity) blockEntity;
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
