package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.entity.HeavyExplosiveSnowball;
import committee.nova.mods.skyresources3.entity.HeavySnowball;
import committee.nova.mods.skyresources3.registry.ModEntityTypes;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

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

    private SkyResources3Client() {
    }
}
