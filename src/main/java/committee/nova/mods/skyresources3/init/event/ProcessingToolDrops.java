package committee.nova.mods.skyresources3.init.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

final class ProcessingToolDrops {
    static void popWithFortune(
            final Level level,
            final BlockPos pos,
            final ItemStack result,
            final Player player,
            final ItemStack tool,
            final float baseChance
    ) {
        float rolls = baseChance * (((float) getFortuneLevel(level, tool) + 3.0F) / 3.0F);
        while (rolls >= 1.0F) {
            Block.popResource(level, pos, result.copy());
            rolls -= 1.0F;
        }
        if (rolls > 0.0F && player.getRandom().nextFloat() <= rolls) {
            Block.popResource(level, pos, result.copy());
        }
    }

    private static int getFortuneLevel(final Level level, final ItemStack tool) {
        return tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
    }

    private ProcessingToolDrops() {
    }
}
