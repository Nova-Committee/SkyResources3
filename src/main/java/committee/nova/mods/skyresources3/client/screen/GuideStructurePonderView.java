package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.core.guide.GuideStructure;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

final class GuideStructurePonderView {
    private static final int PANEL_PADDING = 14;
    private static final int FOOTER_HEIGHT = 34;
    private static final int HEADER_SPACE = 48;
    private static final int STORY_HEIGHT = 82;
    private static final int STORY_PADDING = 8;
    private static final int CONTROL_HEIGHT = 18;
    private static final int CONTROL_GAP = 4;
    private static final int CONTROL_WIDTH = 22;
    private static final int VIEW_CONTROL_WIDTH = 32;
    private static final int TIMELINE_HEIGHT = 8;
    private static final int ICON_SIZE = 16;
    private static final int ANIMATION_STEP_MILLIS = 650;
    private static final int ANIMATION_HOLD_STEPS = 4;

    private static final int CONTENT_COLOR = 0xF0E9DDC4;
    private static final int SCENE_COLOR = 0xF0212730;
    private static final int STORY_COLOR = 0xE0F5E8CE;
    private static final int BORDER_COLOR = 0xFF6E7E89;
    private static final int TEXT_COLOR = 0xFF2B241A;
    private static final int MUTED_TEXT_COLOR = 0xFF675E52;
    private static final int HEADER_TEXT_COLOR = 0xFFEFE5CF;
    private static final int HOVERED_ROW_COLOR = 0x503E6F78;
    private static final int ACTION_ROW_COLOR = 0x305C465F;
    private static final int ACTIVE_BLOCK_COLOR = 0xB0E8B654;
    private static final int GHOST_BLOCK_COLOR = 0x405C6672;
    private static final int TIMELINE_COLOR = 0x605C6672;
    private static final int TIMELINE_PROGRESS_COLOR = 0xC0E8B654;

    private boolean paused;
    private int manualStep = 1;
    private long startedAtMillis;
    private int viewQuarter;

    void open() {
        this.paused = false;
        this.manualStep = 1;
        this.startedAtMillis = System.currentTimeMillis();
        this.viewQuarter = 0;
    }

    void clear() {
        this.paused = false;
        this.manualStep = 1;
        this.startedAtMillis = 0L;
        this.viewQuarter = 0;
    }

    void render(
            final GuiGraphics guiGraphics,
            final Font font,
            final int mouseX,
            final int mouseY,
            final int panelX,
            final int panelY,
            final int panelWidth,
            final int panelHeight,
            final GuideStructure structure
    ) {
        final int contentX = panelX + PANEL_PADDING;
        final int contentY = panelY + HEADER_SPACE;
        final int contentWidth = panelWidth - PANEL_PADDING * 2;
        final int contentBottom = panelY + panelHeight - FOOTER_HEIGHT;
        final int storyHeight = this.storyHeight(panelY, panelHeight);
        final int storyY = contentBottom - storyHeight;
        final int sceneY = contentY + 30;
        final int sceneBottom = Math.max(sceneY, storyY - 6);
        final int visibleCount = this.visibleBlockCount(structure);

        guiGraphics.fill(contentX - 6, contentY - 6, contentX + contentWidth + 6, contentBottom, CONTENT_COLOR);
        guiGraphics.renderOutline(contentX - 6, contentY - 6, contentWidth + 12, contentBottom - contentY + 6, BORDER_COLOR);
        guiGraphics.drawString(
                font,
                this.truncate(font, structure.title().getString(), contentWidth),
                contentX,
                contentY,
                TEXT_COLOR,
                false
        );
        guiGraphics.drawString(
                font,
                this.truncate(
                        font,
                        Component.translatable("screen.skyresources.guide.structure_ponder_subtitle").getString(),
                        contentWidth
                ),
                contentX,
                contentY + 14,
                MUTED_TEXT_COLOR,
                false
        );

        if (sceneBottom > sceneY) {
            this.renderScene(
                    guiGraphics,
                    font,
                    mouseX,
                    mouseY,
                    structure,
                    contentX,
                    sceneY,
                    contentWidth,
                    sceneBottom - sceneY,
                    visibleCount
            );
        }
        this.renderStoryboard(
                guiGraphics,
                font,
                mouseX,
                mouseY,
                structure,
                contentX,
                storyY,
                contentWidth,
                storyHeight,
                visibleCount
        );
    }

