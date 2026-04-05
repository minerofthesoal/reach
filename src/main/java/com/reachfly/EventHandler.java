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

        while (KeybindHandler.toggleHud.wasPressed()) {
            ModConfig.hudVisible = !ModConfig.hudVisible;
            ModConfig.save();
        }

        while (KeybindHandler.toggleReach.wasPressed()) {
            ModConfig.reachEnabled = !ModConfig.reachEnabled;
            ReachHandler.updateReachAttributes();
            ModConfig.save();
        }

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
        }

        while (KeybindHandler.toggleEsp.wasPressed()) {
            ModConfig.espEnabled = !ModConfig.espEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleAutoHit.wasPressed()) {
            ModConfig.autoHitEnabled = !ModConfig.autoHitEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleLowHealthKill.wasPressed()) {
            ModConfig.lowHealthKillEnabled = !ModConfig.lowHealthKillEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleEatingAssist.wasPressed()) {
            ModConfig.eatingAssistEnabled = !ModConfig.eatingAssistEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleAutoKillWhenLow.wasPressed()) {
            ModConfig.autoKillWhenLowEnabled = !ModConfig.autoKillWhenLowEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleJesus.wasPressed()) {
            ModConfig.jesusEnabled = !ModConfig.jesusEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleAutoElytraSwap.wasPressed()) {
            ModConfig.autoElytraSwapEnabled = !ModConfig.autoElytraSwapEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleFlyToCoords.wasPressed()) {
            ModConfig.flyToCoordsEnabled = !ModConfig.flyToCoordsEnabled;
            if (!ModConfig.flyToCoordsEnabled) FlyToCoordsHandler.onDisable();
            ModConfig.save();
        }

        while (KeybindHandler.toggleNoFall.wasPressed()) {
            ModConfig.noFallEnabled = !ModConfig.noFallEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleFullbright.wasPressed()) {
            ModConfig.fullbrightEnabled = !ModConfig.fullbrightEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleSpeed.wasPressed()) {
            ModConfig.speedEnabled = !ModConfig.speedEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleWalkToCoords.wasPressed()) {
            ModConfig.walkToCoordsEnabled = !ModConfig.walkToCoordsEnabled;
            if (!ModConfig.walkToCoordsEnabled) WalkToCoordsHandler.onDisable();
            ModConfig.save();
        }

        while (KeybindHandler.toggleXray.wasPressed()) {
            ModConfig.xrayEnabled = !ModConfig.xrayEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleKnockback.wasPressed()) {
            ModConfig.knockbackEnabled = !ModConfig.knockbackEnabled;
            KnockbackHandler.updateKnockbackAttributes();
            ModConfig.save();
        }

        while (KeybindHandler.toggleAutoTotem.wasPressed()) {
            ModConfig.autoTotemEnabled = !ModConfig.autoTotemEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleAutoArmor.wasPressed()) {
            ModConfig.autoArmorEnabled = !ModConfig.autoArmorEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleScaffold.wasPressed()) {
            ModConfig.scaffoldEnabled = !ModConfig.scaffoldEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.triggerTeleport.wasPressed()) {
            TeleportHandler.triggerTeleport();
        }

        while (KeybindHandler.openConfig.wasPressed()) {
            client.setScreen(new ConfigScreen(client.currentScreen));
        }

        // Run feature tick handlers
        ReachHandler.tick(client);
        FlyHandler.tick(client);
        AutoHitHandler.tick(client);
        LowHealthKillHandler.tick(client);
        AutoKillWhenLowHandler.tick(client);
        EatingAssistHandler.tick(client);
        JesusHandler.tick(client);
        AutoElytraSwapHandler.tick(client);
        FlyToCoordsHandler.tick(client);
        NoFallHandler.tick(client);
        FullbrightHandler.tick(client);
        SpeedHandler.tick(client);
        XrayHandler.tick(client);
        KnockbackHandler.tick(client);
        WalkToCoordsHandler.tick(client);
        AutoTotemHandler.tick(client);
        AutoArmorHandler.tick(client);
        ScaffoldHandler.tick(client);
        TeleportHandler.tick(client);
        ProHandlers.tick(client);
    }
}
