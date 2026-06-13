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
