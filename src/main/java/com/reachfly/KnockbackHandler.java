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
 * Knockback hack - Applies the knockback attribute to the player so all attacks
 * deal massive knockback. Works by modifying ATTACK_KNOCKBACK on both client
 * and server side (like Reach does), so the server actually applies the knockback.
 * Configurable up to 2500.
 */
public class KnockbackHandler {

    private static final Identifier KNOCKBACK_ID = Identifier.of("reachfly", "knockback_boost");
    private static int tickCounter = 0;

    public static void tick(MinecraftClient client) {
        if (client.player == null) return;

        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            updateKnockbackAttributes();
        }
    }

    public static void updateKnockbackAttributes() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        ClientPlayerEntity player = client.player;

        // Apply to client-side player
        applyToPlayer(player);

        // Apply to server-side player (singleplayer)
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
            // Default ATTACK_KNOCKBACK is 0, so the boost IS the total value
            double boost = ModConfig.knockbackStrength;

            knockback.removeModifier(KNOCKBACK_ID);
            knockback.addTemporaryModifier(new EntityAttributeModifier(
                    KNOCKBACK_ID, boost,
                    EntityAttributeModifier.Operation.ADD_VALUE));
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
