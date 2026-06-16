package committee.nova.mods.skyresources3.init.integration.jade;

import committee.nova.mods.skyresources3.common.block.entity.MachineCasingBlockEntity;
import committee.nova.mods.skyresources3.common.item.CombustionHeaterItem;
import committee.nova.mods.skyresources3.common.item.CondenserItem;
import committee.nova.mods.skyresources3.common.item.HeatProviderItem;
import committee.nova.mods.skyresources3.core.machine.CasingType;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;

public final class SkyResourcesJadeGameTests {
    private static final BlockPos CASING_POS = new BlockPos(2, 1, 2);

    public static void machineCasingUsesDynamicObjectNames(final GameTestHelper helper) {
        helper.killAllEntities();
        assertBareCasingName(
                helper,
                ModDataPackRegistries.LIGHT_MATTER,
                "block.skyresources.machine_casing.light_matter"
        );
        assertInstalledMachineName(
                helper,
                ModDataPackRegistries.IRON,
                () -> CombustionHeaterItem.forType(ModDataPackRegistries.IRON_COMBUSTION_HEATER),
                "block.skyresources.machine_casing.iron",
                "block.skyresources.combustion_heater.iron"
        );
        assertInstalledMachineName(
                helper,
                ModDataPackRegistries.END_STONE,
                () -> HeatProviderItem.forType(ModDataPackRegistries.END_STONE_HEAT_PROVIDER),
                "block.skyresources.machine_casing.end_stone",
                "block.skyresources.heat_provider.end_stone"
        );
        assertInstalledMachineName(
                helper,
                ModDataPackRegistries.DARK_MATTER,
                () -> CondenserItem.forType(ModDataPackRegistries.DARK_MATTER_CONDENSER),
                "block.skyresources.machine_casing.dark_matter",
                "block.skyresources.condenser.dark_matter"
        );
        helper.succeed();
    }

    private static void assertBareCasingName(
            final GameTestHelper helper,
            final ResourceKey<CasingType> casingType,
            final String expectedCasingKey
    ) {
        final Component objectName = probeObjectName(helper, casingType, null);
        helper.assertValueEqual(expectedCasingKey, translatableKey(helper, objectName), "Jade bare casing object name");
    }

    private static void assertInstalledMachineName(
            final GameTestHelper helper,
            final ResourceKey<CasingType> casingType,
            final Supplier<ItemStack> machineStack,
            final String expectedCasingKey,
            final String expectedMachineKey
    ) {
        final Component objectName = probeObjectName(helper, casingType, machineStack);
        final TranslatableContents contents = translatableContents(helper, objectName);
        helper.assertValueEqual(
                "container.skyresources.machine_casing.with_machine",
                contents.getKey(),
                "Jade installed machine object name"
        );
        final Object[] args = contents.getArgs();
        helper.assertValueEqual(2, args.length, "Jade installed machine object name argument count");
        helper.assertValueEqual(
                expectedCasingKey,
                translatableKey(helper, componentArg(helper, args[0])),
                "Jade installed machine casing name"
        );
        helper.assertValueEqual(
                expectedMachineKey,
                translatableKey(helper, componentArg(helper, args[1])),
                "Jade installed machine type name"
        );
    }

    private static Component probeObjectName(
            final GameTestHelper helper,
            final ResourceKey<CasingType> casingType,
            final @Nullable Supplier<ItemStack> machineStack
    ) {
        helper.setBlock(CASING_POS, Blocks.AIR);
        helper.setBlock(CASING_POS, ModBlocks.MACHINE_CASING.get());
        final MachineCasingBlockEntity casing = machineCasingAt(helper);
        casing.setCasingType(casingType);
        if (machineStack != null) {
            final Player player = helper.makeMockPlayer(GameType.CREATIVE);
            helper.assertTrue(casing.installHeater(machineStack.get(), player), "Machine should install into casing");
        }

        final SkyResourcesProbeData data = SkyResourcesProbeDataProvider.INSTANCE.streamData(
                new TestBlockAccessor(helper, casing)
        );
        helper.assertTrue(data != null, "Jade probe data should exist for typed machine casing");
        final Component objectName = data.objectName();
        helper.assertTrue(objectName != null, "Jade probe data should include a dynamic object name");
        return objectName;
    }

    private static MachineCasingBlockEntity machineCasingAt(final GameTestHelper helper) {
        final BlockEntity blockEntity = helper.getBlockEntity(CASING_POS, MachineCasingBlockEntity.class);
        if (blockEntity instanceof MachineCasingBlockEntity casing) {
            return casing;
        }
        helper.fail("Expected machine casing block entity");
        throw helper.assertionException("Expected machine casing block entity");
    }

    private static Component componentArg(final GameTestHelper helper, final Object value) {
        if (value instanceof Component component) {
            return component;
        }
        helper.fail("Expected translatable component argument");
        throw helper.assertionException("Expected translatable component argument");
    }

    private static String translatableKey(final GameTestHelper helper, final Component component) {
        return translatableContents(helper, component).getKey();
    }

    private static TranslatableContents translatableContents(final GameTestHelper helper, final Component component) {
        if (component.getContents() instanceof TranslatableContents contents) {
            return contents;
        }
        helper.fail("Expected translatable component");
        throw helper.assertionException("Expected translatable component");
    }

    private record TestBlockAccessor(GameTestHelper helper, MachineCasingBlockEntity casing) implements BlockAccessor {
        @Override
        public Block getBlock() {
            return this.getBlockState().getBlock();
        }

        @Override
        public BlockState getBlockState() {
            return this.casing.getBlockState();
        }

        @Override
        public BlockEntity getBlockEntity() {
            return this.casing;
        }

        @Override
        public BlockPos getPosition() {
            return this.casing.getBlockPos();
        }

        @Override
        public Direction getSide() {
            return Direction.UP;
        }

        @Override
        public ServerLevel getLevel() {
            return this.helper.getLevel();
        }

        @Override
        public Player getPlayer() {
            return this.helper.makeMockPlayer(GameType.CREATIVE);
        }

        @Override
        public CompoundTag getServerData() {
            return new CompoundTag();
        }

        @Override
        public void setServerData(final CompoundTag serverData) {
        }

        @Override
        public <D> Optional<D> decodeFromNbt(
                final StreamDecoder<RegistryFriendlyByteBuf, D> decoder,
                final Tag tag
        ) {
            return Optional.empty();
        }

        @Override
        public <D> Tag encodeAsNbt(
                final StreamEncoder<RegistryFriendlyByteBuf, D> encoder,
                final D value
        ) {
            return new CompoundTag();
        }

        @Override
        public BlockHitResult getHitResult() {
            return new BlockHitResult(
                    Vec3.atCenterOf(this.getPosition()),
                    Direction.UP,
                    this.getPosition(),
                    false
            );
        }

        @Override
        public boolean isServerConnected() {
            return true;
        }

        @Override
        public ItemStack getPickedResult() {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean showDetails() {
            return false;
        }

        @Override
        public Object getTarget() {
            return this.casing;
        }

        @Override
        public Class<? extends Accessor<?>> getAccessorType() {
            return BlockAccessor.class;
        }

        @Override
        public boolean verifyData(final CompoundTag serverData) {
            return true;
        }

        @Override
        public boolean shouldVerifyData() {
            return false;
        }

        @Override
        public float tickRate() {
            return 0.0F;
        }
    }

    private SkyResourcesJadeGameTests() {
    }
}
