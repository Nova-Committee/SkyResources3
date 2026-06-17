package committee.nova.mods.skyresources3.client.screen;

import committee.nova.mods.skyresources3.common.network.IslandGuiActionPayload;
import committee.nova.mods.skyresources3.common.network.IslandGuiRequestPayload;
import committee.nova.mods.skyresources3.common.network.IslandGuiStatePayload;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public final class IslandGuiScreen extends Screen {
    private static final int PANEL_MAX_WIDTH = 640;
    private static final int PANEL_MAX_HEIGHT = 360;
    private static final int PANEL_PADDING = 14;
    private static final int HEADER_HEIGHT = 30;
    private static final int STATUS_WIDTH = 214;
    private static final int BUTTON_HEIGHT = 21;
    private static final int BUTTON_GAP = 6;
    private static final int TEXT_COLOR = 0xFFF4EBD6;
    private static final int MUTED_TEXT_COLOR = 0xFFC8BEA8;
    private static final int WARNING_TEXT_COLOR = 0xFFFFD36D;
    private static final int PANEL_COLOR = 0xEE101419;
    private static final int PANEL_HEADER_COLOR = 0xF0202529;
    private static final int INNER_PANEL_COLOR = 0xD5161B1F;
    private static final int FIELD_COLOR = 0xDD07090B;
    private static final int BORDER_COLOR = 0xFF75846F;
    private static final int INNER_BORDER_COLOR = 0x884C574F;
    private static final int SECTION_COLOR = 0xFFA8CF74;
    private static final int GOLD_COLOR = 0xFFE2BD56;
    private static final int SHADOW_COLOR = 0x88000000;

    private IslandGuiStatePayload state;
    private EditBox playerBox;
    private int templateIndex;
    private String confirmAction = "";

    private IslandGuiScreen(final IslandGuiStatePayload state) {
        super(Component.translatable("screen.skyresources.island.title"));
        this.state = state;
        this.templateIndex = templateIndex(state);
    }

    public static void open(final IslandGuiStatePayload state) {
        final Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
            if (minecraft.screen instanceof IslandGuiScreen screen) {
                screen.updateState(state);
                return;
            }
            minecraft.setScreen(new IslandGuiScreen(state));
        });
    }

    public static void requestOpen() {
        ClientPacketDistributor.sendToServer(new IslandGuiRequestPayload());
    }

    @Override
    protected void init() {
        this.rebuildIslandWidgets("");
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        guiGraphics.fill(0, 0, this.width, this.height, 0x9A000000);

        final int panelX = this.panelX();
        final int panelY = this.panelY();
        final int panelWidth = this.panelWidth();
        final int panelHeight = this.panelHeight();
        this.renderMainPanel(guiGraphics, panelX, panelY, panelWidth, panelHeight);
        this.drawCentered(guiGraphics, this.title, panelX + panelWidth / 2, panelY + 10, GOLD_COLOR);

        this.renderStatus(guiGraphics);
        this.renderActionLabels(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void updateState(final IslandGuiStatePayload nextState) {
        final String playerName = this.playerBox == null ? "" : this.playerBox.getValue();
        this.state = nextState;
        this.templateIndex = templateIndex(nextState);
        this.confirmAction = "";
        if (this.minecraft != null) {
            this.rebuildIslandWidgets(playerName);
        }
    }

    private void rebuildIslandWidgets(final String playerName) {
        this.clearWidgets();
        final int panelX = this.panelX();
        final int panelY = this.panelY();
        final int panelHeight = this.panelHeight();
        final int actionX = this.actionX();
        final int actionWidth = this.actionWidth();
        final int buttonWidth = (actionWidth - BUTTON_GAP) / 2;
        final int compactButtonWidth = (actionWidth - BUTTON_GAP * 3) / 4;
        final int compactStep = compactButtonWidth + BUTTON_GAP;
        int y = this.contentTop() + 22;

        this.addButton("button.skyresources.island.previous_template",
                "tooltip.skyresources.island.previous_template",
                actionX,
                y,
                28,
                button -> this.changeTemplate(-1),
                this.state.enabled() && !this.state.templates().isEmpty(),
                SkyResourcesButton.Tone.QUIET
        );
        this.addButton("button.skyresources.island.next_template",
                "tooltip.skyresources.island.next_template",
                actionX + actionWidth - 28,
                y,
                28,
                button -> this.changeTemplate(1),
                this.state.enabled() && !this.state.templates().isEmpty(),
                SkyResourcesButton.Tone.QUIET
        );
        y += 42;

        this.playerBox = new EditBox(
                this.font,
                actionX + 6,
                y + 4,
                actionWidth - 12,
                BUTTON_HEIGHT - 6,
                Component.translatable("screen.skyresources.island.player_input")
        );
        this.playerBox.setMaxLength(40);
        this.playerBox.setHint(Component.translatable("screen.skyresources.island.player_hint"));
        this.playerBox.setValue(playerName);
        this.playerBox.setBordered(false);
        this.playerBox.setTextColor(TEXT_COLOR);
        this.playerBox.setTextColorUneditable(MUTED_TEXT_COLOR);
        this.addRenderableWidget(this.playerBox);
        y += BUTTON_HEIGHT + 24;

        this.addButton("button.skyresources.island.create",
                this.state.hasIsland()
                        ? "tooltip.skyresources.island.create_disabled"
                        : "tooltip.skyresources.island.create",
                actionX,
                y,
                compactButtonWidth,
                button -> this.send(IslandGuiActionPayload.Action.CREATE, this.selectedTemplate()),
                this.state.enabled() && !this.state.hasIsland(),
                SkyResourcesButton.Tone.PRIMARY
        );
        this.addButton(this.confirmLabel("reset", "button.skyresources.island.reset"),
                "tooltip.skyresources.island.reset",
                actionX + compactStep,
                y,
                compactButtonWidth,
                button -> this.confirmOrSend("reset", IslandGuiActionPayload.Action.RESET, this.selectedTemplate()),
                this.state.enabled() && this.state.owner()
        );
        this.addButton("button.skyresources.island.home",
                "tooltip.skyresources.island.home",
                actionX + compactStep * 2,
                y,
                compactButtonWidth,
                button -> this.send(IslandGuiActionPayload.Action.HOME),
                this.state.enabled() && this.state.hasIsland()
        );
        this.addButton("button.skyresources.island.spawn",
                "tooltip.skyresources.island.spawn",
                actionX + compactStep * 3,
                y,
                compactButtonWidth,
                button -> this.send(IslandGuiActionPayload.Action.SPAWN),
                this.state.enabled()
        );
        y += BUTTON_HEIGHT + BUTTON_GAP;

        this.addButton("button.skyresources.island.info",
                "tooltip.skyresources.island.info",
                actionX,
                y,
                compactButtonWidth,
                button -> this.send(IslandGuiActionPayload.Action.INFO),
                this.state.enabled() && this.state.hasIsland()
        );
        this.addButton("button.skyresources.island.trusted",
                "tooltip.skyresources.island.trusted",
                actionX + compactStep,
                y,
                compactButtonWidth,
                button -> this.send(IslandGuiActionPayload.Action.TRUSTED),
                this.state.enabled() && this.state.owner()
        );
        this.addButton("button.skyresources.island.visit",
                "tooltip.skyresources.island.visit",
                actionX + compactStep * 2,
                y,
                compactButtonWidth,
                button -> this.sendWithPlayer(IslandGuiActionPayload.Action.VISIT),
                this.state.enabled()
        );
        this.addButton("button.skyresources.island.invite",
                "tooltip.skyresources.island.invite",
                actionX + compactStep * 3,
                y,
                compactButtonWidth,
                button -> this.sendWithPlayer(IslandGuiActionPayload.Action.INVITE),
                this.state.enabled() && this.state.owner()
        );
        y += BUTTON_HEIGHT + BUTTON_GAP;

        this.addButton("button.skyresources.island.trust",
                "tooltip.skyresources.island.trust",
                actionX,
                y,
                compactButtonWidth,
                button -> this.sendWithPlayer(IslandGuiActionPayload.Action.TRUST),
                this.state.enabled() && this.state.owner()
        );
        this.addButton("button.skyresources.island.untrust",
                "tooltip.skyresources.island.untrust",
                actionX + compactStep,
                y,
                compactButtonWidth,
                button -> this.sendWithPlayer(IslandGuiActionPayload.Action.UNTRUST),
                this.state.enabled() && this.state.owner()
        );
        this.addButton("button.skyresources.island.accept",
                "tooltip.skyresources.island.accept",
                actionX + compactStep * 2,
                y,
                compactButtonWidth,
                button -> this.send(IslandGuiActionPayload.Action.ACCEPT),
                this.state.enabled() && this.state.hasPendingInvite() && !this.state.hasIsland()
        );
        this.addButton("button.skyresources.island.refresh",
                "tooltip.skyresources.island.refresh",
                actionX + compactStep * 3,
                y,
                compactButtonWidth,
                button -> this.send(IslandGuiActionPayload.Action.REFRESH),
                true,
                SkyResourcesButton.Tone.QUIET
        );

        final int dangerY = panelY + panelHeight - PANEL_PADDING - BUTTON_HEIGHT;
        this.addButton(this.confirmLabel("leave", "button.skyresources.island.leave"),
                "tooltip.skyresources.island.leave",
                actionX,
                dangerY,
                buttonWidth,
                button -> this.confirmOrSend("leave", IslandGuiActionPayload.Action.LEAVE, ""),
                this.state.enabled() && this.state.member(),
                SkyResourcesButton.Tone.DANGER
        );
        this.addButton(this.confirmLabel("disband", "button.skyresources.island.disband"),
                "tooltip.skyresources.island.disband",
                actionX + buttonWidth + BUTTON_GAP,
                dangerY,
                buttonWidth,
                button -> this.confirmOrSend("disband", IslandGuiActionPayload.Action.DISBAND, ""),
                this.state.enabled() && this.state.owner(),
                SkyResourcesButton.Tone.DANGER
        );
    }

    private void renderStatus(final GuiGraphics guiGraphics) {
        final int x = this.panelX() + PANEL_PADDING;
        final int y = this.contentTop();
        final int width = this.statusWidth();
        final int panelBottom = this.panelY() + this.panelHeight() - PANEL_PADDING;
        this.renderInsetPanel(guiGraphics, x - 6, y - 8, width + 12, panelBottom - y + 8);

        int lineY = y;
        lineY = this.drawSectionHeader(
                guiGraphics,
                Component.translatable("screen.skyresources.island.section.status"),
                x,
                lineY,
                width,
                SECTION_COLOR
        );
        if (!this.state.enabled()) {
            this.drawWrapped(guiGraphics, Component.translatable("screen.skyresources.island.status.disabled"), x, lineY, width, WARNING_TEXT_COLOR, 4);
            return;
        }
        if (!this.state.hasIsland()) {
            lineY = this.drawWrapped(guiGraphics, Component.translatable("screen.skyresources.island.status.none"), x, lineY, width, TEXT_COLOR, 4);
            if (this.state.hasPendingInvite()) {
                this.drawWrapped(
                        guiGraphics,
                        Component.translatable("screen.skyresources.island.status.pending", this.state.pendingInviteOwner()),
                        x,
                        lineY + 4,
                        width,
                        WARNING_TEXT_COLOR,
                        3
                );
            }
            return;
        }

        final Component relation = this.state.owner()
                ? Component.translatable("screen.skyresources.island.status.owner")
                : Component.translatable("screen.skyresources.island.status.member", this.state.ownerName());
        lineY = this.drawWrapped(guiGraphics, relation, x, lineY, width, TEXT_COLOR, 2);
        lineY = this.drawLine(
                guiGraphics,
                Component.translatable("screen.skyresources.island.status.type", this.templateName(this.state.islandType())),
                x,
                lineY + 2,
                MUTED_TEXT_COLOR
        );
        lineY = this.drawWrapped(
                guiGraphics,
                Component.translatable("screen.skyresources.island.status.dimension", this.state.dimension()),
                x,
                lineY,
                width,
                MUTED_TEXT_COLOR,
                2
        );
        lineY = this.drawLine(guiGraphics, Component.translatable("screen.skyresources.island.status.home", this.state.home()), x, lineY, MUTED_TEXT_COLOR);
        lineY = this.drawWrapped(
                guiGraphics,
                Component.translatable("screen.skyresources.island.status.players", this.joined(this.state.players())),
                x,
                lineY + 4,
                width,
                TEXT_COLOR,
                3
        );
        lineY = this.drawWrapped(
                guiGraphics,
                Component.translatable("screen.skyresources.island.status.invites", this.joined(this.state.invites())),
                x,
                lineY + 2,
                width,
                MUTED_TEXT_COLOR,
                2
        );
        this.drawWrapped(
                guiGraphics,
                Component.translatable("screen.skyresources.island.status.trusted", this.joined(this.state.trustedVisitors())),
                x,
                lineY + 2,
                width,
                MUTED_TEXT_COLOR,
                3
        );
    }

    private void renderActionLabels(final GuiGraphics guiGraphics) {
        final int actionX = this.actionX();
        final int actionWidth = this.actionWidth();
        final int top = this.contentTop();
        final int bottom = this.panelY() + this.panelHeight() - PANEL_PADDING;
        this.renderInsetPanel(guiGraphics, actionX - 8, top - 8, actionWidth + 16, bottom - top + 8);

        int y = top;
        y = this.drawSectionHeader(
                guiGraphics,
                Component.translatable("screen.skyresources.island.section.template"),
                actionX,
                y,
                actionWidth,
                SECTION_COLOR
        );
        this.renderTemplateSelector(guiGraphics, actionX, y + 4, actionWidth);
        this.drawCentered(
                guiGraphics,
                this.templateName(this.selectedTemplate()),
                actionX + actionWidth / 2,
                y + 10,
                TEXT_COLOR
        );

        final int inputY = this.playerBox == null ? top + 64 : this.playerBox.getY() - 4;
        this.drawLine(
                guiGraphics,
                Component.translatable("screen.skyresources.island.player_input"),
                actionX,
                inputY - 12,
                MUTED_TEXT_COLOR
        );
        this.renderInputFrame(guiGraphics, actionX, inputY, actionWidth);

        final int actionLabelY = inputY + BUTTON_HEIGHT + 6;
        this.drawSectionHeader(
                guiGraphics,
                Component.translatable("screen.skyresources.island.section.actions"),
                actionX,
                actionLabelY,
                actionWidth,
                SECTION_COLOR
        );

    }

    private void renderMainPanel(
            final GuiGraphics guiGraphics,
            final int x,
            final int y,
            final int width,
            final int height
    ) {
        guiGraphics.fill(x + 4, y + 5, x + width + 4, y + height + 5, SHADOW_COLOR);
        guiGraphics.fill(x, y, x + width, y + height, PANEL_COLOR);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + HEADER_HEIGHT, PANEL_HEADER_COLOR);
        guiGraphics.renderOutline(x, y, width, height, BORDER_COLOR);
        guiGraphics.renderOutline(x + 2, y + 2, width - 4, height - 4, INNER_BORDER_COLOR);
        guiGraphics.fill(x + 2, y + HEADER_HEIGHT, x + width - 2, y + HEADER_HEIGHT + 1, GOLD_COLOR);
        this.renderPanelCorner(guiGraphics, x + 4, y + 4);
        this.renderPanelCorner(guiGraphics, x + width - 11, y + 4);
        this.renderPanelCorner(guiGraphics, x + 4, y + height - 11);
        this.renderPanelCorner(guiGraphics, x + width - 11, y + height - 11);
    }

    private void renderPanelCorner(final GuiGraphics guiGraphics, final int x, final int y) {
        guiGraphics.fill(x, y, x + 7, y + 7, 0xFF32381F);
        guiGraphics.renderOutline(x, y, 7, 7, GOLD_COLOR);
        guiGraphics.fill(x + 2, y + 2, x + 5, y + 5, 0xFFD8A83D);
    }

    private void renderInsetPanel(
            final GuiGraphics guiGraphics,
            final int x,
            final int y,
            final int width,
            final int height
    ) {
        guiGraphics.fill(x, y, x + width, y + height, INNER_PANEL_COLOR);
        guiGraphics.renderOutline(x, y, width, height, INNER_BORDER_COLOR);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + 2, 0x22FFFFFF);
        guiGraphics.fill(x + 1, y + height - 2, x + width - 1, y + height - 1, 0x66000000);
    }

    private void renderTemplateSelector(
            final GuiGraphics guiGraphics,
            final int x,
            final int y,
            final int width
    ) {
        final int selectorX = x + 34;
        final int selectorWidth = width - 68;
        guiGraphics.fill(selectorX, y, selectorX + selectorWidth, y + BUTTON_HEIGHT, FIELD_COLOR);
        guiGraphics.renderOutline(selectorX, y, selectorWidth, BUTTON_HEIGHT, 0xAA7C896D);
        guiGraphics.fill(selectorX + 1, y + 1, selectorX + selectorWidth - 1, y + 2, 0x22FFFFFF);
    }

    private void renderInputFrame(
            final GuiGraphics guiGraphics,
            final int x,
            final int y,
            final int width
    ) {
        guiGraphics.fill(x, y, x + width, y + BUTTON_HEIGHT, FIELD_COLOR);
        guiGraphics.renderOutline(x, y, width, BUTTON_HEIGHT, 0xAA7C896D);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + 2, 0x18FFFFFF);
    }

    private int drawSectionHeader(
            final GuiGraphics guiGraphics,
            final Component text,
            final int x,
            final int y,
            final int width,
            final int color
    ) {
        guiGraphics.drawString(this.font, text, x, y, color, false);
        final int lineY = y + 11;
        final int textWidth = this.font.width(text);
        final int lineX = x + Math.min(textWidth + 8, width - 24);
        guiGraphics.fill(lineX, lineY, x + width, lineY + 1, color & 0x99FFFFFF);
        return y + 18;
    }

    private void changeTemplate(final int delta) {
        final List<String> templates = this.state.templates();
        if (templates.isEmpty()) {
            return;
        }
        this.templateIndex = Math.floorMod(this.templateIndex + delta, templates.size());
        this.confirmAction = "";
        this.rebuildIslandWidgets(this.playerBox == null ? "" : this.playerBox.getValue());
    }

    private void confirmOrSend(
            final String confirmation,
            final IslandGuiActionPayload.Action action,
            final String value
    ) {
        if (!confirmation.equals(this.confirmAction)) {
            this.confirmAction = confirmation;
            this.rebuildIslandWidgets(this.playerBox == null ? "" : this.playerBox.getValue());
            return;
        }
        this.confirmAction = "";
        this.send(action, value);
    }

    private Component confirmLabel(final String confirmation, final String fallbackKey) {
        return Component.translatable(confirmation.equals(this.confirmAction)
                ? "button.skyresources.island.confirm"
                : fallbackKey);
    }

    private void sendWithPlayer(final IslandGuiActionPayload.Action action) {
        this.send(action, this.playerBox == null ? "" : this.playerBox.getValue());
    }

    private void send(final IslandGuiActionPayload.Action action) {
        this.send(action, "");
    }

    private void send(final IslandGuiActionPayload.Action action, final String value) {
        this.confirmAction = "";
        ClientPacketDistributor.sendToServer(new IslandGuiActionPayload(action, value));
    }

    private Button addButton(
            final String labelKey,
            final String tooltipKey,
            final int x,
            final int y,
            final int width,
            final Button.OnPress onPress,
            final boolean active
    ) {
        return this.addButton(Component.translatable(labelKey), tooltipKey, x, y, width, onPress, active, SkyResourcesButton.Tone.DEFAULT);
    }

    private Button addButton(
            final String labelKey,
            final String tooltipKey,
            final int x,
            final int y,
            final int width,
            final Button.OnPress onPress,
            final boolean active,
            final SkyResourcesButton.Tone tone
    ) {
        return this.addButton(Component.translatable(labelKey), tooltipKey, x, y, width, onPress, active, tone);
    }

    private Button addButton(
            final Component label,
            final String tooltipKey,
            final int x,
            final int y,
            final int width,
            final Button.OnPress onPress,
            final boolean active
    ) {
        return this.addButton(label, tooltipKey, x, y, width, onPress, active, SkyResourcesButton.Tone.DEFAULT);
    }

    private Button addButton(
            final Component label,
            final String tooltipKey,
            final int x,
            final int y,
            final int width,
            final Button.OnPress onPress,
            final boolean active,
            final SkyResourcesButton.Tone tone
    ) {
        final Button button = SkyResourcesButton.create(
                label,
                onPress,
                x,
                y,
                width,
                BUTTON_HEIGHT,
                Tooltip.create(Component.translatable(tooltipKey)),
                tone
        );
        button.active = active;
        return this.addRenderableWidget(button);
    }

    private int drawWrapped(
            final GuiGraphics guiGraphics,
            final Component text,
            final int x,
            final int y,
            final int width,
            final int color,
            final int maxLines
    ) {
        int lineY = y;
        int rendered = 0;
        for (final FormattedCharSequence line : this.font.split(text, width)) {
            if (rendered >= maxLines) {
                break;
            }
            guiGraphics.drawString(this.font, line, x, lineY, color, false);
            lineY += 10;
            rendered++;
        }
        return lineY;
    }

    private int drawLine(final GuiGraphics guiGraphics, final Component text, final int x, final int y, final int color) {
        guiGraphics.drawString(this.font, text, x, y, color, false);
        return y + 11;
    }

    private void drawCentered(
            final GuiGraphics guiGraphics,
            final Component text,
            final int centerX,
            final int y,
            final int color
    ) {
        guiGraphics.drawString(this.font, text, centerX - this.font.width(text) / 2, y, color, false);
    }

    private String selectedTemplate() {
        final List<String> templates = this.state.templates();
        if (templates.isEmpty()) {
            return "grass";
        }
        return templates.get(Math.floorMod(this.templateIndex, templates.size()));
    }

    private Component templateName(final String id) {
        return Component.translatable("screen.skyresources.island.template." + id);
    }

    private String joined(final List<String> values) {
        if (values.isEmpty()) {
            return Component.translatable("screen.skyresources.island.none").getString();
        }
        final String joined = String.join(", ", values);
        return joined.length() <= 72 ? joined : joined.substring(0, 69) + "...";
    }

    private int panelX() {
        return (this.width - this.panelWidth()) / 2;
    }

    private int panelY() {
        return (this.height - this.panelHeight()) / 2;
    }

    private int panelWidth() {
        return Math.min(PANEL_MAX_WIDTH, Math.max(300, this.width - 24));
    }

    private int panelHeight() {
        return Math.min(PANEL_MAX_HEIGHT, Math.max(260, this.height - 24));
    }

    private int statusWidth() {
        return Math.min(STATUS_WIDTH, this.panelWidth() / 3);
    }

    private int contentTop() {
        return this.panelY() + HEADER_HEIGHT + 14;
    }

    private int actionX() {
        return this.panelX() + PANEL_PADDING + this.statusWidth() + PANEL_PADDING;
    }

    private int actionWidth() {
        return this.panelWidth() - this.statusWidth() - PANEL_PADDING * 3;
    }

    private static int templateIndex(final IslandGuiStatePayload state) {
        final String currentType = state.islandType();
        final int index = state.templates().indexOf(currentType);
        return index < 0 ? 0 : index;
    }
}
