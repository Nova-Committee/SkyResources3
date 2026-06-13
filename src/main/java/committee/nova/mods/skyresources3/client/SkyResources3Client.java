package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.entity.HeavyExplosiveSnowball;
import committee.nova.mods.skyresources3.entity.HeavySnowball;
import committee.nova.mods.skyresources3.registry.ModEntityTypes;
import committee.nova.mods.skyresources3.registry.ModFluidTypes;
import committee.nova.mods.skyresources3.registry.ModMenuTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = Skyresources3.MODID, value = Dist.CLIENT)
public final class SkyResources3Client {
    private static final Identifier CRYSTAL_FLUID_STILL =
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "block/crystal_fluid_still");
    private static final Identifier CRYSTAL_FLUID_FLOW =
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "block/crystal_fluid_flow");

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
        event.register(ModMenuTypes.MACHINE_CASING.get(), MachineCasingScreen::new);
        event.register(ModMenuTypes.COMBUSTION_COLLECTOR.get(), CombustionCollectorScreen::new);
        event.register(ModMenuTypes.COMBUSTION_CONTROLLER.get(), CombustionControllerScreen::new);
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
