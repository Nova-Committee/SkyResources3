package committee.nova.mods.skyresources3;

import committee.nova.mods.skyresources3.init.event.CauldronCleanEvents;
import committee.nova.mods.skyresources3.init.event.CuttingKnifeEvents;
import committee.nova.mods.skyresources3.init.event.EarlyHandHarvestEvents;
import committee.nova.mods.skyresources3.init.event.GrassSeedDropEvents;
import committee.nova.mods.skyresources3.init.event.HealthGemEvents;
import committee.nova.mods.skyresources3.init.event.MachineCasingEvents;
import committee.nova.mods.skyresources3.init.event.RockGrinderEvents;
import committee.nova.mods.skyresources3.init.event.SurvivalistFishingEvents;
import committee.nova.mods.skyresources3.core.island.IslandProtectionEvents;
import committee.nova.mods.skyresources3.core.island.PlayerIdentityEvents;
import committee.nova.mods.skyresources3.core.island.VoidIslandCommands;
import committee.nova.mods.skyresources3.core.island.VoidIslandPlayerEvents;
import committee.nova.mods.skyresources3.common.network.ModNetworking;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModCapabilities;
import committee.nova.mods.skyresources3.init.registry.ModCreativeTabs;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import committee.nova.mods.skyresources3.init.registry.ModEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModFluidTypes;
import committee.nova.mods.skyresources3.init.registry.ModFluids;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import committee.nova.mods.skyresources3.init.registry.ModMenuTypes;
import committee.nova.mods.skyresources3.init.registry.ModRecipeTypes;
import committee.nova.mods.skyresources3.test.ModGameTests;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.common.MinecraftForge;
import org.slf4j.Logger;

@Mod(Skyresources3.MODID)
public final class Skyresources3 {
    public static final String MODID = "skyresources";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Skyresources3() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModCapabilities::register);
        modEventBus.addListener(ModDataPackRegistries::register);

        ModFluidTypes.register(modEventBus);
        ModFluids.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntityTypes.register(modEventBus);
        ModItems.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModGameTests.register(modEventBus);

        MinecraftForge.EVENT_BUS.addListener(IslandProtectionEvents::onBlockBreak);
        MinecraftForge.EVENT_BUS.addListener(IslandProtectionEvents::onBlockPlace);
        MinecraftForge.EVENT_BUS.addListener(IslandProtectionEvents::onRightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(PlayerIdentityEvents::onPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(VoidIslandPlayerEvents::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(VoidIslandPlayerEvents::onPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(SurvivalistFishingEvents::onItemFished);
        MinecraftForge.EVENT_BUS.addListener(EarlyHandHarvestEvents::onRightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(MachineCasingEvents::onRightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(CuttingKnifeEvents::onBlockBreak);
        MinecraftForge.EVENT_BUS.addListener(CauldronCleanEvents::onRightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(RockGrinderEvents::onBlockBreak);
        MinecraftForge.EVENT_BUS.addListener(HealthGemEvents::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(GrassSeedDropEvents::onBlockDrops);
        MinecraftForge.EVENT_BUS.addListener(VoidIslandCommands::register);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetworking.register();
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
