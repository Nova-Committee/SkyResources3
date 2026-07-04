package committee.nova.mods.skyresources3.init.event;

import committee.nova.mods.skyresources3.Config;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;

public final class GrassSeedDropEvents {
    private static final int GRASS_SEED_CHANCE = 8;
    private static final int WHEAT_SEED_WEIGHT = 10;

    public static void onBlockDrops(final BlockEvent.BreakEvent event) {
        final BlockState state = event.getState();
        if (!(event.getLevel() instanceof ServerLevel level) || !isGrassSeedTarget(state)) {
            return;
        }

        final List<WeightedSeed> seeds = seedPool();
        if (seeds.size() == 1) {
            return;
        }

        if (level.random.nextInt(GRASS_SEED_CHANCE) != 0) {
            return;
        }

        final ItemStack selected = selectSeed(level.random, seeds);
        if (selected.isEmpty()) {
            return;
        }

        final BlockPos pos = event.getPos();
        level.addFreshEntity(new ItemEntity(
                level,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                selected
        ));
    }

    private static boolean isGrassSeedTarget(final BlockState state) {
        return state.is(Blocks.GRASS)
                || state.is(Blocks.TALL_GRASS)
                || state.is(Blocks.FERN)
                || state.is(Blocks.LARGE_FERN);
    }

    private static List<WeightedSeed> seedPool() {
        final List<WeightedSeed> seeds = new ArrayList<>();
        seeds.add(new WeightedSeed(Items.WHEAT_SEEDS, WHEAT_SEED_WEIGHT));
        addConfiguredSeed(seeds, Config.addBeetrootSeedDrop, Items.BEETROOT_SEEDS, 10);
        addConfiguredSeed(seeds, Config.addMelonSeedDrop, Items.MELON_SEEDS, 12);
        addConfiguredSeed(seeds, Config.addPumpkinSeedDrop, Items.PUMPKIN_SEEDS, 12);
        addConfiguredSeed(seeds, Config.addCocoaBeanDrop, Items.COCOA_BEANS, 4);
        addConfiguredSeed(seeds, Config.addCarrotDrop, Items.CARROT, 7);
        addConfiguredSeed(seeds, Config.addPotatoDrop, Items.POTATO, 7);
        return seeds;
    }

    private static void addConfiguredSeed(
            final List<WeightedSeed> seeds,
            final boolean enabled,
            final Item item,
            final int weight
    ) {
        if (enabled) {
            seeds.add(new WeightedSeed(item, weight));
        }
    }

    private static ItemStack selectSeed(final RandomSource random, final List<WeightedSeed> seeds) {
        int totalWeight = 0;
        for (final WeightedSeed seed : seeds) {
            totalWeight += seed.weight();
        }

        int selectedWeight = random.nextInt(totalWeight);
        for (final WeightedSeed seed : seeds) {
            selectedWeight -= seed.weight();
            if (selectedWeight < 0) {
                return new ItemStack(seed.item());
            }
        }
        return ItemStack.EMPTY;
    }

    private record WeightedSeed(Item item, int weight) {
    }

    private GrassSeedDropEvents() {
    }
}
