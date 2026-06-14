package committee.nova.mods.skyresources3.test;

import committee.nova.mods.skyresources3.block.entity.FusionTableBlockEntity;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class FusionTableGameTests {
    private static final BlockPos TABLE_POS = new BlockPos(2, 1, 2);

    public static void fusionTableCachesFractionalCatalystYield(final GameTestHelper helper) {
        final FusionTableBlockEntity table = setupFusionTable(helper);
        table.setStackInSlot(FusionTableBlockEntity.CATALYST_SLOT, new ItemStack(ModItems.TERTIUS_ALCHEMICAL_DUST.get()));
        setAlchemicalCoalInputs(table);

        tick(table, helper, 1);

        assertInputEmptyWithFilter(helper, table, 0, Items.COAL, 1);
        assertInputEmptyWithFilter(helper, table, 1, Items.GUNPOWDER, 3);
        helper.assertTrue(
                table.getStackInSlot(FusionTableBlockEntity.CATALYST_SLOT).isEmpty(),
                "Fusion table should store one catalyst charge instead of keeping the dust item"
        );

        tick(table, helper, FusionTableBlockEntity.MAX_PROGRESS);

        assertStack(helper, table.getStackInSlot(FusionTableBlockEntity.OUTPUT_SLOT), ModItems.ALCHEMICAL_COAL.get(), 4);
        helper.assertTrue(
                table.getCurrentYield() > 0.49D && table.getCurrentYield() < 0.51D,
                "Tertius catalyst should cache the 0.5 fractional recipe yield"
        );

        setAlchemicalCoalInputs(table);
        tick(table, helper, FusionTableBlockEntity.MAX_PROGRESS + 1);

        assertStack(helper, table.getStackInSlot(FusionTableBlockEntity.OUTPUT_SLOT), ModItems.ALCHEMICAL_COAL.get(), 9);
        helper.assertTrue(
                table.getCurrentYield() < 0.01D,
                "Second craft should spend the cached fractional yield"
        );
        helper.succeed();
    }

    public static void fusionTableSplitDuplicateStacksDoNotMatchRecipe(final GameTestHelper helper) {
        final FusionTableBlockEntity table = setupFusionTable(helper);
        table.setStackInSlot(FusionTableBlockEntity.CATALYST_SLOT, new ItemStack(ModItems.TERTIUS_ALCHEMICAL_DUST.get()));
        table.setStackInSlot(FusionTableBlockEntity.FIRST_INPUT_SLOT, new ItemStack(Items.COAL));
        table.setStackInSlot(FusionTableBlockEntity.FIRST_INPUT_SLOT + 1, new ItemStack(Items.GUNPOWDER, 2));
        table.setStackInSlot(FusionTableBlockEntity.FIRST_INPUT_SLOT + 2, new ItemStack(Items.GUNPOWDER));

        tick(table, helper, 5);

        helper.assertValueEqual(0, table.getProgress(), "Split duplicate inputs should not start fusion progress");
        helper.assertTrue(
                table.getStackInSlot(FusionTableBlockEntity.OUTPUT_SLOT).isEmpty(),
                "Split duplicate inputs should not produce fusion output"
        );
        assertStack(helper, table.getStackInSlot(FusionTableBlockEntity.CATALYST_SLOT),
                ModItems.TERTIUS_ALCHEMICAL_DUST.get(), 1);
        helper.succeed();
    }

    private static FusionTableBlockEntity setupFusionTable(final GameTestHelper helper) {
        helper.setBlock(TABLE_POS, ModBlocks.FUSION_TABLE.get());
        return helper.getBlockEntity(TABLE_POS, FusionTableBlockEntity.class);
    }

    private static void setAlchemicalCoalInputs(final FusionTableBlockEntity table) {
        table.setStackInSlot(FusionTableBlockEntity.FIRST_INPUT_SLOT, new ItemStack(Items.COAL));
        table.setStackInSlot(FusionTableBlockEntity.FIRST_INPUT_SLOT + 1, new ItemStack(Items.GUNPOWDER, 3));
    }

    private static void tick(
            final FusionTableBlockEntity table,
            final GameTestHelper helper,
            final int ticks
    ) {
        for (int tick = 0; tick < ticks; tick++) {
            table.serverTick(helper.getLevel());
        }
    }

    private static void assertInputEmptyWithFilter(
            final GameTestHelper helper,
            final FusionTableBlockEntity table,
            final int filterIndex,
            final Item item,
            final int expectedFilterCount
    ) {
        final int slot = FusionTableBlockEntity.FIRST_INPUT_SLOT + filterIndex;
        helper.assertTrue(table.getStackInSlot(slot).isEmpty(), "Fusion input slot should be empty after consumption");
        assertStack(helper, table.getFilterStack(filterIndex), item, expectedFilterCount);
    }

    private static void assertStack(
            final GameTestHelper helper,
            final ItemStack stack,
            final Item item,
            final int expectedCount
    ) {
        helper.assertTrue(stack.is(item), "Stack item should match");
        helper.assertValueEqual(expectedCount, stack.getCount(), "Stack count should match");
    }

    private FusionTableGameTests() {
    }
}
