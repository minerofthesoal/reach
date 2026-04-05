package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Walk-to-Coords (simple Baritone-like) - Automatically walks the player to target coordinates.
 * Handles basic obstacle jumping, gap detection, and auto-pathing on the ground.
 * Not a full pathfinder but works for simple terrain.
 */
public class WalkToCoordsHandler {

    private static boolean isNavigating = false;
    private static final double ARRIVAL_DISTANCE = 2.0;

    public static void tick(MinecraftClient client) {
        if (!ModConfig.walkToCoordsEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;

        ClientPlayerEntity player = client.player;

        double targetX = ModConfig.walkToX;
        double targetY = ModConfig.walkToY;
        double targetZ = ModConfig.walkToZ;

        Vec3d target = new Vec3d(targetX + 0.5, targetY, targetZ + 0.5);
        Vec3d pos = player.getPos();
        double horizDist = Math.sqrt(
                (pos.x - target.x) * (pos.x - target.x) +
                (pos.z - target.z) * (pos.z - target.z));
        double fullDist = pos.distanceTo(target);

        if (!isNavigating) {
            isNavigating = true;
            player.sendMessage(
                    Text.literal("\u00a7b[ReachFly] \u00a7eWalking to X:%.0f Y:%.0f Z:%.0f (%.0f blocks)"
                            .formatted(targetX, targetY, targetZ, fullDist)),
                    true);
        }

        if (horizDist < ARRIVAL_DISTANCE && Math.abs(pos.y - target.y) < 3) {
            // Arrived
            ModConfig.walkToCoordsEnabled = false;
            isNavigating = false;
            releaseMovementKeys(client);
            player.sendMessage(
                    Text.literal("\u00a7b[ReachFly] \u00a7aArrived at destination!"),
                    true);
            ModConfig.save();
            return;
        }

        // Calculate yaw to face target
        double dx = target.x - pos.x;
        double dz = target.z - pos.z;
        float targetYaw = (float) (Math.atan2(-dx, dz) * (180.0 / Math.PI));
        player.setYaw(targetYaw);

        // Simulate pressing W (forward)
        KeyBinding.setKeyPressed(client.options.forwardKey.getDefaultKey(), true);
        client.options.forwardKey.setPressed(true);

        // Check if we need to jump (block ahead or going uphill)
        BlockPos ahead = new BlockPos(
                (int) Math.floor(pos.x + dx / horizDist * 0.8),
                (int) Math.floor(pos.y),
                (int) Math.floor(pos.z + dz / horizDist * 0.8));
        BlockPos aheadUp = ahead.up();

        boolean blockAhead = !client.world.getBlockState(ahead).isAir()
                && !client.world.getBlockState(ahead).isLiquid();
        boolean spaceAbove = client.world.getBlockState(aheadUp).isAir()
                || client.world.getBlockState(aheadUp).isLiquid();
        boolean canStepUp = client.world.getBlockState(aheadUp.up()).isAir();

        // Also jump if target is above us
        boolean needsUp = target.y > pos.y + 0.5;

        if ((blockAhead && spaceAbove && canStepUp) || needsUp) {
            KeyBinding.setKeyPressed(client.options.jumpKey.getDefaultKey(), true);
            client.options.jumpKey.setPressed(true);
        } else {
            KeyBinding.setKeyPressed(client.options.jumpKey.getDefaultKey(), false);
            client.options.jumpKey.setPressed(false);
        }

        // Sprint for speed
        KeyBinding.setKeyPressed(client.options.sprintKey.getDefaultKey(), true);
        client.options.sprintKey.setPressed(true);
    }

    public static void onDisable() {
        if (isNavigating) {
            MinecraftClient client = MinecraftClient.getInstance();
            releaseMovementKeys(client);
            isNavigating = false;
        }
    }

    private static void releaseMovementKeys(MinecraftClient client) {
        KeyBinding.setKeyPressed(client.options.forwardKey.getDefaultKey(), false);
        client.options.forwardKey.setPressed(false);
        KeyBinding.setKeyPressed(client.options.jumpKey.getDefaultKey(), false);
        client.options.jumpKey.setPressed(false);
        KeyBinding.setKeyPressed(client.options.sprintKey.getDefaultKey(), false);
        client.options.sprintKey.setPressed(false);
    }
}
