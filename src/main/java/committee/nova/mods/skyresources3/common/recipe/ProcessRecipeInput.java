package committee.nova.mods.skyresources3.common.recipe;

import java.util.List;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public final class ProcessRecipeInput extends SimpleContainer {
    private final String process;
    private final List<ItemStack> items;
    private final float parameter;

    public ProcessRecipeInput(final String process, final List<ItemStack> items, final float parameter) {
        super(items.stream()
                .filter(stack -> !stack.isEmpty())
                .map(ItemStack::copy)
                .toArray(ItemStack[]::new));
        this.process = process;
        this.items = items.stream()
                .filter(stack -> !stack.isEmpty())
                .map(ItemStack::copy)
                .toList();
        this.parameter = parameter;
    }

    public String process() {
        return this.process;
    }

    public List<ItemStack> items() {
        return this.items;
    }

    public float parameter() {
        return this.parameter;
    }

    public static ProcessRecipeInput single(final String process, final ItemStack item, final float parameter) {
        return new ProcessRecipeInput(process, List.of(item), parameter);
    }
}
