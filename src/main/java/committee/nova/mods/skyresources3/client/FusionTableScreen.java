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
    private static final int PROGRESS_X = 7;
    private static final int PROGRESS_Y = 51;
    private static final int PROGRESS_WIDTH = 162;
    private static final int PROGRESS_HEIGHT = 17;
    private static final int YIELD_X = 103;
    private static final int YIELD_Y = 69;
    private static final int YIELD_WIDTH = 3;
    private static final int YIELD_HEIGHT = 26;
    private static final int CATALYST_X = 134;
    private static final int CATALYST_Y = 73;
    private static final int CATALYST_WIDTH = 39;
    private static final int CATALYST_HEIGHT = 18;

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
        GuiBlit.blit(guiGraphics,
                TEXTURE,
                PROGRESS_X,
                PROGRESS_Y,
                0,
                181,
                PROGRESS_WIDTH,
                height,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
    }

    private void renderYield(final GuiGraphics guiGraphics) {
        final int height = Math.round(this.menu.getCurrentYieldRatio() * 26.0F);
        GuiBlit.blit(guiGraphics,
                TEXTURE,
                YIELD_X,
                YIELD_Y,
                176,
                0,
                YIELD_WIDTH,
                YIELD_HEIGHT,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
        if (height <= 0) {
            return;
        }
        GuiBlit.blit(guiGraphics,
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
        guiGraphics.fill(CATALYST_X, CATALYST_Y, CATALYST_X + 3, CATALYST_Y + CATALYST_HEIGHT, 0xFF4A4A4A);
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
                0xFF404040,
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

    private void renderComponentTooltips(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (this.isHovering(PROGRESS_X, PROGRESS_Y, PROGRESS_WIDTH, PROGRESS_HEIGHT, mouseX, mouseY)) {
            GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                    "screen.skyresources3.fusion_table.progress",
                    GuiTooltips.percent(this.menu.getProgressRatio())
            ));
            return;
        }
        if (this.isHovering(YIELD_X, YIELD_Y, YIELD_WIDTH + 2, YIELD_HEIGHT, mouseX, mouseY)) {
            GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                    "screen.skyresources3.fusion_table.yield",
                    GuiTooltips.percent(this.menu.getCurrentYieldRatio())
            ));
            return;
        }
        if (this.isHovering(CATALYST_X, CATALYST_Y, CATALYST_WIDTH, CATALYST_HEIGHT, mouseX, mouseY)) {
            GuiTooltips.render(
                    guiGraphics,
                    this.font,
                    mouseX,
                    mouseY,
                    Component.translatable(
                            "screen.skyresources3.fusion_table.catalyst_bonus",
                            this.menu.getCatalystYieldPercent()
                    ),
                    Component.translatable(
                            "screen.skyresources3.fusion_table.catalyst_left",
                            GuiTooltips.percent(this.menu.getCatalystLeftRatio())
                    )
            );
            return;
        }
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
            if (this.isHovering(x, y, 16, 16, mouseX, mouseY)) {
                GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                        "screen.skyresources3.fusion_table.filter_ghost",
                        filter.getHoverName()
                ));
                return;
            }
        }
    }
}
