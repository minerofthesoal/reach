package com.reachfly;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Teleport handler with two distinct modes:
 *
 * NORMAL MODE (tpUseServerAddon = true):
 *   Uses client + server cooperation. In singleplayer, directly teleports
 *   the server-side player. In multiplayer, sends a custom TeleportPayload
 *   packet to the server addon which performs the teleport server-side.
 *   Reliable and clean - no rubberbanding.
 *
 * BETA MODE (tpUseServerAddon = false):
 *   Fully client-side only. Sets the player's position locally and sends
 *   spoofed position packets to the server. Does NOT access any server
 *   internals. Works anywhere but may rubberband on vanilla servers since
 *   the server may reject the position change.
 */
public class TeleportHandler {

    private static boolean pendingTeleport = false;
    private static int cooldownTicks = 0;

    public static void triggerTeleport() {
        pendingTeleport = true;
    }

    public static void tick(MinecraftClient client) {
        if (client.player == null) return;

        if (cooldownTicks > 0) {
            cooldownTicks--;
            return;
        }

        if (!pendingTeleport) return;
        pendingTeleport = false;
        cooldownTicks = 20; // 1 second cooldown

        double tx = ModConfig.tpX;
        double ty = ModConfig.tpY;
        double tz = ModConfig.tpZ;

        ClientPlayerEntity player = client.player;

        if (ModConfig.tpUseServerAddon) {
            // === NORMAL MODE: client + server ===
            normalTeleport(client, player, tx, ty, tz);
        } else {
            // === BETA MODE: fully client-side ===
            betaTeleport(client, player, tx, ty, tz);
        }
    }

    /**
     * Normal teleport - uses server-side teleportation.
     * Singleplayer: direct server player teleport.
     * Multiplayer: sends custom packet to server addon.
     */
    private static void normalTeleport(MinecraftClient client, ClientPlayerEntity player,
                                        double tx, double ty, double tz) {
        // Try singleplayer/LAN direct teleport first
        MinecraftServer server = client.getServer();
        if (server != null) {
            ServerPlayerEntity serverPlayer = server.getPlayerManager()
                    .getPlayer(player.getUuid());
            if (serverPlayer != null) {
                serverPlayer.requestTeleport(tx, ty, tz);
                player.sendMessage(
                        Text.literal("\u00a7a[TP] Teleported to " +
                                String.format("%.0f, %.0f, %.0f", tx, ty, tz)),
                        true);
                return;
            }
        }

        // Multiplayer: send custom packet to server addon
        try {
            ClientPlayNetworking.send(new TeleportPayload(tx, ty, tz));
            player.sendMessage(
                    Text.literal("\u00a7a[TP] Sent teleport request to server: " +
                            String.format("%.0f, %.0f, %.0f", tx, ty, tz)),
                    true);
        } catch (Exception e) {
            player.sendMessage(
                    Text.literal("\u00a7c[TP] Server addon not installed! Use Beta mode or install the addon."),
                    true);
        }
    }

    /**
     * Beta teleport - fully client-side.
     * Sets local position and sends spoofed position packets.
     * Does NOT access server internals at all.
     * May rubberband on servers with position validation.
     */
    private static void betaTeleport(MinecraftClient client, ClientPlayerEntity player,
                                      double tx, double ty, double tz) {
        if (client.getNetworkHandler() == null) return;

        // Set client-side position
        player.setPosition(tx, ty, tz);
        player.fallDistance = 0.0f;

        // Send position packet to server telling it we moved
        client.getNetworkHandler().sendPacket(
                new PlayerMoveC2SPacket.Full(
                        tx, ty, tz,
                        player.getYaw(), player.getPitch(),
                        true, player.horizontalCollision));

        player.sendMessage(
                Text.literal("\u00a7e[TP BETA] Teleported to " +
                        String.format("%.0f, %.0f, %.0f", tx, ty, tz) +
                        " \u00a77(client-side, may rubberband)"),
                true);
    }

    /**
     * Register the payload type for networking.
     * Must be called during mod init.
     */
    public static void registerPayload() {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
                .playC2S()
                .register(TeleportPayload.ID, TeleportPayload.CODEC);
    }
}
