package com.reachfly.mixin;

import com.reachfly.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "attack", at = @At("TAIL"))
    private void onAttackEntity(Player player, Entity target, CallbackInfo ci) {
        if (!ModConfig.knockbackEnabled) return;
        if (!(player instanceof LocalPlayer)) return;

        Minecraft client = Minecraft.getInstance();
        MinecraftServer server = client.getSingleplayerServer();
        if (server == null) return;

        for (ServerLevel world : server.getAllLevels()) {
            Entity serverTarget = world.getEntity(target.getId());
            if (serverTarget != null) {
                Vec3 playerPos = new Vec3(player.getX(), player.getY(), player.getZ());
                Vec3 targetPos = new Vec3(serverTarget.getX(), serverTarget.getY(), serverTarget.getZ());
                Vec3 direction = targetPos.subtract(playerPos);

                double horizLength = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
                if (horizLength < 0.01) {
                    float yaw = player.getYRot();
                    direction = new Vec3(-Math.sin(Math.toRadians(yaw)), 0, Math.cos(Math.toRadians(yaw)));
                    horizLength = 1.0;
                }

                double normalX = direction.x / horizLength;
                double normalZ = direction.z / horizLength;
                double strength = ModConfig.knockbackStrength;
                double velocityMult = strength * 0.5;
                double verticalBoost = Math.min(strength * 0.15, 80.0);

                // Reset velocity first, then use addVelocity which marks velocity as dirty
                // ensuring the server syncs it to all clients
                serverTarget.setDeltaMovement(Vec3.ZERO);
                serverTarget.push(
                    normalX * velocityMult,
                    verticalBoost,
                    normalZ * velocityMult);
                break;
            }
        }
    }
}
