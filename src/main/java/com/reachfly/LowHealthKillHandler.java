package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;

/**
 * Low Health Kill - Automatically targets and attacks entities below a health threshold.
 * Prioritizes the lowest health target within reach range.
 */
public class LowHealthKillHandler {

    /**
     * Called every client tick. Finds low-health entities and finishes them off.
     */
    public static void tick(Minecraft client) {
        if (!ModConfig.lowHealthKillEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.screen != null) return;

        LocalPlayer player = client.player;

        // Respect attack cooldown
        if (player.getAttackCooldownProgress(0.0f) < 1.0f) return;

        // Use reach distance if reach is enabled, otherwise use default
        double range = ModConfig.reachEnabled ? ModConfig.reachDistance : 4.5;

        LivingEntity weakest = null;
        float lowestHealth = Float.MAX_VALUE;

        for (Entity entity : client.world.getEntities()) {
            if (entity == player) continue;
            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;

            // Only target entities below the health threshold
            if (living.getHealth() > ModConfig.lowHealthThreshold) continue;

            double dist = player.distanceTo(entity);
            if (dist > range) continue;

            // Prioritize the weakest target
            if (living.getHealth() < lowestHealth) {
                lowestHealth = living.getHealth();
                weakest = living;
            }
        }

        if (weakest != null) {
            client.interactionManager.attackEntity(player, weakest);
            player.swingHand(InteractionHand.MAIN_HAND);
        }
    }
}
