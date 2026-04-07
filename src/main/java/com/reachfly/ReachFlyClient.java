package com.reachfly;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client-side mod initializer for f1sch client.
 * Registers keybinds, config, and event handlers.
 */
public class ReachFlyClient implements ClientModInitializer {

    public static final String MOD_ID = "reachfly";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("[f1sch] Initializing f1sch client...");

        // Load saved config from disk
        ModConfig.load();

        // Register all keybinds
        KeybindHandler.register();

        // Register networking payloads
        TeleportHandler.registerPayload();
        ServerSyncHandler.registerPayloads();

        // Register tick and render event handlers
        EventHandler.register();

        LOGGER.info("[f1sch] Initialization complete.");
    }
}
