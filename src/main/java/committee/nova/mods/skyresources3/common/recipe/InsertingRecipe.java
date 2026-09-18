package committee.nova.mods.skyresources3.common.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import committee.nova.mods.skyresources3.init.registry.ModRecipeTypes;
import net.minecraft.MethodsReturnNonnullByDefault;
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
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InsertingRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ProcessIngredient inputBlock;
    private final FluidStack inputFluid;
    private final ItemStack outputBlock;

    public InsertingRecipe(ResourceLocation id, ProcessIngredient inputBlock, FluidStack inputFluid, ItemStack outputBlock) {
        this.id = id;
        this.inputBlock = inputBlock;
        this.inputFluid = inputFluid;
        this.outputBlock = outputBlock;
    }

    public ProcessIngredient getInputBlock() {
        return inputBlock;
    }

    public FluidStack getInputFluid() {
        return inputFluid;
    }

    public ItemStack getOutputBlock() {
        return outputBlock;
    }

    @Override
    public boolean matches(SimpleContainer simpleContainer, Level level) {
        return this.inputBlock.matches(simpleContainer.getItem(0));
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
        return ModRecipeTypes.INSERTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.INSERTING_TYPE.get();
    }

    public static final class Serializer implements RecipeSerializer<InsertingRecipe>{

        @Override
        public InsertingRecipe fromJson(ResourceLocation id, JsonObject json) {
            ProcessIngredient inputBlock = ProcessIngredient.fromJson(GsonHelper.getAsJsonObject(json, "input_block"));
            FluidStack inputFluid = FluidStack.CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonObject(json, "input_fluid"))
                    .getOrThrow(false, message->{
                        throw new IllegalArgumentException("Invalid Extracting recipe input fluid" + message);
                    });
            ItemStack outputBlock = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json,"output_block"),true);

            return new InsertingRecipe(id,inputBlock,inputFluid,outputBlock);
        }

        @Override
        public InsertingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            ProcessIngredient inputBlock = ProcessIngredient.fromNetwork(buffer);
            FluidStack inputFluid = FluidStack.readFromPacket(buffer);
            ItemStack outputBlock = buffer.readItem();

            return new InsertingRecipe(id, inputBlock, inputFluid, outputBlock);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, InsertingRecipe recipe) {
            recipe.inputBlock.toNetwork(buffer);
            recipe.inputFluid.writeToPacket(buffer);
            buffer.writeItem(recipe.outputBlock);
        }
    }

    public static Optional<InsertingRecipe> find(final Level level, final ItemStack input){
        if (input.isEmpty()) {
            return Optional.empty();
        }
        return level.getRecipeManager().getRecipeFor(
                ModRecipeTypes.INSERTING_TYPE.get(),
                new SimpleContainer(input.copy()),
                level
        );
    }
}
