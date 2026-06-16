package committee.nova.mods.skyresources3.common.block.entity;

import committee.nova.mods.skyresources3.common.block.StandaloneMachineBlock;
import committee.nova.mods.skyresources3.common.item.CombustionHeaterItem;
import committee.nova.mods.skyresources3.common.item.CondenserItem;
import committee.nova.mods.skyresources3.common.item.HeatProviderItem;
import committee.nova.mods.skyresources3.core.machine.CombustionHeaterType;
import committee.nova.mods.skyresources3.core.machine.CondenserType;
import committee.nova.mods.skyresources3.core.machine.HeatProviderType;
import committee.nova.mods.skyresources3.init.registry.ModBlockEntityTypes;
import committee.nova.mods.skyresources3.init.registry.ModDataComponents;
import committee.nova.mods.skyresources3.init.registry.ModDataPackRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class StandaloneMachineBlockEntity extends BlockEntity {
    private static final String TYPE_KEY = "machine_type";

    private Identifier typeId;

    public StandaloneMachineBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.STANDALONE_MACHINE.get(), pos, blockState);
        this.typeId = this.kind().defaultTypeId();
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        this.typeId = input.read(TYPE_KEY, Identifier.CODEC).orElse(this.kind().defaultTypeId());
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.store(TYPE_KEY, Identifier.CODEC, this.typeId);
    }

    @Override
    protected void applyImplicitComponents(final DataComponentGetter componentInput) {
        super.applyImplicitComponents(componentInput);
        this.typeId = this.kind().typeId(componentInput);
    }

    @Override
    protected void collectImplicitComponents(final DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        this.kind().writeType(components, this.typeId);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(final HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
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

    public Identifier typeId() {
        return this.typeId;
    }

    public void setTypeId(final Identifier typeId) {
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
            Identifier defaultTypeId() {
                return ModDataPackRegistries.combustionHeaterTypeId(ModDataPackRegistries.IRON_COMBUSTION_HEATER);
            }

            @Override
            Identifier typeId(final DataComponentGetter componentInput) {
                return componentInput.getOrDefault(ModDataComponents.COMBUSTION_HEATER_TYPE.get(), this.defaultTypeId());
            }

            @Override
            void writeType(final DataComponentMap.Builder components, final Identifier typeId) {
                components.set(ModDataComponents.COMBUSTION_HEATER_TYPE.get(), typeId);
            }

            @Override
            ItemStack stack(final Identifier typeId) {
                return CombustionHeaterItem.forType(typeId);
            }
        },
        HEAT_PROVIDER {
            @Override
            Identifier defaultTypeId() {
                return ModDataPackRegistries.heatProviderTypeId(ModDataPackRegistries.IRON_HEAT_PROVIDER);
            }

            @Override
            Identifier typeId(final DataComponentGetter componentInput) {
                return componentInput.getOrDefault(ModDataComponents.HEAT_PROVIDER_TYPE.get(), this.defaultTypeId());
            }

            @Override
            void writeType(final DataComponentMap.Builder components, final Identifier typeId) {
                components.set(ModDataComponents.HEAT_PROVIDER_TYPE.get(), typeId);
            }

            @Override
            ItemStack stack(final Identifier typeId) {
                return HeatProviderItem.forType(typeId);
            }
        },
        CONDENSER {
            @Override
            Identifier defaultTypeId() {
                return ModDataPackRegistries.condenserTypeId(ModDataPackRegistries.IRON_CONDENSER);
            }

            @Override
            Identifier typeId(final DataComponentGetter componentInput) {
                return componentInput.getOrDefault(ModDataComponents.CONDENSER_TYPE.get(), this.defaultTypeId());
            }

            @Override
            void writeType(final DataComponentMap.Builder components, final Identifier typeId) {
                components.set(ModDataComponents.CONDENSER_TYPE.get(), typeId);
            }

            @Override
            ItemStack stack(final Identifier typeId) {
                return CondenserItem.forType(typeId);
            }
        };

        abstract Identifier defaultTypeId();

        abstract Identifier typeId(DataComponentGetter componentInput);

        abstract void writeType(DataComponentMap.Builder components, Identifier typeId);

        abstract ItemStack stack(Identifier typeId);
    }
}
