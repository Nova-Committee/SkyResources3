package committee.nova.mods.skyresources3.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record ProcessIngredient(Ingredient ingredient, Optional<ItemStack> exactStack, int count) {
    public ProcessIngredient {
        if (count <= 0) {
            throw new IllegalArgumentException("Process ingredient count must be positive");
        }
        if (exactStack.isPresent() && exactStack.get().isEmpty()) {
            throw new IllegalArgumentException("Exact process ingredient stack must not be empty");
        }
    }

    public ProcessIngredient(final Ingredient ingredient, final int count) {
        this(ingredient, Optional.empty(), count);
    }

    public static ProcessIngredient stack(final ItemStack stack) {
        return stack(stack, stack.getCount());
    }

    public static ProcessIngredient stack(final ItemStack stack, final int count) {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("Exact process ingredient stack must not be empty");
        }
        final ItemStack normalized = stack.copy();
        normalized.setCount(1);
        return new ProcessIngredient(Ingredient.of(normalized.getItem()), Optional.of(normalized), count);
    }

    public boolean matches(final ItemStack stack) {
        if (stack.getCount() < this.count) {
            return false;
        }
        return this.exactStack
                .map(expected -> ItemStack.isSameItemSameTags(stack, expected))
                .orElseGet(() -> this.ingredient.test(stack));
    }

    public List<ItemStack> displayStacks() {
        return this.exactStack
                .map(expected -> List.of(copyWithCount(expected, this.count)))
                .orElseGet(() -> Arrays.stream(this.ingredient.getItems())
                        .map(stack -> copyWithCount(stack, this.count))
                        .filter(stack -> !stack.isEmpty())
                        .toList());
    }

    static ProcessIngredient fromJson(final JsonElement element) {
        final JsonObject json = GsonHelper.convertToJsonObject(element, "process ingredient");
        final int count = GsonHelper.getAsInt(json, "count", 1);
        if (json.has("stack")) {
            return stack(RecipeJsonUtil.stackFromJson(GsonHelper.getAsJsonObject(json, "stack")), count);
        }
        if (json.has("ingredient")) {
            return new ProcessIngredient(Ingredient.fromJson(json.get("ingredient")), count);
        }
        return new ProcessIngredient(Ingredient.fromJson(element), count);
    }

    static ProcessIngredient fromNetwork(final FriendlyByteBuf buffer) {
        final boolean exact = buffer.readBoolean();
        final int count = buffer.readVarInt();
        if (exact) {
            return stack(buffer.readItem(), count);
        }
        return new ProcessIngredient(Ingredient.fromNetwork(buffer), count);
    }

    void toNetwork(final FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.exactStack.isPresent());
        buffer.writeVarInt(this.count);
        if (this.exactStack.isPresent()) {
            buffer.writeItem(this.exactStack.get());
        } else {
            this.ingredient.toNetwork(buffer);
        }
    }

    private static ItemStack copyWithCount(final ItemStack stack, final int count) {
        final ItemStack copy = stack.copy();
        copy.setCount(count);
        return copy;
    }
}
