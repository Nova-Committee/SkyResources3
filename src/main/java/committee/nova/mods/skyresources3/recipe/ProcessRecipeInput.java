package committee.nova.mods.skyresources3.recipe;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ProcessRecipeInput(String process, List<ItemStack> items, float parameter) implements RecipeInput {
    public ProcessRecipeInput {
        items = items.stream()
                .filter(stack -> !stack.isEmpty())
                .map(ItemStack::copy)
                .toList();
    }

    public static ProcessRecipeInput single(final String process, final ItemStack item, final float parameter) {
        return new ProcessRecipeInput(process, List.of(item), parameter);
    }

    @Override
    public ItemStack getItem(final int index) {
        if (index < 0 || index >= this.items.size()) {
            throw new IllegalArgumentException("No item for index " + index);
        }
        return this.items.get(index);
    }

    @Override
    public int size() {
        return this.items.size();
    }
}
