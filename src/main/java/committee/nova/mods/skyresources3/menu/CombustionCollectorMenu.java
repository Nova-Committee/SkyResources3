package committee.nova.mods.skyresources3.menu;

import committee.nova.mods.skyresources3.block.entity.CombustionCollectorBlockEntity;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

public final class CombustionCollectorMenu extends AbstractCombustionInventoryMenu {
    public CombustionCollectorMenu(
            final int containerId,
            final Inventory playerInventory,
            final RegistryFriendlyByteBuf data
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
