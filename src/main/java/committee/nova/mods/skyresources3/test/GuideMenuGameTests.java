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
import net.minecraft.resources.Identifier;
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

    public static void guideDataIntegrity(final GameTestHelper helper) {
        final JsonObject translations = loadTranslations(helper);
        final Set<String> pageIds = new HashSet<>();

        helper.assertTrue(!GuidePages.categories().isEmpty(), "Guide categories should not be empty");
        for (final String categoryKey : GuidePages.categories()) {
            assertTranslationExists(helper, translations, categoryKey);
        }

        helper.assertTrue(!GuidePages.pages().isEmpty(), "Guide pages should not be empty");
        for (final GuidePage page : GuidePages.pages()) {
            validatePage(helper, translations, pageIds, page);
        }

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

    private static void assertMenuType(
            final GameTestHelper helper,
            final String path,
            final MenuType<?> menuType
    ) {
        final Identifier id = BuiltInRegistries.MENU.getKey(menuType);
        helper.assertTrue(id != null, "Menu type should be registered: " + path);
        helper.assertValueEqual(Skyresources3.MODID, id.getNamespace(), "Menu namespace should match");
        helper.assertValueEqual(path, id.getPath(), "Menu path should match");
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

    private GuideMenuGameTests() {
    }
}
