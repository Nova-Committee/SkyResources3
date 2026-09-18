package committee.nova.mods.skyresources3.common.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import committee.nova.mods.skyresources3.init.registry.ModRecipeTypes;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ExtractingRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ProcessIngredient input;
    private final FluidStack outputFluid;
    private final ItemStack outputBlock;

    public ExtractingRecipe(ResourceLocation id, ProcessIngredient input, FluidStack outputFluid, ItemStack outputBlock) {
        this.id = id;
        this.input = input;
        this.outputFluid = outputFluid;
        this.outputBlock = outputBlock;
    }

    public ProcessIngredient getInput() {
        return input;
    }

    public FluidStack getOutputFluid() {
        return outputFluid;
    }

    public ItemStack getOutputBlock() {
        return outputBlock;
    }

    @Override
    public boolean matches(final SimpleContainer simpleContainer, final Level level) {
        return this.input.matches(simpleContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer simpleContainer, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.EXTRACTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.EXTRACTING_TYPE.get();
    }

    public static final class Serializer implements RecipeSerializer<ExtractingRecipe>{

        @Override
        public ExtractingRecipe fromJson(ResourceLocation id, JsonObject json) {

            ProcessIngredient input = ProcessIngredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            FluidStack outputFluid = FluidStack.CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonObject(json, "output_fluid"))
                    .getOrThrow(false, message->{
                        throw new IllegalArgumentException("Invalid Extracting recipe output fluid" + message);
                    });
            ItemStack outputBlock = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json,"output_block"),true);

            return new ExtractingRecipe(id, input, outputFluid, outputBlock);
        }

        @Override
        public ExtractingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            ProcessIngredient input = ProcessIngredient.fromNetwork(buffer);
            FluidStack outputFluid = FluidStack.readFromPacket(buffer);
            ItemStack outputBlock = buffer.readItem();

            return new ExtractingRecipe(id, input, outputFluid, outputBlock);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ExtractingRecipe recipe) {
            recipe.input.toNetwork(buffer);
            recipe.outputFluid.writeToPacket(buffer);
            buffer.writeItem(recipe.outputBlock);
        }
    }

    public static Optional<ExtractingRecipe> find(final Level level, final ItemStack input){
        if (input.isEmpty()) {
            return Optional.empty();
        }
        return level.getRecipeManager().getRecipeFor(
                ModRecipeTypes.EXTRACTING_TYPE.get(),
                new SimpleContainer(input.copy()),
                level
        );
    }
}
