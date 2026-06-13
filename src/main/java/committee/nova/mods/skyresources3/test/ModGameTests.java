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
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ISLAND_TEAM_COMMANDS =
            TEST_FUNCTIONS.register(
                    "island_team_commands",
                    () -> IslandCommandGameTests::teamInviteHomeLeaveAndDisband
            );
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> ISLAND_TRUST_COMMANDS =
            TEST_FUNCTIONS.register(
                    "island_trust_commands",
                    () -> IslandCommandGameTests::trustListAndUntrustVisitor
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
    private static final DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>> SURVIVALIST_FISHING_LOOT =
            TEST_FUNCTIONS.register(
                    "survivalist_fishing_loot",
                    () -> SurvivalistFishingGameTests::survivalistRodUsesCustomFishingLoot
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
        registerFunctionTest(event, "island_team_commands", ISLAND_TEAM_COMMANDS, environment);
        registerFunctionTest(event, "island_trust_commands", ISLAND_TRUST_COMMANDS, environment);
        registerFunctionTest(event, "cutting_knife_process", CUTTING_KNIFE_PROCESS, environment);
        registerFunctionTest(event, "rock_grinder_process", ROCK_GRINDER_PROCESS, environment);
        registerFunctionTest(event, "magmafied_stone_tick", MAGMAFIED_STONE_TICK, environment);
        registerFunctionTest(event, "infusion_stone_process", INFUSION_STONE_PROCESS, environment);
        registerFunctionTest(event, "life_infuser_process", LIFE_INFUSER_PROCESS, environment);
        registerFunctionTest(event, "life_injector_item_capability", LIFE_INJECTOR_ITEM_CAPABILITY, environment);
        registerFunctionTest(event, "life_infuser_item_capability", LIFE_INFUSER_ITEM_CAPABILITY, environment);
        registerFunctionTest(event, "condenser_drops_output", CONDENSER_DROPS_OUTPUT, environment);
        registerFunctionTest(event, "condenser_blocked_output", CONDENSER_BLOCKED_OUTPUT, environment);
        registerFunctionTest(event, "survivalist_fishing_loot", SURVIVALIST_FISHING_LOOT, environment);
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
