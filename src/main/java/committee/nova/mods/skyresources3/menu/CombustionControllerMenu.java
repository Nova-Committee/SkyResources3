package committee.nova.mods.skyresources3.menu;

import committee.nova.mods.skyresources3.block.entity.CombustionControllerBlockEntity;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

public final class CombustionControllerMenu extends AbstractCombustionInventoryMenu {
    public CombustionControllerMenu(
            final int containerId,
            final Inventory playerInventory,
            final RegistryFriendlyByteBuf data
    ) {
        super(
                ModMenuTypes.COMBUSTION_CONTROLLER.get(),
                ModBlocks.COMBUSTION_CONTROLLER.get(),
                containerId,
                playerInventory,
                readClientData(playerInventory, data)
        );
    }

    public CombustionControllerMenu(
            final int containerId,
            final Inventory playerInventory,
            final CombustionControllerBlockEntity blockEntity
    ) {
        super(
                ModMenuTypes.COMBUSTION_CONTROLLER.get(),
                ModBlocks.COMBUSTION_CONTROLLER.get(),
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
