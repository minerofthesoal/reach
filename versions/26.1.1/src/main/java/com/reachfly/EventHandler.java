package com.reachfly;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class EventHandler {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(EventHandler::onClientTick);
        HudRenderCallback.EVENT.register(HudOverlay::render);
        EspRenderer.register();
    }

    private static void onClientTick(Minecraft minecraft) {
        if (minecraft.player == null) return;

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
            if (!minecraft.player.getAbilities().instabuild) {
                minecraft.player.getAbilities().mayFly = ModConfig.flyEnabled;
                if (!ModConfig.flyEnabled) {
                    minecraft.player.getAbilities().flying = false;
                }
                minecraft.player.onUpdateAbilities();
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
            minecraft.setScreen(new ConfigScreen(minecraft.screen));
        }

        // Run feature tick handlers
        ReachHandler.tick(minecraft);
        FlyHandler.tick(minecraft);
        AutoHitHandler.tick(minecraft);
        LowHealthKillHandler.tick(minecraft);
        AutoKillWhenLowHandler.tick(minecraft);
        EatingAssistHandler.tick(minecraft);
        JesusHandler.tick(minecraft);
        AutoElytraSwapHandler.tick(minecraft);
        FlyToCoordsHandler.tick(minecraft);
        NoFallHandler.tick(minecraft);
        FullbrightHandler.tick(minecraft);
        SpeedHandler.tick(minecraft);
        XrayHandler.tick(minecraft);
        KnockbackHandler.tick(minecraft);
        WalkToCoordsHandler.tick(minecraft);
        AutoTotemHandler.tick(minecraft);
        AutoArmorHandler.tick(minecraft);
        ScaffoldHandler.tick(minecraft);
        TeleportHandler.tick(minecraft);
        MeteorHandlers.tick(minecraft);
        ProHandlers.tick(minecraft);
        FreecamHandler.tick(minecraft);
        WurstHandlers.tick(minecraft);
        MeteorV2Handlers.tick(minecraft);
        ServerSyncHandler.tick(minecraft);
    }
}
