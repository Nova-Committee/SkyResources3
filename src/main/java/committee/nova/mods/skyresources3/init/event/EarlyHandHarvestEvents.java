package committee.nova.mods.skyresources3.init.event;

import committee.nova.mods.skyresources3.init.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class EarlyHandHarvestEvents {
    public static void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        final Player player = event.getEntity();
        if (event.getHand() != InteractionHand.MAIN_HAND
                || player.isSpectator()
                || !player.isShiftKeyDown()
                || !player.getMainHandItem().isEmpty()) {
            return;
        }

        final Level level = event.getLevel();
        final BlockPos pos = event.getPos();
        final BlockState state = level.getBlockState(pos);
        if (!isHarvestTarget(state)
                || !level.mayInteract(player, pos)
                || !player.mayUseItemAt(pos, event.getHitVec().getDirection(), ItemStack.EMPTY)) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (state.is(Blocks.CACTUS)) {
            harvestCactusNeedle(serverLevel, player);
            return;
        }
        if (state.is(Blocks.SNOW)) {
            harvestSnowLayer(serverLevel, player, pos, state);
            return;
        }
        if (state.is(Blocks.SNOW_BLOCK)) {
            harvestSnowBlock(serverLevel, player, pos);
        }
    }

    private static boolean isHarvestTarget(final BlockState state) {
        return state.is(Blocks.CACTUS) || state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK);
    }

    private static void harvestCactusNeedle(final ServerLevel level, final Player player) {
        Block.popResource(level, player.blockPosition(), new ItemStack(ModItems.CACTUS_NEEDLE.get()));
        player.hurtServer(level, level.damageSources().cactus(), 2.0F);
    }

    private static void harvestSnowLayer(
            final ServerLevel level,
            final Player player,
            final BlockPos pos,
            final BlockState state
    ) {
        popSnowballAndFatigue(level, player, pos);
        final int layers = state.getValue(SnowLayerBlock.LAYERS);
        if (layers <= 1) {
            level.destroyBlock(pos, false, player);
            return;
        }
        level.setBlock(pos, state.setValue(SnowLayerBlock.LAYERS, layers - 1), Block.UPDATE_ALL);
    }

    private static void harvestSnowBlock(final ServerLevel level, final Player player, final BlockPos pos) {
        popSnowballAndFatigue(level, player, pos);
        level.setBlock(pos, Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 7), Block.UPDATE_ALL);
    }

    private static void popSnowballAndFatigue(final ServerLevel level, final Player player, final BlockPos pos) {
        Block.popResource(level, pos, new ItemStack(Items.SNOWBALL));
        player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, level.random.nextInt(80) + 20, 1));
    }

    private EarlyHandHarvestEvents() {
    }
}
