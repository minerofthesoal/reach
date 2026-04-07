package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MeteorHandlers {

    private static final ResourceLocation STEP_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "step_height");

    public static void tick(Minecraft minecraft) {
        if (minecraft.player == null) return;
        tickAutoLog(minecraft);
        tickAutoRespawn(minecraft);
        tickBetterSprint(minecraft);
        tickSafeWalk(minecraft);
        tickStep(minecraft);
    }

    private static void tickAutoLog(Minecraft minecraft) {
        if (!ModConfig.autoLogEnabled) return;
        LocalPlayer p = minecraft.player;
        if (p == null || p.isDeadOrDying()) return;
        if (p.getHealth() <= ModConfig.autoLogHealth) {
            ModConfig.autoLogEnabled = false;
            ModConfig.save();
            p.sendSystemMessage(Component.literal("\u00a7c[OSP] Auto Log: disconnecting at " +
                    String.format("%.1f HP", p.getHealth())), false);
            minecraft.getConnection().getConnection().disconnect(
                    Component.literal("OSP Auto Log - Health below " + String.format("%.1f", ModConfig.autoLogHealth)));
        }
    }

    private static void tickAutoRespawn(Minecraft minecraft) {
        if (!ModConfig.autoRespawnEnabled) return;
        if (minecraft.player != null && minecraft.player.isDeadOrDying()) {
            minecraft.player.respawn();
        }
    }

    private static void tickBetterSprint(Minecraft minecraft) {
        if (!ModConfig.betterSprintEnabled) return;
        LocalPlayer p = minecraft.player;
        if (p == null) return;
        if (minecraft.options.keyUp.isDown() && !p.isShiftKeyDown() && !p.isUsingItem()
                && p.getFoodData().getFoodLevel() > 6) {
            p.setSprinting(true);
        }
    }

    private static void tickSafeWalk(Minecraft minecraft) {
        // SafeWalk is now handled by SafeWalkMixin using clipAtLedge()
        // No tick logic needed - the mixin provides edge-clipping without speed reduction
    }

    private static boolean stepApplied = false;

    private static void tickStep(Minecraft minecraft) {
        LocalPlayer p = minecraft.player;
        if (p == null) return;
        AttributeInstance stepAttr = p.getAttribute(Attributes.STEP_HEIGHT);
        if (stepAttr == null) return;

        if (ModConfig.stepEnabled && !stepApplied) {
            stepAttr.removeModifier(STEP_ID);
            stepAttr.addTransientModifier(new AttributeModifier(
                    STEP_ID, ModConfig.stepHeight - 0.6, AttributeModifier.Operation.ADD_VALUE));
            stepApplied = true;
        } else if (!ModConfig.stepEnabled && stepApplied) {
            stepAttr.removeModifier(STEP_ID);
            stepApplied = false;
        }
    }
}
