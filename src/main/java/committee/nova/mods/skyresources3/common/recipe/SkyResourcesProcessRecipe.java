package committee.nova.mods.skyresources3.common.recipe;

import committee.nova.mods.skyresources3.init.registry.ModRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public final class SkyResourcesProcessRecipe implements Recipe<ProcessRecipeInput> {
    private static final Codec<List<ProcessIngredient>> INPUTS_CODEC =
            ExtraCodecs.nonEmptyList(ProcessIngredient.CODEC.listOf());
    private static final Codec<List<ItemStack>> OUTPUTS_CODEC =
            ExtraCodecs.nonEmptyList(ItemStack.STRICT_CODEC.listOf());

    private final String group;
    private final String process;
    private final List<ProcessIngredient> inputs;
    private final List<ItemStack> outputs;
    private final float parameter;

    public SkyResourcesProcessRecipe(
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
    public ItemStack assemble(final ProcessRecipeInput input, final HolderLookup.Provider registries) {
        return this.outputs.isEmpty() ? ItemStack.EMPTY : this.outputs.getFirst().copy();
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
    public ItemStack getResultItem(final HolderLookup.Provider registries) {
        return this.outputs.isEmpty() ? ItemStack.EMPTY : this.outputs.getFirst().copy();
    }

    @Override
    public boolean canCraftInDimensions(final int width, final int height) {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<ProcessRecipeInput>> getSerializer() {
        return ModRecipeTypes.PROCESS_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<ProcessRecipeInput>> getType() {
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
        private static final MapCodec<SkyResourcesProcessRecipe> CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(SkyResourcesProcessRecipe::group),
                        ExtraCodecs.NON_EMPTY_STRING.fieldOf("process").forGetter(SkyResourcesProcessRecipe::process),
                        INPUTS_CODEC.fieldOf("inputs").forGetter(SkyResourcesProcessRecipe::inputs),
                        OUTPUTS_CODEC.fieldOf("outputs").forGetter(SkyResourcesProcessRecipe::outputs),
                        Codec.FLOAT.optionalFieldOf("parameter", 0.0F).forGetter(SkyResourcesProcessRecipe::parameter)
                ).apply(instance, SkyResourcesProcessRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, List<ProcessIngredient>> INPUTS_STREAM_CODEC =
                ProcessIngredient.STREAM_CODEC.apply(ByteBufCodecs.list());
        private static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> OUTPUTS_STREAM_CODEC =
                ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list());
        private static final StreamCodec<RegistryFriendlyByteBuf, SkyResourcesProcessRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8,
                        SkyResourcesProcessRecipe::group,
                        ByteBufCodecs.STRING_UTF8,
                        SkyResourcesProcessRecipe::process,
                        INPUTS_STREAM_CODEC,
                        SkyResourcesProcessRecipe::inputs,
                        OUTPUTS_STREAM_CODEC,
                        SkyResourcesProcessRecipe::outputs,
                        ByteBufCodecs.FLOAT,
                        SkyResourcesProcessRecipe::parameter,
                        SkyResourcesProcessRecipe::new
                );

        @Override
        public MapCodec<SkyResourcesProcessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SkyResourcesProcessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
