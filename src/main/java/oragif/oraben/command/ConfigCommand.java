package oragif.oraben.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.registry.Registry;
import oragif.oraben.Oraben;
import oragif.oraben.config.Config;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ConfigCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        LiteralArgumentBuilder<ServerCommandSource> argumentBuilder = literal("oraben").requires(source -> source.hasPermissionLevel(4));

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
                .then(literal("sleepEnabled").then(argument("bool", BoolArgumentType.bool())
                        .executes(ctx -> {
                            Oraben.cfg.sleepEnabled = ctx.getArgument("bool", Boolean.class);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("sleepPercentage").then(argument("int", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> {
                            Oraben.cfg.sleepPercentage = ctx.getArgument("int", Integer.class);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("sleepWakeUpTime").then(argument("int", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> {
                            Oraben.cfg.sleepWakeUpTime = ctx.getArgument("int", Integer.class);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("sleepWakeUpTime").then(argument("int", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> {
                            Oraben.cfg.sleepWakeUpTime = ctx.getArgument("int", Integer.class);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("sleepClearWeatherEnabled").then(argument("bool", BoolArgumentType.bool())
                        .executes(ctx -> {
                            Oraben.cfg.sleepClearWeatherEnabled = ctx.getArgument("bool", Boolean.class);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                //----- Sleep End -----

                //----- Mob egg -----
                .then(literal("mobEggEnabled").then(argument("bool", BoolArgumentType.bool())
                        .executes(ctx -> {
                            Oraben.cfg.mobEggEnabled = ctx.getArgument("bool", Boolean.class);
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("mobEggList")
                        .then(literal("add")
                                .then(argument("entity", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                        .then(argument("item", ItemStackArgumentType.itemStack(commandRegistryAccess))
                                                .executes(ctx -> {
                                                    String entity = EntitySummonArgumentType.getEntitySummon(ctx, "entity").toString();
                                                    String item = Registry.ITEM.getId(ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem()).toString();

                                                    List<String> current = List.of(entity, item);

                                                    if (!Oraben.cfg.mobEggList.contains(current)) {
                                                        Oraben.cfg.mobEggList.add(current);
                                                    }

                                                    return Command.SINGLE_SUCCESS;
                                                })
                        )))
                        .then(literal("remove")
                                .then(argument("entity", EntitySummonArgumentType.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                        .then(argument("item", ItemStackArgumentType.itemStack(commandRegistryAccess))
                                                .executes(ctx -> {
                                                    String entity = EntitySummonArgumentType.getEntitySummon(ctx, "entity").toString();
                                                    String item = Registry.ITEM.getId(ItemStackArgumentType.getItemStackArgument(ctx, "item").getItem()).toString();

                                                    List<String> current = List.of(entity, item);

                                                    Oraben.cfg.mobEggList.remove(current);

                                                    return Command.SINGLE_SUCCESS;
                                                })
                        )))
                )
                //----- Mob egg End -----
        );

        dispatcher.register(argumentBuilder);
    }
}
