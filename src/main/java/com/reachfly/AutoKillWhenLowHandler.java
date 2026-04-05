package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

/**
 * Auto Kill When Low HP - When YOUR health drops below a threshold,
 * automatically attacks the nearest entity within range to defend yourself.
 */
public class AutoKillWhenLowHandler {

    public static void tick(MinecraftClient client) {
        if (!ModConfig.autoKillWhenLowEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;

        ClientPlayerEntity player = client.player;

        // Only activate when player's health is below threshold
        if (player.getHealth() > ModConfig.autoKillSelfHpThreshold) return;

        // Respect attack cooldown
        if (player.getAttackCooldownProgress(0.0f) < 1.0f) return;

        double range = ModConfig.autoKillWhenLowRange;
        LivingEntity nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (Entity entity : client.world.getEntities()) {
            if (entity == player) continue;
            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;

            double dist = player.distanceTo(entity);
            if (dist > range) continue;

            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = living;
            }
        }

        if (nearest != null) {
            client.interactionManager.attackEntity(player, nearest);
            player.swingHand(Hand.MAIN_HAND);
        }
    }
}
