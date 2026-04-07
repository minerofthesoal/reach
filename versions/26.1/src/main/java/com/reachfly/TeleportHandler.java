package com.reachfly;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

/**
 * Teleport handler with three paths:
 *
 * 1. SINGLEPLAYER: Direct server requestTeleport()
 * 2. NORMAL MODE: Fabric addon packet, or datapack /trigger fallback
 * 3. BETA MODE: Direct client-side position + packet flood
 */
public class TeleportHandler {

    private static boolean pendingTeleport = false;
    private static int cooldownTicks = 0;
    private static long lastTeleportTime = 0;

    public static void triggerTeleport() {
        // Prevent rapid trigger from held key - require 2 second gap minimum
        long now = System.currentTimeMillis();
        if (now - lastTeleportTime < 2000) return;
        pendingTeleport = true;
    }

    public static void tick(Minecraft client) {
        if (client.player == null) return;

        if (cooldownTicks > 0) {
            cooldownTicks--;
            return;
        }

        if (!pendingTeleport) return;
        pendingTeleport = false;
        cooldownTicks = 60; // 3 second cooldown to prevent spam
        lastTeleportTime = System.currentTimeMillis();

        double tx = ModConfig.tpX;
        double ty = ModConfig.tpY;
        double tz = ModConfig.tpZ;

        LocalPlayer player = client.player;

        // Path 1: Singleplayer - direct server access
        MinecraftServer server = client.getServer();
        if (server != null) {
            ServerPlayer serverPlayer = server.getPlayerManager()
                    .getPlayer(player.getUuid());
            if (serverPlayer != null) {
                serverPlayer.requestTeleport(tx, ty, tz);
                player.sendMessage(
                        Component.literal("\u00a7a[TP] Teleported to " +
                                String.format("%.0f, %.0f, %.0f", tx, ty, tz)),
                        true);
                return;
            }
        }

        if (ModConfig.tpUseServerAddon) {
            normalTeleport(client, player, tx, ty, tz);
        } else {
            betaTeleport(client, player, tx, ty, tz);
        }
    }

    private static void normalTeleport(Minecraft client, LocalPlayer player,
                                        double tx, double ty, double tz) {
        if (ClientPlayNetworking.canSend(TeleportPayload.ID)) {
            ClientPlayNetworking.send(new TeleportPayload(tx, ty, tz));
            player.sendMessage(
                    Component.literal("\u00a7a[TP] Teleported via server addon: " +
                            String.format("%.0f, %.0f, %.0f", tx, ty, tz)),
                    true);
            return;
        }

        // Datapack fallback: send ALL trigger commands at once (they're different objectives)
        datapackTeleport(client, player, tx, ty, tz);
    }

    /**
     * Datapack mode: Send all /trigger commands in one tick.
     * Each trigger objective is independent so they can all fire in the same tick.
     * The datapack tick function processes osp.tp=1 next server tick.
     */
    private static void datapackTeleport(Minecraft client, LocalPlayer player,
                                          double tx, double ty, double tz) {
        if (client.getNetworkHandler() == null) return;

        // Send all coordinates + trigger at once
        client.getNetworkHandler().sendChatCommand("trigger osp.tp_x set " + (int) tx);
        client.getNetworkHandler().sendChatCommand("trigger osp.tp_y set " + (int) ty);
        client.getNetworkHandler().sendChatCommand("trigger osp.tp_z set " + (int) tz);
        client.getNetworkHandler().sendChatCommand("trigger osp.tp set 1");

        player.sendMessage(
                Component.literal("\u00a7a[TP] Teleporting to " +
                        String.format("%.0f, %.0f, %.0f", tx, ty, tz)),
                true);
    }

    private static void betaTeleport(Minecraft client, LocalPlayer player,
                                      double tx, double ty, double tz) {
        if (client.getNetworkHandler() == null) return;

        player.setPosition(tx, ty, tz);
        player.fallDistance = 0.0f;
        player.setDeltaMovement(0, 0, 0);

        for (int i = 0; i < 5; i++) {
            client.getNetworkHandler().sendPacket(
                    new ServerboundMovePlayerPacket.Full(
                            tx, ty, tz,
                            player.getYaw(), player.getPitch(),
                            true, false));
        }

        player.sendMessage(
                Component.literal("\u00a7a[TP BETA] Teleported to " +
                        String.format("%.0f, %.0f, %.0f", tx, ty, tz)),
                true);
    }

    public static void registerPayload() {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
                .playC2S()
                .register(TeleportPayload.ID, TeleportPayload.CODEC);
    }
}
