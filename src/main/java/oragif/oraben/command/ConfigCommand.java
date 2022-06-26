package oragif.oraben.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import oragif.oraben.Oraben;
import oragif.oraben.config.Config;

import java.util.List;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static oragif.oraben.util.CommandUtil.clickableButton;
import static oragif.oraben.util.StringUtil.nameCleanup;

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
                            Boolean newValue = ctx.getArgument("bool", Boolean.class);
                            messageChangeConfigValue(ctx, "tpaEnabled", Oraben.cfg.tpaEnabled, newValue, "/oraben edit tpaEnabled " + Oraben.cfg.tpaEnabled.toString());
                            Oraben.cfg.tpaEnabled = newValue;
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("tpaLvlRequired").then(argument("int", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> {
                            int newValue = ctx.getArgument("int", Integer.class);
                            messageChangeConfigValue(ctx, "tpaLvlRequired", Oraben.cfg.tpaLvlRequired, newValue, "/oraben edit tpaLvlRequired " + Oraben.cfg.tpaLvlRequired.toString());
                            Oraben.cfg.tpaLvlRequired = newValue;
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("tpaLvlPerBlock").then(argument("int", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> {
                            int newValue = ctx.getArgument("int", Integer.class);
                            messageChangeConfigValue(ctx, "tpaLvlPerBlock", Oraben.cfg.tpaLvlPerBlock, newValue, "/oraben edit tpaLvlPerBlock " + Oraben.cfg.tpaLvlPerBlock.toString());
                            Oraben.cfg.tpaLvlPerBlock = newValue;
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("tpaBlockModifier").then(argument("int", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> {
                            int newValue = ctx.getArgument("int", Integer.class);
                            messageChangeConfigValue(ctx, "tpaBlockModifier", Oraben.cfg.tpaBlockModifier, newValue, "/oraben edit tpaBlockModifier " + Oraben.cfg.tpaBlockModifier.toString());
                            Oraben.cfg.tpaBlockModifier = newValue;
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("tpaTimeout").then(argument("int", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> {
                            int newValue = ctx.getArgument("int", Integer.class);
                            messageChangeConfigValue(ctx, "tpaTimeout", Oraben.cfg.tpaTimeout, newValue, "/oraben edit tpaTimeout " + Oraben.cfg.tpaTimeout.toString());
                            Oraben.cfg.tpaTimeout = newValue;
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                //----- Tpa End -----

                //----- Sleep -----
                .then(literal("sleepEnabled").then(argument("bool", BoolArgumentType.bool())
                        .executes(ctx -> {
                            Boolean newValue = ctx.getArgument("bool", Boolean.class);
                            messageChangeConfigValue(ctx, "sleepEnabled", Oraben.cfg.sleepEnabled, newValue, "/oraben edit sleepEnabled " + Oraben.cfg.sleepEnabled.toString());
                            Oraben.cfg.sleepEnabled = newValue;
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("sleepPercentage").then(argument("int", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> {
                            int newValue = ctx.getArgument("int", Integer.class);
                            messageChangeConfigValue(ctx, "sleepPercentage", Oraben.cfg.sleepPercentage, newValue, "/oraben edit sleepPercentage " + Oraben.cfg.sleepPercentage.toString());
                            Oraben.cfg.sleepPercentage = newValue;
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("sleepWakeUpTime").then(argument("int", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> {
                            int newValue = ctx.getArgument("int", Integer.class);
                            messageChangeConfigValue(ctx, "sleepWakeUpTime", Oraben.cfg.sleepWakeUpTime, newValue, "/oraben edit sleepWakeUpTime " + Oraben.cfg.sleepWakeUpTime.toString());
                            Oraben.cfg.sleepWakeUpTime = newValue;
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(literal("sleepClearWeatherEnabled").then(argument("bool", BoolArgumentType.bool())
                        .executes(ctx -> {
                            Boolean newValue = ctx.getArgument("bool", Boolean.class);
                            messageChangeConfigValue(ctx, "sleepClearWeatherEnabled", Oraben.cfg.sleepClearWeatherEnabled, newValue, "/oraben edit sleepClearWeatherEnabled " + Oraben.cfg.sleepClearWeatherEnabled.toString());
                            Oraben.cfg.sleepClearWeatherEnabled = newValue;
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                //----- Sleep End -----

                //----- Mob egg -----
                .then(literal("mobEggEnabled").then(argument("bool", BoolArgumentType.bool())
                        .executes(ctx -> {
                            Boolean newValue = ctx.getArgument("bool", Boolean.class);
                            messageChangeConfigValue(ctx, "mobEggEnabled", Oraben.cfg.mobEggEnabled, newValue, "/oraben edit mobEggEnabled " + Oraben.cfg.mobEggEnabled.toString());
                            Oraben.cfg.mobEggEnabled = newValue;
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
                                                        messageChangeConfigList(ctx, "mobEggList", "Added " + nameCleanup(current.toString()), "/oraben edit mobEggList remove " + entity + " " + item);
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

                                                    if (Oraben.cfg.mobEggList.contains(current)) {
                                                        messageChangeConfigList(ctx, "mobEggList", "Removed " + nameCleanup(current.toString()), "/oraben edit mobEggList add " + entity + " " + item);
                                                        Oraben.cfg.mobEggList.remove(current);
                                                    }

                                                    return Command.SINGLE_SUCCESS;
                                                })
                        )))
                )
                .then(literal("mobEggSurvivalSpawnerChange").then(argument("bool", BoolArgumentType.bool())
                        .executes(ctx -> {
                            Boolean newValue = ctx.getArgument("bool", Boolean.class);
                            messageChangeConfigValue(ctx, "mobEggSurvivalSpawnerChange", Oraben.cfg.mobEggSurvivalSpawnerChange, newValue, "/oraben edit mobEggSurvivalSpawnerChange " + Oraben.cfg.mobEggSurvivalSpawnerChange.toString());
                            Oraben.cfg.mobEggSurvivalSpawnerChange = newValue;
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                //----- Mob egg End -----
        );

        argumentBuilder.then(literal("get")
                        //----- Tpa -----
                        .then(literal("tpaEnabled").executes(ctx -> messageGetConfigValue(ctx, "tpaEnabled", Oraben.cfg.tpaEnabled)))
                        .then(literal("tpaLvlRequired").executes(ctx -> messageGetConfigValue(ctx, "tpaLvlRequired", Oraben.cfg.tpaLvlRequired)))
                        .then(literal("tpaLvlPerBlock").executes(ctx -> messageGetConfigValue(ctx, "tpaLvlPerBlock", Oraben.cfg.tpaLvlPerBlock)))
                        .then(literal("tpaBlockModifier").executes(ctx -> messageGetConfigValue(ctx, "tpaBlockModifier", Oraben.cfg.tpaBlockModifier)))
                        .then(literal("tpaTimeout").executes(ctx -> messageGetConfigValue(ctx, "tpaTimeout", Oraben.cfg.tpaTimeout)))
                        //----- Tpa End -----

                        //----- Sleep -----
                        .then(literal("sleepEnabled").executes(ctx -> messageGetConfigValue(ctx, "sleepEnabled", Oraben.cfg.sleepEnabled)))
                        .then(literal("sleepPercentage").executes(ctx -> messageGetConfigValue(ctx, "sleepPercentage", Oraben.cfg.sleepPercentage)))
                        .then(literal("sleepWakeUpTime").executes(ctx -> messageGetConfigValue(ctx, "sleepWakeUpTime", Oraben.cfg.sleepWakeUpTime)))
                        .then(literal("sleepClearWeatherEnabled").executes(ctx -> messageGetConfigValue(ctx, "sleepClearWeatherEnabled", Oraben.cfg.sleepClearWeatherEnabled)))
                        //----- Sleep End -----

                        //----- Mob egg -----
                        .then(literal("mobEggEnabled").executes(ctx -> messageGetConfigValue(ctx, "mobEggEnabled", Oraben.cfg.mobEggEnabled)))
                        .then(literal("mobEggList").executes(ctx -> messageGetConfigValue(ctx, "mobEggList", Oraben.cfg.mobEggList)))
                        .then(literal("mobEggSurvivalSpawnerChange").executes(ctx -> messageGetConfigValue(ctx, "mobEggSurvivalSpawnerChange", Oraben.cfg.mobEggSurvivalSpawnerChange)))
                        //----- Mob egg End -----
        );

        dispatcher.register(argumentBuilder);
    }

    private static int messageGetConfigValue(CommandContext<ServerCommandSource> ctx, String key, Object value) {
        ServerCommandSource source = ctx.getSource();
        if (Objects.requireNonNull(source.getPlayer()).isPlayer()) {
            MutableText msg = Text.literal("| ")
                    .append(Text.literal("Config").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE).withUnderline(true)))
                    .append(Text.literal(" | "));
            if (value instanceof List<?>) {
                msg = msg.append(key).append(clickableButton(ClickEvent.Action.SUGGEST_COMMAND, "Add", "/oraben edit " + key + " add ", Formatting.GREEN));
                source.getPlayer().sendMessage(msg, false);
                source.getPlayer().sendMessage(Text.literal("--------------List--------------"), false);
                ((List<?>) value).forEach(object -> {
                    String objectValue = "";
                    if (object instanceof List<?>) {
                        for (Object innerObject: ((List) object)) {
                            objectValue += " " + innerObject.toString();
                        }
                    } else {
                        objectValue = " " + object.toString();
                    }

                    String objectString = nameCleanup(object.toString());

                    Text _msg = Text.literal(objectString).append(clickableButton(ClickEvent.Action.SUGGEST_COMMAND, "Remove","/oraben edit " + key + " remove" + objectValue, Formatting.RED));
                    source.getPlayer().sendMessage(_msg, false);
                });
                //source.getPlayer().sendMessage(Text.literal("-------------------------------"), false);
                return Command.SINGLE_SUCCESS;
            }

            msg = msg.append(key + " = " + value.toString()).append(clickableButton(ClickEvent.Action.SUGGEST_COMMAND, "Edit", "/oraben edit " + key + " ", Formatting.GREEN));
            source.getPlayer().sendMessage(msg, false);
        }

        return Command.SINGLE_SUCCESS;
    }

    public static void messageChangeConfigValue(CommandContext<ServerCommandSource> context, String key, Object oldValue, Object newValue, String command) {
        messageChangeConfig(context, Text.literal(" | " + key + " from " + oldValue.toString() + " to " + newValue), command);
    }

    public static void messageChangeConfigList(CommandContext<ServerCommandSource> context, String key, String message, String command) {
        messageChangeConfig(context, Text.literal(" | " + key + " - " + message), command);
    }

    public static void messageChangeConfig(CommandContext<ServerCommandSource> context, Text message, String command) {
        Text msg = Text.literal("| ")
                .append(Text.literal("Config").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE).withUnderline(true)))
                .append(message)
                .append(clickableButton(ClickEvent.Action.RUN_COMMAND, "Revert", command, Formatting.RED));

        context.getSource().getPlayer().sendMessage(msg, false);
    }
}
