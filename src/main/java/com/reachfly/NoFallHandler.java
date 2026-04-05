package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * NoFall - Prevents fall damage by spoofing on-ground status to the server.
 * Sends a ground-status packet when the player is about to take fall damage.
 */
public class NoFallHandler {

    public static void tick(MinecraftClient client) {
        if (!ModConfig.noFallEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.getNetworkHandler() == null) return;

        ClientPlayerEntity player = client.player;

        // If falling a significant distance, spoof ground status
        if (player.fallDistance > 2.0f) {
            client.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.OnGroundOnly(true));
            player.fallDistance = 0.0f;
        }
    }
}
