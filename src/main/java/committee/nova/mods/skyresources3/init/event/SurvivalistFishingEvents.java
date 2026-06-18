package committee.nova.mods.skyresources3.init.event;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

public final class SurvivalistFishingEvents {
    private static final ResourceKey<LootTable> SURVIVALIST_FISHING = ResourceKey.create(
            Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(Skyresources3.MODID, "gameplay/fishingsurvivalist")
    );

    public static void onItemFished(final ItemFishedEvent event) {
        final Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        final ItemStack rod = findSurvivalistRod(player);
        if (rod.isEmpty()) {
            return;
        }

        final FishingHook hook = event.getHookEntity();
        if (!(hook.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        final LootParams lootParams = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, hook.position())
                .withParameter(LootContextParams.TOOL, rod)
                .withParameter(LootContextParams.THIS_ENTITY, hook)
                .withParameter(LootContextParams.ATTACKING_ENTITY, serverPlayer)
                .withLuck(EnchantmentHelper.getFishingLuckBonus(serverLevel, rod, serverPlayer) + serverPlayer.getLuck())
                .create(LootContextParamSets.FISHING);
        final LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(SURVIVALIST_FISHING);
        final List<ItemStack> drops = lootTable.getRandomItems(lootParams);

        CriteriaTriggers.FISHING_ROD_HOOKED.trigger(serverPlayer, rod, hook, drops);
        for (final ItemStack drop : drops) {
            spawnDrop(serverLevel, serverPlayer, hook, drop);
        }

        event.damageRodBy(0);
        event.setCanceled(true);
    }

    private static ItemStack findSurvivalistRod(final Player player) {
        final ItemStack mainHand = player.getMainHandItem();
        if (mainHand.is(ModItems.SURVIVALIST_FISHING_ROD.get())) {
            return mainHand;
        }

        final ItemStack offHand = player.getOffhandItem();
        if (offHand.is(ModItems.SURVIVALIST_FISHING_ROD.get())) {
            return offHand;
        }

        return ItemStack.EMPTY;
    }

    private static void spawnDrop(
            final ServerLevel level,
            final ServerPlayer player,
            final FishingHook hook,
            final ItemStack drop
    ) {
        final ItemEntity itemEntity = new ItemEntity(level, hook.getX(), hook.getY(), hook.getZ(), drop);
        final double xDistance = player.getX() - hook.getX();
        final double yDistance = player.getY() - hook.getY();
        final double zDistance = player.getZ() - hook.getZ();
        final double distance = xDistance * xDistance + yDistance * yDistance + zDistance * zDistance;
        itemEntity.setDeltaMovement(
                xDistance * 0.1,
                yDistance * 0.1 + Math.sqrt(Math.sqrt(distance)) * 0.08,
                zDistance * 0.1
        );
        level.addFreshEntity(itemEntity);
        level.addFreshEntity(new ExperienceOrb(
                level,
                player.getX(),
                player.getY() + 0.5,
                player.getZ() + 0.5,
                hook.getRandom().nextInt(6) + 1
        ));
        if (drop.is(ItemTags.FISHES)) {
            player.awardStat(Stats.FISH_CAUGHT, 1);
        }
    }

    private SurvivalistFishingEvents() {
    }
}
