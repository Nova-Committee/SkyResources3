package committee.nova.mods.skyresources3.common.item;

import committee.nova.mods.skyresources3.Skyresources3;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

final class VariantModelData {
    private VariantModelData() {
    }

    static void apply(final ItemStack stack, final ResourceLocation typeId) {
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(value(typeId)));
    }

    private static int value(final ResourceLocation typeId) {
        if (!Skyresources3.MODID.equals(typeId.getNamespace())) {
            return 0;
        }
        return switch (typeId.getPath()) {
            case "wooden" -> 1;
            case "stone" -> 2;
            case "iron" -> 3;
            case "nether_brick" -> 4;
            case "end_stone" -> 5;
            case "dark_matter" -> 6;
            case "light_matter" -> 7;
            default -> 0;
        };
    }
}
