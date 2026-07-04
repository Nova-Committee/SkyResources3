package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.client.utils.GuiBlit;
import committee.nova.mods.skyresources3.client.utils.GuiTooltips;
import committee.nova.mods.skyresources3.client.utils.MachineGuiTheme;
import committee.nova.mods.skyresources3.common.menu.LifeInfuserMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class LifeInfuserScreen extends AbstractContainerScreen<LifeInfuserMenu> {
    private static final ResourceLocation HEART = new ResourceLocation("minecraft", "textures/gui/icons.png");
    private static final int STATUS_X = 95;
    private static final int STATUS_Y = 48;
    private static final int STATUS_WIDTH = 70;
    private static final int STATUS_HEIGHT = 18;
    private static final int HEALTH_X = 16;
    private static final int HEALTH_Y = 31;
    private static final int HEALTH_WIDTH = 74;
    private static final int HEALTH_HEIGHT = 16;

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
        MachineGuiTheme.renderStatus(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.structure"),
                Component.translatable(this.menu.hasValidMultiblock()
                        ? "screen.skyresources.metric.formed"
                        : "screen.skyresources.metric.missing"),
                STATUS_X,
                STATUS_Y,
                STATUS_WIDTH,
                this.menu.hasValidMultiblock()
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
