package committee.nova.mods.skyresources3.common.block;

import com.mojang.serialization.MapCodec;
import committee.nova.mods.skyresources3.core.machine.AqueousMachineMode;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class AqueousDeconcentratorBlock extends AbstractAqueousMachineBlock {
    public static final MapCodec<AqueousDeconcentratorBlock> CODEC = simpleCodec(AqueousDeconcentratorBlock::new);

    public AqueousDeconcentratorBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<AqueousDeconcentratorBlock> codec() {
        return CODEC;
    }

    @Override
    protected AqueousMachineMode mode() {
        return AqueousMachineMode.DECONCENTRATOR;
    }
}
