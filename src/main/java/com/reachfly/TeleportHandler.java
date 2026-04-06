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
 * Teleport handler with two modes:
 *
 * NORMAL MODE (tpUseServerAddon = true):
 *   Server-side teleport. Reliable, no rubberbanding.
 *
 * BETA MODE (tpUseServerAddon = false):
 *   Client-side incremental teleport. Moves in small steps (8 blocks per tick)
 *   to avoid triggering the server's "moved too quickly" detection and crashes.
 *   Sends position packets gradually instead of a single large jump.
 */
public class TeleportHandler {

    private static boolean pendingTeleport = false;
    private static int cooldownTicks = 0;

    // Incremental TP state
    private static boolean incrementalActive = false;
    private static double targetX, targetY, targetZ;
    private static int stepCount = 0;
    private static final double STEP_DISTANCE = 8.0;
    private static final int MAX_STEPS_PER_TICK = 3;
    private static final int PACKET_DELAY = 1;
    private static int delayCounter = 0;

    public static void triggerTeleport() {
        pendingTeleport = true;
    }

    public static void tick(MinecraftClient client) {
        if (client.player == null) return;

        if (cooldownTicks > 0) {
            cooldownTicks--;
            return;
        }

        // Handle incremental beta TP in progress
        if (incrementalActive) {
            tickIncremental(client);
            return;
        }

        if (!pendingTeleport) return;
        pendingTeleport = false;
        cooldownTicks = 20;

        double tx = ModConfig.tpX;
        double ty = ModConfig.tpY;
        double tz = ModConfig.tpZ;

        ClientPlayerEntity player = client.player;

        if (ModConfig.tpUseServerAddon) {
            normalTeleport(client, player, tx, ty, tz);
        } else {
            startBetaTeleport(client, player, tx, ty, tz);
        }
    }

    private static void normalTeleport(MinecraftClient client, ClientPlayerEntity player,
                                        double tx, double ty, double tz) {
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
     * Start incremental beta teleport.
     * Instead of jumping directly (which crashes servers), move in small 8-block
     * steps with position packets between each step.
     */
    private static void startBetaTeleport(MinecraftClient client, ClientPlayerEntity player,
                                           double tx, double ty, double tz) {
        if (client.getNetworkHandler() == null) return;

        Vec3d pos = player.getEntityPos();
        double dist = Math.sqrt((tx - pos.x) * (tx - pos.x) + (ty - pos.y) * (ty - pos.y) + (tz - pos.z) * (tz - pos.z));

        if (dist <= STEP_DISTANCE * 2) {
            // Short distance - just do it directly
            directBetaTp(client, player, tx, ty, tz);
            return;
        }

        // Long distance - use incremental approach
        targetX = tx;
        targetY = ty;
        targetZ = tz;
        incrementalActive = true;
        stepCount = 0;
        delayCounter = 0;

        int totalSteps = (int) Math.ceil(dist / STEP_DISTANCE);
        player.sendMessage(
                Text.literal("\u00a7e[TP BETA] Teleporting to " +
                        String.format("%.0f, %.0f, %.0f", tx, ty, tz) +
                        String.format(" (%.0f blocks, ~%d steps)", dist, totalSteps)),
                true);
    }

    private static void tickIncremental(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.getNetworkHandler() == null) {
            incrementalActive = false;
            return;
        }

        delayCounter++;
        if (delayCounter < PACKET_DELAY) return;
        delayCounter = 0;

        Vec3d pos = player.getEntityPos();
        double dx = targetX - pos.x;
        double dy = targetY - pos.y;
        double dz = targetZ - pos.z;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist <= STEP_DISTANCE * 2) {
            // Close enough - final jump
            directBetaTp(client, player, targetX, targetY, targetZ);
            incrementalActive = false;
            player.sendMessage(
                    Text.literal("\u00a7a[TP BETA] Arrived at " +
                            String.format("%.0f, %.0f, %.0f", targetX, targetY, targetZ) +
                            String.format(" (%d steps)", stepCount)),
                    true);
            return;
        }

        // Move up to MAX_STEPS_PER_TICK steps this tick
        for (int i = 0; i < MAX_STEPS_PER_TICK; i++) {
            pos = player.getEntityPos();
            dx = targetX - pos.x;
            dy = targetY - pos.y;
            dz = targetZ - pos.z;
            dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (dist <= STEP_DISTANCE) break;

            double ratio = STEP_DISTANCE / dist;
            double nx = pos.x + dx * ratio;
            double ny = pos.y + dy * ratio;
            double nz = pos.z + dz * ratio;

            player.setPosition(nx, ny, nz);
            client.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.Full(
                            nx, ny, nz,
                            player.getYaw(), player.getPitch(),
                            false, player.horizontalCollision));
            stepCount++;
        }

        // Safety: abort if too many steps (prevent infinite loop)
        if (stepCount > 10000) {
            incrementalActive = false;
            player.sendMessage(
                    Text.literal("\u00a7c[TP BETA] Aborted - too many steps"),
                    true);
        }
    }

    private static void directBetaTp(MinecraftClient client, ClientPlayerEntity player,
                                      double tx, double ty, double tz) {
        player.setPosition(tx, ty, tz);
        player.fallDistance = 0.0f;
        client.getNetworkHandler().sendPacket(
                new PlayerMoveC2SPacket.Full(
                        tx, ty, tz,
                        player.getYaw(), player.getPitch(),
                        true, player.horizontalCollision));
    }

    public static void registerPayload() {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
                .playC2S()
                .register(TeleportPayload.ID, TeleportPayload.CODEC);
    }
}
