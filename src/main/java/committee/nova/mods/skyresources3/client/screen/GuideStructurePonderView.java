package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.core.guide.GuideStructure;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

final class GuideStructurePonderView {
    private static final int EDGE_PADDING = 20;
    private static final int TOP_OVERLAY_HEIGHT = 48;
    private static final int BOTTOM_OVERLAY_HEIGHT = 64;
    private static final int BACK_BUTTON_WIDTH = 44;
    private static final int BACK_BUTTON_HEIGHT = 22;
    private static final int CONTROL_WIDTH = 34;
    private static final int CONTROL_HEIGHT = 22;
    private static final int CONTROL_GAP = 8;
    private static final int TIMELINE_HEIGHT = 4;
    private static final int ICON_CELL = 24;
    private static final int CONTROL_TEXT = 0xFFE9EEF2;
    private static final int OVERLAY_BACKGROUND = 0x9A0C1014;
    private static final int SCENE_BACKGROUND = 0xEE10151B;
    private static final int TIMELINE_TRACK = 0x772E3742;
    private static final int TIMELINE_ACTIVE = 0xFFE4B64A;
    private static final int TIMELINE_KNOB = 0xFFF7E7B1;
    private static final int STEP_MILLIS = 850;

    private boolean paused;
    private int manualStep;
    private long startedAtMillis;

    void open() {
        this.paused = false;
        this.manualStep = 1;
        this.startedAtMillis = System.currentTimeMillis();
    }

    void clear() {
        this.paused = false;
        this.manualStep = 1;
        this.startedAtMillis = 0L;
    }

    void render(final GuiGraphics guiGraphics, final Font font, final int mouseX, final int mouseY,
                final int x, final int y, final int width, final int height, final GuideStructure structure) {
        final int maxStep = this.layerCount(structure);
        final int currentStep = this.currentStep(maxStep);
        this.renderScene(guiGraphics, font, structure, x, y, width, height, currentStep);
        this.renderTopOverlay(guiGraphics, font, structure, mouseX, mouseY, x, y, width, currentStep, maxStep);
        this.renderBottomOverlay(guiGraphics, font, mouseX, mouseY, x, y, width, height, currentStep, maxStep);
    }

    ClickResult mouseClicked(final double mouseX, final double mouseY, final int x, final int y,
                             final int width, final int height, final GuideStructure structure) {
        if (isInside(mouseX, mouseY, x + EDGE_PADDING, y + 13, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT)) {
            return ClickResult.BACK;
        }

        final StructureControlAction control = this.selectControlAt(mouseX, mouseY, x, y, width, height);
        if (control != null) {
            this.handleControl(control, this.layerCount(structure));
            return ClickResult.HANDLED;
        }

        final Integer timelineStep = this.selectTimelineAt(mouseX, mouseY, x, y, width, height, this.layerCount(structure));
        if (timelineStep != null) {
            this.paused = true;
            this.manualStep = timelineStep;
            return ClickResult.HANDLED;
        }

        return isInside(mouseX, mouseY, this.sceneX(x), this.sceneY(y), this.sceneWidth(width), this.sceneHeight(height))
                ? ClickResult.HANDLED
                : ClickResult.NONE;
    }

    boolean mouseReleased(final int button) {
        return false;
    }

    boolean mouseDragged(final double mouseX, final double mouseY) {
        return false;
    }

    boolean mouseScrolled(final double mouseX, final double mouseY, final int x, final int y, final int width, final int height) {
        return isInside(mouseX, mouseY, this.sceneX(x), this.sceneY(y), this.sceneWidth(width), this.sceneHeight(height));
    }

    private void renderScene(final GuiGraphics guiGraphics, final Font font, final GuideStructure structure,
                             final int x, final int y, final int width, final int height, final int visibleCount) {
        final int sceneX = this.sceneX(x);
        final int sceneY = this.sceneY(y);
        final int sceneWidth = this.sceneWidth(width);
        final int sceneHeight = this.sceneHeight(height);
        guiGraphics.fill(sceneX, sceneY, sceneX + sceneWidth, sceneY + sceneHeight, SCENE_BACKGROUND);

        if (structure.blocks().isEmpty() || visibleCount <= 0) {
            return;
        }

        final StructureBounds bounds = StructureBounds.from(structure);
        final int visibleLayer = bounds.minY() + visibleCount - 1;
        final int spanX = bounds.maxX() - bounds.minX() + 1;
        final int spanZ = bounds.maxZ() - bounds.minZ() + 1;
        final int gridWidth = spanX * ICON_CELL;
        final int gridHeight = spanZ * ICON_CELL;
        final int originX = sceneX + Math.max(8, (sceneWidth - gridWidth) / 2);
        final int originY = sceneY + Math.max(10, (sceneHeight - gridHeight) / 2);

        for (final GuideStructure.BlockEntry entry : structure.blocks()) {
            if (entry.y() > visibleLayer) {
                continue;
            }
            final ItemStack icon = entry.icon();
            if (icon.isEmpty()) {
                continue;
            }
            final int iconX = originX + (entry.x() - bounds.minX()) * ICON_CELL;
            final int iconY = originY + (entry.z() - bounds.minZ()) * ICON_CELL - (entry.y() - bounds.minY()) * 5;
            if (iconX < sceneX || iconY < sceneY || iconX + 16 > sceneX + sceneWidth || iconY + 16 > sceneY + sceneHeight) {
                continue;
            }
            guiGraphics.renderItem(icon, iconX, iconY);
            guiGraphics.renderItemDecorations(font, icon, iconX, iconY);
        }
    }

