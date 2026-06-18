package committee.nova.mods.skyresources3.common.block;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;

final class BlockInteractionResults {
    static ItemInteractionResult item(final InteractionResult result) {
        if (result == InteractionResult.FAIL) {
            return ItemInteractionResult.FAIL;
        }
        if (result.consumesAction()) {
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private BlockInteractionResults() {
    }
}
