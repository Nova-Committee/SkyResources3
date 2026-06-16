package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.client.utils.GuiTooltips;
import committee.nova.mods.skyresources3.client.utils.MachineGuiTheme;
import committee.nova.mods.skyresources3.common.menu.MachineCasingMenu;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class MachineCasingScreen extends AbstractContainerScreen<MachineCasingMenu> {
    private static final int SLOT_X = 79;
    private static final int SLOT_Y = 42;
    private static final int SLOT_SIZE = 18;
    private static final int STATUS_X = 14;
    private static final int STATUS_Y = 61;
    private static final int STATUS_WIDTH = 148;
    private static final int STATUS_HEIGHT = 18;

    public MachineCasingScreen(
            final MachineCasingMenu menu,
            final Inventory playerInventory,
            final Component title
    ) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 174;
        this.inventoryLabelY = 80;
    }

    @Override
    protected void renderBg(
            final GuiGraphics guiGraphics,
            final float partialTick,
            final int mouseX,
            final int mouseY
    ) {
        MachineGuiTheme.renderPanel(guiGraphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        MachineGuiTheme.renderSlots(guiGraphics, this.leftPos, this.topPos, this.menu.slots);
    }

    @Override
    protected void renderLabels(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        MachineGuiTheme.renderTitle(guiGraphics, this.font, this.title, this.imageWidth);
        if (this.menu.usesHeatDisplay()) {
            MachineGuiTheme.renderHorizontalGauge(
                    guiGraphics,
                    this.font,
                    Component.translatable("screen.skyresources.metric.heat"),
                    Component.literal(this.menu.currentHeat() + "/" + this.menu.maxHeat()),
                    19,
                    24,
                    138,
                    this.heatRatio(),
                    MachineGuiTheme.HEAT
            );
            MachineGuiTheme.renderMetricChip(
                    guiGraphics,
                    this.font,
                    Component.translatable("screen.skyresources.metric.speed"),
                    MachineGuiTheme.percent(this.menu.speedPercent()),
                    14,
                    42,
                    62,
                    MachineGuiTheme.PROGRESS
            );
            MachineGuiTheme.renderMetricChip(
                    guiGraphics,
                    this.font,
                    Component.translatable("screen.skyresources.metric.efficiency"),
                    MachineGuiTheme.percent(this.menu.efficiencyPercent()),
                    100,
                    42,
                    62,
                    MachineGuiTheme.CATALYST
            );
        }
        if (this.menu.usesCombustionChamber()) {
            this.renderStatus(guiGraphics, this.menu.hasValidMultiblock());
        } else if (this.menu.hasCondenser()) {
            MachineGuiTheme.renderHorizontalGauge(
                    guiGraphics,
                    this.font,
                    Component.translatable("screen.skyresources.metric.progress"),
                    this.condenserProgressValue(),
                    19,
                    24,
                    138,
                    this.condenserRatio(),
                    MachineGuiTheme.PROGRESS
            );
            MachineGuiTheme.renderMetricChip(
                    guiGraphics,
                    this.font,
                    Component.translatable("screen.skyresources.metric.speed"),
                    MachineGuiTheme.percent(this.menu.speedPercent()),
                    14,
                    42,
                    62,
                    MachineGuiTheme.PROGRESS
            );
            MachineGuiTheme.renderMetricChip(
                    guiGraphics,
                    this.font,
                    Component.translatable("screen.skyresources.metric.efficiency"),
                    MachineGuiTheme.percent(this.menu.efficiencyPercent()),
                    100,
                    42,
                    62,
                    MachineGuiTheme.CATALYST
            );
            this.renderStatus(guiGraphics, this.menu.condenserCatalystLeftRatio() > 0.0F);
        } else if (this.menu.hasHeater()) {
            this.renderStatus(guiGraphics, this.menu.currentHeat() > 0);
        } else {
            this.renderStatus(guiGraphics, false);
        }
        MachineGuiTheme.renderInventoryLabel(
                guiGraphics,
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY
        );
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderComponentTooltips(guiGraphics, mouseX, mouseY);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderComponentTooltips(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (this.isHovering(SLOT_X, SLOT_Y, SLOT_SIZE, SLOT_SIZE, mouseX, mouseY)) {
            GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                    "screen.skyresources.machine_casing.slot"
            ));
            return;
        }
        if (this.isHovering(STATUS_X, STATUS_Y, STATUS_WIDTH, STATUS_HEIGHT, mouseX, mouseY)) {
            if (this.menu.hasCondenser()) {
                GuiTooltips.render(
                        guiGraphics,
                        this.font,
                        mouseX,
                        mouseY,
                        Component.translatable(
                                "screen.skyresources.machine_casing.condenser_catalyst",
                                GuiTooltips.percent(this.menu.condenserCatalystLeftRatio())
                        ),
                        Component.translatable(
                                "screen.skyresources.machine_casing.condenser_expected",
                                this.condenserExpectedValue()
                        )
                );
            } else {
                GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                        "screen.skyresources.machine_casing.status"
                ));
            }
        }
    }

    private void renderStatus(final GuiGraphics guiGraphics, final boolean valid) {
        MachineGuiTheme.renderStatus(
                guiGraphics,
                this.font,
                this.statusLabel(),
                this.statusValue(),
                STATUS_X,
                STATUS_Y,
                STATUS_WIDTH,
                valid
        );
    }

    private Component statusValue() {
        if (this.menu.usesCombustionChamber()) {
            return Component.translatable(this.menu.hasValidMultiblock()
                    ? "screen.skyresources.metric.formed"
                    : "screen.skyresources.metric.missing");
        }
        if (this.menu.hasCondenser()) {
            return this.condenserCatalystValue();
        }
        if (this.menu.hasHeater()) {
            return Component.translatable(this.menu.currentHeat() > 0
                    ? "screen.skyresources.metric.active"
                    : "screen.skyresources.metric.idle");
        }
        return Component.translatable("screen.skyresources.metric.empty");
    }

    private Component statusLabel() {
        if (this.menu.usesCombustionChamber()) {
            return Component.translatable("screen.skyresources.metric.structure");
        }
        if (this.menu.hasCondenser()) {
            return Component.translatable("screen.skyresources.metric.catalyst");
        }
        return Component.translatable("screen.skyresources.metric.status");
    }

    private Component condenserProgressValue() {
        if (this.menu.condenserMaxProgress() <= 0) {
            return Component.translatable("screen.skyresources.metric.idle");
        }
        return Component.literal(this.menu.condenserProgress() + "/" + this.menu.condenserMaxProgress());
    }

    private Component condenserCatalystValue() {
        final int catalystPercent = GuiTooltips.percent(this.menu.condenserCatalystLeftRatio());
        if (this.menu.condenserExpectedOutputValue() <= 0) {
            return Component.translatable("screen.skyresources.metric.percent", catalystPercent);
        }
        return Component.literal(catalystPercent + "% / " + this.formatCondenserExpectedValue());
    }

    private Component condenserExpectedValue() {
        if (this.menu.condenserExpectedOutputValue() <= 0) {
            return Component.translatable("screen.skyresources.metric.idle");
        }
        return Component.literal(this.formatCondenserExpectedValue());
    }

    private String formatCondenserExpectedValue() {
        final float value = this.menu.condenserExpectedOutput();
        final String suffix = this.menu.isCondenserExpectedOutputValueCapped() ? "+" : "";
        if (value >= 100.0F) {
            return String.format(Locale.ROOT, "%.0f%s", value, suffix);
        }
        if (value >= 10.0F) {
            return String.format(Locale.ROOT, "%.1f%s", value, suffix);
        }
        return String.format(Locale.ROOT, "%.2f%s", value, suffix);
    }

    private float heatRatio() {
        if (this.menu.maxHeat() <= 0) {
            return 0.0F;
        }
        return Math.min(1.0F, this.menu.currentHeat() / (float) this.menu.maxHeat());
    }

    private float condenserRatio() {
        if (this.menu.condenserMaxProgress() <= 0) {
            return 0.0F;
        }
        return Math.min(1.0F, this.menu.condenserProgress() / (float) this.menu.condenserMaxProgress());
    }
}
