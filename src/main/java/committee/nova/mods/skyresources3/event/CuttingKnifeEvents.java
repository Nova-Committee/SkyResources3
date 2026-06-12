package committee.nova.mods.skyresources3.event;

import committee.nova.mods.skyresources3.item.CuttingKnifeItem;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModItems;
import java.util.Map;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class CuttingKnifeEvents {
    private static final Map<Block, ItemLike> LOG_TO_PLANKS = Map.of(
            Blocks.OAK_LOG, Blocks.OAK_PLANKS,
            Blocks.SPRUCE_LOG, Blocks.SPRUCE_PLANKS,
            Blocks.BIRCH_LOG, Blocks.BIRCH_PLANKS,
            Blocks.JUNGLE_LOG, Blocks.JUNGLE_PLANKS,
            Blocks.ACACIA_LOG, Blocks.ACACIA_PLANKS,
            Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS
    );

    public static void onBlockBreak(final BlockEvent.BreakEvent event) {
        if (event.isCanceled() || !(event.getLevel() instanceof Level level) || level.isClientSide()) {
            return;
        }

        final Player player = event.getPlayer();
        if (player.isCreative()) {
            return;
        }

        final ItemStack tool = player.getMainHandItem();
        if (!(tool.getItem() instanceof CuttingKnifeItem knife)) {
            return;
        }

        final ItemStack result = findResult(event.getState());
        if (result.isEmpty()) {
            return;
        }

        event.setCanceled(true);
        if (!level.destroyBlock(event.getPos(), false, player)) {
            return;
        }
        ProcessingToolDrops.popWithFortune(level, event.getPos(), result, player, tool, 1.0F);
        tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
    }

    public static float getDestroySpeed(final CuttingKnifeItem knife, final BlockState state) {
        return findResult(state).isEmpty() ? 1.0F : knife.miningSpeed();
    }

    private static ItemStack findResult(final BlockState state) {
        if (state.is(Blocks.CACTUS)) {
            return new ItemStack(ModItems.CACTUS_FRUIT.get(), 2);
        }
        if (state.is(Blocks.MELON)) {
            return new ItemStack(Items.MELON_SLICE, 9);
        }
        if (state.is(ModBlocks.PETRIFIED_WOOD.get())) {
            return new ItemStack(ModBlocks.PETRIFIED_PLANKS.get(), 6);
        }
        if (state.is(ModBlocks.PETRIFIED_PLANKS.get()) || state.is(BlockTags.PLANKS)) {
            return new ItemStack(Items.STICK, 6);
        }

        final ItemLike planks = LOG_TO_PLANKS.get(state.getBlock());
        return planks == null ? ItemStack.EMPTY : new ItemStack(planks, 6);
    }

    private CuttingKnifeEvents() {
    }
}
