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
    private static final int EDGE_PADDING = 12;
    private static final int TOP_BAR_HEIGHT = 28;
    private static final int BOTTOM_BAR_HEIGHT = 44;
    private static final int BACK_BUTTON_WIDTH = 58;
    private static final int BUTTON_HEIGHT = 20;
    private static final int CONTROL_HEIGHT = 20;
    private static final int CONTROL_GAP = 5;
    private static final int CONTROL_WIDTH = 24;
    private static final int VIEW_CONTROL_WIDTH = 34;
    private static final int TIMELINE_HEIGHT = 7;
    private static final int BASE_ICON_SIZE = 16;
    private static final int MIN_TILE_SIZE = 18;
    private static final int MAX_TILE_SIZE = 56;
    private static final int ANIMATION_STEP_MILLIS = 650;
    private static final int ANIMATION_HOLD_STEPS = 4;

    private static final int SCENE_COLOR = 0xF0212730;
    private static final int OVERLAY_COLOR = 0xB010141A;
    private static final int BUTTON_COLOR = 0x90404A55;
    private static final int HOVERED_BUTTON_COLOR = 0xB05B747B;
    private static final int BORDER_COLOR = 0xFF6E7E89;
    private static final int TEXT_COLOR = 0xFFEFE5CF;
    private static final int MUTED_TEXT_COLOR = 0xFFB6C7C2;
    private static final int HOVERED_BLOCK_COLOR = 0x604A7F87;
    private static final int ACTIVE_BLOCK_COLOR = 0xB0E8B654;
    private static final int GHOST_BLOCK_COLOR = 0x385C6672;
    private static final int TIMELINE_COLOR = 0x805C6672;
    private static final int TIMELINE_PROGRESS_COLOR = 0xD0E8B654;

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
            final int x,
            final int y,
            final int width,
            final int height,
            final GuideStructure structure
    ) {
        final int visibleCount = this.visibleBlockCount(structure);
        guiGraphics.fill(x, y, x + width, y + height, SCENE_COLOR);

        final int sceneX = x + EDGE_PADDING;
        final int sceneY = y + TOP_BAR_HEIGHT;
        final int sceneWidth = Math.max(0, width - EDGE_PADDING * 2);
        final int sceneHeight = Math.max(0, height - TOP_BAR_HEIGHT - BOTTOM_BAR_HEIGHT);
        this.renderScene(guiGraphics, font, mouseX, mouseY, structure, sceneX, sceneY, sceneWidth, sceneHeight, visibleCount);
        this.renderTopOverlay(guiGraphics, font, mouseX, mouseY, x, y, width, structure, visibleCount);
        this.renderBottomOverlay(guiGraphics, font, mouseX, mouseY, x, y, width, height, structure, visibleCount);
    }

    ClickResult mouseClicked(
            final double mouseX,
            final double mouseY,
            final int x,
            final int y,
            final int width,
            final int height,
            final GuideStructure structure
    ) {
        if (this.isInside((int) mouseX, (int) mouseY, this.backButtonX(x), this.backButtonY(y), BACK_BUTTON_WIDTH, BUTTON_HEIGHT)) {
            return ClickResult.BACK;
        }
        if (this.selectControlAt(mouseX, mouseY, x, y, width, height, structure)
                || this.selectTimelineAt(mouseX, mouseY, x, y, width, height, structure)) {
            return ClickResult.HANDLED;
        }
        return ClickResult.NONE;
    }

    boolean mouseScrolled(
            final double mouseX,
            final double mouseY,
            final double scrollY,
            final int x,
            final int y,
            final int width,
            final int height
    ) {
        final int sceneX = x + EDGE_PADDING;
        final int sceneY = y + TOP_BAR_HEIGHT;
        final int sceneWidth = Math.max(0, width - EDGE_PADDING * 2);
        final int sceneHeight = Math.max(0, height - TOP_BAR_HEIGHT - BOTTOM_BAR_HEIGHT);
        if (sceneHeight <= 0 || !this.isInside((int) mouseX, (int) mouseY, sceneX, sceneY, sceneWidth, sceneHeight)) {
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
        if (width <= 0 || height <= 0) {
            return;
        }
        final int tileSize = this.tileSize(structure, width, height);
        final List<StructureTile> tiles = this.layoutTiles(structure, tileSize, visibleCount);
        if (tiles.isEmpty()) {
            return;
        }

        int minTileX = Integer.MAX_VALUE;
        int maxTileX = Integer.MIN_VALUE;
        int minTileY = Integer.MAX_VALUE;
        int maxTileY = Integer.MIN_VALUE;
        for (final StructureTile tile : tiles) {
            minTileX = Math.min(minTileX, tile.x());
            maxTileX = Math.max(maxTileX, tile.x() + tile.size());
            minTileY = Math.min(minTileY, tile.y());
            maxTileY = Math.max(maxTileY, tile.y() + tile.size());
        }
        final int offsetX = x + (width - (maxTileX - minTileX)) / 2 - minTileX;
        final int offsetY = y + (height - (maxTileY - minTileY)) / 2 - minTileY;

        guiGraphics.enableScissor(x, y, x + width, y + height);
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

    private void renderTopOverlay(
            final GuiGraphics guiGraphics,
            final Font font,
            final int mouseX,
            final int mouseY,
            final int x,
            final int y,
            final int width,
            final GuideStructure structure,
            final int visibleCount
    ) {
        guiGraphics.fill(x, y, x + width, y + TOP_BAR_HEIGHT, OVERLAY_COLOR);
        final int backX = this.backButtonX(x);
        final int backY = this.backButtonY(y);
        this.renderButton(
                guiGraphics,
                font,
                Component.translatable("button.skyresources.guide.structure_back"),
                Component.translatable("button.skyresources.guide.structure_back"),
                mouseX,
                mouseY,
                backX,
                backY,
                BACK_BUTTON_WIDTH,
                BUTTON_HEIGHT
        );

        final Component step = Component.translatable(
                "screen.skyresources.guide.structure_scene_step",
                visibleCount,
                structure.blocks().size()
        );
        final String fittedStep = this.truncate(font, step.getString(), Math.max(24, width / 4));
        final int stepX = x + width - EDGE_PADDING - font.width(fittedStep);
        guiGraphics.drawString(font, fittedStep, stepX, y + 9, MUTED_TEXT_COLOR, false);

        final int titleX = backX + BACK_BUTTON_WIDTH + 10;
        final int titleWidth = stepX - titleX - 8;
        if (titleWidth > 12) {
            guiGraphics.drawString(
                    font,
                    this.truncate(font, structure.title().getString(), titleWidth),
                    titleX,
                    y + 9,
                    TEXT_COLOR,
                    false
            );
        }
    }

    private void renderBottomOverlay(
            final GuiGraphics guiGraphics,
            final Font font,
            final int mouseX,
            final int mouseY,
            final int x,
            final int y,
            final int width,
            final int height,
            final GuideStructure structure,
            final int visibleCount
    ) {
        final int overlayY = y + height - BOTTOM_BAR_HEIGHT;
        guiGraphics.fill(x, overlayY, x + width, y + height, OVERLAY_COLOR);
        this.renderControls(guiGraphics, font, mouseX, mouseY, this.controlsX(x, width), this.controlsY(y, height));
        this.renderTimeline(
                guiGraphics,
                font,
                mouseX,
                mouseY,
                structure,
                this.timelineX(x),
                this.timelineY(y, height),
                this.timelineWidth(width),
                visibleCount
        );
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
        final int size = tile.size();
        final boolean hovered = this.isInside(mouseX, mouseY, drawX, drawY, size, size);
        guiGraphics.fill(drawX + size / 6, drawY + size / 6, drawX + size * 5 / 6, drawY + size * 5 / 6, GHOST_BLOCK_COLOR);
        guiGraphics.renderOutline(
                drawX + size / 6,
                drawY + size / 6,
                size * 2 / 3,
                size * 2 / 3,
                hovered ? ACTIVE_BLOCK_COLOR : BORDER_COLOR
        );
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
        final int size = tile.size();
        final boolean hovered = this.isInside(mouseX, mouseY, drawX, drawY, size, size);
        if (active || hovered) {
            guiGraphics.fill(
                    drawX - 2,
                    drawY - 2,
                    drawX + size + 2,
                    drawY + size + 2,
                    active ? ACTIVE_BLOCK_COLOR : HOVERED_BLOCK_COLOR
            );
        }
        this.renderScaledItem(guiGraphics, icon, drawX, drawY, size);
        if (active || hovered) {
            guiGraphics.renderOutline(drawX - 2, drawY - 2, size + 4, size + 4, active ? ACTIVE_BLOCK_COLOR : BORDER_COLOR);
        }
        if (hovered) {
            guiGraphics.setTooltipForNextFrame(font, this.blockTooltip(block, icon), mouseX, mouseY);
        }
    }

    private void renderScaledItem(
            final GuiGraphics guiGraphics,
            final ItemStack icon,
            final int x,
            final int y,
            final int size
    ) {
        final float scale = size / (float) BASE_ICON_SIZE;
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x, y);
        guiGraphics.pose().scale(scale, scale);
        guiGraphics.renderFakeItem(icon, 0, 0);
        guiGraphics.pose().popMatrix();
    }

    private void renderControls(
            final GuiGraphics guiGraphics,
            final Font font,
            final int mouseX,
            final int mouseY,
            final int x,
            final int y
    ) {
        for (final StructureControl control : this.controls(x, y)) {
            this.renderButton(
                    guiGraphics,
                    font,
                    control.label(),
                    control.tooltip(),
                    mouseX,
                    mouseY,
                    control.x(),
                    control.y(),
                    control.width(),
                    control.height()
            );
        }
    }

    private void renderButton(
            final GuiGraphics guiGraphics,
            final Font font,
            final Component label,
            final Component tooltip,
            final int mouseX,
            final int mouseY,
            final int x,
            final int y,
            final int width,
            final int height
    ) {
        final boolean hovered = this.isInside(mouseX, mouseY, x, y, width, height);
        guiGraphics.fill(x, y, x + width, y + height, hovered ? HOVERED_BUTTON_COLOR : BUTTON_COLOR);
        guiGraphics.renderOutline(x, y, width, height, BORDER_COLOR);
        this.drawCenteredTruncatedString(guiGraphics, font, label, x + width / 2, y + (height - font.lineHeight) / 2, width - 4, TEXT_COLOR);
        if (hovered) {
            guiGraphics.setTooltipForNextFrame(font, tooltip, mouseX, mouseY);
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
        if (width <= 0 || structure.blocks().isEmpty()) {
            return;
        }
        guiGraphics.fill(x, y, x + width, y + TIMELINE_HEIGHT, TIMELINE_COLOR);
        final int progressWidth = Math.max(1, width * visibleCount / Math.max(1, structure.blocks().size()));
        guiGraphics.fill(x, y, x + progressWidth, y + TIMELINE_HEIGHT, TIMELINE_PROGRESS_COLOR);
        final int marks = Math.min(structure.blocks().size(), 24);
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

    private List<StructureTile> layoutTiles(final GuideStructure structure, final int tileSize, final int visibleCount) {
        final List<GuideStructure.BlockEntry> blocks = structure.blocks();
        final int view = Math.floorMod(this.viewQuarter, 4);
        final StructureBounds bounds = this.bounds(blocks, view);
        final int minY = bounds.minY();
        final int stepX = Math.max(10, tileSize * 3 / 4);
        final int stepY = Math.max(5, tileSize / 3);
        final int layerStep = Math.max(8, tileSize * 2 / 3);
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
                    index,
                    tileSize
            ));
        }
        tiles.sort(Comparator.comparingInt((StructureTile tile) -> tile.block().y())
                .thenComparingInt(StructureTile::depth)
                .thenComparingInt(StructureTile::secondary)
                .thenComparingInt(tile -> Math.abs(tile.index() - visibleCount)));
        return tiles;
    }

    private int tileSize(final GuideStructure structure, final int width, final int height) {
        if (structure.blocks().isEmpty()) {
            return MIN_TILE_SIZE;
        }
        final StructureBounds bounds = this.bounds(structure.blocks(), Math.floorMod(this.viewQuarter, 4));
        final int horizontalUnits = Math.max(1, bounds.maxX() - bounds.minX() + bounds.maxZ() - bounds.minZ() + 2);
        final int verticalUnits = Math.max(1, bounds.maxX() + bounds.maxZ() - bounds.minX() - bounds.minZ()
                + (bounds.maxY() - bounds.minY()) * 2 + 3);
        final int byWidth = width / Math.max(1, horizontalUnits);
        final int byHeight = height * 2 / Math.max(2, verticalUnits);
        return Math.max(MIN_TILE_SIZE, Math.min(MAX_TILE_SIZE, Math.min(byWidth, byHeight)));
    }

    private StructureBounds bounds(final List<GuideStructure.BlockEntry> blocks, final int view) {
        if (blocks.isEmpty()) {
            return new StructureBounds(0, 0, 0, 0, 0, 0);
        }
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
        return new StructureBounds(minX, maxX, minY, maxY, minZ, maxZ);
    }

    private boolean selectControlAt(
            final double mouseX,
            final double mouseY,
            final int x,
            final int y,
            final int width,
            final int height,
            final GuideStructure structure
    ) {
        for (final StructureControl control : this.controls(this.controlsX(x, width), this.controlsY(y, height))) {
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
            final int x,
            final int y,
            final int width,
            final int height,
            final GuideStructure structure
    ) {
        if (structure.blocks().isEmpty()) {
            return false;
        }
        final int timelineX = this.timelineX(x);
        final int timelineY = this.timelineY(y, height);
        final int timelineWidth = this.timelineWidth(width);
        if (!this.isInside((int) mouseX, (int) mouseY, timelineX, timelineY - 4, timelineWidth, TIMELINE_HEIGHT + 8)) {
            return false;
        }
        final int totalBlocks = structure.blocks().size();
        final int selected = Math.max(1, Math.min(totalBlocks, 1 + (int) ((mouseX - timelineX) * totalBlocks / Math.max(1, timelineWidth))));
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

    private int backButtonX(final int x) {
        return x + EDGE_PADDING;
    }

    private int backButtonY(final int y) {
        return y + 4;
    }

    private int controlsX(final int x, final int width) {
        return x + Math.max(EDGE_PADDING, (width - this.controlsWidth()) / 2);
    }

    private int controlsY(final int y, final int height) {
        return y + height - BOTTOM_BAR_HEIGHT + 8;
    }

    private int timelineX(final int x) {
        return x + EDGE_PADDING;
    }

    private int timelineY(final int y, final int height) {
        return y + height - 12;
    }

    private int timelineWidth(final int width) {
        return Math.max(0, width - EDGE_PADDING * 2);
    }

    private int controlsWidth() {
        return VIEW_CONTROL_WIDTH * 2 + CONTROL_WIDTH * 4 + CONTROL_GAP * 5;
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

    enum ClickResult {
        NONE,
        HANDLED,
        BACK
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

    private record StructureBounds(
            int minX,
            int maxX,
            int minY,
            int maxY,
            int minZ,
            int maxZ
    ) {
    }

    private record StructureTile(
            GuideStructure.BlockEntry block,
            int x,
            int y,
            int depth,
            int secondary,
            int index,
            int size
    ) {
    }
}
