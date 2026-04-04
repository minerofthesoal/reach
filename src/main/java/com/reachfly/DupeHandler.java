package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

/**
 * Dupe Handler - Attempts inventory-based item duplication.
 *
 * Method: Donkey/Llama chest dupe
 * When enabled and looking at a donkey/llama with a chest:
 * 1. Opens the donkey's inventory
 * 2. Rapidly moves items between player inventory and donkey inventory
 *    while killing the donkey at specific timing windows
 *
 * Note: This exploit depends on server-side tick timing and may not work
 * on all servers. Many servers have patched this. Works best on vanilla
 * servers without anti-cheat plugins.
 *
 * Toggle with J key. The player must:
 * - Have a donkey/llama with a chest nearby
 * - Be holding items to dupe in their inventory
 */
public class DupeHandler {

    private static int tickCounter = 0;
    private static boolean active = false;

    /**
     * Called every client tick.
     */
    public static void tick(MinecraftClient client) {
        if (!ModConfig.dupeEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;

        ClientPlayerEntity player = client.player;
        tickCounter++;

        // Look for a nearby donkey/llama with a chest
        AbstractDonkeyEntity target = null;
        double closest = 5.0;

        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof AbstractDonkeyEntity donkey) {
                if (donkey.hasChest() && donkey.isAlive()) {
                    double dist = player.distanceTo(donkey);
                    if (dist < closest) {
                        closest = dist;
                        target = donkey;
                    }
                }
            }
        }

        if (target == null) return;

        // Interact with the donkey every 10 ticks to open its inventory,
        // then quickly close and reopen to cause desync
        if (tickCounter % 10 == 0) {
            // Open the donkey inventory by interacting
            client.interactionManager.interactEntity(player, target, Hand.MAIN_HAND);
        }

        // On the next tick after opening, rapidly shift-click items
        if (tickCounter % 10 == 2 && client.player.currentScreenHandler != null) {
            int slots = client.player.currentScreenHandler.slots.size();
            // Shift-click items from player inventory into donkey
            for (int i = slots - 36; i < slots; i++) {
                if (!client.player.currentScreenHandler.getSlot(i).getStack().isEmpty()) {
                    client.interactionManager.clickSlot(
                            client.player.currentScreenHandler.syncId,
                            i, 0, SlotActionType.QUICK_MOVE, player);
                    break; // One item per cycle
                }
            }
        }

        // Kill the donkey at a specific timing window to cause item rollback
        if (tickCounter % 10 == 5) {
            client.interactionManager.attackEntity(player, target);
            player.swingHand(Hand.MAIN_HAND);
        }
    }
}