    boolean mouseClicked(
            final double mouseX,
            final double mouseY,
            final int panelX,
            final int panelY,
            final int panelWidth,
            final int panelHeight,
            final GuideStructure structure
    ) {
        if (this.selectControlAt(mouseX, mouseY, panelX, panelY, panelWidth, panelHeight, structure)) {
            return true;
        }
        return this.selectTimelineAt(mouseX, mouseY, panelX, panelY, panelWidth, panelHeight, structure);
    }

    boolean mouseScrolled(
            final double mouseX,
            final double mouseY,
            final double scrollY,
            final int panelX,
            final int panelY,
            final int panelWidth,
            final int panelHeight
    ) {
        final int contentX = panelX + PANEL_PADDING;
        final int contentWidth = panelWidth - PANEL_PADDING * 2;
        final int sceneY = panelY + HEADER_SPACE + 30;
        final int sceneBottom = this.storyY(panelY, panelHeight) - 6;
        if (sceneBottom <= sceneY
                || !this.isInside((int) mouseX, (int) mouseY, contentX, sceneY, contentWidth, sceneBottom - sceneY)) {
            return false;
        }
        this.rotateView(scrollY > 0.0D ? -1 : 1);
        return true;
    }

    private void renderScene(
            final GuiGraphics guiGraphics,
            final Font font,
            final int mouseX,
            final int mouseY,
            final GuideStructure structure,
            final int x,
            final int y,
            final int width,
            final int height,
            final int visibleCount
    ) {
        guiGraphics.fill(x, y, x + width, y + height, SCENE_COLOR);
        guiGraphics.renderOutline(x, y, width, height, BORDER_COLOR);
        guiGraphics.drawString(
                font,
                Component.translatable("screen.skyresources.guide.structure_scene_identify"),
                x + 8,
                y + 7,
                HEADER_TEXT_COLOR,
                false
        );
        guiGraphics.drawString(
                font,
                Component.translatable("screen.skyresources.guide.structure_scene_rotate"),
                x + width - 8 - font.width(Component.translatable("screen.skyresources.guide.structure_scene_rotate")),
                y + 7,
                HEADER_TEXT_COLOR,
                false
        );

        final List<StructureTile> tiles = this.layoutTiles(structure, width - 18, height - 28, visibleCount);
        if (tiles.isEmpty()) {
            return;
        }
        int minTileX = Integer.MAX_VALUE;
        int maxTileX = Integer.MIN_VALUE;
        int minTileY = Integer.MAX_VALUE;
        int maxTileY = Integer.MIN_VALUE;
        for (final StructureTile tile : tiles) {
            minTileX = Math.min(minTileX, tile.x());
            maxTileX = Math.max(maxTileX, tile.x() + ICON_SIZE);
            minTileY = Math.min(minTileY, tile.y());
            maxTileY = Math.max(maxTileY, tile.y() + ICON_SIZE);
        }
        final int offsetX = x + (width - (maxTileX - minTileX)) / 2 - minTileX;
        final int offsetY = y + 24 + Math.max(0, (height - 28 - (maxTileY - minTileY)) / 2) - minTileY;

        guiGraphics.enableScissor(x + 1, y + 20, x + width - 1, y + height - 1);
        for (final StructureTile tile : tiles) {
            if (tile.index() < visibleCount) {
                continue;
            }
            this.renderGhostTile(guiGraphics, font, mouseX, mouseY, tile, offsetX, offsetY);
        }
        for (final StructureTile tile : tiles) {
            if (tile.index() >= visibleCount) {
                continue;
            }
            this.renderVisibleTile(guiGraphics, font, mouseX, mouseY, tile, offsetX, offsetY, tile.index() == visibleCount - 1);
        }
        guiGraphics.disableScissor();
    }

    private void renderGhostTile(
            final GuiGraphics guiGraphics,
            final Font font,
            final int mouseX,
            final int mouseY,
            final StructureTile tile,
            final int offsetX,
            final int offsetY
    ) {
        final int drawX = offsetX + tile.x();
        final int drawY = offsetY + tile.y();
        final boolean hovered = this.isInside(mouseX, mouseY, drawX, drawY, ICON_SIZE, ICON_SIZE);
        guiGraphics.fill(drawX, drawY, drawX + ICON_SIZE, drawY + ICON_SIZE, GHOST_BLOCK_COLOR);
        guiGraphics.renderOutline(drawX, drawY, ICON_SIZE, ICON_SIZE, hovered ? ACTIVE_BLOCK_COLOR : BORDER_COLOR);
        if (hovered) {
            final ItemStack icon = tile.block().icon();
            guiGraphics.setTooltipForNextFrame(font, this.blockTooltip(tile.block(), icon), mouseX, mouseY);
        }
    }

