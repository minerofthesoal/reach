package com.reachfly;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

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
    public static boolean tryBreak(MinecraftClient client, BlockPos pos) {
        if (client.player == null || client.interactionManager == null) return false;
        if (client.world == null) return false;

        BlockState state = client.world.getBlockState(pos);
        if (state.isAir() || state.isLiquid()) return false;

        // Don't break bedrock or unbreakable blocks
        if (state.getHardness(client.world, pos) < 0) return false;

        // Start or continue breaking
        if (!pos.equals(currentTarget)) {
            currentTarget = pos;
            breakProgress = 0;
        }

        // Attack the block (starts or continues breaking)
        client.interactionManager.attackBlock(pos, Direction.UP);
        client.interactionManager.updateBlockBreakingProgress(pos, Direction.UP);
        client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
        breakProgress++;

        // Check if broken (becomes air)
        if (client.world.getBlockState(pos).isAir()) {
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
