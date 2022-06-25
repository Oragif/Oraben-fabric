package oragif.oraben.item;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import oragif.oraben.Oraben;

import java.util.List;

public class MobEgg {
    static final Item air = Registry.ITEM.get(Identifier.tryParse("minecraft:air"));
    public static void register() {
        UseEntityCallback.EVENT.register(MobEgg::onUseEntity);
    }

    public static ActionResult onUseEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        ActionResult actionResult = ActionResult.PASS;

        if (!world.isClient() && !player.isSpectator()) {
            if (player.getStackInHand(hand).getItem() == air) {return actionResult;}

            List<List<String>> mobList = Oraben.cfg.mobEggList;
            List<String> current = List.of(Registry.ENTITY_TYPE.getId(entity.getType()).toString(), Registry.ITEM.getId(player.getStackInHand(hand).getItem()).toString());

            if (mobList.contains(current)) {
                player.swingHand(hand);

                player.giveItemStack(new ItemStack(SpawnEggItem.forEntity(entity.getType()), 1));
                if (!player.isCreative()) {
                    player.getStackInHand(hand).decrement(1);
                }

                entity.remove(Entity.RemovalReason.DISCARDED);

                return ActionResult.SUCCESS;
            }
        }

        return actionResult;
    }
}