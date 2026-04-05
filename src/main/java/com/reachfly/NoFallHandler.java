package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFallHandler {

    public static void tick(MinecraftClient client) {
        if (!ModConfig.noFallEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.getNetworkHandler() == null) return;

        ClientPlayerEntity player = client.player;

        // Spoof on-ground when falling OR when flying downward fast (fly-into-ground)
        // Also trigger when the player has any fall distance at all while flying
        boolean isFalling = player.fallDistance > 2.0f;
        boolean flyingDown = player.getAbilities().flying
                && player.getVelocity().y < -0.1
                && player.fallDistance > 0;

        if (isFalling || flyingDown) {
            client.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.OnGroundOnly(true, player.horizontalCollision));
            player.fallDistance = 0.0f;
        }
    }
}
