package committee.nova.mods.skyresources3.guide;

import committee.nova.mods.skyresources3.item.DirtyGem;
import committee.nova.mods.skyresources3.item.OreAlchemyDust;
import committee.nova.mods.skyresources3.machine.MachineVariant;
import committee.nova.mods.skyresources3.registry.ModItems;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public final class GuidePages {
    private static final String STAGE_1 = "stage1";
    private static final String STAGE_2 = "stage2";
    private static final String STAGE_3 = "stage3";
    private static final String STAGE_4 = "stage4";

    private static final String GUIDE_PREFIX = "guide.skyresources3.";

    private static final List<String> CATEGORIES = List.of(
            key(STAGE_1),
            key(STAGE_2),
            key(STAGE_3),
            key(STAGE_4)
    );

    private static final List<GuidePage> PAGES = List.of(
            page(
                    "stage1",
                    STAGE_1,
                    stack(Items.BEDROCK),
                    link("sandIsland", stack(Items.SAND)),
                    link("snowIsland", stack(Blocks.SNOW)),
                    link("woodIsland", stack(Items.OAK_PLANKS)),
                    link("grassIsland", stack(Items.GRASS_BLOCK)),
                    link("magmaIsland", stack(() -> ModItems.PETRIFIED_WOOD.get()))
            ),
            page("sandIsland", STAGE_1, stack(Items.SAND)),
            page(
                    "lifeInfusion",
                    STAGE_1,
                    stack(() -> ModItems.ALCHEMICAL_INFUSION_STONE.get()),
                    recipe(GuideRecipeTargets.PROCESS_INFUSION, stack(() -> ModItems.ALCHEMICAL_INFUSION_STONE.get()))
            ),
            page(
                    "knife",
                    STAGE_1,
                    stack(() -> ModItems.CACTUS_CUTTING_KNIFE.get()),
                    recipe(GuideRecipeTargets.PROCESS_KNIFE, stack(() -> ModItems.CACTUS_CUTTING_KNIFE.get()))
            ),
            page("snowIsland", STAGE_1, stack(Blocks.SNOW)),
            page(
                    "freezer",
                    STAGE_1,
                    stack(() -> ModItems.MINI_FREEZER.get()),
                    recipe(stack(() -> ModItems.MINI_FREEZER.get())),
                    recipe(GuideRecipeTargets.PROCESS_FREEZER, stack(() -> ModItems.FLESHY_SNOW_NUGGET.get())),
                    recipe(stack(() -> ModItems.LIGHT_FREEZER.get())),
                    image("ironFreezer", stack(() -> ModItems.IRON_FREEZER.get()))
            ),
            page("heavySnowball", STAGE_1, stack(() -> ModItems.HEAVY_SNOWBALL.get())),
            page("woodIsland", STAGE_1, stack(Items.OAK_PLANKS)),
            page("survFish", STAGE_1, stack(() -> ModItems.SURVIVALIST_FISHING_ROD.get())),
            page("grassIsland", STAGE_1, stack(Items.GRASS_BLOCK)),
            page("magmaIsland", STAGE_1, stack(() -> ModItems.PETRIFIED_WOOD.get())),
            page("magmaStone", STAGE_1, stack(() -> ModItems.MAGMAFIED_STONE.get())),
            page("stage2", STAGE_2, stack(Items.OAK_LOG)),
            page("seeds", STAGE_2, stack(Items.PUMPKIN_SEEDS)),
            page(
                    "casing",
                    STAGE_2,
                    stack(() -> ModItems.MACHINE_CASINGS.get(MachineVariant.WOODEN).get()),
                    link("combustionHeater", stack(() -> ModItems.COMBUSTION_HEATERS.get(MachineVariant.WOODEN).get())),
                    link("heatProvider", stack(() -> ModItems.HEAT_PROVIDERS.get(MachineVariant.WOODEN).get())),
                    link("condenser", stack(() -> ModItems.CONDENSERS.get(MachineVariant.WOODEN).get()))
            ),
            page(
                    "combustionHeater",
                    STAGE_2,
                    stack(() -> ModItems.COMBUSTION_HEATERS.get(MachineVariant.WOODEN).get()),
                    link("casing", stack(() -> ModItems.MACHINE_CASINGS.get(MachineVariant.WOODEN).get())),
                    recipe(stack(() -> ModItems.COMBUSTION_HEATERS.get(MachineVariant.WOODEN).get())),
                    recipe(GuideRecipeTargets.PROCESS_COMBUSTION, stack(() -> ModItems.ALCHEMICAL_COAL.get())),
                    image("combustion", stack(() -> ModItems.COMBUSTION_HEATERS.get(MachineVariant.WOODEN).get()))
            ),
            page("waterExtractor", STAGE_2, stack(() -> ModItems.WATER_EXTRACTOR.get())),
            page("dirtFurnace", STAGE_2, stack(() -> ModItems.DIRT_FURNACE.get())),
            page(
                    "lavaBlaze",
                    STAGE_2,
                    stack(() -> ModItems.BLAZE_POWDER_BLOCK.get()),
                    link("heatSources", stack(Items.TORCH)),
                    recipe(stack(() -> ModItems.BLAZE_POWDER_BLOCK.get())),
                    image("lava", stack(() -> ModItems.BLAZE_POWDER_BLOCK.get()))
            ),
            page(
                    "heatSources",
                    STAGE_2,
                    stack(Items.TORCH),
                    recipe(GuideRecipeTargets.HEAT_SOURCES, stack(Items.TORCH))
            ),
            page(
                    "heatProvider",
                    STAGE_2,
                    stack(() -> ModItems.HEAT_PROVIDERS.get(MachineVariant.WOODEN).get()),
                    recipe(GuideRecipeTargets.HEAT_SOURCES, stack(() -> ModItems.HEAT_PROVIDERS.get(MachineVariant.WOODEN).get()))
            ),
            page(
                    "rockGrinder",
                    STAGE_2,
                    stack(() -> ModItems.STONE_GRINDER.get()),
                    recipe(GuideRecipeTargets.PROCESS_ROCK_GRINDER, stack(() -> ModItems.CRUSHED_STONE.get()))
            ),
            page(
                    "metalCreation",
                    STAGE_2,
                    stack(() -> ModItems.ORE_ALCHEMICAL_DUSTS.get(OreAlchemyDust.IRON).get()),
                    link("fusionTable", stack(() -> ModItems.FUSION_TABLE.get())),
                    link("crucible", stack(() -> ModItems.CRUCIBLE.get())),
                    link("fluidDropper", stack(() -> ModItems.FLUID_DROPPER.get())),
                    link("condenser", stack(() -> ModItems.CONDENSERS.get(MachineVariant.WOODEN).get())),
                    image("crystalSetup", stack(() -> ModItems.CRUCIBLE.get()))
            ),
            page(
                    "crucible",
                    STAGE_2,
                    stack(() -> ModItems.CRUCIBLE.get()),
                    recipe(GuideRecipeTargets.CRUCIBLE, stack(() -> ModItems.CRYSTAL_FLUID_BUCKET.get()))
            ),
            page("fluidDropper", STAGE_2, stack(() -> ModItems.FLUID_DROPPER.get())),
            page(
                    "fusionTable",
                    STAGE_2,
                    stack(() -> ModItems.FUSION_TABLE.get()),
                    recipe(GuideRecipeTargets.PROCESS_FUSION, stack(() -> ModItems.PRIMUS_ALCHEMICAL_DUST.get()))
            ),
            page(
                    "condenser",
                    STAGE_2,
                    stack(() -> ModItems.CONDENSERS.get(MachineVariant.WOODEN).get()),
                    recipe(GuideRecipeTargets.CONDENSER, stack(() -> ModItems.CONDENSERS.get(MachineVariant.WOODEN).get()))
            ),
            page("crucibleInserter", STAGE_2, stack(() -> ModItems.CRUCIBLE_INSERTER.get())),
            page("combustionCollector", STAGE_2, stack(() -> ModItems.COMBUSTION_COLLECTOR.get())),
            page(
                    "crushedStone",
                    STAGE_2,
                    stack(() -> ModItems.CRUSHED_STONE.get()),
                    recipe(GuideRecipeTargets.PROCESS_CAULDRON_CLEAN, stack(() -> ModItems.CRUSHED_STONE.get()))
            ),
            page(
                    "gemProduction",
                    STAGE_2,
                    stack(() -> ModItems.DIRTY_GEMS.get(DirtyGem.EMERALD).get()),
                    recipe(GuideRecipeTargets.PROCESS_ROCK_GRINDER, stack(() -> ModItems.DIRTY_GEMS.get(DirtyGem.EMERALD).get())),
                    recipe(GuideRecipeTargets.PROCESS_CAULDRON_CLEAN, stack(Items.EMERALD))
            ),
            page("wildlifeAttractor", STAGE_2, stack(() -> ModItems.WILDLIFE_ATTRACTOR.get())),
            page("stage3", STAGE_3, stack(Items.GLOWSTONE_DUST)),
            page("quickDropper", STAGE_3, stack(() -> ModItems.QUICK_DROPPER.get())),
            page(
                    "rockCrusher",
                    STAGE_3,
                    stack(() -> ModItems.ROCK_CRUSHER.get()),
                    recipe(GuideRecipeTargets.PROCESS_ROCK_GRINDER, stack(() -> ModItems.CRUSHED_STONE.get()))
            ),
            page(
                    "rockCleaner",
                    STAGE_3,
                    stack(() -> ModItems.ROCK_CLEANER.get()),
                    recipe(GuideRecipeTargets.PROCESS_CAULDRON_CLEAN, stack(() -> ModItems.CRUSHED_STONE.get()))
            ),
            page(
                    "combustionController",
                    STAGE_3,
                    stack(() -> ModItems.COMBUSTION_CONTROLLER.get()),
                    recipe(GuideRecipeTargets.PROCESS_COMBUSTION, stack(() -> ModItems.ALCHEMICAL_COAL.get()))
            ),
            page("aqueous", STAGE_3, stack(() -> ModItems.AQUEOUS_CONCENTRATOR.get())),
            page("stage4", STAGE_4, stack(() -> ModItems.DARK_MATTER.get())),
            page("dmWarper", STAGE_4, stack(() -> ModItems.DARK_MATTER_WARPER.get())),
            page(
                    "end",
                    STAGE_4,
                    stack(Items.ENDER_EYE),
                    recipe(stack(() -> ModItems.END_PORTAL_CORE.get())),
                    image("end", stack(() -> ModItems.END_PORTAL_CORE.get())),
                    link("end2", stack(() -> ModItems.SILVERFISH_DISRUPTOR.get()))
            ),
            page(
                    "end2",
                    STAGE_4,
                    stack(() -> ModItems.SILVERFISH_DISRUPTOR.get()),
                    recipe(stack(() -> ModItems.SILVERFISH_DISRUPTOR.get())),
                    image("end2", stack(() -> ModItems.SILVERFISH_DISRUPTOR.get()))
            ),
            page("healthGem", STAGE_4, stack(() -> ModItems.HEALTH_GEM.get())),
            page(
                    "lifeInfuser",
                    STAGE_4,
                    stack(() -> ModItems.LIFE_INFUSER.get()),
                    link("lifeInfusion", stack(() -> ModItems.ALCHEMICAL_INFUSION_STONE.get())),
                    link("healthGem", stack(() -> ModItems.HEALTH_GEM.get())),
                    recipe(stack(() -> ModItems.LIFE_INFUSER.get())),
                    image("infuser", stack(() -> ModItems.LIFE_INFUSER.get()))
            ),
            page("lifeInjector", STAGE_4, stack(() -> ModItems.LIFE_INJECTOR.get()))
    );

    private static final Map<String, GuidePage> PAGES_BY_ID = indexById();

    public static List<String> categories() {
        return CATEGORIES;
    }

    public static List<GuidePage> pages() {
        return PAGES;
    }

    public static Optional<GuidePage> find(final String id) {
        return Optional.ofNullable(PAGES_BY_ID.get(id));
    }

    public static List<GuidePage> pagesInCategory(final String categoryKey) {
        return PAGES.stream()
                .filter(page -> page.categoryKey().equals(categoryKey))
                .toList();
    }

    private static GuidePage page(
            final String id,
            final String category,
            final Supplier<ItemStack> iconSupplier,
            final GuideAction... actions
    ) {
        final String baseKey = key(category) + "." + id;
        return new GuidePage(id, key(category), baseKey + ".title", baseKey + ".text", iconSupplier, List.of(actions));
    }

    private static GuideAction link(final String pageId, final Supplier<ItemStack> iconSupplier) {
        return GuideAction.link(pageId, iconSupplier);
    }

    private static GuideAction recipe(final Supplier<ItemStack> iconSupplier) {
        return GuideAction.recipe(iconSupplier);
    }

    private static GuideAction recipe(final String target, final Supplier<ItemStack> iconSupplier) {
        return GuideAction.recipe(target, iconSupplier);
    }

    private static GuideAction image(final String structureId, final Supplier<ItemStack> iconSupplier) {
        return GuideAction.image(structureId, "guide.skyresources3.structure." + structureId, iconSupplier);
    }

    private static String key(final String name) {
        return GUIDE_PREFIX + name;
    }

    private static Supplier<ItemStack> stack(final ItemLike item) {
        return () -> new ItemStack(item);
    }

    private static Supplier<ItemStack> stack(final Supplier<? extends ItemLike> item) {
        return () -> new ItemStack(item.get());
    }

    private static Map<String, GuidePage> indexById() {
        final Map<String, GuidePage> pages = new LinkedHashMap<>();
        for (final GuidePage page : PAGES) {
            pages.put(page.id(), page);
        }
        return Collections.unmodifiableMap(pages);
    }

    private GuidePages() {
    }
}
