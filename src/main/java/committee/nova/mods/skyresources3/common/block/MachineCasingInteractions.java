package committee.nova.mods.skyresources3.common.block;

import committee.nova.mods.skyresources3.common.block.entity.MachineCasingBlockEntity;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class MachineCasingInteractions {
    public static InteractionResult removeHeater(final Level level, final Player player, final MachineCasingBlockEntity casing) {
        if (!casing.hasHeater()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        final ItemStack removed = casing.removeHeater();
        if (!removed.isEmpty() && !player.addItem(removed)) {
            Containers.dropItemStack(level, player.getX(), player.getY(), player.getZ(), removed);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    private MachineCasingInteractions() {
    }
}
