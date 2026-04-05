package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Speed hack - Multiplies the player's ground movement speed.
 * Only affects horizontal movement when on the ground and moving.
 */
public class SpeedHandler {

    public static void tick(MinecraftClient client) {
        if (!ModConfig.speedEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;

        ClientPlayerEntity player = client.player;

        if (!player.isOnGround()) return;

        // Only boost if player is actually trying to move
        Vec3d velocity = player.getVelocity();
        if (Math.abs(velocity.x) < 0.001 && Math.abs(velocity.z) < 0.001) return;

        double multiplier = ModConfig.speedMultiplier;
        player.setVelocity(
                velocity.x * multiplier,
                velocity.y,
                velocity.z * multiplier);
    }
}
