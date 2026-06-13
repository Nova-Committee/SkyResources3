package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.guide.GuideAction;
import committee.nova.mods.skyresources3.guide.GuidePage;
import committee.nova.mods.skyresources3.guide.GuidePages;
import committee.nova.mods.skyresources3.guide.GuideStructure;
import committee.nova.mods.skyresources3.guide.GuideStructures;
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
    private static final int ACTION_MAX_ROWS = 4;
    private static final int ACTION_ROW_HEIGHT = 20;
    private static final int ACTION_GAP = 6;
    private static final int STRUCTURE_ROW_HEIGHT = 18;
    private static final int ICON_SIZE = 16;
    private static final int BACKGROUND_COLOR = 0xC0101010;
    private static final int PANEL_COLOR = 0xF0E7D8BD;
    private static final int BORDER_COLOR = 0xFF6B5A44;
    private static final int SELECTED_ROW_COLOR = 0x50FFFFFF;
    private static final int HOVERED_ROW_COLOR = 0x30FFFFFF;
    private static final int ACTION_ROW_COLOR = 0x20FFFFFF;
    private static final int TEXT_COLOR = 0xFF2F261C;
    private static final int MUTED_TEXT_COLOR = 0xFF5D5142;

    private int selectedCategoryIndex;
    private int selectedPageIndex;
    private String searchText = "";
    private EditBox searchBox;
    private GuideStructure currentStructure;
    private Component feedbackMessage;

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
            this.clearTransientView();
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
                        button -> this.closeCurrentView()
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
        if (event.button() != 0) {
            return false;
        }
        return this.selectActionAt(event.x(), event.y()) || this.selectResultAt(event.x(), event.y());
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

        if (this.currentStructure != null) {
            this.renderStructurePreview(guiGraphics, mouseX, mouseY, panelX, panelY, panelWidth, panelHeight);
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
        final int actionRows = this.visibleActionRows(page.actions(), bodyBottom - bodyY);
        final int actionTop = actionRows == 0 ? bodyBottom : bodyBottom - actionRows * ACTION_ROW_HEIGHT;
        int textBottom = actionRows == 0 ? bodyBottom : actionTop - ACTION_GAP;
        if (this.feedbackMessage != null && textBottom - bodyY > this.font.lineHeight + 2) {
            textBottom -= this.font.lineHeight + 2;
            guiGraphics.drawWordWrap(this.font, this.feedbackMessage, contentX, textBottom, contentWidth, MUTED_TEXT_COLOR);
        }
        if (textBottom > bodyY) {
            guiGraphics.enableScissor(contentX, bodyY, contentX + contentWidth, textBottom);
            guiGraphics.drawWordWrap(this.font, page.text(), contentX, bodyY, contentWidth, TEXT_COLOR);
            guiGraphics.disableScissor();
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
            this.renderResultRow(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    pages.get(index),
                    index,
                    listX,
                    listY,
                    listWidth
            );
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
        for (int index = 0; index < Math.min(visibleRows, actions.size()); index++) {
            this.renderActionRow(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    actions.get(index),
                    x,
                    y + index * ACTION_ROW_HEIGHT,
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
        guiGraphics.fill(x, y, x + width, y + ACTION_ROW_HEIGHT - 1, hovered ? HOVERED_ROW_COLOR : ACTION_ROW_COLOR);
        final ItemStack icon = action.icon();
        if (!icon.isEmpty()) {
            guiGraphics.renderFakeItem(icon, x + 1, y + 1);
        }

        final Component type = Component.translatable(
                "screen.skyresources3.guide.action_type." + action.type().name().toLowerCase(Locale.ROOT)
        );
        final int typeWidth = this.font.width(type);
        final int labelX = x + ICON_SIZE + 6;
        final int labelWidth = Math.max(16, width - ICON_SIZE - typeWidth - 14);
        guiGraphics.drawString(
                this.font,
                this.truncate(action.label().getString(), labelWidth),
                labelX,
                y + 6,
                TEXT_COLOR,
                false
        );
        guiGraphics.drawString(
                this.font,
                type,
                x + width - typeWidth - 4,
                y + 6,
                MUTED_TEXT_COLOR,
                false
        );
        if (hovered) {
            guiGraphics.setTooltipForNextFrame(this.font, this.actionTooltip(action), mouseX, mouseY);
        }
    }

    private void renderStructurePreview(
            final GuiGraphics guiGraphics,
            final int mouseX,
            final int mouseY,
            final int panelX,
            final int panelY,
            final int panelWidth,
            final int panelHeight
    ) {
        final GuideStructure structure = this.currentStructure;
        final int contentX = panelX + PANEL_PADDING;
        final int contentY = panelY + 48;
        final int contentWidth = panelWidth - PANEL_PADDING * 2;
        final int contentBottom = panelY + panelHeight - FOOTER_HEIGHT;
        guiGraphics.drawString(this.font, structure.title(), contentX, contentY, TEXT_COLOR, false);
        guiGraphics.drawString(
                this.font,
                Component.translatable("screen.skyresources3.guide.structure_count", structure.blocks().size()),
                contentX,
                contentY + 14,
                MUTED_TEXT_COLOR,
                false
        );
        guiGraphics.drawString(
                this.font,
                Component.translatable("screen.skyresources3.guide.structure_close_hint"),
                contentX,
                contentBottom - this.font.lineHeight,
                MUTED_TEXT_COLOR,
                false
        );

        final int listY = contentY + 32;
        final int listBottom = contentBottom - this.font.lineHeight - 4;
        final int visibleRows = Math.max(0, (listBottom - listY) / STRUCTURE_ROW_HEIGHT);
        guiGraphics.enableScissor(contentX, listY, contentX + contentWidth, listBottom);
        for (int index = 0; index < Math.min(visibleRows, structure.blocks().size()); index++) {
            this.renderStructureBlockRow(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    structure.blocks().get(index),
                    contentX,
                    listY,
                    contentWidth,
                    index
            );
        }
        guiGraphics.disableScissor();
    }

    private void renderStructureBlockRow(
            final GuiGraphics guiGraphics,
            final int mouseX,
            final int mouseY,
            final GuideStructure.BlockEntry block,
            final int x,
            final int listY,
            final int width,
            final int index
    ) {
        final int rowY = listY + index * STRUCTURE_ROW_HEIGHT;
        final boolean hovered = this.isInside(mouseX, mouseY, x, rowY, width, STRUCTURE_ROW_HEIGHT - 1);
        if (hovered) {
            guiGraphics.fill(x, rowY, x + width, rowY + STRUCTURE_ROW_HEIGHT - 1, HOVERED_ROW_COLOR);
        }
        final ItemStack icon = block.icon();
        if (!icon.isEmpty()) {
            guiGraphics.renderFakeItem(icon, x + 1, rowY + 1);
        }
        final Component position = Component.translatable(
                "screen.skyresources3.guide.structure_position",
                block.position()
        );
        final int positionWidth = this.font.width(position);
        guiGraphics.drawString(this.font, position, x + ICON_SIZE + 6, rowY + 5, MUTED_TEXT_COLOR, false);
        guiGraphics.drawString(
                this.font,
                this.truncate(icon.getHoverName().getString(), width - ICON_SIZE - positionWidth - 18),
                x + ICON_SIZE + positionWidth + 10,
                rowY + 5,
                TEXT_COLOR,
                false
        );
        if (hovered && !icon.isEmpty()) {
            guiGraphics.setTooltipForNextFrame(this.font, icon, mouseX, mouseY);
        }
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
        this.clearTransientView();
        this.selectedCategoryIndex = Math.floorMod(this.selectedCategoryIndex + direction, categories.size());
        this.selectedPageIndex = 0;
    }

    private void changePage(final int direction) {
        final List<GuidePage> pages = this.visiblePages();
        if (pages.isEmpty()) {
            return;
        }
        this.clearTransientView();
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
        this.clearTransientView();
        this.selectedPageIndex = index;
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
        final int iconY = panelY + 58 + 24;
        final int bodyY = iconY + 30;
        final int bodyBottom = panelY + panelHeight - FOOTER_HEIGHT;
        final int contentWidth = wideIndex
                ? panelWidth - PANEL_PADDING * 2 - this.indexWidth(panelWidth) - INDEX_GAP
                : panelWidth - PANEL_PADDING * 2;
        final int visibleRows = this.visibleActionRows(page.actions(), bodyBottom - bodyY);
        final int actionTop = bodyBottom - visibleRows * ACTION_ROW_HEIGHT;
        if (visibleRows <= 0 || !this.isInside((int) mouseX, (int) mouseY, contentX, actionTop, contentWidth, visibleRows * ACTION_ROW_HEIGHT)) {
            return false;
        }

        final int actionIndex = ((int) mouseY - actionTop) / ACTION_ROW_HEIGHT;
        if (actionIndex < 0 || actionIndex >= Math.min(visibleRows, page.actions().size())) {
            return false;
        }
        return this.handleAction(page.actions().get(actionIndex));
    }

    private boolean handleAction(final GuideAction action) {
        return switch (action.type()) {
            case LINK -> this.openGuidePage(action.target());
            case IMAGE -> this.openStructure(action.target());
            case RECIPE -> {
                if (GuideRecipeIntegration.open(action)) {
                    this.feedbackMessage = null;
                } else {
                    this.feedbackMessage = Component.translatable("screen.skyresources3.guide.recipe_unavailable", action.label());
                }
                yield true;
            }
        };
    }

    private boolean openGuidePage(final String pageId) {
        final GuidePage target = GuidePages.find(pageId).orElse(null);
        if (target == null) {
            this.feedbackMessage = Component.translatable("screen.skyresources3.guide.missing_page", pageId);
            return true;
        }
        final List<String> categories = GuidePages.categories();
        final int categoryIndex = categories.indexOf(target.categoryKey());
        if (categoryIndex < 0) {
            this.feedbackMessage = Component.translatable("screen.skyresources3.guide.missing_page", pageId);
            return true;
        }

        this.searchText = "";
        if (this.searchBox != null) {
            this.searchBox.setValue("");
        }
        this.clearTransientView();
        this.selectedCategoryIndex = categoryIndex;
        final List<GuidePage> categoryPages = this.currentCategoryPages();
        this.selectedPageIndex = Math.max(0, categoryPages.indexOf(target));
        return true;
    }

    private boolean openStructure(final String structureId) {
        this.currentStructure = GuideStructures.find(structureId).orElse(null);
        if (this.currentStructure == null) {
            this.feedbackMessage = Component.translatable("screen.skyresources3.guide.missing_structure", structureId);
        } else {
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

    private Component actionTooltip(final GuideAction action) {
        return switch (action.type()) {
            case LINK -> Component.translatable("screen.skyresources3.guide.action_tooltip.link");
            case RECIPE -> Component.translatable("screen.skyresources3.guide.action_tooltip.recipe");
            case IMAGE -> Component.translatable("screen.skyresources3.guide.action_tooltip.image");
        };
    }

    private void closeCurrentView() {
        this.onClose();
    }

    private void clearTransientView() {
        this.currentStructure = null;
        this.feedbackMessage = null;
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
