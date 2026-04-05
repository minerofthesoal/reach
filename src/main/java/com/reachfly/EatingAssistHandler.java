package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

/**
 * Eating Assist - Automatically eats food when hunger drops below threshold.
 * Searches the entire hotbar for food. Does NOT hold down use key (which
 * would block auto hit and ESP interactions). Instead uses the interaction
 * manager to start using the item each tick.
 */
public class EatingAssistHandler {

    private static int previousSlot = -1;
    private static int eatTicks = 0;

    public static void tick(MinecraftClient client) {
        if (!ModConfig.eatingAssistEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) {
            reset(client);
            return;
        }
        if (client.interactionManager == null) return;

        ClientPlayerEntity player = client.player;
        int foodLevel = player.getHungerManager().getFoodLevel();

        // If hunger is satisfied, stop eating
        if (foodLevel >= ModConfig.eatingHungerThreshold) {
            if (previousSlot >= 0) {
                reset(client);
            }
            return;
        }

        // If player is currently using an item (eating), let it continue
        if (player.isUsingItem()) {
            eatTicks++;
            // Safety timeout - if eating takes too long, something went wrong
            if (eatTicks > 60) {
                reset(client);
            }
            return;
        }

        // If we were eating and the item finished, check if we need more food
        if (previousSlot >= 0 && !player.isUsingItem()) {
            // Check if still hungry
            if (player.getHungerManager().getFoodLevel() >= ModConfig.eatingHungerThreshold) {
                reset(client);
                return;
            }
            // Check if current slot still has food
            ItemStack held = player.getMainHandStack();
            if (!isFood(held)) {
                // Current food ran out, find more
                player.getInventory().selectedSlot = (previousSlot);
                previousSlot = -1;
            }
        }

        // Find best food in hotbar (most hunger restoration)
        int foodSlot = findBestFoodSlot(player);
        if (foodSlot == -1) return;

        // Save original slot if not already saved
        if (previousSlot < 0) {
            previousSlot = player.getInventory().selectedSlot;
        }

        // Switch to food slot
        player.getInventory().selectedSlot = (foodSlot);
        eatTicks = 0;

        // Use the interaction manager to start eating (doesn't hold use key)
        client.interactionManager.interactItem(player, net.minecraft.util.Hand.MAIN_HAND);
    }

    private static void reset(MinecraftClient client) {
        if (previousSlot >= 0 && client.player != null) {
            client.player.getInventory().selectedSlot = (previousSlot);
        }
        previousSlot = -1;
        eatTicks = 0;
    }

    private static int findBestFoodSlot(ClientPlayerEntity player) {
        int bestSlot = -1;
        int bestNutrition = 0;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!isFood(stack)) continue;

            // Get nutrition value if available, otherwise default to 1
            var foodComp = stack.get(DataComponentTypes.FOOD);
            int nutrition = (foodComp != null) ? foodComp.nutrition() : 1;

            if (nutrition > bestNutrition) {
                bestNutrition = nutrition;
                bestSlot = i;
            }
        }
        return bestSlot;
    }

    private static boolean isFood(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.contains(DataComponentTypes.FOOD);
    }
}
