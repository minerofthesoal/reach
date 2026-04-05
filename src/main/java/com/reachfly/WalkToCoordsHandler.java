package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Walk-to-Coords - Automatically walks the player to target coordinates.
 * Features:
 * - Smart obstacle detection (jump over 1-block, walk around 2+ block walls)
 * - Gap/hole detection (avoids walking into ravines)
 * - Unstuck detection with automatic rerouting
 * - Water/lava escape (jumps out of liquids)
 * - Progress updates with distance in action bar
 * - Sprint management based on hunger
 * - Smooth yaw rotation for natural-looking movement
 */
public class WalkToCoordsHandler {

    private static boolean isNavigating = false;
    private static final double ARRIVAL_DISTANCE = 2.0;
    private static int tickCounter = 0;
    private static Vec3d lastPos = null;
    private static int stuckTicks = 0;
    private static float detourYawOffset = 0;
    private static int detourTicks = 0;

    public static void tick(MinecraftClient client) {
        if (!ModConfig.walkToCoordsEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;

        ClientPlayerEntity player = client.player;

        double targetX = ModConfig.walkToX;
        double targetY = ModConfig.walkToY;
        double targetZ = ModConfig.walkToZ;

        Vec3d target = new Vec3d(targetX + 0.5, targetY, targetZ + 0.5);
        Vec3d pos = player.getEntityPos();
        double horizDist = Math.sqrt(
                (pos.x - target.x) * (pos.x - target.x) +
                (pos.z - target.z) * (pos.z - target.z));

        if (!isNavigating) {
            isNavigating = true;
            lastPos = pos;
            stuckTicks = 0;
            detourYawOffset = 0;
            detourTicks = 0;
            tickCounter = 0;
            player.sendMessage(
                    Text.literal("\u00a7b[ReachFly] \u00a7eWalking to X:%.0f Y:%.0f Z:%.0f (%.0f blocks)"
                            .formatted(targetX, targetY, targetZ, horizDist)),
                    true);
        }

        // Check arrival
        if (horizDist < ARRIVAL_DISTANCE && Math.abs(pos.y - target.y) < 4) {
            ModConfig.walkToCoordsEnabled = false;
            isNavigating = false;
            releaseAllKeys(client);
            player.sendMessage(
                    Text.literal("\u00a7b[ReachFly] \u00a7aArrived at destination!"),
                    true);
            ModConfig.save();
            return;
        }

        // Stuck detection
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            if (lastPos != null) {
                double moved = Math.sqrt(
                        (pos.x - lastPos.x) * (pos.x - lastPos.x) +
                        (pos.z - lastPos.z) * (pos.z - lastPos.z));
                if (moved < 0.5) {
                    stuckTicks += 20;
                } else {
                    stuckTicks = Math.max(0, stuckTicks - 10);
                    // If we were detouring and moved, start reducing detour
                    if (detourTicks > 0 && moved > 1.0) {
                        detourTicks = Math.max(0, detourTicks - 20);
                        if (detourTicks <= 0) {
                            detourYawOffset = 0;
                        }
                    }
                }
            }
            lastPos = pos;

            // Progress update
            player.sendMessage(
                    Text.literal(String.format(
                            "\u00a7b[WalkTo] \u00a7f%.0f blocks remaining%s",
                            horizDist,
                            stuckTicks > 40 ? " \u00a7e(rerouting...)" : "")),
                    true);
        }

        // If stuck for too long, try detouring
        if (stuckTicks > 40 && detourTicks <= 0) {
            // Pick a detour direction (alternate left/right)
            detourYawOffset = (detourYawOffset == 0) ? 70 : -detourYawOffset;
            if (Math.abs(detourYawOffset) < 50) detourYawOffset = 70;
            detourTicks = 60; // detour for 3 seconds
            stuckTicks = 0;
        }
        if (stuckTicks > 100) {
            // Severely stuck - try opposite direction briefly
            detourYawOffset = 180;
            detourTicks = 40;
            stuckTicks = 0;
        }

        // Tick down detour
        if (detourTicks > 0) {
            detourTicks--;
            if (detourTicks <= 0) {
                detourYawOffset = 0;
            }
        }

        // Calculate yaw to face target
        double dx = target.x - pos.x;
        double dz = target.z - pos.z;
        float targetYaw = (float) (Math.atan2(-dx, dz) * (180.0 / Math.PI));

        // Apply detour offset
        targetYaw += detourYawOffset;

        // Smooth rotation
        float currentYaw = player.getYaw();
        float yawDiff = targetYaw - currentYaw;
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;
        player.setYaw(currentYaw + yawDiff * 0.25f);

        // Simulate pressing W (forward)
        KeyBinding.setKeyPressed(client.options.forwardKey.getDefaultKey(), true);
        client.options.forwardKey.setPressed(true);

        // Calculate look direction for block checks (use actual facing, not target)
        float facingYaw = player.getYaw();
        double faceDx = -Math.sin(Math.toRadians(facingYaw));
        double faceDz = Math.cos(Math.toRadians(facingYaw));

        // Check blocks ahead at feet and head level
        BlockPos feetAhead = new BlockPos(
                (int) Math.floor(pos.x + faceDx * 1.0),
                (int) Math.floor(pos.y),
                (int) Math.floor(pos.z + faceDz * 1.0));
        BlockPos headAhead = new BlockPos(
                (int) Math.floor(pos.x + faceDx * 1.0),
                (int) Math.floor(pos.y + 1),
                (int) Math.floor(pos.z + faceDz * 1.0));
        BlockPos feetFar = new BlockPos(
                (int) Math.floor(pos.x + faceDx * 2.0),
                (int) Math.floor(pos.y),
                (int) Math.floor(pos.z + faceDz * 2.0));

        boolean solidAtFeet = isSolid(client.world.getBlockState(feetAhead));
        boolean solidAtHead = isSolid(client.world.getBlockState(headAhead));
        boolean solidAboveHead = isSolid(client.world.getBlockState(headAhead.up()));
        boolean clearAboveFeet = !isSolid(client.world.getBlockState(feetAhead.up()))
                && !isSolid(client.world.getBlockState(feetAhead.up().up()));

        // Check for gaps/holes ahead
        BlockPos groundAhead = new BlockPos(
                (int) Math.floor(pos.x + faceDx * 1.5),
                (int) Math.floor(pos.y - 1),
                (int) Math.floor(pos.z + faceDz * 1.5));
        boolean gapAhead = !isSolid(client.world.getBlockState(groundAhead))
                && !isSolid(client.world.getBlockState(groundAhead.down()))
                && !client.world.getBlockState(groundAhead).isLiquid();

        // Check if in liquid (escape by jumping)
        boolean inLiquid = player.isTouchingWater() || player.isInLava();

        // Jump logic
        boolean shouldJump = false;

        // Jump over 1-block obstacles
        if (solidAtFeet && !solidAtHead && clearAboveFeet) {
            shouldJump = true;
        }

        // Jump when target is above
        if (target.y > pos.y + 0.5) {
            shouldJump = true;
        }

        // Jump out of liquids
        if (inLiquid) {
            shouldJump = true;
        }

        // Jump over small gaps (1-2 block)
        if (gapAhead && !inLiquid && horizDist > 3) {
            shouldJump = true;
        }

        // 2-block wall ahead - need to detour instead of jump
        if (solidAtFeet && solidAtHead && detourTicks <= 0) {
            detourYawOffset = (detourYawOffset >= 0) ? 90 : -90;
            if (detourYawOffset == 0) detourYawOffset = 90;
            detourTicks = 40;
            shouldJump = false;
        }

        if (shouldJump) {
            KeyBinding.setKeyPressed(client.options.jumpKey.getDefaultKey(), true);
            client.options.jumpKey.setPressed(true);
        } else {
            KeyBinding.setKeyPressed(client.options.jumpKey.getDefaultKey(), false);
            client.options.jumpKey.setPressed(false);
        }

        // Sprint if hunger is high enough (> 6) and not in liquid
        boolean canSprint = player.getHungerManager().getFoodLevel() > 6 && !inLiquid;
        KeyBinding.setKeyPressed(client.options.sprintKey.getDefaultKey(), canSprint);
        client.options.sprintKey.setPressed(canSprint);
    }

    private static boolean isSolid(BlockState state) {
        return !state.isAir() && !state.isLiquid() && state.isSolid();
    }

    public static void onDisable() {
        if (isNavigating) {
            MinecraftClient client = MinecraftClient.getInstance();
            releaseAllKeys(client);
        }
        isNavigating = false;
        lastPos = null;
        stuckTicks = 0;
        detourYawOffset = 0;
        detourTicks = 0;
        tickCounter = 0;
    }

    private static void releaseAllKeys(MinecraftClient client) {
        KeyBinding.setKeyPressed(client.options.forwardKey.getDefaultKey(), false);
        client.options.forwardKey.setPressed(false);
        KeyBinding.setKeyPressed(client.options.jumpKey.getDefaultKey(), false);
        client.options.jumpKey.setPressed(false);
        KeyBinding.setKeyPressed(client.options.sprintKey.getDefaultKey(), false);
        client.options.sprintKey.setPressed(false);
    }
}
