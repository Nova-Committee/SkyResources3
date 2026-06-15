package committee.nova.mods.skyresources3.common.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

public final class HeavySnowballItem extends Item implements ProjectileItem {
    private static final float PROJECTILE_SHOOT_POWER = 1.5F;

    private final OwnerProjectileFactory ownerProjectileFactory;
    private final PositionedProjectileFactory positionedProjectileFactory;

    public HeavySnowballItem(
            final Properties properties,
            final OwnerProjectileFactory ownerProjectileFactory,
            final PositionedProjectileFactory positionedProjectileFactory
    ) {
        super(properties);
        this.ownerProjectileFactory = ownerProjectileFactory;
        this.positionedProjectileFactory = positionedProjectileFactory;
    }

    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack itemStack = player.getItemInHand(hand);
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.SNOWBALL_THROW,
                SoundSource.NEUTRAL,
                0.5F,
                0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        if (level instanceof ServerLevel serverLevel) {
            Projectile.spawnProjectileFromRotation(
                    this.ownerProjectileFactory::create,
                    serverLevel,
                    itemStack,
                    player,
                    0.0F,
                    PROJECTILE_SHOOT_POWER,
                    1.0F
            );
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Projectile asProjectile(
            final Level level,
            final Position position,
            final ItemStack stack,
            final Direction direction
    ) {
        return this.positionedProjectileFactory.create(level, position.x(), position.y(), position.z(), stack);
    }

    @FunctionalInterface
    public interface OwnerProjectileFactory {
        Projectile create(ServerLevel level, LivingEntity owner, ItemStack itemStack);
    }

    @FunctionalInterface
    public interface PositionedProjectileFactory {
        Projectile create(Level level, double x, double y, double z, ItemStack itemStack);
    }
}
