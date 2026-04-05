package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Speed hack - Boosts ground movement speed using the player's facing direction.
 * Calculates direction from key inputs and yaw to prevent momentum locking.
 */
public class SpeedHandler {

    public static void tick(MinecraftClient client) {
        if (!ModConfig.speedEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;

        ClientPlayerEntity player = client.player;

        if (!player.isOnGround()) return;

        Vec3d velocity = player.getVelocity();
        double currentSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);

        // Only boost if player is actually moving
        if (currentSpeed < 0.001) return;

        // Detect movement direction from pressed keys
        float forward = 0;
        float strafe = 0;
        if (client.options.forwardKey.isPressed()) forward += 1;
        if (client.options.backKey.isPressed()) forward -= 1;
        if (client.options.leftKey.isPressed()) strafe += 1;
        if (client.options.rightKey.isPressed()) strafe -= 1;

        // If no movement keys pressed, don't override velocity
        if (forward == 0 && strafe == 0) return;

        // Calculate movement direction from player yaw and input
        float yaw = player.getYaw();
        double yawRad = Math.toRadians(yaw);
        double moveAngle = yawRad - Math.atan2(strafe, forward);

        double dirX = -Math.sin(moveAngle);
        double dirZ = Math.cos(moveAngle);

        // Target speed
        double baseSpeed = player.isSprinting() ? 0.14 : 0.108;
        double targetSpeed = baseSpeed * ModConfig.speedMultiplier;

        // Set velocity in the input direction at target speed
        player.setVelocity(
                dirX * targetSpeed,
                velocity.y,
                dirZ * targetSpeed);
    }
}
