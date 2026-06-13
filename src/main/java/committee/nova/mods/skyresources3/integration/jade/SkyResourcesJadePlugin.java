package committee.nova.mods.skyresources3.integration.jade;

import committee.nova.mods.skyresources3.Skyresources3;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class SkyResourcesJadePlugin implements IWailaPlugin {
    static final Identifier PROBE_DATA = id("probe_data");

    @Override
    public void register(final IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(SkyResourcesProbeDataProvider.INSTANCE, Block.class);
    }

    @Override
    public void registerClient(final IWailaClientRegistration registration) {
        registration.addConfig(PROBE_DATA, true);
        registration.registerBlockComponent(SkyResourcesProbeComponentProvider.INSTANCE, Block.class);
    }

    private static Identifier id(final String path) {
        return Identifier.fromNamespaceAndPath(Skyresources3.MODID, path);
    }
}
