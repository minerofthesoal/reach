package com.reachfly;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client-side mod initializer for Reach & Fly mod.
 * Registers keybinds, config, and event handlers.
 */
public class ReachFlyClient implements ClientModInitializer {

    public static final String MOD_ID = "reachfly";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("[ReachFly] Initializing Reach & Fly Mod...");

        // Load saved config from disk
        ModConfig.load();

        // Register all keybinds
        KeybindHandler.register();

        // Register tick and render event handlers
        EventHandler.register();

        LOGGER.info("[ReachFly] Initialization complete.");
    }
}
