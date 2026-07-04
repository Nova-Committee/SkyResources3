package committee.nova.mods.skyresources3.init.event;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.item.HealthGemItem;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

public final class HealthGemEvents {
    private static final int UPDATE_INTERVAL_TICKS = 20;
    private static final ResourceLocation MAX_HEALTH_MODIFIER_ID =
            new ResourceLocation(Skyresources3.MODID, "health_gem_max_health");
    private static final UUID MAX_HEALTH_MODIFIER_UUID =
            UUID.nameUUIDFromBytes(MAX_HEALTH_MODIFIER_ID.toString().getBytes(StandardCharsets.UTF_8));

    public static void onPlayerTick(final PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        final Player player = event.player;
        if (player.level().isClientSide() || player.tickCount % UPDATE_INTERVAL_TICKS != 0) {
            return;
        }

        final AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }

        final int boost = getInventoryHealthBoost(player);
        if (boost <= 0) {
            maxHealth.removeModifier(MAX_HEALTH_MODIFIER_UUID);
            clampHealth(player);
            return;
        }

        final AttributeModifier current = maxHealth.getModifier(MAX_HEALTH_MODIFIER_UUID);
        if (current == null || current.getAmount() != boost) {
            maxHealth.removeModifier(MAX_HEALTH_MODIFIER_UUID);
            maxHealth.addTransientModifier(new AttributeModifier(
                    MAX_HEALTH_MODIFIER_UUID,
                    MAX_HEALTH_MODIFIER_ID.toString(),
                    boost,
                    AttributeModifier.Operation.ADDITION
            ));
        }
        clampHealth(player);
    }

    private static int getInventoryHealthBoost(final Player player) {
        int boost = 0;
        for (int slot = 0; slot < Inventory.INVENTORY_SIZE; slot++) {
            final ItemStack stack = player.getInventory().getItem(slot);
            if (stack.is(ModItems.HEALTH_GEM.get())) {
                boost += HealthGemItem.getHealthBoost(stack);
            }
        }
        return boost;
    }

    private static void clampHealth(final Player player) {
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private HealthGemEvents() {
    }
}
