package oragif.oraben;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
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
        if (cfg.signEditor) { SignEditor.register(); }
    }

    public static void log(String msg) {
        LOGGER.info(logTag + msg);
    }
}
