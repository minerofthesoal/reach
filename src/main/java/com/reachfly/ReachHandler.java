package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;

/**
 * Manages the player's reach attribute modifiers.
 * In MC 1.21.1, interaction range is controlled by entity attributes:
 * - PLAYER_BLOCK_INTERACTION_RANGE (default 4.5, creative 5.0)
 * - PLAYER_ENTITY_INTERACTION_RANGE (default 3.0, creative 5.0)
 *
 * We apply additive modifiers to extend these ranges.
 */
public class ReachHandler {

    private static final Identifier BLOCK_REACH_ID = Identifier.of("reachfly", "block_reach");
    private static final Identifier ENTITY_REACH_ID = Identifier.of("reachfly", "entity_reach");

    // Default vanilla values
    private static final double DEFAULT_BLOCK_RANGE = 4.5;
    private static final double DEFAULT_ENTITY_RANGE = 3.0;

    /**
     * Called every tick to apply or remove reach attribute modifiers
     * based on the current config state.
     */
    public static void updateReachAttributes() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        ClientPlayerEntity player = client.player;

        EntityAttributeInstance blockRange = player.getAttributeInstance(
                EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
        EntityAttributeInstance entityRange = player.getAttributeInstance(
                EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);

        if (blockRange == null || entityRange == null) return;

        if (ModConfig.reachEnabled) {
            // Calculate the additive boost needed to reach the target distance
            double blockBoost = ModConfig.reachDistance - DEFAULT_BLOCK_RANGE;
            double entityBoost = ModConfig.reachDistance - DEFAULT_ENTITY_RANGE;

            // Remove old modifiers before adding updated ones
            blockRange.removeModifier(BLOCK_REACH_ID);
            entityRange.removeModifier(ENTITY_REACH_ID);

            blockRange.addTemporaryModifier(new EntityAttributeModifier(
                    BLOCK_REACH_ID, blockBoost,
                    EntityAttributeModifier.Operation.ADD_VALUE));

            entityRange.addTemporaryModifier(new EntityAttributeModifier(
                    ENTITY_REACH_ID, entityBoost,
                    EntityAttributeModifier.Operation.ADD_VALUE));
        } else {
            // Remove modifiers when reach is disabled
            blockRange.removeModifier(BLOCK_REACH_ID);
            entityRange.removeModifier(ENTITY_REACH_ID);
        }
    }

    /**
     * Remove all reach modifiers (called on disable/disconnect).
     */
    public static void clearReachModifiers() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        ClientPlayerEntity player = client.player;

        EntityAttributeInstance blockRange = player.getAttributeInstance(
                EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
        EntityAttributeInstance entityRange = player.getAttributeInstance(
                EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);

        if (blockRange != null) blockRange.removeModifier(BLOCK_REACH_ID);
        if (entityRange != null) entityRange.removeModifier(ENTITY_REACH_ID);
    }
}
