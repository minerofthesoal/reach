package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class HudOverlay {

    private static final int BG = 0x90000000;
    private static final int BG_LIGHT = 0x40000000;

    // Category colors (Future/Rusher style)
    private static final int COL_COMBAT = 0xFFFF4444;
    private static final int COL_MOVEMENT = 0xFF44AAFF;
    private static final int COL_RENDER = 0xFFBB66FF;
    private static final int COL_PLAYER = 0xFF44FF88;
    private static final int COL_STEALTH = 0xFFFF8800;
    private static final int COL_WORLD = 0xFF22DD22;
    private static final int COL_EXPLOIT = 0xFFFF2222;
    private static final int COL_VISUAL = 0xFFFFDD00;
    private static final int COL_UTILITY = 0xFF44DDDD;
    private static final int COL_SOCIAL = 0xFFFF66CC;
    private static final int COL_BUILD = 0xFF66CCFF;
    private static final int COL_WHITE = 0xFFDDDDDD;
    private static final int COL_GRAY = 0xFF888888;

    public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (client.getDebugHud().shouldShowDebugHud()) return;
        if (!ModConfig.hudVisible) return;

        TextRenderer tr = client.textRenderer;
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();

        // === WATERMARK (top-left) ===
        renderWatermark(ctx, tr);

        // === MODULE ARRAY LIST (top-right, sorted by width) ===
        renderModuleList(ctx, tr, sw, client);

        // === INFO BAR (bottom-left) ===
        renderInfoBar(ctx, tr, sh, client);
    }

    private static void renderWatermark(DrawContext ctx, TextRenderer tr) {
        String brand;
        int brandColor;
        if (ModConfig.proUnlocked) {
            brand = "f1sch PRO";
            brandColor = 0xFFFFD700;
        } else {
            brand = "f1sch v2.2";
            brandColor = 0xFFBB66FF;
        }
        int bw = tr.getWidth(brand);
        ctx.fill(3, 3, 9 + bw, 15, BG);
        ctx.fill(3, 3, 5, 15, brandColor);
        ctx.drawText(tr, brand, 7, 5, brandColor, true);
    }

    private static void renderModuleList(DrawContext ctx, TextRenderer tr, int sw, MinecraftClient client) {
        List<ModEntry> entries = new ArrayList<>();

        // Combat
        if (ModConfig.reachEnabled) entries.add(new ModEntry(String.format("Reach \u00a7f%.1f", ModConfig.reachDistance), COL_COMBAT));
        if (ModConfig.autoHitEnabled) {
            String suffix = ModConfig.killAuraEnabled ? " \u00a7f[Aura]" : "";
            entries.add(new ModEntry(String.format("AutoHit \u00a7f%.1f%s", ModConfig.autoHitRange, suffix), COL_COMBAT));
        }
        if (ModConfig.lowHealthKillEnabled) entries.add(new ModEntry(String.format("LowHP Kill \u00a7f<%.0f", ModConfig.lowHealthThreshold), COL_COMBAT));
        if (ModConfig.autoKillWhenLowEnabled) entries.add(new ModEntry(String.format("AutoKill \u00a7f<%.0fhp", ModConfig.autoKillSelfHpThreshold), COL_COMBAT));
        if (ModConfig.knockbackEnabled) entries.add(new ModEntry(String.format("Knockback \u00a7f%.0f", ModConfig.knockbackStrength), COL_COMBAT));

        // Movement
        if (ModConfig.flyEnabled) entries.add(new ModEntry(String.format("Fly \u00a7f%.1fx", ModConfig.flySpeed), COL_MOVEMENT));
        if (ModConfig.speedEnabled) entries.add(new ModEntry(String.format("Speed \u00a7f%.1fx", ModConfig.speedMultiplier), COL_MOVEMENT));
        if (ModConfig.jesusEnabled) entries.add(new ModEntry("Jesus", COL_MOVEMENT));
        if (ModConfig.noFallEnabled) entries.add(new ModEntry("NoFall", COL_MOVEMENT));
        if (ModConfig.scaffoldEnabled) entries.add(new ModEntry("Scaffold", COL_MOVEMENT));
        if (ModConfig.betterSprintEnabled) entries.add(new ModEntry("BetterSprint", COL_MOVEMENT));
        if (ModConfig.safeWalkEnabled) entries.add(new ModEntry("SafeWalk", COL_MOVEMENT));
        if (ModConfig.stepEnabled) entries.add(new ModEntry(String.format("Step \u00a7f%.0f", ModConfig.stepHeight), COL_MOVEMENT));
        if (ModConfig.flyToCoordsEnabled && client.player != null) {
            Vec3d pos = client.player.getPos();
            double dist = pos.distanceTo(new Vec3d(ModConfig.flyToX, ModConfig.flyToY, ModConfig.flyToZ));
            entries.add(new ModEntry(String.format("FlyTo \u00a7f%.0fm", dist), COL_MOVEMENT));
        }
        if (ModConfig.walkToCoordsEnabled && client.player != null) {
            Vec3d pos = client.player.getPos();
            double dist = Math.sqrt((pos.x - ModConfig.walkToX) * (pos.x - ModConfig.walkToX) + (pos.z - ModConfig.walkToZ) * (pos.z - ModConfig.walkToZ));
            entries.add(new ModEntry(String.format("WalkTo \u00a7f%.0fm", dist), COL_MOVEMENT));
        }

        // Render
        if (ModConfig.espEnabled) entries.add(new ModEntry("ESP", COL_RENDER));
        if (ModConfig.xrayEnabled) entries.add(new ModEntry("X-Ray", COL_RENDER));
        if (ModConfig.fullbrightEnabled) entries.add(new ModEntry("Fullbright", COL_RENDER));

        // Player
        if (ModConfig.autoTotemEnabled) entries.add(new ModEntry("AutoTotem", COL_PLAYER));
        if (ModConfig.autoArmorEnabled) entries.add(new ModEntry("AutoArmor", COL_PLAYER));
        if (ModConfig.autoElytraSwapEnabled) entries.add(new ModEntry("Elytra", COL_PLAYER));
        if (ModConfig.eatingAssistEnabled) entries.add(new ModEntry("EatAssist", COL_PLAYER));
        if (ModConfig.autoLogEnabled) entries.add(new ModEntry(String.format("AutoLog \u00a7f<%.0fhp", ModConfig.autoLogHealth), COL_PLAYER));
        if (ModConfig.autoRespawnEnabled) entries.add(new ModEntry("AutoRespawn", COL_PLAYER));

        // Wurst modules
        if (ModConfig.killAuraPlusEnabled) entries.add(new ModEntry(String.format("KillAura+ \u00a7f%dCPS", ModConfig.killAuraPlusCps), COL_COMBAT));
        if (ModConfig.criticalsEnabled) entries.add(new ModEntry("Criticals", COL_COMBAT));
        if (ModConfig.triggerBotEnabled) entries.add(new ModEntry("TriggerBot", COL_COMBAT));
        if (ModConfig.autoSwordEnabled) entries.add(new ModEntry("AutoSword", COL_COMBAT));
        if (ModConfig.bunnyHopEnabled) entries.add(new ModEntry("BunnyHop", COL_MOVEMENT));
        if (ModConfig.spiderEnabled) entries.add(new ModEntry("Spider", COL_MOVEMENT));
        if (ModConfig.glideEnabled) entries.add(new ModEntry("Glide", COL_MOVEMENT));
        if (ModConfig.highJumpEnabled) entries.add(new ModEntry(String.format("HighJump \u00a7f%.1f", ModConfig.highJumpHeight), COL_MOVEMENT));
        if (ModConfig.dolphinEnabled) entries.add(new ModEntry("Dolphin", COL_MOVEMENT));
        if (ModConfig.sneakEnabled) entries.add(new ModEntry("Sneak", COL_STEALTH));
        if (ModConfig.antiHungerEnabled) entries.add(new ModEntry("AntiHunger", COL_STEALTH));
        if (ModConfig.invMoveEnabled) entries.add(new ModEntry("InvMove", COL_UTILITY));
        if (ModConfig.fastPlaceEnabled) entries.add(new ModEntry("FastPlace", COL_WORLD));
        if (ModConfig.parkourEnabled) entries.add(new ModEntry("Parkour", COL_MOVEMENT));
        if (ModConfig.noSlowdownEnabled) entries.add(new ModEntry("NoSlowdown", COL_MOVEMENT));
        if (ModConfig.antiBlindEnabled) entries.add(new ModEntry("AntiBlind", COL_RENDER));
        if (ModConfig.autoWalkEnabled) entries.add(new ModEntry("AutoWalk", COL_MOVEMENT));
        if (ModConfig.airJumpEnabled) entries.add(new ModEntry("AirJump", COL_MOVEMENT));
        if (ModConfig.noWebEnabled) entries.add(new ModEntry("NoWeb", COL_MOVEMENT));
        if (ModConfig.flightPlusEnabled) entries.add(new ModEntry(String.format("Flight+ \u00a7f%.1f", ModConfig.flightPlusSpeed), COL_MOVEMENT));
        if (ModConfig.longJumpEnabled) entries.add(new ModEntry(String.format("LongJump \u00a7f%.1f", ModConfig.longJumpBoost), COL_MOVEMENT));
        if (ModConfig.autoMLGEnabled) entries.add(new ModEntry("AutoMLG", COL_PLAYER));
        if (ModConfig.blinkEnabled) entries.add(new ModEntry("Blink", COL_EXPLOIT));

        // Meteor v2 modules
        if (ModConfig.elytraFlyEnabled) entries.add(new ModEntry(String.format("ElytraFly \u00a7f%.1f", ModConfig.elytraFlySpeed), COL_MOVEMENT));
        if (ModConfig.surroundEnabled) entries.add(new ModEntry("Surround", COL_COMBAT));
        if (ModConfig.crystalAuraEnabled) entries.add(new ModEntry("CrystalAura", COL_COMBAT));
        if (ModConfig.holeEspEnabled) entries.add(new ModEntry("HoleESP", COL_RENDER));
        if (ModConfig.anchorAuraEnabled) entries.add(new ModEntry("AnchorAura", COL_COMBAT));
        if (ModConfig.holeFillerEnabled) entries.add(new ModEntry("HoleFiller", COL_COMBAT));
        if (ModConfig.autoTrapEnabled) entries.add(new ModEntry("AutoTrap", COL_COMBAT));
        if (ModConfig.reversalEnabled) entries.add(new ModEntry("Reversal", COL_COMBAT));

        // Pro modules
        if (ModConfig.proUnlocked) {
            if (ModConfig.antiKnockbackEnabled) entries.add(new ModEntry(String.format("AntiKB \u00a7f%.0f%%", ModConfig.antiKnockbackStrength), COL_STEALTH));
            if (ModConfig.noSwingEnabled) entries.add(new ModEntry("NoSwing", COL_STEALTH));
            if (ModConfig.antiAfkEnabled) entries.add(new ModEntry("AntiAFK", COL_STEALTH));
            if (ModConfig.fastBreakEnabled) entries.add(new ModEntry(String.format("FastBreak \u00a7f%.1fx", ModConfig.fastBreakSpeed), COL_WORLD));
            if (ModConfig.nukerEnabled) entries.add(new ModEntry(String.format("Nuker \u00a7f%.0f", ModConfig.nukerRadius), COL_WORLD));
            if (ModConfig.autoFarmEnabled) entries.add(new ModEntry("AutoFarm", COL_WORLD));
            if (ModConfig.phaseEnabled) entries.add(new ModEntry("Phase", COL_EXPLOIT));
            if (ModConfig.freecamEnabled) entries.add(new ModEntry("Freecam", COL_EXPLOIT));
            if (ModConfig.timerEnabled) entries.add(new ModEntry(String.format("Timer \u00a7f%.1fx", ModConfig.timerSpeed), COL_EXPLOIT));
            if (ModConfig.chestEspEnabled) entries.add(new ModEntry("ChestESP", COL_VISUAL));
            if (ModConfig.trajectoriesEnabled) entries.add(new ModEntry("Trajectories", COL_VISUAL));
            if (ModConfig.nametagsEnabled) entries.add(new ModEntry("Nametags", COL_VISUAL));
            if (ModConfig.autoFishEnabled) entries.add(new ModEntry("AutoFish", COL_UTILITY));
            if (ModConfig.chestStealerEnabled) entries.add(new ModEntry("ChestStealer", COL_UTILITY));
            if (ModConfig.autoToolEnabled) entries.add(new ModEntry("AutoTool", COL_UTILITY));
            if (ModConfig.invSortEnabled) entries.add(new ModEntry("InvSort", COL_UTILITY));
            if (ModConfig.chatSpamEnabled) entries.add(new ModEntry("ChatSpam", COL_SOCIAL));
            if (ModConfig.autoReplyEnabled) entries.add(new ModEntry("AutoReply", COL_SOCIAL));
            if (ModConfig.announcerEnabled) entries.add(new ModEntry("Announcer", COL_SOCIAL));
            if (ModConfig.autoBridgeEnabled) entries.add(new ModEntry("AutoBridge", COL_BUILD));
            if (ModConfig.towerEnabled) entries.add(new ModEntry("Tower", COL_BUILD));
            if (ModConfig.printerEnabled) entries.add(new ModEntry("Printer", COL_BUILD));
            if (ModConfig.opSelfEnabled) entries.add(new ModEntry("OP", COL_STEALTH));
        }

        // Sort by rendered width (longest first, like Future client)
        entries.sort((a, b) -> {
            int wa = tr.getWidth(a.text.replaceAll("\u00a7.", ""));
            int wb = tr.getWidth(b.text.replaceAll("\u00a7.", ""));
            return wb - wa;
        });

        // Render right-aligned with accent bar
        int y = 2;
        for (ModEntry e : entries) {
            String clean = e.text.replaceAll("\u00a7.", "");
            int tw = tr.getWidth(clean);
            int x = sw - tw - 6;

            // Background with slight gradient feel
            ctx.fill(x - 4, y, sw, y + 11, BG);

            // Right accent bar (2px wide, module color)
            ctx.fill(sw - 2, y, sw, y + 11, e.color);

            // Top highlight line (subtle)
            ctx.fill(x - 4, y, sw - 2, y + 1, (e.color & 0x00FFFFFF) | 0x30000000);

            // Text
            ctx.drawText(tr, e.text, x - 2, y + 1, e.color, true);

            y += 11;
        }
    }

    private static void renderInfoBar(DrawContext ctx, TextRenderer tr, int sh, MinecraftClient client) {
        if (client.player == null) return;
        Vec3d pos = client.player.getPos();

        // Coords
        String coords = String.format("XYZ: %.1f / %.1f / %.1f", pos.x, pos.y, pos.z);
        int cw = tr.getWidth(coords);
        int cy = sh - 13;
        ctx.fill(3, cy - 2, 9 + cw, cy + 10, BG);
        ctx.fill(3, cy - 2, 5, cy + 10, 0xFF44AAFF);
        ctx.drawText(tr, coords, 7, cy, COL_WHITE, true);

        // FPS + direction
        String facing = getDirection(client.player.getYaw());
        int fps = client.getCurrentFps();
        String info = String.format("%d FPS | %s", fps, facing);
        int iw = tr.getWidth(info);
        int iy = cy - 14;
        ctx.fill(3, iy - 2, 9 + iw, iy + 10, BG);
        ctx.fill(3, iy - 2, 5, iy + 10, 0xFF888888);
        ctx.drawText(tr, info, 7, iy, COL_GRAY, true);
    }

    private static String getDirection(float yaw) {
        float y = ((yaw % 360) + 360) % 360;
        if (y >= 337.5 || y < 22.5) return "S";
        if (y < 67.5) return "SW";
        if (y < 112.5) return "W";
        if (y < 157.5) return "NW";
        if (y < 202.5) return "N";
        if (y < 247.5) return "NE";
        if (y < 292.5) return "E";
        return "SE";
    }

    private record ModEntry(String text, int color) {}
}
