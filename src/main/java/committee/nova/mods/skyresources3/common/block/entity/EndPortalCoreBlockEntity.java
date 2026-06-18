package committee.nova.mods.skyresources3.common.block.entity;

import committee.nova.mods.skyresources3.Config;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.EndPlatformFeature;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import committee.nova.mods.skyresources3.common.compat.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import committee.nova.mods.skyresources3.common.compat.transfer.ResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemResource;
import committee.nova.mods.skyresources3.common.compat.transfer.item.ItemStacksResourceHandler;
import committee.nova.mods.skyresources3.common.compat.transfer.transaction.TransactionContext;

public final class EndPortalCoreBlockEntity extends BlockEntity {
    public static final int SLOT_COUNT = 1;
    public static final int EYE_SLOT = 0;
    public static final int EYES_PER_TELEPORT = 16;
    private static final String ITEMS_KEY = "items";
    private static final String POWERED_KEY = "powered";
    private static final int SILVERFISH_RANGE = 4;
    private static final int SILVERFISH_VERTICAL_RANGE = 5;
    private static final int SILVERFISH_INTERVAL = 800;
    private static final int AMBIENT_SOUND_INTERVAL = 60;
    private static final int MAX_SILVERFISH = 16;
    private static final Set<RelativeMovement> NO_RELATIVE_MOVEMENT = Set.of();

    private final EndPortalCoreItemHandler items = new EndPortalCoreItemHandler(this);
    private boolean powered;

    public EndPortalCoreBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.END_PORTAL_CORE.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(final net.minecraft.nbt.CompoundTag tag, final net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        final ValueInput input = new ValueInput(tag, registries);
        input.readChild(ITEMS_KEY, this.items);
        this.powered = input.getBooleanOr(POWERED_KEY, false);
    }