    private void renderVisibleTile(
            final GuiGraphics guiGraphics,
            final Font font,
            final int mouseX,
            final int mouseY,
            final StructureTile tile,
            final int offsetX,
            final int offsetY,
            final boolean active
    ) {
        final GuideStructure.BlockEntry block = tile.block();
        final ItemStack icon = block.icon();
        if (icon.isEmpty()) {
            return;
        }
        final int drawX = offsetX + tile.x();
        final int drawY = offsetY + tile.y();
        final boolean hovered = this.isInside(mouseX, mouseY, drawX, drawY, ICON_SIZE, ICON_SIZE);
        if (active || hovered) {
            guiGraphics.fill(
                    drawX - 1,
                    drawY - 1,
                    drawX + ICON_SIZE + 1,
                    drawY + ICON_SIZE + 1,
                    active ? ACTIVE_BLOCK_COLOR : HOVERED_ROW_COLOR
            );
        }
        guiGraphics.renderFakeItem(icon, drawX, drawY);
        if (active || hovered) {
            guiGraphics.renderOutline(drawX - 1, drawY - 1, ICON_SIZE + 2, ICON_SIZE + 2, active ? ACTIVE_BLOCK_COLOR : BORDER_COLOR);
        }
        if (hovered) {
            guiGraphics.setTooltipForNextFrame(font, this.blockTooltip(block, icon), mouseX, mouseY);
        }
    }

    private void renderStoryboard(
            final GuiGraphics guiGraphics,
            final Font font,
            final int mouseX,
            final int mouseY,
            final GuideStructure structure,
            final int x,
            final int y,
            final int width,
            final int height,
            final int visibleCount
    ) {
        guiGraphics.fill(x, y, x + width, y + height, STORY_COLOR);
        guiGraphics.renderOutline(x, y, width, height, BORDER_COLOR);
        final GuideStructure.BlockEntry activeBlock = structure.blocks().get(Math.max(0, visibleCount - 1));
        final ItemStack activeIcon = activeBlock.icon();
        final int textX = x + STORY_PADDING + ICON_SIZE + 8;
        final int controlsY = y + STORY_PADDING;
        if (!activeIcon.isEmpty()) {
            guiGraphics.renderFakeItem(activeIcon, x + STORY_PADDING, y + STORY_PADDING + 2);
        }
        guiGraphics.drawString(
                font,
                this.truncate(
                        font,
                        Component.translatable(
                                "screen.skyresources.guide.structure_scene_step",
                                visibleCount,
                                structure.blocks().size()
                        ).getString(),
                        Math.max(24, width - (textX - x) - this.controlsWidth() - 14)
                ),
                textX,
                y + STORY_PADDING,
                TEXT_COLOR,
                false
        );
        guiGraphics.drawString(
                font,
                this.truncate(
                        font,
                        Component.translatable(
                                "screen.skyresources.guide.structure_scene_place",
                                activeIcon.getHoverName(),
                                activeBlock.position()
                        ).getString(),
                        Math.max(24, width - (textX - x) - this.controlsWidth() - 14)
                ),
                textX,
                y + STORY_PADDING + 14,
                TEXT_COLOR,
                false
        );
        guiGraphics.drawString(
                font,
                this.truncate(
                        font,
                        Component.translatable("screen.skyresources.guide.structure_scene_hint").getString(),
                        width - STORY_PADDING * 2
                ),
                x + STORY_PADDING,
                y + STORY_PADDING + 34,
                MUTED_TEXT_COLOR,
                false
        );
        this.renderControls(guiGraphics, font, mouseX, mouseY, x + width - STORY_PADDING - this.controlsWidth(), controlsY);
        this.renderTimeline(guiGraphics, font, mouseX, mouseY, structure, x + STORY_PADDING, y + height - 18, width - STORY_PADDING * 2, visibleCount);
    }

