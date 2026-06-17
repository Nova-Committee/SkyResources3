package committee.nova.mods.skyresources3.init.integration.jei;

import committee.nova.mods.skyresources3.common.recipe.ProcessIngredient;
import committee.nova.mods.skyresources3.common.recipe.ProcessRecipes;
import committee.nova.mods.skyresources3.common.recipe.SkyResourcesProcessRecipe;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
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

final class ProcessRecipeJeiCategory implements IRecipeCategory<SkyResourcesProcessRecipe> {
    private static final int WIDTH = 184;
    private static final int HEIGHT = 84;
    private static final int TEXT_COLOR = 0xFF404040;

    private final IRecipeType<SkyResourcesProcessRecipe> recipeType;
    private final Component title;
    private final IDrawable icon;
    private final IDrawable arrow;
    private final String parameterKey;
    private final ParameterMode parameterMode;

    ProcessRecipeJeiCategory(
            final IGuiHelper guiHelper,
            final IRecipeType<SkyResourcesProcessRecipe> recipeType,
            final Component title,
            final ItemStack icon,
            final String parameterKey,
            final ParameterMode parameterMode
    ) {
        this.recipeType = recipeType;
        this.title = title;
        this.icon = guiHelper.createDrawableItemStack(icon);
        this.arrow = guiHelper.getRecipeArrow();
        this.parameterKey = parameterKey;
        this.parameterMode = parameterMode;
    }

    @Override
    public IRecipeType<SkyResourcesProcessRecipe> getRecipeType() {
        return this.recipeType;
    }

    @Override
    public Component getTitle() {
        return this.title;
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
    public void setRecipe(
            final IRecipeLayoutBuilder builder,
            final SkyResourcesProcessRecipe recipe,
            final IFocusGroup focuses
    ) {
        this.addInputs(builder, recipe.process(), recipe.inputs());
        if (ProcessRecipes.FUSION.equals(recipe.process())) {
            JeiSourceDescriptions.addFusionCatalystSlot(builder);
        }
        this.addOutputs(builder, recipe.process(), recipe.outputs());
    }

    @Override
    public void draw(
            final SkyResourcesProcessRecipe recipe,
            final IRecipeSlotsView recipeSlotsView,
            final GuiGraphics guiGraphics,
            final double mouseX,
            final double mouseY
    ) {
        this.arrow.draw(guiGraphics, 76, 22);
        if (this.parameterMode == ParameterMode.NONE || recipe.parameter() <= 0.0F) {
            return;
        }
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                this.parameter(recipe.parameter()),
                4,
                68,
                TEXT_COLOR,
                false
        );
    }

    private void addInputs(
            final IRecipeLayoutBuilder builder,
            final String process,
            final List<ProcessIngredient> inputs
    ) {
        for (int index = 0; index < inputs.size(); index++) {
            final int x = 4 + index % 3 * 20;
            final int y = 8 + index / 3 * 20;
            final IRecipeSlotBuilder slot = builder.addInputSlot(x, y)
                    .setStandardSlotBackground()
                    .addItemStacks(JeiIngredientStacks.stacks(inputs.get(index)));
            JeiSourceDescriptions.addProcessInputTooltip(slot, process, index);
        }
    }

    private void addOutputs(
            final IRecipeLayoutBuilder builder,
            final String process,
            final List<ItemStack> outputs
    ) {
        for (int index = 0; index < outputs.size(); index++) {
            final int x = 136 + index % 2 * 20;
            final int y = 14 + index / 2 * 20;
            final IRecipeSlotBuilder slot = builder.addOutputSlot(x, y)
                    .setOutputSlotBackground()
                    .add(outputs.get(index));
            JeiSourceDescriptions.addProcessOutputTooltip(slot, process);
        }
    }

    private Component parameter(final float value) {
        final Object parameter = switch (this.parameterMode) {
            case NONE -> "";
            case NUMBER -> JeiIngredientStacks.number(value);
            case INTEGER -> Math.round(value);
            case PERCENT -> Math.round(value * 100.0F);
            case FUSION_CATALYST_PERCENT -> Math.round(value * 10000.0F);
        };
        return Component.translatable(this.parameterKey, parameter);
    }

    enum ParameterMode {
        NONE,
        NUMBER,
        INTEGER,
        PERCENT,
        FUSION_CATALYST_PERCENT
    }
}
