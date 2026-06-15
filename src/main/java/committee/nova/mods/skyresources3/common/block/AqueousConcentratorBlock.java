package committee.nova.mods.skyresources3.common.block;

import com.mojang.serialization.MapCodec;
import committee.nova.mods.skyresources3.core.machine.AqueousMachineMode;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class AqueousConcentratorBlock extends AbstractAqueousMachineBlock {
    public static final MapCodec<AqueousConcentratorBlock> CODEC = simpleCodec(AqueousConcentratorBlock::new);

    public AqueousConcentratorBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<AqueousConcentratorBlock> codec() {
        return CODEC;
    }

    @Override
    protected AqueousMachineMode mode() {
        return AqueousMachineMode.CONCENTRATOR;
    }
}
