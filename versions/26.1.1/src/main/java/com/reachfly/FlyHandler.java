package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * Handles survival fly logic each tick.
 * Allows flying in survival/adventure mode when enabled.
 */
public class FlyHandler {

    private static boolean wasFlying = false;

    /**
     * Called every client tick to manage fly state and speed.
     */
    public static void tick(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) return;

        // Don't override creative mode flight
        if (player.getAbilities().instabuild) return;

        if (ModConfig.flyEnabled) {
            wasFlying = true;

            // Enable flight ability
            player.getAbilities().mayFly = true;

            // Set fly speed based on config (vanilla default is 0.05f)
            player.getAbilities().setFlyingSpeed(0.05f * ModConfig.flySpeed);

            // Prevent fall damage while flying by resetting fall distance
            if (player.getAbilities().flying) {
                player.fallDistance = 0.0f;
            }
        } else {
            // Only disable if we previously enabled it (don't touch spectator/creative)
            if (!player.isSpectator()) {
                // Notify the player when fly is turned off and they're airborne
                if (wasFlying) {
                    wasFlying = false;
                    if (!player.onGround()) {
                        player.sendMessage(
                            net.minecraft.network.chat.Component.literal("\u00a7c[OSP] \u00a7eFly disabled! You are falling - brace for landing!"),
                            true  // overlay / action bar
                        );
                    } else {
                        player.sendMessage(
                            net.minecraft.network.chat.Component.literal("\u00a7c[OSP] \u00a7aFly disabled. Safe on the ground."),
                            true
                        );
                    }
                }
                player.getAbilities().mayFly = false;
                player.getAbilities().flying = false;
            }
        }
    }
}
