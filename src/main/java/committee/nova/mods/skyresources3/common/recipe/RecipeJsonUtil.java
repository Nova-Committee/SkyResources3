package committee.nova.mods.skyresources3.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;

public final class RecipeJsonUtil {
    public static ItemStack stackFromJson(final JsonObject json) {
        final String key = json.has("item") ? "item" : "id";
        final ResourceLocation itemId = new ResourceLocation(GsonHelper.getAsString(json, key));
        final Item item = BuiltInRegistries.ITEM.getOptional(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown item '" + itemId + "'"));
        final ItemStack stack = new ItemStack(item, GsonHelper.getAsInt(json, "count", 1));
        if (json.has("nbt")) {
            stack.setTag(CraftingHelper.getNBT(json.get("nbt")));
        }
        return stack;
    }

    public static JsonObject stackToJson(final ItemStack stack) {
        final JsonObject json = new JsonObject();
        final ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        json.addProperty("item", itemId.toString());
        if (stack.getCount() != 1) {
            json.addProperty("count", stack.getCount());
        }
        final CompoundTag tag = stack.getTag();
        if (tag != null && !tag.isEmpty()) {
            json.addProperty("nbt", tag.toString());
        }
        return json;
    }

    private RecipeJsonUtil() {
    }
}