    private void renderControls(
            final GuiGraphics guiGraphics,
            final Font font,
            final int mouseX,
            final int mouseY,
            final int x,
            final int y
    ) {
        int controlX = x;
        for (final StructureControl control : this.controls(x, y)) {
            final boolean hovered = this.isInside(mouseX, mouseY, control.x(), control.y(), control.width(), control.height());
            guiGraphics.fill(
                    control.x(),
                    control.y(),
                    control.x() + control.width(),
                    control.y() + control.height(),
                    hovered ? HOVERED_ROW_COLOR : ACTION_ROW_COLOR
            );
            guiGraphics.renderOutline(control.x(), control.y(), control.width(), control.height(), BORDER_COLOR);
            this.drawCenteredTruncatedString(
                    guiGraphics,
                    font,
                    control.label(),
                    control.x() + control.width() / 2,
                    control.y() + (control.height() - font.lineHeight) / 2,
                    control.width() - 4,
                    TEXT_COLOR
            );
            if (hovered) {
                guiGraphics.setTooltipForNextFrame(font, control.tooltip(), mouseX, mouseY);
            }
            controlX += control.width() + CONTROL_GAP;
        }
    }

    private void renderTimeline(
            final GuiGraphics guiGraphics,
            final Font font,
            final int mouseX,
            final int mouseY,
            final GuideStructure structure,
            final int x,
            final int y,
            final int width,
            final int visibleCount
    ) {
        guiGraphics.fill(x, y, x + width, y + TIMELINE_HEIGHT, TIMELINE_COLOR);
        final int progressWidth = Math.max(1, width * visibleCount / Math.max(1, structure.blocks().size()));
        guiGraphics.fill(x, y, x + progressWidth, y + TIMELINE_HEIGHT, TIMELINE_PROGRESS_COLOR);
        final int marks = Math.min(structure.blocks().size(), 16);
        for (int mark = 1; mark < marks; mark++) {
            final int markX = x + width * mark / marks;
            guiGraphics.fill(markX, y - 1, markX + 1, y + TIMELINE_HEIGHT + 1, BORDER_COLOR);
        }
        if (this.isInside(mouseX, mouseY, x, y - 4, width, TIMELINE_HEIGHT + 8)) {
            guiGraphics.setTooltipForNextFrame(
                    font,
                    Component.translatable("screen.skyresources.guide.structure_timeline_tooltip"),
                    mouseX,
                    mouseY
            );
        }
    }

    private List<StructureTile> layoutTiles(final GuideStructure structure, final int width, final int height, final int visibleCount) {
        final List<GuideStructure.BlockEntry> blocks = structure.blocks();
        final int view = Math.floorMod(this.viewQuarter, 4);
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (final GuideStructure.BlockEntry block : blocks) {
            final int viewX = this.viewX(block, view);
            final int viewZ = this.viewZ(block, view);
            minX = Math.min(minX, viewX);
            maxX = Math.max(maxX, viewX);
            minY = Math.min(minY, block.y());
            maxY = Math.max(maxY, block.y());
            minZ = Math.min(minZ, viewZ);
            maxZ = Math.max(maxZ, viewZ);
        }
        final int horizontalUnits = Math.max(1, maxX - minX + maxZ - minZ);
        final int verticalUnits = Math.max(1, maxX + maxZ - minX - minZ + (maxY - minY) * 2);
        final int stepX = this.previewStep(width - ICON_SIZE, horizontalUnits, 8, 24);
        final int stepY = this.previewStep(height - ICON_SIZE, verticalUnits, 3, 10);
        final int layerStep = stepY * 2;
        final List<StructureTile> tiles = new ArrayList<>(blocks.size());
        for (int index = 0; index < blocks.size(); index++) {
            final GuideStructure.BlockEntry block = blocks.get(index);
            final int viewX = this.viewX(block, view);
            final int viewZ = this.viewZ(block, view);
            tiles.add(new StructureTile(
                    block,
                    (viewX - viewZ) * stepX,
                    (viewX + viewZ) * stepY - (block.y() - minY) * layerStep,
                    viewX + viewZ,
                    viewX,
                    index
            ));
        }
        tiles.sort(Comparator.comparingInt((StructureTile tile) -> tile.block().y())
                .thenComparingInt(StructureTile::depth)
                .thenComparingInt(StructureTile::secondary)
                .thenComparingInt(tile -> Math.abs(tile.index() - visibleCount)));
        return tiles;
    }

