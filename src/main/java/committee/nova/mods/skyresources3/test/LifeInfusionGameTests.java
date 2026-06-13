package committee.nova.mods.skyresources3.test;

import committee.nova.mods.skyresources3.block.entity.LifeInfuserBlockEntity;
import committee.nova.mods.skyresources3.item.HealthGemItem;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class LifeInfusionGameTests {
    private static final BlockPos TARGET_POS = new BlockPos(2, 1, 2);
    private static final BlockPos PLAYER_POS = new BlockPos(2, 1, 3);
    private static final BlockPos INFUSER_POS = new BlockPos(2, 2, 2);
    private static final double ITEM_ASSERT_RADIUS = 2.0D;
    private static final int OAK_SAPLING_HEALTH_COST = 10;
    private static final int GRASS_BLOCK_HEALTH_COST = 14;
    private static final int STORED_HEALTH = 20;

    public static void infusionStoneUsesProcessRecipe(final GameTestHelper helper) {
        helper.killAllEntities();
        helper.setBlock(TARGET_POS, Blocks.SPRUCE_SAPLING);

        final Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        final Vec3 playerPos = helper.absoluteVec(Vec3.atBottomCenterOf(PLAYER_POS));
        player.setPos(playerPos);
        player.setHealth(player.getMaxHealth());
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.SANDSTONE_INFUSION_STONE.get()));
        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.APPLE, 4));

        final InteractionResult result = player.getMainHandItem().useOn(new UseOnContext(
                player,
                InteractionHand.MAIN_HAND,
                hitResult(helper, TARGET_POS)
        ));

        helper.assertTrue(result.consumesAction(), "Infusion stone should consume a matching infusion recipe action");
        helper.assertTrue(helper.getBlockState(TARGET_POS).isAir(), "Infusion stone should consume the target block");
        helper.assertValueEqual(0, player.getOffhandItem().getCount(), "Infusion ingredient count");
        helper.assertValueEqual(
                player.getMaxHealth() - OAK_SAPLING_HEALTH_COST,
                player.getHealth(),
                "Player health after infusion"
        );
        helper.assertTrue(
                player.getMainHandItem().getDamageValue() > 0,
                "Infusion stone should take durability damage"
        );
        GameTestAssertions.assertDroppedItemCount(
                helper,
                Items.OAK_SAPLING,
                1,
                PLAYER_POS,
                ITEM_ASSERT_RADIUS,
                "Dropped stack count should match infusion output"
        );
        helper.succeed();
    }

    public static void lifeInfuserUsesProcessRecipe(final GameTestHelper helper) {
        helper.killAllEntities();
        setupLifeInfuserMultiblock(helper);
        helper.setBlock(TARGET_POS, Blocks.DIRT);

        final LifeInfuserBlockEntity lifeInfuser = lifeInfuserAt(helper, INFUSER_POS);
        final ItemStack gem = new ItemStack(ModItems.HEALTH_GEM.get());
        helper.assertTrue(
                HealthGemItem.addStoredHealth(gem, STORED_HEALTH),
                "Health gem should accept stored health for the test"
        );
        lifeInfuser.insertGem(gem);
        lifeInfuser.insertInput(new ItemStack(Items.WHEAT_SEEDS, 4));

        lifeInfuser.updatePowered(helper.getLevel(), true);

        helper.assertTrue(helper.getBlockState(TARGET_POS).isAir(), "Life Infuser should consume the target block");
        helper.assertTrue(!lifeInfuser.hasInput(), "Life Infuser should consume the recipe input");
        helper.assertValueEqual(
                STORED_HEALTH - GRASS_BLOCK_HEALTH_COST,
                HealthGemItem.getHealthInjected(lifeInfuser.removeGem()),
                "Stored health after Life Infuser craft"
        );
        GameTestAssertions.assertDroppedItemCount(
                helper,
                Items.GRASS_BLOCK,
                1,
                TARGET_POS,
                ITEM_ASSERT_RADIUS,
                "Dropped stack count should match Life Infuser output"
        );
        helper.succeed();
    }

    private static void setupLifeInfuserMultiblock(final GameTestHelper helper) {
        helper.setBlock(INFUSER_POS, ModBlocks.LIFE_INFUSER.get());
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                final BlockPos topPos = INFUSER_POS.offset(x, 1, z);
                helper.setBlock(
                        topPos,
                        x == 0 && z == 0 ? ModBlocks.DARK_MATTER_BLOCK.get() : Blocks.OAK_LEAVES
                );
            }
        }

        for (final BlockPos pillar : new BlockPos[] {
                INFUSER_POS.north().west(),
                INFUSER_POS.north().east(),
                INFUSER_POS.south().west(),
                INFUSER_POS.south().east()
        }) {
            helper.setBlock(pillar, Blocks.OAK_LOG);
            helper.setBlock(pillar.below(), Blocks.OAK_LOG);
        }
    }

    private static BlockHitResult hitResult(final GameTestHelper helper, final BlockPos relativePos) {
        final BlockPos absolutePos = helper.absolutePos(relativePos);
        return new BlockHitResult(Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
    }

    private static LifeInfuserBlockEntity lifeInfuserAt(
            final GameTestHelper helper,
            final BlockPos relativePos
    ) {
        final BlockEntity blockEntity = helper.getLevel().getBlockEntity(helper.absolutePos(relativePos));
        helper.assertTrue(
                blockEntity instanceof LifeInfuserBlockEntity,
                "Expected a Life Infuser block entity"
        );
        return (LifeInfuserBlockEntity) blockEntity;
    }

    private LifeInfusionGameTests() {
    }
}
