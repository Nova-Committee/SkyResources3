package committee.nova.mods.skyresources3.common.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public final class SurvivalistFishingRodItem extends FishingRodItem {
    private static final int BASE_LURE_TIME_REDUCTION_TICKS = 250;

    public SurvivalistFishingRodItem(final Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack itemStack = player.getItemInHand(hand);
        if (player.fishing != null) {
            if (!level.isClientSide()) {
                player.fishing.retrieve(itemStack);
            }

            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.FISHING_BOBBER_RETRIEVE,
                    SoundSource.NEUTRAL,
                    1.0F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            level.gameEvent(player, GameEvent.ITEM_INTERACT_FINISH, player.position());
        } else {
            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.FISHING_BOBBER_THROW,
                    SoundSource.NEUTRAL,
                    0.5F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            if (level instanceof ServerLevel serverLevel) {
                final int lureTimeReduction = BASE_LURE_TIME_REDUCTION_TICKS
                        + EnchantmentHelper.getFishingSpeedBonus(itemStack) * 20;
                final int luck = EnchantmentHelper.getFishingLuckBonus(itemStack);
                serverLevel.addFreshEntity(new FishingHook(player, level, luck, lureTimeReduction));
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            level.gameEvent(player, GameEvent.ITEM_INTERACT_START, player.position());
        }

        return InteractionResultHolder.success(itemStack);
    }
}
