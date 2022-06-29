package oragif.oraben.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;

import java.util.HashMap;
import java.util.List;
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

    public static String listIdentifierCleaner(String string) {
        String objectString = string
                .replace("[", "")
                .replace("]", "")
                .replace("minecraft:", "")
                .replace("_", " ");

        return firstLetterUppercase(objectString).replace(",", " for");
    }
    
    public static List<String> required(int required) {
        return List.of("Required", Integer.toString(required));
    }
    
    public static List<String> sleeping(int sleeping) {
        return List.of("Sleeping", Integer.toString(sleeping));
    }

    public static List<String> player(ServerPlayerEntity player) {
        return List.of("Player", player.getEntityName());
    }

    public static List<String> world(World world) {
        return List.of("World", world.toString());
    }
    public static String replaceMsg(String msg, List<List<String>> valuesList) {
        Map<String, String> stringValues = new HashMap<>();
        for (List<String> values : valuesList) {
            stringValues.put(values.get(0), values.get(1));
        }
        
        msg = StrSubstitutor.replace(msg, stringValues, "{", "}");
        return msg;
    }
}
