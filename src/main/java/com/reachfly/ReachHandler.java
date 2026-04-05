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
 * Manages reach attribute modifiers on BOTH client and server side.
 * Only updates when the value actually changes to prevent flickering.
 */
public class ReachHandler {

    private static final Identifier BLOCK_REACH_ID = Identifier.of("reachfly", "block_reach");
    private static final Identifier ENTITY_REACH_ID = Identifier.of("reachfly", "entity_reach");

    private static final double DEFAULT_BLOCK_RANGE = 4.5;
    private static final double DEFAULT_ENTITY_RANGE = 3.0;

    private static int tickCounter = 0;
    private static boolean lastEnabled = false;
    private static float lastDistance = 0;

    public static void tick(MinecraftClient client) {
        if (client.player == null) return;

        // Only update when state actually changes
        boolean needsUpdate = (ModConfig.reachEnabled != lastEnabled)
                || (ModConfig.reachEnabled && ModConfig.reachDistance != lastDistance);

        tickCounter++;
        // Also re-apply every 2 seconds as safety net (respawn, dimension change)
        if (tickCounter >= 40) {
            tickCounter = 0;
            if (ModConfig.reachEnabled) needsUpdate = true;
        }

        if (needsUpdate) {
            lastEnabled = ModConfig.reachEnabled;
            lastDistance = ModConfig.reachDistance;
            updateReachAttributes();
        }
    }

    public static void updateReachAttributes() {
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
        EntityAttributeInstance blockRange = player.getAttributeInstance(
                EntityAttributes.BLOCK_INTERACTION_RANGE);
        EntityAttributeInstance entityRange = player.getAttributeInstance(
                EntityAttributes.ENTITY_INTERACTION_RANGE);

        if (blockRange == null || entityRange == null) return;

        if (ModConfig.reachEnabled) {
            double blockBoost = ModConfig.reachDistance - DEFAULT_BLOCK_RANGE;
            double entityBoost = ModConfig.reachDistance - DEFAULT_ENTITY_RANGE;

            // Check if modifier already exists with correct value to avoid flickering
            EntityAttributeModifier existingBlock = blockRange.getModifier(BLOCK_REACH_ID);
            if (existingBlock == null || existingBlock.value() != blockBoost) {
                blockRange.removeModifier(BLOCK_REACH_ID);
                blockRange.addTemporaryModifier(new EntityAttributeModifier(
                        BLOCK_REACH_ID, blockBoost,
                        EntityAttributeModifier.Operation.ADD_VALUE));
            }

            EntityAttributeModifier existingEntity = entityRange.getModifier(ENTITY_REACH_ID);
            if (existingEntity == null || existingEntity.value() != entityBoost) {
                entityRange.removeModifier(ENTITY_REACH_ID);
                entityRange.addTemporaryModifier(new EntityAttributeModifier(
                        ENTITY_REACH_ID, entityBoost,
                        EntityAttributeModifier.Operation.ADD_VALUE));
            }
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
