package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.client.utils.MachineGuiTheme;
import committee.nova.mods.skyresources3.common.menu.DirtFurnaceMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;

public final class DirtFurnaceScreen extends AbstractFurnaceScreen<DirtFurnaceMenu> {
    private static final int FUEL_X = 40;
    private static final int FUEL_Y = 28;
    private static final int FUEL_WIDTH = 10;
    private static final int FUEL_HEIGHT = 35;
    private static final int PROGRESS_X = 74;
    private static final int PROGRESS_Y = 30;
    private static final int PROGRESS_WIDTH = 40;
    private static final int STATUS_X = 104;
    private static final int STATUS_Y = 56;
    private static final int STATUS_WIDTH = 58;

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

    @Override
    protected void renderBg(
            final GuiGraphics guiGraphics,
            final float partialTick,
            final int mouseX,
            final int mouseY
    ) {
        MachineGuiTheme.renderPanel(guiGraphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        MachineGuiTheme.renderSlots(guiGraphics, this.leftPos, this.topPos, this.menu.slots);
    }

    @Override
    protected void renderLabels(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        MachineGuiTheme.renderTitle(guiGraphics, this.font, this.title, this.imageWidth);
        MachineGuiTheme.renderVerticalGauge(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.fuel"),
                FUEL_X,
                FUEL_Y,
                FUEL_WIDTH,
                FUEL_HEIGHT,
                this.menu.getLitProgress(),
                MachineGuiTheme.HEAT
        );
        MachineGuiTheme.renderHorizontalGauge(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.progress"),
                MachineGuiTheme.percent(this.menu.getBurnProgress()),
                PROGRESS_X,
                PROGRESS_Y,
                PROGRESS_WIDTH,
                this.menu.getBurnProgress(),
                MachineGuiTheme.PROGRESS
        );
        MachineGuiTheme.renderStatus(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.status"),
                Component.translatable(this.menu.isLit()
                        ? "screen.skyresources.metric.active"
                        : "screen.skyresources.metric.idle"),
                STATUS_X,
                STATUS_Y,
                STATUS_WIDTH,
                this.menu.isLit()
        );
        MachineGuiTheme.renderInventoryLabel(
                guiGraphics,
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY
        );
    }
}
