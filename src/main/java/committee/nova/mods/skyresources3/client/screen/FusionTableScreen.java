package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.client.utils.GuiTooltips;
import committee.nova.mods.skyresources3.client.utils.MachineGuiTheme;
import committee.nova.mods.skyresources3.common.block.entity.FusionTableBlockEntity;
import committee.nova.mods.skyresources3.common.menu.FusionTableMenu;
import committee.nova.mods.skyresources3.common.network.FusionTableDumpPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public final class FusionTableScreen extends AbstractContainerScreen<FusionTableMenu> {
    private static final int PROGRESS_X = 7;
    private static final int PROGRESS_Y = 51;
    private static final int PROGRESS_WIDTH = 80;
    private static final int PROGRESS_HEIGHT = 16;
    private static final int YIELD_X = 103;
    private static final int YIELD_Y = 69;
    private static final int YIELD_WIDTH = 3;
    private static final int YIELD_HEIGHT = 26;
    private static final int CATALYST_X = 134;
    private static final int CATALYST_Y = 73;
    private static final int CATALYST_WIDTH = 18;
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
        this.addRenderableWidget(SkyResourcesButton.create(
                Component.translatable("button.skyresources.dump.short"),
                button -> ClientPacketDistributor.sendToServer(new FusionTableDumpPayload(this.menu.getBlockPos())),
                this.leftPos + 153,
                this.topPos + 73,
                18,
                18,
                Tooltip.create(Component.translatable("button.skyresources.dump")),
                SkyResourcesButton.Tone.DEFAULT
        ));
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
        this.renderProgress(guiGraphics);
        this.renderYield(guiGraphics);
        this.renderCatalyst(guiGraphics);
        this.renderFilterGhosts(guiGraphics);
    }

    private void renderProgress(final GuiGraphics guiGraphics) {
        MachineGuiTheme.renderHorizontalGauge(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.progress"),
                MachineGuiTheme.percent(this.menu.getProgressRatio()),
                PROGRESS_X,
                PROGRESS_Y,
                PROGRESS_WIDTH,
                this.menu.getProgressRatio(),
                MachineGuiTheme.PROGRESS
        );
    }

    private void renderYield(final GuiGraphics guiGraphics) {
        final int height = Math.round(this.menu.getCurrentYieldRatio() * 26.0F);
        guiGraphics.fill(YIELD_X, YIELD_Y, YIELD_X + YIELD_WIDTH, YIELD_Y + YIELD_HEIGHT, 0xFF3B4650);
        guiGraphics.fill(YIELD_X + 1, YIELD_Y + 1, YIELD_X + YIELD_WIDTH - 1, YIELD_Y + YIELD_HEIGHT - 1, 0xFF0C1117);
        if (height <= 0) {
            return;
        }
        guiGraphics.fill(
                YIELD_X + 1,
                YIELD_Y + YIELD_HEIGHT - 1 - height,
                YIELD_X + YIELD_WIDTH - 1,
                YIELD_Y + YIELD_HEIGHT - 1,
                MachineGuiTheme.MATTER
        );
    }

    private void renderCatalyst(final GuiGraphics guiGraphics) {
        MachineGuiTheme.renderMetricChip(
                guiGraphics,
                this.font,
                Component.translatable("screen.skyresources.metric.efficiency"),
                MachineGuiTheme.percent(this.menu.getCatalystYieldPercent()),
                92,
                51,
                77,
                MachineGuiTheme.CATALYST
        );
        MachineGuiTheme.renderVerticalGauge(
                guiGraphics,
                this.font,
                Component.empty(),
                136,
                73,
                5,
                CATALYST_HEIGHT,
                this.menu.getCatalystLeftRatio(),
                MachineGuiTheme.CATALYST
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
                    "screen.skyresources.fusion_table.progress",
                    GuiTooltips.percent(this.menu.getProgressRatio())
            ));
            return;
        }
        if (this.isHovering(YIELD_X, YIELD_Y, YIELD_WIDTH + 2, YIELD_HEIGHT, mouseX, mouseY)) {
            GuiTooltips.render(guiGraphics, this.font, mouseX, mouseY, Component.translatable(
                    "screen.skyresources.fusion_table.yield",
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
                            "screen.skyresources.fusion_table.catalyst_bonus",
                            this.menu.getCatalystYieldPercent()
                    ),
                    Component.translatable(
                            "screen.skyresources.fusion_table.catalyst_left",
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
                        "screen.skyresources.fusion_table.filter_ghost",
                        filter.getHoverName()
                ));
                return;
            }
        }
    }
}
