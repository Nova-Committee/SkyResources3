package committee.nova.mods.skyresources3.recipe;

import committee.nova.mods.skyresources3.registry.ModRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public final class CondenserRecipe implements Recipe<CondenserRecipeInput> {
    private final String group;
    private final Ingredient catalyst;
    private final Source source;
    private final ItemStack output;
    private final float parameter;
    private @Nullable PlacementInfo placementInfo;

    public CondenserRecipe(
            final String group,
            final Ingredient catalyst,
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
        this.group = group;
        this.catalyst = catalyst;
        this.source = source;
        this.output = output.copy();
        this.parameter = parameter;
    }

    @Override
    public boolean matches(final CondenserRecipeInput input, final Level level) {
        return this.source.equals(input.source()) && this.catalyst.test(input.catalyst());
    }

    @Override
    public ItemStack assemble(final CondenserRecipeInput input, final HolderLookup.Provider registries) {
        return this.output.copy();
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
    public RecipeSerializer<? extends Recipe<CondenserRecipeInput>> getSerializer() {
        return ModRecipeTypes.CONDENSER_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<CondenserRecipeInput>> getType() {
        return ModRecipeTypes.CONDENSER_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.catalyst);
        }
        return this.placementInfo;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public Ingredient catalyst() {
        return this.catalyst;
    }

    public boolean isCatalyst(final ItemStack stack) {
        return this.catalyst.test(stack);
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
        return Objects.hash(this.source, this.output.getItem(), this.output.getCount(), this.parameter);
    }

    public record Source(SourceType type, Identifier id) {
        private static final Codec<Identifier> IDENTIFIER_CODEC =
                Codec.STRING.xmap(Identifier::parse, Identifier::toString);
        private static final Codec<Source> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                SourceType.CODEC.fieldOf("type").forGetter(Source::type),
                IDENTIFIER_CODEC.fieldOf("id").forGetter(Source::id)
        ).apply(instance, Source::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, Source> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                source -> source.type().id(),
                ByteBufCodecs.STRING_UTF8,
                source -> source.id().toString(),
                (type, id) -> new Source(SourceType.byId(type), Identifier.parse(id))
        );

        public static Source fluid(final Identifier id) {
            return new Source(SourceType.FLUID, id);
        }

        public static Source block(final Identifier id) {
            return new Source(SourceType.BLOCK, id);
        }
    }

    public enum SourceType {
        FLUID("fluid"),
        BLOCK("block");

        private static final Codec<SourceType> CODEC = Codec.STRING.xmap(SourceType::byId, SourceType::id);

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
        private static final MapCodec<CondenserRecipe> CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(CondenserRecipe::group),
                        Ingredient.CODEC.fieldOf("catalyst").forGetter(CondenserRecipe::catalyst),
                        Source.CODEC.fieldOf("source").forGetter(CondenserRecipe::source),
                        ItemStack.STRICT_CODEC.fieldOf("output").forGetter(CondenserRecipe::output),
                        Codec.FLOAT.fieldOf("parameter").forGetter(CondenserRecipe::parameter)
                ).apply(instance, CondenserRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, CondenserRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8,
                        CondenserRecipe::group,
                        Ingredient.CONTENTS_STREAM_CODEC,
                        CondenserRecipe::catalyst,
                        Source.STREAM_CODEC,
                        CondenserRecipe::source,
                        ItemStack.STREAM_CODEC,
                        CondenserRecipe::output,
                        ByteBufCodecs.FLOAT,
                        CondenserRecipe::parameter,
                        CondenserRecipe::new
                );

        @Override
        public MapCodec<CondenserRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CondenserRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
