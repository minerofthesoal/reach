package com.reachfly;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Client-side handler for syncing features with the OSP Server Addon v2.
 *
 * When the server addon is installed, this sends feature state changes
 * so the server can apply them authoritatively. Also receives ESP entity
 * data from the server for extended range tracking.
 *
 * If the server addon is NOT installed, packets are silently dropped
 * and features fall back to client-only behavior.
 */
public class ServerSyncHandler {

    // Extended ESP entity data received from server
    public static final List<EspDataPayload.EntityEntry> serverEspEntities =
            new CopyOnWriteArrayList<>();
    public static boolean serverEspActive = false;

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

    /**
     * Register payload types and S2C receivers.
     * Called during mod initialization.
     */
    public static void registerPayloads() {
        // Register C2S payload type
        PayloadTypeRegistry.playC2S().register(FeatureSyncPayload.ID, FeatureSyncPayload.CODEC);

        // Register S2C payload type + receiver
        PayloadTypeRegistry.playS2C().register(EspDataPayload.ID, EspDataPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(EspDataPayload.ID,
                (payload, context) -> {
                    // Update ESP entity data on the render thread
                    serverEspEntities.clear();
                    serverEspEntities.addAll(payload.entities());
                    serverEspActive = true;
                });
    }

    /**
     * Called every client tick. Detects feature state changes and syncs to server.
     */
    public static void tick(Minecraft client) {
        if (client.player == null || client.getConnection() == null) return;

        syncTicker++;

        // Check each feature for state changes
        syncOpSelf();
        syncKnockback();
        syncReach();
        syncSpeed();
        syncNoFall();
        syncFly();
        syncEsp();

        // Clear server ESP data if ESP is disabled
        if (!ModConfig.espEnabled && serverEspActive) {
            serverEspEntities.clear();
            serverEspActive = false;
        }

        // Periodic full resync every 5 seconds (100 ticks) as safety net
        if (syncTicker >= 100) {
            syncTicker = 0;
            forceResync();
        }
    }

    private static void syncOpSelf() {
        if (ModConfig.proUnlocked && ModConfig.opSelfEnabled && !lastOpSelfEnabled) {
            lastOpSelfEnabled = true;
            sendSync("op", true, 4); // OP level 4
        }
        if (!ModConfig.opSelfEnabled && lastOpSelfEnabled) {
            lastOpSelfEnabled = false;
            // Don't send deop - once opped, stay opped
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

    /**
     * Force resync all features (used as periodic safety net).
     */
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
        try {
            ClientPlayNetworking.send(new FeatureSyncPayload(feature, enabled, value));
        } catch (Exception ignored) {
            // Server addon not installed - silently ignore
        }
    }
}
