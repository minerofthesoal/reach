package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Auto Totem - Automatically moves totems of undying to the offhand slot.
 * Scans inventory every second for totems and swaps them in.
 */
public class AutoTotemHandler {

    private static int tickCounter = 0;

    public static void tick(MinecraftClient client) {
        if (!ModConfig.autoTotemEnabled) return;
        if (client.player == null || client.interactionManager == null) return;
        if (client.currentScreen != null) return;

        tickCounter++;
        if (tickCounter < 20) return; // Check once per second
        tickCounter = 0;

        ClientPlayerEntity player = client.player;

        // Check if offhand already has a totem
        if (player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) return;

        // Search inventory for totem (slots 9-44 = main inventory + hotbar)
        int syncId = player.currentScreenHandler.syncId;
        for (int i = 9; i < 45; i++) {
            if (player.currentScreenHandler.getSlot(i).getStack().isOf(Items.TOTEM_OF_UNDYING)) {
                // Pick up totem
                client.interactionManager.clickSlot(syncId, i, 0, SlotActionType.PICKUP, player);
                // Place in offhand (slot 45)
                client.interactionManager.clickSlot(syncId, 45, 0, SlotActionType.PICKUP, player);
                // Put whatever was in offhand back
                if (!player.currentScreenHandler.getCursorStack().isEmpty()) {
                    client.interactionManager.clickSlot(syncId, i, 0, SlotActionType.PICKUP, player);
                }
                return;
            }
        }
    }
}
