package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Knockback hack - Applies massive knockback to entities when the player hits them.
 * Works by detecting when the player attacks and launching the target entity away.
 * Configurable strength up to 2500 blocks of knockback force.
 */
public class KnockbackHandler {

    private static Entity lastTarget = null;
    private static int cooldownTicks = 0;

    public static void tick(MinecraftClient client) {
        if (!ModConfig.knockbackEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.interactionManager == null) return;

        if (cooldownTicks > 0) {
            cooldownTicks--;
        }
    }

    /**
     * Called when the player attacks an entity.
     * Applies extra knockback based on configured strength.
     */
    public static void onAttack(ClientPlayerEntity player, Entity target) {
        if (!ModConfig.knockbackEnabled) return;
        if (!(target instanceof LivingEntity)) return;

        // Calculate knockback direction (away from player)
        Vec3d playerPos = player.getPos();
        Vec3d targetPos = target.getPos();
        Vec3d direction = targetPos.subtract(playerPos);

        // Normalize horizontal direction
        double horizLength = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        if (horizLength < 0.01) {
            // If standing on top of target, use look direction instead
            float yaw = player.getYaw();
            direction = new Vec3d(
                    -Math.sin(Math.toRadians(yaw)),
                    0,
                    Math.cos(Math.toRadians(yaw)));
            horizLength = 1.0;
        }

        double normalX = direction.x / horizLength;
        double normalZ = direction.z / horizLength;

        // Scale knockback strength - convert configured value to velocity
        // At strength 1, modest knockback. At 2500, absolutely launched.
        double strength = ModConfig.knockbackStrength;

        // Velocity formula: logarithmic scaling so low values feel useful
        // and high values are extreme but not literally infinite
        double velocityMult = strength * 0.15;

        // Vertical component - launch them upward proportionally
        double verticalBoost = Math.min(strength * 0.05, 50.0);

        // Apply the knockback velocity to the target
        target.setVelocity(
                normalX * velocityMult,
                verticalBoost,
                normalZ * velocityMult);

        // Force velocity update to server for the target
        target.velocityModified = true;
    }
}
