package committee.nova.mods.skyresources3.item;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.recipe.WaterExtractorRecipes;
import committee.nova.mods.skyresources3.registry.ModDataComponents;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

public final class WaterExtractorItem extends Item {
    public static final int DEFAULT_CAPACITY = 4000;
    private static final int USE_DURATION_TICKS = 70_000;
    private static final int MIN_EXTRACTION_USE_TICKS = 25;

    public WaterExtractorItem(final Properties properties) {
        super(properties
                .stacksTo(1)
                .component(ModDataComponents.WATER_EXTRACTOR_FLUID.get(), SimpleFluidContent.EMPTY));
    }

    @Override
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        final Level level = context.getLevel();
        if (level.isClientSide()) {
            return shouldConsumeClientUseOn(context, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        final boolean changed = tryInteractWithFluidHandler(context, player)
                || tryInsertWaterRecipe(context, player)
                || tryPlaceWater(context, player);
        if (!changed) {
            return InteractionResult.PASS;
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public ItemUseAnimation getUseAnimation(final ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public int getUseDuration(final ItemStack stack, final LivingEntity entity) {
        return USE_DURATION_TICKS;
    }

    @Override
    public boolean releaseUsing(
            final ItemStack stack,
            final Level level,
            final LivingEntity entity,
            final int timeLeft
    ) {
        if (!(entity instanceof Player player)) {
            return false;
        }

        if (this.getUseDuration(stack, entity) - timeLeft < MIN_EXTRACTION_USE_TICKS) {
            return false;
        }

        if (level.isClientSide()) {
            return true;
        }

        final boolean changed = tryExtractRecipeWater(stack, level, player) || tryPickupWaterSource(stack, level, player);
        if (changed) {
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return changed;
    }

    @Override
    @Deprecated
    public void appendHoverText(
            final ItemStack stack,
            final TooltipContext context,
            final TooltipDisplay tooltipDisplay,
            final Consumer<Component> tooltipAdder,
            final TooltipFlag tooltipFlag
    ) {
        tooltipAdder.accept(Component.translatable(
                "item.skyresources.water_extractor.water",
                getWaterAmount(stack),
                getCapacity()
        ));
    }

    public static int getWaterAmount(final ItemStack stack) {
        final SimpleFluidContent content = stack.getOrDefault(
                ModDataComponents.WATER_EXTRACTOR_FLUID.get(),
                SimpleFluidContent.EMPTY
        );
        if (!content.is(Fluids.WATER)) {
            return 0;
        }
        return Math.min(content.getAmount(), getCapacity());
    }

    public static float getModelLevel(final ItemStack stack) {
        final int water = getWaterAmount(stack);
        if (water <= 0) {
            return 0.0F;
        }
        return Mth.clamp((int) (water * 6.0F / getCapacity()), 0, 6);
    }

    public static int getCapacity() {
        return Config.waterExtractorCapacity > 0 ? Config.waterExtractorCapacity : DEFAULT_CAPACITY;
    }

    private static boolean tryInteractWithFluidHandler(final UseOnContext context, final Player player) {
        return FluidUtil.interactWithFluidHandler(
                player,
                context.getHand(),
                context.getLevel(),
                context.getClickedPos(),
                context.getClickedFace()
        );
    }

    private static boolean shouldConsumeClientUseOn(final UseOnContext context, final Player player) {
        final ItemStack stack = context.getItemInHand();
        final BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        return WaterExtractorRecipes.findBlockInsertion(state)
                .map(recipe -> canDrainWater(stack, recipe.waterAmount()))
                .orElse(false)
                || (player.isShiftKeyDown() && canDrainWater(stack, FluidType.BUCKET_VOLUME));
    }

    private static boolean tryInsertWaterRecipe(final UseOnContext context, final Player player) {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final ItemStack stack = context.getItemInHand();
        final BlockState state = level.getBlockState(pos);
        return WaterExtractorRecipes.findBlockInsertion(state)
                .map(recipe -> insertWaterAndReplaceBlock(
                        stack,
                        level,
                        player,
                        pos,
                        context,
                        recipe.outputState(),
                        recipe.waterAmount()
                ))
                .orElse(false);
    }

    private static boolean insertWaterAndReplaceBlock(
            final ItemStack stack,
            final Level level,
            final Player player,
            final BlockPos pos,
            final UseOnContext context,
            final BlockState replacement,
            final int waterAmount
    ) {
        if (!canDrainWater(stack, waterAmount)) {
            return false;
        }
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, context.getClickedFace(), stack)) {
            return false;
        }

        drainWater(stack, waterAmount);
        level.setBlock(pos, replacement, Block.UPDATE_ALL);
        playWaterSound(level, player, pos, SoundEvents.BUCKET_EMPTY);
        level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
        return true;
    }

    private static boolean tryPlaceWater(final UseOnContext context, final Player player) {
        if (!player.isShiftKeyDown()) {
            return false;
        }

        final Level level = context.getLevel();
        final ItemStack stack = context.getItemInHand();
        if (!canDrainWater(stack, FluidType.BUCKET_VOLUME)) {
            return false;
        }

        final BlockPos target = getFluidPlacementTarget(context, player);
        if (!level.mayInteract(player, target) || !player.mayUseItemAt(target, context.getClickedFace(), stack)) {
            return false;
        }

        if (FluidUtil.tryPlaceFluid(FluidResource.of(Fluids.WATER), player, level, context.getHand(), target)) {
            drainWater(stack, FluidType.BUCKET_VOLUME);
            return true;
        }
        return false;
    }

    private static BlockPos getFluidPlacementTarget(final UseOnContext context, final Player player) {
        final Level level = context.getLevel();
        final BlockPos clicked = context.getClickedPos();
        final BlockState clickedState = level.getBlockState(clicked);
        if (clickedState.getBlock() instanceof LiquidBlockContainer container
                && container.canPlaceLiquid(player, level, clicked, clickedState, Fluids.WATER)) {
            return clicked;
        }
        return clickedState.canBeReplaced(Fluids.WATER) ? clicked : clicked.relative(context.getClickedFace());
    }

    private static boolean tryExtractRecipeWater(final ItemStack stack, final Level level, final Player player) {
        final BlockHitResult hit = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return false;
        }

        final BlockPos pos = hit.getBlockPos();
        final BlockState state = level.getBlockState(pos);
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, hit.getDirection(), stack)) {
            return false;
        }
        return WaterExtractorRecipes.findBlockExtraction(state)
                .map(recipe -> extractWaterForBlockRecipe(stack, level, player, pos, recipe))
                .orElse(false);
    }

