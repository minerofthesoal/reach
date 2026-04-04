package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Handles survival fly logic each tick.
 * Allows flying in survival/adventure mode when enabled.
 */
public class FlyHandler {

    /**
     * Called every client tick to manage fly state and speed.
     */
    public static void tick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        // Don't override creative mode flight
        if (player.getAbilities().creativeMode) return;

        if (ModConfig.flyEnabled) {
            // Enable flight ability
            player.getAbilities().allowFlying = true;

            // Set fly speed based on config (vanilla default is 0.05f)
            player.getAbilities().setFlySpeed(0.05f * ModConfig.flySpeed);

            // Prevent fall damage while flying by resetting fall distance
            if (player.getAbilities().flying) {
                player.fallDistance = 0.0f;
            }
        } else {
            // Only disable if we previously enabled it (don't touch spectator/creative)
            if (!player.isSpectator()) {
                player.getAbilities().allowFlying = false;
                player.getAbilities().flying = false;
            }
        }
    }
}
