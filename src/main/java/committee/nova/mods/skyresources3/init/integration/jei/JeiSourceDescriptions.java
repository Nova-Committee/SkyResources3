package committee.nova.mods.skyresources3.init.integration.jei;

import committee.nova.mods.skyresources3.common.block.entity.FusionTableBlockEntity;
import committee.nova.mods.skyresources3.common.recipe.CondenserRecipe;
import committee.nova.mods.skyresources3.common.recipe.ProcessIngredient;
import committee.nova.mods.skyresources3.common.recipe.ProcessRecipes;
import committee.nova.mods.skyresources3.common.recipe.SkyResourcesProcessRecipe;
import committee.nova.mods.skyresources3.init.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

final class JeiSourceDescriptions {
    private static final int FUSION_CATALYST_X = 102;
    private static final int FUSION_CATALYST_Y = 48;

    static void addProcessInputTooltip(
            final IRecipeSlotBuilder slot,
            final String process,
            final int index
    ) {
        switch (process) {
            case ProcessRecipes.FUSION -> slot.addRichTooltipCallback((recipeSlotView, tooltip) -> {
                addLine(tooltip, "jei.skyresources.tooltip.fusion.input");
                addGuideJump(tooltip, "guide.skyresources.stage2.fusionTable.title");
            });
            case ProcessRecipes.COMBUSTION -> slot.addRichTooltipCallback((recipeSlotView, tooltip) -> {
                addLine(tooltip, "jei.skyresources.tooltip.combustion.input");
                addGuideJump(tooltip, "guide.skyresources.stage2.combustionHeater.title");
            });
            case ProcessRecipes.INFUSION -> slot.addRichTooltipCallback((recipeSlotView, tooltip) -> {
                addLine(tooltip, index == 0
                        ? "jei.skyresources.tooltip.infusion.ingredient"
                        : "jei.skyresources.tooltip.infusion.target");
                addGuideJump(tooltip, "guide.skyresources.stage1.lifeInfusion.title");
            });
            default -> {
            }
        }
    }

    static void addProcessOutputTooltip(final IRecipeSlotBuilder slot, final String process) {
        switch (process) {
            case ProcessRecipes.FUSION -> slot.addRichTooltipCallback((recipeSlotView, tooltip) -> {
                addLine(tooltip, "jei.skyresources.tooltip.fusion.output");
                addGuideJump(tooltip, "guide.skyresources.stage2.fusionTable.title");
            });
            case ProcessRecipes.COMBUSTION -> slot.addRichTooltipCallback((recipeSlotView, tooltip) -> {
                addLine(tooltip, "jei.skyresources.tooltip.combustion.output");
                addGuideJump(tooltip, "guide.skyresources.stage2.combustionHeater.title");
            });
            case ProcessRecipes.INFUSION -> slot.addRichTooltipCallback((recipeSlotView, tooltip) -> {
                addLine(tooltip, "jei.skyresources.tooltip.infusion.output");
                addGuideJump(tooltip, "guide.skyresources.stage1.lifeInfusion.title");
            });
            default -> {
            }
        }
    }

