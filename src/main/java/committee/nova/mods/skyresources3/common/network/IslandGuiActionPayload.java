package committee.nova.mods.skyresources3.common.network;

import committee.nova.mods.skyresources3.Skyresources3;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record IslandGuiActionPayload(Action action, String value) {
    private static final int MAX_VALUE_LENGTH = 64;

    public IslandGuiActionPayload {
        action = action == null ? Action.REFRESH : action;
        value = value == null ? "" : value.strip();
    }

    static void handle(final IslandGuiActionPayload payload, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            if (player != null) {
                payload.run(player);
                IslandGuiStatePayload.sendTo(player);
            }
        });
        context.setPacketHandled(true);
    }

    private void run(final ServerPlayer player) {
        final String command = switch (this.action) {
            case CREATE -> commandWithValue(player, "island create");
            case HOME -> "island home";
            case SPAWN -> "island spawn";
            case VISIT -> commandWithValue(player, "island visit");
            case INFO -> "island info";
            case INVITE -> commandWithValue(player, "island invite");
            case TRUST -> commandWithValue(player, "island trust");
            case UNTRUST -> commandWithValue(player, "island untrust");
            case TRUSTED -> "island trusted";
            case RESET -> commandWithValue(player, "island reset", "confirm");
            case ACCEPT -> "island accept";
            case LEAVE -> "island leave";
            case DISBAND -> "island disband";
            case REFRESH -> "";
        };
        if (command.isEmpty()) {
            return;
        }
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        final CommandSourceStack source = player.createCommandSourceStack();
        level.getServer().getCommands().performPrefixedCommand(source, command);
    }

    private String commandWithValue(final ServerPlayer player, final String prefix) {
        return commandWithValue(player, prefix, "");
    }

    private String commandWithValue(final ServerPlayer player, final String prefix, final String suffix) {
        final String argument = this.singleWord(player);
        if (argument.isEmpty()) {
            return "";
        }
        return suffix.isEmpty() ? prefix + " " + argument : prefix + " " + argument + " " + suffix;
    }

    private String singleWord(final ServerPlayer player) {
        if (this.value.isEmpty()
                || this.value.length() > MAX_VALUE_LENGTH
                || this.value.chars().anyMatch(Character::isWhitespace)) {
            player.sendSystemMessage(Component.translatable("message.skyresources.island.gui.invalid_value"));
            return "";
        }
        return this.value;
    }

    static void encode(final IslandGuiActionPayload payload, final FriendlyByteBuf buffer) {
        buffer.writeVarInt(payload.action.ordinal());
        buffer.writeUtf(payload.value, MAX_VALUE_LENGTH);
    }

    static IslandGuiActionPayload decode(final FriendlyByteBuf buffer) {
        return new IslandGuiActionPayload(Action.byOrdinal(buffer.readVarInt()), buffer.readUtf(MAX_VALUE_LENGTH));
    }

    public enum Action {
        REFRESH,
        CREATE,
        HOME,
        SPAWN,
        VISIT,
        INFO,
        INVITE,
        TRUST,
        UNTRUST,
        TRUSTED,
        RESET,
        ACCEPT,
        LEAVE,
        DISBAND;

        private static Action byOrdinal(final int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                return REFRESH;
            }
            return values()[ordinal];
        }
    }
}
