package committee.nova.mods.skyresources3.init.event;

import committee.nova.mods.skyresources3.common.recipe.ProcessRecipes;
import committee.nova.mods.skyresources3.common.recipe.SkyResourcesProcessRecipe;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class CauldronCleanEvents {
    private static final float WATER_USE_CHANCE = 0.16F;

    public static void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        final Player player = event.getEntity();
        final ItemStack stack = player.getMainHandItem();
        final BlockPos pos = event.getPos();
        final BlockState state = level.getBlockState(pos);
        if (stack.isEmpty()
                || !state.is(Blocks.WATER_CAULDRON)
                || !level.mayInteract(player, pos)
                || !player.mayUseItemAt(pos, event.getHitVec().getDirection(), stack)) {
            return;
        }

        final List<RecipeHolder<SkyResourcesProcessRecipe>> recipes =
                ProcessRecipes.findAll(level, ProcessRecipes.CAULDRON_CLEAN, List.of(stack.copy()));
        if (recipes.isEmpty()) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        for (final RecipeHolder<SkyResourcesProcessRecipe> holder : recipes) {
            final SkyResourcesProcessRecipe recipe = holder.value();
            for (final ItemStack output : recipe.outputs()) {
                if (!output.isEmpty() && level.random.nextFloat() <= recipe.parameter()) {
                    Block.popResource(level, pos.above(), output.copy());
                }
            }
        }

        if (!player.getAbilities().instabuild) {
            final int consumed = Math.max(1, recipes.getFirst().value().inputs().getFirst().count());
            stack.shrink(consumed);
            if (stack.isEmpty()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
        if (level.random.nextFloat() < WATER_USE_CHANCE) {
            lowerWaterLevel(level, pos, state);
        }
    }

    private static void lowerWaterLevel(final ServerLevel level, final BlockPos pos, final BlockState state) {
        final int cauldronLevel = state.getValue(LayeredCauldronBlock.LEVEL);
        if (cauldronLevel <= 1) {
            level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
            return;
        }
        level.setBlock(pos, state.setValue(LayeredCauldronBlock.LEVEL, cauldronLevel - 1), Block.UPDATE_ALL);
    }

    private CauldronCleanEvents() {
    }
}
