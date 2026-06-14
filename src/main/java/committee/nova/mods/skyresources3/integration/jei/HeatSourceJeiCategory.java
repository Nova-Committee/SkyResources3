package committee.nova.mods.skyresources3.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

final class HeatSourceJeiCategory implements IRecipeCategory<HeatSourceJeiRecipe> {
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int TEXT_COLOR = 0xFF404040;

    private final IDrawable icon;

    HeatSourceJeiCategory(final IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Items.LAVA_BUCKET));
    }

    @Override
    public IRecipeType<HeatSourceJeiRecipe> getRecipeType() {
        return SkyResourcesJeiRecipeTypes.HEAT_SOURCES;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.skyresources.category.heat_sources");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(final IRecipeLayoutBuilder builder, final HeatSourceJeiRecipe recipe, final IFocusGroup focuses) {
        builder.addInputSlot(8, 11)
                .setStandardSlotBackground()
                .add(recipe.source());
    }

    @Override
    public void draw(
            final HeatSourceJeiRecipe recipe,
            final IRecipeSlotsView recipeSlotsView,
            final GuiGraphics guiGraphics,
            final double mouseX,
            final double mouseY
    ) {
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                Component.translatable("jei.skyresources.heat_value", recipe.heat()),
                34,
                16,
                TEXT_COLOR,
                false
        );
    }
}
