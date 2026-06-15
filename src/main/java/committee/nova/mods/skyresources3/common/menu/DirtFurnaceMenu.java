package committee.nova.mods.skyresources3.common.menu;

import committee.nova.mods.skyresources3.init.registry.ModMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;

public final class DirtFurnaceMenu extends AbstractFurnaceMenu {
    public DirtFurnaceMenu(
            final int containerId,
            final Inventory playerInventory,
            final RegistryFriendlyByteBuf data
    ) {
        this(containerId, playerInventory);
    }

    public DirtFurnaceMenu(final int containerId, final Inventory playerInventory) {
        super(
                ModMenuTypes.DIRT_FURNACE.get(),
                RecipeType.SMELTING,
                RecipePropertySet.FURNACE_INPUT,
                RecipeBookType.FURNACE,
                containerId,
                playerInventory
        );
    }

    public DirtFurnaceMenu(
            final int containerId,
            final Inventory playerInventory,
            final Container container,
            final ContainerData data
    ) {
        super(
                ModMenuTypes.DIRT_FURNACE.get(),
                RecipeType.SMELTING,
                RecipePropertySet.FURNACE_INPUT,
                RecipeBookType.FURNACE,
                containerId,
                playerInventory,
                container,
                data
        );
    }
}
