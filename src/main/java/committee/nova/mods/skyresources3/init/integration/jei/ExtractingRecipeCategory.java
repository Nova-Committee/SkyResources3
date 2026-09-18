package committee.nova.mods.skyresources3.init.integration.jei;

import committee.nova.mods.skyresources3.common.recipe.ExtractingRecipe;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ExtractingRecipeCategory extends AbstractRecipeCategory<ExtractingRecipe> {
    private static final int WIDTH = 110;
    private static final int HEIGHT = 34;

    private final IDrawable fluidBackground;
    private final IDrawable fluidOverlay;

    public ExtractingRecipeCategory(IGuiHelper helper) {
        super(
                SkyResourcesJeiRecipeTypes.EXTRACTING,
                Component.translatable("jei.skyresources.category.extracting"),
                helper.createDrawableItemLike(ModItems.WATER_EXTRACTOR.get()),
                WIDTH,
                HEIGHT
        );
        fluidBackground = helper.createDrawable(SkyResourcesJeiPlugin.ICONS, 0, 0, 18, 34);
        fluidOverlay = helper.createDrawable(SkyResourcesJeiPlugin.ICONS, 18, 0, 18, 34);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ExtractingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(1,9)
                .setStandardSlotBackground()
                .addItemStacks(JeiIngredientStacks.stacks(recipe.getInput()));

        builder.addOutputSlot(61,1)
                .setFluidRenderer(100,false,16,32)
                .setBackground(fluidBackground,-1,-1)
                .setOverlay(fluidOverlay,-1,-1)
                .addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutputFluid());

        builder.addOutputSlot(88,9)
                .setOutputSlotBackground()
                .addItemStack(recipe.getOutputBlock());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, ExtractingRecipe recipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(26, 9);
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(ExtractingRecipe recipe) {
        return recipe.getId();
    }
}