    private void renderTopOverlay(final GuiGraphics guiGraphics, final Font font, final GuideStructure structure,
                                  final int mouseX, final int mouseY, final int x, final int y, final int width,
                                  final int currentStep, final int maxStep) {
        guiGraphics.fill(x, y, x + width, y + TOP_OVERLAY_HEIGHT, OVERLAY_BACKGROUND);
        final int buttonX = x + EDGE_PADDING;
        final int buttonY = y + 13;
        this.drawButton(guiGraphics, font, buttonX, buttonY, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT,
                Component.translatable("button.skyresources.guide.structure_back"),
                isInside(mouseX, mouseY, buttonX, buttonY, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT),
                SkyResourcesButton.Tone.DEFAULT);

        guiGraphics.drawCenteredString(font, Component.translatable(structure.titleKey()), x + width / 2, y + 10, CONTROL_TEXT);
        guiGraphics.drawCenteredString(
                font,
                Component.translatable("screen.skyresources.guide.structure_scene_layer", currentStep, maxStep),
                x + width / 2,
                y + 28,
                0xFFB8C0C8
        );
    }

    private void renderBottomOverlay(final GuiGraphics guiGraphics, final Font font, final int mouseX, final int mouseY,
                                     final int x, final int y, final int width, final int height,
                                     final int currentStep, final int maxStep) {
        final int overlayY = y + height - BOTTOM_OVERLAY_HEIGHT;
        guiGraphics.fill(x, overlayY, x + width, y + height, OVERLAY_BACKGROUND);

        for (final StructureControl control : this.controls(x, y, width, height)) {
            this.drawButton(guiGraphics, font, control.x(), control.y(), control.width(), control.height(), control.label(),
                    isInside(mouseX, mouseY, control.x(), control.y(), control.width(), control.height()),
                    SkyResourcesButton.Tone.QUIET);
        }

        final int timelineX = x + Math.max(EDGE_PADDING, width / 5);
        final int timelineW = width - (timelineX - x) * 2;
        final int timelineY = y + height - 16;
        guiGraphics.fill(timelineX, timelineY, timelineX + timelineW, timelineY + TIMELINE_HEIGHT, TIMELINE_TRACK);
        final int activeW = maxStep <= 0
                ? 0
                : maxStep == 1 ? timelineW : Math.round(((currentStep - 1) / (float) (maxStep - 1)) * timelineW);
        guiGraphics.fill(timelineX, timelineY, timelineX + activeW, timelineY + TIMELINE_HEIGHT, TIMELINE_ACTIVE);
        final int knobX = timelineX + activeW;
        guiGraphics.fill(knobX - 2, timelineY - 3, knobX + 2, timelineY + TIMELINE_HEIGHT + 3, TIMELINE_KNOB);
    }

    private void drawButton(final GuiGraphics guiGraphics, final Font font, final int x, final int y, final int width,
                            final int height, final Component label, final boolean hovered,
                            final SkyResourcesButton.Tone tone) {
        SkyResourcesButton.renderFrame(guiGraphics, x, y, width, height, true, hovered, 1.0F, tone);
        SkyResourcesButton.renderLabel(guiGraphics, font, label, x, y, width, height, true, 1.0F, tone);
    }

    private int layerCount(final GuideStructure structure) {
        return structure.blocks().isEmpty() ? 0 : StructureBounds.from(structure).layerCount();
    }

    private int currentStep(final int maxStep) {
        if (maxStep <= 0) {
            return 0;
        }
        if (this.paused) {
            return Math.max(1, Math.min(this.manualStep, maxStep));
        }
        final long elapsed = Math.max(0L, System.currentTimeMillis() - this.startedAtMillis);
        return (int) ((elapsed / STEP_MILLIS) % maxStep) + 1;
    }

    private StructureControlAction selectControlAt(final double mouseX, final double mouseY,
                                                   final int x, final int y, final int width, final int height) {
        for (final StructureControl control : this.controls(x, y, width, height)) {
            if (isInside(mouseX, mouseY, control.x(), control.y(), control.width(), control.height())) {
                return control.action();
            }
        }
        return null;
    }

