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

        float tickDelta = tickCounter.getTickProgress(true);

        // Get projection matrix using client FOV setting
        float fov = client.options.getFov().getValue().floatValue();
        Matrix4f projMatrix = client.gameRenderer.getBasicProjectionMatrix(fov);

        // Apply camera rotation
        MatrixStack modelViewStack = new MatrixStack();
        net.minecraft.client.render.Camera camera = client.gameRenderer.getCamera();
        modelViewStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        modelViewStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        Matrix4f mvMatrix = modelViewStack.peek().getPositionMatrix();

        Vec3d cameraPos = camera.getCameraPos();

        for (Entity entity : client.world.getEntities()) {
            if (entity == client.player) continue;
            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;

            if (!shouldShowLine(entity)) continue;

            // Get interpolated position
            Vec3d lerpedPos = entity.getLerpedPos(tickDelta);
            double x = lerpedPos.x - cameraPos.x;
            double y = lerpedPos.y - cameraPos.y + entity.getHeight() / 2.0;
            double z = lerpedPos.z - cameraPos.z;

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

            int color = getLineColor(entity);

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
        if (entity instanceof PlayerEntity) return 0xFFFF5555;
        if (entity instanceof HostileEntity) return 0xFFFF8800;
        if (entity instanceof MobEntity && !(entity instanceof PassiveEntity)) return 0xFFFF8800;
        if (entity instanceof PassiveEntity) return 0xFF55FF55;
        return 0xFFFF8800;
    }

    private static void drawLine(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int steps = Math.max(dx, dy);
        if (steps == 0) return;

        int step = Math.max(1, steps / 100);
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
