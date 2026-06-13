package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.menu.DirtFurnaceMenu;
import java.util.List;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;

public final class DirtFurnaceScreen extends AbstractFurnaceScreen<DirtFurnaceMenu> {
    private static final Identifier LIT_PROGRESS_SPRITE =
            Identifier.fromNamespaceAndPath("minecraft", "container/furnace/lit_progress");
    private static final Identifier BURN_PROGRESS_SPRITE =
            Identifier.fromNamespaceAndPath("minecraft", "container/furnace/burn_progress");
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/furnace.png");
    private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.smeltable");
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
            new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.FURNACE),
            new RecipeBookComponent.TabInfo(Items.PORKCHOP, RecipeBookCategories.FURNACE_FOOD),
            new RecipeBookComponent.TabInfo(Items.STONE, RecipeBookCategories.FURNACE_BLOCKS),
            new RecipeBookComponent.TabInfo(Items.LAVA_BUCKET, Items.EMERALD, RecipeBookCategories.FURNACE_MISC)
    );

    public DirtFurnaceScreen(
            final DirtFurnaceMenu menu,
            final Inventory playerInventory,
            final Component title
    ) {
        super(menu, playerInventory, title, FILTER_NAME, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE, TABS);
    }
}
