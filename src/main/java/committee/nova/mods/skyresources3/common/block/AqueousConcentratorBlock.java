package committee.nova.mods.skyresources3.common.block;

import committee.nova.mods.skyresources3.core.machine.AqueousMachineMode;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class AqueousConcentratorBlock extends AbstractAqueousMachineBlock {
    public AqueousConcentratorBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected AqueousMachineMode mode() {
        return AqueousMachineMode.CONCENTRATOR;
    }
}
