package committee.nova.mods.skyresources3.common.block.entity;

import committee.nova.mods.skyresources3.common.block.StandaloneMachineBlock;
import committee.nova.mods.skyresources3.common.item.CombustionHeaterItem;
import committee.nova.mods.skyresources3.common.item.CondenserItem;
import committee.nova.mods.skyresources3.common.item.HeatProviderItem;
import committee.nova.mods.skyresources3.core.machine.CombustionHeaterType;
import committee.nova.mods.skyresources3.core.machine.CondenserType;
import committee.nova.mods.skyresources3.core.machine.HeatProviderType;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import committee.nova.mods.skyresources3.common.compat.ValueInput;
import committee.nova.mods.skyresources3.common.compat.ValueOutput;

public final class StandaloneMachineBlockEntity extends BlockEntity {
    private static final String TYPE_KEY = "machine_type";

    private ResourceLocation typeId;

    public StandaloneMachineBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.STANDALONE_MACHINE.get(), pos, blockState);
        this.typeId = this.kind().defaultTypeId();
    }

    @Override
    public void load(final net.minecraft.nbt.CompoundTag tag) {
        super.load(tag);
        final ValueInput input = new ValueInput(tag);
        this.typeId = input.read(TYPE_KEY, ResourceLocation.CODEC).orElse(this.kind().defaultTypeId());
    }

    @Override
    protected void saveAdditional(final net.minecraft.nbt.CompoundTag tag) {
        super.saveAdditional(tag);
        final ValueOutput output = new ValueOutput(tag);
        output.store(TYPE_KEY, ResourceLocation.CODEC, this.typeId);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        Containers.dropItemStack(
                this.level,
                this.worldPosition.getX() + 0.5D,
                this.worldPosition.getY() + 0.5D,
                this.worldPosition.getZ() + 0.5D,
                this.asItemStack()
        );
    }

    public MachineKind kind() {
        if (this.getBlockState().getBlock() instanceof StandaloneMachineBlock machineBlock) {
            return machineBlock.kind();
        }
        return MachineKind.COMBUSTION_HEATER;
    }

    public ResourceLocation typeId() {
        return this.typeId;
    }

    public void setTypeId(final ResourceLocation typeId) {
        if (this.typeId.equals(typeId)) {
            return;
        }
        this.typeId = typeId;
        this.setChangedAndUpdate();
    }

    public ItemStack asItemStack() {
        return this.kind().stack(this.typeId);
    }

    public Component displayName() {
        return Component.translatable(switch (this.kind()) {
            case COMBUSTION_HEATER -> this.combustionHeaterType().translationKey();
            case HEAT_PROVIDER -> this.heatProviderType().translationKey();
            case CONDENSER -> this.condenserType().translationKey();
        });
    }

    public CombustionHeaterType combustionHeaterType() {
        if (this.level == null || this.kind() != MachineKind.COMBUSTION_HEATER) {
            return CombustionHeaterType.fallback();
        }
        return this.level.registryAccess()
                .lookup(ModDataPackRegistries.COMBUSTION_HEATER_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.combustionHeaterTypeKey(this.typeId)))
                .map(reference -> reference.value())
                .orElse(CombustionHeaterType.fallback());
    }

    public HeatProviderType heatProviderType() {
        if (this.level == null || this.kind() != MachineKind.HEAT_PROVIDER) {
            return HeatProviderType.fallback();
        }
        return this.level.registryAccess()
                .lookup(ModDataPackRegistries.HEAT_PROVIDER_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.heatProviderTypeKey(this.typeId)))
                .map(reference -> reference.value())
                .orElse(HeatProviderType.fallback());
    }

    public CondenserType condenserType() {
        if (this.level == null || this.kind() != MachineKind.CONDENSER) {
            return CondenserType.fallback();
        }
        return this.level.registryAccess()
                .lookup(ModDataPackRegistries.CONDENSER_TYPES)
                .flatMap(registry -> registry.get(ModDataPackRegistries.condenserTypeKey(this.typeId)))
                .map(reference -> reference.value())
                .orElse(CondenserType.fallback());
    }

    private void setChangedAndUpdate() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public enum MachineKind {
        COMBUSTION_HEATER {
            @Override
            ResourceLocation defaultTypeId() {
                return ModDataPackRegistries.combustionHeaterTypeId(ModDataPackRegistries.IRON_COMBUSTION_HEATER);
            }

            @Override
            ItemStack stack(final ResourceLocation typeId) {
                return CombustionHeaterItem.forType(typeId);
            }
        },
        HEAT_PROVIDER {
            @Override
            ResourceLocation defaultTypeId() {
                return ModDataPackRegistries.heatProviderTypeId(ModDataPackRegistries.IRON_HEAT_PROVIDER);
            }

            @Override
            ItemStack stack(final ResourceLocation typeId) {
                return HeatProviderItem.forType(typeId);
            }
        },
        CONDENSER {
            @Override
            ResourceLocation defaultTypeId() {
                return ModDataPackRegistries.condenserTypeId(ModDataPackRegistries.IRON_CONDENSER);
            }

            @Override
            ItemStack stack(final ResourceLocation typeId) {
                return CondenserItem.forType(typeId);
            }
        };

        abstract ResourceLocation defaultTypeId();

        abstract ItemStack stack(ResourceLocation typeId);
    }
}
