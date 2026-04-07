package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;

/**
 * Auto Kill When Low HP - When YOUR health drops below a threshold,
 * automatically attacks the nearest entity within range to defend yourself.
 */
public class AutoKillWhenLowHandler {

    public static void tick(Minecraft client) {
        if (!ModConfig.autoKillWhenLowEnabled) return;
        if (client.player == null || client.level == null) return;
        if (client.screen != null) return;

        LocalPlayer player = client.player;

        // Only activate when player's health is below threshold
        if (player.getHealth() > ModConfig.autoKillSelfHpThreshold) return;

        // Respect attack cooldown
        if (player.getAttackCooldownProgress(0.0f) < 1.0f) return;

        double range = ModConfig.autoKillWhenLowRange;
        LivingEntity nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (Entity entity : client.level.getEntities()) {
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
            client.gameMode.attack(player, nearest);
            player.swing(InteractionHand.MAIN_HAND);
        }
    }
}
