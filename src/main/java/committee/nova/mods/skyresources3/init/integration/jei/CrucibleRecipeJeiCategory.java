package committee.nova.mods.skyresources3.init.integration.jei;

import committee.nova.mods.skyresources3.common.recipe.CrucibleRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;

final class CrucibleRecipeJeiCategory implements IRecipeCategory<CrucibleRecipe> {
    private static final int WIDTH = 120;
    private static final int HEIGHT = 64;
    private static final int TEXT_COLOR = 0xFF404040;

    private final IDrawable icon;
    private final IDrawable arrow;

    CrucibleRecipeJeiCategory(final IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new net.minecraft.world.item.ItemStack(
                committee.nova.mods.skyresources3.init.registry.ModItems.CRUCIBLE.get()
        ));
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public RecipeType<CrucibleRecipe> getRecipeType() {
        return SkyResourcesJeiRecipeTypes.CRUCIBLE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.skyresources.category.crucible");
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
    public void setRecipe(final IRecipeLayoutBuilder builder, final CrucibleRecipe recipe, final IFocusGroup focuses) {
        builder.addInputSlot(10, 22)
                .setStandardSlotBackground()
                .addItemStacks(JeiIngredientStacks.stacks(recipe.input()));
        final FluidStack output = recipe.output();
        builder.addOutputSlot(86, 22)
                .setOutputSlotBackground()
                .setFluidRenderer(output.getAmount(), true, 16, 16)
                .addFluidStack(output.getFluid(), output.getAmount());
    }

    @Override
    public void draw(
            final CrucibleRecipe recipe,
            final IRecipeSlotsView recipeSlotsView,
            final GuiGraphics guiGraphics,
            final double mouseX,
            final double mouseY
    ) {
        this.arrow.draw(guiGraphics, 48, 24);
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                Component.translatable("jei.skyresources.fluid_amount", recipe.output().getAmount()),
                4,
                52,
                TEXT_COLOR,
                false
        );
    }
}
