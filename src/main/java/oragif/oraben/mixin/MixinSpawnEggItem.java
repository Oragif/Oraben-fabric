package oragif.oraben.mixin;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import oragif.oraben.Oraben;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(SpawnEggItem.class)
public class MixinSpawnEggItem {

    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/MobSpawnerBlockEntity;getLogic()Lnet/minecraft/world/MobSpawnerLogic;"), cancellable = true)
    void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context.getPlayer().isCreative() || Oraben.cfg.mobEggSurvivalSpawnerChange) { return; }
        context.getPlayer().sendMessage(Text.literal("Not allowed to change spawner"), false);
        cir.setReturnValue(ActionResult.CONSUME);
        cir.cancel();
    }
}
