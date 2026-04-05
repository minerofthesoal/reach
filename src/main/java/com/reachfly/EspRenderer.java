package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * ESP line renderer - draws tracer lines from crosshair to glowing entities.
 * Uses 2D screen-space projection for compatibility with 1.21.11 rendering.
 */
public class EspRenderer {

    public static void register() {
        HudRenderCallback.EVENT.register(EspRenderer::renderLines);
    }

    private static void renderLines(DrawContext context, RenderTickCounter tickCounter) {
        if (!ModConfig.espEnabled || !ModConfig.espLines) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        if (client.gameRenderer == null || client.gameRenderer.getCamera() == null) return;

        int screenCenterX = client.getWindow().getScaledWidth() / 2;
        int screenCenterY = client.getWindow().getScaledHeight() / 2;

        float tickDelta = tickCounter.getTickDelta(true);

        // Get camera matrices for world-to-screen projection
        Matrix4f projMatrix = client.gameRenderer.getBasicProjectionMatrix(
                client.gameRenderer.getFov(client.gameRenderer.getCamera(), tickDelta, true));
        MatrixStack modelViewStack = new MatrixStack();

        // Apply camera rotation
        net.minecraft.client.render.Camera camera = client.gameRenderer.getCamera();
        modelViewStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        modelViewStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        Matrix4f mvMatrix = modelViewStack.peek().getPositionMatrix();

        Vec3d cameraPos = camera.getPos();

        for (Entity entity : client.world.getEntities()) {
            if (entity == client.player) continue;
            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;

            if (!shouldShowLine(entity)) continue;

            // Interpolate entity position
            double x = entity.prevX + (entity.getX() - entity.prevX) * tickDelta - cameraPos.x;
            double y = entity.prevY + (entity.getY() - entity.prevY) * tickDelta - cameraPos.y
                    + entity.getHeight() / 2.0;
            double z = entity.prevZ + (entity.getZ() - entity.prevZ) * tickDelta - cameraPos.z;

            // Project to screen space
            Vector4f pos4 = new Vector4f((float) x, (float) y, (float) z, 1.0f);
            pos4.mul(mvMatrix);
            pos4.mul(projMatrix);

            if (pos4.w <= 0) continue; // Behind camera

            float ndcX = pos4.x / pos4.w;
            float ndcY = pos4.y / pos4.w;

            int screenW = client.getWindow().getScaledWidth();
            int screenH = client.getWindow().getScaledHeight();
            int sx = (int) ((ndcX + 1.0f) / 2.0f * screenW);
            int sy = (int) ((1.0f - ndcY) / 2.0f * screenH);

            // Choose color
            int color = getLineColor(entity);

            // Draw line from center to entity
            drawLine(context, screenCenterX, screenCenterY, sx, sy, color);
        }
    }

    private static boolean shouldShowLine(Entity entity) {
        if (entity instanceof PlayerEntity) return ModConfig.espPlayers;
        if (entity instanceof HostileEntity) return ModConfig.espHostile;
        if (entity instanceof MobEntity && !(entity instanceof PassiveEntity)) return ModConfig.espHostile;
        if (entity instanceof PassiveEntity) return ModConfig.espPassive;
        if (entity instanceof LivingEntity) return ModConfig.espHostile;
        return false;
    }

    private static int getLineColor(Entity entity) {
        if (entity instanceof PlayerEntity) return 0xFFFF5555; // Red
        if (entity instanceof HostileEntity) return 0xFFFF8800; // Orange
        if (entity instanceof MobEntity && !(entity instanceof PassiveEntity)) return 0xFFFF8800;
        if (entity instanceof PassiveEntity) return 0xFF55FF55; // Green
        return 0xFFFF8800;
    }

    private static void drawLine(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        // Bresenham-style line drawing using fill rects (1px wide segments)
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int steps = Math.max(dx, dy);
        if (steps == 0) return;

        // Draw thicker line by stepping and filling small rects
        int step = Math.max(1, steps / 100); // limit to ~100 segments for performance
        int cx = x1, cy = y1;
        for (int i = 0; i <= steps; i++) {
            if (i % step == 0 || i == steps) {
                context.fill(cx - 1, cy - 1, cx + 1, cy + 1, color);
            }
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; cx += sx; }
            if (e2 < dx) { err += dx; cy += sy; }
        }
    }
}
