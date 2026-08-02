package com.reachfly;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.util.InputUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("reachfly.json");

    // --- Reach ---
    public static boolean reachEnabled = false;
    public static float reachDistance = 6.0f;
    public static final float REACH_MIN = 3.0f;
    public static final float REACH_MAX = 50.0f;

    // --- Fly ---
    public static boolean flyEnabled = false;
    public static float flySpeed = 1.0f;
    public static final float FLY_SPEED_MIN = 0.1f;
    public static final float FLY_SPEED_MAX = 10.0f;

    // --- ESP ---
    public static boolean espEnabled = false;
    public static boolean espPlayers = true;
    public static boolean espHostile = true;
    public static boolean espPassive = false;
    public static boolean espLines = false;
    public static boolean espPathTrace = false;

    // --- Auto Hit ---
    public static boolean autoHitEnabled = false;
    public static float autoHitRange = 3.0f;
    public static final float AUTO_HIT_RANGE_MIN = 1.0f;
    public static final float AUTO_HIT_RANGE_MAX = 50.0f;
    public static boolean autoHitPlayersOnly = false;
    public static boolean killAuraEnabled = false;
    public static boolean killAuraPlusEnabled = false;
    public static int killAuraPlusCps = 20;
    public static final int KA_PLUS_CPS_MIN = 5;
    public static final int KA_PLUS_CPS_MAX = 40;

    // --- Low Health Kill ---
    public static boolean lowHealthKillEnabled = false;
    public static float lowHealthThreshold = 6.0f;
    public static final float LOW_HEALTH_MIN = 1.0f;
    public static final float LOW_HEALTH_MAX = 20.0f;

    // --- Auto Kill When Low HP ---
    public static boolean autoKillWhenLowEnabled = false;
    public static float autoKillSelfHpThreshold = 6.0f;
    public static float autoKillWhenLowRange = 4.0f;
    public static final float AUTO_KILL_SELF_HP_MIN = 1.0f;
    public static final float AUTO_KILL_SELF_HP_MAX = 20.0f;
    public static final float AUTO_KILL_RANGE_MIN = 1.0f;
    public static final float AUTO_KILL_RANGE_MAX = 50.0f;

    // --- Eating Assist ---
    public static boolean eatingAssistEnabled = false;
    public static int eatingHungerThreshold = 14;
    public static final int EATING_HUNGER_MIN = 1;
    public static final int EATING_HUNGER_MAX = 19;

    // --- Jesus ---
    public static boolean jesusEnabled = false;

    // --- Auto Elytra Swap ---
    public static boolean autoElytraSwapEnabled = false;

    // --- Fly to Coords ---
    public static boolean flyToCoordsEnabled = false;
    public static float flyToX = 0;
    public static float flyToY = 100;
    public static float flyToZ = 0;
    public static float flyToCoordsSpeed = 2.0f;
    public static final float FLY_TO_SPEED_MIN = 0.5f;
    public static final float FLY_TO_SPEED_MAX = 20.0f;

    // --- NoFall ---
    public static boolean noFallEnabled = false;

    // --- Fullbright ---
    public static boolean fullbrightEnabled = false;

    // --- Speed ---
    public static boolean speedEnabled = false;
    public static float speedMultiplier = 2.0f;
    public static final float SPEED_MIN = 1.0f;
    public static final float SPEED_MAX = 10.0f;

    // --- X-Ray ---
    public static boolean xrayEnabled = false;

    // --- Knockback ---
    public static boolean knockbackEnabled = false;
    public static float knockbackStrength = 5.0f;
    public static final float KNOCKBACK_MIN = 1.0f;
    public static final float KNOCKBACK_MAX = 2500.0f;

    // --- Auto Totem ---
    public static boolean autoTotemEnabled = false;

    // --- Auto Armor ---
    public static boolean autoArmorEnabled = false;

    // --- Scaffold ---
    public static boolean scaffoldEnabled = false;

    // --- Meteor-style ---
    public static boolean autoLogEnabled = false;
    public static float autoLogHealth = 4.0f;
    public static final float AUTO_LOG_HP_MIN = 1.0f;
    public static final float AUTO_LOG_HP_MAX = 19.0f;
    public static boolean autoRespawnEnabled = false;
    public static boolean betterSprintEnabled = false;
    public static boolean safeWalkEnabled = false;
    public static boolean stepEnabled = false;

    // --- Meteor-style v2 ---
    public static boolean elytraFlyEnabled = false;
    public static float elytraFlySpeed = 2.0f;
    public static final float ELYTRA_FLY_MIN = 0.5f;
    public static final float ELYTRA_FLY_MAX = 10.0f;
    public static boolean surroundEnabled = false;
    public static boolean holeEspEnabled = false;
    public static boolean crystalAuraEnabled = false;

    // --- Wurst-style ---
    public static boolean criticalsEnabled = false;
    public static boolean bunnyHopEnabled = false;
    public static boolean spiderEnabled = false;
    public static boolean glideEnabled = false;
    public static boolean highJumpEnabled = false;
    public static float highJumpHeight = 2.0f;
    public static final float HIGH_JUMP_MIN = 1.0f;
    public static final float HIGH_JUMP_MAX = 10.0f;
    public static boolean dolphinEnabled = false;
    public static boolean autoSwordEnabled = false;
    public static boolean sneakEnabled = false;
    public static boolean panicEnabled = false;
    public static boolean antiHungerEnabled = false;
    public static boolean triggerBotEnabled = false;

    // --- Inventory Move ---
    public static boolean invMoveEnabled = false;
    public static float stepHeight = 2.0f;
    public static final float STEP_MIN = 1.0f;
    public static final float STEP_MAX = 10.0f;

    // --- New Wurst features ---
    public static boolean fastPlaceEnabled = false;
    public static boolean parkourEnabled = false;
    public static boolean noSlowdownEnabled = false;
    public static boolean antiBlindEnabled = false;
    public static boolean autoWalkEnabled = false;
    public static boolean airJumpEnabled = false;
    public static boolean noWebEnabled = false;
    public static boolean flightPlusEnabled = false;
    public static float flightPlusSpeed = 2.0f;
    public static final float FLIGHT_PLUS_MIN = 0.5f;
    public static final float FLIGHT_PLUS_MAX = 10.0f;
    public static boolean longJumpEnabled = false;
    public static float longJumpBoost = 2.0f;
    public static final float LONG_JUMP_MIN = 0.5f;
    public static final float LONG_JUMP_MAX = 5.0f;
    public static boolean autoMLGEnabled = false;
    public static boolean blinkEnabled = false;

    // --- New Meteor features ---
    public static boolean anchorAuraEnabled = false;
    public static boolean holeFillerEnabled = false;
    public static boolean autoTrapEnabled = false;
    public static boolean reversalEnabled = false;

    // --- Teleport ---
    public static boolean tpUseServerAddon = true;
    public static float tpX = 0;
    public static float tpY = 100;
    public static float tpZ = 0;

    // --- HUD ---
    public static boolean hudVisible = true;

    // --- Walk to Coords ---
    public static boolean walkToCoordsEnabled = false;
    public static float walkToX = 0;
    public static float walkToY = 64;
    public static float walkToZ = 0;

    // --- Pro Unlock ---
    public static boolean proUnlocked = false;

    // ===== PRO: Stealth =====
    public static boolean antiKnockbackEnabled = false;
    public static float antiKnockbackStrength = 100.0f;
    public static final float ANTI_KB_MIN = 0.0f;
    public static final float ANTI_KB_MAX = 100.0f;
    public static boolean noSwingEnabled = false;
    public static boolean antiAfkEnabled = false;
    public static int antiAfkInterval = 200;
    public static final int ANTI_AFK_MIN = 20;
    public static final int ANTI_AFK_MAX = 1200;

    // ===== PRO: Level =====
    public static boolean fastBreakEnabled = false;
    public static float fastBreakSpeed = 3.0f;
    public static final float FAST_BREAK_MIN = 1.0f;
    public static final float FAST_BREAK_MAX = 10.0f;
    public static boolean nukerEnabled = false;
    public static float nukerRadius = 3.0f;
    public static final float NUKER_MIN = 1.0f;
    public static final float NUKER_MAX = 6.0f;
    public static boolean autoFarmEnabled = false;

    // ===== PRO: Exploit =====
    public static boolean phaseEnabled = false;
    public static boolean freecamEnabled = false;
    public static boolean timerEnabled = false;
    public static float timerSpeed = 2.0f;
    public static final float TIMER_MIN = 0.1f;
    public static final float TIMER_MAX = 10.0f;

    // ===== PRO: Visual =====
    public static boolean chestEspEnabled = false;
    public static boolean trajectoriesEnabled = false;
    public static boolean nametagsEnabled = false;

    // ===== PRO: Utility =====
    public static boolean autoFishEnabled = false;
    public static boolean chestStealerEnabled = false;
    public static int chestStealerDelay = 3;
    public static final int CHEST_STEALER_MIN = 0;
    public static final int CHEST_STEALER_MAX = 20;
    public static boolean autoToolEnabled = false;
    public static boolean invSortEnabled = false;

    // ===== PRO: Social =====
    public static boolean chatSpamEnabled = false;
    public static String chatSpamMessage = "f1sch Pro";
    public static int chatSpamDelay = 100;
    public static final int SPAM_DELAY_MIN = 20;
    public static final int SPAM_DELAY_MAX = 1200;
    public static boolean autoReplyEnabled = false;
    public static String autoReplyMessage = "I'm AFK";
    public static boolean announcerEnabled = false;

    // ===== PRO: Build =====
    public static boolean autoBridgeEnabled = false;
    public static boolean towerEnabled = false;
    public static boolean printerEnabled = false;

    // ===== PRO: Server (requires addon/datapack) =====
    public static boolean opSelfEnabled = false;

    // --- Obfuscated validation ---
    private static final int[] _d = {0x39, 0x7D, 0x62, 0x3F, 0x3D, 0x20, 0x61, 0x29, 0x23, 0x26, 0x24};
    private static final int _x = 0x4F;

    public static boolean validateCode(String input) {
        if (input == null || input.length() != _d.length) return false;
        for (int i = 0; i < _d.length; i++) {
            if ((input.charAt(i) ^ _x) != _d[i]) return false;
        }
        return true;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                ConfigData data = GSON.fromJson(json, ConfigData.class);
                if (data != null) {
                    reachEnabled = data.reachEnabled;
                    reachDistance = clamp(data.reachDistance, REACH_MIN, REACH_MAX);
                    flyEnabled = data.flyEnabled;
                    flySpeed = clamp(data.flySpeed, FLY_SPEED_MIN, FLY_SPEED_MAX);
                    espEnabled = data.espEnabled;
                    espPlayers = data.espPlayers;
                    espHostile = data.espHostile;
                    espPassive = data.espPassive;
                    espLines = data.espLines;
                    espPathTrace = data.espPathTrace;
                    autoHitEnabled = data.autoHitEnabled;
                    autoHitRange = clamp(data.autoHitRange, AUTO_HIT_RANGE_MIN, AUTO_HIT_RANGE_MAX);
                    autoHitPlayersOnly = data.autoHitPlayersOnly;
                    killAuraEnabled = data.killAuraEnabled;
                    killAuraPlusEnabled = data.killAuraPlusEnabled;
                    killAuraPlusCps = Math.max(KA_PLUS_CPS_MIN, Math.min(KA_PLUS_CPS_MAX, data.killAuraPlusCps));
                    lowHealthKillEnabled = data.lowHealthKillEnabled;
                    lowHealthThreshold = clamp(data.lowHealthThreshold, LOW_HEALTH_MIN, LOW_HEALTH_MAX);
                    autoKillWhenLowEnabled = data.autoKillWhenLowEnabled;
                    autoKillSelfHpThreshold = clamp(data.autoKillSelfHpThreshold, AUTO_KILL_SELF_HP_MIN, AUTO_KILL_SELF_HP_MAX);
                    autoKillWhenLowRange = clamp(data.autoKillWhenLowRange, AUTO_KILL_RANGE_MIN, AUTO_KILL_RANGE_MAX);
                    eatingAssistEnabled = data.eatingAssistEnabled;
                    eatingHungerThreshold = (int) clamp(data.eatingHungerThreshold, EATING_HUNGER_MIN, EATING_HUNGER_MAX);
                    jesusEnabled = data.jesusEnabled;
                    autoElytraSwapEnabled = data.autoElytraSwapEnabled;
                    flyToCoordsEnabled = data.flyToCoordsEnabled;
                    flyToX = data.flyToX;
                    flyToY = data.flyToY;
                    flyToZ = data.flyToZ;
                    flyToCoordsSpeed = clamp(data.flyToCoordsSpeed, FLY_TO_SPEED_MIN, FLY_TO_SPEED_MAX);
                    noFallEnabled = data.noFallEnabled;
                    fullbrightEnabled = data.fullbrightEnabled;
                    speedEnabled = data.speedEnabled;
                    speedMultiplier = clamp(data.speedMultiplier, SPEED_MIN, SPEED_MAX);
                    xrayEnabled = data.xrayEnabled;
                    knockbackEnabled = data.knockbackEnabled;
                    knockbackStrength = clamp(data.knockbackStrength, KNOCKBACK_MIN, KNOCKBACK_MAX);
                    hudVisible = data.hudVisible;
                    autoTotemEnabled = data.autoTotemEnabled;
                    autoArmorEnabled = data.autoArmorEnabled;
                    scaffoldEnabled = data.scaffoldEnabled;
                    autoLogEnabled = data.autoLogEnabled;
                    autoLogHealth = clamp(data.autoLogHealth, AUTO_LOG_HP_MIN, AUTO_LOG_HP_MAX);
                    autoRespawnEnabled = data.autoRespawnEnabled;
                    betterSprintEnabled = data.betterSprintEnabled;
                    safeWalkEnabled = data.safeWalkEnabled;
                    stepEnabled = data.stepEnabled;
                    stepHeight = clamp(data.stepHeight, STEP_MIN, STEP_MAX);
                    elytraFlyEnabled = data.elytraFlyEnabled;
                    elytraFlySpeed = clamp(data.elytraFlySpeed, ELYTRA_FLY_MIN, ELYTRA_FLY_MAX);
                    surroundEnabled = data.surroundEnabled;
                    holeEspEnabled = data.holeEspEnabled;
                    crystalAuraEnabled = data.crystalAuraEnabled;
                    criticalsEnabled = data.criticalsEnabled;
                    bunnyHopEnabled = data.bunnyHopEnabled;
                    spiderEnabled = data.spiderEnabled;
                    glideEnabled = data.glideEnabled;
                    highJumpEnabled = data.highJumpEnabled;
                    highJumpHeight = clamp(data.highJumpHeight, HIGH_JUMP_MIN, HIGH_JUMP_MAX);
                    dolphinEnabled = data.dolphinEnabled;
                    autoSwordEnabled = data.autoSwordEnabled;
                    sneakEnabled = data.sneakEnabled;
                    panicEnabled = data.panicEnabled;
                    antiHungerEnabled = data.antiHungerEnabled;
                    triggerBotEnabled = data.triggerBotEnabled;
                    invMoveEnabled = data.invMoveEnabled;
                    fastPlaceEnabled = data.fastPlaceEnabled;
                    parkourEnabled = data.parkourEnabled;
                    noSlowdownEnabled = data.noSlowdownEnabled;
                    antiBlindEnabled = data.antiBlindEnabled;
                    autoWalkEnabled = data.autoWalkEnabled;
                    airJumpEnabled = data.airJumpEnabled;
                    noWebEnabled = data.noWebEnabled;
                    flightPlusEnabled = data.flightPlusEnabled;
                    flightPlusSpeed = clamp(data.flightPlusSpeed, FLIGHT_PLUS_MIN, FLIGHT_PLUS_MAX);
                    longJumpEnabled = data.longJumpEnabled;
                    longJumpBoost = clamp(data.longJumpBoost, LONG_JUMP_MIN, LONG_JUMP_MAX);
                    autoMLGEnabled = data.autoMLGEnabled;
                    blinkEnabled = data.blinkEnabled;
                    anchorAuraEnabled = data.anchorAuraEnabled;
                    holeFillerEnabled = data.holeFillerEnabled;
                    autoTrapEnabled = data.autoTrapEnabled;
                    reversalEnabled = data.reversalEnabled;
                    tpUseServerAddon = data.tpUseServerAddon;
                    tpX = data.tpX;
                    tpY = data.tpY;
                    tpZ = data.tpZ;
                    walkToCoordsEnabled = data.walkToCoordsEnabled;
                    walkToX = data.walkToX;
                    walkToY = data.walkToY;
                    walkToZ = data.walkToZ;
                    proUnlocked = data.proUnlocked;
                    antiKnockbackEnabled = data.antiKnockbackEnabled;
                    antiKnockbackStrength = clamp(data.antiKnockbackStrength, ANTI_KB_MIN, ANTI_KB_MAX);
                    noSwingEnabled = data.noSwingEnabled;
                    antiAfkEnabled = data.antiAfkEnabled;
                    antiAfkInterval = (int) clamp(data.antiAfkInterval, ANTI_AFK_MIN, ANTI_AFK_MAX);
                    fastBreakEnabled = data.fastBreakEnabled;
                    fastBreakSpeed = clamp(data.fastBreakSpeed, FAST_BREAK_MIN, FAST_BREAK_MAX);
                    nukerEnabled = data.nukerEnabled;
                    nukerRadius = clamp(data.nukerRadius, NUKER_MIN, NUKER_MAX);
                    autoFarmEnabled = data.autoFarmEnabled;
                    phaseEnabled = data.phaseEnabled;
                    freecamEnabled = data.freecamEnabled;
                    timerEnabled = data.timerEnabled;
                    timerSpeed = clamp(data.timerSpeed, TIMER_MIN, TIMER_MAX);
                    chestEspEnabled = data.chestEspEnabled;
                    trajectoriesEnabled = data.trajectoriesEnabled;
                    nametagsEnabled = data.nametagsEnabled;
                    autoFishEnabled = data.autoFishEnabled;
                    chestStealerEnabled = data.chestStealerEnabled;
                    chestStealerDelay = (int) clamp(data.chestStealerDelay, CHEST_STEALER_MIN, CHEST_STEALER_MAX);
                    autoToolEnabled = data.autoToolEnabled;
                    invSortEnabled = data.invSortEnabled;
                    chatSpamEnabled = data.chatSpamEnabled;
                    if (data.chatSpamMessage != null) chatSpamMessage = data.chatSpamMessage;
                    chatSpamDelay = (int) clamp(data.chatSpamDelay, SPAM_DELAY_MIN, SPAM_DELAY_MAX);
                    autoReplyEnabled = data.autoReplyEnabled;
                    if (data.autoReplyMessage != null) autoReplyMessage = data.autoReplyMessage;
                    announcerEnabled = data.announcerEnabled;
                    autoBridgeEnabled = data.autoBridgeEnabled;
                    towerEnabled = data.towerEnabled;
                    printerEnabled = data.printerEnabled;
                    savedKeybinds = data.keybinds;
                }
                ReachFlyClient.LOGGER.info("[f1sch] Config loaded.");
            } catch (IOException e) {
                ReachFlyClient.LOGGER.error("[f1sch] Failed to load config", e);
            }
        } else {
            save();
        }
    }

    public static void save() {
        ConfigData data = new ConfigData();
        data.reachEnabled = reachEnabled;
        data.reachDistance = reachDistance;
        data.flyEnabled = flyEnabled;
        data.flySpeed = flySpeed;
        data.espEnabled = espEnabled;
        data.espPlayers = espPlayers;
        data.espHostile = espHostile;
        data.espPassive = espPassive;
        data.espLines = espLines;
        data.espPathTrace = espPathTrace;
        data.autoHitEnabled = autoHitEnabled;
        data.autoHitRange = autoHitRange;
        data.autoHitPlayersOnly = autoHitPlayersOnly;
        data.killAuraEnabled = killAuraEnabled;
        data.killAuraPlusEnabled = killAuraPlusEnabled;
        data.killAuraPlusCps = killAuraPlusCps;
        data.lowHealthKillEnabled = lowHealthKillEnabled;
        data.lowHealthThreshold = lowHealthThreshold;
        data.autoKillWhenLowEnabled = autoKillWhenLowEnabled;
        data.autoKillSelfHpThreshold = autoKillSelfHpThreshold;
        data.autoKillWhenLowRange = autoKillWhenLowRange;
        data.eatingAssistEnabled = eatingAssistEnabled;
        data.eatingHungerThreshold = eatingHungerThreshold;
        data.jesusEnabled = jesusEnabled;
        data.autoElytraSwapEnabled = autoElytraSwapEnabled;
        data.flyToCoordsEnabled = flyToCoordsEnabled;
        data.flyToX = flyToX;
        data.flyToY = flyToY;
        data.flyToZ = flyToZ;
        data.flyToCoordsSpeed = flyToCoordsSpeed;
        data.noFallEnabled = noFallEnabled;
        data.fullbrightEnabled = fullbrightEnabled;
        data.speedEnabled = speedEnabled;
        data.speedMultiplier = speedMultiplier;
        data.xrayEnabled = xrayEnabled;
        data.knockbackEnabled = knockbackEnabled;
        data.knockbackStrength = knockbackStrength;
        data.hudVisible = hudVisible;
        data.autoTotemEnabled = autoTotemEnabled;
        data.autoArmorEnabled = autoArmorEnabled;
        data.scaffoldEnabled = scaffoldEnabled;
        data.autoLogEnabled = autoLogEnabled;
        data.autoLogHealth = autoLogHealth;
        data.autoRespawnEnabled = autoRespawnEnabled;
        data.betterSprintEnabled = betterSprintEnabled;
        data.safeWalkEnabled = safeWalkEnabled;
        data.stepEnabled = stepEnabled;
        data.stepHeight = stepHeight;
        data.elytraFlyEnabled = elytraFlyEnabled;
        data.elytraFlySpeed = elytraFlySpeed;
        data.surroundEnabled = surroundEnabled;
        data.holeEspEnabled = holeEspEnabled;
        data.crystalAuraEnabled = crystalAuraEnabled;
        data.criticalsEnabled = criticalsEnabled;
        data.bunnyHopEnabled = bunnyHopEnabled;
        data.spiderEnabled = spiderEnabled;
        data.glideEnabled = glideEnabled;
        data.highJumpEnabled = highJumpEnabled;
        data.highJumpHeight = highJumpHeight;
        data.dolphinEnabled = dolphinEnabled;
        data.autoSwordEnabled = autoSwordEnabled;
        data.sneakEnabled = sneakEnabled;
        data.panicEnabled = panicEnabled;
        data.antiHungerEnabled = antiHungerEnabled;
        data.triggerBotEnabled = triggerBotEnabled;
        data.invMoveEnabled = invMoveEnabled;
        data.fastPlaceEnabled = fastPlaceEnabled;
        data.parkourEnabled = parkourEnabled;
        data.noSlowdownEnabled = noSlowdownEnabled;
        data.antiBlindEnabled = antiBlindEnabled;
        data.autoWalkEnabled = autoWalkEnabled;
        data.airJumpEnabled = airJumpEnabled;
        data.noWebEnabled = noWebEnabled;
        data.flightPlusEnabled = flightPlusEnabled;
        data.flightPlusSpeed = flightPlusSpeed;
        data.longJumpEnabled = longJumpEnabled;
        data.longJumpBoost = longJumpBoost;
        data.autoMLGEnabled = autoMLGEnabled;
        data.blinkEnabled = blinkEnabled;
        data.anchorAuraEnabled = anchorAuraEnabled;
        data.holeFillerEnabled = holeFillerEnabled;
        data.autoTrapEnabled = autoTrapEnabled;
        data.reversalEnabled = reversalEnabled;
        data.tpUseServerAddon = tpUseServerAddon;
        data.tpX = tpX;
        data.tpY = tpY;
        data.tpZ = tpZ;
        data.walkToCoordsEnabled = walkToCoordsEnabled;
        data.walkToX = walkToX;
        data.walkToY = walkToY;
        data.walkToZ = walkToZ;
        data.proUnlocked = proUnlocked;
        data.antiKnockbackEnabled = antiKnockbackEnabled;
        data.antiKnockbackStrength = antiKnockbackStrength;
        data.noSwingEnabled = noSwingEnabled;
        data.antiAfkEnabled = antiAfkEnabled;
        data.antiAfkInterval = antiAfkInterval;
        data.fastBreakEnabled = fastBreakEnabled;
        data.fastBreakSpeed = fastBreakSpeed;
        data.nukerEnabled = nukerEnabled;
        data.nukerRadius = nukerRadius;
        data.autoFarmEnabled = autoFarmEnabled;
        data.phaseEnabled = phaseEnabled;
        data.freecamEnabled = freecamEnabled;
        data.timerEnabled = timerEnabled;
        data.timerSpeed = timerSpeed;
        data.chestEspEnabled = chestEspEnabled;
        data.trajectoriesEnabled = trajectoriesEnabled;
        data.nametagsEnabled = nametagsEnabled;
        data.autoFishEnabled = autoFishEnabled;
        data.chestStealerEnabled = chestStealerEnabled;
        data.chestStealerDelay = chestStealerDelay;
        data.autoToolEnabled = autoToolEnabled;
        data.invSortEnabled = invSortEnabled;
        data.chatSpamEnabled = chatSpamEnabled;
        data.chatSpamMessage = chatSpamMessage;
        data.chatSpamDelay = chatSpamDelay;
        data.autoReplyEnabled = autoReplyEnabled;
        data.autoReplyMessage = autoReplyMessage;
        data.announcerEnabled = announcerEnabled;
        data.autoBridgeEnabled = autoBridgeEnabled;
        data.towerEnabled = towerEnabled;
        data.printerEnabled = printerEnabled;

        // Keybind persistence not available in this MC version (no getId/getBoundKeyTranslationKey)

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(data));
        } catch (IOException e) {
            ReachFlyClient.LOGGER.error("[f1sch] Failed to save config", e);
        }
    }

    /** Save current keybind assignments to our config so they transfer between MC versions. */
    public static void saveKeybinds() {
        // Re-save the full config which now captures keybinds
        save();
    }

    /** Apply saved keybind overrides to the registered KeyBindings. Call after KeybindHandler.register(). */
    public static void applyKeybinds() {
        if (savedKeybinds == null || savedKeybinds.isEmpty()) return;
        for (KeyMapping kb : KeybindHandler.allKeybinds()) {
            String saved = savedKeybinds.get(kb.getTranslationKey());
            if (saved != null) {
                try {
                    InputUtil.Key key = InputUtil.fromTranslationKey(saved);
                    kb.setBoundKey(key);
                } catch (Exception ignored) {}
            }
        }
        KeyMapping.updateKeysByCode();
    }

    private static Map<String, String> savedKeybinds = null;

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static class ConfigData {
        boolean reachEnabled = false;
        float reachDistance = 6.0f;
        boolean flyEnabled = false;
        float flySpeed = 1.0f;
        boolean espEnabled = false;
        boolean espPlayers = true;
        boolean espHostile = true;
        boolean espPassive = false;
        boolean espLines = false;
        boolean espPathTrace = false;
        boolean autoHitEnabled = false;
        float autoHitRange = 3.0f;
        boolean autoHitPlayersOnly = false;
        boolean killAuraEnabled = false;
        boolean killAuraPlusEnabled = false;
        int killAuraPlusCps = 20;
        boolean lowHealthKillEnabled = false;
        float lowHealthThreshold = 6.0f;
        boolean autoKillWhenLowEnabled = false;
        float autoKillSelfHpThreshold = 6.0f;
        float autoKillWhenLowRange = 4.0f;
        boolean eatingAssistEnabled = false;
        int eatingHungerThreshold = 14;
        boolean jesusEnabled = false;
        boolean autoElytraSwapEnabled = false;
        boolean flyToCoordsEnabled = false;
        float flyToX = 0;
        float flyToY = 100;
        float flyToZ = 0;
        float flyToCoordsSpeed = 2.0f;
        boolean noFallEnabled = false;
        boolean fullbrightEnabled = false;
        boolean speedEnabled = false;
        float speedMultiplier = 2.0f;
        boolean xrayEnabled = false;
        boolean knockbackEnabled = false;
        float knockbackStrength = 5.0f;
        boolean hudVisible = true;
        boolean autoTotemEnabled = false;
        boolean autoArmorEnabled = false;
        boolean scaffoldEnabled = false;
        boolean autoLogEnabled = false;
        float autoLogHealth = 4.0f;
        boolean autoRespawnEnabled = false;
        boolean betterSprintEnabled = false;
        boolean safeWalkEnabled = false;
        boolean stepEnabled = false;
        float stepHeight = 2.0f;
        boolean tpUseServerAddon = true;
        float tpX = 0;
        float tpY = 100;
        float tpZ = 0;
        boolean walkToCoordsEnabled = false;
        float walkToX = 0;
        float walkToY = 64;
        float walkToZ = 0;
        boolean proUnlocked = false;
        boolean antiKnockbackEnabled = false;
        float antiKnockbackStrength = 100.0f;
        boolean noSwingEnabled = false;
        boolean antiAfkEnabled = false;
        int antiAfkInterval = 200;
        boolean fastBreakEnabled = false;
        float fastBreakSpeed = 3.0f;
        boolean nukerEnabled = false;
        float nukerRadius = 3.0f;
        boolean autoFarmEnabled = false;
        boolean phaseEnabled = false;
        boolean freecamEnabled = false;
        boolean timerEnabled = false;
        float timerSpeed = 2.0f;
        boolean chestEspEnabled = false;
        boolean trajectoriesEnabled = false;
        boolean nametagsEnabled = false;
        boolean autoFishEnabled = false;
        boolean chestStealerEnabled = false;
        int chestStealerDelay = 3;
        boolean autoToolEnabled = false;
        boolean invSortEnabled = false;
        boolean chatSpamEnabled = false;
        String chatSpamMessage = "f1sch Pro";
        int chatSpamDelay = 100;
        boolean autoReplyEnabled = false;
        String autoReplyMessage = "I'm AFK";
        boolean announcerEnabled = false;
        boolean autoBridgeEnabled = false;
        boolean towerEnabled = false;
        boolean printerEnabled = false;
        // Meteor v2
        boolean elytraFlyEnabled = false;
        float elytraFlySpeed = 2.0f;
        boolean surroundEnabled = false;
        boolean holeEspEnabled = false;
        boolean crystalAuraEnabled = false;
        // Wurst
        boolean criticalsEnabled = false;
        boolean bunnyHopEnabled = false;
        boolean spiderEnabled = false;
        boolean glideEnabled = false;
        boolean highJumpEnabled = false;
        float highJumpHeight = 2.0f;
        boolean dolphinEnabled = false;
        boolean autoSwordEnabled = false;
        boolean sneakEnabled = false;
        boolean panicEnabled = false;
        boolean antiHungerEnabled = false;
        boolean triggerBotEnabled = false;
        // Inv Move
        boolean invMoveEnabled = false;
        // New Wurst
        boolean fastPlaceEnabled = false;
        boolean parkourEnabled = false;
        boolean noSlowdownEnabled = false;
        boolean antiBlindEnabled = false;
        boolean autoWalkEnabled = false;
        boolean airJumpEnabled = false;
        boolean noWebEnabled = false;
        boolean flightPlusEnabled = false;
        float flightPlusSpeed = 2.0f;
        boolean longJumpEnabled = false;
        float longJumpBoost = 2.0f;
        boolean autoMLGEnabled = false;
        boolean blinkEnabled = false;
        // New Meteor
        boolean anchorAuraEnabled = false;
        boolean holeFillerEnabled = false;
        boolean autoTrapEnabled = false;
        boolean reversalEnabled = false;
        // Keybind overrides (translation key -> bound key translation key)
        Map<String, String> keybinds = null;
    }
}
