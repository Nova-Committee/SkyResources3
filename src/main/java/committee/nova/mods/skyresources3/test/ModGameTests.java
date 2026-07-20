package committee.nova.mods.skyresources3.test;

import committee.nova.mods.skyresources3.Skyresources3;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;

public final class ModGameTests {
    private static final String DEFAULT_BATCH = "defaultBatch";
    private static final String EMPTY_STRUCTURE = Skyresources3.MODID + ":empty";
    private static final int DEFAULT_MAX_TICKS = 400;
    private static final long DEFAULT_SETUP_TICKS = 1L;

    public static void register(final IEventBus modEventBus) {
        modEventBus.addListener(ModGameTests::registerGameTests);
    }

    private static void registerGameTests(final RegisterGameTestsEvent event) {
        event.register(ModGameTests.class);
    }

    @GameTestGenerator
    public static Collection<TestFunction> generateTests() {
        final List<TestFunction> tests = new ArrayList<>();
        registerFunctionTest(tests, "island_create_reset", IslandCommandGameTests::createInfoAndReset);
        registerFunctionTest(tests, "island_spawn_platform", IslandCommandGameTests::spawnGeneratesConfiguredPlatform);
        registerFunctionTest(
                tests,
                "island_feature_defaults",
                IslandCommandGameTests::voidIslandFeatureDefaultFollowsEmptyFlatWorld
        );
        registerFunctionTest(tests, "island_visit", IslandCommandGameTests::visitTeleportsToOnlinePlayerIsland);
        registerFunctionTest(
                tests,
                "island_offline_visit",
                IslandCommandGameTests::visitTeleportsToOfflineSavedIsland
        );
        registerFunctionTest(tests, "island_magma_template", IslandCommandGameTests::magmaIslandPlacesCrystalFluid);
        registerFunctionTest(
                tests,
                "island_layered_templates",
                IslandCommandGameTests::starterTemplatesUseLegacyLayeredStructures
        );
        registerFunctionTest(
                tests,
                "island_relation_commands",
                IslandCommandGameTests::islandInviteHomeLeaveAndDisband
        );
        registerFunctionTest(
                tests,
                "island_trust_commands",
                IslandCommandGameTests::trustListAndUntrustVisitor
        );
        registerFunctionTest(
                tests,
                "island_offline_identity",
                IslandCommandGameTests::offlineIdentityInviteAndTrust
        );
        registerFunctionTest(tests, "cutting_knife_process", RuntimeMigrationGameTests::cuttingKnifeUsesProcessRecipe);
        registerFunctionTest(tests, "rock_grinder_process", RuntimeMigrationGameTests::rockGrinderUsesProcessRecipe);
        registerFunctionTest(
                tests,
                "water_extractor_places_source",
                WorldFluidPlacementGameTests::waterExtractorPlacesSourceAndConsumesOneBucket
        );
        registerFunctionTest(
                tests,
                "water_extractor_blocked_target",
                WorldFluidPlacementGameTests::waterExtractorBlockedTargetKeepsWater
        );
        registerFunctionTest(
                tests,
                "fluid_dropper_places_source",
                WorldFluidPlacementGameTests::fluidDropperPlacesSourceAndConsumesOneBucket
        );
        registerFunctionTest(
                tests,
                "fluid_dropper_blocked_target",
                WorldFluidPlacementGameTests::fluidDropperBlockedTargetKeepsFluid
        );
        registerFunctionTest(
                tests,
                "magmafied_stone_tick",
                RuntimeMigrationGameTests::magmafiedStoneTicksCrystalFluid
        );
        registerFunctionTest(tests, "infusion_stone_process", LifeInfusionGameTests::infusionStoneUsesProcessRecipe);
        registerFunctionTest(tests, "life_infuser_process", LifeInfusionGameTests::lifeInfuserUsesProcessRecipe);
        registerFunctionTest(
                tests,
                "life_injector_item_capability",
                LifeInfusionGameTests::lifeInjectorItemCapabilityTransfers
        );
        registerFunctionTest(
                tests,
                "life_injector_shift_right_click",
                LifeInfusionGameTests::lifeInjectorShiftRightClickRemovesGemWithHeldItem
        );
        registerFunctionTest(
                tests,
                "life_infuser_item_capability",
                LifeInfusionGameTests::lifeInfuserItemCapabilityTransfers
        );
        registerFunctionTest(
                tests,
                "fusion_table_catalyst_yield",
                FusionTableGameTests::fusionTableCachesFractionalCatalystYield
        );
        registerFunctionTest(
                tests,
                "fusion_table_split_duplicate_inputs",
                FusionTableGameTests::fusionTableSplitDuplicateStacksDoNotMatchRecipe
        );
        registerFunctionTest(
                tests,
                "fusion_table_menu_slot_persistence",
                FusionTableGameTests::fusionTableMenuWritesToBlockEntity
        );
        registerFunctionTest(tests, "iron_freezer_menu_multiblock", FreezerGameTests::ironFreezerMenuReadsValidMultiblock);
        registerFunctionTest(
                tests,
                "light_freezer_menu_multiblock",
                FreezerGameTests::lightFreezerMenuReadsValidMultiblock
        );
        registerFunctionTest(
                tests,
                "combustion_heater_embeds_as_type_id",
                MachineRuntimeGameTests::combustionHeaterEmbedsAsTypeId
        );
        registerFunctionTest(
                tests,
                "combustion_heater_shift_right_click_removes",
                MachineRuntimeGameTests::shiftRightClickRemovesEmbeddedCombustionHeaterWithHeldItem
        );
        registerFunctionTest(
                tests,
                "heat_provider_embeds_as_type_id",
                MachineRuntimeGameTests::heatProviderEmbedsAsTypeIdAndProvidesHeat
        );
        registerFunctionTest(
                tests,
                "standalone_machine_type_components",
                MachineRuntimeGameTests::standaloneMachineBlocksPersistTypeComponents
        );
        registerFunctionTest(
                tests,
                "creative_tab_material_variants",
                MachineRuntimeGameTests::creativeTabIncludesBuiltInMaterialVariants
        );
        registerFunctionTest(
                tests,
                "material_seed_colors",
                MachineRuntimeGameTests::materialSeedColorsResolveByTypeId
        );
        registerFunctionTest(tests, "condenser_drops_output", MachineRuntimeGameTests::condenserDropsOutputWhenNoHandlerExists);
        registerFunctionTest(tests, "condenser_blocked_output", MachineRuntimeGameTests::condenserKeepsSourceWhenOutputIsBlocked);
        registerFunctionTest(
                tests,
                "condenser_catalyst_slot",
                MachineRuntimeGameTests::condenserFuelSlotAcceptsOreAlchemyDustCatalyst
        );
        registerFunctionTest(
                tests,
                "condenser_hopper_output",
                MachineRuntimeGameTests::condenserHopperBelowDoesNotExtractCatalyst
        );
        registerFunctionTest(
                tests,
                "condenser_item_capability",
                MachineRuntimeGameTests::condenserCasingExposesItemCapability
        );
        registerFunctionTest(
                tests,
                "condenser_structure_flow",
                MachineRuntimeGameTests::condenserUsesCasingSourceAboveAndOutputBelow
        );
        registerFunctionTest(
                tests,
                "fluid_dropper_pulls_from_crucible",
                MachineRuntimeGameTests::fluidDropperPullsFromCrucibleAndPlacesSourceBelow
        );
        registerFunctionTest(
                tests,
                "machine_capability_matrix",
                MachineRuntimeGameTests::forgeCapabilityMatrixCoversMigratedMachines
        );
        registerFunctionTest(
                tests,
                "alchemical_glass_properties",
                MachineRuntimeGameTests::alchemicalGlassKeepsTransparentBlockProperties
        );
        registerFunctionTest(tests, "combustion_priority", MachineRuntimeGameTests::combustionControllerUsesFilterPriority);
        registerFunctionTest(tests, "combustion_cooldown", MachineRuntimeGameTests::combustionControllerWaitsForCooldown);
        registerFunctionTest(
                tests,
                "combustion_powered_controller",
                MachineRuntimeGameTests::combustionControllerStopsWhenPowered
        );
        registerFunctionTest(
                tests,
                "combustion_controller_direction",
                MachineRuntimeGameTests::combustionControllerRequiresBackFacingChamber
        );
        registerFunctionTest(
                tests,
                "combustion_controller_all_sides_direction",
                MachineRuntimeGameTests::combustionControllerBackFacesChamberFromEverySide
        );
        registerFunctionTest(
                tests,
                "combustion_controller_ghost_filters",
                MachineRuntimeGameTests::combustionControllerFilterSlotsAreGhosts
        );
        registerFunctionTest(
                tests,
                "combustion_controller_wood_stone_reject",
                MachineRuntimeGameTests::woodAndStoneCombustionHeatersRejectSmartController
        );
        registerFunctionTest(
                tests,
                "combustion_collector_overflow",
                MachineRuntimeGameTests::combustionCollectorDropsOverflow
        );
        registerFunctionTest(
                tests,
                "manual_combustion_collector_output",
                MachineRuntimeGameTests::manualCombustionRoutesOutputsToCollector
        );
        registerFunctionTest(
                tests,
                "manual_combustion_multi_input_priority",
                MachineRuntimeGameTests::manualCombustionPrefersMultiInputRecipe
        );
        registerFunctionTest(
                tests,
                "combustion_collector_wood_stone_reject",
                MachineRuntimeGameTests::woodAndStoneCombustionHeatersRejectCollector
        );
        registerFunctionTest(
                tests,
                "wooden_combustion_structure",
                MachineRuntimeGameTests::woodenCombustionHeaterUsesWoodStructure
        );
        registerFunctionTest(
                tests,
                "stone_combustion_structure",
                MachineRuntimeGameTests::stoneCombustionHeaterRejectsAutomationBlocks
        );
        registerFunctionTest(
                tests,
                "iron_combustion_automation_structure",
                MachineRuntimeGameTests::ironCombustionHeaterAcceptsMetalAutomationShell
        );
        registerFunctionTest(
                tests,
                "manual_combustion_restored_structure",
                MachineRuntimeGameTests::manualCombustionCraftsAfterStructureIsRestored
        );
        registerFunctionTest(
                tests,
                "survivalist_fishing_loot",
                SurvivalistFishingGameTests::survivalistRodUsesCustomFishingLoot
        );
        registerFunctionTest(tests, "guide_data_integrity", GuideMenuGameTests::guideDataIntegrity);
        registerFunctionTest(tests, "menu_type_registration", GuideMenuGameTests::menuTypesResolve);
        registerFunctionTest(tests, "jade_machine_casing_object_name", ModGameTests::jadeMachineCasingObjectName);
        return tests;
    }

    private static void registerFunctionTest(
            final List<TestFunction> tests,
            final String name,
            final Consumer<GameTestHelper> function
    ) {
        tests.add(new TestFunction(
                DEFAULT_BATCH,
                name,
                EMPTY_STRUCTURE,
                Rotation.NONE,
                DEFAULT_MAX_TICKS,
                DEFAULT_SETUP_TICKS,
                true,
                function
        ));
    }

    private static void jadeMachineCasingObjectName(final GameTestHelper helper) {
        if (!ModList.get().isLoaded("jade")) {
            helper.succeed();
            return;
        }
        try {
            Class.forName("committee.nova.mods.skyresources3.init.integration.jade.SkyResourcesJadeGameTests")
                    .getMethod("machineCasingUsesDynamicObjectNames", GameTestHelper.class)
                    .invoke(null, helper);
        } catch (final InvocationTargetException exception) {
            final Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            helper.fail("Jade machine casing object name test failed: " + cause);
        } catch (final ReflectiveOperationException exception) {
            helper.fail("Jade machine casing object name test is unavailable: " + exception.getMessage());
        }
    }

    private ModGameTests() {
    }
}
