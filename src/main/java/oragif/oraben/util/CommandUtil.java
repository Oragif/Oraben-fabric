package oragif.oraben.util;

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
}
