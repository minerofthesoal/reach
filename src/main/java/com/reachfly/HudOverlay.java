package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudOverlay {

    private static final int GREEN = 0xFF55FF55;
    private static final int RED = 0xFFFF5555;
    private static final int YELLOW = 0xFFFFFF55;
    private static final int BG = 0x80000000;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (client.getDebugHud().shouldShowDebugHud()) return;
        if (!ModConfig.hudVisible) return;

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

        drawLine(context, tr, x, y, "ESP: " + (ModConfig.espEnabled ? "ON" : "OFF"), ModConfig.espEnabled);
        y += lh;

        String autoHitText;
        if (ModConfig.autoHitEnabled) {
            String mode = ModConfig.killAuraEnabled ? " [AURA]" : "";
            autoHitText = String.format("AutoHit: ON (%.1f)%s", ModConfig.autoHitRange, mode);
        } else {
            autoHitText = "AutoHit: OFF";
        }
        drawLine(context, tr, x, y, autoHitText, ModConfig.autoHitEnabled);
        y += lh;

        drawLine(context, tr, x, y,
                ModConfig.lowHealthKillEnabled ? String.format("LowHP Kill: ON (<%.0f)", ModConfig.lowHealthThreshold) : "LowHP Kill: OFF",
                ModConfig.lowHealthKillEnabled);
        y += lh;

        drawLine(context, tr, x, y,
                ModConfig.autoKillWhenLowEnabled ? String.format("AutoKill(LowHP): ON (<%.0f)", ModConfig.autoKillSelfHpThreshold) : "AutoKill(LowHP): OFF",
                ModConfig.autoKillWhenLowEnabled);
        y += lh;

        drawLine(context, tr, x, y, "Jesus: " + (ModConfig.jesusEnabled ? "ON" : "OFF"), ModConfig.jesusEnabled);
        y += lh;

        drawLine(context, tr, x, y, "NoFall: " + (ModConfig.noFallEnabled ? "ON" : "OFF"), ModConfig.noFallEnabled);
        y += lh;

        drawLine(context, tr, x, y,
                ModConfig.speedEnabled ? String.format("Speed: ON (%.1fx)", ModConfig.speedMultiplier) : "Speed: OFF",
                ModConfig.speedEnabled);
        y += lh;

        drawLine(context, tr, x, y, "Fullbright: " + (ModConfig.fullbrightEnabled ? "ON" : "OFF"), ModConfig.fullbrightEnabled);
        y += lh;

        drawLine(context, tr, x, y, "Elytra Swap: " + (ModConfig.autoElytraSwapEnabled ? "ON" : "OFF"), ModConfig.autoElytraSwapEnabled);
        y += lh;

        if (ModConfig.flyToCoordsEnabled) {
            drawLine(context, tr, x, y,
                    String.format("FlyTo: %.0f,%.0f,%.0f", ModConfig.flyToX, ModConfig.flyToY, ModConfig.flyToZ),
                    true, YELLOW);
        } else {
            drawLine(context, tr, x, y, "FlyTo: OFF", false);
        }
        y += lh;

        if (ModConfig.walkToCoordsEnabled) {
            drawLine(context, tr, x, y,
                    String.format("WalkTo: %.0f,%.0f,%.0f", ModConfig.walkToX, ModConfig.walkToY, ModConfig.walkToZ),
                    true, YELLOW);
        } else {
            drawLine(context, tr, x, y, "WalkTo: OFF", false);
        }
        y += lh;

        drawLine(context, tr, x, y, "Eat Assist: " + (ModConfig.eatingAssistEnabled ? "ON" : "OFF"), ModConfig.eatingAssistEnabled);
    }

    private static void drawLine(DrawContext ctx, TextRenderer tr, int x, int y, String text, boolean enabled) {
        drawLine(ctx, tr, x, y, text, enabled, enabled ? GREEN : RED);
    }

    private static void drawLine(DrawContext ctx, TextRenderer tr, int x, int y, String text, boolean enabled, int color) {
        int w = tr.getWidth(text);
        ctx.fill(x - 2, y - 2, x + w + 2, y + 10, BG);
        ctx.drawText(tr, text, x, y, color, true);
    }
}
