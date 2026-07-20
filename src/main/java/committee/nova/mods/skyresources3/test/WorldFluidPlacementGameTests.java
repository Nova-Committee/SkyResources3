package committee.nova.mods.skyresources3.test;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.common.block.entity.FluidDropperBlockEntity;
import committee.nova.mods.skyresources3.common.item.WaterExtractorItem;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public final class WorldFluidPlacementGameTests {
    private static final BlockPos CLICKED_POS = new BlockPos(2, 1, 2);
    private static final BlockPos WATER_TARGET_POS = CLICKED_POS.above();
    private static final BlockPos DROPPER_POS = new BlockPos(5, 2, 2);
    private static final BlockPos DROPPER_TARGET_POS = DROPPER_POS.below();
    private static final int INITIAL_FLUID_AMOUNT = FluidType.BUCKET_VOLUME * 2;

    public static void waterExtractorPlacesSourceAndConsumesOneBucket(final GameTestHelper helper) {
        helper.setBlock(CLICKED_POS, Blocks.OBSIDIAN);
        helper.setBlock(WATER_TARGET_POS, Blocks.AIR);
        final Player player = waterExtractorPlayer(helper);

        final InteractionResult result = useWaterExtractor(helper, player);

        helper.assertTrue(result.consumesAction(), "Water Extractor should place stored water");
        assertWaterSource(helper, WATER_TARGET_POS);
        GameTestAssertions.assertValueEqual(
                helper,
                FluidType.BUCKET_VOLUME,
                WaterExtractorItem.getWaterAmount(player.getMainHandItem()),
                "Water Extractor should consume exactly one bucket"
        );
        helper.succeed();
    }

    public static void waterExtractorBlockedTargetKeepsWater(final GameTestHelper helper) {
        helper.setBlock(CLICKED_POS, Blocks.OBSIDIAN);
        helper.setBlock(WATER_TARGET_POS, Blocks.OBSIDIAN);
        final Player player = waterExtractorPlayer(helper);

        final InteractionResult result = useWaterExtractor(helper, player);

        helper.assertTrue(!result.consumesAction(), "Water Extractor should reject a blocked target");
        helper.assertTrue(helper.getBlockState(WATER_TARGET_POS).is(Blocks.OBSIDIAN), "Blocked target should not change");
        GameTestAssertions.assertValueEqual(
                helper,
                INITIAL_FLUID_AMOUNT,
                WaterExtractorItem.getWaterAmount(player.getMainHandItem()),
                "Blocked Water Extractor placement should not consume water"
        );
        helper.succeed();
    }

    public static void fluidDropperPlacesSourceAndConsumesOneBucket(final GameTestHelper helper) {
        withDropperCapacity(INITIAL_FLUID_AMOUNT, () -> {
            helper.setBlock(DROPPER_TARGET_POS, Blocks.AIR);
            final FluidDropperBlockEntity dropper = filledDropper(helper);

            dropper.serverTick(helper.getLevel());

            assertWaterSource(helper, DROPPER_TARGET_POS);
            GameTestAssertions.assertValueEqual(
                    helper,
                    FluidType.BUCKET_VOLUME,
                    dropper.getFluidHandler().getFluidInTank(0).getAmount(),
                    "Fluid Dropper should consume exactly one bucket"
            );
        });
        helper.succeed();
    }

    public static void fluidDropperBlockedTargetKeepsFluid(final GameTestHelper helper) {
        withDropperCapacity(INITIAL_FLUID_AMOUNT, () -> {
            helper.setBlock(DROPPER_TARGET_POS, Blocks.OBSIDIAN);
            final FluidDropperBlockEntity dropper = filledDropper(helper);

            dropper.serverTick(helper.getLevel());

            helper.assertTrue(
                    helper.getBlockState(DROPPER_TARGET_POS).is(Blocks.OBSIDIAN),
                    "Blocked target should not change"
            );
            GameTestAssertions.assertValueEqual(
                    helper,
                    INITIAL_FLUID_AMOUNT,
                    dropper.getFluidHandler().getFluidInTank(0).getAmount(),
                    "Blocked Fluid Dropper placement should not consume fluid"
            );
        });
        helper.succeed();
    }

    private static Player waterExtractorPlayer(final GameTestHelper helper) {
        final Player player = GameTestAssertions.makeMockPlayer(helper, GameType.SURVIVAL);
        player.setShiftKeyDown(true);
        player.setPos(helper.absoluteVec(Vec3.atCenterOf(CLICKED_POS.north())));

        final ItemStack stack = new ItemStack(ModItems.WATER_EXTRACTOR.get());
        final IFluidHandler handler = new FluidHandlerItemStack(stack, WaterExtractorItem.getCapacity());
        GameTestAssertions.assertValueEqual(
                helper,
                INITIAL_FLUID_AMOUNT,
                handler.fill(new FluidStack(Fluids.WATER, INITIAL_FLUID_AMOUNT), IFluidHandler.FluidAction.EXECUTE),
                "Water Extractor should accept the test water"
        );
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        return player;
    }

    private static InteractionResult useWaterExtractor(final GameTestHelper helper, final Player player) {
        final BlockPos absolutePos = helper.absolutePos(CLICKED_POS);
        return player.getMainHandItem().useOn(new UseOnContext(
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false)
        ));
    }

    private static FluidDropperBlockEntity filledDropper(final GameTestHelper helper) {
        helper.setBlock(DROPPER_POS, Blocks.AIR);
        helper.setBlock(DROPPER_POS, ModBlocks.FLUID_DROPPER.get());
        final FluidDropperBlockEntity dropper = GameTestAssertions.getBlockEntity(
                helper,
                DROPPER_POS,
                FluidDropperBlockEntity.class
        );
        GameTestAssertions.assertValueEqual(
                helper,
                INITIAL_FLUID_AMOUNT,
                dropper.getFluidHandler().fill(
                        new FluidStack(Fluids.WATER, INITIAL_FLUID_AMOUNT),
                        IFluidHandler.FluidAction.EXECUTE
                ),
                "Fluid Dropper should accept the test water"
        );
        return dropper;
    }

    private static void assertWaterSource(final GameTestHelper helper, final BlockPos relativePos) {
        final var fluidState = helper.getLevel().getFluidState(helper.absolutePos(relativePos));
        helper.assertTrue(fluidState.is(Fluids.WATER), "Target should contain water");
        helper.assertTrue(fluidState.isSource(), "Target water should be a source block");
    }

    private static void withDropperCapacity(final int capacity, final Runnable action) {
        final int previousCapacity = Config.fluidDropperCapacity;
        Config.fluidDropperCapacity = capacity;
        try {
            action.run();
        } finally {
            Config.fluidDropperCapacity = previousCapacity;
        }
    }

    private WorldFluidPlacementGameTests() {
    }
}
