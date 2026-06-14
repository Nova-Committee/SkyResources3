package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.menu.DarkMatterWarperMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class DarkMatterWarperScreen extends AbstractContainerScreen<DarkMatterWarperMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            Skyresources3.MODID,
            "textures/gui/blank_inventory.png"
    );
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int FUEL_X = 81;
    private static final int FUEL_Y = 37;
    private static final int FUEL_WIDTH = 14;
    private static final int FUEL_HEIGHT = 13;
    private static final int BORDER_DARK = 0xFF555555;
    private static final int BORDER_LIGHT = 0xFFFFFFFF;
    private static final int FUEL_BACKGROUND = 0xFF2F2634;
    private static final int FUEL_FILL = 0xFF8D3B8E;

    public DarkMatterWarperScreen(
            final DarkMatterWarperMenu menu,
            final Inventory playerInventory,
            final Component title
    ) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 72;
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderComponentTooltips(guiGraphics, mouseX, mouseY);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderComponentTooltips(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (this.isHovering(FUEL_X, FUEL_Y, FUEL_WIDTH, FUEL_HEIGHT, mouseX, mouseY)) {
            GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                    "screen.skyresources3.dark_matter_warper.fuel",
                    this.menu.getBurnTime(),
                    this.menu.getMaxBurnTime()
            ));
        }
        if (this.isHovering(DarkMatterWarperMenu.SLOT_X, DarkMatterWarperMenu.SLOT_Y, 16, 16, mouseX, mouseY)) {
            GuiTooltips.render(
                    guiGraphics,
                    this.font,
                    mouseX,
                    mouseY,
                    Component.translatable("screen.skyresources3.dark_matter_warper.fuel_slot")
            );
        }
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
        GuiBlit.blit(guiGraphics,
                TEXTURE,
                this.leftPos + DarkMatterWarperMenu.SLOT_X - 1,
                this.topPos + DarkMatterWarperMenu.SLOT_Y - 1,
                7,
                83,
                18,
                18,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
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
        this.renderFuelBar(guiGraphics);
    }

    private void renderFuelBar(final GuiGraphics guiGraphics) {
        guiGraphics.fill(FUEL_X - 1, FUEL_Y - 1, FUEL_X + FUEL_WIDTH + 1, FUEL_Y + FUEL_HEIGHT + 1, BORDER_DARK);
        guiGraphics.fill(FUEL_X, FUEL_Y, FUEL_X + FUEL_WIDTH, FUEL_Y + FUEL_HEIGHT, FUEL_BACKGROUND);
        guiGraphics.fill(FUEL_X, FUEL_Y, FUEL_X + FUEL_WIDTH, FUEL_Y + 1, BORDER_LIGHT);
        guiGraphics.fill(FUEL_X, FUEL_Y, FUEL_X + 1, FUEL_Y + FUEL_HEIGHT, BORDER_LIGHT);

        final int fillHeight = Math.round(this.menu.getFuelRatio() * FUEL_HEIGHT);
        if (fillHeight <= 0) {
            return;
        }
        guiGraphics.fill(
                FUEL_X + 1,
                FUEL_Y + FUEL_HEIGHT - fillHeight,
                FUEL_X + FUEL_WIDTH - 1,
                FUEL_Y + FUEL_HEIGHT,
                FUEL_FILL
        );
    }
}
