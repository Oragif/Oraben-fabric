package oragif.oraben.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import oragif.oraben.Oraben;
import oragif.oraben.util.SleepManager;

import static net.minecraft.server.command.CommandManager.literal;

public class SleepCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        LiteralArgumentBuilder<ServerCommandSource> argumentBuilder = literal("sleep");

        argumentBuilder.then(literal("force").requires(source -> source.hasPermissionLevel(Oraben.cfg.sleepForcePermissionLevel)).executes(ctx -> {
            SleepManager.skipNight(ctx.getSource().getWorld());
            return Command.SINGLE_SUCCESS;
        }));
        argumentBuilder.executes(SleepManager::sleepCommand);

        dispatcher.register(argumentBuilder);
    }
}
