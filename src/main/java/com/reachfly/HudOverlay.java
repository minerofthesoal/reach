package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

/**
 * Renders HUD overlay showing the status of all mod features.
 */
public class HudOverlay {

    private static final int GREEN = 0xFF55FF55;
    private static final int RED = 0xFFFF5555;
    private static final int BG = 0x80000000;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (client.getDebugHud().shouldShowDebugHud()) return;

        TextRenderer tr = client.textRenderer;
        int x = 6;
        int y = 6;
        int lh = 12;

        drawLine(context, tr, x, y,
                ModConfig.reachEnabled ? String.format("Reach: ON (%.1f)", ModConfig.reachDistance) : "Reach: OFF",
                ModConfig.reachEnabled);
        y += lh;

        drawLine(context, tr, x, y,
                ModConfig.flyEnabled ? String.format("Fly: ON (%.1fx)", ModConfig.flySpeed) : "Fly: OFF",
                ModConfig.flyEnabled);
        y += lh;

        drawLine(context, tr, x, y,
                "ESP: " + (ModConfig.espEnabled ? "ON" : "OFF"),
                ModConfig.espEnabled);
        y += lh;

        drawLine(context, tr, x, y,
                ModConfig.autoHitEnabled ? String.format("AutoHit: ON (%.1f)", ModConfig.autoHitRange) : "AutoHit: OFF",
                ModConfig.autoHitEnabled);
        y += lh;

        drawLine(context, tr, x, y,
                ModConfig.lowHealthKillEnabled ? String.format("LowHP Kill: ON (<%.0f)", ModConfig.lowHealthThreshold) : "LowHP Kill: OFF",
                ModConfig.lowHealthKillEnabled);
        y += lh;

        drawLine(context, tr, x, y,
                ModConfig.autoKillWhenLowEnabled ? String.format("AutoKill(LowHP): ON (<%.0f)", ModConfig.autoKillSelfHpThreshold) : "AutoKill(LowHP): OFF",
                ModConfig.autoKillWhenLowEnabled);
        y += lh;

        drawLine(context, tr, x, y,
                "Eat Assist: " + (ModConfig.eatingAssistEnabled ? "ON" : "OFF"),
                ModConfig.eatingAssistEnabled);
        y += lh;

        drawLine(context, tr, x, y,
                "Shield: " + (ModConfig.shieldAssistEnabled ? "ON" : "OFF"),
                ModConfig.shieldAssistEnabled);
        y += lh;

        drawLine(context, tr, x, y,
                "Dupe: " + (ModConfig.dupeEnabled ? "ON" : "OFF"),
                ModConfig.dupeEnabled);
    }

    private static void drawLine(DrawContext ctx, TextRenderer tr, int x, int y, String text, boolean enabled) {
        int w = tr.getWidth(text);
        ctx.fill(x - 2, y - 2, x + w + 2, y + 10, BG);
        ctx.drawText(tr, text, x, y, enabled ? GREEN : RED, true);
    }
}
