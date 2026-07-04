package committee.nova.mods.skyresources3.test;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.core.guide.GuideAction;
import committee.nova.mods.skyresources3.core.guide.GuidePage;
import committee.nova.mods.skyresources3.core.guide.GuidePages;
import committee.nova.mods.skyresources3.core.guide.GuideRecipeTargets;
import committee.nova.mods.skyresources3.core.guide.GuideStructure;
import committee.nova.mods.skyresources3.core.guide.GuideStructures;
import committee.nova.mods.skyresources3.init.registry.ModMenuTypes;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public final class GuideMenuGameTests {
    private static final String EN_US_TRANSLATIONS = "/assets/skyresources/lang/en_us.json";
    private static final Pattern ACTION_MARKER = Pattern.compile("\\{action:(\\d+)}");
    private static final Set<String> GUIDE_RECIPE_TARGETS = Set.of(
            GuideRecipeTargets.PROCESS_COMBUSTION,
            GuideRecipeTargets.PROCESS_FREEZER,
            GuideRecipeTargets.PROCESS_FUSION,
            GuideRecipeTargets.PROCESS_INFUSION,
            GuideRecipeTargets.PROCESS_KNIFE,
            GuideRecipeTargets.PROCESS_ROCK_GRINDER,
            GuideRecipeTargets.PROCESS_CAULDRON_CLEAN,
            GuideRecipeTargets.CRUCIBLE,
            GuideRecipeTargets.CONDENSER,
            GuideRecipeTargets.HEAT_SOURCES
    );
    private static final Set<String> STRUCTURE_GUIDE_UI_KEYS = Set.of(
            "screen.skyresources.guide.structure_scene_layer",
            "button.skyresources.guide.structure_back",
            "button.skyresources.guide.structure_prev_step_short",
            "button.skyresources.guide.structure_next_step_short",
            "button.skyresources.guide.structure_restart_short",
            "button.skyresources.guide.structure_pause_short",
            "button.skyresources.guide.structure_play_short"
    );

    public static void guideDataIntegrity(final GameTestHelper helper) {
        final JsonObject translations = loadTranslations(helper);
        final Set<String> pageIds = new HashSet<>();

        helper.assertTrue(!GuidePages.categories().isEmpty(), "Guide categories should not be empty");
        for (final String categoryKey : GuidePages.categories()) {
            assertTranslationExists(helper, translations, categoryKey);
        }
        for (final String uiKey : STRUCTURE_GUIDE_UI_KEYS) {
            assertTranslationExists(helper, translations, uiKey);
        }

        helper.assertTrue(!GuidePages.pages().isEmpty(), "Guide pages should not be empty");
        for (final GuidePage page : GuidePages.pages()) {
            validatePage(helper, translations, pageIds, page);
        }
        validateKnownStructureLayouts(helper);

        helper.succeed();
    }

    public static void menuTypesResolve(final GameTestHelper helper) {
        assertMenuType(helper, "fusion_table", ModMenuTypes.FUSION_TABLE.get());
        assertMenuType(helper, "dirt_furnace", ModMenuTypes.DIRT_FURNACE.get());
        assertMenuType(helper, "freezer", ModMenuTypes.FREEZER.get());
        assertMenuType(helper, "quick_dropper", ModMenuTypes.QUICK_DROPPER.get());
        assertMenuType(helper, "dark_matter_warper", ModMenuTypes.DARK_MATTER_WARPER.get());
        assertMenuType(helper, "end_portal_core", ModMenuTypes.END_PORTAL_CORE.get());
        assertMenuType(helper, "crucible_inserter", ModMenuTypes.CRUCIBLE_INSERTER.get());
        assertMenuType(helper, "rock_crusher", ModMenuTypes.ROCK_CRUSHER.get());
        assertMenuType(helper, "rock_cleaner", ModMenuTypes.ROCK_CLEANER.get());
        assertMenuType(helper, "aqueous_machine", ModMenuTypes.AQUEOUS_MACHINE.get());
        assertMenuType(helper, "wildlife_attractor", ModMenuTypes.WILDLIFE_ATTRACTOR.get());
        assertMenuType(helper, "machine_casing", ModMenuTypes.MACHINE_CASING.get());
        assertMenuType(helper, "life_infuser", ModMenuTypes.LIFE_INFUSER.get());
        assertMenuType(helper, "life_injector", ModMenuTypes.LIFE_INJECTOR.get());
        assertMenuType(helper, "combustion_collector", ModMenuTypes.COMBUSTION_COLLECTOR.get());
        assertMenuType(helper, "combustion_controller", ModMenuTypes.COMBUSTION_CONTROLLER.get());

        helper.succeed();
    }

    private static void validatePage(
            final GameTestHelper helper,
            final JsonObject translations,
            final Set<String> pageIds,
            final GuidePage page
    ) {
        helper.assertTrue(!page.id().isBlank(), "Guide page id should not be blank");
        helper.assertTrue(pageIds.add(page.id()), "Guide page id should be unique: " + page.id());
        helper.assertTrue(
                GuidePages.find(page.id()).orElse(null) == page,
                "Guide page lookup should return page: " + page.id()
        );
        helper.assertTrue(
                GuidePages.categories().contains(page.categoryKey()),
                "Guide page category should be registered: " + page.id()
        );
        assertTranslationExists(helper, translations, page.categoryKey());
        assertTranslationExists(helper, translations, page.titleKey());
        final String text = translationString(helper, translations, page.textKey());
        assertNonEmptyIcon(helper, page.icon(), "Guide page icon should resolve: " + page.id());

        final var markerMatcher = ACTION_MARKER.matcher(text);
        while (markerMatcher.find()) {
            final int actionIndex = Integer.parseInt(markerMatcher.group(1));
            helper.assertTrue(
                    actionIndex >= 1 && actionIndex <= page.actions().size(),
                    "Guide page inline action marker should resolve: " + page.id() + " #" + actionIndex
            );
        }

        for (int index = 0; index < page.actions().size(); index++) {
            validateAction(helper, translations, page, page.actions().get(index), index + 1);
        }
    }

    private static void validateAction(
            final GameTestHelper helper,
            final JsonObject translations,
            final GuidePage page,
            final GuideAction action,
            final int displayIndex
    ) {
        assertNonEmptyIcon(
                helper,
                action.icon(),
                "Guide action icon should resolve: " + page.id() + " #" + displayIndex
        );
        switch (action.type()) {
            case LINK -> validateLinkAction(helper, page, action, displayIndex);
            case RECIPE -> validateRecipeAction(helper, page, action, displayIndex);
            case IMAGE -> validateImageAction(helper, translations, page, action, displayIndex);
        }
    }

    private static void validateLinkAction(
            final GameTestHelper helper,
            final GuidePage page,
            final GuideAction action,
            final int displayIndex
    ) {
        helper.assertTrue(
                !action.target().isBlank(),
                "Guide link action target should not be blank: " + page.id() + " #" + displayIndex
        );
        helper.assertTrue(
                GuidePages.find(action.target()).isPresent(),
                "Guide link action target should resolve: " + page.id() + " -> " + action.target()
        );
    }

    private static void validateRecipeAction(
            final GameTestHelper helper,
            final GuidePage page,
            final GuideAction action,
            final int displayIndex
    ) {
        helper.assertTrue(
                action.target().isBlank() || GUIDE_RECIPE_TARGETS.contains(action.target()),
                "Guide recipe action target should be blank or registered: " + page.id() + " #" + displayIndex
        );
    }

    private static void validateImageAction(
            final GameTestHelper helper,
            final JsonObject translations,
            final GuidePage page,
            final GuideAction action,
            final int displayIndex
    ) {
        helper.assertTrue(
                !action.target().isBlank(),
                "Guide image action target should not be blank: " + page.id() + " #" + displayIndex
        );
        helper.assertTrue(
                !action.labelKey().isBlank(),
                "Guide image action label key should not be blank: " + page.id() + " #" + displayIndex
        );
        assertTranslationExists(helper, translations, action.labelKey());

        final GuideStructure structure = GuideStructures.find(action.target()).orElse(null);
        helper.assertTrue(
                structure != null,
                "Guide image action target should resolve: " + page.id() + " -> " + action.target()
        );
        assertTranslationExists(helper, translations, structure.titleKey());
        helper.assertTrue(!structure.blocks().isEmpty(), "Guide structure should contain blocks: " + structure.id());
        for (final GuideStructure.BlockEntry block : structure.blocks()) {
            assertNonEmptyIcon(
                    helper,
                    block.icon(),
                    "Guide structure block icon should resolve: " + structure.id()
            );
        }
    }

    private static void validateKnownStructureLayouts(final GameTestHelper helper) {
        assertStructureLayout(helper, "ironFreezer", Set.of(
                pos(0, -1, 0),
                pos(0, 0, 0)
        ));
        assertStructureLayout(helper, "combustion", Set.of(
                pos(0, -1, 0),
                pos(1, 0, 0),
                pos(-1, 0, 0),
                pos(0, 0, 1),
                pos(0, 0, -1),
                pos(0, 1, 0),
                pos(0, -1, -1)
        ));
        assertStructureLayout(helper, "lava", Set.of(
                pos(0, -1, 0),
                pos(0, 0, 0)
        ));
        assertStructureLayout(helper, "crystalSetup", Set.of(
                pos(0, -1, 0),
                pos(-1, 0, 0),
                pos(0, 0, 1),
                pos(0, 0, -1),
                pos(1, 0, 0),
                pos(0, 1, 0),
                pos(0, 2, 0),
                pos(1, 2, 0),
                pos(1, 1, 0)
        ));
        assertStructureLayout(helper, "end", endPortalPositions(false));
        assertStructureLayout(helper, "end2", endPortalPositions(true));
        assertStructureLayout(helper, "infuser", lifeInfuserPositions());
    }

    private static void assertStructureLayout(
            final GameTestHelper helper,
            final String structureId,
            final Set<StructurePos> expectedPositions
    ) {
        final GuideStructure structure = GuideStructures.find(structureId).orElse(null);
        helper.assertTrue(structure != null, "Guide structure should resolve: " + structureId);
        final Set<StructurePos> actualPositions = new HashSet<>();
        for (final GuideStructure.BlockEntry block : structure.blocks()) {
            final StructurePos position = pos(block.x(), block.y(), block.z());
            helper.assertTrue(
                    actualPositions.add(position),
                    "Guide structure should not duplicate position: " + structureId + " " + position
            );
        }
        helper.assertTrue(
                actualPositions.size() == expectedPositions.size(),
                "Guide structure should match original block count: "
                        + structureId
                        + " expected "
                        + expectedPositions.size()
                        + " got "
                        + actualPositions.size()
        );
        for (final StructurePos expectedPosition : expectedPositions) {
            helper.assertTrue(
                    actualPositions.contains(expectedPosition),
                    "Guide structure should include original position: " + structureId + " " + expectedPosition
            );
        }
    }

    private static Set<StructurePos> endPortalPositions(final boolean improved) {
        final Set<StructurePos> positions = new HashSet<>();
        addBaseEndPortalPositions(positions);
        final int radius = improved ? 3 : 2;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (Math.abs(x) == radius || Math.abs(z) == radius) {
                    positions.add(pos(x, -1, z));
                } else if (improved && (Math.abs(x) == 2 || Math.abs(z) == 2)) {
                    positions.add(pos(x, -1, z));
                }
            }
        }
        addEndPillarPositions(positions, improved);
        positions.add(pos(0, 0, 0));
        return positions;
    }

    private static void addBaseEndPortalPositions(final Set<StructurePos> positions) {
        positions.add(pos(0, -1, 0));
        positions.add(pos(-1, -1, 0));
        positions.add(pos(1, -1, 0));
        positions.add(pos(0, -1, -1));
        positions.add(pos(0, -1, 1));
        positions.add(pos(-1, -1, -1));
        positions.add(pos(1, -1, -1));
        positions.add(pos(-1, -1, 1));
        positions.add(pos(1, -1, 1));
    }

    private static void addEndPillarPositions(final Set<StructurePos> positions, final boolean improved) {
        for (final int x : new int[] {-2, 2}) {
            for (final int z : new int[] {-2, 2}) {
                positions.add(pos(x, 0, z));
                positions.add(pos(x, 1, z));
                positions.add(pos(x, 2, z));
                if (improved) {
                    positions.add(pos(x, 3, z));
                }
            }
        }
        if (!improved) {
            return;
        }
        for (final int x : new int[] {-3, 3}) {
            for (final int z : new int[] {-3, 3}) {
                positions.add(pos(x, 0, z));
                positions.add(pos(x, 1, z));
                positions.add(pos(x, 2, z));
                positions.add(pos(x, 3, z));
                positions.add(pos(x, 4, z));
            }
        }
    }

    private static Set<StructurePos> lifeInfuserPositions() {
        final Set<StructurePos> positions = new HashSet<>();
        for (final int x : new int[] {-1, 1}) {
            for (final int z : new int[] {-1, 1}) {
                positions.add(pos(x, -1, z));
                positions.add(pos(x, 0, z));
            }
        }
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x != 0 || z != 0) {
                    positions.add(pos(x, 1, z));
                }
            }
        }
        positions.add(pos(0, 0, 0));
        positions.add(pos(0, 1, 0));
        positions.add(pos(0, 2, 0));
        return positions;
    }

    private static StructurePos pos(final int x, final int y, final int z) {
        return new StructurePos(x, y, z);
    }

    private static void assertMenuType(
            final GameTestHelper helper,
            final String path,
            final MenuType<?> menuType
    ) {
        final ResourceLocation id = BuiltInRegistries.MENU.getKey(menuType);
        helper.assertTrue(id != null, "Menu type should be registered: " + path);
        GameTestAssertions.assertValueEqual(helper, Skyresources3.MODID, id.getNamespace(), "Menu namespace should match");
        GameTestAssertions.assertValueEqual(helper, path, id.getPath(), "Menu path should match");
    }

    private static void assertTranslationExists(
            final GameTestHelper helper,
            final JsonObject translations,
            final String key
    ) {
        helper.assertTrue(!key.isBlank(), "Translation key should not be blank");
        helper.assertTrue(translations.has(key), "Translation key should exist: " + key);
        helper.assertTrue(
                translations.get(key).isJsonPrimitive(),
                "Translation value should be a string primitive: " + key
        );
    }

    private static String translationString(
            final GameTestHelper helper,
            final JsonObject translations,
            final String key
    ) {
        assertTranslationExists(helper, translations, key);
        return translations.get(key).getAsString();
    }

    private static void assertNonEmptyIcon(
            final GameTestHelper helper,
            final net.minecraft.world.item.ItemStack icon,
            final String message
    ) {
        helper.assertTrue(!icon.isEmpty(), message);
    }

    private static JsonObject loadTranslations(final GameTestHelper helper) {
        try (InputStream stream = GuideMenuGameTests.class.getResourceAsStream(EN_US_TRANSLATIONS)) {
            if (stream == null) {
                helper.fail("Missing translation resource: " + EN_US_TRANSLATIONS);
                return new JsonObject();
            }
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return JsonParser.parseReader(reader).getAsJsonObject();
            }
        } catch (IOException | IllegalStateException exception) {
            helper.fail("Failed to load guide translations: " + exception.getMessage());
            return new JsonObject();
        }
    }

    private record StructurePos(int x, int y, int z) {
    }

    private GuideMenuGameTests() {
    }
}
