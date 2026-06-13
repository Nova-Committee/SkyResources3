package committee.nova.mods.skyresources3.test;

import committee.nova.mods.skyresources3.event.SurvivalistFishingEvents;
import committee.nova.mods.skyresources3.registry.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

public final class SurvivalistFishingGameTests {
    private static final BlockPos PLAYER_POS = new BlockPos(2, 1, 2);
    private static final BlockPos HOOK_POS = new BlockPos(2, 1, 3);
    private static final double ITEM_ASSERT_RADIUS = 4.0D;
    private static final int ORIGINAL_ROD_DAMAGE = 1;

    @SuppressWarnings("removal")
    public static void survivalistRodUsesCustomFishingLoot(final GameTestHelper helper) {
        helper.killAllEntities();

        final ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.setPos(helper.absoluteVec(Vec3.atBottomCenterOf(PLAYER_POS)));
        player.setItemInHand(
                InteractionHand.MAIN_HAND,
                new ItemStack(ModItems.SURVIVALIST_FISHING_ROD.get())
        );

        final FishingHook hook = new FishingHook(player, helper.getLevel(), 0, 0);
        hook.setPos(helper.absoluteVec(Vec3.atBottomCenterOf(HOOK_POS)));
        final ItemFishedEvent event = new ItemFishedEvent(
                List.of(new ItemStack(Items.DIAMOND)),
                ORIGINAL_ROD_DAMAGE,
                hook
        );

        SurvivalistFishingEvents.onItemFished(event);

        helper.assertTrue(event.isCanceled(), "Survivalist fishing should cancel vanilla fishing drops");
        helper.assertValueEqual(0, event.getRodDamage(), "Survivalist fishing should not damage the rod");
        helper.assertItemEntityNotPresent(
                Items.DIAMOND,
                HOOK_POS,
                ITEM_ASSERT_RADIUS
        );
        helper.assertTrue(
                !helper.getEntities(EntityType.ITEM, HOOK_POS, ITEM_ASSERT_RADIUS).isEmpty(),
                "Survivalist fishing should spawn at least one custom loot item"
        );
        helper.assertTrue(
                !helper.getEntities(EntityType.EXPERIENCE_ORB, PLAYER_POS, ITEM_ASSERT_RADIUS).isEmpty(),
                "Survivalist fishing should spawn fishing experience"
        );
        helper.succeed();
    }

    private SurvivalistFishingGameTests() {
    }
}
