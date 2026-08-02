package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.ClickType;
/**
 * Auto Totem - Automatically moves totems of undying to the offhand slot.
 * Scans inventory every second for totems and swaps them in.
 */
public class AutoTotemHandler {

    private static int tickCounter = 0;

    public static void tick(Minecraft client) {
        if (!ModConfig.autoTotemEnabled) return;
        if (client.player == null || client.interactionManager == null) return;
        if (client.screen != null) return;

        tickCounter++;
        if (tickCounter < 20) return; // Check once per second
        tickCounter = 0;

        LocalPlayer player = client.player;

        // Check if offhand already has a totem
        if (player.getOffhandItem().isOf(Items.TOTEM_OF_UNDYING)) return;

        // Search inventory for totem (slots 9-44 = main inventory + hotbar)
        int syncId = player.screenHandler.syncId;
        for (int i = 9; i < 45; i++) {
            if (player.screenHandler.getSlot(i).getItem().isOf(Items.TOTEM_OF_UNDYING)) {
                // Pick up totem
                client.interactionManager.clickSlot(syncId, i, 0, ClickType.PICKUP, player);
                // Place in offhand (slot 45)
                client.interactionManager.clickSlot(syncId, 45, 0, ClickType.PICKUP, player);
                // Put whatever was in offhand back
                if (!player.screenHandler.getCursorStack().isEmpty()) {
                    client.interactionManager.clickSlot(syncId, i, 0, ClickType.PICKUP, player);
                }
                return;
            }
        }
    }
}
