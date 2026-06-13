package committee.nova.mods.skyresources3.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record ProcessIngredient(Ingredient ingredient, int count) {
    public static final Codec<ProcessIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(ProcessIngredient::ingredient),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(ProcessIngredient::count)
    ).apply(instance, ProcessIngredient::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ProcessIngredient> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            ProcessIngredient::ingredient,
            ByteBufCodecs.VAR_INT,
            ProcessIngredient::count,
            ProcessIngredient::new
    );

    public ProcessIngredient {
        if (count <= 0) {
            throw new IllegalArgumentException("Process ingredient count must be positive");
        }
    }

    public boolean matches(final ItemStack stack) {
        return stack.getCount() >= this.count && this.ingredient.test(stack);
    }
}
