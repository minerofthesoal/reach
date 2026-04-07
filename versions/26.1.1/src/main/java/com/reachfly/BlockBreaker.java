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
    public static boolean tryBreak(Minecraft minecraft, BlockPos pos) {
        if (minecraft.player == null || minecraft.gameMode == null) return false;
        if (minecraft.level == null) return false;

        BlockState state = minecraft.level.getBlockState(pos);
        if (state.isAir() || state.isLiquid()) return false;

        // Don't break bedrock or unbreakable blocks
        if (state.getDestroySpeed(minecraft.level, pos) < 0) return false;

        // Start or continue breaking
        if (!pos.equals(currentTarget)) {
            currentTarget = pos;
            breakProgress = 0;
        }

        // Attack the block (starts or continues breaking)
        minecraft.gameMode.startDestroyBlock(pos, Direction.UP);
        minecraft.gameMode.continueDestroyBlock(pos, Direction.UP);
        minecraft.player.swing(net.minecraft.level.InteractionHand.MAIN_HAND);
        breakProgress++;

        // Check if broken (becomes air)
        if (minecraft.level.getBlockState(pos).isAir()) {
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
