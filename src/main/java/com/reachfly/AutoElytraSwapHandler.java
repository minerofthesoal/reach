package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Auto Elytra Swap - Automatically swaps between chestplate and elytra.
 * When falling/jumping: equips elytra. When on ground: equips chestplate.
 * Searches inventory for the item to swap in.
 */
public class AutoElytraSwapHandler {

    private static int swapCooldown = 0;

    public static void tick(MinecraftClient client) {
        if (!ModConfig.autoElytraSwapEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;
        if (client.interactionManager == null) return;

        ClientPlayerEntity player = client.player;

        if (swapCooldown > 0) {
            swapCooldown--;
            return;
        }

        ItemStack chestSlot = player.getEquippedStack(EquipmentSlot.CHEST);
        boolean hasElytra = chestSlot.isOf(Items.ELYTRA);
        boolean hasChestplate = !chestSlot.isEmpty() && !hasElytra;

        if (!player.isOnGround() && player.fallDistance > 0.5f && !hasElytra) {
            // Falling - swap to elytra
            int elytraSlot = findInInventory(player, Items.ELYTRA);
            if (elytraSlot != -1) {
                swapToChestSlot(client, player, elytraSlot);
                swapCooldown = 5;
            }
        } else if (player.isOnGround() && hasElytra) {
            // On ground - swap back to chestplate
            int chestplateSlot = findChestplateInInventory(player);
            if (chestplateSlot != -1) {
                swapToChestSlot(client, player, chestplateSlot);
                swapCooldown = 5;
            }
        }
    }

    private static int findInInventory(ClientPlayerEntity player, net.minecraft.item.Item item) {
        for (int i = 0; i < player.getInventory().main.size(); i++) {
            if (player.getInventory().main.get(i).isOf(item)) {
                return i;
            }
        }
        return -1;
    }

    private static int findChestplateInInventory(ClientPlayerEntity player) {
        for (int i = 0; i < player.getInventory().main.size(); i++) {
            ItemStack stack = player.getInventory().main.get(i);
            if (stack.isOf(Items.NETHERITE_CHESTPLATE) || stack.isOf(Items.DIAMOND_CHESTPLATE)
                    || stack.isOf(Items.IRON_CHESTPLATE) || stack.isOf(Items.GOLDEN_CHESTPLATE)
                    || stack.isOf(Items.CHAINMAIL_CHESTPLATE) || stack.isOf(Items.LEATHER_CHESTPLATE)) {
                return i;
            }
        }
        return -1;
    }

    private static void swapToChestSlot(MinecraftClient client, ClientPlayerEntity player, int inventorySlot) {
        // Chest armor slot in the screen handler is slot 6
        int chestArmorScreenSlot = 6;
        // Convert inventory slot to screen handler slot
        // Hotbar: 0-8 -> screen 36-44, Main: 9-35 -> screen 9-35
        int screenSlot;
        if (inventorySlot < 9) {
            screenSlot = inventorySlot + 36;
        } else {
            screenSlot = inventorySlot;
        }

        // Pick up from inventory slot, place in chest slot
        int syncId = player.currentScreenHandler.syncId;
        client.interactionManager.clickSlot(syncId, screenSlot, 0, SlotActionType.PICKUP, player);
        client.interactionManager.clickSlot(syncId, chestArmorScreenSlot, 0, SlotActionType.PICKUP, player);
        client.interactionManager.clickSlot(syncId, screenSlot, 0, SlotActionType.PICKUP, player);
    }
}
