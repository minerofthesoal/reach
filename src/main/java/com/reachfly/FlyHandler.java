package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Handles survival fly logic each tick.
 * Allows flying in survival/adventure mode when enabled.
 */
public class FlyHandler {

    private static boolean wasFlying = false;

    /**
     * Called every client tick to manage fly state and speed.
     */
    public static void tick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        // Don't override creative mode flight
        if (player.getAbilities().creativeMode) return;

        if (ModConfig.flyEnabled) {
            wasFlying = true;

            // Enable flight ability (client-side)
            player.getAbilities().allowFlying = true;
            player.getAbilities().setFlySpeed(0.05f * ModConfig.flySpeed);

            // In singleplayer/LAN: also set server-side abilities to prevent kick
            setServerFlightAllowed(client, player, true);

            // Prevent fall damage while flying by resetting fall distance
            if (player.getAbilities().flying) {
                player.fallDistance = 0.0f;
            }
        } else {
            // Only disable if we previously enabled it (don't touch spectator/creative)
            if (!player.isSpectator()) {
                if (wasFlying) {
                    wasFlying = false;
                    if (!player.isOnGround()) {
                        player.sendMessage(
                            net.minecraft.text.Text.literal("\u00a7c[f1sch] \u00a7eFly disabled! You are falling - brace for landing!"),
                            true
                        );
                    } else {
                        player.sendMessage(
                            net.minecraft.text.Text.literal("\u00a7c[f1sch] \u00a7aFly disabled. Safe on the ground."),
                            true
                        );
                    }

                    // In singleplayer/LAN: disable server-side flight too
                    setServerFlightAllowed(client, player, false);
                }
                player.getAbilities().allowFlying = false;
                player.getAbilities().flying = false;
            }
        }
    }

    /**
     * Set allowFlying on the server-side player entity.
     * Only works in singleplayer/LAN where the integrated server is accessible.
     * On dedicated servers, the FeatureSyncPayload handles this instead.
     */
    private static void setServerFlightAllowed(MinecraftClient client, ClientPlayerEntity player, boolean allowed) {
        MinecraftServer server = client.getServer();
        if (server == null) return; // Dedicated server - handled by server addon

        ServerPlayerEntity serverPlayer = server.getPlayerManager().getPlayer(player.getUuid());
        if (serverPlayer == null) return;

        if (allowed) {
            serverPlayer.getAbilities().allowFlying = true;
            serverPlayer.getAbilities().setFlySpeed(0.05f * ModConfig.flySpeed);
        } else {
            if (!serverPlayer.isCreative() && !serverPlayer.isSpectator()) {
                serverPlayer.getAbilities().allowFlying = false;
                serverPlayer.getAbilities().flying = false;
                serverPlayer.getAbilities().setFlySpeed(0.05f);
            }
        }
        serverPlayer.sendAbilitiesUpdate();
    }
}
