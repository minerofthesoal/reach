package com.reachfly.mixin;

import com.reachfly.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "attackEntity", at = @At("TAIL"))
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (!ModConfig.knockbackEnabled) return;
        if (!(player instanceof ClientPlayerEntity)) return;

        MinecraftClient client = MinecraftClient.getInstance();
        MinecraftServer server = client.getServer();
        if (server == null) return;

        for (ServerWorld world : server.getWorlds()) {
            Entity serverTarget = world.getEntityById(target.getId());
            if (serverTarget != null) {
                Vec3d playerPos = new Vec3d(player.getX(), player.getY(), player.getZ());
                Vec3d targetPos = new Vec3d(serverTarget.getX(), serverTarget.getY(), serverTarget.getZ());
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
                double velocityMult = strength * 0.5;
                double verticalBoost = Math.min(strength * 0.15, 80.0);

                // Reset velocity first, then use addVelocity which marks velocity as dirty
                // ensuring the server syncs it to all clients
                serverTarget.setVelocity(Vec3d.ZERO);
                serverTarget.addVelocity(
                    normalX * velocityMult,
                    verticalBoost,
                    normalZ * velocityMult);
                break;
            }
        }
    }
}
