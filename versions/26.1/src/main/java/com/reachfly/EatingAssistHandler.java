package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public class EatingAssistHandler {

    private static int previousSlot = -1;
    private static int eatTicks = 0;
    private static boolean isHoldingUse = false;

    public static void tick(Minecraft minecraft) {
        if (!ModConfig.eatingAssistEnabled) return;
        if (minecraft.player == null || minecraft.level == null) return;
        if (minecraft.screen != null) {
            reset(minecraft);
            return;
        }
        if (minecraft.gameMode == null) return;

        LocalPlayer player = minecraft.player;
        int foodLevel = player.getFoodData().getFoodLevel();

        // If hunger is satisfied, stop eating
        if (foodLevel >= ModConfig.eatingHungerThreshold) {
            if (previousSlot >= 0 || isHoldingUse) {
                reset(minecraft);
            }
            return;
        }

        // If player is currently using an item (eating), hold the use key
        if (player.isUsingItem()) {
            KeyBinding.setKeyPressed(minecraft.options.useKey.getDefaultKey(), true);
            isHoldingUse = true;
            eatTicks++;
            // Safety timeout - foods take max 40 ticks (2 sec), 72 with dried kelp
            if (eatTicks > 80) {
                reset(minecraft);
            }
            return;
        }

        // If we just finished eating (were holding use but player stopped using item)
        if (isHoldingUse) {
            KeyBinding.setKeyPressed(minecraft.options.useKey.getDefaultKey(), false);
            isHoldingUse = false;
            eatTicks = 0;
            // Check if still hungry
            if (player.getFoodData().getFoodLevel() >= ModConfig.eatingHungerThreshold) {
                reset(minecraft);
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
        minecraft.gameMode.useItem(player, net.minecraft.level.InteractionHand.MAIN_HAND);
        KeyBinding.setKeyPressed(minecraft.options.useKey.getDefaultKey(), true);
        isHoldingUse = true;
    }

    private static void reset(Minecraft minecraft) {
        if (isHoldingUse) {
            KeyBinding.setKeyPressed(minecraft.options.useKey.getDefaultKey(), false);
            isHoldingUse = false;
        }
        if (previousSlot >= 0 && minecraft.player != null) {
            minecraft.player.getInventory().selected = previousSlot;
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
        return stack.contains(DataComponents.FOOD);
    }
}
