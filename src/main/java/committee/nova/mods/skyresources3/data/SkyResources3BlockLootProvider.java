package committee.nova.mods.skyresources3.data;

import committee.nova.mods.skyresources3.registry.ModBlocks;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class SkyResources3BlockLootProvider extends BlockLootSubProvider {
    public SkyResources3BlockLootProvider(final HolderLookup.Provider lookupProvider) {
        super(Set.<Item>of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.COMPRESSED_COAL_BLOCK.get());
        this.dropSelf(ModBlocks.COAL_INFUSED_BLOCK.get());
        this.dropSelf(ModBlocks.SANDY_NETHERRACK.get());
        this.dropSelf(ModBlocks.PETRIFIED_WOOD.get());
        this.dropSelf(ModBlocks.PETRIFIED_PLANKS.get());
        this.dropSelf(ModBlocks.MAGMAFIED_STONE.get());
        this.dropSelf(ModBlocks.HEAVY_SNOW.get());
        this.dropSelf(ModBlocks.BLAZE_POWDER_BLOCK.get());
        this.dropSelf(ModBlocks.DARK_MATTER_BLOCK.get());
        this.dropSelf(ModBlocks.LIGHT_MATTER_BLOCK.get());
        this.dropSelf(ModBlocks.ALCHEMICAL_GLASS.get());
        this.add(ModBlocks.CRYSTAL_FLUID.get(), noDrop());
        this.dropSelf(ModBlocks.FUSION_TABLE.get());
        this.dropSelf(ModBlocks.DIRT_FURNACE.get());
        this.dropSelf(ModBlocks.QUICK_DROPPER.get());
        this.dropSelf(ModBlocks.DARK_MATTER_WARPER.get());
        this.dropSelf(ModBlocks.END_PORTAL_CORE.get());
        this.dropSelf(ModBlocks.SILVERFISH_DISRUPTOR.get());
        this.dropSelf(ModBlocks.FLUID_DROPPER.get());
        this.dropSelf(ModBlocks.CRUCIBLE.get());
        this.dropSelf(ModBlocks.CRUCIBLE_INSERTER.get());
        this.dropSelf(ModBlocks.ROCK_CRUSHER.get());
        this.dropSelf(ModBlocks.ROCK_CLEANER.get());
        this.dropSelf(ModBlocks.AQUEOUS_CONCENTRATOR.get());
        this.dropSelf(ModBlocks.AQUEOUS_DECONCENTRATOR.get());
        this.dropSelf(ModBlocks.WILDLIFE_ATTRACTOR.get());
        this.add(ModBlocks.MACHINE_CASING.get(), noDrop());
        this.dropSelf(ModBlocks.COMBUSTION_COLLECTOR.get());
        this.dropSelf(ModBlocks.COMBUSTION_CONTROLLER.get());
        this.dropSelf(ModBlocks.MINI_FREEZER.get());
        this.dropSelf(ModBlocks.IRON_FREEZER.get());
        this.dropSelf(ModBlocks.LIGHT_FREEZER.get());
        this.dropSelf(ModBlocks.LIFE_INFUSER.get());
        this.dropSelf(ModBlocks.LIFE_INJECTOR.get());
        this.dropSelf(ModBlocks.CACTUS_FRUIT_NEEDLE.get());
        this.dropSelf(ModBlocks.DRY_CACTUS.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(holder -> (Block) holder.get())
                .toList();
    }
}
