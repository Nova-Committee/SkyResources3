package committee.nova.mods.skyresources3.common.item;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.common.recipe.InfusionRecipes;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
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

        if (!(level instanceof ServerLevel serverLevel)) {
            return clientInteractionResult(level, pos, player);
        }

        final Optional<InfusionRecipes.Match> recipe =
                InfusionRecipes.find(serverLevel, level.getBlockState(pos), player.getOffhandItem());
        if (recipe.isPresent()) {
            tryInfuse(context, player, recipe.get());
            return InteractionResult.SUCCESS_SERVER;
        }

        if (!Config.infusionStoneBonemealCapability || !BonemealGrowth.isValidTarget(level, pos)) {
            return InteractionResult.PASS;
        }
        BonemealGrowth.growUntilStable(serverLevel, pos);
        hurtStoneAndPlayer(context, player, BONEMEAL_HEALTH_COST);
        context.getItemInHand().causeUseVibration(player, GameEvent.ITEM_INTERACT_FINISH);
        level.levelEvent(1505, pos, 15);
        return InteractionResult.SUCCESS_SERVER;
    }

    private static InteractionResult clientInteractionResult(final Level level, final BlockPos pos, final Player player) {
        if (!player.getOffhandItem().isEmpty()) {
            return InteractionResult.SUCCESS;
        }
        return Config.infusionStoneBonemealCapability && BonemealGrowth.isValidTarget(level, pos)
                ? InteractionResult.SUCCESS
                : InteractionResult.PASS;
    }

    private static void tryInfuse(
            final UseOnContext context,
            final Player player,
            final InfusionRecipes.Match recipe
    ) {
        final Level level = context.getLevel();
        if (player.getMaxHealth() < recipe.healthCost()) {
            player.displayClientMessage(
                    Component.translatable("message.skyresources.infusion_stone.max_health_too_low"),
                    true
            );
            return;
        }
        if (player.getHealth() < recipe.healthCost()) {
            player.displayClientMessage(
                    Component.translatable("message.skyresources.infusion_stone.not_enough_health"),
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
}
