package committee.nova.mods.skyresources3;

import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModCreativeTabs;
import committee.nova.mods.skyresources3.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(Skyresources3.MODID)
public final class Skyresources3 {
    public static final String MODID = "skyresources3";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Skyresources3(final IEventBus modEventBus, final ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("SkyResources3 common setup complete");
        if (Config.enableMigrationDebugLogging) {
            LOGGER.debug(
                    "Migration switches: voidIslandFeatures={}, magmaIsland={}",
                    Config.enableVoidIslandFeatures,
                    Config.enableMagmaIsland
            );
        }
    }
}
