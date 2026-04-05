package com.reachfly;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

/**
 * Registers tick and render event callbacks.
 * Handles keybind processing and dispatches to all feature handlers.
 */
public class EventHandler {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(EventHandler::onClientTick);
        HudRenderCallback.EVENT.register(HudOverlay::render);
        EspRenderer.register();
    }

    private static void onClientTick(MinecraftClient client) {
        if (client.player == null) return;

        // --- Toggle Reach ---
        while (KeybindHandler.toggleReach.wasPressed()) {
            ModConfig.reachEnabled = !ModConfig.reachEnabled;
            ModConfig.save();
            ReachFlyClient.LOGGER.info("[ReachFly] Reach: {}",
                    ModConfig.reachEnabled ? "ON (" + ModConfig.reachDistance + ")" : "OFF");
        }

        // --- Toggle Fly ---
        while (KeybindHandler.toggleFly.wasPressed()) {
            ModConfig.flyEnabled = !ModConfig.flyEnabled;
            ModConfig.save();
            if (!client.player.getAbilities().creativeMode) {
                client.player.getAbilities().allowFlying = ModConfig.flyEnabled;
                if (!ModConfig.flyEnabled) {
                    client.player.getAbilities().flying = false;
                }
                client.player.sendAbilitiesUpdate();
            }
            ReachFlyClient.LOGGER.info("[ReachFly] Fly: {}",
                    ModConfig.flyEnabled ? "ON (speed: " + ModConfig.flySpeed + ")" : "OFF");
        }

        // --- Toggle ESP ---
        while (KeybindHandler.toggleEsp.wasPressed()) {
            ModConfig.espEnabled = !ModConfig.espEnabled;
            ModConfig.save();
            ReachFlyClient.LOGGER.info("[ReachFly] ESP: {}", ModConfig.espEnabled ? "ON" : "OFF");
        }

        // --- Toggle Auto Hit ---
        while (KeybindHandler.toggleAutoHit.wasPressed()) {
            ModConfig.autoHitEnabled = !ModConfig.autoHitEnabled;
            ModConfig.save();
            ReachFlyClient.LOGGER.info("[ReachFly] Auto Hit: {}", ModConfig.autoHitEnabled ? "ON" : "OFF");
        }

        // --- Toggle Low Health Kill ---
        while (KeybindHandler.toggleLowHealthKill.wasPressed()) {
            ModConfig.lowHealthKillEnabled = !ModConfig.lowHealthKillEnabled;
            ModConfig.save();
            ReachFlyClient.LOGGER.info("[ReachFly] Low Health Kill: {}", ModConfig.lowHealthKillEnabled ? "ON" : "OFF");
        }

        // --- Toggle Eating Assist ---
        while (KeybindHandler.toggleEatingAssist.wasPressed()) {
            ModConfig.eatingAssistEnabled = !ModConfig.eatingAssistEnabled;
            ModConfig.save();
            ReachFlyClient.LOGGER.info("[ReachFly] Eating Assist: {}", ModConfig.eatingAssistEnabled ? "ON" : "OFF");
        }

        // --- Toggle Shield Assist ---
        while (KeybindHandler.toggleShieldAssist.wasPressed()) {
            ModConfig.shieldAssistEnabled = !ModConfig.shieldAssistEnabled;
            ModConfig.save();
            ReachFlyClient.LOGGER.info("[ReachFly] Shield Assist: {}", ModConfig.shieldAssistEnabled ? "ON" : "OFF");
        }

        // --- Toggle Auto Kill When Low ---
        while (KeybindHandler.toggleAutoKillWhenLow.wasPressed()) {
            ModConfig.autoKillWhenLowEnabled = !ModConfig.autoKillWhenLowEnabled;
            ModConfig.save();
            ReachFlyClient.LOGGER.info("[ReachFly] Auto Kill When Low: {}", ModConfig.autoKillWhenLowEnabled ? "ON" : "OFF");
        }

        // --- Toggle Dupe ---
        while (KeybindHandler.toggleDupe.wasPressed()) {
            ModConfig.dupeEnabled = !ModConfig.dupeEnabled;
            ModConfig.save();
            ReachFlyClient.LOGGER.info("[ReachFly] Dupe: {}", ModConfig.dupeEnabled ? "ON" : "OFF");
        }

        // --- Open Config Screen ---
        while (KeybindHandler.openConfig.wasPressed()) {
            client.setScreen(new ConfigScreen(client.currentScreen));
        }

        // --- Run all feature tick handlers ---
        FlyHandler.tick(client);
        AutoHitHandler.tick(client);
        LowHealthKillHandler.tick(client);
        EatingAssistHandler.tick(client);
        ShieldAssistHandler.tick(client);
        AutoKillWhenLowHandler.tick(client);
        DupeHandler.tick(client);
    }
}
