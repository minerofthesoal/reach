package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.Vec3d;

public class HudOverlay {

    private static final int GREEN = 0xFF55FF55;
    private static final int RED = 0xFFFF5555;
    private static final int YELLOW = 0xFFFFFF55;
    private static final int CYAN = 0xFF55FFFF;
    private static final int PURPLE = 0xFFAA55FF;
    private static final int GOLD = 0xFFFFD700;
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

        if (ModConfig.proUnlocked) {
            drawLine(context, tr, x, y, "\u00a7d\u00a7lOSP \u00a76\u00a7lPRO", true, GOLD);
        } else {
            drawLine(context, tr, x, y, "\u00a7d\u00a7lOSP \u00a78v2.1", true, PURPLE);
        }
        y += lh;

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

        String autoHitLabel = ModConfig.autoHitEnabled
                ? String.format("AutoHit: ON (%.1f)%s", ModConfig.autoHitRange, ModConfig.killAuraEnabled ? " [AURA]" : "")
                : "AutoHit: OFF";
        drawLine(context, tr, x, y, autoHitLabel, ModConfig.autoHitEnabled);
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

        drawLine(context, tr, x, y,
                ModConfig.knockbackEnabled ? String.format("Knockback: ON (%.0f)", ModConfig.knockbackStrength) : "Knockback: OFF",
                ModConfig.knockbackEnabled);
        y += lh;

        drawLine(context, tr, x, y, "X-Ray: " + (ModConfig.xrayEnabled ? "ON" : "OFF"), ModConfig.xrayEnabled);
        y += lh;

        drawLine(context, tr, x, y, "Scaffold: " + (ModConfig.scaffoldEnabled ? "ON" : "OFF"), ModConfig.scaffoldEnabled);
        y += lh;

        drawLine(context, tr, x, y, "Auto Totem: " + (ModConfig.autoTotemEnabled ? "ON" : "OFF"), ModConfig.autoTotemEnabled);
        y += lh;

        drawLine(context, tr, x, y, "Auto Armor: " + (ModConfig.autoArmorEnabled ? "ON" : "OFF"), ModConfig.autoArmorEnabled);
        y += lh;

        drawLine(context, tr, x, y, "Fullbright: " + (ModConfig.fullbrightEnabled ? "ON" : "OFF"), ModConfig.fullbrightEnabled);
        y += lh;

        drawLine(context, tr, x, y, "Elytra Swap: " + (ModConfig.autoElytraSwapEnabled ? "ON" : "OFF"), ModConfig.autoElytraSwapEnabled);
        y += lh;

        if (ModConfig.flyToCoordsEnabled && client.player != null) {
            Vec3d pos = client.player.getEntityPos();
            double dist = pos.distanceTo(new Vec3d(ModConfig.flyToX, ModConfig.flyToY, ModConfig.flyToZ));
            drawLine(context, tr, x, y,
                    String.format("FlyTo: %.0f,%.0f,%.0f (%.0f blks)", ModConfig.flyToX, ModConfig.flyToY, ModConfig.flyToZ, dist),
                    true, YELLOW);
        } else {
            drawLine(context, tr, x, y, "FlyTo: OFF", false);
        }
        y += lh;

        if (ModConfig.walkToCoordsEnabled && client.player != null) {
            Vec3d pos = client.player.getEntityPos();
            double dist = Math.sqrt(
                    (pos.x - ModConfig.walkToX) * (pos.x - ModConfig.walkToX) +
                    (pos.z - ModConfig.walkToZ) * (pos.z - ModConfig.walkToZ));
            drawLine(context, tr, x, y,
                    String.format("WalkTo: %.0f,%.0f,%.0f (%.0f blks)", ModConfig.walkToX, ModConfig.walkToY, ModConfig.walkToZ, dist),
                    true, YELLOW);
        } else {
            drawLine(context, tr, x, y, "WalkTo: OFF", false);
        }
        y += lh;

        String tpMode = ModConfig.tpUseServerAddon ? "Addon" : "Beta";
        drawLine(context, tr, x, y,
                String.format("TP [%s]: %.0f, %.0f, %.0f (T)", tpMode, ModConfig.tpX, ModConfig.tpY, ModConfig.tpZ),
                true, CYAN);
        y += lh;

        drawLine(context, tr, x, y, "Eat Assist: " + (ModConfig.eatingAssistEnabled ? "ON" : "OFF"), ModConfig.eatingAssistEnabled);
        y += lh;

        // ===== PRO HUD (only show enabled pro modules) =====
        if (ModConfig.proUnlocked) {
            boolean anyPro = false;

            if (ModConfig.antiKnockbackEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, String.format("Anti KB: ON (%.0f%%)", ModConfig.antiKnockbackStrength), true, GOLD);
                y += lh;
            }
            if (ModConfig.noSwingEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, "No Swing: ON", true, GOLD);
                y += lh;
            }
            if (ModConfig.antiAfkEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, "Anti AFK: ON", true, GOLD);
                y += lh;
            }
            if (ModConfig.fastBreakEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, String.format("FastBreak: ON (%.1fx)", ModConfig.fastBreakSpeed), true, GOLD);
                y += lh;
            }
            if (ModConfig.nukerEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, String.format("Nuker: ON (%.0f)", ModConfig.nukerRadius), true, GOLD);
                y += lh;
            }
            if (ModConfig.autoFarmEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, "AutoFarm: ON", true, GOLD);
                y += lh;
            }
            if (ModConfig.phaseEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, "Phase: ON", true, GOLD);
                y += lh;
            }
            if (ModConfig.freecamEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, "Freecam: ON", true, GOLD);
                y += lh;
            }
            if (ModConfig.timerEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, String.format("Timer: ON (%.1fx)", ModConfig.timerSpeed), true, GOLD);
                y += lh;
            }
            if (ModConfig.chestEspEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, "ChestESP: ON", true, GOLD);
                y += lh;
            }
            if (ModConfig.trajectoriesEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, "Trajectories: ON", true, GOLD);
                y += lh;
            }
            if (ModConfig.nametagsEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, "Nametags: ON", true, GOLD);
                y += lh;
            }
            if (ModConfig.autoFishEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, "AutoFish: ON", true, GOLD);
                y += lh;
            }
            if (ModConfig.chestStealerEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, "ChestStealer: ON", true, GOLD);
                y += lh;
            }
            if (ModConfig.autoToolEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, "AutoTool: ON", true, GOLD);
                y += lh;
            }
            if (ModConfig.chatSpamEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, "ChatSpam: ON", true, GOLD);
                y += lh;
            }
            if (ModConfig.autoBridgeEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, "AutoBridge: ON", true, GOLD);
                y += lh;
            }
            if (ModConfig.towerEnabled) {
                if (!anyPro) { drawLine(context, tr, x, y, "\u00a76--- PRO ---", true, GOLD); y += lh; anyPro = true; }
                drawLine(context, tr, x, y, "Tower: ON", true, GOLD);
                y += lh;
            }
        }
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
