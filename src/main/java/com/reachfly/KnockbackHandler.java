package com.reachfly;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class KnockbackHandler {

    private static int cooldownTicks = 0;

    public static void tick(MinecraftClient client) {
        if (!ModConfig.knockbackEnabled) return;
        if (client.player == null || client.world == null) return;
        if (cooldownTicks > 0) cooldownTicks--;
    }

    public static void onAttack(ClientPlayerEntity player, Entity target) {
        if (!ModConfig.knockbackEnabled) return;
        if (!(target instanceof LivingEntity)) return;

        Vec3d playerPos = player.getEntityPos();
        Vec3d targetPos = target.getEntityPos();
        Vec3d direction = targetPos.subtract(playerPos);

        double horizLength = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        if (horizLength < 0.01) {
            float yaw = player.getYaw();
            direction = new Vec3d(-Math.sin(Math.toRadians(yaw)), 0, Math.cos(Math.toRadians(yaw)));
            horizLength = 1.0;
        }

        double normalX = direction.x / horizLength;
        double normalZ = direction.z / horizLength;
        double strength = ModConfig.knockbackStrength;
        double velocityMult = strength * 0.15;
        double verticalBoost = Math.min(strength * 0.05, 50.0);

        target.setVelocity(normalX * velocityMult, verticalBoost, normalZ * velocityMult);
    }
}
