package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Freecam: Allows the camera to fly freely while the player's server-side
 * position stays in place. Works by:
 *   1. On enable: save the player's real position, enable noclip + flight
 *   2. Each tick: let the player move freely (noclip, no gravity)
 *   3. On disable: restore the player's real position
 *
 * Movement packets are still sent but the server sees them as invalid
 * and typically rubber-bands the player back to their real position.
 * The visual experience is a free-flying camera.
 */
public class FreecamHandler {

    private static boolean active = false;
    private static double savedX, savedY, savedZ;
    private static float savedYaw, savedPitch;
    private static boolean wasFlying = false;

    public static boolean isActive() {
        return active && ModConfig.freecamEnabled && ModConfig.proUnlocked;
    }

    public static void tick(MinecraftClient client) {
        if (!ModConfig.proUnlocked) return;
        if (client.player == null) return;

        ClientPlayerEntity p = client.player;

        if (ModConfig.freecamEnabled && !active) {
            // Entering freecam - save position
            active = true;
            savedX = p.getX();
            savedY = p.getY();
            savedZ = p.getZ();
            savedYaw = p.getYaw();
            savedPitch = p.getPitch();
            wasFlying = p.getAbilities().flying;

            // Enable noclip and flight
            p.noClip = true;
            p.getAbilities().allowFlying = true;
            p.getAbilities().flying = true;
            p.getAbilities().setFlySpeed(0.1f);
            p.sendAbilitiesUpdate();

            p.sendMessage(
                    net.minecraft.text.Text.literal("\u00a7b[Freecam] Enabled - fly around freely"),
                    true);
        } else if (!ModConfig.freecamEnabled && active) {
            // Exiting freecam - restore position
            active = false;
            p.setPosition(savedX, savedY, savedZ);
            p.setYaw(savedYaw);
            p.setPitch(savedPitch);
            p.noClip = false;
            p.fallDistance = 0;

            // Restore flight state
            if (!p.isCreative() && !p.isSpectator()) {
                p.getAbilities().allowFlying = ModConfig.flyEnabled;
                p.getAbilities().flying = wasFlying && ModConfig.flyEnabled;
                if (ModConfig.flyEnabled) {
                    p.getAbilities().setFlySpeed(0.05f * ModConfig.flySpeed);
                } else {
                    p.getAbilities().setFlySpeed(0.05f);
                }
                p.sendAbilitiesUpdate();
            }

            p.sendMessage(
                    net.minecraft.text.Text.literal("\u00a7b[Freecam] Disabled - returned to position"),
                    true);
        }

        if (active && ModConfig.freecamEnabled) {
            // Keep noclip enabled and prevent fall damage
            p.noClip = true;
            p.fallDistance = 0;

            // Ensure flight stays on
            if (!p.getAbilities().flying) {
                p.getAbilities().flying = true;
                p.sendAbilitiesUpdate();
            }
        }
    }

    /**
     * Returns saved position for rendering ghost at original location.
     */
    public static Vec3d getSavedPosition() {
        return new Vec3d(savedX, savedY, savedZ);
    }
}
