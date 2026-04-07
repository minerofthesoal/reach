package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Auto Fly-to-Coords - Flies the player to target coordinates with smart speed management.
 * Features:
 * - Smooth acceleration and deceleration
 * - Slows down near destination for precision landing
 * - Collision avoidance (rises above obstacles)
 * - Progress updates with distance/ETA in action bar
 * - Auto-disables fly hack on arrival
 */
public class FlyToCoordsHandler {

    private static boolean isNavigating = false;
    private static final double ARRIVAL_DISTANCE = 1.5;
    private static final double SLOWDOWN_DISTANCE = 30.0;
    private static final double MIN_SPEED_FACTOR = 0.15;
    private static int tickCounter = 0;
    private static Vec3d lastPos = null;
    private static int stuckTicks = 0;

    public static void tick(MinecraftClient client) {
        if (!ModConfig.flyToCoordsEnabled) return;
        if (client.player == null || client.world == null) return;

        ClientPlayerEntity player = client.player;

        double targetX = ModConfig.flyToX;
        double targetY = ModConfig.flyToY;
        double targetZ = ModConfig.flyToZ;

        Vec3d target = new Vec3d(targetX + 0.5, targetY, targetZ + 0.5);
        Vec3d pos = player.getPos();
        double horizDist = Math.sqrt(
                (pos.x - target.x) * (pos.x - target.x) +
                (pos.z - target.z) * (pos.z - target.z));
        double distance = pos.distanceTo(target);

        if (!isNavigating) {
            isNavigating = true;
            lastPos = pos;
            stuckTicks = 0;
            tickCounter = 0;
            player.sendMessage(
                    Text.literal("\u00a7b[f1sch] \u00a7eFlying to X:%.0f Y:%.0f Z:%.0f (%.0f blocks away)"
                            .formatted(targetX, targetY, targetZ, distance)),
                    true);
        }

        // Check arrival
        if (distance < ARRIVAL_DISTANCE) {
            ModConfig.flyToCoordsEnabled = false;
            isNavigating = false;
            player.setVelocity(Vec3d.ZERO);
            player.sendMessage(
                    Text.literal("\u00a7b[f1sch] \u00a7aArrived at destination!"),
                    true);
            ModConfig.save();
            return;
        }

        // Enable fly
        if (!player.getAbilities().creativeMode) {
            player.getAbilities().allowFlying = true;
            player.getAbilities().flying = true;
            player.fallDistance = 0.0f;
        }

        // Stuck detection - if barely moved in 40 ticks, try rising
        if (lastPos != null) {
            double movedDist = pos.distanceTo(lastPos);
            if (movedDist < 0.5) {
                stuckTicks++;
            } else {
                stuckTicks = 0;
            }
        }

        // Update last pos every 20 ticks
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            lastPos = pos;

            // Show progress in action bar
            double speed = ModConfig.flyToCoordsSpeed * 0.05 * 20; // rough blocks/sec
            int etaSeconds = (int) (distance / Math.max(speed, 0.1));
            String eta = etaSeconds > 60
                    ? String.format("%dm %ds", etaSeconds / 60, etaSeconds % 60)
                    : String.format("%ds", etaSeconds);
            player.sendMessage(
                    Text.literal(String.format(
                            "\u00a7b[FlyTo] \u00a7f%.0f blocks | ETA: %s | Speed: %.1fx",
                            distance, eta, ModConfig.flyToCoordsSpeed)),
                    true);
        }

        // Calculate direction
        Vec3d direction = target.subtract(pos).normalize();
        float baseSpeed = 0.05f * ModConfig.flyToCoordsSpeed;

        // Speed management - smooth deceleration near target
        double speedFactor = 1.0;
        if (distance < SLOWDOWN_DISTANCE) {
            // Smooth ease-out curve: faster at distance, slower near target
            speedFactor = MIN_SPEED_FACTOR + (1.0 - MIN_SPEED_FACTOR) * (distance / SLOWDOWN_DISTANCE);
        }

        // Collision avoidance - check blocks ahead and rise if needed
        double extraY = 0;
        if (horizDist > 3) {
            BlockPos ahead1 = new BlockPos(
                    (int) Math.floor(pos.x + direction.x * 2),
                    (int) Math.floor(pos.y),
                    (int) Math.floor(pos.z + direction.z * 2));
            BlockPos ahead2 = new BlockPos(
                    (int) Math.floor(pos.x + direction.x * 4),
                    (int) Math.floor(pos.y),
                    (int) Math.floor(pos.z + direction.z * 4));
            BlockPos ahead1Up = ahead1.up();
            BlockPos ahead2Up = ahead2.up();

            boolean blocked = !client.world.getBlockState(ahead1).isAir()
                    || !client.world.getBlockState(ahead2).isAir()
                    || !client.world.getBlockState(ahead1Up).isAir()
                    || !client.world.getBlockState(ahead2Up).isAir();

            if (blocked || stuckTicks > 20) {
                extraY = 0.3; // Rise above obstacles
            }
        }

        // If stuck for a long time, rise more aggressively
        if (stuckTicks > 40) {
            extraY = 0.5;
        }

        float speed = (float) (baseSpeed * speedFactor);
        player.setVelocity(
                direction.x * speed,
                direction.y * speed + extraY,
                direction.z * speed);

        // Face the direction of travel (smooth rotation)
        float targetYaw = (float) (Math.atan2(-direction.x, direction.z) * (180.0 / Math.PI));
        float targetPitch = (float) (Math.atan2(-direction.y,
                Math.sqrt(direction.x * direction.x + direction.z * direction.z))
                * (180.0 / Math.PI));

        // Smooth rotation interpolation
        float currentYaw = player.getYaw();
        float currentPitch = player.getPitch();
        float yawDiff = targetYaw - currentYaw;
        // Normalize yaw diff to -180..180
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;

        player.setYaw(currentYaw + yawDiff * 0.15f);
        player.setPitch(currentPitch + (targetPitch - currentPitch) * 0.15f);
    }

    public static void onDisable() {
        isNavigating = false;
        lastPos = null;
        stuckTicks = 0;
        tickCounter = 0;
    }
}
