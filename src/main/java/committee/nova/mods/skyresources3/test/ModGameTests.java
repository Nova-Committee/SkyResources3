package committee.nova.mods.skyresources3.test;

import committee.nova.mods.skyresources3.Skyresources3;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModGameTests {
    private static final Identifier EMPTY_STRUCTURE = Identifier.withDefaultNamespace("empty");
    private static final Identifier COMMAND_ENVIRONMENT_ID = id("command_environment");
    private static final int DEFAULT_MAX_TICKS = 400;
    private static final int DEFAULT_SETUP_TICKS = 1;

    public static final DeferredRegister<Consumer<GameTestHelper>> TEST_FUNCTIONS =
            DeferredRegister.create(BuiltInRegistries.TEST_FUNCTION, Skyresources3.MODID);

    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ISLAND_CREATE_RESET =
            TEST_FUNCTIONS.register("island_create_reset", () -> IslandCommandGameTests::createInfoAndReset);
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ISLAND_SPAWN_PLATFORM =
            TEST_FUNCTIONS.register(
                    "island_spawn_platform",
                    () -> IslandCommandGameTests::spawnGeneratesConfiguredPlatform
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ISLAND_VISIT =
            TEST_FUNCTIONS.register(
                    "island_visit",
                    () -> IslandCommandGameTests::visitTeleportsToOnlinePlayerIsland
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ISLAND_OFFLINE_VISIT =
            TEST_FUNCTIONS.register(
                    "island_offline_visit",
                    () -> IslandCommandGameTests::visitTeleportsToOfflineSavedIsland
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ISLAND_MAGMA_TEMPLATE =
            TEST_FUNCTIONS.register(
                    "island_magma_template",
                    () -> IslandCommandGameTests::magmaIslandPlacesCrystalFluid
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ISLAND_RELATION_COMMANDS =
            TEST_FUNCTIONS.register(
                    "island_relation_commands",
                    () -> IslandCommandGameTests::islandInviteHomeLeaveAndDisband
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ISLAND_TRUST_COMMANDS =
            TEST_FUNCTIONS.register(
                    "island_trust_commands",
                    () -> IslandCommandGameTests::trustListAndUntrustVisitor
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ISLAND_OFFLINE_IDENTITY =
            TEST_FUNCTIONS.register(
                    "island_offline_identity",
                    () -> IslandCommandGameTests::offlineIdentityInviteAndTrust
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> CUTTING_KNIFE_PROCESS =
            TEST_FUNCTIONS.register(
                    "cutting_knife_process",
                    () -> RuntimeMigrationGameTests::cuttingKnifeUsesProcessRecipe
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ROCK_GRINDER_PROCESS =
            TEST_FUNCTIONS.register(
                    "rock_grinder_process",
                    () -> RuntimeMigrationGameTests::rockGrinderUsesProcessRecipe
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> MAGMAFIED_STONE_TICK =
            TEST_FUNCTIONS.register(
                    "magmafied_stone_tick",
                    () -> RuntimeMigrationGameTests::magmafiedStoneTicksCrystalFluid
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> INFUSION_STONE_PROCESS =
            TEST_FUNCTIONS.register(
                    "infusion_stone_process",
                    () -> LifeInfusionGameTests::infusionStoneUsesProcessRecipe
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> LIFE_INFUSER_PROCESS =
            TEST_FUNCTIONS.register(
                    "life_infuser_process",
                    () -> LifeInfusionGameTests::lifeInfuserUsesProcessRecipe
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> LIFE_INJECTOR_ITEM_CAPABILITY =
            TEST_FUNCTIONS.register(
                    "life_injector_item_capability",
                    () -> LifeInfusionGameTests::lifeInjectorItemCapabilityTransfers
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> LIFE_INFUSER_ITEM_CAPABILITY =
            TEST_FUNCTIONS.register(
                    "life_infuser_item_capability",
                    () -> LifeInfusionGameTests::lifeInfuserItemCapabilityTransfers
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            FUSION_TABLE_CATALYST_YIELD =
            TEST_FUNCTIONS.register(
                    "fusion_table_catalyst_yield",
                    () -> FusionTableGameTests::fusionTableCachesFractionalCatalystYield
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            FUSION_TABLE_SPLIT_DUPLICATE_INPUTS =
            TEST_FUNCTIONS.register(
                    "fusion_table_split_duplicate_inputs",
                    () -> FusionTableGameTests::fusionTableSplitDuplicateStacksDoNotMatchRecipe
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            FUSION_TABLE_MENU_SLOT_PERSISTENCE =
            TEST_FUNCTIONS.register(
                    "fusion_table_menu_slot_persistence",
                    () -> FusionTableGameTests::fusionTableMenuWritesToBlockEntity
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            COMBUSTION_HEATER_EMBEDS_AS_TYPE_ID =
            TEST_FUNCTIONS.register(
                    "combustion_heater_embeds_as_type_id",
                    () -> MachineRuntimeGameTests::combustionHeaterEmbedsAsTypeId
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            COMBUSTION_HEATER_SHIFT_RIGHT_CLICK_REMOVES =
            TEST_FUNCTIONS.register(
                    "combustion_heater_shift_right_click_removes",
                    () -> MachineRuntimeGameTests::shiftRightClickRemovesEmbeddedCombustionHeaterWithHeldItem
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            HEAT_PROVIDER_EMBEDS_AS_TYPE_ID =
            TEST_FUNCTIONS.register(
                    "heat_provider_embeds_as_type_id",
                    () -> MachineRuntimeGameTests::heatProviderEmbedsAsTypeIdAndProvidesHeat
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> CONDENSER_DROPS_OUTPUT =
            TEST_FUNCTIONS.register(
                    "condenser_drops_output",
                    () -> MachineRuntimeGameTests::condenserDropsOutputWhenNoHandlerExists
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> CONDENSER_BLOCKED_OUTPUT =
            TEST_FUNCTIONS.register(
                    "condenser_blocked_output",
                    () -> MachineRuntimeGameTests::condenserKeepsSourceWhenOutputIsBlocked
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COMBUSTION_PRIORITY =
            TEST_FUNCTIONS.register(
                    "combustion_priority",
                    () -> MachineRuntimeGameTests::combustionControllerUsesFilterPriority
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COMBUSTION_COOLDOWN =
            TEST_FUNCTIONS.register(
                    "combustion_cooldown",
                    () -> MachineRuntimeGameTests::combustionControllerWaitsForCooldown
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            COMBUSTION_POWERED_CONTROLLER =
            TEST_FUNCTIONS.register(
                    "combustion_powered_controller",
                    () -> MachineRuntimeGameTests::combustionControllerStopsWhenPowered
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            COMBUSTION_CONTROLLER_DIRECTION =
            TEST_FUNCTIONS.register(
                    "combustion_controller_direction",
                    () -> MachineRuntimeGameTests::combustionControllerRequiresBackFacingChamber
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            COMBUSTION_CONTROLLER_ALL_SIDES_DIRECTION =
            TEST_FUNCTIONS.register(
                    "combustion_controller_all_sides_direction",
                    () -> MachineRuntimeGameTests::combustionControllerBackFacesChamberFromEverySide
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            COMBUSTION_CONTROLLER_GHOST_FILTERS =
            TEST_FUNCTIONS.register(
                    "combustion_controller_ghost_filters",
                    () -> MachineRuntimeGameTests::combustionControllerFilterSlotsAreGhosts
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            COMBUSTION_CONTROLLER_WOOD_STONE_REJECT =
            TEST_FUNCTIONS.register(
                    "combustion_controller_wood_stone_reject",
                    () -> MachineRuntimeGameTests::woodAndStoneCombustionHeatersRejectSmartController
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> COMBUSTION_COLLECTOR_OVERFLOW =
            TEST_FUNCTIONS.register(
                    "combustion_collector_overflow",
                    () -> MachineRuntimeGameTests::combustionCollectorDropsOverflow
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            MANUAL_COMBUSTION_COLLECTOR_OUTPUT =
            TEST_FUNCTIONS.register(
                    "manual_combustion_collector_output",
                    () -> MachineRuntimeGameTests::manualCombustionRoutesOutputsToCollector
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            MANUAL_COMBUSTION_MULTI_INPUT_PRIORITY =
            TEST_FUNCTIONS.register(
                    "manual_combustion_multi_input_priority",
                    () -> MachineRuntimeGameTests::manualCombustionPrefersMultiInputRecipe
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            COMBUSTION_COLLECTOR_WOOD_STONE_REJECT =
            TEST_FUNCTIONS.register(
                    "combustion_collector_wood_stone_reject",
                    () -> MachineRuntimeGameTests::woodAndStoneCombustionHeatersRejectCollector
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            WOODEN_COMBUSTION_STRUCTURE =
            TEST_FUNCTIONS.register(
                    "wooden_combustion_structure",
                    () -> MachineRuntimeGameTests::woodenCombustionHeaterUsesWoodStructure
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            STONE_COMBUSTION_STRUCTURE =
            TEST_FUNCTIONS.register(
                    "stone_combustion_structure",
                    () -> MachineRuntimeGameTests::stoneCombustionHeaterRejectsAutomationBlocks
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            IRON_COMBUSTION_AUTOMATION_STRUCTURE =
            TEST_FUNCTIONS.register(
                    "iron_combustion_automation_structure",
                    () -> MachineRuntimeGameTests::ironCombustionHeaterAcceptsMetalAutomationShell
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>
            MANUAL_COMBUSTION_RESTORED_STRUCTURE =
            TEST_FUNCTIONS.register(
                    "manual_combustion_restored_structure",
                    () -> MachineRuntimeGameTests::manualCombustionCraftsAfterStructureIsRestored
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> SURVIVALIST_FISHING_LOOT =
            TEST_FUNCTIONS.register(
                    "survivalist_fishing_loot",
                    () -> SurvivalistFishingGameTests::survivalistRodUsesCustomFishingLoot
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> GUIDE_DATA_INTEGRITY =
            TEST_FUNCTIONS.register(
                    "guide_data_integrity",
                    () -> GuideMenuGameTests::guideDataIntegrity
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> MENU_TYPE_REGISTRATION =
            TEST_FUNCTIONS.register(
                    "menu_type_registration",
                    () -> GuideMenuGameTests::menuTypesResolve
            );

    public static void register(final IEventBus modEventBus) {
        TEST_FUNCTIONS.register(modEventBus);
        modEventBus.addListener(ModGameTests::registerGameTests);
    }

    private static void registerGameTests(final RegisterGameTestsEvent event) {
        final Holder<TestEnvironmentDefinition> environment = event.registerEnvironment(
                COMMAND_ENVIRONMENT_ID,
                new TestEnvironmentDefinition.AllOf(List.of())
        );
        registerFunctionTest(event, "island_create_reset", ISLAND_CREATE_RESET, environment);
        registerFunctionTest(event, "island_spawn_platform", ISLAND_SPAWN_PLATFORM, environment);
        registerFunctionTest(event, "island_visit", ISLAND_VISIT, environment);
        registerFunctionTest(event, "island_offline_visit", ISLAND_OFFLINE_VISIT, environment);
        registerFunctionTest(event, "island_magma_template", ISLAND_MAGMA_TEMPLATE, environment);
        registerFunctionTest(event, "island_relation_commands", ISLAND_RELATION_COMMANDS, environment);
        registerFunctionTest(event, "island_trust_commands", ISLAND_TRUST_COMMANDS, environment);
        registerFunctionTest(event, "island_offline_identity", ISLAND_OFFLINE_IDENTITY, environment);
        registerFunctionTest(event, "cutting_knife_process", CUTTING_KNIFE_PROCESS, environment);
        registerFunctionTest(event, "rock_grinder_process", ROCK_GRINDER_PROCESS, environment);
        registerFunctionTest(event, "magmafied_stone_tick", MAGMAFIED_STONE_TICK, environment);
        registerFunctionTest(event, "infusion_stone_process", INFUSION_STONE_PROCESS, environment);
        registerFunctionTest(event, "life_infuser_process", LIFE_INFUSER_PROCESS, environment);
        registerFunctionTest(event, "life_injector_item_capability", LIFE_INJECTOR_ITEM_CAPABILITY, environment);
        registerFunctionTest(event, "life_infuser_item_capability", LIFE_INFUSER_ITEM_CAPABILITY, environment);
        registerFunctionTest(event, "fusion_table_catalyst_yield", FUSION_TABLE_CATALYST_YIELD, environment);
        registerFunctionTest(
                event,
                "fusion_table_split_duplicate_inputs",
                FUSION_TABLE_SPLIT_DUPLICATE_INPUTS,
                environment
        );
        registerFunctionTest(
                event,
                "fusion_table_menu_slot_persistence",
                FUSION_TABLE_MENU_SLOT_PERSISTENCE,
                environment
        );
        registerFunctionTest(event, "combustion_heater_embeds_as_type_id", COMBUSTION_HEATER_EMBEDS_AS_TYPE_ID, environment);
        registerFunctionTest(
                event,
                "combustion_heater_shift_right_click_removes",
                COMBUSTION_HEATER_SHIFT_RIGHT_CLICK_REMOVES,
                environment
        );
        registerFunctionTest(event, "heat_provider_embeds_as_type_id", HEAT_PROVIDER_EMBEDS_AS_TYPE_ID, environment);
        registerFunctionTest(event, "condenser_drops_output", CONDENSER_DROPS_OUTPUT, environment);
        registerFunctionTest(event, "condenser_blocked_output", CONDENSER_BLOCKED_OUTPUT, environment);
        registerFunctionTest(event, "combustion_priority", COMBUSTION_PRIORITY, environment);
        registerFunctionTest(event, "combustion_cooldown", COMBUSTION_COOLDOWN, environment);
        registerFunctionTest(event, "combustion_powered_controller", COMBUSTION_POWERED_CONTROLLER, environment);
        registerFunctionTest(event, "combustion_controller_direction", COMBUSTION_CONTROLLER_DIRECTION, environment);
        registerFunctionTest(
                event,
                "combustion_controller_all_sides_direction",
                COMBUSTION_CONTROLLER_ALL_SIDES_DIRECTION,
                environment
        );
        registerFunctionTest(
                event,
                "combustion_controller_ghost_filters",
                COMBUSTION_CONTROLLER_GHOST_FILTERS,
                environment
        );
        registerFunctionTest(
                event,
                "combustion_controller_wood_stone_reject",
                COMBUSTION_CONTROLLER_WOOD_STONE_REJECT,
                environment
        );
        registerFunctionTest(event, "combustion_collector_overflow", COMBUSTION_COLLECTOR_OVERFLOW, environment);
        registerFunctionTest(
                event,
                "manual_combustion_collector_output",
                MANUAL_COMBUSTION_COLLECTOR_OUTPUT,
                environment
        );
        registerFunctionTest(
                event,
                "manual_combustion_multi_input_priority",
                MANUAL_COMBUSTION_MULTI_INPUT_PRIORITY,
                environment
        );
        registerFunctionTest(
                event,
                "combustion_collector_wood_stone_reject",
                COMBUSTION_COLLECTOR_WOOD_STONE_REJECT,
                environment
        );
        registerFunctionTest(event, "wooden_combustion_structure", WOODEN_COMBUSTION_STRUCTURE, environment);
        registerFunctionTest(event, "stone_combustion_structure", STONE_COMBUSTION_STRUCTURE, environment);
        registerFunctionTest(
                event,
                "iron_combustion_automation_structure",
                IRON_COMBUSTION_AUTOMATION_STRUCTURE,
                environment
        );
        registerFunctionTest(
                event,
                "manual_combustion_restored_structure",
                MANUAL_COMBUSTION_RESTORED_STRUCTURE,
                environment
        );
        registerFunctionTest(event, "survivalist_fishing_loot", SURVIVALIST_FISHING_LOOT, environment);
        registerFunctionTest(event, "guide_data_integrity", GUIDE_DATA_INTEGRITY, environment);
        registerFunctionTest(event, "menu_type_registration", MENU_TYPE_REGISTRATION, environment);
    }

    private static void registerFunctionTest(
            final RegisterGameTestsEvent event,
            final String name,
            final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> function,
            final Holder<TestEnvironmentDefinition> environment
    ) {
        final TestData<Holder<TestEnvironmentDefinition>> data = new TestData<>(
                environment,
                EMPTY_STRUCTURE,
                DEFAULT_MAX_TICKS,
                DEFAULT_SETUP_TICKS,
                true
        );
        final GameTestInstance instance = new FunctionGameTestInstance(function.getKey(), data);
        event.registerTest(id(name), instance);
    }

    private static Identifier id(final String path) {
        return Identifier.fromNamespaceAndPath(Skyresources3.MODID, path);
    }

    private ModGameTests() {
    }
}
