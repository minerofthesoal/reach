package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

/**
 * Auto Hit - Automatically attacks the nearest entity within range.
 * Respects attack cooldown for optimal damage output.
 */
public class AutoHitHandler {

    /**
     * Called every client tick. Finds and attacks the nearest valid target.
     */
    public static void tick(MinecraftClient client) {
        if (!ModConfig.autoHitEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return; // Don't attack while in menus

        ClientPlayerEntity player = client.player;

        // Only attack when attack cooldown is ready (smooth hits, max damage)
        if (player.getAttackCooldownProgress(0.0f) < 1.0f) return;

        Entity nearest = null;
        double nearestDist = ModConfig.autoHitRange;

        for (Entity entity : client.world.getEntities()) {
            if (entity == player) continue;
            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;

            // Filter: players only mode
            if (ModConfig.autoHitPlayersOnly && !(entity instanceof PlayerEntity)) continue;

            // Skip passive mobs unless they're players
            if (!ModConfig.autoHitPlayersOnly) {
                if (!(entity instanceof HostileEntity) && !(entity instanceof PlayerEntity)) continue;
            }

            double dist = player.distanceTo(entity);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = entity;
            }
        }

        if (nearest != null) {
            // Face the target and attack
            client.interactionManager.attackEntity(player, nearest);
            player.swingHand(Hand.MAIN_HAND);
        }
    }
}
