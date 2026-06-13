package committee.nova.mods.skyresources3.data;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.machine.MachineVariant;
import committee.nova.mods.skyresources3.registry.ModBlocks;
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
                ModBlocks.FLUID_DROPPER.get(),
                ModBlocks.CRUCIBLE.get(),
                ModBlocks.CRUCIBLE_INSERTER.get(),
                ModBlocks.ROCK_CRUSHER.get(),
                ModBlocks.ROCK_CLEANER.get(),
                ModBlocks.MACHINE_CASINGS.get(MachineVariant.STONE).get(),
                ModBlocks.MACHINE_CASINGS.get(MachineVariant.IRON).get(),
                ModBlocks.MACHINE_CASINGS.get(MachineVariant.NETHER_BRICK).get(),
                ModBlocks.MACHINE_CASINGS.get(MachineVariant.END_STONE).get(),
                ModBlocks.MACHINE_CASINGS.get(MachineVariant.DARK_MATTER).get(),
                ModBlocks.MACHINE_CASINGS.get(MachineVariant.LIGHT_MATTER).get(),
                ModBlocks.IRON_FREEZER.get(),
                ModBlocks.LIGHT_FREEZER.get()
        );
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(
                ModBlocks.PETRIFIED_PLANKS.get(),
                ModBlocks.FUSION_TABLE.get(),
                ModBlocks.MACHINE_CASINGS.get(MachineVariant.WOODEN).get()
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
                ModBlocks.MACHINE_CASINGS.get(MachineVariant.IRON).get(),
                ModBlocks.MACHINE_CASINGS.get(MachineVariant.NETHER_BRICK).get(),
                ModBlocks.MACHINE_CASINGS.get(MachineVariant.END_STONE).get(),
                ModBlocks.MACHINE_CASINGS.get(MachineVariant.DARK_MATTER).get(),
                ModBlocks.MACHINE_CASINGS.get(MachineVariant.LIGHT_MATTER).get()
        );
    }
}
