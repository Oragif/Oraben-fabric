package oragif.oraben.util;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PlayerUtil {
    private static final Item totem = Registry.ITEM.get(Identifier.tryParse("minecraft:totem_of_undying"));

    public static boolean emptyHands(PlayerEntity player) {
        if (player.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()
                && (player.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty() || player.getEquippedStack(EquipmentSlot.OFFHAND).isOf(totem))) {return true;}
        return false;
    }
}
