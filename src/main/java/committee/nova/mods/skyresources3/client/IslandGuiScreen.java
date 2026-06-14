package committee.nova.mods.skyresources3.client;

import committee.nova.mods.skyresources3.network.IslandGuiActionPayload;
import committee.nova.mods.skyresources3.network.IslandGuiRequestPayload;
import committee.nova.mods.skyresources3.network.IslandGuiStatePayload;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public final class IslandGuiScreen extends Screen {
    private static final int PANEL_MAX_WIDTH = 560;
    private static final int PANEL_MAX_HEIGHT = 332;
    private static final int PANEL_PADDING = 12;
    private static final int STATUS_WIDTH = 190;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_GAP = 4;
    private static final int TEXT_COLOR = 0xFFE7E2D3;
    private static final int MUTED_TEXT_COLOR = 0xFFB8B0A0;
    private static final int WARNING_TEXT_COLOR = 0xFFFFC766;
    private static final int PANEL_COLOR = 0xE0181B20;
    private static final int INNER_PANEL_COLOR = 0xD0282B32;
    private static final int BORDER_COLOR = 0xFF6F7E70;
    private static final int SECTION_COLOR = 0xFF94C477;

    private IslandGuiStatePayload state;
    private EditBox playerBox;
    private int templateIndex;
    private String confirmAction = "";

    private IslandGuiScreen(final IslandGuiStatePayload state) {
        super(Component.translatable("screen.skyresources3.island.title"));
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
        guiGraphics.fill(0, 0, this.width, this.height, 0xAA000000);

        final int panelX = this.panelX();
        final int panelY = this.panelY();
        final int panelWidth = this.panelWidth();
        final int panelHeight = this.panelHeight();
        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, PANEL_COLOR);
        guiGraphics.renderOutline(panelX, panelY, panelWidth, panelHeight, BORDER_COLOR);
        this.drawCentered(guiGraphics, this.title, panelX + panelWidth / 2, panelY + 8, TEXT_COLOR);

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
        int y = panelY + 42;

        this.addButton("button.skyresources3.island.previous_template",
                "tooltip.skyresources3.island.previous_template",
                actionX,
                y,
                24,
                button -> this.changeTemplate(-1),
                this.state.enabled() && !this.state.templates().isEmpty()
        );
        this.addButton("button.skyresources3.island.next_template",
                "tooltip.skyresources3.island.next_template",
                actionX + actionWidth - 24,
                y,
                24,
                button -> this.changeTemplate(1),
                this.state.enabled() && !this.state.templates().isEmpty()
        );
        y += 24;

        this.addButton("button.skyresources3.island.create",
                this.state.hasIsland()
                        ? "tooltip.skyresources3.island.create_disabled"
                        : "tooltip.skyresources3.island.create",
                actionX,
                y,
                buttonWidth,
                button -> this.send(IslandGuiActionPayload.Action.CREATE, this.selectedTemplate()),
                this.state.enabled() && !this.state.hasIsland()
        );
        this.addButton(this.confirmLabel("reset", "button.skyresources3.island.reset"),
                "tooltip.skyresources3.island.reset",
                actionX + buttonWidth + BUTTON_GAP,
                y,
                buttonWidth,
                button -> this.confirmOrSend("reset", IslandGuiActionPayload.Action.RESET, this.selectedTemplate()),
                this.state.enabled() && this.state.owner()
        );
        y += 24;

        this.addButton("button.skyresources3.island.home",
                "tooltip.skyresources3.island.home",
                actionX,
                y,
                buttonWidth,
                button -> this.send(IslandGuiActionPayload.Action.HOME),
                this.state.enabled() && this.state.hasIsland()
        );
        this.addButton("button.skyresources3.island.spawn",
                "tooltip.skyresources3.island.spawn",
                actionX + buttonWidth + BUTTON_GAP,
                y,
                buttonWidth,
                button -> this.send(IslandGuiActionPayload.Action.SPAWN),
                this.state.enabled()
        );
        y += 24;

        this.addButton("button.skyresources3.island.info",
                "tooltip.skyresources3.island.info",
                actionX,
                y,
                buttonWidth,
                button -> this.send(IslandGuiActionPayload.Action.INFO),
                this.state.enabled() && this.state.hasIsland()
        );
        this.addButton("button.skyresources3.island.trusted",
                "tooltip.skyresources3.island.trusted",
                actionX + buttonWidth + BUTTON_GAP,
                y,
                buttonWidth,
                button -> this.send(IslandGuiActionPayload.Action.TRUSTED),
                this.state.enabled() && this.state.owner()
        );
        y += 34;

        this.playerBox = new EditBox(
                this.font,
                actionX,
                y,
                actionWidth,
                BUTTON_HEIGHT,
                Component.translatable("screen.skyresources3.island.player_input")
        );
        this.playerBox.setMaxLength(40);
        this.playerBox.setHint(Component.translatable("screen.skyresources3.island.player_hint"));
        this.playerBox.setValue(playerName);
        this.addRenderableWidget(this.playerBox);
        y += 24;

        this.addButton("button.skyresources3.island.visit",
                "tooltip.skyresources3.island.visit",
                actionX,
                y,
                buttonWidth,
                button -> this.sendWithPlayer(IslandGuiActionPayload.Action.VISIT),
                this.state.enabled()
        );
        this.addButton("button.skyresources3.island.invite",
                "tooltip.skyresources3.island.invite",
                actionX + buttonWidth + BUTTON_GAP,
                y,
                buttonWidth,
                button -> this.sendWithPlayer(IslandGuiActionPayload.Action.INVITE),
                this.state.enabled() && this.state.owner()
        );
        y += 24;

        this.addButton("button.skyresources3.island.trust",
                "tooltip.skyresources3.island.trust",
                actionX,
                y,
                buttonWidth,
                button -> this.sendWithPlayer(IslandGuiActionPayload.Action.TRUST),
                this.state.enabled() && this.state.owner()
        );
        this.addButton("button.skyresources3.island.untrust",
                "tooltip.skyresources3.island.untrust",
                actionX + buttonWidth + BUTTON_GAP,
                y,
                buttonWidth,
                button -> this.sendWithPlayer(IslandGuiActionPayload.Action.UNTRUST),
                this.state.enabled() && this.state.owner()
        );
        y += 24;

        this.addButton("button.skyresources3.island.accept",
                "tooltip.skyresources3.island.accept",
                actionX,
                y,
                buttonWidth,
                button -> this.send(IslandGuiActionPayload.Action.ACCEPT),
                this.state.enabled() && this.state.hasPendingInvite() && !this.state.hasIsland()
        );
        this.addButton("button.skyresources3.island.refresh",
                "tooltip.skyresources3.island.refresh",
                actionX + buttonWidth + BUTTON_GAP,
                y,
                buttonWidth,
                button -> this.send(IslandGuiActionPayload.Action.REFRESH),
                true
        );

        final int dangerY = panelY + panelHeight - PANEL_PADDING - BUTTON_HEIGHT;
        this.addButton(this.confirmLabel("leave", "button.skyresources3.island.leave"),
                "tooltip.skyresources3.island.leave",
                actionX,
                dangerY,
                buttonWidth,
                button -> this.confirmOrSend("leave", IslandGuiActionPayload.Action.LEAVE, ""),
                this.state.enabled() && this.state.member()
        );
        this.addButton(this.confirmLabel("disband", "button.skyresources3.island.disband"),
                "tooltip.skyresources3.island.disband",
                actionX + buttonWidth + BUTTON_GAP,
                dangerY,
                buttonWidth,
                button -> this.confirmOrSend("disband", IslandGuiActionPayload.Action.DISBAND, ""),
                this.state.enabled() && this.state.owner()
        );
    }

    private void renderStatus(final GuiGraphics guiGraphics) {
        final int x = this.panelX() + PANEL_PADDING;
        final int y = this.panelY() + 34;
        final int width = this.statusWidth();
        final int panelBottom = this.panelY() + this.panelHeight() - PANEL_PADDING;
        guiGraphics.fill(x - 4, y - 4, x + width + 4, panelBottom, INNER_PANEL_COLOR);
        guiGraphics.renderOutline(x - 4, y - 4, width + 8, panelBottom - y + 4, 0x885B665B);

        int lineY = y;
        lineY = this.drawLine(guiGraphics, Component.translatable("screen.skyresources3.island.section.status"), x, lineY, SECTION_COLOR);
        if (!this.state.enabled()) {
            this.drawWrapped(guiGraphics, Component.translatable("screen.skyresources3.island.status.disabled"), x, lineY, width, WARNING_TEXT_COLOR, 4);
            return;
        }
        if (!this.state.hasIsland()) {
            lineY = this.drawWrapped(guiGraphics, Component.translatable("screen.skyresources3.island.status.none"), x, lineY, width, TEXT_COLOR, 4);
            if (this.state.hasPendingInvite()) {
                this.drawWrapped(
                        guiGraphics,
                        Component.translatable("screen.skyresources3.island.status.pending", this.state.pendingInviteOwner()),
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
                ? Component.translatable("screen.skyresources3.island.status.owner")
                : Component.translatable("screen.skyresources3.island.status.member", this.state.ownerName());
        lineY = this.drawWrapped(guiGraphics, relation, x, lineY, width, TEXT_COLOR, 2);
        lineY = this.drawLine(
                guiGraphics,
                Component.translatable("screen.skyresources3.island.status.type", this.templateName(this.state.islandType())),
                x,
                lineY + 2,
                MUTED_TEXT_COLOR
        );
        lineY = this.drawWrapped(
                guiGraphics,
                Component.translatable("screen.skyresources3.island.status.dimension", this.state.dimension()),
                x,
                lineY,
                width,
                MUTED_TEXT_COLOR,
                2
        );
        lineY = this.drawLine(guiGraphics, Component.translatable("screen.skyresources3.island.status.home", this.state.home()), x, lineY, MUTED_TEXT_COLOR);
        lineY = this.drawWrapped(
                guiGraphics,
                Component.translatable("screen.skyresources3.island.status.players", this.joined(this.state.players())),
                x,
                lineY + 4,
                width,
                TEXT_COLOR,
                3
        );
        lineY = this.drawWrapped(
                guiGraphics,
                Component.translatable("screen.skyresources3.island.status.invites", this.joined(this.state.invites())),
                x,
                lineY + 2,
                width,
                MUTED_TEXT_COLOR,
                2
        );
        this.drawWrapped(
                guiGraphics,
                Component.translatable("screen.skyresources3.island.status.trusted", this.joined(this.state.trustedVisitors())),
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
        int y = this.panelY() + 28;
        this.drawLine(guiGraphics, Component.translatable("screen.skyresources3.island.section.actions"), actionX, y, SECTION_COLOR);
        y += 18;
        this.drawCentered(
                guiGraphics,
                Component.translatable("screen.skyresources3.island.template.current", this.templateName(this.selectedTemplate())),
                actionX + actionWidth / 2,
                y,
                TEXT_COLOR
        );
        this.drawLine(
                guiGraphics,
                Component.translatable("screen.skyresources3.island.player_input"),
                actionX,
                this.panelY() + 136,
                MUTED_TEXT_COLOR
        );
        if (!this.confirmAction.isEmpty()) {
            this.drawWrapped(
                    guiGraphics,
                    Component.translatable("screen.skyresources3.island.confirm_hint"),
                    actionX,
                    this.panelY() + this.panelHeight() - 58,
                    actionWidth,
                    WARNING_TEXT_COLOR,
                    2
            );
        }
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
                ? "button.skyresources3.island.confirm"
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
        return this.addButton(Component.translatable(labelKey), tooltipKey, x, y, width, onPress, active);
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
        final Button button = Button.builder(label, onPress)
                .bounds(x, y, width, BUTTON_HEIGHT)
                .tooltip(Tooltip.create(Component.translatable(tooltipKey)))
                .build(NoShadowButton::new);
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
        return Component.translatable("screen.skyresources3.island.template." + id);
    }

    private String joined(final List<String> values) {
        if (values.isEmpty()) {
            return Component.translatable("screen.skyresources3.island.none").getString();
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
        return Math.min(PANEL_MAX_HEIGHT, Math.max(268, this.height - 24));
    }

    private int statusWidth() {
        return Math.min(STATUS_WIDTH, this.panelWidth() / 3);
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

    private static final class NoShadowButton extends Button {
        private NoShadowButton(final Builder builder) {
            super(builder);
        }

        @Override
        protected void renderContents(
                final GuiGraphics guiGraphics,
                final int mouseX,
                final int mouseY,
                final float partialTick
        ) {
            this.renderDefaultSprite(guiGraphics);
            final Font font = Minecraft.getInstance().font;
            final Component message = this.getMessage();
            final int textWidth = font.width(message);
            final int textX = this.getX() + Math.max(2, (this.getWidth() - textWidth) / 2);
            final int textY = this.getY() + (this.getHeight() - 8) / 2;
            final int textColor = this.getFGColor() | ((int) Math.ceil(this.alpha * 255.0F) << 24);
            guiGraphics.enableScissor(
                    this.getX() + 2,
                    this.getY(),
                    this.getX() + this.getWidth() - 2,
                    this.getY() + this.getHeight()
            );
            guiGraphics.drawString(font, message, textX, textY, textColor, false);
            guiGraphics.disableScissor();
        }
    }
}
