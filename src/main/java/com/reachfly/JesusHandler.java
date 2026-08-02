package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class JesusHandler {

    public static void tick(Minecraft client) {
        if (!ModConfig.jesusEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.screen != null) return;

        LocalPlayer player = client.player;

        // Don't interfere if player is sneaking (allow them to sink)
        if (player.isSneaking()) return;

        // Don't interfere if player is already flying
        if (player.getAbilities().flying) return;

        BlockPos feetPos = new BlockPos(
                (int) Math.floor(player.x()),
                (int) Math.floor(player.y() - 0.01),
                (int) Math.floor(player.z()));
        BlockPos belowFeet = new BlockPos(
                (int) Math.floor(player.x()),
                (int) Math.floor(player.y() - 0.5),
                (int) Math.floor(player.z()));

        boolean liquidAtFeet = isLiquid(client.world.getBlockState(feetPos));
        boolean liquidBelow = isLiquid(client.world.getBlockState(belowFeet));
        boolean inLiquid = player.isTouchingWater() || player.isInLava();

        if (!inLiquid && !liquidAtFeet && !liquidBelow) return;

        // Find the surface Y - top of the liquid block
        int surfaceY = feetPos.y();
        // Search upward from feet for the liquid surface
        for (int y = (int) Math.floor(player.y()) + 1; y > (int) Math.floor(player.y()) - 5; y--) {
            BlockPos checkPos = new BlockPos(feetPos.x(), y, feetPos.z());
            BlockPos aboveCheck = new BlockPos(feetPos.x(), y + 1, feetPos.z());
            if (isLiquid(client.world.getBlockState(checkPos)) && !isLiquid(client.world.getBlockState(aboveCheck))) {
                surfaceY = y + 1;
                break;
            }
        }

        double targetY = surfaceY;

        if (player.isSubmergedInWater() || (player.isInLava() && player.y() < targetY - 0.5)) {
            // Submerged - push up fast
            player.setVelocity(
                    player.getVelocity().x,
                    0.3,
                    player.getVelocity().z);
        } else {
            // At or near the surface - snap to surface and simulate ground
            if (player.y() < targetY) {
                player.setPosition(player.x(), targetY, player.z());
            }
            player.setVelocity(
                    player.getVelocity().x,
                    player.getVelocity().y > 0 ? player.getVelocity().y : 0.0,
                    player.getVelocity().z);
            player.setOnGround(true);
            player.fallDistance = 0.0f;
        }
    }

    private static boolean isLiquid(BlockState state) {
        return state.isOf(Blocks.WATER) || state.isOf(Blocks.LAVA)
                || state.getBlock() instanceof LiquidBlock;
    }
}
