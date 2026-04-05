package com.reachfly;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Teleport handler with two modes:
 * 1. Server addon mode: sends custom TeleportPayload packet (reliable, works on any server with addon)
 * 2. Beta mode: direct teleport in singleplayer, position-packet spoofing in multiplayer (unreliable)
 *
 * Toggle activates a one-shot teleport to the configured coordinates.
 */
public class TeleportHandler {

    private static boolean pendingTeleport = false;
    private static boolean serverAddonDetected = false;
    private static int cooldownTicks = 0;

    public static void setServerAddonDetected(boolean detected) {
        serverAddonDetected = detected;
    }

    public static boolean isServerAddonDetected() {
        return serverAddonDetected;
    }

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

        // Method 1: Try server addon packet (works on multiplayer with addon installed)
        if (ModConfig.tpUseServerAddon) {
            try {
                ClientPlayNetworking.send(new TeleportPayload(tx, ty, tz));
                player.sendMessage(
                        Text.literal("\u00a7a[TP] Sent teleport request to server: " +
                                String.format("%.0f, %.0f, %.0f", tx, ty, tz)),
                        true);
                return;
            } catch (Exception e) {
                // Server doesn't have addon, fall through to beta mode
                player.sendMessage(
                        Text.literal("\u00a7c[TP] Server addon not available, using beta mode"),
                        true);
            }
        }

        // Method 2: Beta mode - direct teleport
        MinecraftServer server = client.getServer();
        if (server != null) {
            // Singleplayer / LAN host: directly teleport server-side player
            ServerPlayerEntity serverPlayer = server.getPlayerManager()
                    .getPlayer(player.getUuid());
            if (serverPlayer != null) {
                serverPlayer.teleport(
                        serverPlayer.getServerWorld(),
                        tx, ty, tz,
                        java.util.Set.of(),
                        serverPlayer.getYaw(), serverPlayer.getPitch(),
                        true);
                player.sendMessage(
                        Text.literal("\u00a7a[TP] Teleported to " +
                                String.format("%.0f, %.0f, %.0f", tx, ty, tz)),
                        true);
                return;
            }
        }

        // Method 3: Multiplayer without addon - send position packets (beta, may rubberband)
        player.setPosition(tx, ty, tz);
        client.getNetworkHandler().sendPacket(
                new PlayerMoveC2SPacket.Full(
                        tx, ty, tz,
                        player.getYaw(), player.getPitch(),
                        true, player.horizontalCollision));
        player.sendMessage(
                Text.literal("\u00a7e[TP BETA] Attempted teleport to " +
                        String.format("%.0f, %.0f, %.0f", tx, ty, tz) +
                        " \u00a7c(may rubberband on vanilla servers)"),
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
