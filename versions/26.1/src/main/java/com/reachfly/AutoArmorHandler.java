package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.equipment.EquippableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ClickType;

/**
 * Auto Armor - Automatically equips the best armor from inventory.
 * Checks each armor slot and swaps in better armor when found.
 * Priority: Netherite > Diamond > Iron > Gold > Chain > Leather
 */
public class AutoArmorHandler {

    private static int tickCounter = 0;

    public static void tick(Minecraft minecraft) {
        if (!ModConfig.autoArmorEnabled) return;
        if (minecraft.player == null || minecraft.gameMode == null) return;
        if (minecraft.screen != null) return;

        tickCounter++;
        if (tickCounter < 40) return; // Check every 2 seconds
        tickCounter = 0;

        LocalPlayer player = minecraft.player;

        // Armor slots: 5=helmet, 6=chest, 7=legs, 8=boots
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        int[] slotIndices = {5, 6, 7, 8};

        for (int s = 0; s < 4; s++) {
            ItemStack current = player.screenHandler.getSlot(slotIndices[s]).getItem();
            int currentTier = getArmorTier(current);
            EquipmentSlot targetSlot = slots[s];

            int bestInvSlot = -1;
            int bestTier = currentTier;

            // Search inventory (9-44) for better armor
            for (int i = 9; i < 45; i++) {
                ItemStack stack = player.screenHandler.getSlot(i).getItem();
                EquippableComponent equippable = stack.get(DataComponents.EQUIPPABLE);
                if (equippable != null && equippable.slot() == targetSlot) {
                    int tier = getArmorTier(stack);
                    if (tier > bestTier) {
                        bestTier = tier;
                        bestInvSlot = i;
                    }
                }
            }

            if (bestInvSlot >= 0) {
                int syncId = player.screenHandler.containerId;
                // Shift-click to equip
                minecraft.gameMode.handleInventoryMouseClick(syncId, bestInvSlot, 0, ClickType.QUICK_MOVE, player);
                return; // One swap per cycle
            }
        }
    }

    private static int getArmorTier(ItemStack stack) {
        if (stack.isEmpty()) return -1;
        EquippableComponent equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) return -1;
        String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getId(stack.getItem()).getPath();
        if (id.contains("netherite")) return 6;
        if (id.contains("diamond")) return 5;
        if (id.contains("iron")) return 4;
        if (id.contains("gold")) return 3;
        if (id.contains("chainmail")) return 2;
        if (id.contains("leather")) return 1;
        return 0;
    }
}
