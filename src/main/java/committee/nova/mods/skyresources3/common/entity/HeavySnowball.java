package committee.nova.mods.skyresources3.common.entity;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.init.registry.ModEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class HeavySnowball extends AbstractHeavySnowball {
    public HeavySnowball(final EntityType<HeavySnowball> entityType, final Level level) {
        super(entityType, level);
    }

    public HeavySnowball(final Level level, final LivingEntity owner, final ItemStack item) {
        super(ModEntityTypes.HEAVY_SNOWBALL.get(), owner.getX(), owner.getEyeY() - 0.1F, owner.getZ(), level, item);
        this.setOwner(owner);
    }

    public HeavySnowball(final Level level, final double x, final double y, final double z, final ItemStack item) {
        super(ModEntityTypes.HEAVY_SNOWBALL.get(), x, y, z, level, item);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.HEAVY_SNOWBALL.get();
    }

    @Override
    protected int baseDamage() {
        return Config.heavySnowballDamage;
    }
}
