package com.reachfly;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class EventHandler {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(EventHandler::onClientTick);
        HudRenderCallback.EVENT.register(HudOverlay::render);
        EspRenderer.register();
    }

    private static void onClientTick(MinecraftClient client) {
        if (client.player == null) return;

        // --- Toggle HUD ---
        while (KeybindHandler.toggleHud.wasPressed()) {
            ModConfig.hudVisible = !ModConfig.hudVisible;
            ModConfig.save();
        }

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
        }

        // --- Toggle Auto Hit ---
        while (KeybindHandler.toggleAutoHit.wasPressed()) {
            ModConfig.autoHitEnabled = !ModConfig.autoHitEnabled;
            ModConfig.save();
        }

        // --- Toggle Low Health Kill ---
        while (KeybindHandler.toggleLowHealthKill.wasPressed()) {
            ModConfig.lowHealthKillEnabled = !ModConfig.lowHealthKillEnabled;
            ModConfig.save();
        }

        // --- Toggle Eating Assist ---
        while (KeybindHandler.toggleEatingAssist.wasPressed()) {
            ModConfig.eatingAssistEnabled = !ModConfig.eatingAssistEnabled;
            ModConfig.save();
        }

        // --- Toggle Shield Assist ---
        while (KeybindHandler.toggleShieldAssist.wasPressed()) {
            ModConfig.shieldAssistEnabled = !ModConfig.shieldAssistEnabled;
            ModConfig.save();
        }

        // --- Toggle Auto Kill When Low ---
        while (KeybindHandler.toggleAutoKillWhenLow.wasPressed()) {
            ModConfig.autoKillWhenLowEnabled = !ModConfig.autoKillWhenLowEnabled;
            ModConfig.save();
        }

        // --- Toggle Jesus ---
        while (KeybindHandler.toggleJesus.wasPressed()) {
            ModConfig.jesusEnabled = !ModConfig.jesusEnabled;
            ModConfig.save();
        }

        // --- Toggle Auto Elytra Swap ---
        while (KeybindHandler.toggleAutoElytraSwap.wasPressed()) {
            ModConfig.autoElytraSwapEnabled = !ModConfig.autoElytraSwapEnabled;
            ModConfig.save();
        }

        // --- Toggle Fly to Coords ---
        while (KeybindHandler.toggleFlyToCoords.wasPressed()) {
            ModConfig.flyToCoordsEnabled = !ModConfig.flyToCoordsEnabled;
            if (!ModConfig.flyToCoordsEnabled) {
                FlyToCoordsHandler.onDisable();
            }
            ModConfig.save();
        }

        // --- Toggle NoFall ---
        while (KeybindHandler.toggleNoFall.wasPressed()) {
            ModConfig.noFallEnabled = !ModConfig.noFallEnabled;
            ModConfig.save();
        }

        // --- Toggle Fullbright ---
        while (KeybindHandler.toggleFullbright.wasPressed()) {
            ModConfig.fullbrightEnabled = !ModConfig.fullbrightEnabled;
            ModConfig.save();
        }

        // --- Toggle Speed ---
        while (KeybindHandler.toggleSpeed.wasPressed()) {
            ModConfig.speedEnabled = !ModConfig.speedEnabled;
            ModConfig.save();
        }

        // --- Toggle Dupe ---
        while (KeybindHandler.toggleDupe.wasPressed()) {
            ModConfig.dupeEnabled = !ModConfig.dupeEnabled;
            ModConfig.save();
        }

        // --- Open Config Screen ---
        while (KeybindHandler.openConfig.wasPressed()) {
            client.setScreen(new ConfigScreen(client.currentScreen));
        }

        // --- Run all feature tick handlers ---
        FlyHandler.tick(client);
        AutoHitHandler.tick(client);
        LowHealthKillHandler.tick(client);
        AutoKillWhenLowHandler.tick(client);
        EatingAssistHandler.tick(client);
        ShieldAssistHandler.tick(client);
        JesusHandler.tick(client);
        AutoElytraSwapHandler.tick(client);
        FlyToCoordsHandler.tick(client);
        NoFallHandler.tick(client);
        FullbrightHandler.tick(client);
        SpeedHandler.tick(client);
        DupeHandler.tick(client);
    }
}
