package com.reachfly.mixin;

import com.reachfly.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityGlowMixin {

    @Inject(method = "isGlowing", at = @At("RETURN"), cancellable = true)
    private void onIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.espEnabled) return;

        Entity self = (Entity) (Object) this;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && self == client.player) return;

        if (!(self instanceof LivingEntity)) return;

        if (shouldGlow(self)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getTeamColorValue", at = @At("RETURN"), cancellable = true)
    private void onGetTeamColorValue(CallbackInfoReturnable<Integer> cir) {
        if (!ModConfig.espEnabled) return;

        Entity self = (Entity) (Object) this;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && self == client.player) return;

        if (self instanceof PlayerEntity && ModConfig.espPlayers) {
            cir.setReturnValue(0xFF5555); // Red
        } else if (isHostile(self) && ModConfig.espHostile) {
            cir.setReturnValue(0xFF8800); // Orange
        } else if (isPassive(self) && ModConfig.espPassive) {
            cir.setReturnValue(0x55FF55); // Green
        }
    }

    private static boolean shouldGlow(Entity entity) {
        if (entity instanceof PlayerEntity) return ModConfig.espPlayers;
        if (isHostile(entity)) return ModConfig.espHostile;
        if (isPassive(entity)) return ModConfig.espPassive;
        // Catch-all: any other LivingEntity - treat as hostile
        if (entity instanceof LivingEntity) return ModConfig.espHostile;
        return false;
    }

    private static boolean isHostile(Entity entity) {
        if (entity instanceof HostileEntity) return true;
        // Slimes, Magma Cubes, Ghasts, Phantoms, etc. extend MobEntity but not HostileEntity
        // Treat any MobEntity that isn't passive as hostile
        if (entity instanceof MobEntity && !(entity instanceof PassiveEntity)) return true;
        return false;
    }

    private static boolean isPassive(Entity entity) {
        return entity instanceof PassiveEntity;
    }
}
