package com.reachfly;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Client-side handler for syncing features with the f1sch Server Addon v2.
 *
 * When the server addon is installed, this sends feature state changes
 * so the server can apply them authoritatively. Also receives ESP entity
 * data from the server for extended range tracking.
 *
 * FIX: If the server addon is NOT installed (no FeatureSyncPayload channel
 * registered on the server), all packets are silently dropped and features
 * use client-only mode. We detect this via ClientPlayNetworking.canSend()
 * before attempting to send, so no exceptions are thrown and no spam occurs.
 */
public class ServerSyncHandler {

    // Extended ESP entity data received from server
    public static final List<EspDataPayload.EntityEntry> serverEspEntities =
            new CopyOnWriteArrayList<>();
    public static boolean serverEspActive = false;

    /** True once the server has ACKed our channel (server addon is present). */
    public static boolean serverAddonPresent = false;

    // Track last-sent state to avoid spamming packets
    private static boolean lastOpSelfEnabled = false;
    private static boolean lastKnockbackEnabled = false;
    private static float lastKnockbackStrength = 0;
    private static boolean lastReachEnabled = false;
    private static float lastReachDistance = 0;
    private static boolean lastSpeedEnabled = false;
    private static float lastSpeedMultiplier = 0;
    private static boolean lastNoFallEnabled = false;
    private static boolean lastFlyEnabled = false;
    private static float lastFlySpeed = 0;
    private static boolean lastEspEnabled = false;

    private static int syncTicker = 0;
    // Probe ticks: try canSend for a few ticks after joining before giving up
    private static int probeTicker = 0;
    private static boolean probeComplete = false;

