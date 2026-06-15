package committee.nova.mods.skyresources3.core.guide;

import committee.nova.mods.skyresources3.common.item.MachineCasingItem;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public final class GuideStructures {
    private static final String STRUCTURE_PREFIX = "guide.skyresources.structure.";

    private static final Map<String, GuideStructure> STRUCTURES = index(List.of(
            ironFreezer(),
            combustion(),
            lava(),
            crystalSetup(),
            endPortal(),
            improvedEndPortal(),
            lifeInfuser()
    ));

    public static Optional<GuideStructure> find(final String id) {
        return Optional.ofNullable(STRUCTURES.get(id));
    }

    private static GuideStructure ironFreezer() {
        return structure("ironFreezer", List.of(
                entry(0, -1, 0, () -> ModItems.IRON_FREEZER.get()),
                entry(0, 0, 0, () -> ModItems.IRON_FREEZER.get())
        ));
    }

    private static GuideStructure combustion() {
        return structure("combustion", List.of(
                new GuideStructure.BlockEntry(0, -1, 0, () -> MachineCasingItem.forType(ModDataPackRegistries.WOODEN)),
                entry(1, 0, 0, Items.OAK_PLANKS),
                entry(-1, 0, 0, Items.OAK_PLANKS),
                entry(0, 0, 1, Items.OAK_PLANKS),
                entry(0, 0, -1, Items.OAK_PLANKS),
                entry(0, 1, 0, Items.OAK_PLANKS),
                entry(0, -1, -1, Items.OAK_BUTTON)
        ));
    }

    private static GuideStructure lava() {
        return structure("lava", List.of(
                entry(0, -1, 0, Items.TORCH),
                entry(0, 0, 0, () -> ModItems.BLAZE_POWDER_BLOCK.get())
        ));
    }

    private static GuideStructure crystalSetup() {
        return structure("crystalSetup", List.of(
                new GuideStructure.BlockEntry(0, -1, 0, () -> MachineCasingItem.forType(ModDataPackRegistries.WOODEN)),
                entry(-1, 0, 0, Items.GLASS),
                entry(0, 0, 1, Items.GLASS),
                entry(0, 0, -1, Items.GLASS),
                entry(1, 0, 0, Items.GLASS),
                entry(0, 1, 0, () -> ModItems.FLUID_DROPPER.get()),
                entry(0, 2, 0, () -> ModItems.FLUID_DROPPER.get()),
                entry(1, 2, 0, () -> ModItems.CRUCIBLE.get()),
                entry(1, 1, 0, Items.TORCH)
        ));
    }

    private static GuideStructure endPortal() {
        final List<GuideStructure.BlockEntry> blocks = new ArrayList<>();
        addBaseEndPortal(blocks);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                    blocks.add(entry(x, -1, z, () -> ModItems.DARK_MATTER_BLOCK.get()));
                }
            }
        }
        addEndPillars(blocks, 2);
        blocks.add(entry(0, 0, 0, Items.STONE_BUTTON));
        return structure("end", blocks);
    }

    private static GuideStructure improvedEndPortal() {
        final List<GuideStructure.BlockEntry> blocks = new ArrayList<>();
        addBaseEndPortal(blocks);
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (Math.abs(x) == 3 || Math.abs(z) == 3) {
                    blocks.add(entry(x, -1, z, () -> ModItems.LIGHT_MATTER_BLOCK.get()));
                } else if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                    blocks.add(entry(x, -1, z, () -> ModItems.DARK_MATTER_BLOCK.get()));
                }
            }
        }
        addEndPillars(blocks, 3);
        blocks.add(entry(0, 0, 0, Items.STONE_BUTTON));
        return structure("end2", blocks);
    }

    private static GuideStructure lifeInfuser() {
        return structure("infuser", List.of(
                entry(-1, -1, -1, Items.OAK_LOG),
                entry(-1, 0, -1, Items.OAK_LOG),
                entry(1, -1, -1, Items.OAK_LOG),
                entry(1, 0, -1, Items.OAK_LOG),
                entry(-1, -1, 1, Items.OAK_LOG),
                entry(-1, 0, 1, Items.OAK_LOG),
                entry(1, -1, 1, Items.OAK_LOG),
                entry(1, 0, 1, Items.OAK_LOG),
                entry(-1, 1, -1, Items.OAK_LEAVES),
                entry(0, 1, -1, Items.OAK_LEAVES),
                entry(1, 1, -1, Items.OAK_LEAVES),
                entry(1, 1, 0, Items.OAK_LEAVES),
                entry(1, 1, 1, Items.OAK_LEAVES),
                entry(0, 1, 1, Items.OAK_LEAVES),
                entry(-1, 1, 1, Items.OAK_LEAVES),
                entry(-1, 1, 0, Items.OAK_LEAVES),
                entry(0, 0, 0, () -> ModItems.LIFE_INFUSER.get()),
                entry(0, 1, 0, () -> ModItems.DARK_MATTER_BLOCK.get()),
                entry(0, 2, 0, Items.STONE_BUTTON)
        ));
    }

    private static void addBaseEndPortal(final List<GuideStructure.BlockEntry> blocks) {
        blocks.add(entry(0, -1, 0, () -> ModItems.END_PORTAL_CORE.get()));
        blocks.add(entry(-1, -1, 0, Items.GOLD_BLOCK));
        blocks.add(entry(1, -1, 0, Items.GOLD_BLOCK));
        blocks.add(entry(0, -1, -1, Items.GOLD_BLOCK));
        blocks.add(entry(0, -1, 1, Items.GOLD_BLOCK));
        blocks.add(entry(-1, -1, -1, Items.DIAMOND_BLOCK));
        blocks.add(entry(1, -1, -1, Items.DIAMOND_BLOCK));
        blocks.add(entry(-1, -1, 1, Items.DIAMOND_BLOCK));
        blocks.add(entry(1, -1, 1, Items.DIAMOND_BLOCK));
    }

    private static void addEndPillars(final List<GuideStructure.BlockEntry> blocks, final int topY) {
        for (final int x : new int[] {-2, 2}) {
            for (final int z : new int[] {-2, 2}) {
                blocks.add(entry(x, 0, z, Items.END_STONE_BRICKS));
                blocks.add(entry(x, 1, z, Items.END_STONE_BRICKS));
                blocks.add(entry(x, 2, z, Items.GLOWSTONE));
                if (topY >= 3) {
                    blocks.add(entry(x, 3, z, () -> ModItems.SILVERFISH_DISRUPTOR.get()));
                }
            }
        }
        if (topY < 3) {
            return;
        }
        for (final int x : new int[] {-3, 3}) {
            for (final int z : new int[] {-3, 3}) {
                blocks.add(entry(x, 0, z, Items.PURPUR_PILLAR));
                blocks.add(entry(x, 1, z, Items.PURPUR_PILLAR));
                blocks.add(entry(x, 2, z, Items.PURPUR_PILLAR));
                blocks.add(entry(x, 3, z, Items.PURPUR_PILLAR));
                blocks.add(entry(x, 4, z, Items.END_ROD));
            }
        }
    }

    private static GuideStructure structure(final String id, final List<GuideStructure.BlockEntry> blocks) {
        return new GuideStructure(id, STRUCTURE_PREFIX + id, blocks);
    }

    private static GuideStructure.BlockEntry entry(
            final int x,
            final int y,
            final int z,
            final ItemLike item
    ) {
        return entry(x, y, z, () -> item);
    }

    private static GuideStructure.BlockEntry entry(
            final int x,
            final int y,
            final int z,
            final Supplier<? extends ItemLike> item
    ) {
        return new GuideStructure.BlockEntry(x, y, z, stack(item));
    }

    private static Supplier<ItemStack> stack(final Supplier<? extends ItemLike> item) {
        return () -> new ItemStack(item.get());
    }

    private static Map<String, GuideStructure> index(final List<GuideStructure> structures) {
        final Map<String, GuideStructure> byId = new LinkedHashMap<>();
        for (final GuideStructure structure : structures) {
            byId.put(structure.id(), structure);
        }
        return Collections.unmodifiableMap(byId);
    }

    private GuideStructures() {
    }
}
