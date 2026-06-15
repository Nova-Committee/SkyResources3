package committee.nova.mods.skyresources3.test;

import committee.nova.mods.skyresources3.common.block.FreezerBlock;
import committee.nova.mods.skyresources3.common.block.entity.FreezerBlockEntity;
import committee.nova.mods.skyresources3.common.menu.FreezerMenu;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;

public final class FreezerGameTests {
    private static final BlockPos FREEZER_POS = new BlockPos(2, 1, 2);

    public static void ironFreezerMenuReadsValidMultiblock(final GameTestHelper helper) {
        assertMenuReadsValidMultiblock(helper, ModBlocks.IRON_FREEZER.get());
    }

    public static void lightFreezerMenuReadsValidMultiblock(final GameTestHelper helper) {
        assertMenuReadsValidMultiblock(helper, ModBlocks.LIGHT_FREEZER.get());
    }

    private static void assertMenuReadsValidMultiblock(final GameTestHelper helper, final Block block) {
        final FreezerBlockEntity freezer = setupFreezer(helper, block);
        final Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        final FreezerMenu menu = new FreezerMenu(0, player.getInventory(), freezer);

        menu.slots.get(0).setByPlayer(new ItemStack(Items.SNOWBALL, 4));
        menu.removed(player);

        helper.assertTrue(menu.requiresMultiblock(), "Freezer tier should require a multiblock");
        helper.assertTrue(freezer.hasValidMultiblock(), "Freezer block entity should see the assembled multiblock");
        helper.assertTrue(menu.hasValidMultiblock(), "Freezer menu should expose the live multiblock state");
        helper.assertTrue(
                freezer.getStackInSlot(0).is(Items.SNOWBALL),
                "Freezer menu input slot should write to the block entity"
        );
        helper.assertValueEqual(4, freezer.getStackInSlot(0).getCount(), "Freezer input stack count should persist");
        helper.succeed();
    }

    private static FreezerBlockEntity setupFreezer(final GameTestHelper helper, final Block block) {
        helper.setBlock(
                FREEZER_POS,
                block.defaultBlockState()
                        .setValue(FreezerBlock.FACING, Direction.NORTH)
                        .setValue(FreezerBlock.PART, FreezerBlock.FreezerPart.BOTTOM)
        );
        helper.setBlock(
                FREEZER_POS.above(),
                block.defaultBlockState()
                        .setValue(FreezerBlock.FACING, Direction.NORTH)
                        .setValue(FreezerBlock.PART, FreezerBlock.FreezerPart.TOP)
        );
        return helper.getBlockEntity(FREEZER_POS, FreezerBlockEntity.class);
    }

    private FreezerGameTests() {
    }
}
