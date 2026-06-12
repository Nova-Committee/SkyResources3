package committee.nova.mods.skyresources3.item;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModItems;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public final class InfusionStoneItem extends Item {
    private static final int BONEMEAL_HEALTH_COST = 4;

    public InfusionStoneItem(final Properties properties, final int durability) {
        super(properties
                .durability(durability)
                .setNoCombineRepair()
                .stacksTo(1));
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, context.getClickedFace(), context.getItemInHand())) {
            return InteractionResult.PASS;
        }

        final Optional<InfusionRecipe> recipe = findRecipe(level.getBlockState(pos), player.getOffhandItem());
        if (recipe.isPresent()) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            tryInfuse(context, player, recipe.get());
            return InteractionResult.SUCCESS_SERVER;
        }

        if (!Config.infusionStoneBonemealCapability || !BonemealGrowth.isValidTarget(level, pos)) {
            return InteractionResult.PASS;
        }
        if (level instanceof ServerLevel serverLevel) {
            BonemealGrowth.growUntilStable(serverLevel, pos);
            hurtStoneAndPlayer(context, player, BONEMEAL_HEALTH_COST);
            context.getItemInHand().causeUseVibration(player, GameEvent.ITEM_INTERACT_FINISH);
            level.levelEvent(1505, pos, 15);
        }
        return InteractionResult.SUCCESS;
    }

    private static void tryInfuse(
            final UseOnContext context,
            final Player player,
            final InfusionRecipe recipe
    ) {
        final Level level = context.getLevel();
        if (player.getMaxHealth() < recipe.healthCost()) {
            player.displayClientMessage(
                    Component.translatable("message.skyresources3.infusion_stone.max_health_too_low"),
                    true
            );
            return;
        }
        if (player.getHealth() < recipe.healthCost()) {
            player.displayClientMessage(
                    Component.translatable("message.skyresources3.infusion_stone.not_enough_health"),
                    true
            );
            return;
        }

        player.getOffhandItem().shrink(recipe.ingredientCount());
        player.drop(recipe.createOutput(), false);
        level.destroyBlock(context.getClickedPos(), false, player);
        hurtStoneAndPlayer(context, player, recipe.healthCost());
    }

    private static void hurtStoneAndPlayer(final UseOnContext context, final Player player, final int healthCost) {
        context.getItemInHand().hurtAndBreak(1, player, slotForHand(context.getHand()));
        if (context.getLevel() instanceof ServerLevel serverLevel) {
            player.hurtServer(serverLevel, serverLevel.damageSources().magic(), (float) healthCost);
        }
    }

    private static EquipmentSlot slotForHand(final InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }

    private static Optional<InfusionRecipe> findRecipe(final BlockState state, final ItemStack ingredient) {
        for (final InfusionRecipe recipe : recipes()) {
            if (recipe.matches(state, ingredient)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    private static List<InfusionRecipe> recipes() {
        return List.of(
                recipe(
                        ModItems.PRIMUS_ALCHEMICAL_DUST.get(),
                        10,
                        block(ModBlocks.CACTUS_FRUIT_NEEDLE.get()),
                        Blocks.ACACIA_SAPLING,
                        1,
                        10
                ),
                recipe(Items.APPLE, 4, InfusionStoneItem::isSapling, Blocks.OAK_SAPLING, 1, 10),
                recipe(ModItems.CACTUS_FRUIT.get(), 4, block(Blocks.RED_SAND), Blocks.COARSE_DIRT, 1, 15),
                recipe(ModItems.PRIMUS_ALCHEMICAL_DUST.get(), 6, block(Blocks.CACTUS), Blocks.CACTUS, 3, 8),
                recipe(Items.ROTTEN_FLESH, 4, InfusionStoneItem::isSapling, Blocks.DEAD_BUSH, 1, 10),
                recipe(Items.WHEAT_SEEDS, 4, block(Blocks.DIRT), Blocks.GRASS_BLOCK, 1, 14),
                recipe(Blocks.BROWN_MUSHROOM, 4, block(Blocks.DIRT), Blocks.MYCELIUM, 1, 16),
                recipe(Items.SUGAR, 3, block(Blocks.HAY_BLOCK), Items.APPLE, 1, 10),
                recipe(Items.COCOA_BEANS, 10, InfusionStoneItem::isSapling, Blocks.JUNGLE_SAPLING, 1, 19),
                recipe(Items.BONE_MEAL, 10, InfusionStoneItem::isSapling, Blocks.BIRCH_SAPLING, 1, 19),
                recipe(Items.GLISTERING_MELON_SLICE, 3, block(Blocks.PUMPKIN), Items.SUGAR_CANE, 1, 17),
                recipe(Items.POPPED_CHORUS_FRUIT, 3, block(Blocks.RED_MUSHROOM), Blocks.CHORUS_FLOWER, 1, 19),
                recipe(Items.ENDER_EYE, 4, block(Blocks.MELON), Items.CHORUS_FRUIT, 1, 12),
                recipe(Items.SPIDER_EYE, 4, InfusionStoneItem::isGrassLikePlant, Items.NETHER_WART, 1, 12),
                recipe(Items.RED_DYE, 8, InfusionStoneItem::isGrassLikePlant, Blocks.RED_MUSHROOM, 1, 12),
                recipe(Items.COCOA_BEANS, 8, InfusionStoneItem::isGrassLikePlant, Blocks.BROWN_MUSHROOM, 1, 12),
                recipe(ModItems.ALCHEMICAL_DIAMOND.get(), 1, block(Blocks.CHORUS_FLOWER), ModItems.HEALTH_GEM.get(), 1, 15)
        );
    }

    private static InfusionRecipe recipe(
            final ItemLike ingredient,
            final int ingredientCount,
            final TargetMatcher target,
            final ItemLike output,
            final int outputCount,
            final int healthCost
    ) {
        return new InfusionRecipe(ingredient, ingredientCount, target, output, outputCount, healthCost);
    }

    private static TargetMatcher block(final Block block) {
        return state -> state.is(block);
    }

    private static boolean isSapling(final BlockState state) {
        return state.is(BlockTags.SAPLINGS);
    }

    private static boolean isGrassLikePlant(final BlockState state) {
        return state.is(Blocks.SHORT_GRASS)
                || state.is(Blocks.FERN)
                || state.is(Blocks.TALL_GRASS)
                || state.is(Blocks.LARGE_FERN);
    }

    @FunctionalInterface
    private interface TargetMatcher {
        boolean matches(BlockState state);
    }

    private record InfusionRecipe(
            ItemLike ingredient,
            int ingredientCount,
            TargetMatcher target,
            ItemLike output,
            int outputCount,
            int healthCost
    ) {
        boolean matches(final BlockState state, final ItemStack ingredientStack) {
            return this.target.matches(state)
                    && ingredientStack.is(this.ingredient.asItem())
                    && ingredientStack.getCount() >= this.ingredientCount;
        }

        ItemStack createOutput() {
            return new ItemStack(this.output, this.outputCount);
        }
    }
}
