package committee.nova.mods.skyresources3.test;

import committee.nova.mods.skyresources3.block.entity.MachineCasingBlockEntity;
import committee.nova.mods.skyresources3.item.OreAlchemyDust;
import committee.nova.mods.skyresources3.machine.MachineVariant;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class MachineRuntimeGameTests {
    private static final BlockPos CASING_POS = new BlockPos(1, 2, 1);
    private static final BlockPos SOURCE_POS = CASING_POS.above();
    private static final BlockPos OUTPUT_POS = CASING_POS.below();
    private static final double ITEM_ASSERT_RADIUS = 2.0D;
    private static final int EXPECTED_OUTPUT_COUNT = 1;

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

    private static MachineCasingBlockEntity setupCopperCrystalFluidCondenser(final GameTestHelper helper) {
        helper.setBlock(CASING_POS, ModBlocks.MACHINE_CASINGS.get(MachineVariant.DARK_MATTER).get());
        helper.setBlock(SOURCE_POS, ModBlocks.CRYSTAL_FLUID.get());

        final MachineCasingBlockEntity casing = machineCasingAt(helper, CASING_POS);
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

    private MachineRuntimeGameTests() {
    }
}
