package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;

import java.util.ArrayList;
import java.util.List;

public class AutoHitHandler {

    // KillAura+ timing
    private static long lastAuraPlusAttack = 0;

    public static void tick(Minecraft client) {
        if (!ModConfig.autoHitEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.screen != null) return;
        if (client.interactionManager == null) return;

        LocalPlayer player = client.player;

        // KillAura+ mode: ignores attack cooldown, attacks at configurable CPS
        if (ModConfig.killAuraPlusEnabled && ModConfig.killAuraEnabled) {
            tickKillAuraPlus(client, player);
            return;
        }

        // Standard modes wait for cooldown
        if (player.getAttackCooldownProgress(0.0f) < 1.0f) return;

        // Kill Aura mode - attacks all entities in range
        if (ModConfig.killAuraEnabled) {
            List<Entity> targets = new ArrayList<>();
            for (Entity entity : client.world.getEntities()) {
                if (entity == player) continue;
                if (!(entity instanceof LivingEntity living)) continue;
                if (!living.isAlive()) continue;
                if (ModConfig.autoHitPlayersOnly && !(entity instanceof Player)) continue;

                double dist = player.distanceTo(entity);
                if (dist <= ModConfig.autoHitRange) {
                    targets.add(entity);
                }
            }
            boolean first = true;
            for (Entity target : targets) {
                client.interactionManager.attackEntity(player, target);
                if (first) {
                    player.swingHand(InteractionHand.MAIN_HAND);
                    first = false;
                }
            }
            return;
        }

        // Normal auto-hit - attack nearest entity
        Entity nearest = null;
        double nearestDist = ModConfig.autoHitRange;

        for (Entity entity : client.world.getEntities()) {
            if (entity == player) continue;
            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;
            if (ModConfig.autoHitPlayersOnly && !(entity instanceof Player)) continue;

            double dist = player.distanceTo(entity);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = entity;
            }
        }

        if (nearest != null) {
            client.interactionManager.attackEntity(player, nearest);
            player.swingHand(InteractionHand.MAIN_HAND);
        }
    }

    /**
     * KillAura+ - Bypasses attack cooldown entirely.
     * Attacks at configurable CPS (clicks per second) rate.
     * Resets attack cooldown after each hit to get full damage every swing.
     */
    private static void tickKillAuraPlus(Minecraft client, LocalPlayer player) {
        long now = System.currentTimeMillis();
        long interval = 1000L / ModConfig.killAuraPlusCps;

        if (now - lastAuraPlusAttack < interval) return;
        lastAuraPlusAttack = now;

        List<Entity> targets = new ArrayList<>();
        for (Entity entity : client.world.getEntities()) {
            if (entity == player) continue;
            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;
            if (ModConfig.autoHitPlayersOnly && !(entity instanceof Player)) continue;

            double dist = player.distanceTo(entity);
            if (dist <= ModConfig.autoHitRange) {
                targets.add(entity);
            }
        }

        boolean first = true;
        for (Entity target : targets) {
            client.interactionManager.attackEntity(player, target);
            if (first) {
                player.swingHand(InteractionHand.MAIN_HAND);
                first = false;
            }
        }
    }
}
