package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;

public class EatingAssistHandler {

    private static int previousSlot = -1;
    private static int eatTicks = 0;
    private static boolean isHoldingUse = false;

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
            if (previousSlot >= 0 || isHoldingUse) {
                reset(client);
            }
            return;
        }

        // If player is currently using an item (eating), hold the use key
        if (player.isUsingItem()) {
            KeyBinding.setKeyPressed(client.options.useKey.getDefaultKey(), true);
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
            KeyBinding.setKeyPressed(client.options.useKey.getDefaultKey(), false);
            isHoldingUse = false;
            eatTicks = 0;
            // Check if still hungry
            if (player.getHungerManager().getFoodLevel() >= ModConfig.eatingHungerThreshold) {
                reset(client);
                return;
            }
        }

        // If we were eating and the food ran out, find more
        if (previousSlot >= 0) {
            ItemStack held = player.getMainHandStack();
            if (!isFood(held)) {
                player.getInventory().selectedSlot = previousSlot;
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

        // Switch to food slot and start eating
        player.getInventory().selectedSlot = foodSlot;
        eatTicks = 0;

        // Start eating via interaction manager, then hold use key
        client.interactionManager.interactItem(player, net.minecraft.util.Hand.MAIN_HAND);
        KeyBinding.setKeyPressed(client.options.useKey.getDefaultKey(), true);
        isHoldingUse = true;
    }

    private static void reset(MinecraftClient client) {
        if (isHoldingUse) {
            KeyBinding.setKeyPressed(client.options.useKey.getDefaultKey(), false);
            isHoldingUse = false;
        }
        if (previousSlot >= 0 && client.player != null) {
            client.player.getInventory().selectedSlot = previousSlot;
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
