package com.reachfly;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

/**
 * Scaffold - Automatically places blocks under your feet as you walk.
 * Great for bridging across gaps. Uses blocks from your hotbar.
 */
public class ScaffoldHandler {

    private static int cooldown = 0;

    public static void tick(Minecraft client) {
        if (!ModConfig.scaffoldEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.interactionManager == null) return;
        if (client.screen != null) return;

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        LocalPlayer player = client.player;
        BlockPos below = new BlockPos(
                (int) Math.floor(player.x()),
                (int) Math.floor(player.y() - 1),
                (int) Math.floor(player.z()));

        // Only place if air/liquid below us
        BlockState belowState = client.world.getBlockState(below);
        if (!belowState.isAir() && !belowState.isLiquid()) return;

        // Find a block item in hotbar
        int origSlot = player.getInventory().getSelectedSlot();
        int blockSlot = -1;
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).getItem() instanceof BlockItem) {
                blockSlot = i;
                break;
            }
        }
        if (blockSlot < 0) return;

        // Switch to block slot
        player.getInventory().setSelectedSlot(blockSlot);

        // Find a solid face to place against
        Direction[] dirs = {Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP};
        for (Direction dir : dirs) {
            BlockPos neighbor = below.offset(dir);
            BlockState neighborState = client.world.getBlockState(neighbor);
            if (!neighborState.isAir() && !neighborState.isLiquid()) {
                // Place against this neighbor
                BlockHitResult hit = new BlockHitResult(
                        Vec3.ofCenter(neighbor),
                        dir.getOpposite(),
                        below, false);
                client.interactionManager.interactBlock(player, InteractionHand.MAIN_HAND, hit);
                cooldown = 2; // Small cooldown to prevent spam
                break;
            }
        }

        // Restore slot
        player.getInventory().setSelectedSlot(origSlot);
    }
}
