package committee.nova.mods.skyresources3.common.item;

import committee.nova.mods.skyresources3.Config;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public final class HealthGemItem extends Item {
    private static final String HEALTH_KEY = "health";
    private static final int HEALTH_PER_INJECTION = 2;
    private static final int INJECTION_COOLDOWN_TICKS = 20;

    public HealthGemItem(final Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack itemStack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() || !canInject(player, itemStack)) {
            return InteractionResultHolder.pass(itemStack);
        }

        if (level instanceof ServerLevel serverLevel) {
            player.hurt(player.damageSources().generic(), HEALTH_PER_INJECTION);
            setHealthInjected(itemStack, getHealthInjected(itemStack) + HEALTH_PER_INJECTION);
            player.getCooldowns().addCooldown(this, INJECTION_COOLDOWN_TICKS);
            player.awardStat(Stats.ITEM_USED.get(this));
        }

        return InteractionResultHolder.success(itemStack);
    }

    @Override
    @Deprecated
    public void appendHoverText(
            final ItemStack stack,
            final TooltipContext context,
            final List<Component> tooltipComponents,
            final TooltipFlag tooltipFlag
    ) {
        if (!tooltipFlag.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.skyresources.health_gem.info")
                    .withStyle(ChatFormatting.GREEN));
            return;
        }

        tooltipComponents.add(Component.translatable("item.skyresources.health_gem.inject")
                .withStyle(ChatFormatting.GREEN));
        tooltipComponents.add(Component.translatable(
                "item.skyresources.health_gem.health_injected",
                getHealthInjected(stack)
        ).withStyle(ChatFormatting.RED));
        tooltipComponents.add(Component.translatable(
                "item.skyresources.health_gem.health_gained",
                getHealthBoost(stack)
        ).withStyle(ChatFormatting.DARK_RED));
    }

    public static int getHealthBoost(final ItemStack itemStack) {
        return (int) (getHealthInjected(itemStack) * Config.healthGemPercentage);
    }

    public static int getHealthInjected(final ItemStack itemStack) {
        final CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.contains(HEALTH_KEY) ? tag.getInt(HEALTH_KEY) : 0;
    }

    public static boolean canReceiveHealth(final ItemStack itemStack, final int health) {
        return health > 0
                && itemStack.getItem() instanceof HealthGemItem
                && getHealthInjected(itemStack) + health <= Config.healthGemMaxHealth;
    }

    public static boolean addStoredHealth(final ItemStack itemStack, final int health) {
        if (!canReceiveHealth(itemStack, health)) {
            return false;
        }
        setHealthInjected(itemStack, getHealthInjected(itemStack) + health);
        return true;
    }

    public static boolean canConsumeStoredHealth(final ItemStack itemStack, final int health) {
        return health > 0
                && itemStack.getItem() instanceof HealthGemItem
                && getHealthInjected(itemStack) >= health;
    }

    public static boolean consumeStoredHealth(final ItemStack itemStack, final int health) {
        if (!canConsumeStoredHealth(itemStack, health)) {
            return false;
        }
        setHealthInjected(itemStack, getHealthInjected(itemStack) - health);
        return true;
    }

    private static boolean canInject(final Player player, final ItemStack itemStack) {
        final ItemCooldowns cooldowns = player.getCooldowns();
        return !cooldowns.isOnCooldown(itemStack.getItem())
                && getHealthInjected(itemStack) + HEALTH_PER_INJECTION <= Config.healthGemMaxHealth;
    }

    private static void setHealthInjected(final ItemStack itemStack, final int health) {
        final CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt(HEALTH_KEY, Math.min(health, Config.healthGemMaxHealth));
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
