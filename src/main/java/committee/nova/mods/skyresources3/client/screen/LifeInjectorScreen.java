package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.client.utils.GuiBlit;
import committee.nova.mods.skyresources3.client.utils.GuiTooltips;
import committee.nova.mods.skyresources3.client.utils.MachineGuiTheme;
import committee.nova.mods.skyresources3.common.menu.LifeInjectorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class LifeInjectorScreen extends AbstractContainerScreen<LifeInjectorMenu> {
    private static final ResourceLocation HEART = new ResourceLocation("minecraft", "textures/gui/icons.png");
    private static final int HEALTH_X = 99;
    private static final int HEALTH_Y = 31;
    private static final int HEALTH_WIDTH = 60;
    private static final int HEALTH_HEIGHT = 16;

    public LifeInjectorScreen(
            final LifeInjectorMenu menu,
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
        this.renderHealthTooltip(guiGraphics, mouseX, mouseY);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
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
        MachineGuiTheme.renderInventoryLabel(
                guiGraphics,
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY
        );
        GuiBlit.sprite(guiGraphics, HEART, HEALTH_X, HEALTH_Y, 9, 9);
        MachineGuiTheme.renderHorizontalGauge(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.health"),
                Component.literal(Float.toString(this.menu.storedHealth() / 2.0F)),
                HEALTH_X + 12,
                HEALTH_Y,
                HEALTH_WIDTH,
                Math.min(1.0F, this.menu.storedHealth() / 100.0F),
                MachineGuiTheme.LIFE
        );
    }

    private void renderHealthTooltip(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (!this.isHovering(HEALTH_X, HEALTH_Y, HEALTH_WIDTH, HEALTH_HEIGHT, mouseX, mouseY)) {
            return;
        }
        GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                "screen.skyresources.life.stored_health",
                this.menu.storedHealth() / 2.0F
        ));
    }
}
