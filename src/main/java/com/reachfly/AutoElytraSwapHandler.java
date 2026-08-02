package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ClickType;
public class AutoElytraSwapHandler {

    private static int swapCooldown = 0;

    public static void tick(Minecraft client) {
        if (!ModConfig.autoElytraSwapEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.screen != null) return;
        if (client.interactionManager == null) return;

        LocalPlayer player = client.player;

        if (swapCooldown > 0) {
            swapCooldown--;
            return;
        }

        ItemStack chestSlot = player.getEquippedStack(EquipmentSlot.CHEST);
        boolean hasElytra = chestSlot.isOf(Items.ELYTRA);

        if (!player.isOnGround() && player.fallDistance > 0.5f && !hasElytra) {
            int elytraSlot = findItem(player, Items.ELYTRA);
            if (elytraSlot != -1) {
                swapToChestSlot(client, player, elytraSlot);
                swapCooldown = 5;
            }
        } else if (player.isOnGround() && hasElytra) {
            int chestplateSlot = findChestplate(player);
            if (chestplateSlot != -1) {
                swapToChestSlot(client, player, chestplateSlot);
                swapCooldown = 5;
            }
        }
    }

    private static int findItem(LocalPlayer player, net.minecraft.item.Item item) {
        // Use getStack(slot) instead of accessing private 'main' field
        for (int i = 0; i < 36; i++) {
            if (player.getInventory().getStack(i).isOf(item)) {
                return i;
            }
        }
        return -1;
    }

    private static int findChestplate(LocalPlayer player) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(Items.NETHERITE_CHESTPLATE) || stack.isOf(Items.DIAMOND_CHESTPLATE)
                    || stack.isOf(Items.IRON_CHESTPLATE) || stack.isOf(Items.GOLDEN_CHESTPLATE)
                    || stack.isOf(Items.CHAINMAIL_CHESTPLATE) || stack.isOf(Items.LEATHER_CHESTPLATE)) {
                return i;
            }
        }
        return -1;
    }

    private static void swapToChestSlot(Minecraft client, LocalPlayer player, int inventorySlot) {
        int chestArmorScreenSlot = 6;
        int screenSlot;
        if (inventorySlot < 9) {
            screenSlot = inventorySlot + 36;
        } else {
            screenSlot = inventorySlot;
        }

        int syncId = player.screenHandler.syncId;
        client.interactionManager.clickSlot(syncId, screenSlot, 0, ClickType.PICKUP, player);
        client.interactionManager.clickSlot(syncId, chestArmorScreenSlot, 0, ClickType.PICKUP, player);
        client.interactionManager.clickSlot(syncId, screenSlot, 0, ClickType.PICKUP, player);
    }
}
