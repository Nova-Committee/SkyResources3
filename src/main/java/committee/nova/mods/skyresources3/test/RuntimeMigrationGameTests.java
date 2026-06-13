package committee.nova.mods.skyresources3.test;

import committee.nova.mods.skyresources3.event.CuttingKnifeEvents;
import committee.nova.mods.skyresources3.event.RockGrinderEvents;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class RuntimeMigrationGameTests {
    private static final BlockPos TOOL_BLOCK_POS = new BlockPos(1, 1, 1);
    private static final BlockPos MAGMAFIED_STONE_POS = new BlockPos(1, 2, 1);
    private static final double ITEM_ASSERT_RADIUS = 2.0D;
    private static final int EXPECTED_OAK_PLANKS = 6;
    private static final int EXPECTED_GRAVEL = 1;

    public static void cuttingKnifeUsesProcessRecipe(final GameTestHelper helper) {
        helper.killAllEntities();
        helper.setBlock(TOOL_BLOCK_POS, Blocks.OAK_LOG);

        final Player player = makeSurvivalPlayerWith(
                helper,
                new ItemStack(ModItems.STONE_CUTTING_KNIFE.get())
        );
        final BlockEvent.BreakEvent event = breakEvent(helper, TOOL_BLOCK_POS, player);
        CuttingKnifeEvents.onBlockBreak(event);

        helper.assertTrue(event.isCanceled(), "Cutting knife should handle a matching process recipe");
        helper.assertTrue(helper.getBlockState(TOOL_BLOCK_POS).isAir(), "Cutting knife should consume the source block");
        GameTestAssertions.assertDroppedItemCount(
                helper,
                Items.OAK_PLANKS,
                EXPECTED_OAK_PLANKS,
                TOOL_BLOCK_POS,
                ITEM_ASSERT_RADIUS,
                "Dropped stack count should match migrated recipe output"
        );
        helper.assertTrue(
                player.getMainHandItem().getDamageValue() > 0,
                "Cutting knife should take durability damage"
        );
        helper.succeed();
    }

    public static void rockGrinderUsesProcessRecipe(final GameTestHelper helper) {
        helper.killAllEntities();
        helper.setBlock(TOOL_BLOCK_POS, Blocks.COBBLESTONE);

        final Player player = makeSurvivalPlayerWith(helper, new ItemStack(ModItems.STONE_GRINDER.get()));
        final BlockEvent.BreakEvent event = breakEvent(helper, TOOL_BLOCK_POS, player);
        RockGrinderEvents.onBlockBreak(event);

        helper.assertTrue(event.isCanceled(), "Rock grinder should handle a matching process recipe");
        helper.assertTrue(helper.getBlockState(TOOL_BLOCK_POS).isAir(), "Rock grinder should consume the source block");
        GameTestAssertions.assertDroppedItemCount(
                helper,
                Items.GRAVEL,
                EXPECTED_GRAVEL,
                TOOL_BLOCK_POS,
                ITEM_ASSERT_RADIUS,
                "Dropped stack count should match migrated recipe output"
        );
        helper.assertTrue(
                player.getMainHandItem().getDamageValue() > 0,
                "Rock grinder should take durability damage"
        );
        helper.succeed();
    }

    public static void magmafiedStoneTicksCrystalFluid(final GameTestHelper helper) {
        helper.killAllEntities();
        helper.setBlock(MAGMAFIED_STONE_POS, ModBlocks.MAGMAFIED_STONE.get());
        for (final Direction direction : Direction.values()) {
            helper.setBlock(MAGMAFIED_STONE_POS.relative(direction), ModBlocks.CRYSTAL_FLUID.get());
        }

        helper.onEachTick(() -> helper.tickBlock(MAGMAFIED_STONE_POS));
        helper.succeedWhen(() -> helper.assertItemEntityPresent(
                Items.COBBLESTONE,
                MAGMAFIED_STONE_POS,
                ITEM_ASSERT_RADIUS
        ));
    }

    private static Player makeSurvivalPlayerWith(final GameTestHelper helper, final ItemStack stack) {
        final Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        return player;
    }

    private static BlockEvent.BreakEvent breakEvent(
            final GameTestHelper helper,
            final BlockPos relativePos,
            final Player player
    ) {
        final ServerLevel level = helper.getLevel();
        final BlockPos absolutePos = helper.absolutePos(relativePos);
        return new BlockEvent.BreakEvent(level, absolutePos, level.getBlockState(absolutePos), player);
    }

    private RuntimeMigrationGameTests() {
    }
}
