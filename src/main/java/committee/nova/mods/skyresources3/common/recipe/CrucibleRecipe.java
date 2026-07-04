package committee.nova.mods.skyresources3.common.recipe;

import committee.nova.mods.skyresources3.init.registry.ModRecipeTypes;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fluids.FluidStack;

public final class CrucibleRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final String group;
    private final ProcessIngredient input;
    private final FluidStack output;

    public CrucibleRecipe(
            final ResourceLocation id,
            final String group,
            final ProcessIngredient input,
            final FluidStack output
    ) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("Crucible recipe output must not be empty");
        }
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output.copy();
    }

    @Override
    public boolean matches(final SimpleContainer recipeInput, final Level level) {
        return this.input.matches(recipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(final SimpleContainer input, final RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public String group() {
        return this.group;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResultItem(final RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(final int width, final int height) {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CRUCIBLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CRUCIBLE_TYPE.get();
    }

    public ProcessIngredient input() {
        return this.input;
    }

    public FluidStack output() {
        return this.output.copy();
    }

    public static final class Serializer implements RecipeSerializer<CrucibleRecipe> {
        @Override
        public CrucibleRecipe fromJson(final ResourceLocation id, final JsonObject json) {
            return this.fromJson(id, json, ICondition.IContext.EMPTY);
        }

        @Override
        public CrucibleRecipe fromJson(
                final ResourceLocation id,
                final JsonObject json,
                final ICondition.IContext context
        ) {
            if (!RecipeJsonUtil.areForgeConditionsMet(json, context)) {
                return null;
            }
            return new CrucibleRecipe(
                    id,
                    GsonHelper.getAsString(json, "group", ""),
                    ProcessIngredient.fromJson(GsonHelper.getAsJsonObject(json, "input")),
                    FluidStack.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, GsonHelper.getAsJsonObject(json, "output"))
                            .getOrThrow(false, message -> {
                                throw new IllegalArgumentException("Invalid crucible output: " + message);
                            })
            );
        }

        @Override
        public CrucibleRecipe fromNetwork(final ResourceLocation id, final FriendlyByteBuf buffer) {
            return new CrucibleRecipe(
                    id,
                    buffer.readUtf(),
                    ProcessIngredient.fromNetwork(buffer),
                    FluidStack.readFromPacket(buffer)
            );
        }

        @Override
        public void toNetwork(final FriendlyByteBuf buffer, final CrucibleRecipe recipe) {
            buffer.writeUtf(recipe.group());
            recipe.input().toNetwork(buffer);
            recipe.output().writeToPacket(buffer);
        }
    }
}
