package committee.nova.mods.skyresources3.event;

import committee.nova.mods.skyresources3.item.RockGrinderItem;
import committee.nova.mods.skyresources3.recipe.ProcessRecipes;
import committee.nova.mods.skyresources3.recipe.SkyResourcesProcessRecipe;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class RockGrinderEvents {
    public static void onBlockBreak(final BlockEvent.BreakEvent event) {
        if (event.isCanceled() || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        final Player player = event.getPlayer();
        if (player.isCreative()) {
            return;
        }

        final ItemStack tool = player.getMainHandItem();
        if (!(tool.getItem() instanceof RockGrinderItem)) {
            return;
        }

        final List<GrinderDrop> drops = findDrops(level, event.getState());
        if (drops.isEmpty()) {
            return;
        }

        event.setCanceled(true);
        if (!level.destroyBlock(event.getPos(), false, player)) {
            return;
        }
        for (final GrinderDrop drop : drops) {
            ProcessingToolDrops.popWithFortune(level, event.getPos(), drop.stack(), player, tool, drop.chance());
        }
        tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
    }

    public static boolean hasResult(final BlockState state) {
        return state.is(Blocks.COBBLESTONE)
                || state.is(Blocks.GRAVEL)
                || state.is(Blocks.STONE)
                || state.is(Blocks.NETHERRACK)
                || state.is(BlockTags.LOGS);
    }

    private static List<GrinderDrop> findDrops(final ServerLevel level, final BlockState state) {
        final ItemStack input = stackForState(state);
        if (input.isEmpty()) {
            return List.of();
        }
        return ProcessRecipes.findAll(level, ProcessRecipes.ROCK_GRINDER, List.of(input))
                .stream()
                .flatMap(recipe -> dropsFor(recipe.value()).stream())
                .toList();
    }

    private static List<GrinderDrop> dropsFor(final SkyResourcesProcessRecipe recipe) {
        return recipe.outputs()
                .stream()
                .filter(output -> !output.isEmpty())
                .map(output -> new GrinderDrop(output, recipe.parameter()))
                .toList();
    }

    private static ItemStack stackForState(final BlockState state) {
        final Item item = state.getBlock().asItem();
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }

    private record GrinderDrop(ItemStack stack, float chance) {
    }

    private RockGrinderEvents() {
    }
}
