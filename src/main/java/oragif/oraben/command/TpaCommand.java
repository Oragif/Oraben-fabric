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
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import oragif.oraben.Oraben;
import oragif.oraben.util.StringReplacer;

import java.util.*;

import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TpaCommand {
    private static final List<Request> requests = new ArrayList<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        LiteralArgumentBuilder<ServerCommandSource> argumentBuilder = literal("tpa");

        argumentBuilder.then(literal("here").then(argument("tTo", EntityArgumentType.player()).executes(TpaCommand::requestHere)));
        argumentBuilder.then(argument("tTo", EntityArgumentType.player()).executes(TpaCommand::requestTo));
        argumentBuilder.then(literal("accept").then(argument("tFrom", EntityArgumentType.player()).executes(TpaCommand::accept)));
        argumentBuilder.then(literal("cancel").then(argument("tTo", EntityArgumentType.player()).executes(TpaCommand::cancel)));

        dispatcher.register(argumentBuilder);
    }

    private static int requestHere(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return request(context, true);
    }

    private static int requestTo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return request(context, false);
    }

    private static int request(CommandContext<ServerCommandSource> context, Boolean toHere) throws CommandSyntaxException {
        final ServerCommandSource source = context.getSource();
        if (!Oraben.cfg.tpaEnabled) {
            source.sendFeedback(Text.literal("Tpa not enabled"), false);
            return Command.SINGLE_SUCCESS;
        }

        ServerPlayerEntity targetFrom = source.getPlayerOrThrow();
        ServerPlayerEntity targetTo = getPlayer(context, "tTo");

        Request request = new Request(targetFrom.getUuid(), targetTo.getUuid(), toHere);
        if (findRequest(request.targetFrom, request.targetTo) == null) {
            int requiredLvl = requiredLvl(targetFrom, targetTo);
            if (Oraben.cfg.tpaLvlRequired != 0 && requiredLvl > targetFrom.experienceLevel) {
                source.sendFeedback(Text.literal(StringReplacer.replace(Oraben.cfg.tpaNotEnoughLevelsMsg, requiredLvl)), false);
                return Command.SINGLE_SUCCESS;
            }

            request.setTimeoutCallback(() -> {
                requests.remove(request);
                targetFrom.sendMessage(Text.literal(StringReplacer.replace(Oraben.cfg.tpaTimeoutMsg, targetTo)));
            });
            requests.add(request);

            targetTo.sendMessage(Text.literal(StringReplacer.replace(Oraben.cfg.tpaRequestMsg, targetFrom) + " | ")
                            .append(Text.literal("Accept").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(
                                            ClickEvent.Action.RUN_COMMAND, "/tpa accept " + targetFrom.getEntityName()))
                                    .withBold(true).withColor(Formatting.GREEN)))
                            .append(" |"),
                    false);
            targetFrom.sendMessage(Text.literal(StringReplacer.replace(Oraben.cfg.tpaSendToMsg, targetTo) + " levels required: " + requiredLvl + " | ")
                    .append(Text.literal("Cancel").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(
                                    ClickEvent.Action.RUN_COMMAND, "/tpa cancel " + targetTo.getEntityName()))
                            .withBold(true).withColor(Formatting.RED)))
                    .append(" |"), false);
        } else {
            targetFrom.sendMessage(Text.literal(StringReplacer.replace(Oraben.cfg.tpaPendingMsg, targetTo)), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static void removeRequest(Request request) {
        requests.remove(request);
        request.cancelTimeout();
    }

    private static Request findRequest(UUID targetFrom, UUID targetTo) {
        Optional<Request> req = requests.stream().filter(r -> r.targetTo.equals(targetTo) && r.targetFrom.equals(targetFrom)).findFirst();
        if (req.isEmpty()) {return null;}
        Oraben.log(req.toString());
        return req.get();
    }

    private static int cancel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final ServerCommandSource source = context.getSource();

        ServerPlayerEntity targetFrom = source.getPlayerOrThrow();
        ServerPlayerEntity targetTo = getPlayer(context, "tTo");

        Request req = findRequest(targetFrom.getUuid(), targetTo.getUuid());
        if (req != null) {
            removeRequest(req);
            targetFrom.sendMessage(Text.literal(StringReplacer.replace(Oraben.cfg.tpaCancelledFromMsg, targetTo)), false);
            targetTo.sendMessage(Text.literal(StringReplacer.replace(Oraben.cfg.tpaCancelledToMsg, targetFrom)), false);
        } else {
            targetFrom.sendMessage(Text.literal(StringReplacer.replace(Oraben.cfg.tpaTimeoutMsg, targetTo)));
            Oraben.log("Request not found");
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int accept(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final ServerCommandSource source = context.getSource();
        if (!Oraben.cfg.tpaEnabled) {
            source.sendFeedback(Text.literal("Tpa not enabled"), false);
            return Command.SINGLE_SUCCESS;
        }

        ServerPlayerEntity targetTo = source.getPlayerOrThrow();
        ServerPlayerEntity targetFrom = getPlayer(context, "tFrom");

        int requiredLvl = requiredLvl(targetFrom, targetTo);
        if (Oraben.cfg.tpaLvlRequired != 0 && requiredLvl > targetFrom.experienceLevel) {
            source.sendFeedback(Text.literal(StringReplacer.replace(Oraben.cfg.tpaNotEnoughLevelsMsg, requiredLvl)), false);
            return Command.SINGLE_SUCCESS;
        }

        if (targetTo.world != targetFrom.world) {
            source.sendFeedback(Text.literal(StringReplacer.replace(Oraben.cfg.tpaWrongDimension, targetFrom, targetFrom.world)), false);
            return Command.SINGLE_SUCCESS;
        }

        Request req = findRequest(targetFrom.getUuid(), targetTo.getUuid());
        if (req != null) {
            removeRequest(req);
            targetFrom.sendMessage(Text.literal(StringReplacer.replace(Oraben.cfg.tpaAcceptedMsg, targetTo, requiredLvl)), false);
            targetFrom.setExperienceLevel(targetFrom.experienceLevel - requiredLvl);

            if (req.toHere) {
                teleportToPlayer(targetTo, targetFrom);
            } else {
                teleportToPlayer(targetFrom, targetTo);
            }
        } else {
            targetTo.sendMessage(Text.literal(StringReplacer.replace(Oraben.cfg.tpaTimeoutMsg, targetFrom)));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int requiredLvl(ServerPlayerEntity targetFrom, ServerPlayerEntity targetTo) {
        int requiredLvl = Oraben.cfg.tpaLvlRequired;
        if (Oraben.cfg.tpaLvlPerBlock != 0 || Oraben.cfg.tpaBlockModifier != 0) {
            requiredLvl += Oraben.cfg.tpaLvlPerBlock * Math.round(targetFrom.distanceTo(targetTo) / Oraben.cfg.tpaBlockModifier);
        }

        return requiredLvl;
    }

    public static void teleportToPlayer(ServerPlayerEntity playerFrom, ServerPlayerEntity playerTo) {
        playerFrom.teleport(playerTo.getWorld(), playerTo.getX(), playerTo.getY(), playerTo.getZ(), playerTo.getYaw(), playerTo.getPitch());
    }

    public static class Request {
        public UUID targetFrom;
        public UUID targetTo;
        public boolean toHere;
        private Timer timer;

        public Request(UUID targetFrom, UUID targetTo, Boolean toHere) {
            this.targetFrom = targetFrom;
            this.targetTo = targetTo;
            this.toHere = toHere;
        }

        void setTimeoutCallback(Timeout callback) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    callback.onTimeout();
                }
            }, Oraben.cfg.tpaTimeout * 1000L);
        }

        void cancelTimeout() {
            timer.cancel();
        }
    }

    interface Timeout {
        void onTimeout();
    }
}
