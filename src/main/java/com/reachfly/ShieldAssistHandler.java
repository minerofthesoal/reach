package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;

/**
 * Shield Assist - Automatically raises the shield when:
 * - A hostile entity is within melee range and winding up an attack
 * - A projectile (arrow, fireball, etc.) is heading toward the player
 * - Another player is swinging at you
 */
public class ShieldAssistHandler {

    private static boolean isBlocking = false;

    /**
     * Called every client tick.
     */
    public static void tick(MinecraftClient client) {
        if (!ModConfig.shieldAssistEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) {
            stopBlocking(client);
            return;
        }

        ClientPlayerEntity player = client.player;

        // Check if player has a shield in either hand
        boolean hasShield = player.getMainHandStack().getItem() instanceof ShieldItem
                || player.getOffHandStack().getItem() instanceof ShieldItem;

        if (!hasShield) {
            stopBlocking(client);
            return;
        }

        boolean shouldBlock = false;

        // Check for nearby threats
        for (Entity entity : client.world.getEntities()) {
            if (entity == player) continue;

            // Check for incoming projectiles
            if (entity instanceof ProjectileEntity projectile) {
                double dist = player.distanceTo(projectile);
                if (dist < 8.0) {
                    // Check if projectile is heading toward us
                    double dx = player.getX() - projectile.getX();
                    double dz = player.getZ() - projectile.getZ();
                    double vx = projectile.getVelocity().x;
                    double vz = projectile.getVelocity().z;
                    // Dot product: positive means heading toward us
                    if (dx * vx + dz * vz > 0) {
                        shouldBlock = true;
                        break;
                    }
                }
            }

            // Check for nearby hostile entities or attacking players
            if (entity instanceof LivingEntity living && living.isAlive()) {
                double dist = player.distanceTo(living);

                if (living instanceof HostileEntity && dist < 4.0) {
                    shouldBlock = true;
                    break;
                }

                if (living instanceof PlayerEntity other && dist < 5.0) {
                    // Block if they're swinging at us
                    if (other.handSwinging) {
                        shouldBlock = true;
                        break;
                    }
                }
            }
        }

        if (shouldBlock && !isBlocking) {
            client.options.useKey.setPressed(true);
            isBlocking = true;
        } else if (!shouldBlock && isBlocking) {
            stopBlocking(client);
        }
    }

    private static void stopBlocking(MinecraftClient client) {
        if (!isBlocking) return;
        client.options.useKey.setPressed(false);
        isBlocking = false;
    }
}
