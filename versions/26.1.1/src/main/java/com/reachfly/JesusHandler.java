package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class JesusHandler {

    public static void tick(Minecraft client) {
        if (!ModConfig.jesusEnabled) return;
        if (client.player == null || client.level == null) return;
        if (client.screen != null) return;

        LocalPlayer player = client.player;

        // Don't interfere if player is sneaking (allow them to sink)
        if (player.isSneaking()) return;

        // Don't interfere if player is already flying
        if (player.getAbilities().flying) return;

        BlockPos feetPos = new BlockPos(
                (int) Math.floor(player.getX()),
                (int) Math.floor(player.getY() - 0.01),
                (int) Math.floor(player.getZ()));
        BlockPos belowFeet = new BlockPos(
                (int) Math.floor(player.getX()),
                (int) Math.floor(player.getY() - 0.5),
                (int) Math.floor(player.getZ()));

        boolean liquidAtFeet = isLiquid(client.level.getBlockState(feetPos));
        boolean liquidBelow = isLiquid(client.level.getBlockState(belowFeet));
        boolean inLiquid = player.isInWater() || player.isInLava();

        if (!inLiquid && !liquidAtFeet && !liquidBelow) return;

        // Find the surface Y - top of the liquid block
        int surfaceY = feetPos.getY();
        // Search upward from feet for the liquid surface
        for (int y = (int) Math.floor(player.getY()) + 1; y > (int) Math.floor(player.getY()) - 5; y--) {
            BlockPos checkPos = new BlockPos(feetPos.getX(), y, feetPos.getZ());
            BlockPos aboveCheck = new BlockPos(feetPos.getX(), y + 1, feetPos.getZ());
            if (isLiquid(client.level.getBlockState(checkPos)) && !isLiquid(client.level.getBlockState(aboveCheck))) {
                surfaceY = y + 1;
                break;
            }
        }

        double targetY = surfaceY;

        if (player.isUnderWater() || (player.isInLava() && player.getY() < targetY - 0.5)) {
            // Submerged - push up fast
            player.setDeltaMovement(
                    player.getDeltaMovement().x,
                    0.3,
                    player.getDeltaMovement().z);
        } else {
            // At or near the surface - snap to surface and simulate ground
            if (player.getY() < targetY) {
                player.setPosition(player.getX(), targetY, player.getZ());
            }
            player.setDeltaMovement(
                    player.getDeltaMovement().x,
                    player.getDeltaMovement().y > 0 ? player.getDeltaMovement().y : 0.0,
                    player.getDeltaMovement().z);
            player.setOnGround(true);
            player.fallDistance = 0.0f;
        }
    }

    private static boolean isLiquid(BlockState state) {
        return state.isOf(Blocks.WATER) || state.isOf(Blocks.LAVA)
                || state.getBlock() instanceof FluidBlock;
    }
}
