package committee.nova.mods.skyresources3.test;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class GameTestAssertions {
    private static final Field SPAWN_INVULNERABLE_TIME_FIELD = findSpawnInvulnerableTimeField();

    public static void assertValueEqual(
            final GameTestHelper helper,
            final Object expected,
            final Object actual,
            final String message
    ) {
        helper.assertTrue(
                Objects.equals(expected, actual),
                message + " (expected: " + expected + ", actual: " + actual + ")"
        );
    }

    public static void assertDroppedItemCount(
            final GameTestHelper helper,
            final Item item,
            final int expectedCount,
            final BlockPos relativePos,
            final double radius,
            final String message
    ) {
        final int actualCount = helper.getEntities(EntityType.ITEM, relativePos, radius)
                .stream()
                .map(ItemEntity::getItem)
                .filter(stack -> stack.is(item))
                .mapToInt(ItemStack::getCount)
                .sum();
        assertValueEqual(helper, expectedCount, actualCount, message);
    }

    public static <T extends BlockEntity> T getBlockEntity(
            final GameTestHelper helper,
            final BlockPos relativePos,
            final Class<T> expectedType
    ) {
        final BlockEntity blockEntity = helper.getBlockEntity(relativePos);
        helper.assertTrue(
                expectedType.isInstance(blockEntity),
                "Expected block entity type " + expectedType.getSimpleName()
        );
        return expectedType.cast(blockEntity);
    }

    public static boolean inventoryContains(final Inventory inventory, final Predicate<ItemStack> predicate) {
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (predicate.test(inventory.getItem(slot))) {
                return true;
            }
        }
        return false;
    }

    public static Player makeMockPlayer(final GameTestHelper helper, final GameType gameType) {
        return gameType == GameType.CREATIVE
                ? helper.makeMockPlayer()
                : helper.makeMockSurvivalPlayer();
    }

    public static ServerPlayer makeMockServerPlayer(final GameTestHelper helper, final GameType gameType) {
        return makeNamedMockServerPlayer(helper, UUID.randomUUID(), "test-mock-player", gameType);
    }

    public static ServerPlayer makeNamedMockServerPlayer(
            final GameTestHelper helper,
            final UUID uuid,
            final String name,
            final GameType gameType
    ) {
        final ServerLevel level = helper.getLevel();
        final ServerPlayer player = new TestServerPlayer(level, new GameProfile(uuid, name));
        final Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        level.getServer().getPlayerList().placeNewPlayer(connection, player);
        player.setGameMode(gameType);
        clearSpawnInvulnerability(player);
        return player;
    }

    private static Field findSpawnInvulnerableTimeField() {
        try {
            final Field field = ServerPlayer.class.getDeclaredField("spawnInvulnerableTime");
            field.setAccessible(true);
            return field;
        } catch (final ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to access ServerPlayer spawn invulnerability state", exception);
        }
    }

    private static void clearSpawnInvulnerability(final ServerPlayer player) {
        try {
            SPAWN_INVULNERABLE_TIME_FIELD.setInt(player, 0);
        } catch (final IllegalAccessException exception) {
            throw new IllegalStateException("Unable to clear ServerPlayer spawn invulnerability", exception);
        }
    }

    private static final class TestServerPlayer extends ServerPlayer {
        private TestServerPlayer(final ServerLevel level, final GameProfile profile) {
            super(level.getServer(), level, profile);
        }

        @Override
        public boolean teleportTo(
                final ServerLevel level,
                final double x,
                final double y,
                final double z,
                final Set<RelativeMovement> relativeMovements,
                final float yaw,
                final float pitch
        ) {
            this.setServerLevel(level);
            this.moveTo(x, y, z, yaw, pitch);
            return true;
        }

        @Override
        public void teleportTo(
                final ServerLevel level,
                final double x,
                final double y,
                final double z,
                final float yaw,
                final float pitch
        ) {
            this.setServerLevel(level);
            this.moveTo(x, y, z, yaw, pitch);
        }

        @Override
        public void teleportTo(final double x, final double y, final double z) {
            this.setPos(x, y, z);
        }

        @Override
        public void sendSystemMessage(final Component component) {
        }

        @Override
        public void sendSystemMessage(final Component component, final boolean overlay) {
        }

        @Override
        public void displayClientMessage(final Component component, final boolean actionBar) {
        }
    }

    private GameTestAssertions() {
    }
}
