package committee.nova.mods.skyresources3.init.data;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.init.registry.ModBlocks;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

public final class SkyResources3BlockTagsProvider extends BlockTagsProvider {
    public SkyResources3BlockTagsProvider(
            final PackOutput output,
            final CompletableFuture<HolderLookup.Provider> lookupProvider
    ) {
        super(output, lookupProvider, Skyresources3.MODID);
    }

    @Override
    protected void addTags(final HolderLookup.Provider lookupProvider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModBlocks.COMPRESSED_COAL_BLOCK.get(),
                ModBlocks.COAL_INFUSED_BLOCK.get(),
                ModBlocks.SANDY_NETHERRACK.get(),
                ModBlocks.PETRIFIED_WOOD.get(),
                ModBlocks.MAGMAFIED_STONE.get(),
                ModBlocks.DARK_MATTER_BLOCK.get(),
                ModBlocks.LIGHT_MATTER_BLOCK.get(),
                ModBlocks.ALCHEMICAL_GLASS.get(),
                ModBlocks.QUICK_DROPPER.get(),
                ModBlocks.DARK_MATTER_WARPER.get(),
                ModBlocks.END_PORTAL_CORE.get(),
                ModBlocks.SILVERFISH_DISRUPTOR.get(),
                ModBlocks.FLUID_DROPPER.get(),
                ModBlocks.CRUCIBLE.get(),
                ModBlocks.CRUCIBLE_INSERTER.get(),
                ModBlocks.ROCK_CRUSHER.get(),
                ModBlocks.ROCK_CLEANER.get(),
                ModBlocks.AQUEOUS_CONCENTRATOR.get(),
                ModBlocks.AQUEOUS_DECONCENTRATOR.get(),
                ModBlocks.WILDLIFE_ATTRACTOR.get(),
                ModBlocks.COMBUSTION_COLLECTOR.get(),
                ModBlocks.COMBUSTION_CONTROLLER.get(),
                ModBlocks.COMBUSTION_HEATER.get(),
                ModBlocks.HEAT_PROVIDER.get(),
                ModBlocks.CONDENSER.get(),
                ModBlocks.MACHINE_CASING.get(),
                ModBlocks.IRON_FREEZER.get(),
                ModBlocks.LIGHT_FREEZER.get()
        );
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(
                ModBlocks.PETRIFIED_PLANKS.get(),
                ModBlocks.FUSION_TABLE.get(),
                ModBlocks.MACHINE_CASING.get()
        );
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(
                ModBlocks.HEAVY_SNOW.get(),
                ModBlocks.BLAZE_POWDER_BLOCK.get(),
                ModBlocks.DIRT_FURNACE.get(),
                ModBlocks.MINI_FREEZER.get()
        );
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(
                ModBlocks.COMPRESSED_COAL_BLOCK.get(),
                ModBlocks.COAL_INFUSED_BLOCK.get(),
                ModBlocks.DARK_MATTER_BLOCK.get(),
                ModBlocks.LIGHT_MATTER_BLOCK.get(),
                ModBlocks.DARK_MATTER_WARPER.get(),
                ModBlocks.END_PORTAL_CORE.get(),
                ModBlocks.MACHINE_CASING.get(),
                ModBlocks.COMBUSTION_HEATER.get(),
                ModBlocks.HEAT_PROVIDER.get(),
                ModBlocks.CONDENSER.get(),
                ModBlocks.COMBUSTION_COLLECTOR.get(),
                ModBlocks.COMBUSTION_CONTROLLER.get()
        );
    }
}
