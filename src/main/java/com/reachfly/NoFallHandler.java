package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * NoFall - Prevents ALL fall damage by constantly spoofing ground status.
 * Handles: normal falling, fly-into-ground, getting hit while flying,
 * knockback while airborne, and any other scenario that causes fall damage.
 */
public class NoFallHandler {

    public static void tick(MinecraftClient client) {
        if (!ModConfig.noFallEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.getNetworkHandler() == null) return;

        ClientPlayerEntity player = client.player;

        // Strategy: if the player has ANY fall distance, immediately cancel it.
        // Don't wait for thresholds - just always keep fallDistance at 0
        // and tell the server we're on the ground.
        if (player.fallDistance > 0.5f) {
            client.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.OnGroundOnly(true, player.horizontalCollision));
            player.fallDistance = 0.0f;
        }

        // While flying (our fly hack), aggressively prevent fall damage
        if (player.getAbilities().flying || ModConfig.flyEnabled) {
            player.fallDistance = 0.0f;
        }

        // If the player has significant downward velocity, also spoof
        // This catches getting hit/knocked while flying
        if (player.getVelocity().y < -0.5 && !player.isOnGround()) {
            client.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.OnGroundOnly(true, player.horizontalCollision));
            player.fallDistance = 0.0f;
        }
    }
}
