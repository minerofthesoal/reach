package com.reachfly;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    // --- Auto Hit ---
    public static boolean autoHitEnabled = false;
    public static float autoHitRange = 3.0f;
    public static final float AUTO_HIT_RANGE_MIN = 1.0f;
    public static final float AUTO_HIT_RANGE_MAX = 50.0f;
    public static boolean autoHitPlayersOnly = false;

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

    // --- Shield Assist ---
    public static boolean shieldAssistEnabled = false;

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

    // --- HUD ---
    public static boolean hudVisible = true;

    // --- Walk to Coords ---
    public static boolean walkToCoordsEnabled = false;
    public static float walkToX = 0;
    public static float walkToY = 64;
    public static float walkToZ = 0;

    // --- Dupe ---
    public static boolean dupeEnabled = false;

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
                    autoHitEnabled = data.autoHitEnabled;
                    autoHitRange = clamp(data.autoHitRange, AUTO_HIT_RANGE_MIN, AUTO_HIT_RANGE_MAX);
                    autoHitPlayersOnly = data.autoHitPlayersOnly;
                    lowHealthKillEnabled = data.lowHealthKillEnabled;
                    lowHealthThreshold = clamp(data.lowHealthThreshold, LOW_HEALTH_MIN, LOW_HEALTH_MAX);
                    autoKillWhenLowEnabled = data.autoKillWhenLowEnabled;
                    autoKillSelfHpThreshold = clamp(data.autoKillSelfHpThreshold, AUTO_KILL_SELF_HP_MIN, AUTO_KILL_SELF_HP_MAX);
                    autoKillWhenLowRange = clamp(data.autoKillWhenLowRange, AUTO_KILL_RANGE_MIN, AUTO_KILL_RANGE_MAX);
                    eatingAssistEnabled = data.eatingAssistEnabled;
                    eatingHungerThreshold = (int) clamp(data.eatingHungerThreshold, EATING_HUNGER_MIN, EATING_HUNGER_MAX);
                    shieldAssistEnabled = data.shieldAssistEnabled;
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
                    hudVisible = data.hudVisible;
                    walkToCoordsEnabled = data.walkToCoordsEnabled;
                    walkToX = data.walkToX;
                    walkToY = data.walkToY;
                    walkToZ = data.walkToZ;
                    dupeEnabled = data.dupeEnabled;
                }
                ReachFlyClient.LOGGER.info("[ReachFly] Config loaded.");
            } catch (IOException e) {
                ReachFlyClient.LOGGER.error("[ReachFly] Failed to load config", e);
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
        data.autoHitEnabled = autoHitEnabled;
        data.autoHitRange = autoHitRange;
        data.autoHitPlayersOnly = autoHitPlayersOnly;
        data.lowHealthKillEnabled = lowHealthKillEnabled;
        data.lowHealthThreshold = lowHealthThreshold;
        data.autoKillWhenLowEnabled = autoKillWhenLowEnabled;
        data.autoKillSelfHpThreshold = autoKillSelfHpThreshold;
        data.autoKillWhenLowRange = autoKillWhenLowRange;
        data.eatingAssistEnabled = eatingAssistEnabled;
        data.eatingHungerThreshold = eatingHungerThreshold;
        data.shieldAssistEnabled = shieldAssistEnabled;
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
        data.hudVisible = hudVisible;
        data.walkToCoordsEnabled = walkToCoordsEnabled;
        data.walkToX = walkToX;
        data.walkToY = walkToY;
        data.walkToZ = walkToZ;
        data.dupeEnabled = dupeEnabled;

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(data));
        } catch (IOException e) {
            ReachFlyClient.LOGGER.error("[ReachFly] Failed to save config", e);
        }
    }

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
        boolean autoHitEnabled = false;
        float autoHitRange = 3.0f;
        boolean autoHitPlayersOnly = false;
        boolean lowHealthKillEnabled = false;
        float lowHealthThreshold = 6.0f;
        boolean autoKillWhenLowEnabled = false;
        float autoKillSelfHpThreshold = 6.0f;
        float autoKillWhenLowRange = 4.0f;
        boolean eatingAssistEnabled = false;
        int eatingHungerThreshold = 14;
        boolean shieldAssistEnabled = false;
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
        boolean hudVisible = true;
        boolean walkToCoordsEnabled = false;
        float walkToX = 0;
        float walkToY = 64;
        float walkToZ = 0;
        boolean dupeEnabled = false;
    }
}