    private static boolean extractWaterForBlockRecipe(
            final ItemStack stack,
            final Level level,
            final Player player,
            final BlockPos pos,
            final WaterExtractorRecipes.BlockExtraction recipe
    ) {
        return recipe.replacementState()
                .map(replacement -> extractWaterAndReplaceBlock(stack, level, player, pos, replacement, recipe.waterAmount()))
                .orElseGet(() -> extractWaterAndClearBlock(stack, level, player, pos, recipe.waterAmount()));
    }

    private static boolean extractWaterAndClearBlock(
            final ItemStack stack,
            final Level level,
            final Player player,
            final BlockPos pos,
            final int waterAmount
    ) {
        if (!canAddWater(stack, waterAmount)) {
            return false;
        }
        addWater(stack, waterAmount);
        level.destroyBlock(pos, false, player);
        playWaterSound(level, player, pos, SoundEvents.PLAYER_SPLASH);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        return true;
    }

    private static boolean extractWaterAndReplaceBlock(
            final ItemStack stack,
            final Level level,
            final Player player,
            final BlockPos pos,
            final BlockState replacement,
            final int waterAmount
    ) {
        if (!canAddWater(stack, waterAmount)) {
            return false;
        }
        addWater(stack, waterAmount);
        level.setBlock(pos, replacement, Block.UPDATE_ALL);
        playWaterSound(level, player, pos, SoundEvents.PLAYER_SPLASH);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        return true;
    }

    private static boolean tryPickupWaterSource(final ItemStack stack, final Level level, final Player player) {
        if (!canAddWater(stack, FluidType.BUCKET_VOLUME)) {
            return false;
        }

        final BlockHitResult hit = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return false;
        }

        final BlockPos pos = hit.getBlockPos();
        if (!level.getFluidState(pos).is(FluidTags.WATER) || !level.getFluidState(pos).isSource()) {
            return false;
        }
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, hit.getDirection(), stack)) {
            return false;
        }

        final BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof BucketPickup bucketPickup) {
            final ItemStack pickedUp = bucketPickup.pickupBlock(player, level, pos, state);
            if (pickedUp.isEmpty()) {
                return false;
            }
            bucketPickup.getPickupSound(state)
                    .ifPresent(sound -> playWaterSound(level, player, pos, sound));
        } else {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            playWaterSound(level, player, pos, SoundEvents.BUCKET_FILL);
        }

        addWater(stack, FluidType.BUCKET_VOLUME);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        return true;
    }

    private static boolean canAddWater(final ItemStack stack, final int amount) {
        return getWaterAmount(stack) + amount <= getCapacity();
    }

    private static boolean canDrainWater(final ItemStack stack, final int amount) {
        return getWaterAmount(stack) >= amount;
    }

    private static void addWater(final ItemStack stack, final int amount) {
        setWaterAmount(stack, getWaterAmount(stack) + amount);
    }

    private static void drainWater(final ItemStack stack, final int amount) {
        setWaterAmount(stack, getWaterAmount(stack) - amount);
    }

    private static void setWaterAmount(final ItemStack stack, final int amount) {
        final int clamped = Mth.clamp(amount, 0, getCapacity());
        final SimpleFluidContent content = clamped == 0
                ? SimpleFluidContent.EMPTY
                : SimpleFluidContent.copyOf(new FluidStack(Fluids.WATER, clamped));
        stack.set(ModDataComponents.WATER_EXTRACTOR_FLUID.get(), content);
    }

    private static void playWaterSound(
            final Level level,
            final Player player,
            final BlockPos pos,
            final net.minecraft.sounds.SoundEvent sound
    ) {
        level.playSound(null, pos, sound, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
