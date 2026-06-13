package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.guide.GuidePage;
import committee.nova.mods.skyresources3.guide.GuidePages;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class GuideScreen extends Screen {
    private static final int PANEL_MAX_WIDTH = 520;
    private static final int PANEL_MAX_HEIGHT = 260;
    private static final int PANEL_PADDING = 16;
    private static final int FOOTER_HEIGHT = 36;
    private static final int BUTTON_SIZE = 20;
    private static final int SMALL_BUTTON_WIDTH = 24;
    private static final int CLOSE_BUTTON_WIDTH = 60;
    private static final int SEARCH_HEIGHT = 18;
    private static final int INDEX_MIN_WIDTH = 120;
    private static final int INDEX_MAX_WIDTH = 148;
    private static final int INDEX_GAP = 12;
    private static final int RESULT_ROW_HEIGHT = 18;
    private static final int ICON_SIZE = 16;
    private static final int BACKGROUND_COLOR = 0xC0101010;
    private static final int PANEL_COLOR = 0xF0E7D8BD;
    private static final int BORDER_COLOR = 0xFF6B5A44;
    private static final int SELECTED_ROW_COLOR = 0x50FFFFFF;
    private static final int HOVERED_ROW_COLOR = 0x30FFFFFF;
    private static final int TEXT_COLOR = 0xFF2F261C;
    private static final int MUTED_TEXT_COLOR = 0xFF5D5142;

    private int selectedCategoryIndex;
    private int selectedPageIndex;
    private String searchText = "";
    private EditBox searchBox;

    public GuideScreen() {
        super(Component.translatable("screen.skyresources3.guide.title"));
    }

    @Override
    protected void init() {
        final int panelX = this.panelX();
        final int panelY = this.panelY();
        final int panelWidth = this.panelWidth();
        final int footerY = panelY + this.panelHeight() - 28;
        final String currentSearch = this.searchBox == null ? this.searchText : this.searchBox.getValue();

        this.searchBox = new EditBox(
                this.font,
                panelX + PANEL_PADDING,
                panelY + 24,
                panelWidth - PANEL_PADDING * 2,
                SEARCH_HEIGHT,
                Component.translatable("screen.skyresources3.guide.search")
        );
        this.searchBox.setMaxLength(40);
        this.searchBox.setHint(Component.translatable("screen.skyresources3.guide.search_hint"));
        this.searchBox.setValue(currentSearch);
        this.searchBox.setResponder(value -> {
            this.searchText = value;
            this.selectedPageIndex = 0;
            this.clampSelection();
        });
        this.addRenderableWidget(this.searchBox);
        this.searchBox.setFocused(true);
        this.setFocused(this.searchBox);

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
    public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) {
            return true;
        }
        return event.button() == 0 && this.selectResultAt(event.x(), event.y());
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

        final List<GuidePage> categoryPages = this.currentCategoryPages();
        if (categoryPages.isEmpty()) {
            guiGraphics.drawCenteredString(
                    this.font,
                    Component.translatable("screen.skyresources3.guide.no_pages"),
                    panelX + panelWidth / 2,
                    panelY + panelHeight / 2 - this.font.lineHeight,
                    MUTED_TEXT_COLOR
            );
            return;
        }

        final List<GuidePage> pages = this.visiblePages();
        this.renderResultIndex(
                guiGraphics,
                mouseX,
                mouseY,
                panelX,
                panelY,
                panelWidth,
                panelHeight,
                categoryPages.size(),
                pages
        );
        if (pages.isEmpty()) {
            this.renderNoResults(guiGraphics, panelX, panelY, panelWidth, panelHeight);
            return;
        }

        final GuidePage page = pages.get(this.selectedPageIndex);
        final boolean wideIndex = this.hasWideIndex(panelWidth);
        final int contentX = wideIndex
                ? panelX + PANEL_PADDING + this.indexWidth(panelWidth) + INDEX_GAP
                : panelX + PANEL_PADDING;
        final int contentY = panelY + 58;
        final int contentWidth = wideIndex
                ? panelWidth - PANEL_PADDING * 2 - this.indexWidth(panelWidth) - INDEX_GAP
                : panelWidth - PANEL_PADDING * 2;
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

    private void renderResultIndex(
            final GuiGraphics guiGraphics,
            final int mouseX,
            final int mouseY,
            final int panelX,
            final int panelY,
            final int panelWidth,
            final int panelHeight,
            final int categoryPageCount,
            final List<GuidePage> pages
    ) {
        final int x = panelX + PANEL_PADDING;
        final int y = panelY + 48;
        final Component resultCount = Component.translatable(
                "screen.skyresources3.guide.result_count",
                pages.size(),
                categoryPageCount
        );
        guiGraphics.drawString(this.font, resultCount, x, y, MUTED_TEXT_COLOR, false);

        if (!this.hasWideIndex(panelWidth)) {
            return;
        }

        final int listX = this.resultListX(panelX);
        final int listY = this.resultListY(panelY);
        final int listWidth = this.indexWidth(panelWidth);
        final int listBottom = this.resultListBottom(panelY, panelHeight);
        final int visibleRows = this.visibleResultRows(panelY, panelHeight);
        guiGraphics.enableScissor(listX, listY, listX + listWidth, listBottom);
        for (int index = 0; index < Math.min(visibleRows, pages.size()); index++) {
            this.renderResultRow(guiGraphics, mouseX, mouseY, pages.get(index), index, listX, listY, listWidth);
        }
        guiGraphics.disableScissor();
    }

    private void renderResultRow(
            final GuiGraphics guiGraphics,
            final int mouseX,
            final int mouseY,
            final GuidePage page,
            final int index,
            final int listX,
            final int listY,
            final int listWidth
    ) {
        final int rowY = listY + index * RESULT_ROW_HEIGHT;
        if (index == this.selectedPageIndex) {
            guiGraphics.fill(listX, rowY, listX + listWidth, rowY + RESULT_ROW_HEIGHT - 1, SELECTED_ROW_COLOR);
        } else if (this.isInside(mouseX, mouseY, listX, rowY, listWidth, RESULT_ROW_HEIGHT - 1)) {
            guiGraphics.fill(listX, rowY, listX + listWidth, rowY + RESULT_ROW_HEIGHT - 1, HOVERED_ROW_COLOR);
        }

        final ItemStack icon = page.icon();
        if (!icon.isEmpty()) {
            guiGraphics.renderFakeItem(icon, listX + 1, rowY + 1);
        }
        final int titleX = listX + ICON_SIZE + 5;
        final int titleWidth = listWidth - ICON_SIZE - 8;
        guiGraphics.drawString(
                this.font,
                this.truncate(page.title().getString(), titleWidth),
                titleX,
                rowY + 5,
                TEXT_COLOR,
                false
        );
    }

    private void renderNoResults(
            final GuiGraphics guiGraphics,
            final int panelX,
            final int panelY,
            final int panelWidth,
            final int panelHeight
    ) {
        final boolean wideIndex = this.hasWideIndex(panelWidth);
        final int contentX = wideIndex
                ? panelX + PANEL_PADDING + this.indexWidth(panelWidth) + INDEX_GAP
                : panelX + PANEL_PADDING;
        final int contentWidth = wideIndex
                ? panelWidth - PANEL_PADDING * 2 - this.indexWidth(panelWidth) - INDEX_GAP
                : panelWidth - PANEL_PADDING * 2;
        guiGraphics.drawWordWrap(
                this.font,
                Component.translatable("screen.skyresources3.guide.no_results"),
                contentX,
                panelY + Math.max(72, panelHeight / 2 - this.font.lineHeight),
                contentWidth,
                MUTED_TEXT_COLOR
        );
    }

    private List<GuidePage> currentCategoryPages() {
        final List<String> categories = GuidePages.categories();
        if (categories.isEmpty()) {
            return List.of();
        }
        return GuidePages.pagesInCategory(categories.get(this.selectedCategoryIndex));
    }

    private List<GuidePage> visiblePages() {
        final String query = this.normalizedSearch();
        return this.currentCategoryPages().stream()
                .filter(page -> query.isEmpty() || this.matchesSearch(page, query))
                .toList();
    }

    private boolean matchesSearch(final GuidePage page, final String query) {
        return page.id().toLowerCase(Locale.ROOT).contains(query)
                || page.title().getString().toLowerCase(Locale.ROOT).contains(query);
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
        final List<GuidePage> pages = this.visiblePages();
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
        final List<GuidePage> pages = this.visiblePages();
        this.selectedPageIndex = pages.isEmpty() ? 0 : Math.floorMod(this.selectedPageIndex, pages.size());
    }

    private int panelX() {
        return (this.width - this.panelWidth()) / 2;
    }

    private int panelY() {
        return (this.height - this.panelHeight()) / 2;
    }

    private int panelWidth() {
        return Math.min(PANEL_MAX_WIDTH, Math.max(180, this.width - 24));
    }

    private int panelHeight() {
        return Math.min(PANEL_MAX_HEIGHT, Math.max(140, this.height - 36));
    }

    private boolean selectResultAt(final double mouseX, final double mouseY) {
        final int panelX = this.panelX();
        final int panelY = this.panelY();
        final int panelWidth = this.panelWidth();
        final int panelHeight = this.panelHeight();
        if (!this.hasWideIndex(panelWidth)) {
            return false;
        }

        final int listX = this.resultListX(panelX);
        final int listY = this.resultListY(panelY);
        final int listWidth = this.indexWidth(panelWidth);
        final int listHeight = this.resultListBottom(panelY, panelHeight) - listY;
        if (!this.isInside((int) mouseX, (int) mouseY, listX, listY, listWidth, listHeight)) {
            return false;
        }

        final int index = ((int) mouseY - listY) / RESULT_ROW_HEIGHT;
        final List<GuidePage> pages = this.visiblePages();
        if (index < 0 || index >= Math.min(this.visibleResultRows(panelY, panelHeight), pages.size())) {
            return false;
        }
        this.selectedPageIndex = index;
        return true;
    }

    private int indexWidth(final int panelWidth) {
        return Math.min(INDEX_MAX_WIDTH, Math.max(INDEX_MIN_WIDTH, panelWidth / 3));
    }

    private boolean hasWideIndex(final int panelWidth) {
        return panelWidth >= 340;
    }

    private int resultListX(final int panelX) {
        return panelX + PANEL_PADDING;
    }

    private int resultListY(final int panelY) {
        return panelY + 64;
    }

    private int resultListBottom(final int panelY, final int panelHeight) {
        return panelY + panelHeight - FOOTER_HEIGHT;
    }

    private int visibleResultRows(final int panelY, final int panelHeight) {
        return Math.max(0, (this.resultListBottom(panelY, panelHeight) - this.resultListY(panelY)) / RESULT_ROW_HEIGHT);
    }

    private String normalizedSearch() {
        return this.searchText.trim().toLowerCase(Locale.ROOT);
    }

    private String truncate(final String text, final int width) {
        if (this.font.width(text) <= width) {
            return text;
        }
        final String ellipsis = "...";
        return this.font.plainSubstrByWidth(text, Math.max(0, width - this.font.width(ellipsis))) + ellipsis;
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
