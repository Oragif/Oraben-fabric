package oragif.oraben.util;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import oragif.oraben.Oraben;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SleepManager {
    private static List<UUID> commandSleepers = new ArrayList<>();
    private static int lastVoteDay = 0;

    public static void initialize() {
        EntitySleepEvents.START_SLEEPING.register((listener, pos) -> {
            if (listener.isPlayer()) {
                ServerPlayerEntity player = (ServerPlayerEntity) listener;
                ServerWorld world = player.getWorld();
                sameDay(world);
                trySkipNight(world);
                messageAll(world, StringUtil.replace(Oraben.cfg.sleepStartSleepMsg, player));
            }
        });
    }

    public static int sleepCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (!Oraben.cfg.sleepEnabled) { return Command.SINGLE_SUCCESS; }

        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ServerWorld world = player.getWorld();
        UUID uuid = player.getUuid();

        if (timeCurrentDay(world) > 12000) {
            sameDay(world);

            if (!commandSleepers.contains(uuid) && !player.isSleeping()) {
                commandSleepers.add(uuid);
                messageAll(world, StringUtil.replace(Oraben.cfg.sleepStartSleepMsg, player));
            } else if (commandSleepers.contains(uuid)) {
                commandSleepers.remove(uuid);
                messageAll(world, StringUtil.replace(Oraben.cfg.sleepStopSleepMsg, player));
            }

            trySkipNight(world);
            return Command.SINGLE_SUCCESS;
        }

        if ((world.isThundering() || world.isRaining()) && Oraben.cfg.sleepClearWeatherEnabled) {
            clearWeather(world);
        }

        messageAll(world, Oraben.cfg.sleepClearWeatherMsg);
        return Command.SINGLE_SUCCESS;
    }

    private static void clearWeather(ServerWorld world) {
        if (!world.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) { return; }
        world.setWeather(0, 0, false, false);
    }

    private static void trySkipNight(ServerWorld world) {
        if (canSkipNight(world) && world.isSleepingEnabled()) {
            skipNight(world);
        }
    }

    public static void skipNight(ServerWorld world) {
        world.setTimeOfDay(world.getTimeOfDay() + (24000 + Oraben.cfg.sleepWakeUpTime) - timeCurrentDay(world));
        world.getPlayers().forEach(playerEntity -> playerEntity.getStatHandler().setStat(playerEntity, Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST), 0));
        clearWeather(world);
        messageAll(world, Oraben.cfg.sleepSkipNightMsg);
        commandSleepers.clear();
    }

    private static boolean canSkipNight(ServerWorld world) {
        List<ServerPlayerEntity> bedSleepers = world.getPlayers().stream().filter(ServerPlayerEntity::isSleeping).toList();
        int duplicates = (int) bedSleepers.stream().filter(player -> commandSleepers.contains(player.getUuid())).count();
        int sleeping = commandSleepers.size() + bedSleepers.size() - duplicates;
        return sleeping >= requiredToSleep(world);
    }

    private static int requiredToSleep(ServerWorld world) {
        List<ServerPlayerEntity> players = world.getPlayers();
        int totalPlayers = players.size() - players.stream().filter(ServerPlayerEntity::isSpectator).toList().size();
        return Math.max(1, Math.round((totalPlayers / 100f) * Oraben.cfg.sleepPercentage));
    }

    public static void sameDay(ServerWorld world) {
        if (lastVoteDay != currentDay(world)) {
            commandSleepers.clear();
            lastVoteDay = currentDay(world);
        }
    }

    public static int timeCurrentDay(ServerWorld world) {
        return (int) (world.getTimeOfDay() % 24000L);
    }

    public static int currentDay(ServerWorld world) {
        return (((int)world.getTimeOfDay() - timeCurrentDay(world)) / 24000);
    }


    public static void messageAll(ServerWorld world, String msg) {
        world.getPlayers().forEach(player -> {
            player.sendMessage(Text.literal(msg), true);
        });
    }
}