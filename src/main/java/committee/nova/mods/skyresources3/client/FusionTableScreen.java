package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.Skyresources3;
import committee.nova.mods.skyresources3.block.entity.FusionTableBlockEntity;
import committee.nova.mods.skyresources3.menu.FusionTableMenu;
import committee.nova.mods.skyresources3.network.FusionTableDumpPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public final class FusionTableScreen extends AbstractContainerScreen<FusionTableMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            Skyresources3.MODID,
            "textures/gui/fusion_table.png"
    );
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    public FusionTableScreen(
            final FusionTableMenu menu,
            final Inventory playerInventory,
            final Component title
    ) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 181;
        this.inventoryLabelY = 87;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(
                        Component.translatable("button.skyresources3.dump.short"),
                        button -> ClientPacketDistributor.sendToServer(new FusionTableDumpPayload(this.menu.getBlockPos()))
                )
                .bounds(this.leftPos + 115, this.topPos + 64, 18, 18)
                .tooltip(Tooltip.create(Component.translatable("button.skyresources3.dump")))
                .build());
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(
            final GuiGraphics guiGraphics,
            final float partialTick,
            final int mouseX,
            final int mouseY
    ) {
        guiGraphics.blit(
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
                4210752,
                false
        );
        guiGraphics.drawString(
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY,
                4210752,
                false
        );
        this.renderProgress(guiGraphics);
        this.renderYield(guiGraphics);
        this.renderCatalyst(guiGraphics);
        this.renderFilterGhosts(guiGraphics);
    }

    private void renderProgress(final GuiGraphics guiGraphics) {
        final int height = Math.round(this.menu.getProgressRatio() * 17.0F);
        if (height <= 0) {
            return;
        }
        guiGraphics.blit(
                TEXTURE,
                7,
                51,
                0,
                181,
                162,
                height,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
    }

    private void renderYield(final GuiGraphics guiGraphics) {
        final int height = Math.round(this.menu.getCurrentYieldRatio() * 26.0F);
        guiGraphics.blit(
                TEXTURE,
                103,
                69,
                176,
                0,
                3,
                26,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
        if (height <= 0) {
            return;
        }
        guiGraphics.blit(
                TEXTURE,
                104,
                95 - height,
                179,
                26 - height,
                1,
                height,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
    }

    private void renderCatalyst(final GuiGraphics guiGraphics) {
        guiGraphics.fill(134, 73, 137, 91, 0xFF4A4A4A);
        guiGraphics.fill(135, 74, 136, 90, 0xFFB8B8B8);
        final int height = Math.round(this.menu.getCatalystLeftRatio() * 16.0F);
        if (height > 0) {
            guiGraphics.fill(135, 90 - height, 136, 90, 0xFF5F85C4);
        }
        guiGraphics.drawString(
                this.font,
                this.menu.getCatalystYieldPercent() + "%",
                140,
                78,
                4210752,
                false
        );
    }

    private void renderFilterGhosts(final GuiGraphics guiGraphics) {
        for (int index = 0; index < FusionTableBlockEntity.INPUT_SLOT_COUNT; index++) {
            if (!this.menu.getSlot(FusionTableBlockEntity.FIRST_INPUT_SLOT + index).getItem().isEmpty()) {
                continue;
            }
            final ItemStack filter = this.menu.getFilterStack(index);
            if (filter.isEmpty()) {
                continue;
            }
            final int x = 8 + index * 18;
            final int y = 34;
            guiGraphics.renderFakeItem(filter.copyWithCount(1), x, y);
            guiGraphics.drawString(this.font, "0", x + 11, y + 9, 0xFFFF0000, false);
        }
    }
}