    private boolean selectControlAt(
            final double mouseX,
            final double mouseY,
            final int panelX,
            final int panelY,
            final int panelWidth,
            final int panelHeight,
            final GuideStructure structure
    ) {
        final int controlsX = panelX + panelWidth - PANEL_PADDING - STORY_PADDING - this.controlsWidth();
        final int controlsY = this.storyY(panelY, panelHeight) + STORY_PADDING;
        for (final StructureControl control : this.controls(controlsX, controlsY)) {
            if (!this.isInside((int) mouseX, (int) mouseY, control.x(), control.y(), control.width(), control.height())) {
                continue;
            }
            this.handleControl(control.action(), structure);
            return true;
        }
        return false;
    }

    private boolean selectTimelineAt(
            final double mouseX,
            final double mouseY,
            final int panelX,
            final int panelY,
            final int panelWidth,
            final int panelHeight,
            final GuideStructure structure
    ) {
        final int x = panelX + PANEL_PADDING + STORY_PADDING;
        final int y = this.storyY(panelY, panelHeight) + this.storyHeight(panelY, panelHeight) - 18;
        final int width = panelWidth - PANEL_PADDING * 2 - STORY_PADDING * 2;
        if (!this.isInside((int) mouseX, (int) mouseY, x, y - 4, width, TIMELINE_HEIGHT + 8)) {
            return false;
        }
        final int totalBlocks = structure.blocks().size();
        final int selected = Math.max(1, Math.min(totalBlocks, 1 + (int) ((mouseX - x) * totalBlocks / Math.max(1, width))));
        this.manualStep = selected;
        this.paused = true;
        return true;
    }

    private void handleControl(final StructureControlAction action, final GuideStructure structure) {
        switch (action) {
            case VIEW_LEFT -> this.rotateView(-1);
            case VIEW_RIGHT -> this.rotateView(1);
            case PREVIOUS_STEP -> this.step(structure, -1);
            case RESTART -> this.restart();
            case TOGGLE_ANIMATION -> this.toggle(structure);
            case NEXT_STEP -> this.step(structure, 1);
        }
    }

    private void rotateView(final int direction) {
        this.viewQuarter = Math.floorMod(this.viewQuarter + direction, 4);
    }

    private void step(final GuideStructure structure, final int direction) {
        if (structure.blocks().isEmpty()) {
            return;
        }
        if (!this.paused) {
            this.manualStep = this.visibleBlockCount(structure);
            this.paused = true;
        }
        final int totalBlocks = structure.blocks().size();
        this.manualStep = Math.floorMod(this.manualStep - 1 + direction, totalBlocks) + 1;
    }

    private void restart() {
        this.paused = false;
        this.manualStep = 1;
        this.startedAtMillis = System.currentTimeMillis();
    }

    private void toggle(final GuideStructure structure) {
        if (structure.blocks().isEmpty()) {
            return;
        }
        if (this.paused) {
            this.paused = false;
            this.startedAtMillis = System.currentTimeMillis()
                    - (long) Math.max(0, this.manualStep - 1) * ANIMATION_STEP_MILLIS;
        } else {
            this.manualStep = this.visibleBlockCount(structure);
            this.paused = true;
        }
    }

    private int visibleBlockCount(final GuideStructure structure) {
        final int totalBlocks = structure.blocks().size();
        if (totalBlocks <= 0) {
            return 0;
        }
        if (this.paused) {
            return Math.max(1, Math.min(this.manualStep, totalBlocks));
        }
        final long elapsed = Math.max(0L, System.currentTimeMillis() - this.startedAtMillis);
        final int cycleSteps = totalBlocks + ANIMATION_HOLD_STEPS;
        final int animationStep = (int) ((elapsed / ANIMATION_STEP_MILLIS) % cycleSteps);
        return Math.min(totalBlocks, animationStep + 1);
    }

