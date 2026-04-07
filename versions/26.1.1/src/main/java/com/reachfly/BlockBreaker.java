package com.reachfly;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * Shared utility for auto-breaking blocks in the way during FlyTo/WalkTo.
 * Simulates holding left-click on a block until it breaks.
 */
public class BlockBreaker {

    private static BlockPos currentTarget = null;
    private static int breakProgress = 0;

    /**
     * Try to break a block at the given position.
     * Returns true if actively breaking (caller should wait).
     */
    public static boolean tryBreak(Minecraft client, BlockPos pos) {
        if (client.player == null || client.gameMode == null) return false;
        if (client.level == null) return false;

        BlockState state = client.level.getBlockState(pos);
        if (state.isAir() || state.isLiquid()) return false;

        // Don't break bedrock or unbreakable blocks
        if (state.getDestroySpeed(client.level, pos) < 0) return false;

        // Start or continue breaking
        if (!pos.equals(currentTarget)) {
            currentTarget = pos;
            breakProgress = 0;
        }

        // Attack the block (starts or continues breaking)
        client.gameMode.startDestroyBlock(pos, Direction.UP);
        client.gameMode.continueDestroyBlock(pos, Direction.UP);
        client.player.swing(net.minecraft.util.InteractionHand.MAIN_HAND);
        breakProgress++;

        // Check if broken (becomes air)
        if (client.level.getBlockState(pos).isAir()) {
            currentTarget = null;
            breakProgress = 0;
            return false; // Done breaking
        }

        return true; // Still breaking
    }

    public static void reset() {
        currentTarget = null;
        breakProgress = 0;
    }
}