    private Integer selectTimelineAt(final double mouseX, final double mouseY,
                                     final int x, final int y, final int width, final int height, final int maxStep) {
        if (maxStep <= 0) {
            return null;
        }
        final int timelineX = x + Math.max(EDGE_PADDING, width / 5);
        final int timelineW = width - (timelineX - x) * 2;
        final int timelineY = y + height - 18;
        if (!isInside(mouseX, mouseY, timelineX, timelineY, timelineW, 12)) {
            return null;
        }
        final float progress = (float) ((mouseX - timelineX) / Math.max(1.0D, timelineW));
        return maxStep == 1 ? 1 : Math.round(clamp(progress, 0.0F, 1.0F) * (maxStep - 1)) + 1;
    }

    private void handleControl(final StructureControlAction action, final int maxStep) {
        switch (action) {
            case PREVIOUS -> {
                this.paused = true;
                this.manualStep = Math.max(1, this.currentStep(maxStep) - 1);
            }
            case RESTART -> this.open();
            case TOGGLE -> {
                final int step = this.currentStep(maxStep);
                if (this.paused) {
                    this.paused = false;
                    this.startedAtMillis = System.currentTimeMillis() - (long) Math.max(0, step - 1) * STEP_MILLIS;
                } else {
                    this.paused = true;
                    this.manualStep = step;
                }
            }
            case NEXT -> {
                this.paused = true;
                this.manualStep = Math.min(maxStep, this.currentStep(maxStep) + 1);
            }
        }
    }

    private List<StructureControl> controls(final int x, final int y, final int width, final int height) {
        final List<StructureControl> controls = new ArrayList<>();
        final int controlsWidth = CONTROL_WIDTH * 4 + CONTROL_GAP * 3;
        int controlX = x + (width - controlsWidth) / 2;
        final int controlY = y + height - BOTTOM_OVERLAY_HEIGHT + 14;
        controls.add(new StructureControl(controlX, controlY, CONTROL_WIDTH, CONTROL_HEIGHT,
                Component.translatable("button.skyresources.guide.structure_prev_step_short"), StructureControlAction.PREVIOUS));
        controlX += CONTROL_WIDTH + CONTROL_GAP;
        controls.add(new StructureControl(controlX, controlY, CONTROL_WIDTH, CONTROL_HEIGHT,
                Component.translatable("button.skyresources.guide.structure_restart_short"), StructureControlAction.RESTART));
        controlX += CONTROL_WIDTH + CONTROL_GAP;
        controls.add(new StructureControl(controlX, controlY, CONTROL_WIDTH, CONTROL_HEIGHT,
                Component.translatable(this.paused
                        ? "button.skyresources.guide.structure_play_short"
                        : "button.skyresources.guide.structure_pause_short"), StructureControlAction.TOGGLE));
        controlX += CONTROL_WIDTH + CONTROL_GAP;
        controls.add(new StructureControl(controlX, controlY, CONTROL_WIDTH, CONTROL_HEIGHT,
                Component.translatable("button.skyresources.guide.structure_next_step_short"), StructureControlAction.NEXT));
        return controls;
    }

    private int sceneX(final int x) {
        return x + EDGE_PADDING;
    }

    private int sceneY(final int y) {
        return y + TOP_OVERLAY_HEIGHT;
    }

    private int sceneWidth(final int width) {
        return Math.max(1, width - EDGE_PADDING * 2);
    }

    private int sceneHeight(final int height) {
        return Math.max(1, height - TOP_OVERLAY_HEIGHT - BOTTOM_OVERLAY_HEIGHT);
    }

    private static boolean isInside(final double mouseX, final double mouseY, final int x, final int y,
                                    final int width, final int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private static float clamp(final float value, final float min, final float max) {
        return Math.max(min, Math.min(max, value));
    }

    enum ClickResult {
        NONE,
        HANDLED,
        BACK
    }

    private enum StructureControlAction {
        PREVIOUS,
        RESTART,
        TOGGLE,
        NEXT
    }

    private record StructureControl(int x, int y, int width, int height, Component label, StructureControlAction action) {
    }

    private record StructureBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        static StructureBounds from(final GuideStructure structure) {
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            int maxZ = Integer.MIN_VALUE;
            for (final GuideStructure.BlockEntry entry : structure.blocks()) {
                minX = Math.min(minX, entry.x());
                minY = Math.min(minY, entry.y());
                minZ = Math.min(minZ, entry.z());
                maxX = Math.max(maxX, entry.x());
                maxY = Math.max(maxY, entry.y());
                maxZ = Math.max(maxZ, entry.z());
            }
            return new StructureBounds(minX, minY, minZ, maxX, maxY, maxZ);
        }

        int layerCount() {
            return this.maxY - this.minY + 1;
        }
    }
}
