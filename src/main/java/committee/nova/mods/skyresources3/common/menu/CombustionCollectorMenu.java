package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.common.block.entity.CombustionCollectorBlockEntity;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import committee.nova.mods.skyresources3.init.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

public final class CombustionCollectorMenu extends AbstractCombustionInventoryMenu {
    public CombustionCollectorMenu(
            final int containerId,
            final Inventory playerInventory,
            final FriendlyByteBuf data
    ) {
        super(
                ModMenuTypes.COMBUSTION_COLLECTOR.get(),
                ModBlocks.COMBUSTION_COLLECTOR.get(),
                containerId,
                playerInventory,
                readClientData(playerInventory, data)
        );
    }

    public CombustionCollectorMenu(
            final int containerId,
            final Inventory playerInventory,
            final CombustionCollectorBlockEntity blockEntity
    ) {
        super(
                ModMenuTypes.COMBUSTION_COLLECTOR.get(),
                ModBlocks.COMBUSTION_COLLECTOR.get(),
                containerId,
                playerInventory,
                new ClientData(
                        blockEntity.getBlockPos(),
                        blockEntity,
                        ContainerLevelAccess.create(playerInventory.player.level(), blockEntity.getBlockPos())
                )
        );
    }
}
