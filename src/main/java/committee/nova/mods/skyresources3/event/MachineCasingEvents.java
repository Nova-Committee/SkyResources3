package committee.nova.mods.skyresources3.event;

import committee.nova.mods.skyresources3.block.MachineCasingInteractions;
import committee.nova.mods.skyresources3.block.entity.MachineCasingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class MachineCasingEvents {
    public static void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled() || event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        final Player player = event.getEntity();
        if (player.isSpectator() || !player.isShiftKeyDown()) {
            return;
        }

        final Level level = event.getLevel();
        final BlockPos pos = event.getPos();
        final ItemStack stack = player.getItemInHand(event.getHand());
        if (!level.mayInteract(player, pos)
                || !player.mayUseItemAt(pos, event.getHitVec().getDirection(), stack)
                || !(level.getBlockEntity(pos) instanceof MachineCasingBlockEntity casing)
                || !casing.hasHeater()) {
            return;
        }

        final InteractionResult result = MachineCasingInteractions.removeHeater(level, player, casing);
        if (!result.consumesAction()) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(result);
    }

    private MachineCasingEvents() {
    }
}
