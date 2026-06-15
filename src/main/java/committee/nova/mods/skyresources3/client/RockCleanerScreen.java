package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.menu.RockCleanerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class RockCleanerScreen extends AbstractContainerScreen<RockCleanerMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            Skyresources3.MODID,
            "textures/gui/rock_cleaner.png"
    );
    private static final Identifier ICONS = Identifier.fromNamespaceAndPath(
            Skyresources3.MODID,
            "textures/gui/gui_icons.png"
    );
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int ENERGY_X = 22;
    private static final int ENERGY_Y = 30;
    private static final int ENERGY_WIDTH = 8;
    private static final int ENERGY_HEIGHT = 58;
    private static final int WATER_X = 142;
    private static final int WATER_Y = 30;
    private static final int WATER_WIDTH = 16;
    private static final int WATER_HEIGHT = 58;
    private static final int WATER_OVERLAY_U = 34;
    private static final int WATER_OVERLAY_V = 0;
    private static final int PROGRESS_X = 78;
    private static final int PROGRESS_Y = 49;
    private static final int PROGRESS_WIDTH = 24;
    private static final int WATER_COLOR = 0xCC3F76E4;

    public RockCleanerScreen(
            final RockCleanerMenu menu,
            final Inventory playerInventory,
            final Component title
    ) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 189;
        this.inventoryLabelY = 96;
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderEnergyTooltip(guiGraphics, mouseX, mouseY);
        this.renderWaterTooltip(guiGraphics, mouseX, mouseY);
        this.renderProgressTooltip(guiGraphics, mouseX, mouseY);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(
            final GuiGraphics guiGraphics,
            final float partialTick,
            final int mouseX,
            final int mouseY
    ) {
        GuiBlit.blit(guiGraphics,
                TEXTURE,
                this.leftPos,
                this.topPos,
                0,
                0,
                this.imageWidth,
                this.imageHeight,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
        this.renderEnergy(guiGraphics);
        this.renderWater(guiGraphics);
        this.renderProgress(guiGraphics);
    }

    @Override
    protected void renderLabels(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        guiGraphics.drawString(
                this.font,
                this.title,
                (this.imageWidth - this.font.width(this.title)) / 2,
                6,
                0xFF404040,
                false
        );
        guiGraphics.drawString(
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY,
                0xFF404040,
                false
        );
    }

    private void renderEnergy(final GuiGraphics guiGraphics) {
        final int height = Math.round(this.menu.getEnergyRatio() * ENERGY_HEIGHT);
        if (height <= 0) {
            return;
        }
        GuiBlit.blit(guiGraphics,
                ICONS,
                this.leftPos + ENERGY_X,
                this.topPos + ENERGY_Y + ENERGY_HEIGHT - height,
                51,
                59 - height,
                ENERGY_WIDTH,
                height,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
    }

    private void renderWater(final GuiGraphics guiGraphics) {
        final int height = Math.round(this.menu.getWaterRatio() * WATER_HEIGHT);
        if (height > 0) {
            guiGraphics.fill(
                    this.leftPos + WATER_X,
                    this.topPos + WATER_Y + WATER_HEIGHT - height,
                    this.leftPos + WATER_X + WATER_WIDTH,
                    this.topPos + WATER_Y + WATER_HEIGHT,
                    WATER_COLOR
            );
        }
        GuiBlit.blit(guiGraphics,
                ICONS,
                this.leftPos + WATER_X,
                this.topPos + WATER_Y,
                WATER_OVERLAY_U,
                WATER_OVERLAY_V,
                WATER_WIDTH,
                WATER_HEIGHT + 1,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
    }

    private void renderProgress(final GuiGraphics guiGraphics) {
        final int width = Math.round(this.menu.getProgressRatio() * PROGRESS_WIDTH);
        if (width <= 0) {
            return;
        }
        GuiBlit.blit(guiGraphics,
                ICONS,
                this.leftPos + PROGRESS_X,
                this.topPos + PROGRESS_Y,
                35,
                60,
                width + 1,
                16,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
    }

    private void renderEnergyTooltip(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(ENERGY_X, ENERGY_Y, ENERGY_WIDTH, ENERGY_HEIGHT + 1, mouseX, mouseY)) {
            return;
        }
        GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                "screen.skyresources.rock_cleaner.energy",
                this.menu.getEnergyStored(),
                this.menu.getMaxEnergyStored()
        ));
    }

    private void renderWaterTooltip(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(WATER_X, WATER_Y, WATER_WIDTH, WATER_HEIGHT + 1, mouseX, mouseY)) {
            return;
        }
        GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                "screen.skyresources.rock_cleaner.water",
                this.menu.getWaterStored(),
                this.menu.getMaxWaterStored()
        ));
    }

    private void renderProgressTooltip(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(PROGRESS_X, PROGRESS_Y, PROGRESS_WIDTH + 1, 16, mouseX, mouseY)) {
            return;
        }
        GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                "screen.skyresources.rock_cleaner.progress",
                GuiTooltips.percent(this.menu.getProgressRatio())
        ));
    }
}
