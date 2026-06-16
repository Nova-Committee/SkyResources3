package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.client.model.*;
import committee.nova.mods.skyresources3.client.render.MachineCasingBlockEntityRenderer;
import committee.nova.mods.skyresources3.client.render.StandaloneMachineBlockEntityRenderer;
import committee.nova.mods.skyresources3.client.screen.*;
import committee.nova.mods.skyresources3.client.utils.MaterialItemTintSources;
import committee.nova.mods.skyresources3.common.entity.HeavyExplosiveSnowball;
import committee.nova.mods.skyresources3.common.entity.HeavySnowball;
import committee.nova.mods.skyresources3.common.network.IslandGuiStatePayload;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModFluidTypes;
import committee.nova.mods.skyresources3.init.registry.ModMenuTypes;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = Skyresources3.MODID, value = Dist.CLIENT)
public final class SkyResources3Client {
    private static final Identifier CRYSTAL_FLUID_STILL =
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "block/crystal_fluid_still");
    private static final Identifier CRYSTAL_FLUID_FLOW =
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "block/crystal_fluid_flow");
    private static final KeyMapping.Category KEY_CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(Skyresources3.MODID, "guide"));
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
    public static void registerMenuScreens(final RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.FUSION_TABLE.get(), FusionTableScreen::new);
        event.register(ModMenuTypes.DIRT_FURNACE.get(), DirtFurnaceScreen::new);
        event.register(ModMenuTypes.FREEZER.get(), FreezerScreen::new);
        event.register(ModMenuTypes.QUICK_DROPPER.get(), QuickDropperScreen::new);
        event.register(ModMenuTypes.DARK_MATTER_WARPER.get(), DarkMatterWarperScreen::new);
        event.register(ModMenuTypes.END_PORTAL_CORE.get(), EndPortalCoreScreen::new);
        event.register(ModMenuTypes.CRUCIBLE_INSERTER.get(), CrucibleInserterScreen::new);
        event.register(ModMenuTypes.ROCK_CRUSHER.get(), RockCrusherScreen::new);
        event.register(ModMenuTypes.ROCK_CLEANER.get(), RockCleanerScreen::new);
        event.register(ModMenuTypes.AQUEOUS_MACHINE.get(), AqueousMachineScreen::new);
        event.register(ModMenuTypes.WILDLIFE_ATTRACTOR.get(), WildlifeAttractorScreen::new);
        event.register(ModMenuTypes.MACHINE_CASING.get(), MachineCasingScreen::new);
        event.register(ModMenuTypes.LIFE_INFUSER.get(), LifeInfuserScreen::new);
        event.register(ModMenuTypes.LIFE_INJECTOR.get(), LifeInjectorScreen::new);
        event.register(ModMenuTypes.COMBUSTION_COLLECTOR.get(), CombustionCollectorScreen::new);
        event.register(ModMenuTypes.COMBUSTION_CONTROLLER.get(), CombustionControllerScreen::new);
    }

    @SubscribeEvent
    public static void registerKeyMappings(final RegisterKeyMappingsEvent event) {
        IslandGuiStatePayload.setClientHandler(IslandGuiScreen::open);
        event.registerCategory(KEY_CATEGORY);
        event.register(OPEN_GUIDE);
        event.register(OPEN_ISLAND);
    }

    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Post event) {
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
    public static void registerRangeSelectItemModelProperties(
            final RegisterRangeSelectItemModelPropertyEvent event
    ) {
        event.register(
                Identifier.fromNamespaceAndPath(Skyresources3.MODID, "water_extractor_level"),
                WaterExtractorLevelProperty.MAP_CODEC
        );
    }

    @SubscribeEvent
    public static void registerSelectItemModelProperties(final RegisterSelectItemModelPropertyEvent event) {
        event.register(
                Identifier.fromNamespaceAndPath(Skyresources3.MODID, "casing_type"),
                CasingTypeItemModelProperty.TYPE
        );
        event.register(
                Identifier.fromNamespaceAndPath(Skyresources3.MODID, "combustion_heater_type"),
                CombustionHeaterTypeItemModelProperty.TYPE
        );
        event.register(
                Identifier.fromNamespaceAndPath(Skyresources3.MODID, "heat_provider_type"),
                HeatProviderTypeItemModelProperty.TYPE
        );
        event.register(
                Identifier.fromNamespaceAndPath(Skyresources3.MODID, "condenser_type"),
                CondenserTypeItemModelProperty.TYPE
        );
    }

    @SubscribeEvent
    public static void registerItemTintSources(final RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(
                MaterialItemTintSources.ORE_ALCHEMY_DUST,
                MaterialItemTintSources.OreAlchemyDustColor.MAP_CODEC
        );
        event.register(
                MaterialItemTintSources.DIRTY_GEM,
                MaterialItemTintSources.DirtyGemColor.MAP_CODEC
        );
    }

    @SubscribeEvent
    public static void registerClientExtensions(final RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public Identifier getStillTexture() {
                return CRYSTAL_FLUID_STILL;
            }

            @Override
            public Identifier getFlowingTexture() {
                return CRYSTAL_FLUID_FLOW;
            }
        }, ModFluidTypes.CRYSTAL_FLUID);
    }

    private SkyResources3Client() {
    }
}
