package committee.nova.mods.skyresources3.integration.jei;

import committee.nova.mods.skyresources3.item.CondenserItem;
import committee.nova.mods.skyresources3.recipe.CondenserRecipe;
import committee.nova.mods.skyresources3.registry.ModDataPackRegistries;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

final class CondenserRecipeJeiCategory implements IRecipeCategory<CondenserRecipe> {
    private static final int WIDTH = 150;
    private static final int HEIGHT = 64;
    private static final int TEXT_COLOR = 0xFF404040;
    private static final int SOURCE_FLUID_AMOUNT = 1000;

    private final IDrawable icon;
    private final IDrawable arrow;

    CondenserRecipeJeiCategory(final IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(
                CondenserItem.forType(ModDataPackRegistries.WOODEN_CONDENSER)
        );
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public IRecipeType<CondenserRecipe> getRecipeType() {
        return SkyResourcesJeiRecipeTypes.CONDENSER;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.skyresources3.category.condenser");
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
    public void setRecipe(final IRecipeLayoutBuilder builder, final CondenserRecipe recipe, final IFocusGroup focuses) {
        builder.addInputSlot(8, 22)
                .setStandardSlotBackground()
                .addItemStacks(JeiIngredientStacks.stacks(recipe.catalyst()));
        final IRecipeSlotBuilder sourceSlot = builder.addInputSlot(38, 22)
                .setStandardSlotBackground();
        this.addSource(sourceSlot, recipe.source());
        builder.addOutputSlot(116, 22)
                .setOutputSlotBackground()
                .add(recipe.output());
    }

    @Override
    public void draw(
            final CondenserRecipe recipe,
            final IRecipeSlotsView recipeSlotsView,
            final GuiGraphics guiGraphics,
            final double mouseX,
            final double mouseY
    ) {
        this.arrow.draw(guiGraphics, 76, 24);
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                Component.translatable("jei.skyresources3.condenser_time", JeiIngredientStacks.number(recipe.parameter())),
                4,
                52,
                TEXT_COLOR,
                false
        );
    }

    private void addSource(final IRecipeSlotBuilder slot, final CondenserRecipe.Source source) {
        switch (source.type()) {
            case FLUID -> BuiltInRegistries.FLUID.getOptional(source.id())
                    .ifPresent(fluid -> this.addFluidSource(slot, fluid));
            case BLOCK -> BuiltInRegistries.BLOCK.getOptional(source.id())
                    .map(this::sourceStack)
                    .filter(stack -> !stack.isEmpty())
                    .ifPresent(slot::add);
        }
    }

    private void addFluidSource(final IRecipeSlotBuilder slot, final Fluid fluid) {
        slot.setFluidRenderer(SOURCE_FLUID_AMOUNT, false, 16, 16)
                .add(fluid, SOURCE_FLUID_AMOUNT);
    }

    private ItemStack sourceStack(final Block block) {
        return new ItemStack(block);
    }
}
