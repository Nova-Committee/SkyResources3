package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.menu.EndPortalCoreMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class EndPortalCoreScreen extends AbstractContainerScreen<EndPortalCoreMenu> {
    private static final Identifier BACKGROUND =
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "textures/gui/blank_inventory.png");
    private static final Identifier ICONS =
            Identifier.fromNamespaceAndPath(Skyresources3.MODID, "textures/gui/gui_icons.png");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int STATUS_X = 3;
    private static final int STATUS_Y = 12;
    private static final int STATUS_WIDTH = 55;
    private static final int STATUS_HEIGHT = 28;

    public EndPortalCoreScreen(
            final EndPortalCoreMenu menu,
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
        GuiBlit.blit(
                guiGraphics,
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
        GuiBlit.blit(
                guiGraphics,
                BACKGROUND,
                this.leftPos + EndPortalCoreMenu.SLOT_X - 1,
                this.topPos + EndPortalCoreMenu.SLOT_Y - 1,
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
        super.renderLabels(guiGraphics, mouseX, mouseY);
        GuiBlit.blit(guiGraphics, ICONS, STATUS_X, STATUS_Y, 0, 16, 32, 28, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        if (this.menu.hasValidTier2()) {
            GuiBlit.blit(guiGraphics, ICONS, 42, 20, 0, 0, 16, 16, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
        GuiBlit.blit(
                guiGraphics,
                ICONS,
                35,
                20,
                this.menu.hasValidMultiblock() ? 0 : 16,
                0,
                16,
                16,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderStructureTooltip(guiGraphics, mouseX, mouseY);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderStructureTooltip(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(STATUS_X, STATUS_Y, STATUS_WIDTH, STATUS_HEIGHT, mouseX, mouseY)) {
            return;
        }
        final String key;
        if (this.menu.hasValidTier2()) {
            key = "screen.skyresources3.end_portal_core.structure.improved";
        } else if (this.menu.hasValidMultiblock()) {
            key = "screen.skyresources3.end_portal_core.structure.basic";
        } else {
            key = "screen.skyresources3.end_portal_core.structure.missing";
        }
        GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(key));
    }
}
