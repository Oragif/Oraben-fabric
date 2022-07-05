package oragif.oraben.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import oragif.oraben.Oraben;
import oragif.oraben.mixin.AccessorVillagerEntity;

public class VillagerOverhaul {
    public static final Item stick = Registry.ITEM.get(Identifier.tryParse("minecraft:stick"));

    public static boolean onUseEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (entity instanceof VillagerEntity villager) {
            if (Oraben.cfg.villagerReroll) {
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
                        return true;
                    }
                }
            }

            long restockTime =  ((AccessorVillagerEntity) villager).getLastRestockTime() + Oraben.cfg.villagerRestockTime;
            if (restockTime <= world.getTime()) {
                villager.restock();
                return true;
            }
        }
        return false;
    }
}
