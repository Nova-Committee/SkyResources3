package committee.nova.mods.skyresources3.event;

import committee.nova.mods.skyresources3.item.RockGrinderItem;
import committee.nova.mods.skyresources3.registry.ModItems;
import java.util.List;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;

public final class RockGrinderEvents {
    public static void onBlockBreak(final BlockEvent.BreakEvent event) {
        if (event.isCanceled() || !(event.getLevel() instanceof Level level) || level.isClientSide()) {
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

        final List<GrinderDrop> drops = findDrops(event.getState());
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

    private static List<GrinderDrop> findDrops(final BlockState state) {
        if (state.is(Blocks.COBBLESTONE)) {
            return List.of(new GrinderDrop(new ItemStack(Blocks.GRAVEL), 1.0F));
        }
        if (state.is(Blocks.GRAVEL)) {
            return List.of(
                    new GrinderDrop(new ItemStack(Blocks.SAND), 1.0F),
                    new GrinderDrop(new ItemStack(Items.FLINT), 0.3F)
            );
        }
        if (state.is(Blocks.STONE)) {
            return List.of(new GrinderDrop(new ItemStack(ModItems.CRUSHED_STONE.get()), 0.44F));
        }
        if (state.is(Blocks.NETHERRACK)) {
            return List.of(new GrinderDrop(new ItemStack(ModItems.CRUSHED_NETHERRACK.get()), 0.44F));
        }
        if (state.is(BlockTags.LOGS)) {
            return List.of(new GrinderDrop(new ItemStack(ModItems.SAWDUST.get()), 1.5F));
        }
        return List.of();
    }

    private record GrinderDrop(ItemStack stack, float chance) {
    }

    private RockGrinderEvents() {
    }
}
