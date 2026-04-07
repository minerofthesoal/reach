package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;

/**
 * Knockback hack - Applies ATTACK_KNOCKBACK attribute modifier on both client
 * and server. Combined with the mixin that directly sets velocity + velocityDirty
 * on the server entity, this produces massive knockback in singleplayer.
 *
 * Uses the same "check before update" pattern as ReachHandler to avoid
 * modifier flickering.
 */
public class KnockbackHandler {

    private static final ResourceLocation KNOCKBACK_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "knockback_boost");

    private static int tickCounter = 0;
    private static boolean lastEnabled = false;
    private static float lastStrength = 0;

    public static void tick(Minecraft client) {
        if (client.player == null) return;

        boolean needsUpdate = (ModConfig.knockbackEnabled != lastEnabled)
                || (ModConfig.knockbackEnabled && ModConfig.knockbackStrength != lastStrength);

        tickCounter++;
        if (tickCounter >= 40) {
            tickCounter = 0;
            if (ModConfig.knockbackEnabled) needsUpdate = true;
        }

        if (needsUpdate) {
            lastEnabled = ModConfig.knockbackEnabled;
            lastStrength = ModConfig.knockbackStrength;
            updateKnockbackAttributes();
        }
    }

    public static void updateKnockbackAttributes() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        LocalPlayer player = client.player;
        applyToPlayer(player);

        MinecraftServer server = client.getSingleplayerServer();
        if (server != null) {
            ServerPlayer serverPlayer = server.getPlayerList()
                    .getPlayer(player.getUUID());
            if (serverPlayer != null) {
                applyToPlayer(serverPlayer);
            }
        }
    }

    private static void applyToPlayer(net.minecraft.world.entity.LivingEntity player) {
        AttributeInstance knockback = player.getAttribute(
                Attributes.ATTACK_KNOCKBACK);
        if (knockback == null) return;

        if (ModConfig.knockbackEnabled) {
            double boost = ModConfig.knockbackStrength;

            // Check if modifier already exists with correct value
            AttributeModifier existing = knockback.getModifier(KNOCKBACK_ID);
            if (existing == null || existing.value() != boost) {
                knockback.removeModifier(KNOCKBACK_ID);
                knockback.addTemporaryModifier(new AttributeModifier(
                        KNOCKBACK_ID, boost,
                        AttributeModifier.Operation.ADD_VALUE));
            }
        } else {
            knockback.removeModifier(KNOCKBACK_ID);
        }
    }

    public static void clearKnockbackModifiers() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        clearForPlayer(client.player);

        MinecraftServer server = client.getSingleplayerServer();
        if (server != null) {
            ServerPlayer serverPlayer = server.getPlayerList()
                    .getPlayer(client.player.getUUID());
            if (serverPlayer != null) {
                clearForPlayer(serverPlayer);
            }
        }
    }

    private static void clearForPlayer(net.minecraft.world.entity.LivingEntity player) {
        AttributeInstance knockback = player.getAttribute(
                Attributes.ATTACK_KNOCKBACK);
        if (knockback != null) knockback.removeModifier(KNOCKBACK_ID);
    }
}
