package committee.nova.mods.skyresources3.integration.jei;

import committee.nova.mods.skyresources3.recipe.ProcessIngredient;
import java.util.List;
import net.minecraft.world.item.ItemStack;

final class JeiIngredientStacks {
    @SuppressWarnings("deprecation")
    static List<ItemStack> stacks(final ProcessIngredient ingredient) {
        return ingredient.ingredient().items()
                .map(holder -> new ItemStack(holder.value(), ingredient.count()))
                .filter(stack -> !stack.isEmpty())
                .toList();
    }

    static String number(final float value) {
        final int rounded = Math.round(value);
        if (Math.abs(value - rounded) < 0.001F) {
            return Integer.toString(rounded);
        }
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }

    private JeiIngredientStacks() {
    }
}
