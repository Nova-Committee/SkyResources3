package committee.nova.mods.skyresources3.common.recipe;

import committee.nova.mods.skyresources3.init.registry.ModRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

public final class CrucibleRecipe implements Recipe<SingleRecipeInput> {
    private final String group;
    private final ProcessIngredient input;
    private final FluidStack output;
    private @Nullable PlacementInfo placementInfo;

    public CrucibleRecipe(final String group, final ProcessIngredient input, final FluidStack output) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("Crucible recipe output must not be empty");
        }
        this.group = group;
        this.input = input;
        this.output = output.copy();
    }

    @Override
    public boolean matches(final SingleRecipeInput recipeInput, final Level level) {
        return this.input.matches(recipeInput.item());
    }

    @Override
    public ItemStack assemble(final SingleRecipeInput input, final HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return ModRecipeTypes.CRUCIBLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return ModRecipeTypes.CRUCIBLE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.input.ingredient());
        }
        return this.placementInfo;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public ProcessIngredient input() {
        return this.input;
    }

    public FluidStack output() {
        return this.output.copy();
    }

    public static final class Serializer implements RecipeSerializer<CrucibleRecipe> {
        private static final MapCodec<CrucibleRecipe> CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(CrucibleRecipe::group),
                        ProcessIngredient.CODEC.fieldOf("input").forGetter(CrucibleRecipe::input),
                        FluidStack.CODEC.fieldOf("output").forGetter(CrucibleRecipe::output)
                ).apply(instance, CrucibleRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8,
                        CrucibleRecipe::group,
                        ProcessIngredient.STREAM_CODEC,
                        CrucibleRecipe::input,
                        FluidStack.STREAM_CODEC,
                        CrucibleRecipe::output,
                        CrucibleRecipe::new
                );

        @Override
        public MapCodec<CrucibleRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
