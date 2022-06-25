package oragif.oraben.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import oragif.oraben.Oraben;
import oragif.oraben.config.Config;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ConfigCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        LiteralArgumentBuilder<ServerCommandSource> argumentBuilder = literal("oraben");

        argumentBuilder.then(literal("save").executes(ctx -> {
            Config.get().save();
            return Command.SINGLE_SUCCESS;
        }));

        argumentBuilder.then(literal("reload").executes(ctx -> {
            Config.get().load();
            return Command.SINGLE_SUCCESS;
        }));

        argumentBuilder.then(literal("edit")
                //----- Tpa -----
                .then(literal("tpaEnabled").then(argument("bool", BoolArgumentType.bool())
                        .executes(ctx -> {
                            Oraben.cfg.tpaEnabled = ctx.getArgument("bool", Boolean.class);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("tpaLvlRequired").then(argument("int", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> {
                            Oraben.cfg.tpaLvlRequired = ctx.getArgument("int", Integer.class);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("tpaLvlPerBlock").then(argument("int", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> {
                            Oraben.cfg.tpaLvlPerBlock = ctx.getArgument("int", Integer.class);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("tpaBlockModifier").then(argument("int", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> {
                            Oraben.cfg.tpaBlockModifier = ctx.getArgument("int", Integer.class);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("tpaTimeout").then(argument("int", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> {
                            Oraben.cfg.tpaTimeout = ctx.getArgument("int", Integer.class);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                //----- Tpa End -----
                //----- Sleep -----
                //----- Sleep End -----
        );

        dispatcher.register(argumentBuilder);
    }
}
