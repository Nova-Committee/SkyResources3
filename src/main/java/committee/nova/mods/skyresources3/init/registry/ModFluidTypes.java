package committee.nova.mods.skyresources3.init.registry;

import committee.nova.mods.skyresources3.Skyresources3;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public final class ModFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(
            ForgeRegistries.Keys.FLUID_TYPES,
            Skyresources3.MODID
    );

    public static final RegistryObject<FluidType> CRYSTAL_FLUID = FLUID_TYPES.register(
            "crystal_fluid",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid_type.skyresources.crystal_fluid")
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)) {
                @Override
                public void initializeClient(final Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final ResourceLocation STILL_TEXTURE =
                                new ResourceLocation(Skyresources3.MODID, "block/crystal_fluid_still");
                        private static final ResourceLocation FLOWING_TEXTURE =
                                new ResourceLocation(Skyresources3.MODID, "block/crystal_fluid_flow");

                        @Override
                        public ResourceLocation getStillTexture() {
                            return STILL_TEXTURE;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return FLOWING_TEXTURE;
                        }
                    });
                }
            }
    );

    public static void register(final IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
    }

    private ModFluidTypes() {
    }
}
