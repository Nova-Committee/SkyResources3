package committee.nova.mods.skyresources3.entity;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.registry.ModEntityTypes;
import committee.nova.mods.skyresources3.registry.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class HeavyExplosiveSnowball extends AbstractHeavySnowball {
    private static final float EXPLOSION_RADIUS = 0.01F;

    public HeavyExplosiveSnowball(final EntityType<? extends HeavyExplosiveSnowball> entityType, final Level level) {
        super(entityType, level);
    }

    public HeavyExplosiveSnowball(final Level level, final LivingEntity owner, final ItemStack item) {
        super(ModEntityTypes.HEAVY_EXPLOSIVE_SNOWBALL.get(), owner.getX(), owner.getEyeY() - 0.1F, owner.getZ(), level, item);
        this.setOwner(owner);
    }

    public HeavyExplosiveSnowball(final Level level, final double x, final double y, final double z, final ItemStack item) {
        super(ModEntityTypes.HEAVY_EXPLOSIVE_SNOWBALL.get(), x, y, z, level, item);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.HEAVY_EXPLOSIVE_SNOWBALL.get();
    }

    @Override
    protected int baseDamage() {
        return Config.explosiveHeavySnowballDamage;
    }

    @Override
    protected void explodeAtTarget(final Entity target) {
        if (!this.level().isClientSide()) {
            this.level().explode(
                    this,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    EXPLOSION_RADIUS,
                    false,
                    Level.ExplosionInteraction.NONE
            );
        }
    }
}
