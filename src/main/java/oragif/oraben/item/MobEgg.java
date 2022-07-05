package oragif.oraben.item;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
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
import oragif.oraben.mixin.AccessorVillagerEntity;

import java.util.*;

public class MobEgg {
    static final Item air = Registry.ITEM.get(Identifier.tryParse("minecraft:air"));
    public static final Item stick = Registry.ITEM.get(Identifier.tryParse("minecraft:stick"));
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

                    if (Oraben.cfg.mobEggLog) { Oraben.log(player.getEntityName() + "picked up: " + entity.getType().toString()); }

                    if (entity instanceof VillagerEntity villager) {
                        ((AccessorVillagerEntity) villager).invokeReleaseAllTickets();
                    }

                    entity.remove(Entity.RemovalReason.DISCARDED);

                    return ActionResult.SUCCESS;
                }
            }

            if (entity instanceof VillagerEntity villager) {
                if (player.isSneaking() && player.isHolding(stick)) {
                    if (((AccessorVillagerEntity) villager).getExperience() == 0) {
                        Entity vil = new VillagerEntity(EntityType.VILLAGER, world);
                        vil.setPosition(villager.getPos());
                        vil.setYaw(villager.getYaw());
                        vil.setPitch(villager.getPitch());
                        vil.setBodyYaw(villager.getBodyYaw());
                        vil.setHeadYaw(villager.getHeadYaw());

                        ((AccessorVillagerEntity) villager).invokeReleaseAllTickets();
                        villager.remove(Entity.RemovalReason.DISCARDED);

                        world.spawnEntity(vil);
                        return actionResult;
                    }
                }

                long restockTime =  ((AccessorVillagerEntity) villager).getLastRestockTime() + Oraben.cfg.restockTime;
                if (restockTime <= world.getTime()) {
                    villager.restock();
                    return actionResult;
                }
            }

        }

        return actionResult;
    }
}