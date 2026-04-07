package com.reachfly;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * Scaffold - Automatically places blocks under your feet as you walk.
 * Great for bridging across gaps. Uses blocks from your hotbar.
 */
public class ScaffoldHandler {

    private static int cooldown = 0;

    public static void tick(MinecraftClient client) {
        if (!ModConfig.scaffoldEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.interactionManager == null) return;
        if (client.currentScreen != null) return;

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        ClientPlayerEntity player = client.player;
        BlockPos below = new BlockPos(
                (int) Math.floor(player.getX()),
                (int) Math.floor(player.getY() - 1),
                (int) Math.floor(player.getZ()));

        // Only place if air/liquid below us
        BlockState belowState = client.world.getBlockState(below);
        if (!belowState.isAir() && !belowState.isLiquid()) return;

        // Find a block item in hotbar
        int origSlot = player.getInventory().selectedSlot;
        int blockSlot = -1;
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).getItem() instanceof BlockItem) {
                blockSlot = i;
                break;
            }
        }
        if (blockSlot < 0) return;

        // Switch to block slot
        player.getInventory().selectedSlot = blockSlot;

        // Find a solid face to place against
        Direction[] dirs = {Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP};
        for (Direction dir : dirs) {
            BlockPos neighbor = below.offset(dir);
            BlockState neighborState = client.world.getBlockState(neighbor);
            if (!neighborState.isAir() && !neighborState.isLiquid()) {
                // Place against this neighbor
                BlockHitResult hit = new BlockHitResult(
                        Vec3d.ofCenter(neighbor),
                        dir.getOpposite(),
                        below, false);
                client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
                cooldown = 2; // Small cooldown to prevent spam
                break;
            }
        }

        // Restore slot
        player.getInventory().selectedSlot = origSlot;
    }
}
