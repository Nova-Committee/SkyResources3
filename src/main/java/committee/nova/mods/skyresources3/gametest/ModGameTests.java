package committee.nova.mods.skyresources3.gametest;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

public final class ModGameTests {
    public static void register(final IEventBus modEventBus) {
        modEventBus.addListener(ModGameTests::registerGameTests);
    }

    private static void registerGameTests(final RegisterGameTestsEvent event) {
        event.register(WorldFluidPlacementGameTests.class);
    }

    private ModGameTests() {
    }
}
