package oragif.oraben.block;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.ActionResult;
import oragif.oraben.Oraben;

import static oragif.oraben.util.PlayerUtil.emptyHands;

public class SignEditor {
    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!Oraben.cfg.signEditor) { return ActionResult.PASS; }
            BlockEntity blockEntity = world.getBlockEntity(hitResult.getBlockPos());
            if (!(blockEntity instanceof SignBlockEntity)) return ActionResult.PASS;
            if (player.isSneaking() && emptyHands(player)) {
                SignBlockEntity signBlock = (SignBlockEntity) blockEntity;
                signBlock.setEditable(true);
                if (signBlock.isEditable()) {
                    player.openEditSignScreen(signBlock);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }
}
