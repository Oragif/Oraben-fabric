package oragif.oraben;

import net.fabricmc.api.ModInitializer;
import oragif.oraben.config.Config;
import oragif.oraben.config.Config.ConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Oraben implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Oraben");
    private static final String logTag = "[Oraben] ";
    public static ConfigData cfg;

    @Override
    public void onInitialize() {
        cfg = Config.get().configData;
        CommandRegister.register();
    }

    public static void log(String msg) {
        LOGGER.info(logTag + msg);
    }
}
