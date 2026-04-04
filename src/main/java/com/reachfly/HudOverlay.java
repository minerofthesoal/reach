package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

/**
 * Renders a HUD overlay showing the current status of Reach and Fly features.
 * Displayed in the top-left corner of the screen.
 */
public class HudOverlay {

    /**
     * Render callback for the HUD overlay.
     */
    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (client.getDebugHud().shouldShowDebugHud()) return; // Hide when F3 debug screen is open

        TextRenderer textRenderer = client.textRenderer;
        int x = 6;
        int y = 6;
        int lineHeight = 12;

        // --- Reach Status ---
        String reachStatus;
        int reachColor;
        if (ModConfig.reachEnabled) {
            reachStatus = String.format("Reach: ON (%.1f)", ModConfig.reachDistance);
            reachColor = 0xFF55FF55; // Green
        } else {
            reachStatus = "Reach: OFF";
            reachColor = 0xFFFF5555; // Red
        }

        // Draw background for readability
        int reachWidth = textRenderer.getWidth(reachStatus);
        context.fill(x - 2, y - 2, x + reachWidth + 2, y + 10, 0x80000000);
        context.drawText(textRenderer, reachStatus, x, y, reachColor, true);

        y += lineHeight;

        // --- Fly Status ---
        String flyStatus;
        int flyColor;
        if (ModConfig.flyEnabled) {
            flyStatus = String.format("Fly: ON (%.1fx)", ModConfig.flySpeed);
            flyColor = 0xFF55FF55; // Green
        } else {
            flyStatus = "Fly: OFF";
            flyColor = 0xFFFF5555; // Red
        }

        int flyWidth = textRenderer.getWidth(flyStatus);
        context.fill(x - 2, y - 2, x + flyWidth + 2, y + 10, 0x80000000);
        context.drawText(textRenderer, flyStatus, x, y, flyColor, true);
    }
}
