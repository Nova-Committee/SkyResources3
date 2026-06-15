package committee.nova.mods.skyresources3.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record CondenserRecipeInput(ItemStack catalyst, CondenserRecipe.Source source) implements RecipeInput {
    public CondenserRecipeInput {
        catalyst = catalyst.copy();
    }

    @Override
    public ItemStack getItem(final int index) {
        if (index != 0) {
            throw new IllegalArgumentException("No item for index " + index);
        }
        return this.catalyst;
    }

    @Override
    public int size() {
        return 1;
    }
}
