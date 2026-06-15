package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.common.item.MachineCasingItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record CasingTypeItemModelProperty() implements SelectItemModelProperty<Identifier> {
    public static final MapCodec<CasingTypeItemModelProperty> MAP_CODEC =
            MapCodec.unit(new CasingTypeItemModelProperty());
    public static final SelectItemModelProperty.Type<CasingTypeItemModelProperty, Identifier> TYPE =
            SelectItemModelProperty.Type.create(MAP_CODEC, Identifier.CODEC);

    @Nullable
    @Override
    public Identifier get(
            final ItemStack stack,
            @Nullable final ClientLevel level,
            @Nullable final LivingEntity entity,
            final int seed,
            final ItemDisplayContext displayContext
    ) {
        return MachineCasingItem.casingTypeId(stack);
    }

    @Override
    public Codec<Identifier> valueCodec() {
        return Identifier.CODEC;
    }

    @Override
    public SelectItemModelProperty.Type<CasingTypeItemModelProperty, Identifier> type() {
        return TYPE;
    }
}
