package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Speed hack - Boosts the player's ground movement to a target speed.
 * Uses a target-speed approach to avoid compounding acceleration.
 */
public class SpeedHandler {

    // Approximate base movement speeds in Minecraft (blocks/tick)
    private static final double BASE_WALK_SPEED = 0.108;
    private static final double BASE_SPRINT_SPEED = 0.14;

    public static void tick(MinecraftClient client) {
        if (!ModConfig.speedEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;

        ClientPlayerEntity player = client.player;

        if (!player.isOnGround()) return;

        Vec3d velocity = player.getVelocity();
        double currentSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);

        // Only boost if player is actually trying to move
        if (currentSpeed < 0.001) return;

        // Calculate target speed based on whether sprinting
        double baseSpeed = player.isSprinting() ? BASE_SPRINT_SPEED : BASE_WALK_SPEED;
        double targetSpeed = baseSpeed * ModConfig.speedMultiplier;

        // Only boost up to target, never compound beyond it
        if (currentSpeed >= targetSpeed) return;

        double scale = targetSpeed / currentSpeed;
        player.setVelocity(
                velocity.x * scale,
                velocity.y,
                velocity.z * scale);
    }
}
