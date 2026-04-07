package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Knockback hack - Applies ATTACK_KNOCKBACK attribute modifier on both client
 * and server. Combined with the mixin that directly sets velocity + velocityDirty
 * on the server entity, this produces massive knockback in singleplayer.
 *
 * Uses the same "check before update" pattern as ReachHandler to avoid
 * modifier flickering.
 */
public class KnockbackHandler {

    private static final Identifier KNOCKBACK_ID = Identifier.of("reachfly", "knockback_boost");

    private static int tickCounter = 0;
    private static boolean lastEnabled = false;
    private static float lastStrength = 0;

    public static void tick(MinecraftClient client) {
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
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        ClientPlayerEntity player = client.player;
        applyToPlayer(player);

        MinecraftServer server = client.getServer();
        if (server != null) {
            ServerPlayerEntity serverPlayer = server.getPlayerManager()
                    .getPlayer(player.getUuid());
            if (serverPlayer != null) {
                applyToPlayer(serverPlayer);
            }
        }
    }

    private static void applyToPlayer(net.minecraft.entity.LivingEntity player) {
        EntityAttributeInstance knockback = player.getAttributeInstance(
                EntityAttributes.ATTACK_KNOCKBACK);
        if (knockback == null) return;

        if (ModConfig.knockbackEnabled) {
            double boost = ModConfig.knockbackStrength;

            // Check if modifier already exists with correct value
            EntityAttributeModifier existing = knockback.getModifier(KNOCKBACK_ID);
            if (existing == null || existing.value() != boost) {
                knockback.removeModifier(KNOCKBACK_ID);
                knockback.addTemporaryModifier(new EntityAttributeModifier(
                        KNOCKBACK_ID, boost,
                        EntityAttributeModifier.Operation.ADD_VALUE));
            }
        } else {
            knockback.removeModifier(KNOCKBACK_ID);
        }
    }

    public static void clearKnockbackModifiers() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        clearForPlayer(client.player);

        MinecraftServer server = client.getServer();
        if (server != null) {
            ServerPlayerEntity serverPlayer = server.getPlayerManager()
                    .getPlayer(client.player.getUuid());
            if (serverPlayer != null) {
                clearForPlayer(serverPlayer);
            }
        }
    }

    private static void clearForPlayer(net.minecraft.entity.LivingEntity player) {
        EntityAttributeInstance knockback = player.getAttributeInstance(
                EntityAttributes.ATTACK_KNOCKBACK);
        if (knockback != null) knockback.removeModifier(KNOCKBACK_ID);
    }
}
