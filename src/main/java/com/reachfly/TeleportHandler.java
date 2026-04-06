package com.reachfly;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Teleport handler with three paths:
 *
 * 1. SINGLEPLAYER: Direct server requestTeleport()
 * 2. NORMAL MODE: Checks if Fabric addon can receive packets.
 *    If not, sends /trigger commands for the datapack.
 * 3. BETA MODE: Direct client-side position set + packet flood.
 */
public class TeleportHandler {

    private static boolean pendingTeleport = false;
    private static int cooldownTicks = 0;

    // Datapack command queue (spread across ticks)
    private static String[] pendingCommands = null;
    private static int commandIndex = 0;

    public static void triggerTeleport() {
        pendingTeleport = true;
    }

    public static void tick(MinecraftClient client) {
        if (client.player == null) return;

        // Process queued datapack commands (one per tick for reliability)
        if (pendingCommands != null && client.getNetworkHandler() != null) {
            if (commandIndex < pendingCommands.length) {
                client.getNetworkHandler().sendChatCommand(pendingCommands[commandIndex]);
                commandIndex++;
            } else {
                pendingCommands = null;
                commandIndex = 0;
            }
            return;
        }

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

        // Path 1: Singleplayer - direct server access
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

        // Path 2 & 3: Multiplayer
        if (ModConfig.tpUseServerAddon) {
            normalTeleport(client, player, tx, ty, tz);
        } else {
            betaTeleport(client, player, tx, ty, tz);
        }
    }

    /**
     * Normal mode: Check if server has the Fabric addon registered.
     * If yes, send custom packet. If no, use datapack /trigger commands.
     */
    private static void normalTeleport(MinecraftClient client, ClientPlayerEntity player,
                                        double tx, double ty, double tz) {
        // Check if the server actually supports our custom packet
        if (ClientPlayNetworking.canSend(TeleportPayload.ID)) {
            ClientPlayNetworking.send(new TeleportPayload(tx, ty, tz));
            player.sendMessage(
                    Text.literal("\u00a7a[TP] Teleported via server addon: " +
                            String.format("%.0f, %.0f, %.0f", tx, ty, tz)),
                    true);
            return;
        }

        // Fallback: datapack mode via /trigger commands
        // Queue commands to send one per tick (triggers need re-enabling between uses)
        datapackTeleport(client, player, tx, ty, tz);
    }

    /**
     * Datapack mode: Queue /trigger commands sent one per tick.
     */
    private static void datapackTeleport(MinecraftClient client, ClientPlayerEntity player,
                                          double tx, double ty, double tz) {
        pendingCommands = new String[]{
                "trigger osp.tp_x set " + (int) tx,
                "trigger osp.tp_y set " + (int) ty,
                "trigger osp.tp_z set " + (int) tz,
                "trigger osp.tp set 1"
        };
        commandIndex = 0;

        player.sendMessage(
                Text.literal("\u00a7e[TP] Sending datapack teleport to " +
                        String.format("%.0f, %.0f, %.0f", tx, ty, tz) + "..."),
                true);
    }

    /**
     * Beta mode: Direct client-side teleport with packet flood.
     */
    private static void betaTeleport(MinecraftClient client, ClientPlayerEntity player,
                                      double tx, double ty, double tz) {
        if (client.getNetworkHandler() == null) return;

        player.setPosition(tx, ty, tz);
        player.fallDistance = 0.0f;
        player.setVelocity(0, 0, 0);

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
