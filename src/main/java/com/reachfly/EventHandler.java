package com.reachfly;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

/**
 * Registers tick and render event callbacks.
 * Handles keybind processing, fly logic, and HUD rendering.
 */
public class EventHandler {

    public static void register() {
        // Process keybinds and fly logic every client tick
        ClientTickEvents.END_CLIENT_TICK.register(EventHandler::onClientTick);

        // Render HUD overlay
        HudRenderCallback.EVENT.register(HudOverlay::render);
    }

    /**
     * Called every client tick. Processes keybind presses and applies fly movement.
     */
    private static void onClientTick(MinecraftClient client) {
        if (client.player == null) return;

        // --- Toggle Reach ---
        while (KeybindHandler.toggleReach.wasPressed()) {
            ModConfig.reachEnabled = !ModConfig.reachEnabled;
            ModConfig.save();
            ReachFlyClient.LOGGER.info("[ReachFly] Reach: {}",
                    ModConfig.reachEnabled ? "ON (" + ModConfig.reachDistance + " blocks)" : "OFF");
        }

        // --- Toggle Fly ---
        while (KeybindHandler.toggleFly.wasPressed()) {
            ModConfig.flyEnabled = !ModConfig.flyEnabled;
            ModConfig.save();

            // Update flight ability
            if (client.player.getAbilities().creativeMode) {
                // Don't interfere with creative mode flight
            } else {
                client.player.getAbilities().allowFlying = ModConfig.flyEnabled;
                if (!ModConfig.flyEnabled) {
                    client.player.getAbilities().flying = false;
                }
                client.player.sendAbilitiesUpdate();
            }

            ReachFlyClient.LOGGER.info("[ReachFly] Fly: {}",
                    ModConfig.flyEnabled ? "ON (speed: " + ModConfig.flySpeed + ")" : "OFF");
        }

        // --- Open Config Screen ---
        while (KeybindHandler.openConfig.wasPressed()) {
            client.setScreen(new ConfigScreen(client.currentScreen));
        }

        // --- Apply fly logic each tick ---
        FlyHandler.tick(client);
    }
}
