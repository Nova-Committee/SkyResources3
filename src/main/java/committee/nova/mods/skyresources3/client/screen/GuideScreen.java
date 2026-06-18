package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.client.utils.GuideRecipeIntegration;
import committee.nova.mods.skyresources3.core.guide.GuideAction;
import committee.nova.mods.skyresources3.core.guide.GuidePage;
import committee.nova.mods.skyresources3.core.guide.GuidePages;
import committee.nova.mods.skyresources3.core.guide.GuideStructure;
import committee.nova.mods.skyresources3.core.guide.GuideStructures;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class GuideScreen extends Screen {
    private static final int PANEL_MAX_WIDTH = 680;
    private static final int PANEL_MAX_HEIGHT = 380;
    private static final int PANEL_PADDING = 14;
    private static final int FOOTER_HEIGHT = 34;
    private static final int BUTTON_SIZE = 20;
    private static final int SMALL_BUTTON_WIDTH = 24;
    private static final int CLOSE_BUTTON_WIDTH = 60;
    private static final int SEARCH_HEIGHT = 18;
    private static final int INDEX_MIN_WIDTH = 132;
    private static final int INDEX_MAX_WIDTH = 176;
    private static final int INDEX_GAP = 12;
    private static final int RESULT_ROW_HEIGHT = 22;
    private static final int ACTION_MAX_ROWS = 4;
    private static final int ACTION_ROW_HEIGHT = 22;
    private static final int ACTION_GAP = 6;
    private static final int INLINE_ACTION_GAP = 4;
    private static final int ICON_SIZE = 16;
    private static final int BACKGROUND_COLOR = 0xC0101010;
    private static final int PANEL_COLOR = 0xF0181B21;
    private static final int HEADER_COLOR = 0xF02A3038;
    private static final int SIDEBAR_COLOR = 0xE0212730;
    private static final int CONTENT_COLOR = 0xF0E9DDC4;
    private static final int BORDER_COLOR = 0xFF6E7E89;
    private static final int SELECTED_ROW_COLOR = 0x703E6F78;
    private static final int HOVERED_ROW_COLOR = 0x503E6F78;
    private static final int TEXT_COLOR = 0xFF2B241A;
    private static final int HEADER_TEXT_COLOR = 0xFFEFE5CF;
    private static final int MUTED_TEXT_COLOR = 0xFF675E52;
    private static final int SIDEBAR_TEXT_COLOR = 0xFFE5DAC6;
    private static final int SIDEBAR_MUTED_TEXT_COLOR = 0xFFB6C7C2;
    private static final int SCROLL_TRACK_COLOR = 0x303E6F78;
    private static final int SCROLL_THUMB_COLOR = 0xB05B8A8F;

    private int selectedCategoryIndex;
    private int selectedPageIndex;
    private int resultScrollOffset;
    private int actionScrollOffset;
    private int bodyScrollOffset;
    private String searchText = "";
    private EditBox searchBox;
    private GuideStructure currentStructure;
    private Component feedbackMessage;
    private final List<InlineActionRegion> inlineActionRegions = new ArrayList<>();
    private final GuideStructurePonderView structurePonderView = new GuideStructurePonderView();

    public GuideScreen() {
        super(Component.translatable("screen.skyresources.guide.title"));
    }

    @Override
    protected void init() {
        final int panelX = this.panelX();
        final int panelY = this.panelY();
        final int panelWidth = this.panelWidth();
        final int footerY = panelY + this.panelHeight() - 28;
        final boolean wideIndex = this.hasWideIndex(panelWidth);
        final int searchX = panelX + PANEL_PADDING;
        final int searchY = panelY + 26;
        final int searchWidth = wideIndex ? this.indexWidth(panelWidth) : panelWidth - PANEL_PADDING * 2;
        final String currentSearch = this.searchBox == null ? this.searchText : this.searchBox.getValue();

        this.searchBox = new EditBox(
                this.font,
                searchX,
                searchY,
                searchWidth,
                SEARCH_HEIGHT,
                Component.translatable("screen.skyresources.guide.search")
        );
        this.searchBox.setMaxLength(40);
        this.searchBox.setHint(Component.translatable("screen.skyresources.guide.search_hint"));
        this.searchBox.setValue(currentSearch);
        this.searchBox.setResponder(value -> {
            this.searchText = value;
            this.selectedPageIndex = 0;
            this.resultScrollOffset = 0;
            this.actionScrollOffset = 0;
            this.bodyScrollOffset = 0;
            this.clearTransientView();
            this.clampSelection();
        });
        this.addRenderableWidget(this.searchBox);
        this.searchBox.setFocused(true);
        this.setFocused(this.searchBox);

        this.addRenderableWidget(SkyResourcesButton.create(
                Component.literal("<"),
                button -> this.changeCategory(-1),
                panelX + PANEL_PADDING,
                footerY,
                SMALL_BUTTON_WIDTH,
                BUTTON_SIZE,
                Tooltip.create(Component.translatable("button.skyresources.guide.previous_category")),
                SkyResourcesButton.Tone.QUIET
        ));
        this.addRenderableWidget(SkyResourcesButton.create(
                Component.literal(">"),
                button -> this.changeCategory(1),
                panelX + PANEL_PADDING + SMALL_BUTTON_WIDTH + 4,
                footerY,
                SMALL_BUTTON_WIDTH,
                BUTTON_SIZE,
                Tooltip.create(Component.translatable("button.skyresources.guide.next_category")),
                SkyResourcesButton.Tone.QUIET
        ));
        this.addRenderableWidget(SkyResourcesButton.create(
                Component.literal("<"),
                button -> this.changePage(-1),
                panelX + 84,
                footerY,
                SMALL_BUTTON_WIDTH,
                BUTTON_SIZE,
                Tooltip.create(Component.translatable("button.skyresources.guide.previous_page")),
                SkyResourcesButton.Tone.QUIET
        ));
        this.addRenderableWidget(SkyResourcesButton.create(
                Component.literal(">"),
                button -> this.changePage(1),
                panelX + 84 + SMALL_BUTTON_WIDTH + 4,
                footerY,
                SMALL_BUTTON_WIDTH,
                BUTTON_SIZE,
                Tooltip.create(Component.translatable("button.skyresources.guide.next_page")),
                SkyResourcesButton.Tone.QUIET
        ));
        this.addRenderableWidget(SkyResourcesButton.create(
                Component.translatable("gui.done"),
                button -> this.closeCurrentView(),
                panelX + panelWidth - PANEL_PADDING - CLOSE_BUTTON_WIDTH,
                footerY,
                CLOSE_BUTTON_WIDTH,
                BUTTON_SIZE,
                null,
                SkyResourcesButton.Tone.DEFAULT
        ));
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        guiGraphics.fill(0, 0, this.width, this.height, BACKGROUND_COLOR);

        if (this.currentStructure != null) {
            this.structurePonderView.render(
                    guiGraphics,
                    this.font,
                    mouseX,
                    mouseY,
                    0,
                    0,
                    this.width,
                    this.height,
                    this.currentStructure
            );
            return;
        }

        final int panelX = this.panelX();
        final int panelY = this.panelY();
        final int panelWidth = this.panelWidth();
        final int panelHeight = this.panelHeight();
        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, PANEL_COLOR);
        guiGraphics.renderOutline(panelX, panelY, panelWidth, panelHeight, BORDER_COLOR);
        guiGraphics.fill(panelX + 1, panelY + 1, panelX + panelWidth - 1, panelY + 26, HEADER_COLOR);
        this.drawCenteredTruncatedString(
                guiGraphics,
                this.title,
                panelX + panelWidth / 2,
                panelY + 8,
                panelWidth - PANEL_PADDING * 2,
                HEADER_TEXT_COLOR
        );

        this.renderPage(guiGraphics, mouseX, mouseY, panelX, panelY, panelWidth, panelHeight);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (this.currentStructure != null) {
            if (button != 0) {
                return false;
            }
            return switch (this.structurePonderView.mouseClicked(
                    mouseX,
                    mouseY,
                    0,
                    0,
                    this.width,
                    this.height,
                    this.currentStructure
            )) {
                case BACK -> {
                    this.clearTransientView();
                    yield true;
                }
                case HANDLED -> true;
                case NONE -> false;
            };
        }
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button != 0) {
            return false;
        }
        return this.selectInlineActionAt(mouseX, mouseY)
                || this.selectActionAt(mouseX, mouseY)
                || this.selectResultAt(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        if (this.currentStructure != null) {
            return this.structurePonderView.mouseReleased(button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(
            final double mouseX,
            final double mouseY,
            final int button,
            final double dragX,
            final double dragY
    ) {
        if (this.currentStructure != null) {
            return this.structurePonderView.mouseDragged(mouseX, mouseY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(
            final double mouseX,
            final double mouseY,
            final double scrollX,
            final double scrollY
    ) {
        if (this.currentStructure != null) {
            if (scrollY == 0.0D) {
                return false;
            }
            return this.structurePonderView.mouseScrolled(
                    mouseX,
                    mouseY,
                    0,
                    0,
                    this.width,
                    this.height
            );
        }
        if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }
        if (scrollY == 0.0D) {
            return false;
        }
        return this.scrollActionListAt(mouseX, mouseY, scrollY)
                || this.scrollGuideBodyAt(mouseX, mouseY, scrollY)
                || this.scrollResultIndexAt(mouseX, mouseY, scrollY);
    }

    @Override
    public void onClose() {
        if (this.currentStructure != null) {
            this.clearTransientView();
            return;
        }
        super.onClose();
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
        this.inlineActionRegions.clear();

        final List<String> categories = GuidePages.categories();
        if (categories.isEmpty()) {
            this.drawCenteredString(
                    guiGraphics,
                    Component.translatable("screen.skyresources.guide.no_pages"),
                    panelX + panelWidth / 2,
                    panelY + panelHeight / 2 - this.font.lineHeight,
                    MUTED_TEXT_COLOR
            );
            return;
        }

        final List<GuidePage> categoryPages = this.currentCategoryPages();
        if (categoryPages.isEmpty()) {
            this.drawCenteredString(
                    guiGraphics,
                    Component.translatable("screen.skyresources.guide.no_pages"),
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
                this.isSearching() ? GuidePages.pages().size() : categoryPages.size(),
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
        final int contentY = this.contentTop(panelWidth, panelY);
        final int contentWidth = wideIndex
                ? panelWidth - PANEL_PADDING * 2 - this.indexWidth(panelWidth) - INDEX_GAP
                : panelWidth - PANEL_PADDING * 2;
        final int contentBottom = panelY + panelHeight - FOOTER_HEIGHT;
        guiGraphics.fill(
                contentX - 6,
                contentY - 6,
                contentX + contentWidth + 6,
                contentBottom,
                CONTENT_COLOR
        );
        guiGraphics.renderOutline(contentX - 6, contentY - 6, contentWidth + 12, contentBottom - contentY + 6, BORDER_COLOR);
        final Component category = Component.translatable(
                "screen.skyresources.guide.category_counter",
                page.category(),
                this.categoryIndex(page.categoryKey()) + 1,
                categories.size()
        );
        final Component pageCounter = Component.translatable(
                "screen.skyresources.guide.page_counter",
                this.selectedPageIndex + 1,
                pages.size()
        );

        final String fittedPageCounter = this.truncate(pageCounter.getString(), Math.max(24, contentWidth / 3));
        guiGraphics.drawString(
                this.font,
                this.truncate(category.getString(), Math.max(24, contentWidth - this.font.width(fittedPageCounter) - 8)),
                contentX,
                contentY,
                MUTED_TEXT_COLOR,
                false
        );
        guiGraphics.drawString(
                this.font,
                fittedPageCounter,
                contentX + contentWidth - this.font.width(fittedPageCounter),
                contentY,
                MUTED_TEXT_COLOR,
                false
        );

        final int iconY = contentY + 20;
        final ItemStack icon = page.icon();
        if (!icon.isEmpty()) {
            guiGraphics.renderFakeItem(icon, contentX, iconY);
            guiGraphics.renderItemDecorations(this.font, icon, contentX, iconY);
            if (this.isInside(mouseX, mouseY, contentX, iconY, ICON_SIZE, ICON_SIZE)) {
                guiGraphics.renderTooltip(this.font, icon, mouseX, mouseY);
            }
        }

        guiGraphics.drawString(
                this.font,
                this.truncate(page.title().getString(), contentWidth - ICON_SIZE - 8),
                contentX + ICON_SIZE + 8,
                iconY + 4,
                TEXT_COLOR,
                false
        );

        final int bodyY = iconY + 26;
        final int bodyBottom = contentBottom - 4;
        if (bodyBottom <= bodyY) {
            return;
        }
        final int actionRows = this.visibleActionRows(page.actions(), this.actionAvailableHeight(panelY, panelHeight));
        final int actionTop = actionRows == 0 ? bodyBottom : this.actionTop(panelY, panelHeight, actionRows);
        int textBottom = actionRows == 0 ? bodyBottom : actionTop - ACTION_GAP;
        if (this.feedbackMessage != null && textBottom - bodyY > this.font.lineHeight + 2) {
            textBottom -= this.font.lineHeight + 2;
            guiGraphics.drawWordWrap(
                    this.font,
                    this.feedbackMessage,
                    contentX,
                    textBottom,
                    contentWidth,
                    MUTED_TEXT_COLOR
            );
        }
        if (textBottom > bodyY) {
            final int bodyWidth = Math.max(24, contentWidth - 8);
            final int visibleRows = this.visibleBodyRows(bodyY, textBottom);
            final int totalRows = this.measureGuideBodyRows(page, bodyWidth);
            this.bodyScrollOffset = this.clampScrollOffset(this.bodyScrollOffset, totalRows, visibleRows);
            this.renderGuideBody(guiGraphics, mouseX, mouseY, page, contentX, bodyY, bodyWidth, textBottom);
            this.renderScrollBar(
                    guiGraphics,
                    contentX + contentWidth - 4,
                    bodyY,
                    3,
                    textBottom - bodyY,
                    totalRows,
                    visibleRows,
                    this.bodyScrollOffset
            );
        }
        if (actionRows > 0) {
            this.renderActions(guiGraphics, mouseX, mouseY, page.actions(), contentX, actionTop, contentWidth, actionRows);
        }
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
        final int sidebarBottom = panelY + panelHeight - FOOTER_HEIGHT;
        final int sidebarWidth = this.hasWideIndex(panelWidth) ? this.indexWidth(panelWidth) : panelWidth - PANEL_PADDING * 2;
        guiGraphics.fill(x - 6, y - 4, x + sidebarWidth + 6, sidebarBottom, SIDEBAR_COLOR);
        guiGraphics.renderOutline(x - 6, y - 4, sidebarWidth + 12, sidebarBottom - y + 4, 0x80515F66);
        final Component resultCount = Component.translatable(
                "screen.skyresources.guide.result_count",
                pages.size(),
                categoryPageCount
        );
        guiGraphics.drawString(
                this.font,
                this.truncate(resultCount.getString(), sidebarWidth),
                x,
                y,
                SIDEBAR_MUTED_TEXT_COLOR,
                false
        );

        if (!this.hasWideIndex(panelWidth)) {
            return;
        }

        final int listX = this.resultListX(panelX);
        final int listY = this.resultListY(panelY);
        final int listWidth = this.indexWidth(panelWidth);
        final int listBottom = this.resultListBottom(panelY, panelHeight);
        final int visibleRows = this.visibleResultRows(panelY, panelHeight);
        final int firstIndex = this.resultScrollOffset;
        final int rowCount = Math.min(visibleRows, Math.max(0, pages.size() - firstIndex));
        guiGraphics.enableScissor(listX, listY, listX + listWidth, listBottom);
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            final int pageIndex = firstIndex + rowIndex;
            this.renderResultRow(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    pages.get(pageIndex),
                    pageIndex,
                    rowIndex,
                    listX,
                    listY,
                    listWidth,
                    this.isSearching()
            );
        }
        guiGraphics.disableScissor();
        this.renderScrollBar(
                guiGraphics,
                listX + listWidth - 4,
                listY,
                3,
                listBottom - listY,
                pages.size(),
                visibleRows,
                this.resultScrollOffset
        );
    }

    private void renderResultRow(
            final GuiGraphics guiGraphics,
            final int mouseX,
            final int mouseY,
            final GuidePage page,
            final int pageIndex,
            final int rowIndex,
            final int listX,
            final int listY,
            final int listWidth,
            final boolean showCategory
    ) {
        final int rowY = listY + rowIndex * RESULT_ROW_HEIGHT;
        if (pageIndex == this.selectedPageIndex) {
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
        if (showCategory) {
            guiGraphics.drawString(
                    this.font,
                    this.truncate(page.title().getString(), titleWidth),
                    titleX,
                    rowY + 1,
                    SIDEBAR_TEXT_COLOR,
                    false
            );
            guiGraphics.drawString(
                    this.font,
                    this.truncate(page.category().getString(), titleWidth),
                    titleX,
                    rowY + 9,
                    SIDEBAR_MUTED_TEXT_COLOR,
                    false
            );
        } else {
            guiGraphics.drawString(
                    this.font,
                    this.truncate(page.title().getString(), titleWidth),
                    titleX,
                    rowY + 5,
                    SIDEBAR_TEXT_COLOR,
                    false
            );
        }
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
                Component.translatable("screen.skyresources.guide.no_results"),
                contentX,
                panelY + Math.max(72, panelHeight / 2 - this.font.lineHeight),
                contentWidth,
                MUTED_TEXT_COLOR
        );
    }

    private void renderGuideBody(
            final GuiGraphics guiGraphics,
            final int mouseX,
            final int mouseY,
            final GuidePage page,
            final int x,
            final int y,
            final int width,
            final int bottom
    ) {
        final BodyCursor cursor = new BodyCursor(x, y - this.bodyScrollOffset * ACTION_ROW_HEIGHT, width, y, bottom);
        final String text = page.text().getString();
        int index = 0;
        guiGraphics.enableScissor(x, y, x + width, bottom);
        while (index < text.length() && cursor.canRender()) {
            if (text.charAt(index) == '\n') {
                cursor.newLine();
                index++;
            } else if (text.startsWith("{action:", index)) {
                final int markerEnd = text.indexOf('}', index);
                if (markerEnd > index) {
                    final boolean rendered = this.renderInlineActionMarker(
                            guiGraphics,
                            mouseX,
                            mouseY,
                            page,
                            text.substring(index + 8, markerEnd),
                            cursor
                    );
                    if (!rendered) {
                        this.renderInlineText(guiGraphics, text.substring(index, markerEnd + 1), cursor);
                    }
                    index = markerEnd + 1;
                } else {
                    this.renderInlineText(guiGraphics, String.valueOf(text.charAt(index)), cursor);
                    index++;
                }
            } else if (Character.isWhitespace(text.charAt(index))) {
                this.renderInlineText(guiGraphics, " ", cursor);
                index++;
            } else {
                final int tokenEnd = this.nextInlineTokenEnd(text, index);
                this.renderInlineText(guiGraphics, text.substring(index, tokenEnd), cursor);
                index = tokenEnd;
            }
        }
        guiGraphics.disableScissor();
    }

    private int measureGuideBodyRows(final GuidePage page, final int width) {
        final BodyCursor cursor = new BodyCursor(0, 0, width, 0, Integer.MAX_VALUE / 2);
        final String text = page.text().getString();
        int index = 0;
        while (index < text.length()) {
            if (text.charAt(index) == '\n') {
                cursor.newLine();
                index++;
            } else if (text.startsWith("{action:", index)) {
                final int markerEnd = text.indexOf('}', index);
                if (markerEnd > index) {
                    final boolean measured = this.measureInlineActionMarker(
                            page,
                            text.substring(index + 8, markerEnd),
                            cursor
                    );
                    if (!measured) {
                        this.measureInlineText(text.substring(index, markerEnd + 1), cursor);
                    }
                    index = markerEnd + 1;
                } else {
                    this.measureInlineText(String.valueOf(text.charAt(index)), cursor);
                    index++;
                }
            } else if (Character.isWhitespace(text.charAt(index))) {
                this.measureInlineText(" ", cursor);
                index++;
            } else {
                final int tokenEnd = this.nextInlineTokenEnd(text, index);
                this.measureInlineText(text.substring(index, tokenEnd), cursor);
                index = tokenEnd;
            }
        }
        return Math.max(1, (cursor.y / ACTION_ROW_HEIGHT) + 1);
    }

    private void drawCenteredString(
            final GuiGraphics guiGraphics,
            final Component text,
            final int centerX,
            final int y,
            final int color
    ) {
        guiGraphics.drawString(this.font, text, centerX - this.font.width(text) / 2, y, color, false);
    }

    private void drawCenteredTruncatedString(
            final GuiGraphics guiGraphics,
            final Component text,
            final int centerX,
            final int y,
            final int width,
            final int color
    ) {
        final String fitted = this.truncate(text.getString(), width);
        guiGraphics.drawString(this.font, fitted, centerX - this.font.width(fitted) / 2, y, color, false);
    }

    private boolean measureInlineActionMarker(
            final GuidePage page,
            final String markerIndex,
            final BodyCursor cursor
    ) {
        final int actionIndex;
        try {
            actionIndex = Integer.parseInt(markerIndex.trim()) - 1;
        } catch (final NumberFormatException ignored) {
            return false;
        }
        if (actionIndex < 0 || actionIndex >= page.actions().size()) {
            return false;
        }
        this.measureInlineAction(page.actions().get(actionIndex), cursor);
        return true;
    }

    private boolean renderInlineActionMarker(
            final GuiGraphics guiGraphics,
            final int mouseX,
            final int mouseY,
            final GuidePage page,
            final String markerIndex,
            final BodyCursor cursor
    ) {
        final int actionIndex;
        try {
            actionIndex = Integer.parseInt(markerIndex.trim()) - 1;
        } catch (final NumberFormatException ignored) {
            return false;
        }
        if (actionIndex < 0 || actionIndex >= page.actions().size()) {
            return false;
        }
        this.renderInlineAction(guiGraphics, mouseX, mouseY, page.actions().get(actionIndex), cursor);
        return true;
    }

    private void measureInlineText(final String text, final BodyCursor cursor) {
        if (text.isBlank() && cursor.x == cursor.startX) {
            return;
        }
        String remaining = text;
        while (!remaining.isEmpty()) {
            final int textWidth = this.font.width(remaining);
            if (cursor.x > cursor.startX && cursor.x + textWidth > cursor.rightX()) {
                cursor.newLine();
                if (remaining.isBlank()) {
                    return;
                }
                continue;
            }
            final int availableWidth = cursor.rightX() - cursor.x;
            if (textWidth <= availableWidth) {
                cursor.x += textWidth;
                return;
            }

            String line = this.font.plainSubstrByWidth(remaining, Math.max(1, availableWidth));
            if (line.isEmpty()) {
                line = remaining.substring(0, remaining.offsetByCodePoints(0, 1));
            }
            cursor.x += this.font.width(line);
            remaining = remaining.substring(line.length());
            cursor.newLine();
        }
    }

    private void renderInlineText(final GuiGraphics guiGraphics, final String text, final BodyCursor cursor) {
        if (text.isBlank() && cursor.x == cursor.startX) {
            return;
        }
        String remaining = text;
        while (!remaining.isEmpty() && cursor.canRender()) {
            final int textWidth = this.font.width(remaining);
            if (cursor.x > cursor.startX && cursor.x + textWidth > cursor.rightX()) {
                cursor.newLine();
                if (remaining.isBlank()) {
                    return;
                }
                continue;
            }
            final int availableWidth = cursor.rightX() - cursor.x;
            if (textWidth <= availableWidth) {
                this.drawInlineTextLine(guiGraphics, remaining, cursor, textWidth);
                return;
            }

            String line = this.font.plainSubstrByWidth(remaining, Math.max(1, availableWidth));
            if (line.isEmpty()) {
                line = remaining.substring(0, remaining.offsetByCodePoints(0, 1));
            }
            this.drawInlineTextLine(guiGraphics, line, cursor, this.font.width(line));
            remaining = remaining.substring(line.length());
            cursor.newLine();
        }
    }

    private void drawInlineTextLine(
            final GuiGraphics guiGraphics,
            final String text,
            final BodyCursor cursor,
            final int textWidth
    ) {
        guiGraphics.drawString(this.font, text, cursor.x, cursor.y + 5, TEXT_COLOR, false);
        cursor.x += textWidth;
    }

    private void renderInlineAction(
            final GuiGraphics guiGraphics,
            final int mouseX,
            final int mouseY,
            final GuideAction action,
            final BodyCursor cursor
    ) {
        final Component label = action.label();
        final int maxLabelWidth = Math.max(16, cursor.width - ICON_SIZE - 14);
        final int chipWidth = this.inlineActionWidth(label, cursor.width, maxLabelWidth);
        if (cursor.x > cursor.startX && cursor.x + chipWidth > cursor.rightX()) {
            cursor.newLine();
        }
        if (!cursor.canRender()) {
            return;
        }

        final boolean hovered = this.isInside(mouseX, mouseY, cursor.x, cursor.y, chipWidth, ACTION_ROW_HEIGHT - 1);
        SkyResourcesButton.renderFrame(
                guiGraphics,
                cursor.x,
                cursor.y,
                chipWidth,
                ACTION_ROW_HEIGHT - 1,
                true,
                hovered,
                1.0F,
                SkyResourcesButton.Tone.DEFAULT
        );
        final ItemStack icon = action.icon();
        if (!icon.isEmpty()) {
            guiGraphics.renderFakeItem(icon, cursor.x + 2, cursor.y + 2);
        }
        guiGraphics.drawString(
                this.font,
                this.truncate(label.getString(), maxLabelWidth),
                cursor.x + ICON_SIZE + 6,
                cursor.y + 6,
                SkyResourcesButton.textColor(SkyResourcesButton.Tone.DEFAULT, true, 1.0F),
                false
        );
        if (cursor.isVisible()) {
            this.inlineActionRegions.add(new InlineActionRegion(cursor.x, cursor.y, chipWidth, ACTION_ROW_HEIGHT - 1, action));
        }
        if (hovered && cursor.isVisible()) {
            guiGraphics.renderTooltip(this.font, this.actionTooltip(action), mouseX, mouseY);
        }
        cursor.x += chipWidth + INLINE_ACTION_GAP;
    }

    private void measureInlineAction(final GuideAction action, final BodyCursor cursor) {
        final Component label = action.label();
        final int maxLabelWidth = Math.max(16, cursor.width - ICON_SIZE - 14);
        final int chipWidth = this.inlineActionWidth(label, cursor.width, maxLabelWidth);
        if (cursor.x > cursor.startX && cursor.x + chipWidth > cursor.rightX()) {
            cursor.newLine();
        }
        cursor.x += chipWidth + INLINE_ACTION_GAP;
    }

    private int inlineActionWidth(final Component label, final int width, final int maxLabelWidth) {
        return Math.min(
                width,
                ICON_SIZE + 10 + this.font.width(this.truncate(label.getString(), maxLabelWidth))
        );
    }

    private void renderActions(
            final GuiGraphics guiGraphics,
            final int mouseX,
            final int mouseY,
            final List<GuideAction> actions,
            final int x,
            final int y,
            final int width,
            final int visibleRows
    ) {
        final int firstIndex = this.actionScrollOffset;
        final int rowCount = Math.min(visibleRows, Math.max(0, actions.size() - firstIndex));
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            this.renderActionRow(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    actions.get(firstIndex + rowIndex),
                    x,
                    y + rowIndex * ACTION_ROW_HEIGHT,
                    width
            );
        }
    }

    private void renderActionRow(
            final GuiGraphics guiGraphics,
            final int mouseX,
            final int mouseY,
            final GuideAction action,
            final int x,
            final int y,
            final int width
    ) {
        final boolean hovered = this.isInside(mouseX, mouseY, x, y, width, ACTION_ROW_HEIGHT - 1);
        SkyResourcesButton.renderFrame(
                guiGraphics,
                x,
                y,
                width,
                ACTION_ROW_HEIGHT - 1,
                true,
                hovered,
                1.0F,
                SkyResourcesButton.Tone.DEFAULT
        );
        final ItemStack icon = action.icon();
        if (!icon.isEmpty()) {
            guiGraphics.renderFakeItem(icon, x + 2, y + 2);
        }

        final Component type = Component.translatable(
                "screen.skyresources.guide.action_type." + action.type().name().toLowerCase(Locale.ROOT)
        );
        final int typeWidth = this.font.width(type);
        final int labelX = x + ICON_SIZE + 6;
        final int labelWidth = Math.max(16, width - ICON_SIZE - typeWidth - 14);
        guiGraphics.drawString(
                this.font,
                this.truncate(action.label().getString(), labelWidth),
                labelX,
                y + 6,
                SkyResourcesButton.textColor(SkyResourcesButton.Tone.DEFAULT, true, 1.0F),
                false
        );
        guiGraphics.drawString(
                this.font,
                type,
                x + width - typeWidth - 4,
                y + 6,
                SkyResourcesButton.mutedTextColor(1.0F),
                false
        );
        if (hovered) {
            guiGraphics.renderTooltip(this.font, this.actionTooltip(action), mouseX, mouseY);
        }
    }

    private List<GuidePage> currentCategoryPages() {
        final List<String> categories = GuidePages.categories();
        if (categories.isEmpty()) {
            return List.of();
        }
        return GuidePages.pagesInCategory(categories.get(this.selectedCategoryIndex));
    }

    private int categoryIndex(final String categoryKey) {
        return Math.max(0, GuidePages.categories().indexOf(categoryKey));
    }

    private List<GuidePage> visiblePages() {
        final String query = this.normalizedSearch();
        final List<GuidePage> sourcePages = query.isEmpty() ? this.currentCategoryPages() : GuidePages.pages();
        return sourcePages.stream()
                .filter(page -> query.isEmpty() || this.matchesSearch(page, query))
                .toList();
    }

    private boolean matchesSearch(final GuidePage page, final String query) {
        final String searchableText = this.stripInlineActionMarkers(page.text().getString()).toLowerCase(Locale.ROOT);
        return page.id().toLowerCase(Locale.ROOT).contains(query)
                || page.title().getString().toLowerCase(Locale.ROOT).contains(query)
                || searchableText.contains(query);
    }

    private void changeCategory(final int direction) {
        final List<String> categories = GuidePages.categories();
        if (categories.isEmpty()) {
            return;
        }
        this.clearTransientView();
        this.selectedCategoryIndex = Math.floorMod(this.selectedCategoryIndex + direction, categories.size());
        this.selectedPageIndex = 0;
        this.resultScrollOffset = 0;
        this.actionScrollOffset = 0;
        this.bodyScrollOffset = 0;
        this.clampSelection();
    }

    private void changePage(final int direction) {
        final List<GuidePage> pages = this.visiblePages();
        if (pages.isEmpty()) {
            return;
        }
        this.clearTransientView();
        this.selectedPageIndex = Math.floorMod(this.selectedPageIndex + direction, pages.size());
        this.actionScrollOffset = 0;
        this.bodyScrollOffset = 0;
        this.clampSelection();
        this.keepSelectedResultVisible();
    }

    private void clampSelection() {
        final List<String> categories = GuidePages.categories();
        if (categories.isEmpty()) {
            this.selectedCategoryIndex = 0;
            this.selectedPageIndex = 0;
            this.resultScrollOffset = 0;
            this.actionScrollOffset = 0;
            this.bodyScrollOffset = 0;
            return;
        }

        this.selectedCategoryIndex = Math.floorMod(this.selectedCategoryIndex, categories.size());
        final List<GuidePage> pages = this.visiblePages();
        this.selectedPageIndex = pages.isEmpty() ? 0 : Math.floorMod(this.selectedPageIndex, pages.size());
        final int panelY = this.panelY();
        final int panelHeight = this.panelHeight();
        final int resultRows = this.visibleResultRows(panelY, panelHeight);
        this.resultScrollOffset = this.clampScrollOffset(this.resultScrollOffset, pages.size(), resultRows);

        final GuidePage page = pages.isEmpty() ? null : pages.get(this.selectedPageIndex);
        final int actionRows = page == null
                ? 0
                : this.visibleActionRows(page.actions(), this.actionAvailableHeight(panelY, panelHeight));
        final int actionCount = page == null ? 0 : page.actions().size();
        this.actionScrollOffset = this.clampScrollOffset(this.actionScrollOffset, actionCount, actionRows);
        if (page == null) {
            this.bodyScrollOffset = 0;
        } else {
            final int panelWidth = this.panelWidth();
            final int contentWidth = this.hasWideIndex(panelWidth)
                    ? panelWidth - PANEL_PADDING * 2 - this.indexWidth(panelWidth) - INDEX_GAP
                    : panelWidth - PANEL_PADDING * 2;
            final int bodyY = this.contentTop(panelWidth, panelY) + 20 + 26;
            final int bodyBottom = this.bodyBottomFor(page, panelY, panelHeight);
            final int visibleRows = this.visibleBodyRows(bodyY, bodyBottom);
            final int totalRows = this.measureGuideBodyRows(page, Math.max(24, contentWidth - 8));
            this.bodyScrollOffset = this.clampScrollOffset(this.bodyScrollOffset, totalRows, visibleRows);
        }
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

    private int contentTop(final int panelWidth, final int panelY) {
        return this.hasWideIndex(panelWidth) ? panelY + 54 : panelY + 70;
    }

    private boolean selectInlineActionAt(final double mouseX, final double mouseY) {
        for (final InlineActionRegion region : this.inlineActionRegions) {
            if (this.isInside((int) mouseX, (int) mouseY, region.x, region.y, region.width, region.height)) {
                return this.handleAction(region.action);
            }
        }
        return false;
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

        final int rowIndex = ((int) mouseY - listY) / RESULT_ROW_HEIGHT;
        final int pageIndex = this.resultScrollOffset + rowIndex;
        final List<GuidePage> pages = this.visiblePages();
        if (rowIndex < 0 || rowIndex >= this.visibleResultRows(panelY, panelHeight) || pageIndex >= pages.size()) {
            return false;
        }
        this.clearTransientView();
        this.selectedPageIndex = pageIndex;
        this.actionScrollOffset = 0;
        this.bodyScrollOffset = 0;
        return true;
    }

    private boolean selectActionAt(final double mouseX, final double mouseY) {
        if (this.currentStructure != null) {
            return false;
        }
        final GuidePage page = this.selectedPage();
        if (page == null || page.actions().isEmpty()) {
            return false;
        }

        final int panelX = this.panelX();
        final int panelY = this.panelY();
        final int panelWidth = this.panelWidth();
        final int panelHeight = this.panelHeight();
        final boolean wideIndex = this.hasWideIndex(panelWidth);
        final int contentX = wideIndex
                ? panelX + PANEL_PADDING + this.indexWidth(panelWidth) + INDEX_GAP
                : panelX + PANEL_PADDING;
        final int contentWidth = wideIndex
                ? panelWidth - PANEL_PADDING * 2 - this.indexWidth(panelWidth) - INDEX_GAP
                : panelWidth - PANEL_PADDING * 2;
        final int visibleRows = this.visibleActionRows(page.actions(), this.actionAvailableHeight(panelY, panelHeight));
        final int actionTop = this.actionTop(panelY, panelHeight, visibleRows);
        if (visibleRows <= 0 || !this.isInside((int) mouseX, (int) mouseY, contentX, actionTop, contentWidth, visibleRows * ACTION_ROW_HEIGHT)) {
            return false;
        }

        final int rowIndex = ((int) mouseY - actionTop) / ACTION_ROW_HEIGHT;
        final int actionIndex = this.actionScrollOffset + rowIndex;
        if (rowIndex < 0 || rowIndex >= visibleRows || actionIndex >= page.actions().size()) {
            return false;
        }
        return this.handleAction(page.actions().get(actionIndex));
    }

    private boolean scrollResultIndexAt(final double mouseX, final double mouseY, final double scrollY) {
        if (this.currentStructure != null) {
            return false;
        }
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

        final int visibleRows = this.visibleResultRows(panelY, panelHeight);
        final int oldOffset = this.resultScrollOffset;
        this.resultScrollOffset = this.scrollOffset(this.resultScrollOffset, this.visiblePages().size(), visibleRows, scrollY);
        return oldOffset != this.resultScrollOffset;
    }

    private boolean scrollActionListAt(final double mouseX, final double mouseY, final double scrollY) {
        if (this.currentStructure != null) {
            return false;
        }
        final GuidePage page = this.selectedPage();
        if (page == null || page.actions().isEmpty()) {
            return false;
        }

        final int panelX = this.panelX();
        final int panelY = this.panelY();
        final int panelWidth = this.panelWidth();
        final int panelHeight = this.panelHeight();
        final boolean wideIndex = this.hasWideIndex(panelWidth);
        final int contentX = wideIndex
                ? panelX + PANEL_PADDING + this.indexWidth(panelWidth) + INDEX_GAP
                : panelX + PANEL_PADDING;
        final int contentWidth = wideIndex
                ? panelWidth - PANEL_PADDING * 2 - this.indexWidth(panelWidth) - INDEX_GAP
                : panelWidth - PANEL_PADDING * 2;
        final int visibleRows = this.visibleActionRows(page.actions(), this.actionAvailableHeight(panelY, panelHeight));
        final int actionTop = this.actionTop(panelY, panelHeight, visibleRows);
        if (visibleRows <= 0 || !this.isInside((int) mouseX, (int) mouseY, contentX, actionTop, contentWidth, visibleRows * ACTION_ROW_HEIGHT)) {
            return false;
        }

        final int oldOffset = this.actionScrollOffset;
        this.actionScrollOffset = this.scrollOffset(this.actionScrollOffset, page.actions().size(), visibleRows, scrollY);
        return oldOffset != this.actionScrollOffset;
    }

    private boolean scrollGuideBodyAt(final double mouseX, final double mouseY, final double scrollY) {
        if (this.currentStructure != null) {
            return false;
        }
        final GuidePage page = this.selectedPage();
        if (page == null) {
            return false;
        }

        final int panelX = this.panelX();
        final int panelY = this.panelY();
        final int panelWidth = this.panelWidth();
        final int panelHeight = this.panelHeight();
        final boolean wideIndex = this.hasWideIndex(panelWidth);
        final int contentX = wideIndex
                ? panelX + PANEL_PADDING + this.indexWidth(panelWidth) + INDEX_GAP
                : panelX + PANEL_PADDING;
        final int contentWidth = wideIndex
                ? panelWidth - PANEL_PADDING * 2 - this.indexWidth(panelWidth) - INDEX_GAP
                : panelWidth - PANEL_PADDING * 2;
        final int bodyY = this.contentTop(panelWidth, panelY) + 20 + 26;
        final int bodyBottom = this.bodyBottomFor(page, panelY, panelHeight);
        if (bodyBottom <= bodyY || !this.isInside((int) mouseX, (int) mouseY, contentX, bodyY, contentWidth, bodyBottom - bodyY)) {
            return false;
        }

        final int visibleRows = this.visibleBodyRows(bodyY, bodyBottom);
        final int totalRows = this.measureGuideBodyRows(page, Math.max(24, contentWidth - 8));
        final int oldOffset = this.bodyScrollOffset;
        this.bodyScrollOffset = this.scrollOffset(this.bodyScrollOffset, totalRows, visibleRows, scrollY);
        return oldOffset != this.bodyScrollOffset;
    }

    private boolean handleAction(final GuideAction action) {
        return switch (action.type()) {
            case LINK -> this.openGuidePage(action.target());
            case IMAGE -> this.openStructure(action.target());
            case RECIPE -> {
                if (GuideRecipeIntegration.open(action)) {
                    this.feedbackMessage = null;
                } else {
                    this.feedbackMessage = Component.translatable("screen.skyresources.guide.recipe_unavailable", action.label());
                }
                yield true;
            }
        };
    }

    private boolean openGuidePage(final String pageId) {
        final GuidePage target = GuidePages.find(pageId).orElse(null);
        if (target == null) {
            this.feedbackMessage = Component.translatable("screen.skyresources.guide.missing_page", pageId);
            return true;
        }
        final List<String> categories = GuidePages.categories();
        final int categoryIndex = categories.indexOf(target.categoryKey());
        if (categoryIndex < 0) {
            this.feedbackMessage = Component.translatable("screen.skyresources.guide.missing_page", pageId);
            return true;
        }

        this.searchText = "";
        this.resultScrollOffset = 0;
        this.actionScrollOffset = 0;
        this.bodyScrollOffset = 0;
        if (this.searchBox != null) {
            this.searchBox.setValue("");
        }
        this.clearTransientView();
        this.selectedCategoryIndex = categoryIndex;
        final List<GuidePage> categoryPages = this.currentCategoryPages();
        this.selectedPageIndex = Math.max(0, categoryPages.indexOf(target));
        this.keepSelectedResultVisible();
        return true;
    }

    private boolean openStructure(final String structureId) {
        this.currentStructure = GuideStructures.find(structureId).orElse(null);
        this.bodyScrollOffset = 0;
        if (this.currentStructure == null) {
            this.structurePonderView.clear();
            this.feedbackMessage = Component.translatable("screen.skyresources.guide.missing_structure", structureId);
        } else {
            this.structurePonderView.open();
            this.feedbackMessage = null;
        }
        return true;
    }

    private GuidePage selectedPage() {
        final List<GuidePage> pages = this.visiblePages();
        if (pages.isEmpty()) {
            return null;
        }
        return pages.get(this.selectedPageIndex);
    }

    private int visibleActionRows(final List<GuideAction> actions, final int availableHeight) {
        return Math.min(actions.size(), Math.min(ACTION_MAX_ROWS, Math.max(0, availableHeight / ACTION_ROW_HEIGHT)));
    }

    private int actionAvailableHeight(final int panelY, final int panelHeight) {
        final int iconY = this.contentTop(this.panelWidth(), panelY) + 20;
        final int bodyY = iconY + 26;
        final int bodyBottom = panelY + panelHeight - FOOTER_HEIGHT;
        return bodyBottom - bodyY;
    }

    private int actionTop(final int panelY, final int panelHeight, final int visibleRows) {
        return panelY + panelHeight - FOOTER_HEIGHT - visibleRows * ACTION_ROW_HEIGHT;
    }

    private int bodyBottomFor(final GuidePage page, final int panelY, final int panelHeight) {
        final int bodyY = this.contentTop(this.panelWidth(), panelY) + 20 + 26;
        final int bodyBottom = panelY + panelHeight - FOOTER_HEIGHT - 4;
        final int actionRows = this.visibleActionRows(page.actions(), this.actionAvailableHeight(panelY, panelHeight));
        final int actionTop = actionRows == 0 ? bodyBottom : this.actionTop(panelY, panelHeight, actionRows);
        int textBottom = actionRows == 0 ? bodyBottom : actionTop - ACTION_GAP;
        if (this.feedbackMessage != null && textBottom - bodyY > this.font.lineHeight + 2) {
            textBottom -= this.font.lineHeight + 2;
        }
        return textBottom;
    }

    private int visibleBodyRows(final int top, final int bottom) {
        return Math.max(0, (bottom - top) / ACTION_ROW_HEIGHT);
    }

    private void renderScrollBar(
            final GuiGraphics guiGraphics,
            final int x,
            final int y,
            final int width,
            final int height,
            final int totalRows,
            final int visibleRows,
            final int offset
    ) {
        if (height <= 0 || visibleRows <= 0 || totalRows <= visibleRows) {
            return;
        }
        guiGraphics.fill(x, y, x + width, y + height, SCROLL_TRACK_COLOR);
        final int thumbHeight = Math.max(10, height * visibleRows / totalRows);
        final int maxOffset = Math.max(1, totalRows - visibleRows);
        final int maxThumbTravel = Math.max(0, height - thumbHeight);
        final int thumbY = y + maxThumbTravel * Math.max(0, Math.min(offset, maxOffset)) / maxOffset;
        guiGraphics.fill(x, thumbY, x + width, thumbY + thumbHeight, SCROLL_THUMB_COLOR);
    }

    private int scrollOffset(
            final int currentOffset,
            final int totalRows,
            final int visibleRows,
            final double scrollY
    ) {
        if (totalRows <= visibleRows || visibleRows <= 0) {
            return 0;
        }
        final int step = Math.max(1, (int) Math.ceil(Math.abs(scrollY)));
        final int nextOffset = scrollY > 0.0D ? currentOffset - step : currentOffset + step;
        return this.clampScrollOffset(nextOffset, totalRows, visibleRows);
    }

    private int clampScrollOffset(final int offset, final int totalRows, final int visibleRows) {
        if (totalRows <= visibleRows || visibleRows <= 0) {
            return 0;
        }
        final int maxOffset = Math.max(0, totalRows - visibleRows);
        return Math.max(0, Math.min(offset, maxOffset));
    }

    private void ensureSelectedResultVisible(final int totalRows, final int visibleRows) {
        if (totalRows <= 0 || visibleRows <= 0) {
            this.resultScrollOffset = 0;
            return;
        }
        if (this.selectedPageIndex < this.resultScrollOffset) {
            this.resultScrollOffset = this.selectedPageIndex;
        } else if (this.selectedPageIndex >= this.resultScrollOffset + visibleRows) {
            this.resultScrollOffset = this.selectedPageIndex - visibleRows + 1;
        }
        this.resultScrollOffset = this.clampScrollOffset(this.resultScrollOffset, totalRows, visibleRows);
    }

    private void keepSelectedResultVisible() {
        final int panelY = this.panelY();
        final int panelHeight = this.panelHeight();
        this.ensureSelectedResultVisible(this.visiblePages().size(), this.visibleResultRows(panelY, panelHeight));
    }

    private Component actionTooltip(final GuideAction action) {
        return switch (action.type()) {
            case LINK -> Component.translatable("screen.skyresources.guide.action_tooltip.link");
            case RECIPE -> Component.translatable("screen.skyresources.guide.action_tooltip.recipe");
            case IMAGE -> Component.translatable("screen.skyresources.guide.action_tooltip.image");
        };
    }

    private void closeCurrentView() {
        this.onClose();
    }

    private void clearTransientView() {
        this.currentStructure = null;
        this.feedbackMessage = null;
        this.structurePonderView.clear();
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

    private boolean isSearching() {
        return !this.normalizedSearch().isEmpty();
    }

    private int nextInlineTokenEnd(final String text, final int start) {
        int index = start;
        while (index < text.length()
                && text.charAt(index) != '\n'
                && !Character.isWhitespace(text.charAt(index))
                && !text.startsWith("{action:", index)) {
            index++;
        }
        return index;
    }

    private String stripInlineActionMarkers(final String text) {
        final StringBuilder stripped = new StringBuilder(text.length());
        int index = 0;
        while (index < text.length()) {
            if (text.startsWith("{action:", index)) {
                final int markerEnd = text.indexOf('}', index);
                if (markerEnd > index) {
                    index = markerEnd + 1;
                    continue;
                }
            }
            stripped.append(text.charAt(index));
            index++;
        }
        return stripped.toString();
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

    private static final class BodyCursor {
        private final int startX;
        private final int width;
        private final int top;
        private final int bottom;
        private int x;
        private int y;

        private BodyCursor(final int x, final int y, final int width, final int top, final int bottom) {
            this.startX = x;
            this.width = width;
            this.top = top;
            this.bottom = bottom;
            this.x = x;
            this.y = y;
        }

        private int rightX() {
            return this.startX + this.width;
        }

        private boolean canRender() {
            return this.y < this.bottom;
        }

        private boolean isVisible() {
            return this.y + ACTION_ROW_HEIGHT > this.top && this.y < this.bottom;
        }

        private void newLine() {
            this.x = this.startX;
            this.y += ACTION_ROW_HEIGHT;
        }
    }

    private record InlineActionRegion(int x, int y, int width, int height, GuideAction action) {
    }

}
