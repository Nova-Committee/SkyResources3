package committee.nova.mods.skyresources3.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import committee.nova.mods.skyresources3.item.CombustionHeaterItem;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record CombustionHeaterTypeItemModelProperty() implements SelectItemModelProperty<Identifier> {
    public static final MapCodec<CombustionHeaterTypeItemModelProperty> MAP_CODEC =
            MapCodec.unit(new CombustionHeaterTypeItemModelProperty());
    public static final SelectItemModelProperty.Type<CombustionHeaterTypeItemModelProperty, Identifier> TYPE =
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
        return CombustionHeaterItem.combustionHeaterTypeId(stack);
    }

    @Override
    public Codec<Identifier> valueCodec() {
        return Identifier.CODEC;
    }

    @Override
    public SelectItemModelProperty.Type<CombustionHeaterTypeItemModelProperty, Identifier> type() {
        return TYPE;
    }
}
