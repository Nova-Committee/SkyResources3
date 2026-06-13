package committee.nova.mods.skyresources3;

import committee.nova.mods.skyresources3.event.CuttingKnifeEvents;
import committee.nova.mods.skyresources3.event.EarlyHandHarvestEvents;
import committee.nova.mods.skyresources3.event.GrassSeedDropEvents;
import committee.nova.mods.skyresources3.event.HealthGemEvents;
import committee.nova.mods.skyresources3.event.RockGrinderEvents;
import committee.nova.mods.skyresources3.event.SurvivalistFishingEvents;
import committee.nova.mods.skyresources3.island.VoidIslandCommands;
import committee.nova.mods.skyresources3.network.ModNetworking;
import committee.nova.mods.skyresources3.registry.ModBlocks;
import committee.nova.mods.skyresources3.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.registry.ModCapabilities;
import committee.nova.mods.skyresources3.registry.ModCreativeTabs;
import committee.nova.mods.skyresources3.registry.ModDataComponents;
import committee.nova.mods.skyresources3.registry.ModEntityTypes;
import committee.nova.mods.skyresources3.registry.ModItems;
import committee.nova.mods.skyresources3.registry.ModMenuTypes;
import committee.nova.mods.skyresources3.registry.ModRecipeTypes;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(Skyresources3.MODID)
public final class Skyresources3 {
    public static final String MODID = "skyresources3";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Skyresources3(final IEventBus modEventBus, final ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModCapabilities::register);
        modEventBus.addListener(ModNetworking::register);

        ModBlocks.register(modEventBus);
        ModBlockEntityTypes.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModItems.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(SurvivalistFishingEvents::onItemFished);
        NeoForge.EVENT_BUS.addListener(EarlyHandHarvestEvents::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(CuttingKnifeEvents::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(RockGrinderEvents::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(HealthGemEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(GrassSeedDropEvents::onBlockDrops);
        NeoForge.EVENT_BUS.addListener(VoidIslandCommands::register);

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
