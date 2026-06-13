package committee.nova.mods.skyresources3.test;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

final class GameTestAssertions {
    static void assertDroppedItemCount(
            final GameTestHelper helper,
            final Item item,
            final int expectedCount,
            final BlockPos relativePos,
            final double radius,
            final String message
    ) {
        final int actualCount = helper.getEntities(EntityType.ITEM, relativePos, radius)
                .stream()
                .map(ItemEntity::getItem)
                .filter(stack -> stack.is(item))
                .mapToInt(ItemStack::getCount)
                .sum();
        helper.assertValueEqual(expectedCount, actualCount, message);
    }

    private GameTestAssertions() {
    }
}
