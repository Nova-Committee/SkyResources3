package committee.nova.mods.skyresources3.event;

import committee.nova.mods.skyresources3.item.CuttingKnifeItem;
import committee.nova.mods.skyresources3.recipe.ProcessRecipes;
import committee.nova.mods.skyresources3.recipe.SkyResourcesProcessRecipe;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class CuttingKnifeEvents {
    public static void onBlockBreak(final BlockEvent.BreakEvent event) {
        if (event.isCanceled() || !(event.getLevel() instanceof ServerLevel level)) {
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

        final Optional<ItemStack> result = findResult(level, event.getState());
        if (result.isEmpty()) {
            return;
        }

        event.setCanceled(true);
        if (!level.destroyBlock(event.getPos(), false, player)) {
            return;
        }
        ProcessingToolDrops.popWithFortune(level, event.getPos(), result.get(), player, tool, 1.0F);
        tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
    }

    public static float getDestroySpeed(final CuttingKnifeItem knife, final BlockState state) {
        return hasBuiltinResultHint(state) ? knife.miningSpeed() : 1.0F;
    }

    private static Optional<ItemStack> findResult(final ServerLevel level, final BlockState state) {
        final ItemStack input = stackForState(state);
        if (input.isEmpty()) {
            return Optional.empty();
        }

        return ProcessRecipes.find(level, ProcessRecipes.KNIFE, List.of(input))
                .flatMap(CuttingKnifeEvents::firstOutput);
    }

    private static Optional<ItemStack> firstOutput(final RecipeHolder<SkyResourcesProcessRecipe> recipe) {
        final List<ItemStack> outputs = recipe.value().outputs();
        return outputs.isEmpty() ? Optional.empty() : Optional.of(outputs.getFirst());
    }

    private static ItemStack stackForState(final BlockState state) {
        final Item item = state.getBlock().asItem();
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static boolean hasBuiltinResultHint(final BlockState state) {
        return state.is(Blocks.CACTUS)
                || state.is(Blocks.MELON)
                || state.is(ModBlocks.PETRIFIED_WOOD.get())
                || state.is(ModBlocks.PETRIFIED_PLANKS.get())
                || state.is(BlockTags.PLANKS)
                || state.is(Blocks.OAK_LOG)
                || state.is(Blocks.SPRUCE_LOG)
                || state.is(Blocks.BIRCH_LOG)
                || state.is(Blocks.JUNGLE_LOG)
                || state.is(Blocks.ACACIA_LOG)
                || state.is(Blocks.DARK_OAK_LOG);
    }

    private CuttingKnifeEvents() {
    }
}