    private List<StructureControl> controls(final int x, final int y) {
        final List<StructureControl> controls = new ArrayList<>(6);
        int controlX = x;
        controls.add(control(controlX, y, VIEW_CONTROL_WIDTH, "L", "button.skyresources.guide.structure_view_left", StructureControlAction.VIEW_LEFT));
        controlX += VIEW_CONTROL_WIDTH + CONTROL_GAP;
        controls.add(control(controlX, y, CONTROL_WIDTH, "<", "button.skyresources.guide.structure_prev_step", StructureControlAction.PREVIOUS_STEP));
        controlX += CONTROL_WIDTH + CONTROL_GAP;
        controls.add(control(controlX, y, CONTROL_WIDTH, "S", "button.skyresources.guide.structure_restart", StructureControlAction.RESTART));
        controlX += CONTROL_WIDTH + CONTROL_GAP;
        controls.add(control(
                controlX,
                y,
                CONTROL_WIDTH,
                this.paused ? ">" : "||",
                this.paused ? "button.skyresources.guide.structure_play" : "button.skyresources.guide.structure_pause",
                StructureControlAction.TOGGLE_ANIMATION
        ));
        controlX += CONTROL_WIDTH + CONTROL_GAP;
        controls.add(control(controlX, y, CONTROL_WIDTH, ">", "button.skyresources.guide.structure_next_step", StructureControlAction.NEXT_STEP));
        controlX += CONTROL_WIDTH + CONTROL_GAP;
        controls.add(control(controlX, y, VIEW_CONTROL_WIDTH, "R", "button.skyresources.guide.structure_view_right", StructureControlAction.VIEW_RIGHT));
        return controls;
    }

    private StructureControl control(
            final int x,
            final int y,
            final int width,
            final String label,
            final String tooltipKey,
            final StructureControlAction action
    ) {
        return new StructureControl(x, y, width, CONTROL_HEIGHT, Component.literal(label), Component.translatable(tooltipKey), action);
    }

    private int controlsWidth() {
        return VIEW_CONTROL_WIDTH * 2 + CONTROL_WIDTH * 4 + CONTROL_GAP * 5;
    }

    private int storyY(final int panelY, final int panelHeight) {
        final int contentBottom = panelY + panelHeight - FOOTER_HEIGHT;
        return contentBottom - this.storyHeight(panelY, panelHeight);
    }

    private int storyHeight(final int panelY, final int panelHeight) {
        final int contentY = panelY + HEADER_SPACE;
        final int contentBottom = panelY + panelHeight - FOOTER_HEIGHT;
        return Math.max(44, Math.min(STORY_HEIGHT, contentBottom - contentY - 72));
    }

    private int viewX(final GuideStructure.BlockEntry block, final int viewQuarter) {
        return switch (viewQuarter) {
            case 1 -> block.z();
            case 2 -> -block.x();
            case 3 -> -block.z();
            default -> block.x();
        };
    }

    private int viewZ(final GuideStructure.BlockEntry block, final int viewQuarter) {
        return switch (viewQuarter) {
            case 1 -> -block.x();
            case 2 -> -block.z();
            case 3 -> block.x();
            default -> block.z();
        };
    }

    private int previewStep(final int available, final int units, final int min, final int max) {
        return Math.max(min, Math.min(max, Math.max(1, available / Math.max(1, units))));
    }

    private Component blockTooltip(final GuideStructure.BlockEntry block, final ItemStack icon) {
        return Component.literal(icon.getHoverName().getString()
                + " ("
                + block.x()
                + ", "
                + block.y()
                + ", "
                + block.z()
                + ")");
    }

    private void drawCenteredTruncatedString(
            final GuiGraphics guiGraphics,
            final Font font,
            final Component text,
            final int centerX,
            final int y,
            final int width,
            final int color
    ) {
        final String fitted = this.truncate(font, text.getString(), width);
        guiGraphics.drawString(font, fitted, centerX - font.width(fitted) / 2, y, color, false);
    }

    private String truncate(final Font font, final String text, final int width) {
        if (font.width(text) <= width) {
            return text;
        }
        final String ellipsis = "...";
        return font.plainSubstrByWidth(text, Math.max(0, width - font.width(ellipsis))) + ellipsis;
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

    private enum StructureControlAction {
        VIEW_LEFT,
        PREVIOUS_STEP,
        RESTART,
        TOGGLE_ANIMATION,
        NEXT_STEP,
        VIEW_RIGHT
    }

    private record StructureControl(
            int x,
            int y,
            int width,
            int height,
            Component label,
            Component tooltip,
            StructureControlAction action
    ) {
    }

    private record StructureTile(
            GuideStructure.BlockEntry block,
            int x,
            int y,
            int depth,
            int secondary,
            int index
    ) {
    }
}
