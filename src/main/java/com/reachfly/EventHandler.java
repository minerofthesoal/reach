package com.reachfly;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class EventHandler {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(EventHandler::onClientTick);
        HudElementRegistry.addLast(ResourceLocation.fromNamespaceAndPath("reachfly", "hud_overlay"), HudOverlay::render);
        EspRenderer.register();
    }

    private static void onClientTick(Minecraft client) {
        if (client.player == null) return;

        while (KeybindHandler.toggleHud.consumeClick()) {
            ModConfig.hudVisible = !ModConfig.hudVisible;
            ModConfig.save();
        }

        while (KeybindHandler.toggleReach.consumeClick()) {
            ModConfig.reachEnabled = !ModConfig.reachEnabled;
            ReachHandler.updateReachAttributes();
            ModConfig.save();
        }

        while (KeybindHandler.toggleFly.consumeClick()) {
            ModConfig.flyEnabled = !ModConfig.flyEnabled;
            ModConfig.save();
            if (!client.player.getAbilities().instabuild) {
                client.player.getAbilities().mayFly = ModConfig.flyEnabled;
                if (!ModConfig.flyEnabled) {
                    client.player.getAbilities().flying = false;
                }
                client.player.onUpdateAbilities();
            }
        }

        while (KeybindHandler.toggleEsp.consumeClick()) {
            ModConfig.espEnabled = !ModConfig.espEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleAutoHit.consumeClick()) {
            ModConfig.autoHitEnabled = !ModConfig.autoHitEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleLowHealthKill.consumeClick()) {
            ModConfig.lowHealthKillEnabled = !ModConfig.lowHealthKillEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleEatingAssist.consumeClick()) {
            ModConfig.eatingAssistEnabled = !ModConfig.eatingAssistEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleAutoKillWhenLow.consumeClick()) {
            ModConfig.autoKillWhenLowEnabled = !ModConfig.autoKillWhenLowEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleJesus.consumeClick()) {
            ModConfig.jesusEnabled = !ModConfig.jesusEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleAutoElytraSwap.consumeClick()) {
            ModConfig.autoElytraSwapEnabled = !ModConfig.autoElytraSwapEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleFlyToCoords.consumeClick()) {
            ModConfig.flyToCoordsEnabled = !ModConfig.flyToCoordsEnabled;
            if (!ModConfig.flyToCoordsEnabled) FlyToCoordsHandler.onDisable();
            ModConfig.save();
        }

        while (KeybindHandler.toggleNoFall.consumeClick()) {
            ModConfig.noFallEnabled = !ModConfig.noFallEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleFullbright.consumeClick()) {
            ModConfig.fullbrightEnabled = !ModConfig.fullbrightEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleSpeed.consumeClick()) {
            ModConfig.speedEnabled = !ModConfig.speedEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleWalkToCoords.consumeClick()) {
            ModConfig.walkToCoordsEnabled = !ModConfig.walkToCoordsEnabled;
            if (!ModConfig.walkToCoordsEnabled) WalkToCoordsHandler.onDisable();
            ModConfig.save();
        }

        while (KeybindHandler.toggleXray.consumeClick()) {
            ModConfig.xrayEnabled = !ModConfig.xrayEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleKnockback.consumeClick()) {
            ModConfig.knockbackEnabled = !ModConfig.knockbackEnabled;
            KnockbackHandler.updateKnockbackAttributes();
            ModConfig.save();
        }

        while (KeybindHandler.toggleAutoTotem.consumeClick()) {
            ModConfig.autoTotemEnabled = !ModConfig.autoTotemEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleAutoArmor.consumeClick()) {
            ModConfig.autoArmorEnabled = !ModConfig.autoArmorEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.toggleScaffold.consumeClick()) {
            ModConfig.scaffoldEnabled = !ModConfig.scaffoldEnabled;
            ModConfig.save();
        }

        while (KeybindHandler.triggerTeleport.consumeClick()) {
            TeleportHandler.triggerTeleport();
        }

        while (KeybindHandler.openConfig.consumeClick()) {
            client.setScreen(new ConfigScreen(client.screen));
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
        MeteorHandlers.tick(client);
        ProHandlers.tick(client);
        FreecamHandler.tick(client);
        WurstHandlers.tick(client);
        MeteorV2Handlers.tick(client);
        ServerSyncHandler.tick(client);
    }
}
