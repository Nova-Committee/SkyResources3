package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.client.render.MachineCasingBlockEntityRenderer;
import committee.nova.mods.skyresources3.client.render.StandaloneMachineBlockEntityRenderer;
import committee.nova.mods.skyresources3.client.screen.*;
import committee.nova.mods.skyresources3.client.utils.MaterialItemTintSources;
import committee.nova.mods.skyresources3.common.entity.HeavyExplosiveSnowball;
import committee.nova.mods.skyresources3.common.entity.HeavySnowball;
import committee.nova.mods.skyresources3.common.network.IslandGuiStatePayload;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import committee.nova.mods.skyresources3.init.registry.ModMenuTypes;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Skyresources3.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SkyResources3Client {
    private static final String KEY_CATEGORY = "key.categories.skyresources";
    private static final KeyMapping OPEN_GUIDE = new KeyMapping(
            "key.skyresources.guide",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_Y,
            KEY_CATEGORY
    );
    private static final KeyMapping OPEN_ISLAND = new KeyMapping(
            "key.skyresources.island",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_I,
            KEY_CATEGORY
    );

    @SubscribeEvent
    public static void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                ModEntityTypes.HEAVY_SNOWBALL.get(),
                context -> new ThrownItemRenderer<HeavySnowball>(context)
        );
        event.registerEntityRenderer(
                ModEntityTypes.HEAVY_EXPLOSIVE_SNOWBALL.get(),
                context -> new ThrownItemRenderer<HeavyExplosiveSnowball>(context)
        );
        event.registerBlockEntityRenderer(
                ModBlockEntityTypes.MACHINE_CASING.get(),
                MachineCasingBlockEntityRenderer::new
        );
        event.registerBlockEntityRenderer(
                ModBlockEntityTypes.STANDALONE_MACHINE.get(),
                StandaloneMachineBlockEntityRenderer::new
        );
    }

    @SubscribeEvent
    public static void registerMenuScreens(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.FUSION_TABLE.get(), FusionTableScreen::new);
            MenuScreens.register(ModMenuTypes.DIRT_FURNACE.get(), DirtFurnaceScreen::new);
            MenuScreens.register(ModMenuTypes.FREEZER.get(), FreezerScreen::new);
            MenuScreens.register(ModMenuTypes.QUICK_DROPPER.get(), QuickDropperScreen::new);
            MenuScreens.register(ModMenuTypes.DARK_MATTER_WARPER.get(), DarkMatterWarperScreen::new);
            MenuScreens.register(ModMenuTypes.END_PORTAL_CORE.get(), EndPortalCoreScreen::new);
            MenuScreens.register(ModMenuTypes.CRUCIBLE_INSERTER.get(), CrucibleInserterScreen::new);
            MenuScreens.register(ModMenuTypes.ROCK_CRUSHER.get(), RockCrusherScreen::new);
            MenuScreens.register(ModMenuTypes.ROCK_CLEANER.get(), RockCleanerScreen::new);
            MenuScreens.register(ModMenuTypes.AQUEOUS_MACHINE.get(), AqueousMachineScreen::new);
            MenuScreens.register(ModMenuTypes.WILDLIFE_ATTRACTOR.get(), WildlifeAttractorScreen::new);
            MenuScreens.register(ModMenuTypes.MACHINE_CASING.get(), MachineCasingScreen::new);
            MenuScreens.register(ModMenuTypes.LIFE_INFUSER.get(), LifeInfuserScreen::new);
            MenuScreens.register(ModMenuTypes.LIFE_INJECTOR.get(), LifeInjectorScreen::new);
            MenuScreens.register(ModMenuTypes.COMBUSTION_COLLECTOR.get(), CombustionCollectorScreen::new);
            MenuScreens.register(ModMenuTypes.COMBUSTION_CONTROLLER.get(), CombustionControllerScreen::new);
        });
    }

    @SubscribeEvent
    public static void registerKeyMappings(final RegisterKeyMappingsEvent event) {
        IslandGuiStatePayload.setClientHandler(IslandGuiScreen::open);
        event.register(OPEN_GUIDE);
        event.register(OPEN_ISLAND);
    }

    private static void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        final Minecraft minecraft = Minecraft.getInstance();
        while (OPEN_GUIDE.consumeClick()) {
            if (minecraft.player != null && minecraft.screen == null) {
                minecraft.setScreen(new GuideScreen());
            }
        }
        while (OPEN_ISLAND.consumeClick()) {
            if (minecraft.player != null && minecraft.screen == null) {
                IslandGuiScreen.requestOpen();
            }
        }
    }

    @SubscribeEvent
    public static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        event.register(
                (stack, tintIndex) -> MaterialItemTintSources.oreAlchemyDustColor(stack),
                ModItems.ORE_ALCHEMICAL_DUST.get()
        );
        event.register(
                (stack, tintIndex) -> MaterialItemTintSources.dirtyGemColor(stack),
                ModItems.DIRTY_GEM.get()
        );
    }

    @Mod.EventBusSubscriber(modid = Skyresources3.MODID, value = Dist.CLIENT)
    private static final class ForgeEvents {
        @SubscribeEvent
        public static void onClientTick(final TickEvent.ClientTickEvent event) {
            SkyResources3Client.onClientTick(event);
        }

        private ForgeEvents() {
        }
    }

    private SkyResources3Client() {
    }
}
