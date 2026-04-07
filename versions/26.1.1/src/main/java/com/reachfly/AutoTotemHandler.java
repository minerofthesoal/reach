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

    public static void tick(Minecraft minecraft) {
        if (!ModConfig.autoTotemEnabled) return;
        if (minecraft.player == null || minecraft.gameMode == null) return;
        if (minecraft.screen != null) return;

        tickCounter++;
        if (tickCounter < 20) return; // Check once per second
        tickCounter = 0;

        LocalPlayer player = minecraft.player;

        // Check if offhand already has a totem
        if (player.getOffHandStack().is(Items.TOTEM_OF_UNDYING)) return;

        // Search inventory for totem (slots 9-44 = main inventory + hotbar)
        int syncId = player.screenHandler.containerId;
        for (int i = 9; i < 45; i++) {
            if (player.screenHandler.getSlot(i).getItem().is(Items.TOTEM_OF_UNDYING)) {
                // Pick up totem
                minecraft.gameMode.handleInventoryMouseClick(syncId, i, 0, ClickType.PICKUP, player);
                // Place in offhand (slot 45)
                minecraft.gameMode.handleInventoryMouseClick(syncId, 45, 0, ClickType.PICKUP, player);
                // Put whatever was in offhand back
                if (!player.screenHandler.getCursorStack().isEmpty()) {
                    minecraft.gameMode.handleInventoryMouseClick(syncId, i, 0, ClickType.PICKUP, player);
                }
                return;
            }
        }
    }
}
