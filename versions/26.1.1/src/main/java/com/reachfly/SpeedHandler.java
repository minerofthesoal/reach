package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Speed hack - Boosts ground movement speed using the player's facing direction.
 * Calculates direction from key inputs and yaw to prevent momentum locking.
 */
public class SpeedHandler {

    public static void tick(Minecraft minecraft) {
        if (!ModConfig.speedEnabled) return;
        if (minecraft.player == null || minecraft.level == null) return;
        if (minecraft.screen != null) return;

        LocalPlayer player = minecraft.player;

        if (!player.onGround()) return;

        Vec3 velocity = player.getDeltaMovement();
        double currentSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);

        // Only boost if player is actually moving
        if (currentSpeed < 0.001) return;

        // Detect movement direction from pressed keys
        float forward = 0;
        float strafe = 0;
        if (minecraft.options.keyUp.isDown()) forward += 1;
        if (minecraft.options.keyDown.isDown()) forward -= 1;
        if (minecraft.options.keyLeft.isDown()) strafe += 1;
        if (minecraft.options.keyRight.isDown()) strafe -= 1;

        // If no movement keys pressed, don't override velocity
        if (forward == 0 && strafe == 0) return;

        // Calculate movement direction from player yaw and input
        float yaw = player.getYRot();
        double yawRad = Math.toRadians(yaw);
        double moveAngle = yawRad - Math.atan2(strafe, forward);

        double dirX = -Math.sin(moveAngle);
        double dirZ = Math.cos(moveAngle);

        // Target speed
        double baseSpeed = player.isSprinting() ? 0.14 : 0.108;
        double targetSpeed = baseSpeed * ModConfig.speedMultiplier;

        // Set velocity in the input direction at target speed
        player.setDeltaMovement(
                dirX * targetSpeed,
                velocity.y,
                dirZ * targetSpeed);
    }
}
