package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.List;

/**
 * Auto Hit - Automatically attacks the nearest entity within range.
 * Targets ALL living entities (hostile, passive, neutral, players) unless
 * "players only" mode is enabled.
 *
 * Kill Aura mode: attacks ALL entities in range each tick (not just nearest).
 */
public class AutoHitHandler {

    public static void tick(MinecraftClient client) {
        if (!ModConfig.autoHitEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;
        if (client.interactionManager == null) return;

        ClientPlayerEntity player = client.player;

        if (player.getAttackCooldownProgress(0.0f) < 1.0f) return;

        if (ModConfig.killAuraEnabled) {
            // Kill Aura: attack ALL entities in range
            List<Entity> targets = new ArrayList<>();
            for (Entity entity : client.world.getEntities()) {
                if (entity == player) continue;
                if (!(entity instanceof LivingEntity living)) continue;
                if (!living.isAlive()) continue;
                if (ModConfig.autoHitPlayersOnly && !(entity instanceof PlayerEntity)) continue;

                double dist = player.distanceTo(entity);
                if (dist <= ModConfig.autoHitRange) {
                    targets.add(entity);
                }
            }
            boolean first = true;
            for (Entity target : targets) {
                client.interactionManager.attackEntity(player, target);
                if (first) {
                    player.swingHand(Hand.MAIN_HAND);
                    first = false;
                }
            }
            return;
        }

        // Normal mode: attack nearest entity
        Entity nearest = null;
        double nearestDist = ModConfig.autoHitRange;

        for (Entity entity : client.world.getEntities()) {
            if (entity == player) continue;
            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;

            // Filter: players only mode
            if (ModConfig.autoHitPlayersOnly && !(entity instanceof PlayerEntity)) continue;

            double dist = player.distanceTo(entity);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = entity;
            }
        }

        if (nearest != null) {
            client.interactionManager.attackEntity(player, nearest);
            player.swingHand(Hand.MAIN_HAND);
        }
    }
}
