package com.reachfly;

import net.minecraft.client.Minecraft;

/**
 * Fullbright - Sets gamma to maximum so the player can see in the dark.
 * Restores original gamma when disabled.
 * Also forces fullbright when X-Ray is active so ores are visible underground.
 */
public class FullbrightHandler {

    private static double originalGamma = -1;
    private static boolean wasEnabled = false;

    public static void tick(Minecraft minecraft) {
        if (minecraft.player == null) return;

        // Active if fullbright is on OR xray needs it
        boolean shouldBeActive = ModConfig.fullbrightEnabled || XrayHandler.isActive();

        if (shouldBeActive) {
            if (!wasEnabled) {
                originalGamma = minecraft.options.gamma().get();
                wasEnabled = true;
            }
            // Set gamma very high for fullbright
            minecraft.options.gamma().set(16.0);
        } else {
            if (wasEnabled) {
                // Restore original gamma
                minecraft.options.gamma().set(originalGamma >= 0 ? originalGamma : 1.0);
                wasEnabled = false;
            }
        }
    }
}
