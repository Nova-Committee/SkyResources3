package committee.nova.mods.skyresources3.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

abstract class AbstractHeavySnowball extends ThrowableItemProjectile {
    private static final float BLAZE_DAMAGE_MULTIPLIER = 1.7F;

    protected AbstractHeavySnowball(final EntityType<? extends AbstractHeavySnowball> entityType, final Level level) {
        super(entityType, level);
    }

    protected AbstractHeavySnowball(
            final EntityType<? extends AbstractHeavySnowball> entityType,
            final double x,
            final double y,
            final double z,
            final Level level,
            final ItemStack item
    ) {
        super(entityType, x, y, z, level, item);
    }

    private ParticleOptions particle() {
        final ItemStack itemStack = this.getItem();
        return itemStack.isEmpty()
                ? ParticleTypes.ITEM_SNOWBALL
                : new ItemParticleOption(ParticleTypes.ITEM, itemStack);
    }

    @Override
    public void handleEntityEvent(final byte id) {
        if (id == 3) {
            final ParticleOptions particle = this.particle();

            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onHitEntity(final EntityHitResult result) {
        super.onHitEntity(result);
        final Entity target = result.getEntity();
        final int baseDamage = this.baseDamage();
        final int damage = target instanceof Blaze
                ? Math.round(baseDamage * BLAZE_DAMAGE_MULTIPLIER)
                : baseDamage;

        if (damage > 0) {
            target.hurt(this.damageSources().thrown(this, this.getOwner()), damage);
        }
        this.explodeAtTarget(target);
    }

    @Override
    protected void onHit(final HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    protected abstract int baseDamage();

    protected void explodeAtTarget(final Entity target) {
    }
}
