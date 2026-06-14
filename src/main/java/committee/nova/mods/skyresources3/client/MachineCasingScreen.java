package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.menu.MachineCasingMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class MachineCasingScreen extends AbstractContainerScreen<MachineCasingMenu> {
    private static final Identifier BACKGROUND =
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "textures/gui/blank_inventory.png");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int TEXT_COLOR = 0xFF404040;
    private static final int VALID_TEXT_COLOR = 0xFF207020;
    private static final int INVALID_TEXT_COLOR = 0xFF902020;
    private static final int SLOT_X = 79;
    private static final int SLOT_Y = 52;
    private static final int SLOT_SIZE = 18;
    private static final int STATUS_X = 19;
    private static final int STATUS_Y = 24;
    private static final int STATUS_WIDTH = 140;
    private static final int STATUS_HEIGHT = 31;

    public MachineCasingScreen(
            final MachineCasingMenu menu,
            final Inventory playerInventory,
            final Component title
    ) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 72;
    }

    @Override
    protected void renderBg(
            final GuiGraphics guiGraphics,
            final float partialTick,
            final int mouseX,
            final int mouseY
    ) {
        GuiBlit.blit(guiGraphics,
                BACKGROUND,
                this.leftPos,
                this.topPos,
                0,
                0,
                this.imageWidth,
                this.imageHeight,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
        GuiBlit.blit(guiGraphics,
                BACKGROUND,
                this.leftPos + SLOT_X,
                this.topPos + SLOT_Y,
                7,
                83,
                SLOT_SIZE,
                SLOT_SIZE,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
    }

    @Override
    protected void renderLabels(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        if (this.menu.usesHeatDisplay()) {
            guiGraphics.drawString(
                    this.font,
                    Component.translatable(
                            "screen.skyresources.machine_casing.heat",
                            this.menu.currentHeat(),
                            this.menu.maxHeat()
                    ),
                    19,
                    24,
                    TEXT_COLOR,
                    false
            );
            guiGraphics.drawString(
                    this.font,
                    Component.translatable("screen.skyresources.machine_casing.heat_per_tick", this.menu.heatPerTick()),
                    19,
                    34,
                    TEXT_COLOR,
                    false
            );
        }
        if (this.menu.usesCombustionChamber()) {
            guiGraphics.drawString(
                    this.font,
                    Component.translatable(this.menu.hasValidMultiblock()
                            ? "screen.skyresources.machine_casing.multiblock.formed"
                            : "screen.skyresources.machine_casing.multiblock.missing"),
                    19,
                    44,
                    this.menu.hasValidMultiblock() ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR,
                    false
            );
        } else if (this.menu.hasCondenser()) {
            guiGraphics.drawString(
                    this.font,
                    Component.translatable("screen.skyresources.machine_casing.condenser.installed"),
                    19,
                    24,
                    TEXT_COLOR,
                    false
            );
            guiGraphics.drawString(
                    this.font,
                    this.condenserProgressText(),
                    19,
                    34,
                    this.menu.condenserMaxProgress() > 0 ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR,
                    false
            );
        } else if (this.menu.hasHeater()) {
            guiGraphics.drawString(
                    this.font,
                    Component.translatable(this.menu.currentHeat() > 0
                            ? "screen.skyresources.machine_casing.heat_provider.active"
                            : "screen.skyresources.machine_casing.heat_provider.idle"),
                    19,
                    44,
                    this.menu.currentHeat() > 0 ? VALID_TEXT_COLOR : INVALID_TEXT_COLOR,
                    false
            );
        }
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
            GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                    "screen.skyresources.machine_casing.status"
            ));
        }
    }

    private Component condenserProgressText() {
        if (this.menu.condenserMaxProgress() <= 0) {
            return Component.translatable("screen.skyresources.machine_casing.condenser.idle");
        }
        return Component.translatable(
                "screen.skyresources.machine_casing.condenser.progress",
                this.menu.condenserProgress(),
                this.menu.condenserMaxProgress()
        );
    }
}
