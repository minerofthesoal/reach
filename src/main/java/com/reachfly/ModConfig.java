package com.reachfly;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuration system for the Reach & Fly mod.
 * Persists settings to a JSON file in the config directory.
 */
public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("reachfly.json");

    // --- Reach settings ---
    public static boolean reachEnabled = false;
    public static float reachDistance = 6.0f;
    public static final float REACH_MIN = 3.0f;
    public static final float REACH_MAX = 50.0f;

    // --- Fly settings ---
    public static boolean flyEnabled = false;
    public static float flySpeed = 1.0f;
    public static final float FLY_SPEED_MIN = 0.1f;
    public static final float FLY_SPEED_MAX = 10.0f;

    /**
     * Load configuration from disk. If the file doesn't exist, defaults are used
     * and a new config file is created.
     */
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
                }
                ReachFlyClient.LOGGER.info("[ReachFly] Config loaded.");
            } catch (IOException e) {
                ReachFlyClient.LOGGER.error("[ReachFly] Failed to load config", e);
            }
        } else {
            save(); // Create default config file
        }
    }

    /**
     * Save current configuration to disk.
     */
    public static void save() {
        ConfigData data = new ConfigData();
        data.reachEnabled = reachEnabled;
        data.reachDistance = reachDistance;
        data.flyEnabled = flyEnabled;
        data.flySpeed = flySpeed;

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

    /**
     * Internal data class for JSON serialization.
     */
    private static class ConfigData {
        boolean reachEnabled = false;
        float reachDistance = 6.0f;
        boolean flyEnabled = false;
        float flySpeed = 1.0f;
    }
}
