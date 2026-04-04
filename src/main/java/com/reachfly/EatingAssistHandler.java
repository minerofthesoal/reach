package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 * Eating Assist - Automatically eats food when the player's hunger drops below a threshold.
 * Scans hotbar for food items and switches + eats automatically.
 */
public class EatingAssistHandler {

    private static boolean isEating = false;
    private static int previousSlot = -1;

    /**
     * Called every client tick to check hunger and auto-eat.
     */
    public static void tick(MinecraftClient client) {
        if (!ModConfig.eatingAssistEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) {
            stopEating(client);
            return;
        }

        ClientPlayerEntity player = client.player;
        int foodLevel = player.getHungerManager().getFoodLevel();

        // If hunger is above threshold and we were eating, stop
        if (foodLevel >= ModConfig.eatingHungerThreshold) {
            if (isEating) {
                stopEating(client);
            }
            return;
        }

        // Check if currently held item is food and we're already eating
        if (isEating) {
            // Keep holding use key
            if (!player.isUsingItem()) {
                // Item finished or was interrupted, try again
                ItemStack held = player.getMainHandStack();
                if (isFood(held)) {
                    client.options.useKey.setPressed(true);
                } else {
                    stopEating(client);
                }
            }
            return;
        }

        // Find food in hotbar
        int foodSlot = findFoodSlot(player);
        if (foodSlot == -1) return; // No food available

        // Save current slot and switch to food
        previousSlot = player.getInventory().selectedSlot;
        player.getInventory().selectedSlot = foodSlot;

        // Start eating
        client.options.useKey.setPressed(true);
        isEating = true;
    }

    /**
     * Stop the eating process and restore the previous hotbar slot.
     */
    private static void stopEating(MinecraftClient client) {
        if (!isEating) return;

        client.options.useKey.setPressed(false);
        isEating = false;

        if (previousSlot >= 0 && client.player != null) {
            client.player.getInventory().selectedSlot = previousSlot;
            previousSlot = -1;
        }
    }

    /**
     * Find the first food item in the player's hotbar (slots 0-8).
     */
    private static int findFoodSlot(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (isFood(stack)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Check if an ItemStack is a food item.
     */
    private static boolean isFood(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.contains(DataComponentTypes.FOOD);
    }
}
