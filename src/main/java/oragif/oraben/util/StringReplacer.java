package oragif.oraben.util;

import org.apache.logging.log4j.core.lookup.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;

public class StringReplacer {
    public static String replace(String msg, int required) {
        Map<String, String> stringValues = new HashMap<>();

        stringValues.put("Required", Integer.toString(required));
        msg = StrSubstitutor.replace(msg, stringValues, "{", "}");

        return msg;
    }
}
