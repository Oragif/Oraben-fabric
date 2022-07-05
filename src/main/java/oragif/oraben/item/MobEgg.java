package oragif.oraben.item;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import oragif.oraben.Oraben;
import oragif.oraben.mixin.AccessorVillagerEntity;

import java.util.*;

public class MobEgg {
    static final Item air = Registry.ITEM.get(Identifier.tryParse("minecraft:air"));
    static final Item stick = Registry.ITEM.get(Identifier.tryParse("minecraft:stick"));
    public static void register() {
        UseEntityCallback.EVENT.register(MobEgg::onUseEntity);
    }

    public static ActionResult onUseEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        ActionResult actionResult = ActionResult.PASS;

        if (!world.isClient() && !player.isSpectator()) {
            if (player.getStackInHand(hand).getItem() != air) {
                List<List<String>> mobList = Oraben.cfg.mobEggList;
                List<String> current = List.of(Registry.ENTITY_TYPE.getId(entity.getType()).toString(), Registry.ITEM.getId(player.getStackInHand(hand).getItem()).toString());

                if (mobList.contains(current)) {
                    player.swingHand(hand);

                    Item item = SpawnEggItem.forEntity(entity.getType());
                    ItemStack egg = new ItemStack(item, 1);
                    egg.addEnchantment(Registry.ENCHANTMENT.get(Identifier.tryParse("binding_curse")), 1);

                    player.giveItemStack(egg);
                    if (!player.isCreative()) {
                        player.getStackInHand(hand).decrement(1);
                    }

                    entity.remove(Entity.RemovalReason.DISCARDED);

                    return ActionResult.SUCCESS;
                }
            }

            if (entity instanceof VillagerEntity villager) {
                if (player.isSneaking() && player.isHolding(stick)) {
                    villager.setVillagerData(villager.getVillagerData().withProfession(VillagerProfession.NONE));
                }
                long restockTime =  ((AccessorVillagerEntity) villager).getLastRestockTime() + Oraben.cfg.restockTime;
                Oraben.log("Restock time: " + restockTime + " / " + world.getTime());
                if (restockTime <= world.getTime()) {
                    player.sendMessage(Text.literal("Restocking"), false);
                    villager.restock();
                    return actionResult;
                }
            }

        }

        return actionResult;
    }
}