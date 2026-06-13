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
    private static final int TEXT_COLOR = 0x404040;

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
                this.leftPos + 79,
                this.topPos + 52,
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
                Component.translatable(
                        "screen.skyresources3.machine_casing.heat",
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
                Component.translatable("screen.skyresources3.machine_casing.heat_per_tick", this.menu.heatPerTick()),
                19,
                34,
                TEXT_COLOR,
                false
        );
        guiGraphics.drawString(
                this.font,
                Component.translatable(this.menu.hasValidMultiblock()
                        ? "screen.skyresources3.machine_casing.multiblock.formed"
                        : "screen.skyresources3.machine_casing.multiblock.missing"),
                19,
                44,
                this.menu.hasValidMultiblock() ? 0x207020 : 0x902020,
                false
        );
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
