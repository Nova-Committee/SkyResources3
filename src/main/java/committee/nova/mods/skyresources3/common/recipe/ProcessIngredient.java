package committee.nova.mods.skyresources3.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record ProcessIngredient(Ingredient ingredient, Optional<ItemStack> exactStack, int count) {
    public static final Codec<ProcessIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.optionalFieldOf("ingredient").forGetter(ProcessIngredient::codecIngredient),
            ItemStack.STRICT_CODEC.optionalFieldOf("stack").forGetter(ProcessIngredient::exactStack),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(ProcessIngredient::count)
    ).apply(instance, ProcessIngredient::create));
    public static final StreamCodec<RegistryFriendlyByteBuf, ProcessIngredient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC),
            ProcessIngredient::codecIngredient,
            ByteBufCodecs.optional(ItemStack.STREAM_CODEC),
            ProcessIngredient::exactStack,
            ByteBufCodecs.VAR_INT,
            ProcessIngredient::count,
            ProcessIngredient::create
    );

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
        final ItemStack normalized = stack.copyWithCount(1);
        return new ProcessIngredient(Ingredient.of(normalized.getItem()), Optional.of(normalized), count);
    }

    public boolean matches(final ItemStack stack) {
        if (stack.getCount() < this.count) {
            return false;
        }
        return this.exactStack
                .map(expected -> ItemStack.isSameItemSameComponents(stack, expected))
                .orElseGet(() -> this.ingredient.test(stack));
    }

    public List<ItemStack> displayStacks() {
        return this.exactStack
                .map(expected -> List.of(expected.copyWithCount(this.count)))
                .orElseGet(() -> Arrays.stream(this.ingredient.getItems())
                        .map(stack -> stack.copyWithCount(this.count))
                        .filter(stack -> !stack.isEmpty())
                        .toList());
    }

    private Optional<Ingredient> codecIngredient() {
        return this.exactStack.isPresent() ? Optional.empty() : Optional.of(this.ingredient);
    }

    private static ProcessIngredient create(
            final Optional<Ingredient> ingredient,
            final Optional<ItemStack> exactStack,
            final int count
    ) {
        if (exactStack.isPresent()) {
            return stack(exactStack.get(), count);
        }
        return new ProcessIngredient(
                ingredient.orElseThrow(() -> new IllegalArgumentException("Process ingredient requires ingredient or stack")),
                count
        );
    }
}
