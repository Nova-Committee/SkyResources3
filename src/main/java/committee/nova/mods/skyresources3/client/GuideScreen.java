package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.guide.GuidePage;
import committee.nova.mods.skyresources3.guide.GuidePages;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class GuideScreen extends Screen {
    private static final int PANEL_MAX_WIDTH = 360;
    private static final int PANEL_MAX_HEIGHT = 224;
    private static final int PANEL_PADDING = 16;
    private static final int FOOTER_HEIGHT = 36;
    private static final int BUTTON_SIZE = 20;
    private static final int SMALL_BUTTON_WIDTH = 24;
    private static final int CLOSE_BUTTON_WIDTH = 60;
    private static final int ICON_SIZE = 16;
    private static final int BACKGROUND_COLOR = 0xC0101010;
    private static final int PANEL_COLOR = 0xF0E7D8BD;
    private static final int BORDER_COLOR = 0xFF6B5A44;
    private static final int TEXT_COLOR = 0xFF2F261C;
    private static final int MUTED_TEXT_COLOR = 0xFF5D5142;

    private int selectedCategoryIndex;
    private int selectedPageIndex;

    public GuideScreen() {
        super(Component.translatable("screen.skyresources3.guide.title"));
    }

    @Override
    protected void init() {
        final int panelX = this.panelX();
        final int panelY = this.panelY();
        final int panelWidth = this.panelWidth();
        final int footerY = panelY + this.panelHeight() - 28;

        this.addRenderableWidget(Button.builder(
                        Component.literal("<"),
                        button -> this.changeCategory(-1)
                )
                .bounds(panelX + PANEL_PADDING, footerY, SMALL_BUTTON_WIDTH, BUTTON_SIZE)
                .tooltip(Tooltip.create(Component.translatable("button.skyresources3.guide.previous_category")))
                .build());
        this.addRenderableWidget(Button.builder(
                        Component.literal(">"),
                        button -> this.changeCategory(1)
                )
                .bounds(panelX + PANEL_PADDING + SMALL_BUTTON_WIDTH + 4, footerY, SMALL_BUTTON_WIDTH, BUTTON_SIZE)
                .tooltip(Tooltip.create(Component.translatable("button.skyresources3.guide.next_category")))
                .build());
        this.addRenderableWidget(Button.builder(
                        Component.literal("<"),
                        button -> this.changePage(-1)
                )
                .bounds(panelX + 84, footerY, SMALL_BUTTON_WIDTH, BUTTON_SIZE)
                .tooltip(Tooltip.create(Component.translatable("button.skyresources3.guide.previous_page")))
                .build());
        this.addRenderableWidget(Button.builder(
                        Component.literal(">"),
                        button -> this.changePage(1)
                )
                .bounds(panelX + 84 + SMALL_BUTTON_WIDTH + 4, footerY, SMALL_BUTTON_WIDTH, BUTTON_SIZE)
                .tooltip(Tooltip.create(Component.translatable("button.skyresources3.guide.next_page")))
                .build());
        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.done"),
                        button -> this.onClose()
                )
                .bounds(panelX + panelWidth - PANEL_PADDING - CLOSE_BUTTON_WIDTH, footerY, CLOSE_BUTTON_WIDTH, BUTTON_SIZE)
                .build());
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        guiGraphics.fill(0, 0, this.width, this.height, BACKGROUND_COLOR);

        final int panelX = this.panelX();
        final int panelY = this.panelY();
        final int panelWidth = this.panelWidth();
        final int panelHeight = this.panelHeight();
        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, PANEL_COLOR);
        guiGraphics.renderOutline(panelX, panelY, panelWidth, panelHeight, BORDER_COLOR);
        guiGraphics.drawCenteredString(this.font, this.title, panelX + panelWidth / 2, panelY + 8, TEXT_COLOR);

        this.renderPage(guiGraphics, mouseX, mouseY, panelX, panelY, panelWidth, panelHeight);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void renderPage(
            final GuiGraphics guiGraphics,
            final int mouseX,
            final int mouseY,
            final int panelX,
            final int panelY,
            final int panelWidth,
            final int panelHeight
    ) {
        this.clampSelection();

        final List<String> categories = GuidePages.categories();
        if (categories.isEmpty()) {
            guiGraphics.drawCenteredString(
                    this.font,
                    Component.translatable("screen.skyresources3.guide.no_pages"),
                    panelX + panelWidth / 2,
                    panelY + panelHeight / 2 - this.font.lineHeight,
                    MUTED_TEXT_COLOR
            );
            return;
        }

        final List<GuidePage> pages = this.currentPages();
        if (pages.isEmpty()) {
            guiGraphics.drawCenteredString(
                    this.font,
                    Component.translatable("screen.skyresources3.guide.no_pages"),
                    panelX + panelWidth / 2,
                    panelY + panelHeight / 2 - this.font.lineHeight,
                    MUTED_TEXT_COLOR
            );
            return;
        }

        final GuidePage page = pages.get(this.selectedPageIndex);
        final int contentX = panelX + PANEL_PADDING;
        final int contentY = panelY + 28;
        final int contentWidth = panelWidth - PANEL_PADDING * 2;
        final Component category = Component.translatable(
                "screen.skyresources3.guide.category_counter",
                page.category(),
                this.selectedCategoryIndex + 1,
                categories.size()
        );
        final Component pageCounter = Component.translatable(
                "screen.skyresources3.guide.page_counter",
                this.selectedPageIndex + 1,
                pages.size()
        );

        guiGraphics.drawString(this.font, category, contentX, contentY, MUTED_TEXT_COLOR, false);
        guiGraphics.drawString(
                this.font,
                pageCounter,
                contentX + contentWidth - this.font.width(pageCounter),
                contentY,
                MUTED_TEXT_COLOR,
                false
        );

        final int iconY = contentY + 24;
        final ItemStack icon = page.icon();
        if (!icon.isEmpty()) {
            guiGraphics.renderFakeItem(icon, contentX, iconY);
            guiGraphics.renderItemDecorations(this.font, icon, contentX, iconY);
            if (this.isInside(mouseX, mouseY, contentX, iconY, ICON_SIZE, ICON_SIZE)) {
                guiGraphics.setTooltipForNextFrame(this.font, icon, mouseX, mouseY);
            }
        }

        guiGraphics.drawWordWrap(
                this.font,
                page.title(),
                contentX + ICON_SIZE + 8,
                iconY + 3,
                contentWidth - ICON_SIZE - 8,
                TEXT_COLOR
        );

        final int bodyY = iconY + 30;
        final int bodyBottom = panelY + panelHeight - FOOTER_HEIGHT;
        if (bodyBottom <= bodyY) {
            return;
        }
        guiGraphics.enableScissor(contentX, bodyY, contentX + contentWidth, bodyBottom);
        guiGraphics.drawWordWrap(this.font, page.text(), contentX, bodyY, contentWidth, TEXT_COLOR);
        guiGraphics.disableScissor();
    }

    private List<GuidePage> currentPages() {
        final List<String> categories = GuidePages.categories();
        if (categories.isEmpty()) {
            return List.of();
        }
        return GuidePages.pagesInCategory(categories.get(this.selectedCategoryIndex));
    }

    private void changeCategory(final int direction) {
        final List<String> categories = GuidePages.categories();
        if (categories.isEmpty()) {
            return;
        }
        this.selectedCategoryIndex = Math.floorMod(this.selectedCategoryIndex + direction, categories.size());
        this.selectedPageIndex = 0;
    }

    private void changePage(final int direction) {
        final List<GuidePage> pages = this.currentPages();
        if (pages.isEmpty()) {
            return;
        }
        this.selectedPageIndex = Math.floorMod(this.selectedPageIndex + direction, pages.size());
    }

    private void clampSelection() {
        final List<String> categories = GuidePages.categories();
        if (categories.isEmpty()) {
            this.selectedCategoryIndex = 0;
            this.selectedPageIndex = 0;
            return;
        }

        this.selectedCategoryIndex = Math.floorMod(this.selectedCategoryIndex, categories.size());
        final List<GuidePage> pages = this.currentPages();
        this.selectedPageIndex = pages.isEmpty() ? 0 : Math.floorMod(this.selectedPageIndex, pages.size());
    }

    private int panelX() {
        return (this.width - this.panelWidth()) / 2;
    }

    private int panelY() {
        return (this.height - this.panelHeight()) / 2;
    }

    private int panelWidth() {
        return Math.min(PANEL_MAX_WIDTH, Math.max(160, this.width - 24));
    }

    private int panelHeight() {
        return Math.min(PANEL_MAX_HEIGHT, Math.max(140, this.height - 36));
    }

    private boolean isInside(
            final int mouseX,
            final int mouseY,
            final int x,
            final int y,
            final int width,
            final int height
    ) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
}
