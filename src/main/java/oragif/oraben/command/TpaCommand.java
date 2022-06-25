package oragif.oraben.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import oragif.oraben.Oraben;
import oragif.oraben.util.StringReplacer;

import java.util.Map;
import java.util.UUID;

import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TpaCommand {
    private Map<UUID, UUID> requests;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        LiteralArgumentBuilder<ServerCommandSource> argumentBuilder = literal("tpa");

        argumentBuilder.then(argument("tTo", EntityArgumentType.player()).executes(TpaCommand::request));

        argumentBuilder.then(literal("accept").then(argument("tFrom", EntityArgumentType.player()).executes(TpaCommand::accept)));

        argumentBuilder.then(literal("cancel").then(argument("tFrom", EntityArgumentType.player()).executes(TpaCommand::cancel)));

        dispatcher.register(argumentBuilder);
    }

    private static int request(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final ServerCommandSource source = context.getSource();
        if (!Oraben.cfg.tpaEnabled) {
            source.sendFeedback(Text.literal("Tpa not enabled"), false);
            return Command.SINGLE_SUCCESS;
        }

        ServerPlayerEntity targetFrom = source.getPlayerOrThrow();
        ServerPlayerEntity targetTo = getPlayer(context, "tTo");

        int requiredLvl = requiredLvl(targetFrom, targetTo);
        if (Oraben.cfg.tpaLvlRequired != 0 && requiredLvl < targetFrom.experienceLevel) {
            source.sendFeedback(Text.literal(StringReplacer.replace(Oraben.cfg.tpaNotEnoughLevels, requiredLvl)), false);
            return Command.SINGLE_SUCCESS;
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int cancel(CommandContext<ServerCommandSource> context) {
        return Command.SINGLE_SUCCESS;
    }

    private static int accept(CommandContext<ServerCommandSource> context) {
        return Command.SINGLE_SUCCESS;
    }

    private static int requiredLvl(ServerPlayerEntity targetFrom, ServerPlayerEntity targetTo) {
        int requiredLvl = Oraben.cfg.tpaLvlRequired;
        if (Oraben.cfg.tpaLvlPerBlock != 0) {
            requiredLvl += Oraben.cfg.tpaLvlPerBlock * Math.round(targetFrom.distanceTo(targetTo) / Oraben.cfg.tpaBlockModifier);
        }

        return requiredLvl;
    }
}
