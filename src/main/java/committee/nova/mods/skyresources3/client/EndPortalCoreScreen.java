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
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int STATUS_X = 24;
    private static final int STATUS_Y = 28;

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
        guiGraphics.blit(
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
        guiGraphics.blit(
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
        guiGraphics.drawString(
                this.font,
                this.statusText(),
                STATUS_X,
                STATUS_Y,
                this.menu.hasValidMultiblock() ? 0x207020 : 0x902020,
                false
        );
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private Component statusText() {
        if (this.menu.hasValidTier2()) {
            return Component.translatable("screen.skyresources3.end_portal_core.structure.improved");
        }
        if (this.menu.hasValidMultiblock()) {
            return Component.translatable("screen.skyresources3.end_portal_core.structure.basic");
        }
        return Component.translatable("screen.skyresources3.end_portal_core.structure.missing");
    }
}
