package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * NoFall - Prevents ALL fall damage by:
 * 1. Sending Full position packets with onGround=true every tick while not grounded
 * 2. Resetting both client and server-side fallDistance every tick
 * 3. Directly setting server player onGround in singleplayer
 */
public class NoFallHandler {

    public static void tick(MinecraftClient client) {
        if (!ModConfig.noFallEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.getNetworkHandler() == null) return;

        ClientPlayerEntity player = client.player;

        // Always reset client-side fall distance every tick
        player.fallDistance = 0.0f;

        // If the player is not on the ground, send a spoofed Full position packet
        // with onGround=true. The Full packet includes position so the server
        // doesn't just discard it like it can with OnGroundOnly.
        if (!player.isOnGround()) {
            client.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.Full(
                            player.getX(), player.getY(), player.getZ(),
                            player.getYaw(), player.getPitch(),
                            true, player.horizontalCollision));
        }

        // In singleplayer, directly manipulate the server player
        MinecraftServer server = client.getServer();
        if (server != null) {
            ServerPlayerEntity serverPlayer = server.getPlayerManager()
                    .getPlayer(player.getUuid());
            if (serverPlayer != null) {
                serverPlayer.fallDistance = 0.0f;
            }
        }
    }
}
