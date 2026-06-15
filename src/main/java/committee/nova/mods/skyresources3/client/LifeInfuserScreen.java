package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.common.menu.LifeInfuserMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public final class LifeInfuserScreen extends AbstractContainerScreen<LifeInfuserMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            Skyresources3.MODID,
            "textures/gui/infuser.png"
    );
    private static final Identifier ICONS = Identifier.fromNamespaceAndPath(
            Skyresources3.MODID,
            "textures/gui/gui_icons.png"
    );
    private static final Identifier HEART = Identifier.withDefaultNamespace("hud/heart/full");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int STATUS_X = 100;
    private static final int STATUS_Y = 50;
    private static final int STATUS_WIDTH = 48;
    private static final int STATUS_HEIGHT = 28;
    private static final int HEALTH_X = 120;
    private static final int HEALTH_Y = 29;
    private static final int HEALTH_WIDTH = 50;
    private static final int HEALTH_HEIGHT = 10;

    public LifeInfuserScreen(
            final LifeInfuserMenu menu,
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

    @Override
    protected void renderBg(
            final GuiGraphics guiGraphics,
            final float partialTick,
            final int mouseX,
            final int mouseY
    ) {
        GuiBlit.blit(
                guiGraphics,
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
        GuiBlit.blit(guiGraphics, ICONS, STATUS_X, STATUS_Y, 0, 16, 32, 28, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        GuiBlit.blit(
                guiGraphics,
                ICONS,
                132,
                58,
                this.menu.hasValidMultiblock() ? 0 : 16,
                0,
                16,
                16,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
        GuiBlit.sprite(guiGraphics, HEART, HEALTH_X, HEALTH_Y, 9, 9);
        guiGraphics.drawString(
                this.font,
                "x" + this.menu.storedHealth() / 2.0F,
                HEALTH_X + 10,
                HEALTH_Y,
                0xFF404040,
                false
        );
    }

    private void renderComponentTooltips(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (this.isHovering(HEALTH_X, HEALTH_Y, HEALTH_WIDTH, HEALTH_HEIGHT, mouseX, mouseY)) {
            GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                    "screen.skyresources.life.stored_health",
                    this.menu.storedHealth() / 2.0F
            ));
            return;
        }
        if (this.isHovering(STATUS_X, STATUS_Y, STATUS_WIDTH, STATUS_HEIGHT, mouseX, mouseY)) {
            GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(this.menu.hasValidMultiblock()
                    ? "screen.skyresources.life_infuser.multiblock.formed"
                    : "screen.skyresources.life_infuser.multiblock.missing"));
        }
    }
}