    static void addFusionCatalystSlot(final IRecipeLayoutBuilder builder) {
        builder.addInputSlot(FUSION_CATALYST_X, FUSION_CATALYST_Y)
                .setStandardSlotBackground()
                .addItemStacks(fusionCatalysts())
                .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                    recipeSlotView.getDisplayedItemStack()
                            .map(FusionTableBlockEntity::getCatalystValue)
                            .map(value -> Math.round(value * 100.0F))
                            .filter(value -> value > 0)
                            .ifPresent(value -> tooltip.add(Component.translatable(
                                    "jei.skyresources.tooltip.fusion.catalyst_value",
                                    value
                            ).withStyle(ChatFormatting.AQUA)));
                    addLine(tooltip, "jei.skyresources.tooltip.fusion.catalyst");
                    addGuideJump(tooltip, "guide.skyresources.stage2.fusionTable.title");
                });
    }

    static void addCondenserCatalystTooltip(final IRecipeSlotBuilder slot) {
        slot.addRichTooltipCallback((recipeSlotView, tooltip) -> {
            addLine(tooltip, "jei.skyresources.tooltip.condenser.catalyst");
            addGuideJump(tooltip, "guide.skyresources.stage2.condenser.title");
        });
    }

    static void addCondenserSourceTooltip(
            final IRecipeSlotBuilder slot,
            final CondenserRecipe.Source source
    ) {
        slot.addRichTooltipCallback((recipeSlotView, tooltip) -> {
            addLine(tooltip, source.type() == CondenserRecipe.SourceType.FLUID
                    ? "jei.skyresources.tooltip.condenser.source_fluid"
                    : "jei.skyresources.tooltip.condenser.source_block");
            addGuideJump(tooltip, "guide.skyresources.stage2.condenser.title");
        });
    }

    static void addCondenserOutputTooltip(final IRecipeSlotBuilder slot) {
        slot.addRichTooltipCallback((recipeSlotView, tooltip) -> {
            addLine(tooltip, "jei.skyresources.tooltip.condenser.output");
            addGuideJump(tooltip, "guide.skyresources.stage2.condenser.title");
        });
    }

    static void registerIngredientInfo(
            final IRecipeRegistration registration,
            final List<SkyResourcesProcessRecipe> combustionRecipes,
            final List<SkyResourcesProcessRecipe> fusionRecipes,
            final List<SkyResourcesProcessRecipe> infusionRecipes,
            final List<CondenserRecipe> condenserRecipes
    ) {
        addInfo(
                registration,
                collectProcessInputs(fusionRecipes),
                "jei.skyresources.info.fusion.inputs",
                "guide.skyresources.stage2.fusionTable.title"
        );
        addInfo(
                registration,
                fusionCatalysts(),
                "jei.skyresources.info.fusion.catalysts",
                "guide.skyresources.stage2.fusionTable.title"
        );
        addInfo(
                registration,
                collectProcessInputs(combustionRecipes),
                "jei.skyresources.info.combustion.inputs",
                "guide.skyresources.stage2.combustionHeater.title"
        );
        addInfo(
                registration,
                collectProcessInputs(infusionRecipes),
                "jei.skyresources.info.infusion.inputs",
                "guide.skyresources.stage1.lifeInfusion.title"
        );
        addInfo(
                registration,
                collectCondenserCatalysts(condenserRecipes),
                "jei.skyresources.info.condenser.catalysts",
                "guide.skyresources.stage2.condenser.title"
        );
        addInfo(
                registration,
                collectCondenserSources(condenserRecipes),
                "jei.skyresources.info.condenser.sources",
                "guide.skyresources.stage2.condenser.title"
        );
    }

    private static List<ItemStack> fusionCatalysts() {
        return List.of(
                new ItemStack(ModItems.PRIMUS_ALCHEMICAL_DUST.get()),
                new ItemStack(ModItems.SECUNDUS_ALCHEMICAL_DUST.get()),
                new ItemStack(ModItems.TERTIUS_ALCHEMICAL_DUST.get()),
                new ItemStack(ModItems.QUARTUS_ALCHEMICAL_DUST.get())
        );
    }

    private static List<ItemStack> collectProcessInputs(final List<SkyResourcesProcessRecipe> recipes) {
        final List<ItemStack> stacks = new ArrayList<>();
        for (final SkyResourcesProcessRecipe recipe : recipes) {
            for (final ProcessIngredient ingredient : recipe.inputs()) {
                for (final ItemStack stack : ingredient.displayStacks()) {
                    addUnique(stacks, stack);
                }
            }
        }
        return stacks;
    }

    private static List<ItemStack> collectCondenserCatalysts(final List<CondenserRecipe> recipes) {
        final List<ItemStack> stacks = new ArrayList<>();
        for (final CondenserRecipe recipe : recipes) {
            for (final ItemStack stack : recipe.catalyst().displayStacks()) {
                addUnique(stacks, stack);
            }
        }
        return stacks;
    }

    private static List<ItemStack> collectCondenserSources(final List<CondenserRecipe> recipes) {
        final List<ItemStack> stacks = new ArrayList<>();
        for (final CondenserRecipe recipe : recipes) {
            switch (recipe.source().type()) {
                case BLOCK -> BuiltInRegistries.BLOCK.getOptional(recipe.source().id())
                        .map(JeiSourceDescriptions::sourceStack)
                        .ifPresent(stack -> addUnique(stacks, stack));
                case FLUID -> BuiltInRegistries.FLUID.getOptional(recipe.source().id())
                        .map(JeiSourceDescriptions::bucketStack)
                        .ifPresent(stack -> addUnique(stacks, stack));
            }
        }
        return stacks;
    }

    private static ItemStack sourceStack(final Block block) {
        return new ItemStack(block);
    }

    private static ItemStack bucketStack(final Fluid fluid) {
        final Item bucket = fluid.getBucket();
        return bucket == Items.AIR ? ItemStack.EMPTY : new ItemStack(bucket);
    }

    private static void addInfo(
            final IRecipeRegistration registration,
            final List<ItemStack> stacks,
            final String key,
            final String guideTitleKey
    ) {
        final List<ItemStack> filtered = stacks.stream()
                .filter(stack -> !stack.isEmpty())
                .toList();
        if (!filtered.isEmpty()) {
            registration.addItemStackInfo(
                    filtered,
                    Component.translatable(key, Component.translatable(guideTitleKey))
            );
        }
    }

    private static void addUnique(final List<ItemStack> stacks, final ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        final ItemStack normalized = stack.copyWithCount(1);
        for (final ItemStack existing : stacks) {
            if (ItemStack.isSameItemSameTags(existing, normalized)) {
                return;
            }
        }
        stacks.add(normalized);
    }

    private static void addLine(final ITooltipBuilder tooltip, final String key) {
        tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
    }

    private static void addGuideJump(final ITooltipBuilder tooltip, final String guideTitleKey) {
        tooltip.add(Component.translatable(
                "jei.skyresources.tooltip.guide_jump",
                Component.translatable(guideTitleKey)
        ).withStyle(ChatFormatting.DARK_GREEN));
    }

    private JeiSourceDescriptions() {
    }
}
