package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.guide.GuideAction;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

final class GuideRecipeIntegration {
    private static final String JEI_MOD_ID = "jei";
    private static final String JEI_PLUGIN_CLASS =
            "committee.nova.mods.skyresources3.integration.jei.SkyResourcesJeiPlugin";

    static boolean open(final GuideAction action) {
        if (!ModList.get().isLoaded(JEI_MOD_ID)) {
            return false;
        }
        try {
            final Class<?> pluginClass = Class.forName(JEI_PLUGIN_CLASS);
            final Method method = pluginClass.getMethod("openGuideRecipe", String.class, ItemStack.class);
            final Object result = method.invoke(null, action.target(), action.icon());
            return Boolean.TRUE.equals(result);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException
                 | LinkageError ignored) {
            return false;
        }
    }

    private GuideRecipeIntegration() {
    }
}
