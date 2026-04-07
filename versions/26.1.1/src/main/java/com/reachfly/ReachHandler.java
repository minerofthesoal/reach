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
 * Manages reach attribute modifiers on BOTH client and server side.
 * Only updates when the value actually changes to prevent flickering.
 */
public class ReachHandler {

    private static final ResourceLocation BLOCK_REACH_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "block_reach");
    private static final ResourceLocation ENTITY_REACH_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "entity_reach");

    private static final double DEFAULT_BLOCK_RANGE = 4.5;
    private static final double DEFAULT_ENTITY_RANGE = 3.0;

    private static int tickCounter = 0;
    private static boolean lastEnabled = false;
    private static float lastDistance = 0;

    public static void tick(Minecraft minecraft) {
        if (minecraft.player == null) return;

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
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        LocalPlayer player = minecraft.player;
        applyToPlayer(player);

        MinecraftServer server = minecraft.getSingleplayerServer();
        if (server != null) {
            ServerPlayer serverPlayer = server.getPlayerList()
                    .getPlayer(player.getUuid());
            if (serverPlayer != null) {
                applyToPlayer(serverPlayer);
            }
        }
    }

    private static void applyToPlayer(net.minecraft.level.entity.LivingEntity player) {
        AttributeInstance blockRange = player.getAttribute(
                Attributes.BLOCK_INTERACTION_RANGE);
        AttributeInstance entityRange = player.getAttribute(
                Attributes.ENTITY_INTERACTION_RANGE);

        if (blockRange == null || entityRange == null) return;

        if (ModConfig.reachEnabled) {
            double blockBoost = ModConfig.reachDistance - DEFAULT_BLOCK_RANGE;
            double entityBoost = ModConfig.reachDistance - DEFAULT_ENTITY_RANGE;

            // Check if modifier already exists with correct value to avoid flickering
            AttributeModifier existingBlock = blockRange.getModifier(BLOCK_REACH_ID);
            if (existingBlock == null || existingBlock.value() != blockBoost) {
                blockRange.removeModifier(BLOCK_REACH_ID);
                blockRange.addTransientModifier(new AttributeModifier(
                        BLOCK_REACH_ID, blockBoost,
                        AttributeModifier.Operation.ADD_VALUE));
            }

            AttributeModifier existingEntity = entityRange.getModifier(ENTITY_REACH_ID);
            if (existingEntity == null || existingEntity.value() != entityBoost) {
                entityRange.removeModifier(ENTITY_REACH_ID);
                entityRange.addTransientModifier(new AttributeModifier(
                        ENTITY_REACH_ID, entityBoost,
                        AttributeModifier.Operation.ADD_VALUE));
            }
        } else {
            blockRange.removeModifier(BLOCK_REACH_ID);
            entityRange.removeModifier(ENTITY_REACH_ID);
        }
    }

    public static void clearReachModifiers() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        clearForPlayer(minecraft.player);

        MinecraftServer server = minecraft.getSingleplayerServer();
        if (server != null) {
            ServerPlayer serverPlayer = server.getPlayerList()
                    .getPlayer(minecraft.player.getUuid());
            if (serverPlayer != null) {
                clearForPlayer(serverPlayer);
            }
        }
    }

    private static void clearForPlayer(net.minecraft.level.entity.LivingEntity player) {
        AttributeInstance blockRange = player.getAttribute(
                Attributes.BLOCK_INTERACTION_RANGE);
        AttributeInstance entityRange = player.getAttribute(
                Attributes.ENTITY_INTERACTION_RANGE);

        if (blockRange != null) blockRange.removeModifier(BLOCK_REACH_ID);
        if (entityRange != null) entityRange.removeModifier(ENTITY_REACH_ID);
    }
}
