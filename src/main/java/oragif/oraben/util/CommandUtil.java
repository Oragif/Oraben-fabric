package oragif.oraben.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CommandUtil {
    public static Text clickableButton(ClickEvent.Action action, String name, String command, Formatting color) {
        Text text = Text.literal(" | ")
                .append(Text.literal(name).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(action, command)).withBold(true).withColor(color)))
                .append(" | ");
        return text;
    }

    public static void teleportToPlayer(ServerPlayerEntity playerFrom, ServerPlayerEntity playerTo) {
        playerFrom.teleport(playerTo.getWorld(), playerTo.getX(), playerTo.getY(), playerTo.getZ(), playerTo.getYaw(), playerTo.getPitch());
    }
}
