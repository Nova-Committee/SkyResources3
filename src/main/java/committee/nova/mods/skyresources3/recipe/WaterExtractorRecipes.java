package committee.nova.mods.skyresources3.recipe;

import committee.nova.mods.skyresources3.registry.ModBlocks;
import java.util.Optional;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class WaterExtractorRecipes {
    public static final int SNOW_WATER = 50;
    public static final int LEAVES_WATER = 20;
    public static final int DIRT_TO_CLAY_WATER = 200;
    public static final int CACTUS_TO_DRY_CACTUS_WATER = 50;
    public static final int DRY_CACTUS_TO_CACTUS_WATER = 1200;

    public static Optional<BlockInsertion> findBlockInsertion(final BlockState state) {
        if (state.is(Blocks.DIRT)) {
            return Optional.of(new BlockInsertion(Blocks.CLAY.defaultBlockState(), DIRT_TO_CLAY_WATER));
        }
        if (state.is(ModBlocks.DRY_CACTUS.get())) {
            return Optional.of(new BlockInsertion(Blocks.CACTUS.defaultBlockState(), DRY_CACTUS_TO_CACTUS_WATER));
        }
        return Optional.empty();
    }

    public static Optional<BlockExtraction> findBlockExtraction(final BlockState state) {
        if (state.is(Blocks.SNOW)) {
            return Optional.of(new BlockExtraction(Optional.empty(), SNOW_WATER));
        }
        if (state.is(Blocks.CACTUS)) {
            return Optional.of(new BlockExtraction(
                    Optional.of(ModBlocks.DRY_CACTUS.get().defaultBlockState()),
                    CACTUS_TO_DRY_CACTUS_WATER
            ));
        }
        if (state.is(BlockTags.LEAVES)) {
            return Optional.of(new BlockExtraction(Optional.empty(), LEAVES_WATER));
        }
        return Optional.empty();
    }

    public static Optional<ItemWaterRecipe> findConcentration(final ItemStack input) {
        if (input.is(Blocks.DIRT.asItem())) {
            return Optional.of(new ItemWaterRecipe(new ItemStack(Blocks.CLAY), DIRT_TO_CLAY_WATER));
        }
        if (input.is(ModBlocks.DRY_CACTUS.get().asItem())) {
            return Optional.of(new ItemWaterRecipe(new ItemStack(Blocks.CACTUS), DRY_CACTUS_TO_CACTUS_WATER));
        }
        return Optional.empty();
    }

    public static Optional<ItemWaterRecipe> findDeconcentration(final ItemStack input) {
        if (input.is(Blocks.SNOW.asItem())) {
            return Optional.of(new ItemWaterRecipe(ItemStack.EMPTY, SNOW_WATER));
        }
        if (input.is(Blocks.CACTUS.asItem())) {
            return Optional.of(new ItemWaterRecipe(
                    new ItemStack(ModBlocks.DRY_CACTUS.get()),
                    CACTUS_TO_DRY_CACTUS_WATER
            ));
        }
        if (isLeavesItem(input)) {
            return Optional.of(new ItemWaterRecipe(ItemStack.EMPTY, LEAVES_WATER));
        }
        return Optional.empty();
    }

    private static boolean isLeavesItem(final ItemStack input) {
        return input.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock().defaultBlockState().is(BlockTags.LEAVES);
    }

    public record BlockInsertion(BlockState outputState, int waterAmount) {
    }

    public record BlockExtraction(Optional<BlockState> replacementState, int waterAmount) {
    }

    public record ItemWaterRecipe(ItemStack output, int waterAmount) {
        public boolean hasOutput() {
            return !this.output.isEmpty();
        }
    }

    private WaterExtractorRecipes() {
    }
}
