package committee.nova.mods.skyresources3.common.item;

import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public final class InstantBonemealItem extends Item {
    private final BooleanSupplier enabled;

    public InstantBonemealItem(final Properties properties, final BooleanSupplier enabled) {
        super(properties);
        this.enabled = enabled;
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        if (!this.enabled.getAsBoolean()) {
            return InteractionResult.PASS;
        }

        final Level level = context.getLevel();
        final BlockPos target = context.getClickedPos();
        if (!BonemealGrowth.isValidTarget(level, target)) {
            return InteractionResult.PASS;
        }

        if (level instanceof ServerLevel serverLevel) {
            BonemealGrowth.growUntilStable(serverLevel, target);
            final ItemStack itemStack = context.getItemInHand();
            itemStack.shrink(1);
            itemStack.causeUseVibration(context.getPlayer(), GameEvent.ITEM_INTERACT_FINISH);
            level.levelEvent(1505, target, 15);
        }

        return InteractionResult.SUCCESS;
    }
}
