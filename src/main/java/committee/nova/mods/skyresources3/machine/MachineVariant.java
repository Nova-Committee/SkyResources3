package committee.nova.mods.skyresources3.machine;

import committee.nova.mods.skyresources3.registry.ModItems;
import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public enum MachineVariant {
    WOODEN("wooden", 100, 0.5F, 0.4F, FuelKind.FURNACE, null, 1),
    STONE("stone", 600, 1.0F, 0.8F, FuelKind.FURNACE, null, 1),
    IRON("iron", 1538, 1.2F, 1.0F, FuelKind.FURNACE, null, 1),
    NETHER_BRICK("nether_brick", 3072, 0.4F, 1.6F, FuelKind.ITEM, () -> Items.BLAZE_POWDER, 300),
    END_STONE("end_stone", 2164, 6.6F, 6.6F, FuelKind.ITEM, () -> Items.ENDER_PEARL, 2200),
    DARK_MATTER("dark_matter", 4042, 1.0F, 100.0F, FuelKind.ITEM, () -> ModItems.DARK_MATTER.get(), 31415),
    LIGHT_MATTER("light_matter", 1566, 100.0F, 1.0F, FuelKind.ITEM, () -> ModItems.LIGHT_MATTER.get(), 27183);

    private final String id;
    private final int maxHeat;
    private final float efficiency;
    private final float speed;
    private final FuelKind fuelKind;
    private final Supplier<ItemLike> fuelItem;
    private final int fuelRate;

    MachineVariant(
            final String id,
            final int maxHeat,
            final float efficiency,
            final float speed,
            final FuelKind fuelKind,
            final Supplier<ItemLike> fuelItem,
            final int fuelRate
    ) {
        this.id = id;
        this.maxHeat = maxHeat;
        this.efficiency = efficiency;
        this.speed = speed;
        this.fuelKind = fuelKind;
        this.fuelItem = fuelItem;
        this.fuelRate = fuelRate;
    }

    public String id() {
        return this.id;
    }

    public String registryName(final String suffix) {
        return this.id + "_" + suffix;
    }

    public String translationName() {
        final String[] words = this.id.split("_");
        final StringBuilder name = new StringBuilder();
        for (final String word : words) {
            if (name.length() > 0) {
                name.append(' ');
            }
            name.append(word.substring(0, 1).toUpperCase(Locale.ROOT)).append(word.substring(1));
        }
        return name.toString();
    }

    public int maxHeat() {
        return this.maxHeat;
    }

    public float efficiency() {
        return this.efficiency;
    }

    public float speed() {
        return this.speed;
    }

    public float heatPerTick() {
        return this.speed * 10.0F;
    }

    public boolean isValidFuel(final ItemStack stack, final Level level) {
        if (stack.isEmpty()) {
            return false;
        }
        if (this.fuelKind == FuelKind.FURNACE) {
            return stack.getBurnTime(RecipeType.SMELTING, level.fuelValues()) > 0;
        }
        return stack.is(this.fuelItem.get().asItem());
    }

    public float fuelHeat(final ItemStack stack, final Level level, final float combinedEfficiency) {
        if (!this.isValidFuel(stack, level)) {
            return 0.0F;
        }
        if (this.fuelKind == FuelKind.FURNACE) {
            return stack.getBurnTime(RecipeType.SMELTING, level.fuelValues()) * combinedEfficiency;
        }
        return this.fuelRate * combinedEfficiency;
    }

    public enum FuelKind {
        FURNACE,
        ITEM
    }
}
