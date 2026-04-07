package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public class EatingAssistHandler {

    private static int previousSlot = -1;
    private static int eatTicks = 0;
    private static boolean isHoldingUse = false;

    public static void tick(Minecraft client) {
        if (!ModConfig.eatingAssistEnabled) return;
        if (client.player == null || client.level == null) return;
        if (client.screen != null) {
            reset(client);
            return;
        }
        if (client.gameMode == null) return;

        LocalPlayer player = client.player;
        int foodLevel = player.getFoodData().getFoodLevel();

        // If hunger is satisfied, stop eating
        if (foodLevel >= ModConfig.eatingHungerThreshold) {
            if (previousSlot >= 0 || isHoldingUse) {
                reset(client);
            }
            return;
        }

        // If player is currently using an item (eating), hold the use key
        if (player.isUsingItem()) {
            KeyMapping.set(client.options.keyUse.getDefaultKey(), true);
            isHoldingUse = true;
            eatTicks++;
            // Safety timeout - foods take max 40 ticks (2 sec), 72 with dried kelp
            if (eatTicks > 80) {
                reset(client);
            }
            return;
        }

        // If we just finished eating (were holding use but player stopped using item)
        if (isHoldingUse) {
            KeyMapping.set(client.options.keyUse.getDefaultKey(), false);
            isHoldingUse = false;
            eatTicks = 0;
            // Check if still hungry
            if (player.getFoodData().getFoodLevel() >= ModConfig.eatingHungerThreshold) {
                reset(client);
                return;
            }
        }

        // If we were eating and the food ran out, find more
        if (previousSlot >= 0) {
            ItemStack held = player.getMainHandItem();
            if (!isFood(held)) {
                player.getInventory().selected = previousSlot;
                previousSlot = -1;
            }
        }

        // Find best food in hotbar (most hunger restoration)
        int foodSlot = findBestFoodSlot(player);
        if (foodSlot == -1) return;

        // Save original slot if not already saved
        if (previousSlot < 0) {
            previousSlot = player.getInventory().selected;
        }

        // Switch to food slot and start eating
        player.getInventory().selected = foodSlot;
        eatTicks = 0;

        // Start eating via interaction manager, then hold use key
        client.gameMode.useItem(player, net.minecraft.world.InteractionHand.MAIN_HAND);
        KeyMapping.set(client.options.keyUse.getDefaultKey(), true);
        isHoldingUse = true;
    }

    private static void reset(Minecraft client) {
        if (isHoldingUse) {
            KeyMapping.set(client.options.keyUse.getDefaultKey(), false);
            isHoldingUse = false;
        }
        if (previousSlot >= 0 && client.player != null) {
            client.player.getInventory().selected = previousSlot;
        }
        previousSlot = -1;
        eatTicks = 0;
    }

    private static int findBestFoodSlot(LocalPlayer player) {
        int bestSlot = -1;
        int bestNutrition = 0;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!isFood(stack)) continue;

            var foodComp = stack.get(DataComponents.FOOD);
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
        return stack.has(DataComponents.FOOD);
    }
}
