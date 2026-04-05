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
 * Manages the player's reach attribute modifiers on BOTH client and server side.
 * Client-side: controls crosshair targeting distance.
 * Server-side (singleplayer): controls actual interaction validation distance.
 */
public class ReachHandler {

    private static final Identifier BLOCK_REACH_ID = Identifier.of("reachfly", "block_reach");
    private static final Identifier ENTITY_REACH_ID = Identifier.of("reachfly", "entity_reach");

    private static final double DEFAULT_BLOCK_RANGE = 4.5;
    private static final double DEFAULT_ENTITY_RANGE = 3.0;

    public static void updateReachAttributes() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        ClientPlayerEntity player = client.player;

        // Apply to client-side player (crosshair targeting)
        applyToPlayer(player);

        // Apply to server-side player (actual interaction validation in singleplayer)
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
        EntityAttributeInstance blockRange = player.getAttributeInstance(
                EntityAttributes.BLOCK_INTERACTION_RANGE);
        EntityAttributeInstance entityRange = player.getAttributeInstance(
                EntityAttributes.ENTITY_INTERACTION_RANGE);

        if (blockRange == null || entityRange == null) return;

        if (ModConfig.reachEnabled) {
            double blockBoost = ModConfig.reachDistance - DEFAULT_BLOCK_RANGE;
            double entityBoost = ModConfig.reachDistance - DEFAULT_ENTITY_RANGE;

            blockRange.removeModifier(BLOCK_REACH_ID);
            entityRange.removeModifier(ENTITY_REACH_ID);

            blockRange.addTemporaryModifier(new EntityAttributeModifier(
                    BLOCK_REACH_ID, blockBoost,
                    EntityAttributeModifier.Operation.ADD_VALUE));

            entityRange.addTemporaryModifier(new EntityAttributeModifier(
                    ENTITY_REACH_ID, entityBoost,
                    EntityAttributeModifier.Operation.ADD_VALUE));
        } else {
            blockRange.removeModifier(BLOCK_REACH_ID);
            entityRange.removeModifier(ENTITY_REACH_ID);
        }
    }

    public static void clearReachModifiers() {
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
        EntityAttributeInstance blockRange = player.getAttributeInstance(
                EntityAttributes.BLOCK_INTERACTION_RANGE);
        EntityAttributeInstance entityRange = player.getAttributeInstance(
                EntityAttributes.ENTITY_INTERACTION_RANGE);

        if (blockRange != null) blockRange.removeModifier(BLOCK_REACH_ID);
        if (entityRange != null) entityRange.removeModifier(ENTITY_REACH_ID);
    }
}
