package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.common.item.WaterExtractorItem;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;

public record WaterExtractorLevelProperty() implements RangeSelectItemModelProperty {
    public static final MapCodec<WaterExtractorLevelProperty> MAP_CODEC =
            MapCodec.unit(new WaterExtractorLevelProperty());

    @Override
    public float get(
            final ItemStack stack,
            @Nullable final ClientLevel level,
            @Nullable final ItemOwner owner,
            final int seed
    ) {
        return WaterExtractorItem.getModelLevel(stack);
    }

    @Override
    public MapCodec<WaterExtractorLevelProperty> type() {
        return MAP_CODEC;
    }
}
