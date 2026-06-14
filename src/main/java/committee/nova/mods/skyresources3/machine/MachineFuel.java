package committee.nova.mods.skyresources3.machine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record MachineFuel(FuelKind kind, Optional<Identifier> item, int rate) {
    public static final Codec<MachineFuel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FuelKind.CODEC.fieldOf("kind").forGetter(MachineFuel::kind),
            Identifier.CODEC.optionalFieldOf("item").forGetter(MachineFuel::item),
            Codec.INT.optionalFieldOf("rate", 1).forGetter(MachineFuel::rate)
    ).apply(instance, MachineFuel::new));

    public static MachineFuel furnace() {
        return new MachineFuel(FuelKind.FURNACE, Optional.empty(), 1);
    }

    public boolean isValid(final ItemStack stack, final Level level) {
        if (stack.isEmpty()) {
            return false;
        }
        if (this.kind == FuelKind.FURNACE) {
            return stack.getBurnTime(RecipeType.SMELTING, level.fuelValues()) > 0;
        }
        return this.item.map(id -> stack.is(BuiltInRegistries.ITEM.getValue(id))).orElse(false);
    }

    public float heat(final ItemStack stack, final Level level, final float combinedEfficiency) {
        if (!this.isValid(stack, level)) {
            return 0.0F;
        }
        if (this.kind == FuelKind.FURNACE) {
            return stack.getBurnTime(RecipeType.SMELTING, level.fuelValues()) * combinedEfficiency;
        }
        return this.rate * combinedEfficiency;
    }

    public enum FuelKind implements StringRepresentable {
        FURNACE("furnace"),
        ITEM("item");

        public static final Codec<FuelKind> CODEC = StringRepresentable.fromEnum(FuelKind::values);
        private final String serializedName;

        FuelKind(final String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public String getSerializedName() {
            return this.serializedName;
        }
    }
}
