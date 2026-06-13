package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.entity.HeavyExplosiveSnowball;
import committee.nova.mods.skyresources3.entity.HeavySnowball;
import committee.nova.mods.skyresources3.registry.ModEntityTypes;
import committee.nova.mods.skyresources3.registry.ModMenuTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;

@EventBusSubscriber(modid = Skyresources3.MODID, value = Dist.CLIENT)
public final class SkyResources3Client {
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

    private SkyResources3Client() {
    }
}
