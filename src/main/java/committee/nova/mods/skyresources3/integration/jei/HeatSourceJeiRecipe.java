package committee.nova.mods.skyresources3.integration.jei;

import committee.nova.mods.skyresources3.machine.MachineVariant;
import committee.nova.mods.skyresources3.registry.ModItems;
import committee.nova.mods.skyresources3.util.HeatSources;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

record HeatSourceJeiRecipe(ItemStack source, int heat) {
    HeatSourceJeiRecipe {
        source = source.copy();
        if (heat < 0) {
            throw new IllegalArgumentException("Heat source value must not be negative");
        }
    }

    @Override
    public ItemStack source() {
        return this.source.copy();
    }

    static List<HeatSourceJeiRecipe> recipes() {
        final List<HeatSourceJeiRecipe> recipes = new ArrayList<>();
        add(recipes, new ItemStack(Items.LAVA_BUCKET), HeatSources.sourceLavaValue());
        for (final HeatSources.BlockHeatSource source : HeatSources.blockHeatSources()) {
            add(recipes, new ItemStack(source.block()), source.heat());
        }
        for (final MachineVariant variant : MachineVariant.values()) {
            add(
                    recipes,
                    new ItemStack(ModItems.HEAT_PROVIDERS.get(variant).get()),
                    Math.round(variant.heatPerTick())
            );
        }
        return List.copyOf(recipes);
    }

    private static void add(final List<HeatSourceJeiRecipe> recipes, final ItemStack stack, final int heat) {
        if (!stack.isEmpty()) {
            recipes.add(new HeatSourceJeiRecipe(stack, heat));
        }
    }
}
