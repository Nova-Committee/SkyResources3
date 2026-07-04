package committee.nova.mods.skyresources3.common.recipe;

import committee.nova.mods.skyresources3.init.registry.ModRecipeTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public final class SkyResourcesProcessRecipe implements Recipe<ProcessRecipeInput> {
    private final ResourceLocation id;
    private final String group;
    private final String process;
    private final List<ProcessIngredient> inputs;
    private final List<ItemStack> outputs;
    private final float parameter;

    public SkyResourcesProcessRecipe(
            final ResourceLocation id,
            final String group,
            final String process,
            final List<ProcessIngredient> inputs,
            final List<ItemStack> outputs,
            final float parameter
    ) {
        if (inputs.isEmpty()) {
            throw new IllegalArgumentException("Process recipe inputs must not be empty");
        }
        if (outputs.isEmpty()) {
            throw new IllegalArgumentException("Process recipe outputs must not be empty");
        }
        this.id = id;
        this.group = group;
        this.process = process;
        this.inputs = List.copyOf(inputs);
        this.outputs = outputs.stream().map(ItemStack::copy).toList();
        this.parameter = parameter;
    }

    @Override
    public boolean matches(final ProcessRecipeInput input, final Level level) {
        return this.process.equals(input.process())
                && input.parameter() >= this.parameter
                && this.inputsMatch(input.items());
    }

    @Override
    public ItemStack assemble(final ProcessRecipeInput input, final RegistryAccess registryAccess) {
        return this.outputs.isEmpty() ? ItemStack.EMPTY : this.outputs.get(0).copy();
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
        return this.outputs.isEmpty() ? ItemStack.EMPTY : this.outputs.get(0).copy();
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
        return ModRecipeTypes.PROCESS_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.PROCESS_TYPE.get();
    }

    public String process() {
        return this.process;
    }

    public List<ProcessIngredient> inputs() {
        return this.inputs;
    }

    public List<ItemStack> outputs() {
        return this.outputs.stream().map(ItemStack::copy).toList();
    }

    public float parameter() {
        return this.parameter;
    }

    private boolean inputsMatch(final List<ItemStack> stacks) {
        if (stacks.size() != this.inputs.size()) {
            return false;
        }

        final boolean[] used = new boolean[stacks.size()];
        for (final ProcessIngredient ingredient : this.inputs) {
            final int matchIndex = firstMatchingStack(ingredient, stacks, used);
            if (matchIndex < 0) {
                return false;
            }
            used[matchIndex] = true;
        }
        return true;
    }

    private static int firstMatchingStack(
            final ProcessIngredient ingredient,
            final List<ItemStack> stacks,
            final boolean[] used
    ) {
        for (int i = 0; i < stacks.size(); i++) {
            if (!used[i] && ingredient.matches(stacks.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static final class Serializer implements RecipeSerializer<SkyResourcesProcessRecipe> {
        @Override
        public SkyResourcesProcessRecipe fromJson(final ResourceLocation id, final JsonObject json) {
            final JsonArray inputJson = GsonHelper.getAsJsonArray(json, "inputs");
            final List<ProcessIngredient> inputs = inputJson.asList().stream()
                    .map(ProcessIngredient::fromJson)
                    .toList();
            final JsonArray outputJson = GsonHelper.getAsJsonArray(json, "outputs");
            final List<ItemStack> outputs = outputJson.asList().stream()
                    .map(element -> RecipeJsonUtil.stackFromJson(GsonHelper.convertToJsonObject(element, "output")))
                    .toList();
            return new SkyResourcesProcessRecipe(
                    id,
                    GsonHelper.getAsString(json, "group", ""),
                    GsonHelper.getAsString(json, "process"),
                    inputs,
                    outputs,
                    GsonHelper.getAsFloat(json, "parameter", 0.0F)
            );
        }

        @Override
        public SkyResourcesProcessRecipe fromNetwork(final ResourceLocation id, final FriendlyByteBuf buffer) {
            final String group = buffer.readUtf();
            final String process = buffer.readUtf();
            final List<ProcessIngredient> inputs = buffer.readList(ProcessIngredient::fromNetwork);
            final List<ItemStack> outputs = buffer.readList(FriendlyByteBuf::readItem);
            final float parameter = buffer.readFloat();
            return new SkyResourcesProcessRecipe(id, group, process, inputs, outputs, parameter);
        }

        @Override
        public void toNetwork(final FriendlyByteBuf buffer, final SkyResourcesProcessRecipe recipe) {
            buffer.writeUtf(recipe.group());
            buffer.writeUtf(recipe.process());
            buffer.writeCollection(recipe.inputs(), (target, input) -> input.toNetwork(target));
            buffer.writeCollection(recipe.outputs(), FriendlyByteBuf::writeItem);
            buffer.writeFloat(recipe.parameter());
        }
    }
}
