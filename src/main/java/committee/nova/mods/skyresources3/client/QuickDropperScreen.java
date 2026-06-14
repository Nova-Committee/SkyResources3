package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.menu.QuickDropperMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class QuickDropperScreen extends AbstractContainerScreen<QuickDropperMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            Skyresources3.MODID,
            "textures/gui/blank_inventory.png"
    );
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    public QuickDropperScreen(
            final QuickDropperMenu menu,
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
        this.renderSlotTooltip(guiGraphics, mouseX, mouseY);
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
        GuiBlit.blit(guiGraphics,
                TEXTURE,
                this.leftPos + QuickDropperMenu.SLOT_X - 1,
                this.topPos + QuickDropperMenu.SLOT_Y - 1,
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
    }

    private void renderSlotTooltip(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(QuickDropperMenu.SLOT_X, QuickDropperMenu.SLOT_Y, 16, 16, mouseX, mouseY)) {
            return;
        }
        GuiTooltips.render(
                guiGraphics,
                this.font,
                mouseX,
                mouseY,
                Component.translatable("screen.skyresources3.quick_dropper.slot")
        );
    }
}
