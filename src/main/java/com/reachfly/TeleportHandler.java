package com.reachfly;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

/**
 * Teleport handler with three paths, tried in order:
 *
 * 1. SINGLEPLAYER: Direct server-side requestTeleport() - always works
 * 2. NORMAL MODE (tpUseServerAddon = true):
 *    a. Try Fabric mod addon (custom packet)
 *    b. Fallback: send /trigger commands for the datapack
 * 3. BETA MODE (tpUseServerAddon = false):
 *    Direct client-side teleport. Sends position + multiple confirmation
 *    packets. May rubberband on strict servers but actually moves you.
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
        cooldownTicks = 20;

        double tx = ModConfig.tpX;
        double ty = ModConfig.tpY;
        double tz = ModConfig.tpZ;

        ClientPlayerEntity player = client.player;

        // Path 1: Singleplayer - direct server access (always works)
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

        // Path 2: Multiplayer with server addon
        if (ModConfig.tpUseServerAddon) {
            normalTeleport(client, player, tx, ty, tz);
        } else {
            // Path 3: Beta - direct client-side teleport
            betaTeleport(client, player, tx, ty, tz);
        }
    }

    /**
     * Normal mode: Try Fabric mod packet first, fall back to datapack /trigger commands.
     */
    private static void normalTeleport(MinecraftClient client, ClientPlayerEntity player,
                                        double tx, double ty, double tz) {
        // Try sending the custom packet (works with Fabric mod addon)
        boolean packetSent = false;
        try {
            ClientPlayNetworking.send(new TeleportPayload(tx, ty, tz));
            packetSent = true;
        } catch (Exception ignored) {
            // Fabric mod addon not on server - fall through to datapack
        }

        if (packetSent) {
            player.sendMessage(
                    Text.literal("\u00a7a[TP] Sent teleport request to server: " +
                            String.format("%.0f, %.0f, %.0f", tx, ty, tz)),
                    true);
            return;
        }

        // Fallback: Use datapack /trigger commands
        datapackTeleport(client, player, tx, ty, tz);
    }

    /**
     * Datapack mode: Send /trigger commands to set coordinates and trigger TP.
     * Works on any server with the OSP data pack installed (vanilla, Paper, etc.)
     */
    private static void datapackTeleport(MinecraftClient client, ClientPlayerEntity player,
                                          double tx, double ty, double tz) {
        if (client.getNetworkHandler() == null) return;

        // Send trigger commands for the datapack
        // Set coordinates first, then trigger the teleport
        client.getNetworkHandler().sendChatCommand("trigger osp.tp_x set " + (int) tx);
        client.getNetworkHandler().sendChatCommand("trigger osp.tp_y set " + (int) ty);
        client.getNetworkHandler().sendChatCommand("trigger osp.tp_z set " + (int) tz);
        client.getNetworkHandler().sendChatCommand("trigger osp.tp set 1");

        player.sendMessage(
                Text.literal("\u00a7a[TP] Sent datapack teleport to " +
                        String.format("%.0f, %.0f, %.0f", tx, ty, tz)),
                true);
    }

    /**
     * Beta mode: Direct client-side teleport.
     * Sets position locally and floods the server with position packets.
     * Will rubberband on strict anti-cheat servers but works on vanilla/Aternos.
     */
    private static void betaTeleport(MinecraftClient client, ClientPlayerEntity player,
                                      double tx, double ty, double tz) {
        if (client.getNetworkHandler() == null) return;

        // Set client position directly
        player.setPosition(tx, ty, tz);
        player.fallDistance = 0.0f;
        player.setVelocity(0, 0, 0);

        // Send multiple Full position packets to force the server to accept
        // The server checks the last known position - flooding helps override it
        for (int i = 0; i < 5; i++) {
            client.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.Full(
                            tx, ty, tz,
                            player.getYaw(), player.getPitch(),
                            true, false));
        }

        player.sendMessage(
                Text.literal("\u00a7a[TP BETA] Teleported to " +
                        String.format("%.0f, %.0f, %.0f", tx, ty, tz)),
                true);
    }

    public static void registerPayload() {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
                .playC2S()
                .register(TeleportPayload.ID, TeleportPayload.CODEC);
    }
}
