package oragif.oraben.mixin;

import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VillagerEntity.class)
public interface AccessorVillagerEntity {
    @Accessor
    long getLastRestockTime();

    @Accessor
    int getExperience();

    @Invoker("releaseAllTickets")
    public void invokeReleaseAllTickets();
}
