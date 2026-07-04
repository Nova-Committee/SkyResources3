package committee.nova.mods.skyresources3.init.integration.jade;

import committee.nova.mods.skyresources3.Skyresources3;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class SkyResourcesJadePlugin implements IWailaPlugin {
    static final ResourceLocation PROBE_DATA = id("probe_data");

    @Override
    public void register(final IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(SkyResourcesProbeDataProvider.INSTANCE, BlockEntity.class);
    }

    @Override
    public void registerClient(final IWailaClientRegistration registration) {
        registration.registerBlockComponent(SkyResourcesProbeComponentProvider.INSTANCE, Block.class);
    }

    private static ResourceLocation id(final String path) {
        return new ResourceLocation(Skyresources3.MODID, path);
    }
}
