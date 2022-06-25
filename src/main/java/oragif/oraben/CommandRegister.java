package oragif.oraben;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import oragif.oraben.command.ConfigCommand;
import oragif.oraben.command.TpaCommand;

public class CommandRegister {
    public static void register() {
        Event<CommandRegistrationCallback> event = CommandRegistrationCallback.EVENT;
        Oraben.log("Registering commands");
        if (Oraben.cfg.tpaEnabled) { event.register(TpaCommand::register); }

        event.register(ConfigCommand::register);
    }
}
