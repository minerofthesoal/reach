package com.reachfly.mixin;

import com.reachfly.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityGlowMixin {

    @Inject(method = "isCurrentlyGlowing", at = @At("RETURN"), cancellable = true)
    private void onIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.espEnabled) return;

        Entity self = (Entity) (Object) this;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && self == minecraft.player) return;

        if (!(self instanceof LivingEntity)) return;

        if (shouldGlow(self)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getTeamColor", at = @At("RETURN"), cancellable = true)
    private void onGetTeamColor(CallbackInfoReturnable<Integer> cir) {
        if (!ModConfig.espEnabled) return;

        Entity self = (Entity) (Object) this;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && self == minecraft.player) return;

        if (self instanceof Player && ModConfig.espPlayers) {
            cir.setReturnValue(0xFF5555);
        } else if (isHostile(self) && ModConfig.espHostile) {
            cir.setReturnValue(0xFF8800);
        } else if (isPassive(self) && ModConfig.espPassive) {
            cir.setReturnValue(0x55FF55);
        }
    }

    private static boolean shouldGlow(Entity entity) {
        if (entity instanceof Player) return ModConfig.espPlayers;
        if (isHostile(entity)) return ModConfig.espHostile;
        if (isPassive(entity)) return ModConfig.espPassive;
        if (entity instanceof LivingEntity) return ModConfig.espHostile;
        return false;
    }

    private static boolean isHostile(Entity entity) {
        if (entity instanceof Monster) return true;
        if (entity instanceof Mob && !(entity instanceof Animal)) return true;
        return false;
    }

    private static boolean isPassive(Entity entity) {
        return entity instanceof Animal;
    }
}
