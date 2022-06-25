package oragif.oraben;

import net.fabricmc.api.ModInitializer;
import oragif.oraben.block.ItemFramePassThrough;
import oragif.oraben.block.SignEditor;
import oragif.oraben.config.Config;
import oragif.oraben.config.Config.ConfigData;
import oragif.oraben.item.MobEgg;
import oragif.oraben.util.SleepManager;
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
        SleepManager.initialize();
        moduleInitializer();
    }

    private void moduleInitializer() {
        if (cfg.mobEggEnabled) { MobEgg.register(); }
        if (cfg.itemframePassThrough) { ItemFramePassThrough.register(); }
        if (cfg.signEditor) { SignEditor.register(); }
    }

    public static void log(String msg) {
        LOGGER.info(logTag + msg);
    }
}
