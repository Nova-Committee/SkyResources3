package committee.nova.mods.skyresources3.common.recipe;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public final class CondenserRecipeInput extends SimpleContainer {
    private final ItemStack catalyst;
    private final CondenserRecipe.Source source;

    public CondenserRecipeInput(final ItemStack catalyst, final CondenserRecipe.Source source) {
        super(catalyst.copy());
        this.catalyst = catalyst.copy();
        this.source = source;
    }

    public ItemStack catalyst() {
        return this.catalyst;
    }

    public CondenserRecipe.Source source() {
        return this.source;
    }
}
