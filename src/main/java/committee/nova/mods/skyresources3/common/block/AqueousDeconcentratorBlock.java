package committee.nova.mods.skyresources3.common.block;

import committee.nova.mods.skyresources3.core.machine.AqueousMachineMode;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class AqueousDeconcentratorBlock extends AbstractAqueousMachineBlock {
    public AqueousDeconcentratorBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected AqueousMachineMode mode() {
        return AqueousMachineMode.DECONCENTRATOR;
    }
}
