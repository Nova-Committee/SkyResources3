package committee.nova.mods.skyresources3.init.integration.jei;

import committee.nova.mods.skyresources3.common.recipe.InsertingRecipe;
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

public class InsertingRecipeCategory extends AbstractRecipeCategory<InsertingRecipe> {
    private static final int WIDTH = 114;
    private static final int HEIGHT = 34;

    private final IDrawable fluidBackground;
    private final IDrawable fluidOverlay;
    private final IDrawable plusSign;

    public InsertingRecipeCategory(IGuiHelper helper) {
        super(
                SkyResourcesJeiRecipeTypes.INSERTING,
                Component.translatable("jei.skyresources.category.inserting"),
                helper.createDrawableItemLike(ModItems.WATER_EXTRACTOR.get()),
                WIDTH,
                HEIGHT
        );
        fluidBackground = helper.createDrawable(SkyResourcesJeiPlugin.ICONS, 0, 0, 18, 34);
        fluidOverlay = helper.createDrawable(SkyResourcesJeiPlugin.ICONS, 18, 0, 18, 34);
        plusSign = helper.createDrawable(SkyResourcesJeiPlugin.ICONS,0,34,9,9);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InsertingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(1,1)
                .setFluidRenderer(1200,false,16,32)
                .setBackground(fluidBackground,-1,-1)
                .setOverlay(fluidOverlay,-1,-1)
                .addIngredient(ForgeTypes.FLUID_STACK, recipe.getInputFluid());

        builder.addInputSlot(33,9)
                .setStandardSlotBackground()
                .addItemStacks(JeiIngredientStacks.stacks(recipe.getInputBlock()));

        builder.addOutputSlot(93,9)
                .setOutputSlotBackground()
                .addItemStack(recipe.getOutputBlock());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, InsertingRecipe recipe, IFocusGroup focuses) {
        builder.addDrawable(plusSign).setPosition(20,12);
        builder.addRecipeArrow().setPosition(58,9);
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(InsertingRecipe recipe) {
        return recipe.getId();
    }
}
