package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

/**
 * Auto Fly-to-Coords - Automatically flies (or hack-flies) the player to target coordinates.
 * Uses fly hack if fly is enabled, otherwise uses elytra flight if available.
 * Set target coords via config, then toggle on to start flying there.
 */
public class FlyToCoordsHandler {

    private static boolean isNavigating = false;
    private static final double ARRIVAL_DISTANCE = 3.0;

    public static void tick(MinecraftClient client) {
        if (!ModConfig.flyToCoordsEnabled) return;
        if (client.player == null || client.world == null) return;

        ClientPlayerEntity player = client.player;

        double targetX = ModConfig.flyToX;
        double targetY = ModConfig.flyToY;
        double targetZ = ModConfig.flyToZ;

        Vec3d target = new Vec3d(targetX, targetY, targetZ);
        Vec3d pos = player.getPos();
        double distance = pos.distanceTo(target);

        if (!isNavigating) {
            isNavigating = true;
            player.sendMessage(
                    Text.literal("\u00a7b[ReachFly] \u00a7eFlying to X:%.0f Y:%.0f Z:%.0f (%.0f blocks away)"
                            .formatted(targetX, targetY, targetZ, distance)),
                    true);
        }

        if (distance < ARRIVAL_DISTANCE) {
            // Arrived
            ModConfig.flyToCoordsEnabled = false;
            isNavigating = false;
            player.sendMessage(
                    Text.literal("\u00a7b[ReachFly] \u00a7aArrived at destination!"),
                    true);
            ModConfig.save();
            return;
        }

        // Enable fly if not already
        if (!player.getAbilities().creativeMode) {
            player.getAbilities().allowFlying = true;
            player.getAbilities().flying = true;
            player.fallDistance = 0.0f;
        }

        // Calculate direction and move
        Vec3d direction = target.subtract(pos).normalize();
        float speed = 0.05f * ModConfig.flyToCoordsSpeed;

        player.setVelocity(
                direction.x * speed,
                direction.y * speed,
                direction.z * speed);

        // Face the direction of travel
        float yaw = (float) (Math.atan2(-direction.x, direction.z) * (180.0 / Math.PI));
        float pitch = (float) (Math.atan2(-direction.y,
                Math.sqrt(direction.x * direction.x + direction.z * direction.z))
                * (180.0 / Math.PI));
        player.setYaw(yaw);
        player.setPitch(pitch);
    }

    public static void onDisable() {
        isNavigating = false;
    }
}