    /**
     * Register payload types and S2C receivers.
     * Called during mod initialization.
     */
    public static void registerPayloads() {
        // Register C2S payload types
        PayloadTypeRegistry.playC2S().register(FeatureSyncPayload.ID, FeatureSyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ItemGivePayload.ID, ItemGivePayload.CODEC);

        // Register S2C payload type + receiver
        PayloadTypeRegistry.playS2C().register(EspDataPayload.ID, EspDataPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(EspDataPayload.ID,
                (payload, context) -> {
                    // If we receive any S2C ESP data, the server addon is definitely there
                    serverAddonPresent = true;
                    serverEspEntities.clear();
                    serverEspEntities.addAll(payload.entities());
                    serverEspActive = true;
                });
    }

    /**
     * Called every client tick. Detects feature state changes and syncs to server.
     */
    public static void tick(MinecraftClient client) {
        if (client.player == null || client.getNetworkHandler() == null) return;

        // FIX: Probe whether the server addon is present using canSend().
        // canSend() returns true only if the server has registered the channel.
        // We check for the first ~40 ticks after joining (2 seconds) to allow
        // the server handshake to complete. After that, we commit to client-only
        // mode if the addon isn't detected.
        if (!probeComplete) {
            probeTicker++;
            if (ClientPlayNetworking.canSend(FeatureSyncPayload.ID)) {
                serverAddonPresent = true;
                probeComplete = true;
                ReachFlyClient.LOGGER.info("[f1sch] Server addon detected - using server-authoritative mode.");
            } else if (probeTicker >= 40) {
                probeComplete = true;
                serverAddonPresent = false;
                ReachFlyClient.LOGGER.info("[f1sch] No server addon detected - using client-only mode.");
            }
        }

        syncTicker++;

        // Only sync if server addon is confirmed present
        if (serverAddonPresent) {
            syncOpSelf();
            syncKnockback();
            syncReach();
            syncSpeed();
            syncNoFall();
            syncFly();
            syncEsp();

            // Periodic full resync every 5 seconds (100 ticks) as safety net
            if (syncTicker >= 100) {
                syncTicker = 0;
                forceResync();
            }
        }

        // Clear server ESP data if ESP is disabled
        if (!ModConfig.espEnabled && serverEspActive) {
            serverEspEntities.clear();
            serverEspActive = false;
        }
    }

    /** Called when the player disconnects - reset all state for next connection. */
    public static void onDisconnect() {
        serverAddonPresent = false;
        probeComplete = false;
        probeTicker = 0;
        syncTicker = 0;
        serverEspEntities.clear();
        serverEspActive = false;
        // Reset last-sent tracking so next login triggers a full sync
        lastOpSelfEnabled = false;
        lastKnockbackEnabled = false;
        lastKnockbackStrength = 0;
        lastReachEnabled = false;
        lastReachDistance = 0;
        lastSpeedEnabled = false;
        lastSpeedMultiplier = 0;
        lastNoFallEnabled = false;
        lastFlyEnabled = false;
        lastFlySpeed = 0;
        lastEspEnabled = false;
    }

    private static void syncOpSelf() {
        if (ModConfig.proUnlocked && ModConfig.opSelfEnabled && !lastOpSelfEnabled) {
            lastOpSelfEnabled = true;
            sendSync("op", true, 4); // OP level 4
        }
        if (!ModConfig.opSelfEnabled && lastOpSelfEnabled) {
            lastOpSelfEnabled = false;
        }
    }

    private static void syncKnockback() {
        if (ModConfig.knockbackEnabled != lastKnockbackEnabled ||
                (ModConfig.knockbackEnabled && ModConfig.knockbackStrength != lastKnockbackStrength)) {
            lastKnockbackEnabled = ModConfig.knockbackEnabled;
            lastKnockbackStrength = ModConfig.knockbackStrength;
            sendSync("knockback", ModConfig.knockbackEnabled, ModConfig.knockbackStrength);
        }
    }

    private static void syncReach() {
        if (ModConfig.reachEnabled != lastReachEnabled ||
                (ModConfig.reachEnabled && ModConfig.reachDistance != lastReachDistance)) {
            lastReachEnabled = ModConfig.reachEnabled;
            lastReachDistance = ModConfig.reachDistance;
            sendSync("reach", ModConfig.reachEnabled, ModConfig.reachDistance);
        }
    }

    private static void syncSpeed() {
        if (ModConfig.speedEnabled != lastSpeedEnabled ||
                (ModConfig.speedEnabled && ModConfig.speedMultiplier != lastSpeedMultiplier)) {
            lastSpeedEnabled = ModConfig.speedEnabled;
            lastSpeedMultiplier = ModConfig.speedMultiplier;
            sendSync("speed", ModConfig.speedEnabled, ModConfig.speedMultiplier);
        }
    }

    private static void syncNoFall() {
        if (ModConfig.noFallEnabled != lastNoFallEnabled) {
            lastNoFallEnabled = ModConfig.noFallEnabled;
            sendSync("nofall", ModConfig.noFallEnabled, 0);
        }
    }

    private static void syncFly() {
        if (ModConfig.flyEnabled != lastFlyEnabled ||
                (ModConfig.flyEnabled && ModConfig.flySpeed != lastFlySpeed)) {
            lastFlyEnabled = ModConfig.flyEnabled;
            lastFlySpeed = ModConfig.flySpeed;
            sendSync("fly", ModConfig.flyEnabled, ModConfig.flySpeed);
        }
    }

    private static void syncEsp() {
        if (ModConfig.espEnabled != lastEspEnabled) {
            lastEspEnabled = ModConfig.espEnabled;
            sendSync("esp", ModConfig.espEnabled, 200); // 200 block range
        }
    }

    private static void forceResync() {
        if (ModConfig.knockbackEnabled)
            sendSync("knockback", true, ModConfig.knockbackStrength);
        if (ModConfig.reachEnabled)
            sendSync("reach", true, ModConfig.reachDistance);
        if (ModConfig.speedEnabled)
            sendSync("speed", true, ModConfig.speedMultiplier);
        if (ModConfig.noFallEnabled)
            sendSync("nofall", true, 0);
        if (ModConfig.flyEnabled)
            sendSync("fly", true, ModConfig.flySpeed);
        if (ModConfig.espEnabled)
            sendSync("esp", true, 200);
    }

    private static void sendSync(String feature, boolean enabled, float value) {
        // FIX: Always guard with canSend() so we never throw on missing channel
        if (!ClientPlayNetworking.canSend(FeatureSyncPayload.ID)) return;
        try {
            ClientPlayNetworking.send(new FeatureSyncPayload(feature, enabled, value));
        } catch (Exception ignored) {
            // Defensive: server addon not installed or channel closed mid-session
        }
    }
}
