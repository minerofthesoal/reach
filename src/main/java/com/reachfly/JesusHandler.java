package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

/**
 * Jesus hack - walk on water and lava.
 * Works by detecting when the player is on the surface of a liquid
 * and keeping them from sinking by simulating ground contact.
 */
public class JesusHandler {

    public static void tick(MinecraftClient client) {
        if (!ModConfig.jesusEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;

        ClientPlayerEntity player = client.player;

        // Don't interfere if player is sneaking (allow them to sink)
        if (player.isSneaking()) return;

        // Don't interfere if player is already flying
        if (player.getAbilities().flying) return;

        // Check if the block at the player's feet is liquid
        BlockPos below = player.getBlockPos();
        BlockPos feetPos = new BlockPos(
                (int) Math.floor(player.getX()),
                (int) Math.floor(player.getY() - 0.1),
                (int) Math.floor(player.getZ()));

        boolean inLiquid = player.isTouchingWater() || player.isInLava();
        boolean liquidBelow = client.world.getBlockState(feetPos).isOf(Blocks.WATER)
                || client.world.getBlockState(feetPos).isOf(Blocks.LAVA);
        boolean liquidAtFeet = client.world.getBlockState(below).isOf(Blocks.WATER)
                || client.world.getBlockState(below).isOf(Blocks.LAVA);

        if (inLiquid || liquidBelow || liquidAtFeet) {
            // If the player is submerged, push them up
            if (player.isSubmergedInWater() || player.isInLava()) {
                player.setVelocity(
                        player.getVelocity().x,
                        0.11,
                        player.getVelocity().z);
            } else if (inLiquid) {
                // On the surface - keep them there
                player.setVelocity(
                        player.getVelocity().x,
                        0.0,
                        player.getVelocity().z);
                player.setOnGround(true);
                player.fallDistance = 0.0f;
            }
        }
    }
}
