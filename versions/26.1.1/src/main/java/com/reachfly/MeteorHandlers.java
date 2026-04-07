package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MeteorHandlers {

    private static final Identifier STEP_ID = Identifier.of("reachfly", "step_height");

    public static void tick(MinecraftClient client) {
        if (client.player == null) return;
        tickAutoLog(client);
        tickAutoRespawn(client);
        tickBetterSprint(client);
        tickSafeWalk(client);
        tickStep(client);
    }

    private static void tickAutoLog(MinecraftClient client) {
        if (!ModConfig.autoLogEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || p.isDead()) return;
        if (p.getHealth() <= ModConfig.autoLogHealth) {
            ModConfig.autoLogEnabled = false;
            ModConfig.save();
            p.sendMessage(Text.literal("\u00a7c[OSP] Auto Log: disconnecting at " +
                    String.format("%.1f HP", p.getHealth())), false);
            client.getNetworkHandler().getConnection().disconnect(
                    Text.literal("OSP Auto Log - Health below " + String.format("%.1f", ModConfig.autoLogHealth)));
        }
    }

    private static void tickAutoRespawn(MinecraftClient client) {
        if (!ModConfig.autoRespawnEnabled) return;
        if (client.player != null && client.player.isDead()) {
            client.player.requestRespawn();
        }
    }

    private static void tickBetterSprint(MinecraftClient client) {
        if (!ModConfig.betterSprintEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;
        if (client.options.forwardKey.isPressed() && !p.isSneaking() && !p.isUsingItem()
                && p.getHungerManager().getFoodLevel() > 6) {
            p.setSprinting(true);
        }
    }

    private static void tickSafeWalk(MinecraftClient client) {
        // SafeWalk is now handled by SafeWalkMixin using clipAtLedge()
        // No tick logic needed - the mixin provides edge-clipping without speed reduction
    }

    private static boolean stepApplied = false;

    private static void tickStep(MinecraftClient client) {
        ClientPlayerEntity p = client.player;
        if (p == null) return;
        EntityAttributeInstance stepAttr = p.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (stepAttr == null) return;

        if (ModConfig.stepEnabled && !stepApplied) {
            stepAttr.removeModifier(STEP_ID);
            stepAttr.addTemporaryModifier(new EntityAttributeModifier(
                    STEP_ID, ModConfig.stepHeight - 0.6, EntityAttributeModifier.Operation.ADD_VALUE));
            stepApplied = true;
        } else if (!ModConfig.stepEnabled && stepApplied) {
            stepAttr.removeModifier(STEP_ID);
            stepApplied = false;
        }
    }
}
