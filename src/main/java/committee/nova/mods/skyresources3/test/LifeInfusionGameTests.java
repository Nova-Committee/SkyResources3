package committee.nova.mods.skyresources3.test;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.common.block.entity.LifeInfuserBlockEntity;
import committee.nova.mods.skyresources3.common.block.entity.LifeInjectorBlockEntity;
import committee.nova.mods.skyresources3.common.item.HealthGemItem;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemResource;
import committee.nova.mods.skyresources3.common.compat.transfer.transaction.Transaction;

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

        final ServerPlayer player = GameTestAssertions.makeMockServerPlayer(helper, GameType.SURVIVAL);
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
        GameTestAssertions.assertValueEqual(
                helper,
                0,
                player.getOffhandItem().getCount(),
                "Infusion ingredient count"
        );
        GameTestAssertions.assertValueEqual(
                helper,
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
                TARGET_POS,
                1.0D,
                "Dropped stack count should match infusion output"
        );
        helper.succeed();
    }

    public static void lifeInfuserUsesProcessRecipe(final GameTestHelper helper) {
        final int originalHealthGemMaxHealth = Config.healthGemMaxHealth;
        helper.killAllEntities();
        Config.healthGemMaxHealth = Math.max(STORED_HEALTH, GRASS_BLOCK_HEALTH_COST);
        try {
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
            GameTestAssertions.assertValueEqual(
                    helper,
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
        } finally {
            Config.healthGemMaxHealth = originalHealthGemMaxHealth;
        }
    }

    public static void lifeInjectorItemCapabilityTransfers(final GameTestHelper helper) {
        helper.setBlock(TARGET_POS, ModBlocks.LIFE_INJECTOR.get());

        final LifeInjectorBlockEntity lifeInjector = lifeInjectorAt(helper, TARGET_POS);
        final ResourceHandler<ItemResource> handler = lifeInjector.getItemHandler();
        final ItemStack healthGem = new ItemStack(ModItems.HEALTH_GEM.get());

        GameTestAssertions.assertValueEqual(
                helper,
                0,
                insert(handler, LifeInjectorBlockEntity.GEM_SLOT, new ItemStack(Items.DIRT), 1),
                "Life Injector capability should reject non-health gems"
        );
        GameTestAssertions.assertValueEqual(
                helper,
                1,
                insert(handler, LifeInjectorBlockEntity.GEM_SLOT, healthGem, 1),
                "Life Injector capability should insert one health gem"
        );
        helper.assertTrue(lifeInjector.hasGem(), "Life Injector should contain the inserted health gem");
        GameTestAssertions.assertValueEqual(
                helper,
                1,
                extract(handler, LifeInjectorBlockEntity.GEM_SLOT, healthGem, 1),
                "Life Injector capability should extract one health gem"
        );
        helper.assertTrue(!lifeInjector.hasGem(), "Life Injector should be empty after extraction");
        helper.succeed();
    }

    public static void lifeInjectorShiftRightClickRemovesGemWithHeldItem(final GameTestHelper helper) {
        helper.setBlock(TARGET_POS, ModBlocks.LIFE_INJECTOR.get());

        final LifeInjectorBlockEntity lifeInjector = lifeInjectorAt(helper, TARGET_POS);
        final ItemStack healthGem = new ItemStack(ModItems.HEALTH_GEM.get());
        lifeInjector.insertGem(healthGem);

        final Player player = GameTestAssertions.makeMockPlayer(helper, GameType.SURVIVAL);
        player.setShiftKeyDown(true);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIRT));

        helper.useBlock(TARGET_POS, player, hitResult(helper, TARGET_POS));

        helper.assertTrue(!lifeInjector.hasGem(), "Life Injector should be empty after shift right click");
        GameTestAssertions.assertValueEqual(
                helper,
                1,
                player.getMainHandItem().getCount(),
                "Held item count should not change"
        );
        helper.assertTrue(
                GameTestAssertions.inventoryContains(player.getInventory(), stack -> stack.is(ModItems.HEALTH_GEM.get())),
                "Shift right click should return the health gem to the player"
        );
        helper.succeed();
    }

    public static void lifeInfuserItemCapabilityTransfers(final GameTestHelper helper) {
        helper.setBlock(TARGET_POS, ModBlocks.LIFE_INFUSER.get());

        final LifeInfuserBlockEntity lifeInfuser = lifeInfuserAt(helper, TARGET_POS);
        final ResourceHandler<ItemResource> handler = lifeInfuser.getItemHandler();
        final ItemStack healthGem = new ItemStack(ModItems.HEALTH_GEM.get());
        final ItemStack seeds = new ItemStack(Items.WHEAT_SEEDS, 4);

        GameTestAssertions.assertValueEqual(
                helper,
                0,
                insert(handler, LifeInfuserBlockEntity.GEM_SLOT, new ItemStack(Items.DIRT), 1),
                "Life Infuser gem slot should reject non-health gems"
        );
        GameTestAssertions.assertValueEqual(
                helper,
                0,
                insert(handler, LifeInfuserBlockEntity.INPUT_SLOT, healthGem, 1),
                "Life Infuser input slot should reject health gems"
        );
        GameTestAssertions.assertValueEqual(
                helper,
                1,
                insert(handler, LifeInfuserBlockEntity.GEM_SLOT, healthGem, 1),
                "Life Infuser gem slot should accept one health gem"
        );
        GameTestAssertions.assertValueEqual(
                helper,
                4,
                insert(handler, LifeInfuserBlockEntity.INPUT_SLOT, seeds, seeds.getCount()),
                "Life Infuser input slot should accept regular inputs"
        );
        helper.assertTrue(lifeInfuser.hasGem(), "Life Infuser should contain the inserted health gem");
        helper.assertTrue(lifeInfuser.hasInput(), "Life Infuser should contain the inserted input");
        GameTestAssertions.assertValueEqual(
                helper,
                1,
                extract(handler, LifeInfuserBlockEntity.GEM_SLOT, healthGem, 1),
                "Life Infuser gem slot should extract one health gem"
        );
        GameTestAssertions.assertValueEqual(
                helper,
                4,
                extract(handler, LifeInfuserBlockEntity.INPUT_SLOT, seeds, seeds.getCount()),
                "Life Infuser input slot should extract the inserted input"
        );
        helper.assertTrue(!lifeInfuser.hasGem(), "Life Infuser should have no gem after extraction");
        helper.assertTrue(!lifeInfuser.hasInput(), "Life Infuser should have no input after extraction");
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

    private static LifeInjectorBlockEntity lifeInjectorAt(
            final GameTestHelper helper,
            final BlockPos relativePos
    ) {
        final BlockEntity blockEntity = helper.getLevel().getBlockEntity(helper.absolutePos(relativePos));
        helper.assertTrue(
                blockEntity instanceof LifeInjectorBlockEntity,
                "Expected a Life Injector block entity"
        );
        return (LifeInjectorBlockEntity) blockEntity;
    }

    private static int insert(
            final ResourceHandler<ItemResource> handler,
            final int slot,
            final ItemStack stack,
            final int amount
    ) {
        try (Transaction transaction = Transaction.openRoot()) {
            final int inserted = handler.insert(slot, ItemResource.of(stack), amount, transaction);
            transaction.commit();
            return inserted;
        }
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

    private LifeInfusionGameTests() {
    }
}
