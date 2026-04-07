package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

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

        BlockPos feetPos = new BlockPos(
                (int) Math.floor(player.getX()),
                (int) Math.floor(player.getY() - 0.01),
                (int) Math.floor(player.getZ()));
        BlockPos belowFeet = new BlockPos(
                (int) Math.floor(player.getX()),
                (int) Math.floor(player.getY() - 0.5),
                (int) Math.floor(player.getZ()));

        boolean liquidAtFeet = isLiquid(client.world.getBlockState(feetPos));
        boolean liquidBelow = isLiquid(client.world.getBlockState(belowFeet));
        boolean inLiquid = player.isTouchingWater() || player.isInLava();

        if (!inLiquid && !liquidAtFeet && !liquidBelow) return;

        // Find the surface Y - top of the liquid block
        int surfaceY = feetPos.getY();
        // Search upward from feet for the liquid surface
        for (int y = (int) Math.floor(player.getY()) + 1; y > (int) Math.floor(player.getY()) - 5; y--) {
            BlockPos checkPos = new BlockPos(feetPos.getX(), y, feetPos.getZ());
            BlockPos aboveCheck = new BlockPos(feetPos.getX(), y + 1, feetPos.getZ());
            if (isLiquid(client.world.getBlockState(checkPos)) && !isLiquid(client.world.getBlockState(aboveCheck))) {
                surfaceY = y + 1;
                break;
            }
        }

        double targetY = surfaceY;

        if (player.isSubmergedInWater() || (player.isInLava() && player.getY() < targetY - 0.5)) {
            // Submerged - push up fast
            player.setVelocity(
                    player.getVelocity().x,
                    0.3,
                    player.getVelocity().z);
        } else {
            // At or near the surface - snap to surface and simulate ground
            if (player.getY() < targetY) {
                player.setPosition(player.getX(), targetY, player.getZ());
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
                || state.getBlock() instanceof FluidBlock;
    }
}