    @Override
    protected void saveAdditional(final net.minecraft.nbt.CompoundTag tag, final net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        final ValueOutput output = new ValueOutput(tag, registries);
        output.putChild(ITEMS_KEY, this.items);
        output.putBoolean(POWERED_KEY, this.powered);
    }

    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        this.dropContents();
    }

    public void serverTick(final ServerLevel level) {
        final boolean pulse = this.updatePowered(level);
        if (!this.hasValidMultiblock()) {
            return;
        }

        if (level.getGameTime() % AMBIENT_SOUND_INTERVAL == 0) {
            level.playSound(
                    null,
                    this.worldPosition,
                    SoundEvents.PORTAL_AMBIENT,
                    SoundSource.BLOCKS,
                    0.8F,
                    1.0F / (level.random.nextFloat() * 0.4F + 1.2F)
            );
        }

        this.spawnSilverfish(level);
        if (pulse) {
            this.teleportPlayers(level);
        }
    }

    public ResourceHandler<ItemResource> getItemHandler() {
        return this.items;
    }

    public ItemStack getStackInSlot(final int slot) {
        return slot == EYE_SLOT ? this.items.stack(EYE_SLOT) : ItemStack.EMPTY;
    }

    public void setStackInSlot(final int slot, final ItemStack stack) {
        if (slot == EYE_SLOT) {
            this.items.setStack(EYE_SLOT, stack);
        }
    }

    public ItemStack removeStack(final int slot, final int amount) {
        if (slot != EYE_SLOT || amount <= 0) {
            return ItemStack.EMPTY;
        }
        final ItemStack stack = this.items.stack(EYE_SLOT);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = stack.split(amount);
        if (stack.isEmpty()) {
            this.items.setStack(EYE_SLOT, ItemStack.EMPTY);
        }
        this.setChanged();
        return removed;
    }

    public ItemStack removeStackNoUpdate(final int slot) {
        if (slot != EYE_SLOT) {
            return ItemStack.EMPTY;
        }
        final ItemStack removed = this.items.stack(EYE_SLOT);
        this.items.setStack(EYE_SLOT, ItemStack.EMPTY);
        return removed;
    }

    public boolean mayPlaceInSlot(final int slot, final ItemStack stack) {
        return slot == EYE_SLOT && isFuel(stack);
    }

    public void dropContents() {
        if (this.level == null) {
            return;
        }
        Containers.dropContents(this.level, this.worldPosition, new SimpleContainer(this.items.stack(EYE_SLOT).copy()));
        this.items.setStack(EYE_SLOT, ItemStack.EMPTY);
    }

    public boolean hasValidMultiblock() {
        if (this.level == null) {
            return false;
        }

        for (final Direction direction : Direction.Plane.HORIZONTAL) {
            if (!this.hasBlock(this.worldPosition.relative(direction), Blocks.GOLD_BLOCK)) {
                return false;
            }
        }

        for (final BlockPos offset : cornerOffsets(1)) {
            if (!this.hasBlock(this.worldPosition.offset(offset), Blocks.DIAMOND_BLOCK)) {
                return false;
            }
        }

        for (final BlockPos offset : cornerOffsets(2)) {
            final BlockPos corner = this.worldPosition.offset(offset);
            if (!this.hasBlock(corner.above(), Blocks.END_STONE_BRICKS)
                    || !this.hasBlock(corner.above(2), Blocks.END_STONE_BRICKS)
                    || !this.hasBlock(corner.above(3), Blocks.GLOWSTONE)) {
                return false;
            }
        }

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if ((Math.abs(x) > 1 || Math.abs(z) > 1)
                        && !this.hasBlock(this.worldPosition.offset(x, 0, z), ModBlocks.DARK_MATTER_BLOCK.get())) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean hasValidMultiblockTier2() {
        if (!this.hasValidMultiblock()) {
            return false;
        }

        for (final BlockPos offset : cornerOffsets(3)) {
            final BlockPos corner = this.worldPosition.offset(offset);
            for (int y = 1; y <= 4; y++) {
                final BlockState state = this.level.getBlockState(corner.above(y));
                if (!state.is(Blocks.PURPUR_PILLAR)
                        || state.getValue(RotatedPillarBlock.AXIS) != Direction.Axis.Y) {
                    return false;
                }
            }
            if (!this.hasBlock(corner.above(5), Blocks.END_ROD)) {
                return false;
            }
        }

        for (final BlockPos offset : cornerOffsets(2)) {
            if (!this.hasBlock(this.worldPosition.offset(offset).above(4), ModBlocks.SILVERFISH_DISRUPTOR.get())) {
                return false;
            }
        }

        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if ((Math.abs(x) > 2 || Math.abs(z) > 2)
                        && !this.hasBlock(this.worldPosition.offset(x, 0, z), ModBlocks.LIGHT_MATTER_BLOCK.get())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isFuel(final ItemStack stack) {
        return !stack.isEmpty() && stack.is(Items.ENDER_EYE);
    }

    private boolean updatePowered(final ServerLevel level) {
        final boolean nowPowered = level.hasNeighborSignal(this.worldPosition);
        final boolean pulse = nowPowered && !this.powered;
        if (nowPowered != this.powered) {
            this.powered = nowPowered;
            this.setChanged();
        }
        return pulse;
    }

    private void teleportPlayers(final ServerLevel level) {
        final ServerLevel endLevel = level.getServer().getLevel(Level.END);
        if (endLevel == null) {
            return;
        }

        final boolean tier2 = this.hasValidMultiblockTier2();
        for (final ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, new AABB(this.worldPosition.above()))) {
            if (!tier2 && this.items.stack(EYE_SLOT).getCount() < EYES_PER_TELEPORT) {
                return;
            }
            EndPlatformFeature.createEndPlatform(endLevel, ServerLevel.END_SPAWN_POINT.below(), true);
            final Vec3 target = ServerLevel.END_SPAWN_POINT.getBottomCenter().subtract(0.0D, 1.0D, 0.0D);
            player.teleportTo(
                    endLevel,
                    target.x(),
                    target.y(),
                    target.z(),
                    NO_RELATIVE_MOVEMENT,
                    Direction.WEST.toYRot(),
                    0.0F
            );
            if (!tier2) {
                this.items.stack(EYE_SLOT).shrink(EYES_PER_TELEPORT);
                if (this.items.stack(EYE_SLOT).isEmpty()) {
                    this.items.setStack(EYE_SLOT, ItemStack.EMPTY);
                }
                this.setChanged();
            }
        }
    }

    private void spawnSilverfish(final ServerLevel level) {
        if (Config.endPortalMode == Config.EndPortalDifficulty.WUSS
                || level.getGameTime() % SILVERFISH_INTERVAL != 0
                || level.random.nextFloat() > 0.9F
                || this.hasValidMultiblockTier2()) {
            return;
        }

        final AABB bounds = new AABB(
                this.worldPosition.getX() - SILVERFISH_RANGE,
                this.worldPosition.getY(),
                this.worldPosition.getZ() - SILVERFISH_RANGE,
                this.worldPosition.getX() + SILVERFISH_RANGE,
                this.worldPosition.getY() + SILVERFISH_VERTICAL_RANGE,
                this.worldPosition.getZ() + SILVERFISH_RANGE
        );
        if (level.getEntitiesOfClass(Silverfish.class, bounds).size() >= MAX_SILVERFISH) {
            return;
        }

        final Silverfish silverfish = EntityType.SILVERFISH.create(level);
        if (silverfish == null) {
            return;
        }
        silverfish.setPos(
                this.worldPosition.getX() + 0.5D,
                this.worldPosition.getY() + 1.5D,
                this.worldPosition.getZ() + 0.5D
        );
        silverfish.finalizeSpawn(
                level,
                level.getCurrentDifficultyAt(silverfish.blockPosition()),
                MobSpawnType.TRIGGERED,
                null
        );
        if (Config.endPortalMode == Config.EndPortalDifficulty.NORMAL) {
            armSilverfish(level, silverfish);
        }
        level.addFreshEntity(silverfish);
    }

    private static void armSilverfish(final ServerLevel level, final Silverfish silverfish) {
        silverfish.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        silverfish.setDropChance(EquipmentSlot.HEAD, 0.0F);
        silverfish.setDropChance(EquipmentSlot.CHEST, 0.0F);
        silverfish.setDropChance(EquipmentSlot.LEGS, 0.0F);
        silverfish.setDropChance(EquipmentSlot.FEET, 0.0F);

        final ItemStack sword = new ItemStack(Items.IRON_SWORD);
        final Holder<Enchantment> fireAspect = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.FIRE_ASPECT);
        final Holder<Enchantment> sharpness = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.SHARPNESS);
        sword.enchant(fireAspect, 1);
        sword.enchant(sharpness, 3);

        silverfish.setItemSlot(EquipmentSlot.MAINHAND, sword);
        silverfish.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
        silverfish.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
        silverfish.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
        silverfish.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
    }

    private boolean hasBlock(final BlockPos target, final Block block) {
        return this.level != null && this.level.getBlockState(target).is(block);
    }

    private static BlockPos[] cornerOffsets(final int radius) {
        return new BlockPos[] {
                new BlockPos(-radius, 0, -radius),
                new BlockPos(radius, 0, -radius),
                new BlockPos(-radius, 0, radius),
                new BlockPos(radius, 0, radius)
        };
    }

    private static final class EndPortalCoreItemHandler extends ItemStacksResourceHandler {
        private final EndPortalCoreBlockEntity owner;

        private EndPortalCoreItemHandler(final EndPortalCoreBlockEntity owner) {
            super(SLOT_COUNT);
            this.owner = owner;
        }

        @Override
        public void deserialize(final ValueInput input) {
            super.deserialize(input);
            if (this.size() != SLOT_COUNT) {
                this.setStacks(NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY));
            }
        }

        @Override
        public boolean isValid(final int index, final ItemResource resource) {
            return index == EYE_SLOT && isFuel(resource.toStack());
        }

        @Override
        public int extract(
                final int index,
                final ItemResource resource,
                final int amount,
                final TransactionContext transaction
        ) {
            return 0;
        }

        @Override
        protected void onContentsChanged(final int index, final ItemStack previousContents) {
            this.owner.setChanged();
        }

        private ItemStack stack(final int slot) {
            return slot == EYE_SLOT ? this.stacks.get(EYE_SLOT) : ItemStack.EMPTY;
        }

        private void setStack(final int slot, final ItemStack stack) {
            if (slot != EYE_SLOT) {
                return;
            }
            this.stacks.set(EYE_SLOT, isFuel(stack) ? stack.copy() : ItemStack.EMPTY);
            this.owner.setChanged();
        }
    }
}
