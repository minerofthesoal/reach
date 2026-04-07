package com.reachfly;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import net.minecraft.client.gui.Click;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigScreen extends Screen {

    private final Screen parent;
    private String activeCategory = "Combat";
    private double scrollOffset = 0;
    private String expandedModule = null;

    private TextFieldWidget editField = null;
    private String editLabel = null;
    private java.util.function.Consumer<Float> editSetter = null;
    private float editMin, editMax;

    // Code entry
    private TextFieldWidget codeField = null;
    private boolean showCodeEntry = false;
    private String codeMessage = null;
    private int codeMsgTimer = 0;

    private static final int BG = 0xF0101020;
    private static final int PANEL_BG = 0xF0181828;
    private static final int ACCENT = 0xFF8B5CF6;
    private static final int ACCENT_DIM = 0xFF5B3CB6;
    private static final int MODULE_BG = 0xFF1E1E36;
    private static final int MODULE_HOVER = 0xFF282848;
    private static final int MODULE_ON = 0xFF2A1F4E;
    private static final int SETTING_BG = 0xFF151528;
    private static final int TEXT_PRIMARY = 0xFFE0E0E0;
    private static final int TEXT_DIM = 0xFF888898;
    private static final int GREEN = 0xFF4ADE80;
    private static final int RED = 0xFFEF4444;
    private static final int GOLD = 0xFFFFD700;
    private static final int PRO_ACCENT = 0xFFFF6B6B;

    private static final int TAB_H = 28;
    private static final int MODULE_H = 22;
    private static final int SETTING_H = 18;

    private final Map<String, List<Module>> categories = new LinkedHashMap<>();

    public ConfigScreen(Screen parent) {
        super(Text.literal("OSP"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        clearChildren();
        editField = null;
        codeField = null;
        showCodeEntry = false;
        categories.clear();
        scrollOffset = 0;

        // === COMBAT ===
        List<Module> combat = new ArrayList<>();
        combat.add(new Module("Auto Hit", () -> ModConfig.autoHitEnabled, v -> ModConfig.autoHitEnabled = v)
                .addNumber("Range", () -> ModConfig.autoHitRange, ModConfig.AUTO_HIT_RANGE_MIN, ModConfig.AUTO_HIT_RANGE_MAX, v -> ModConfig.autoHitRange = v)
                .addToggle("Players Only", () -> ModConfig.autoHitPlayersOnly, v -> ModConfig.autoHitPlayersOnly = v)
                .addToggle("Kill Aura", () -> ModConfig.killAuraEnabled, v -> ModConfig.killAuraEnabled = v));
        combat.add(new Module("Low Health Kill", () -> ModConfig.lowHealthKillEnabled, v -> ModConfig.lowHealthKillEnabled = v)
                .addNumber("HP Threshold", () -> ModConfig.lowHealthThreshold, ModConfig.LOW_HEALTH_MIN, ModConfig.LOW_HEALTH_MAX, v -> ModConfig.lowHealthThreshold = v));
        combat.add(new Module("Auto Kill (Low HP)", () -> ModConfig.autoKillWhenLowEnabled, v -> ModConfig.autoKillWhenLowEnabled = v)
                .addNumber("Self HP Threshold", () -> ModConfig.autoKillSelfHpThreshold, ModConfig.AUTO_KILL_SELF_HP_MIN, ModConfig.AUTO_KILL_SELF_HP_MAX, v -> ModConfig.autoKillSelfHpThreshold = v)
                .addNumber("Kill Range", () -> ModConfig.autoKillWhenLowRange, ModConfig.AUTO_KILL_RANGE_MIN, ModConfig.AUTO_KILL_RANGE_MAX, v -> ModConfig.autoKillWhenLowRange = v));
        combat.add(new Module("Reach", () -> ModConfig.reachEnabled, v -> ModConfig.reachEnabled = v)
                .addNumber("Distance", () -> ModConfig.reachDistance, ModConfig.REACH_MIN, ModConfig.REACH_MAX, v -> ModConfig.reachDistance = v));
        combat.add(new Module("Knockback", () -> ModConfig.knockbackEnabled, v -> ModConfig.knockbackEnabled = v)
                .addNumber("Strength", () -> ModConfig.knockbackStrength, ModConfig.KNOCKBACK_MIN, ModConfig.KNOCKBACK_MAX, v -> ModConfig.knockbackStrength = v));
        categories.put("Combat", combat);

        // === MOVEMENT ===
        List<Module> movement = new ArrayList<>();
        movement.add(new Module("Fly", () -> ModConfig.flyEnabled, v -> ModConfig.flyEnabled = v)
                .addNumber("Speed", () -> ModConfig.flySpeed, ModConfig.FLY_SPEED_MIN, ModConfig.FLY_SPEED_MAX, v -> ModConfig.flySpeed = v));
        movement.add(new Module("Speed", () -> ModConfig.speedEnabled, v -> ModConfig.speedEnabled = v)
                .addNumber("Multiplier", () -> ModConfig.speedMultiplier, ModConfig.SPEED_MIN, ModConfig.SPEED_MAX, v -> ModConfig.speedMultiplier = v));
        movement.add(new Module("Jesus", () -> ModConfig.jesusEnabled, v -> ModConfig.jesusEnabled = v));
        movement.add(new Module("NoFall", () -> ModConfig.noFallEnabled, v -> ModConfig.noFallEnabled = v));
        movement.add(new Module("Fly to Coords", () -> ModConfig.flyToCoordsEnabled, v -> { ModConfig.flyToCoordsEnabled = v; if (!v) FlyToCoordsHandler.onDisable(); })
                .addNumber("X", () -> ModConfig.flyToX, -30000000, 30000000, v -> ModConfig.flyToX = v)
                .addNumber("Y", () -> ModConfig.flyToY, -64, 320, v -> ModConfig.flyToY = v)
                .addNumber("Z", () -> ModConfig.flyToZ, -30000000, 30000000, v -> ModConfig.flyToZ = v)
                .addNumber("Speed", () -> ModConfig.flyToCoordsSpeed, ModConfig.FLY_TO_SPEED_MIN, ModConfig.FLY_TO_SPEED_MAX, v -> ModConfig.flyToCoordsSpeed = v));
        movement.add(new Module("Walk to Coords", () -> ModConfig.walkToCoordsEnabled, v -> { ModConfig.walkToCoordsEnabled = v; if (!v) WalkToCoordsHandler.onDisable(); })
                .addNumber("X", () -> ModConfig.walkToX, -30000000, 30000000, v -> ModConfig.walkToX = v)
                .addNumber("Y", () -> ModConfig.walkToY, -64, 320, v -> ModConfig.walkToY = v)
                .addNumber("Z", () -> ModConfig.walkToZ, -30000000, 30000000, v -> ModConfig.walkToZ = v));
        movement.add(new Module("Scaffold", () -> ModConfig.scaffoldEnabled, v -> ModConfig.scaffoldEnabled = v));
        movement.add(new Module("Better Sprint", () -> ModConfig.betterSprintEnabled, v -> ModConfig.betterSprintEnabled = v));
        movement.add(new Module("SafeWalk", () -> ModConfig.safeWalkEnabled, v -> ModConfig.safeWalkEnabled = v));
        movement.add(new Module("Step", () -> ModConfig.stepEnabled, v -> ModConfig.stepEnabled = v)
                .addNumber("Height", () -> ModConfig.stepHeight, ModConfig.STEP_MIN, ModConfig.STEP_MAX, v -> ModConfig.stepHeight = v));
        categories.put("Movement", movement);

        // === RENDER ===
        List<Module> render = new ArrayList<>();
        render.add(new Module("ESP", () -> ModConfig.espEnabled, v -> ModConfig.espEnabled = v)
                .addToggle("Players", () -> ModConfig.espPlayers, v -> ModConfig.espPlayers = v)
                .addToggle("Hostile", () -> ModConfig.espHostile, v -> ModConfig.espHostile = v)
                .addToggle("Passive", () -> ModConfig.espPassive, v -> ModConfig.espPassive = v)
                .addToggle("Tracer Lines", () -> ModConfig.espLines, v -> ModConfig.espLines = v)
                .addToggle("Path Trace", () -> ModConfig.espPathTrace, v -> ModConfig.espPathTrace = v));
        render.add(new Module("X-Ray", () -> ModConfig.xrayEnabled, v -> ModConfig.xrayEnabled = v));
        render.add(new Module("Fullbright", () -> ModConfig.fullbrightEnabled, v -> ModConfig.fullbrightEnabled = v));
        render.add(new Module("HUD", () -> ModConfig.hudVisible, v -> ModConfig.hudVisible = v));
        categories.put("Render", render);

        // === PLAYER ===
        List<Module> player = new ArrayList<>();
        player.add(new Module("Auto Totem", () -> ModConfig.autoTotemEnabled, v -> ModConfig.autoTotemEnabled = v));
        player.add(new Module("Auto Armor", () -> ModConfig.autoArmorEnabled, v -> ModConfig.autoArmorEnabled = v));
        player.add(new Module("Auto Elytra", () -> ModConfig.autoElytraSwapEnabled, v -> ModConfig.autoElytraSwapEnabled = v));
        player.add(new Module("Eating Assist", () -> ModConfig.eatingAssistEnabled, v -> ModConfig.eatingAssistEnabled = v)
                .addNumber("Hunger Threshold", () -> (float) ModConfig.eatingHungerThreshold, ModConfig.EATING_HUNGER_MIN, ModConfig.EATING_HUNGER_MAX, v -> ModConfig.eatingHungerThreshold = Math.round(v)));
        player.add(new Module("Teleport", () -> ModConfig.tpUseServerAddon, v -> ModConfig.tpUseServerAddon = v)
                .addNumber("X", () -> ModConfig.tpX, -30000000, 30000000, v -> ModConfig.tpX = v)
                .addNumber("Y", () -> ModConfig.tpY, -64, 320, v -> ModConfig.tpY = v)
                .addNumber("Z", () -> ModConfig.tpZ, -30000000, 30000000, v -> ModConfig.tpZ = v));
        player.add(new Module("Auto Log", () -> ModConfig.autoLogEnabled, v -> ModConfig.autoLogEnabled = v)
                .addNumber("HP Threshold", () -> ModConfig.autoLogHealth, ModConfig.AUTO_LOG_HP_MIN, ModConfig.AUTO_LOG_HP_MAX, v -> ModConfig.autoLogHealth = v));
        player.add(new Module("Auto Respawn", () -> ModConfig.autoRespawnEnabled, v -> ModConfig.autoRespawnEnabled = v));
        categories.put("Player", player);

        // ===== PRO CATEGORIES (only when unlocked) =====
        if (ModConfig.proUnlocked) {
            // === PRO: STEALTH ===
            List<Module> stealth = new ArrayList<>();
            stealth.add(new Module("Anti Knockback", () -> ModConfig.antiKnockbackEnabled, v -> ModConfig.antiKnockbackEnabled = v)
                    .addNumber("Strength %", () -> ModConfig.antiKnockbackStrength, ModConfig.ANTI_KB_MIN, ModConfig.ANTI_KB_MAX, v -> ModConfig.antiKnockbackStrength = v));
            stealth.add(new Module("No Swing", () -> ModConfig.noSwingEnabled, v -> ModConfig.noSwingEnabled = v));
            stealth.add(new Module("Anti AFK", () -> ModConfig.antiAfkEnabled, v -> ModConfig.antiAfkEnabled = v)
                    .addNumber("Interval (ticks)", () -> (float) ModConfig.antiAfkInterval, ModConfig.ANTI_AFK_MIN, ModConfig.ANTI_AFK_MAX, v -> ModConfig.antiAfkInterval = Math.round(v)));
            categories.put("\u00a76Stealth", stealth);

            // === PRO: WORLD ===
            List<Module> world = new ArrayList<>();
            world.add(new Module("Fast Break", () -> ModConfig.fastBreakEnabled, v -> ModConfig.fastBreakEnabled = v)
                    .addNumber("Speed Multi", () -> ModConfig.fastBreakSpeed, ModConfig.FAST_BREAK_MIN, ModConfig.FAST_BREAK_MAX, v -> ModConfig.fastBreakSpeed = v));
            world.add(new Module("Nuker", () -> ModConfig.nukerEnabled, v -> ModConfig.nukerEnabled = v)
                    .addNumber("Radius", () -> ModConfig.nukerRadius, ModConfig.NUKER_MIN, ModConfig.NUKER_MAX, v -> ModConfig.nukerRadius = v));
            world.add(new Module("Auto Farm", () -> ModConfig.autoFarmEnabled, v -> ModConfig.autoFarmEnabled = v));
            categories.put("\u00a72World", world);

            // === PRO: EXPLOIT ===
            List<Module> exploit = new ArrayList<>();
            exploit.add(new Module("Phase", () -> ModConfig.phaseEnabled, v -> ModConfig.phaseEnabled = v));
            exploit.add(new Module("Freecam", () -> ModConfig.freecamEnabled, v -> ModConfig.freecamEnabled = v));
            exploit.add(new Module("Timer", () -> ModConfig.timerEnabled, v -> ModConfig.timerEnabled = v)
                    .addNumber("Speed", () -> ModConfig.timerSpeed, ModConfig.TIMER_MIN, ModConfig.TIMER_MAX, v -> ModConfig.timerSpeed = v));
            categories.put("\u00a74Exploit", exploit);

            // === PRO: VISUAL ===
            List<Module> visual = new ArrayList<>();
            visual.add(new Module("Chest ESP", () -> ModConfig.chestEspEnabled, v -> ModConfig.chestEspEnabled = v));
            visual.add(new Module("Trajectories", () -> ModConfig.trajectoriesEnabled, v -> ModConfig.trajectoriesEnabled = v));
            visual.add(new Module("Nametags", () -> ModConfig.nametagsEnabled, v -> ModConfig.nametagsEnabled = v));
            categories.put("\u00a7eVisual+", visual);

            // === PRO: UTILITY ===
            List<Module> utility = new ArrayList<>();
            utility.add(new Module("Auto Fish", () -> ModConfig.autoFishEnabled, v -> ModConfig.autoFishEnabled = v));
            utility.add(new Module("Chest Stealer", () -> ModConfig.chestStealerEnabled, v -> ModConfig.chestStealerEnabled = v)
                    .addNumber("Delay (ticks)", () -> (float) ModConfig.chestStealerDelay, ModConfig.CHEST_STEALER_MIN, ModConfig.CHEST_STEALER_MAX, v -> ModConfig.chestStealerDelay = Math.round(v)));
            utility.add(new Module("Auto Tool", () -> ModConfig.autoToolEnabled, v -> ModConfig.autoToolEnabled = v));
            utility.add(new Module("Inv Sort", () -> ModConfig.invSortEnabled, v -> ModConfig.invSortEnabled = v));
            categories.put("\u00a73Utility", utility);

            // === PRO: SOCIAL ===
            List<Module> social = new ArrayList<>();
            social.add(new Module("Chat Spam", () -> ModConfig.chatSpamEnabled, v -> ModConfig.chatSpamEnabled = v)
                    .addNumber("Delay (ticks)", () -> (float) ModConfig.chatSpamDelay, ModConfig.SPAM_DELAY_MIN, ModConfig.SPAM_DELAY_MAX, v -> ModConfig.chatSpamDelay = Math.round(v)));
            social.add(new Module("Auto Reply", () -> ModConfig.autoReplyEnabled, v -> ModConfig.autoReplyEnabled = v));
            social.add(new Module("Announcer", () -> ModConfig.announcerEnabled, v -> ModConfig.announcerEnabled = v));
            categories.put("\u00a7dSocial", social);

            // === PRO: BUILD ===
            List<Module> build = new ArrayList<>();
            build.add(new Module("Auto Bridge", () -> ModConfig.autoBridgeEnabled, v -> ModConfig.autoBridgeEnabled = v));
            build.add(new Module("Tower", () -> ModConfig.towerEnabled, v -> ModConfig.towerEnabled = v));
            build.add(new Module("Printer", () -> ModConfig.printerEnabled, v -> ModConfig.printerEnabled = v));
            categories.put("\u00a7bBuild", build);
        }

        // === WURST (always available) ===
        List<Module> wurst = new ArrayList<>();
        wurst.add(new Module("KillAura+", () -> ModConfig.killAuraPlusEnabled, v -> ModConfig.killAuraPlusEnabled = v)
                .addNumber("CPS", () -> (float) ModConfig.killAuraPlusCps, ModConfig.KA_PLUS_CPS_MIN, ModConfig.KA_PLUS_CPS_MAX, v -> ModConfig.killAuraPlusCps = Math.round(v)));
        wurst.add(new Module("Criticals", () -> ModConfig.criticalsEnabled, v -> ModConfig.criticalsEnabled = v));
        wurst.add(new Module("BunnyHop", () -> ModConfig.bunnyHopEnabled, v -> ModConfig.bunnyHopEnabled = v));
        wurst.add(new Module("Spider", () -> ModConfig.spiderEnabled, v -> ModConfig.spiderEnabled = v));
        wurst.add(new Module("Glide", () -> ModConfig.glideEnabled, v -> ModConfig.glideEnabled = v));
        wurst.add(new Module("HighJump", () -> ModConfig.highJumpEnabled, v -> ModConfig.highJumpEnabled = v)
                .addNumber("Height", () -> ModConfig.highJumpHeight, ModConfig.HIGH_JUMP_MIN, ModConfig.HIGH_JUMP_MAX, v -> ModConfig.highJumpHeight = v));
        wurst.add(new Module("Dolphin", () -> ModConfig.dolphinEnabled, v -> ModConfig.dolphinEnabled = v));
        wurst.add(new Module("AutoSword", () -> ModConfig.autoSwordEnabled, v -> ModConfig.autoSwordEnabled = v));
        wurst.add(new Module("Sneak", () -> ModConfig.sneakEnabled, v -> ModConfig.sneakEnabled = v));
        wurst.add(new Module("AntiHunger", () -> ModConfig.antiHungerEnabled, v -> ModConfig.antiHungerEnabled = v));
        wurst.add(new Module("TriggerBot", () -> ModConfig.triggerBotEnabled, v -> ModConfig.triggerBotEnabled = v));
        wurst.add(new Module("Panic", () -> ModConfig.panicEnabled, v -> ModConfig.panicEnabled = v));
        wurst.add(new Module("InvMove", () -> ModConfig.invMoveEnabled, v -> ModConfig.invMoveEnabled = v));
        wurst.add(new Module("FastPlace", () -> ModConfig.fastPlaceEnabled, v -> ModConfig.fastPlaceEnabled = v));
        wurst.add(new Module("Parkour", () -> ModConfig.parkourEnabled, v -> ModConfig.parkourEnabled = v));
        wurst.add(new Module("NoSlowdown", () -> ModConfig.noSlowdownEnabled, v -> ModConfig.noSlowdownEnabled = v));
        wurst.add(new Module("AntiBlind", () -> ModConfig.antiBlindEnabled, v -> ModConfig.antiBlindEnabled = v));
        wurst.add(new Module("AutoWalk", () -> ModConfig.autoWalkEnabled, v -> ModConfig.autoWalkEnabled = v));
        wurst.add(new Module("AirJump", () -> ModConfig.airJumpEnabled, v -> ModConfig.airJumpEnabled = v));
        wurst.add(new Module("NoWeb", () -> ModConfig.noWebEnabled, v -> ModConfig.noWebEnabled = v));
        wurst.add(new Module("Flight+", () -> ModConfig.flightPlusEnabled, v -> ModConfig.flightPlusEnabled = v)
                .addNumber("Speed", () -> ModConfig.flightPlusSpeed, ModConfig.FLIGHT_PLUS_MIN, ModConfig.FLIGHT_PLUS_MAX, v -> ModConfig.flightPlusSpeed = v));
        wurst.add(new Module("LongJump", () -> ModConfig.longJumpEnabled, v -> ModConfig.longJumpEnabled = v)
                .addNumber("Boost", () -> ModConfig.longJumpBoost, ModConfig.LONG_JUMP_MIN, ModConfig.LONG_JUMP_MAX, v -> ModConfig.longJumpBoost = v));
        wurst.add(new Module("AutoMLG", () -> ModConfig.autoMLGEnabled, v -> ModConfig.autoMLGEnabled = v));
        wurst.add(new Module("Blink", () -> ModConfig.blinkEnabled, v -> ModConfig.blinkEnabled = v));
        categories.put("Wurst", wurst);

        // === METEOR+ (always available) ===
        List<Module> meteor2 = new ArrayList<>();
        meteor2.add(new Module("ElytraFly", () -> ModConfig.elytraFlyEnabled, v -> ModConfig.elytraFlyEnabled = v)
                .addNumber("Speed", () -> ModConfig.elytraFlySpeed, ModConfig.ELYTRA_FLY_MIN, ModConfig.ELYTRA_FLY_MAX, v -> ModConfig.elytraFlySpeed = v));
        meteor2.add(new Module("Surround", () -> ModConfig.surroundEnabled, v -> ModConfig.surroundEnabled = v));
        meteor2.add(new Module("CrystalAura", () -> ModConfig.crystalAuraEnabled, v -> ModConfig.crystalAuraEnabled = v));
        meteor2.add(new Module("HoleESP", () -> ModConfig.holeEspEnabled, v -> ModConfig.holeEspEnabled = v));
        meteor2.add(new Module("AnchorAura", () -> ModConfig.anchorAuraEnabled, v -> ModConfig.anchorAuraEnabled = v));
        meteor2.add(new Module("HoleFiller", () -> ModConfig.holeFillerEnabled, v -> ModConfig.holeFillerEnabled = v));
        meteor2.add(new Module("AutoTrap", () -> ModConfig.autoTrapEnabled, v -> ModConfig.autoTrapEnabled = v));
        meteor2.add(new Module("Reversal", () -> ModConfig.reversalEnabled, v -> ModConfig.reversalEnabled = v));
        categories.put("Meteor+", meteor2);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, BG);

        if (codeMsgTimer > 0) codeMsgTimer--;
        if (showCodeEntry) { renderCodeEntry(ctx, mouseX, mouseY, delta); return; }
        if (editField != null) { renderEditModal(ctx, mouseX, mouseY, delta); return; }

        int panelW = Math.min(320, this.width - 40);
        int panelX = (this.width - panelW) / 2;
        int panelTop = 20;
        int panelBottom = this.height - 20;

        ctx.fill(panelX - 1, panelTop - 1, panelX + panelW + 1, panelBottom + 1, ACCENT_DIM);
        ctx.fill(panelX, panelTop, panelX + panelW, panelBottom, PANEL_BG);

        // Title bar
        ctx.fill(panelX, panelTop, panelX + panelW, panelTop + TAB_H, 0xFF12122A);
        ctx.fill(panelX, panelTop + TAB_H - 1, panelX + panelW, panelTop + TAB_H, ModConfig.proUnlocked ? GOLD : ACCENT);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a7d\u00a7lOSP"), panelX + 6, panelTop + 6, TEXT_PRIMARY);
        if (ModConfig.proUnlocked) {
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a76\u00a7lPRO"), panelX + 32, panelTop + 6, GOLD);
        } else {
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a78v2.2"), panelX + 32, panelTop + 6, TEXT_DIM);
        }

        // Key icon (click to enter code)
        int keyX = panelX + panelW - 32;
        boolean keyHover = mouseX >= keyX && mouseX < keyX + 12 && mouseY >= panelTop + 2 && mouseY < panelTop + TAB_H - 2;
        ctx.drawTextWithShadow(this.textRenderer, Text.literal(keyHover ? "\u00a7e\u2605" : "\u00a78\u2605"), keyX, panelTop + 6, TEXT_DIM);

        // Close button
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a7cx"), panelX + panelW - 16, panelTop + 6, RED);

        // Category tabs (scrollable if many)
        int tabY = panelTop + TAB_H;
        int tabCount = categories.size();
        int tabW = Math.max(40, panelW / Math.min(tabCount, 6));
        int tabIdx = 0;
        int tabScrollX = 0;
        for (String cat : categories.keySet()) {
            int tx = panelX + tabIdx * tabW - tabScrollX;
            if (tx + tabW > panelX && tx < panelX + panelW) {
                boolean selected = cat.equals(activeCategory);
                boolean hovered = mouseX >= tx && mouseX < tx + tabW && mouseY >= tabY && mouseY < tabY + 20;
                boolean isPro = cat.contains("\u00a7") && !cat.equals("Combat") && !cat.equals("Movement") && !cat.equals("Render") && !cat.equals("Player");
                ctx.fill(tx, tabY, tx + tabW, tabY + 20, selected ? (isPro ? 0xFF2A2210 : 0xFF222244) : (hovered ? 0xFF1A1A38 : 0xFF141430));
                if (selected) ctx.fill(tx, tabY + 18, tx + tabW, tabY + 20, isPro ? GOLD : ACCENT);
                String displayCat = cat.replaceAll("\u00a7.", "");
                int textW = this.textRenderer.getWidth(displayCat);
                String prefix = selected ? "\u00a7f" : "\u00a78";
                if (isPro && selected) prefix = "\u00a76";
                ctx.drawTextWithShadow(this.textRenderer, Text.literal(prefix + displayCat), tx + (tabW - textW) / 2, tabY + 5, TEXT_PRIMARY);
            }
            tabIdx++;
        }

        // Module list area
        int listTop = tabY + 20;
        int listBottom = panelBottom - 2;
        ctx.enableScissor(panelX, listTop, panelX + panelW, listBottom);

        List<Module> modules = categories.get(activeCategory);
        if (modules != null) {
            int y = listTop - (int) scrollOffset;
            for (Module mod : modules) {
                y = renderModule(ctx, mod, panelX + 2, y, panelW - 4, mouseX, mouseY);
            }
        }
        ctx.disableScissor();

        // Scrollbar
        if (modules != null) {
            int contentH = getContentHeight(modules);
            int viewH = listBottom - listTop;
            if (contentH > viewH) {
                int barX = panelX + panelW - 4;
                float ratio = (float) viewH / contentH;
                int thumbH = Math.max(15, (int) (viewH * ratio));
                int maxScroll = contentH - viewH;
                int thumbY = listTop + (maxScroll > 0 ? (int) (scrollOffset / maxScroll * (viewH - thumbH)) : 0);
                ctx.fill(barX, listTop, barX + 3, listBottom, 0x20FFFFFF);
                ctx.fill(barX, thumbY, barX + 3, thumbY + thumbH, ACCENT);
            }
        }
    }

    private int renderModule(DrawContext ctx, Module mod, int x, int y, int w, int mx, int my) {
        boolean enabled = mod.enabled.get();
        boolean hovered = mx >= x && mx < x + w && my >= y && my < y + MODULE_H;
        boolean expanded = mod.name.equals(expandedModule) && !mod.settings.isEmpty();

        int bg = enabled ? MODULE_ON : (hovered ? MODULE_HOVER : MODULE_BG);
        ctx.fill(x, y, x + w, y + MODULE_H, bg);
        if (enabled) ctx.fill(x, y, x + 2, y + MODULE_H, ACCENT);

        ctx.drawTextWithShadow(this.textRenderer, Text.literal(mod.name), x + 8, y + 6, enabled ? TEXT_PRIMARY : TEXT_DIM);

        String status = enabled ? "\u00a7aON" : "\u00a78OFF";
        int statusW = this.textRenderer.getWidth(enabled ? "ON" : "OFF");
        ctx.drawTextWithShadow(this.textRenderer, Text.literal(status), x + w - statusW - 20, y + 6, TEXT_PRIMARY);

        if (!mod.settings.isEmpty()) {
            String arrow = expanded ? "\u25BC" : "\u25B6";
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a78" + arrow), x + w - 12, y + 6, TEXT_DIM);
        }
        ctx.fill(x, y + MODULE_H - 1, x + w, y + MODULE_H, 0x18FFFFFF);
        y += MODULE_H;

        if (expanded) {
            for (Setting s : mod.settings) {
                ctx.fill(x, y, x + w, y + SETTING_H, SETTING_BG);
                ctx.fill(x + 2, y, x + 3, y + SETTING_H, 0x40FFFFFF);
                if (s.type == SettingType.TOGGLE) {
                    boolean on = s.boolGetter.get();
                    String val = on ? "\u00a7aON" : "\u00a7cOFF";
                    ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a77  " + s.name), x + 10, y + 4, TEXT_DIM);
                    int valW = this.textRenderer.getWidth(on ? "ON" : "OFF");
                    ctx.drawTextWithShadow(this.textRenderer, Text.literal(val), x + w - valW - 10, y + 4, TEXT_PRIMARY);
                } else {
                    float val = s.floatGetter.get();
                    ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a77  " + s.name + ": \u00a7f" + formatNumber(val)), x + 10, y + 4, TEXT_DIM);
                    ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a78[\u00a7bedit\u00a78]"), x + w - 28, y + 4, TEXT_DIM);
                }
                ctx.fill(x, y + SETTING_H - 1, x + w, y + SETTING_H, 0x10FFFFFF);
                y += SETTING_H;
            }
        }
        return y;
    }

    private void renderEditModal(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0xC0000000);
        int boxW = 220; int boxH = 90;
        int boxX = this.width / 2 - boxW / 2; int boxY = this.height / 2 - boxH / 2;
        ctx.fill(boxX - 1, boxY - 1, boxX + boxW + 1, boxY + boxH + 1, ACCENT);
        ctx.fill(boxX, boxY, boxX + boxW, boxY + boxH, 0xFF1A1A2E);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("\u00a7b\u00a7l" + editLabel), this.width / 2, boxY + 8, TEXT_PRIMARY);
        editField.setX(this.width / 2 - 100); editField.setY(boxY + 24);
        editField.render(ctx, mouseX, mouseY, delta);
        int btnY = boxY + 50;
        boolean confirmHover = mouseX >= this.width / 2 - 50 && mouseX < this.width / 2 - 5 && mouseY >= btnY && mouseY < btnY + 14;
        boolean cancelHover = mouseX >= this.width / 2 + 5 && mouseX < this.width / 2 + 50 && mouseY >= btnY && mouseY < btnY + 14;
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(confirmHover ? "\u00a7a\u00a7l[Confirm]" : "\u00a7a[Confirm]"), this.width / 2 - 28, btnY, GREEN);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(cancelHover ? "\u00a7c\u00a7l[Cancel]" : "\u00a7c[Cancel]"), this.width / 2 + 28, btnY, RED);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("\u00a78" + formatNumber(editMin) + " \u2014 " + formatNumber(editMax)), this.width / 2, boxY + boxH - 14, TEXT_DIM);
    }

    private void renderCodeEntry(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0xC0000000);
        int boxW = 260; int boxH = 100;
        int boxX = this.width / 2 - boxW / 2; int boxY = this.height / 2 - boxH / 2;
        ctx.fill(boxX - 1, boxY - 1, boxX + boxW + 1, boxY + boxH + 1, GOLD);
        ctx.fill(boxX, boxY, boxX + boxW, boxY + boxH, 0xFF1A1A2E);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("\u00a76\u00a7lEnter Activation Code"), this.width / 2, boxY + 8, GOLD);
        if (codeField != null) {
            codeField.setX(this.width / 2 - 100); codeField.setY(boxY + 26);
            codeField.render(ctx, mouseX, mouseY, delta);
        }
        int btnY = boxY + 54;
        boolean activateHover = mouseX >= this.width / 2 - 55 && mouseX < this.width / 2 - 5 && mouseY >= btnY && mouseY < btnY + 14;
        boolean cancelHover = mouseX >= this.width / 2 + 5 && mouseX < this.width / 2 + 55 && mouseY >= btnY && mouseY < btnY + 14;
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(activateHover ? "\u00a76\u00a7l[Activate]" : "\u00a76[Activate]"), this.width / 2 - 30, btnY, GOLD);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(cancelHover ? "\u00a7c\u00a7l[Cancel]" : "\u00a7c[Cancel]"), this.width / 2 + 30, btnY, RED);
        if (codeMessage != null && codeMsgTimer > 0) {
            boolean success = codeMessage.startsWith("\u00a7a");
            ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(codeMessage), this.width / 2, boxY + boxH - 14, success ? GREEN : RED);
        }
    }

    // ---- INPUT HANDLING ----

    @Override
    public boolean mouseClicked(Click click, boolean bl) {
        double mouseX = click.x(); double mouseY = click.y(); int button = click.button();
        if (button != 0) return false;

        // Code entry modal
        if (showCodeEntry) {
            if (codeField != null && codeField.isMouseOver(mouseX, mouseY)) {
                codeField.mouseClicked(click, bl); setFocused(codeField); return true;
            }
            int btnY = this.height / 2 - 50 + 54;
            if (mouseY >= btnY && mouseY < btnY + 14) {
                if (mouseX >= this.width / 2 - 55 && mouseX < this.width / 2 - 5) { tryActivateCode(); return true; }
                if (mouseX >= this.width / 2 + 5 && mouseX < this.width / 2 + 55) { showCodeEntry = false; codeField = null; return true; }
            }
            return true;
        }

        // Edit modal
        if (editField != null) {
            if (editField.isMouseOver(mouseX, mouseY)) { editField.mouseClicked(click, bl); setFocused(editField); return true; }
            int btnY = this.height / 2 - 45 + 50;
            if (mouseY >= btnY && mouseY < btnY + 14) {
                if (mouseX >= this.width / 2 - 50 && mouseX < this.width / 2 - 5) { confirmEdit(); return true; }
                if (mouseX >= this.width / 2 + 5 && mouseX < this.width / 2 + 50) { cancelEdit(); return true; }
            }
            return true;
        }

        int panelW = Math.min(320, this.width - 40);
        int panelX = (this.width - panelW) / 2;
        int panelTop = 20;

        // Key icon (code entry)
        int keyX = panelX + panelW - 32;
        if (mouseX >= keyX && mouseX < keyX + 12 && mouseY >= panelTop + 2 && mouseY < panelTop + TAB_H - 2) {
            if (!ModConfig.proUnlocked) {
                showCodeEntry = true;
                codeField = new TextFieldWidget(this.textRenderer, 0, 0, 200, 20, Text.literal("Code"));
                codeField.setMaxLength(20);
                codeField.setEditable(true);
                setFocused(codeField);
            }
            return true;
        }

        // Close button
        if (mouseX >= panelX + panelW - 18 && mouseX <= panelX + panelW - 4 && mouseY >= panelTop + 2 && mouseY <= panelTop + TAB_H - 2) {
            close(); return true;
        }

        // Category tabs
        int tabY = panelTop + TAB_H;
        if (mouseY >= tabY && mouseY < tabY + 20) {
            int tabCount = categories.size();
            int tabW = Math.max(40, panelW / Math.min(tabCount, 6));
            int idx = 0;
            for (String cat : categories.keySet()) {
                int tx = panelX + idx * tabW;
                if (mouseX >= tx && mouseX < tx + tabW) {
                    activeCategory = cat; scrollOffset = 0; expandedModule = null; return true;
                }
                idx++;
            }
        }

        // Module list
        int listTop = tabY + 20;
        List<Module> modules = categories.get(activeCategory);
        if (modules == null) return false;
        int y = listTop - (int) scrollOffset;
        for (Module mod : modules) {
            boolean expanded = mod.name.equals(expandedModule) && !mod.settings.isEmpty();
            if (mouseX >= panelX + 2 && mouseX < panelX + panelW - 2 && mouseY >= y && mouseY < y + MODULE_H) {
                if (mouseX >= panelX + panelW - 30 && !mod.settings.isEmpty()) { expandedModule = expanded ? null : mod.name; }
                else { mod.setter.accept(!mod.enabled.get()); ModConfig.save(); }
                return true;
            }
            y += MODULE_H;
            if (expanded) {
                for (Setting s : mod.settings) {
                    if (mouseX >= panelX + 2 && mouseX < panelX + panelW - 2 && mouseY >= y && mouseY < y + SETTING_H) {
                        if (s.type == SettingType.TOGGLE) { s.boolSetter.accept(!s.boolGetter.get()); ModConfig.save(); }
                        else { openEditModal(s.name, s.floatGetter.get(), s.min, s.max, s.floatSetter); }
                        return true;
                    }
                    y += SETTING_H;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (editField != null || showCodeEntry) return true;
        List<Module> modules = categories.get(activeCategory);
        if (modules == null) return true;
        int panelTop = 20 + TAB_H + 20;
        int panelBottom = this.height - 20;
        int viewH = panelBottom - panelTop;
        int contentH = getContentHeight(modules);
        int maxScroll = Math.max(0, contentH - viewH);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - verticalAmount * 16));
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        int keyCode = keyInput.key();
        if (showCodeEntry && codeField != null) {
            if (keyCode == 257) { tryActivateCode(); return true; }
            if (keyCode == 256) { showCodeEntry = false; codeField = null; return true; }
            return codeField.keyPressed(keyInput);
        }
        if (editField != null) {
            if (keyCode == 257) { confirmEdit(); return true; }
            if (keyCode == 256) { cancelEdit(); return true; }
            return editField.keyPressed(keyInput);
        }
        if (keyCode == 256) { close(); return true; }
        return super.keyPressed(keyInput);
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        if (showCodeEntry && codeField != null) return codeField.charTyped(charInput);
        if (editField != null) return editField.charTyped(charInput);
        return super.charTyped(charInput);
    }

    // ---- CODE ACTIVATION ----

    private void tryActivateCode() {
        if (codeField == null) return;
        String input = codeField.getText().trim();
        if (ModConfig.validateCode(input)) {
            ModConfig.proUnlocked = true;
            ModConfig.save();
            codeMessage = "\u00a7a\u00a7lPRO UNLOCKED!";
            codeMsgTimer = 60;
            showCodeEntry = false;
            codeField = null;
            init();
        } else {
            codeMessage = "\u00a7cInvalid code";
            codeMsgTimer = 40;
            codeField.setText("");
        }
    }

    // ---- EDIT MODAL ----

    private void openEditModal(String label, float current, float min, float max, java.util.function.Consumer<Float> setter) {
        editLabel = label; editMin = min; editMax = max; editSetter = setter;
        editField = new TextFieldWidget(this.textRenderer, 0, 0, 200, 20, Text.literal(label));
        editField.setText(formatNumber(current)); editField.setMaxLength(15); editField.setEditable(true);
        setFocused(editField);
    }

    private void confirmEdit() {
        if (editField == null || editSetter == null) return;
        try { float val = Math.max(editMin, Math.min(editMax, Float.parseFloat(editField.getText().trim()))); editSetter.accept(val); ModConfig.save(); } catch (NumberFormatException ignored) {}
        cancelEdit();
    }

    private void cancelEdit() { editField = null; editLabel = null; editSetter = null; }

    @Override
    public void close() {
        if (showCodeEntry) { showCodeEntry = false; codeField = null; return; }
        if (editField != null) { cancelEdit(); return; }
        ModConfig.save();
        if (this.client != null) this.client.setScreen(parent);
    }

    private int getContentHeight(List<Module> modules) {
        int h = 0;
        for (Module mod : modules) { h += MODULE_H; if (mod.name.equals(expandedModule)) h += mod.settings.size() * SETTING_H; }
        return h;
    }

    private static String formatNumber(float val) {
        if (val == Math.floor(val) && !Float.isInfinite(val)) return String.valueOf((int) val);
        return String.format("%.1f", val);
    }

    private enum SettingType { TOGGLE, NUMBER }

    private static class Setting {
        final String name; final SettingType type;
        java.util.function.Supplier<Boolean> boolGetter; java.util.function.Consumer<Boolean> boolSetter;
        java.util.function.Supplier<Float> floatGetter; java.util.function.Consumer<Float> floatSetter;
        float min, max;
        Setting(String name, SettingType type) { this.name = name; this.type = type; }
    }

    private static class Module {
        final String name; final java.util.function.Supplier<Boolean> enabled; final java.util.function.Consumer<Boolean> setter;
        final List<Setting> settings = new ArrayList<>();
        Module(String name, java.util.function.Supplier<Boolean> enabled, java.util.function.Consumer<Boolean> setter) { this.name = name; this.enabled = enabled; this.setter = setter; }
        Module addToggle(String sName, java.util.function.Supplier<Boolean> getter, java.util.function.Consumer<Boolean> setter) {
            Setting s = new Setting(sName, SettingType.TOGGLE); s.boolGetter = getter; s.boolSetter = setter; settings.add(s); return this;
        }
        Module addNumber(String sName, java.util.function.Supplier<Float> getter, float min, float max, java.util.function.Consumer<Float> setter) {
            Setting s = new Setting(sName, SettingType.NUMBER); s.floatGetter = getter; s.floatSetter = setter; s.min = min; s.max = max; settings.add(s); return this;
        }
    }
}
