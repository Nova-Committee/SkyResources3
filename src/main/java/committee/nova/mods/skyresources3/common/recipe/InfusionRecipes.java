package committee.nova.mods.skyresources3.common.recipe;

import java.util.List;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

public final class InfusionRecipes {
    public static Optional<Match> find(
            final ServerLevel level,
            final BlockState targetState,
            final ItemStack ingredient
    ) {
        if (ingredient.isEmpty()) {
            return Optional.empty();
        }

        final ItemStack target = stackForState(targetState);
        if (target.isEmpty()) {
            return Optional.empty();
        }

        return ProcessRecipes.find(level, ProcessRecipes.INFUSION, List.of(ingredient, target))
                .flatMap(InfusionRecipes::resolve);
    }

    private static Optional<Match> resolve(final SkyResourcesProcessRecipe recipe) {
        final List<ItemStack> outputs = recipe.outputs();
        if (outputs.isEmpty() || outputs.get(0).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new Match(
                recipe.inputs().get(0).count(),
                Math.max(0, (int) recipe.parameter()),
                outputs.get(0)
        ));
    }

    private static ItemStack stackForState(final BlockState state) {
        final Item item = state.getBlock().asItem();
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }

    public record Match(int ingredientCount, int healthCost, ItemStack output) {
        public Match {
            if (ingredientCount <= 0) {
                throw new IllegalArgumentException("Infusion ingredient count must be positive");
            }
            if (healthCost < 0) {
                throw new IllegalArgumentException("Infusion health cost must not be negative");
            }
            output = output.copy();
        }

        public ItemStack createOutput() {
            return this.output.copy();
        }
    }

    private InfusionRecipes() {
    }
}
