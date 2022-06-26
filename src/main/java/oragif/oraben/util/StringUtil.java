package oragif.oraben.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;

public class StringUtil {
    public static String firstLetterUppercase(String string) {
        String[] words = string.split(" ");

        for (int i = 0; i < words.length; i++)
        {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
        }

        return String.join(" ", words);
    }

    public static String nameCleanup(String string) {
        String objectString = string
                .replace("[", "")
                .replace("]", "")
                .replace("minecraft:", "")
                .replace("_", " ");

        return firstLetterUppercase(objectString).replace(",", " for");
    }

    public static String replace(String msg, int required) {
        Map<String, String> stringValues = new HashMap<>();
        stringValues.put("Required", Integer.toString(required));
        msg = StrSubstitutor.replace(msg, stringValues, "{", "}");
        return msg;
    }

    public static String replace(String msg, ServerPlayerEntity player) {
        Map<String, String> stringValues = new HashMap<>();
        stringValues.put("Player", player.getEntityName());
        msg = StrSubstitutor.replace(msg, stringValues, "{", "}");
        return msg;
    }

    public static String replace(String msg, World world) {
        Map<String, String> stringValues = new HashMap<>();
        stringValues.put("World", world.toString());
        msg = StrSubstitutor.replace(msg, stringValues, "{", "}");
        return msg;
    }

    public static String replace(String msg, ServerPlayerEntity player, World world) {
        msg = replace(msg, world);
        return replace(msg, player);
    }
    public static String replace(String msg, ServerPlayerEntity player, int levels) {
        msg = replace(msg, player);

        Map<String, String> stringValues = new HashMap<>();
        stringValues.put("Levels", Integer.toString(levels));
        msg = StrSubstitutor.replace(msg, stringValues, "{", "}");
        return msg;
    }
}
