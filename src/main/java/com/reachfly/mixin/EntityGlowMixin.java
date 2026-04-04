package com.reachfly.mixin;

import com.reachfly.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin into Entity to override isGlowing() for ESP functionality.
 * When ESP is enabled, target entities will render with vanilla outline
 * effect that is visible through walls.
 */
@Mixin(Entity.class)
public class EntityGlowMixin {

    /**
     * Override isGlowing() to make filtered entities glow when ESP is on.
     */
    @Inject(method = "isGlowing", at = @At("RETURN"), cancellable = true)
    private void onIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.espEnabled) return;

        Entity self = (Entity) (Object) this;

        // Don't make ourselves glow
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && self == client.player) return;

        // Only affect living entities
        if (!(self instanceof LivingEntity)) return;

        // Filter by entity type
        if (self instanceof PlayerEntity && ModConfig.espPlayers) {
            cir.setReturnValue(true);
        } else if (self instanceof HostileEntity && ModConfig.espHostile) {
            cir.setReturnValue(true);
        } else if (self instanceof PassiveEntity && ModConfig.espPassive) {
            cir.setReturnValue(true);
        }
    }

    /**
     * Override getTeamColorValue() to color the ESP outlines.
     * Players = red, hostiles = orange, passives = green.
     */
    @Inject(method = "getTeamColorValue", at = @At("RETURN"), cancellable = true)
    private void onGetTeamColorValue(CallbackInfoReturnable<Integer> cir) {
        if (!ModConfig.espEnabled) return;

        Entity self = (Entity) (Object) this;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && self == client.player) return;

        if (self instanceof PlayerEntity && ModConfig.espPlayers) {
            cir.setReturnValue(0xFF5555); // Red
        } else if (self instanceof HostileEntity && ModConfig.espHostile) {
            cir.setReturnValue(0xFF8800); // Orange
        } else if (self instanceof PassiveEntity && ModConfig.espPassive) {
            cir.setReturnValue(0x55FF55); // Green
        }
    }
}
