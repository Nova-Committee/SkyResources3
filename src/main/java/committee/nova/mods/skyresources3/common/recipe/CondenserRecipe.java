package committee.nova.mods.skyresources3.common.recipe;

import committee.nova.mods.skyresources3.init.registry.ModRecipeTypes;
import com.google.gson.JsonObject;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public final class CondenserRecipe implements Recipe<CondenserRecipeInput> {
    private final ResourceLocation id;
    private final String group;
    private final ProcessIngredient catalyst;
    private final Source source;
    private final ItemStack output;
    private final float parameter;

    public CondenserRecipe(
            final ResourceLocation id,
            final String group,
            final ProcessIngredient catalyst,
            final Source source,
            final ItemStack output,
            final float parameter
    ) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("Condenser recipe output must not be empty");
        }
        if (parameter <= 0.0F) {
            throw new IllegalArgumentException("Condenser recipe parameter must be positive");
        }
        this.id = id;
        this.group = group;
        this.catalyst = catalyst;
        this.source = source;
        this.output = output.copy();
        this.parameter = parameter;
    }

    @Override
    public boolean matches(final CondenserRecipeInput input, final Level level) {
        return this.source.equals(input.source()) && this.catalyst.matches(input.catalyst());
    }

    @Override
    public ItemStack assemble(final CondenserRecipeInput input, final RegistryAccess registryAccess) {
        return this.output.copy();
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
        return this.output.copy();
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
        return ModRecipeTypes.CONDENSER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CONDENSER_TYPE.get();
    }

    public ProcessIngredient catalyst() {
        return this.catalyst;
    }

    public boolean isCatalyst(final ItemStack stack) {
        return this.catalyst.matches(stack);
    }

    public Source source() {
        return this.source;
    }

    public ItemStack output() {
        return this.output.copy();
    }

    public float parameter() {
        return this.parameter;
    }

    public int runtimeKeyHash() {
        return Objects.hash(
                this.source,
                this.catalyst.displayStacks().stream()
                        .map(stack -> Objects.hash(stack.getItem(), stack.getTag()))
                        .toList(),
                this.output.getItem(),
                this.output.getCount(),
                this.parameter
        );
    }

    public record Source(SourceType type, ResourceLocation id) {
        public static Source fluid(final ResourceLocation id) {
            return new Source(SourceType.FLUID, id);
        }

        public static Source block(final ResourceLocation id) {
            return new Source(SourceType.BLOCK, id);
        }

        private static Source fromJson(final JsonObject json) {
            return new Source(
                    SourceType.byId(GsonHelper.getAsString(json, "type")),
                    new ResourceLocation(GsonHelper.getAsString(json, "id"))
            );
        }

        private static Source fromNetwork(final FriendlyByteBuf buffer) {
            return new Source(buffer.readEnum(SourceType.class), buffer.readResourceLocation());
        }

        private void toNetwork(final FriendlyByteBuf buffer) {
            buffer.writeEnum(this.type);
            buffer.writeResourceLocation(this.id);
        }
    }

    public enum SourceType {
        FLUID("fluid"),
        BLOCK("block");

        private final String id;

        SourceType(final String id) {
            this.id = id;
        }

        public String id() {
            return this.id;
        }

        private static SourceType byId(final String id) {
            final String normalized = id.toLowerCase(Locale.ROOT);
            for (final SourceType type : values()) {
                if (type.id.equals(normalized)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown condenser source type " + id);
        }
    }

    public static final class Serializer implements RecipeSerializer<CondenserRecipe> {
        @Override
        public CondenserRecipe fromJson(final ResourceLocation id, final JsonObject json) {
            return new CondenserRecipe(
                    id,
                    GsonHelper.getAsString(json, "group", ""),
                    ProcessIngredient.fromJson(GsonHelper.getAsJsonObject(json, "catalyst")),
                    Source.fromJson(GsonHelper.getAsJsonObject(json, "source")),
                    RecipeJsonUtil.stackFromJson(GsonHelper.getAsJsonObject(json, "output")),
                    GsonHelper.getAsFloat(json, "parameter")
            );
        }

        @Override
        public CondenserRecipe fromNetwork(final ResourceLocation id, final FriendlyByteBuf buffer) {
            return new CondenserRecipe(
                    id,
                    buffer.readUtf(),
                    ProcessIngredient.fromNetwork(buffer),
                    Source.fromNetwork(buffer),
                    buffer.readItem(),
                    buffer.readFloat()
            );
        }

        @Override
        public void toNetwork(final FriendlyByteBuf buffer, final CondenserRecipe recipe) {
            buffer.writeUtf(recipe.group());
            recipe.catalyst().toNetwork(buffer);
            recipe.source().toNetwork(buffer);
            buffer.writeItem(recipe.output());
            buffer.writeFloat(recipe.parameter());
        }
    }
}
