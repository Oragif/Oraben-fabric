package oragif.oraben;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import oragif.oraben.block.SignEditor;
import oragif.oraben.config.Config;
import oragif.oraben.config.Config.ConfigData;
import oragif.oraben.entity.VillagerOverhaul;
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
        if (cfg.signEditor) { SignEditor.register(); }

        UseEntityCallback.EVENT.register((PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) -> {
            if (cfg.mobEggEnabled) {
                if (MobEgg.onUseEntity(player, world, hand, entity, hitResult)) { return ActionResult.SUCCESS; }
            }
            if (cfg.villagerEnabled) {
                if (VillagerOverhaul.onUseEntity(player,world, hand, entity, hitResult)) { return ActionResult.SUCCESS; }
            }

            return ActionResult.PASS;
        });
    }

    public static void log(String msg) {
        LOGGER.info(logTag + msg);
    }
}
