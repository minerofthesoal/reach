package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class EspRenderer {

    public static void register() {
        HudRenderCallback.EVENT.register(EspRenderer::renderEsp);
    }

    private static void renderEsp(GuiGraphics context, DeltaTracker tickCounter) {
        if (!ModConfig.espEnabled) return;
        if (!ModConfig.espLines && !ModConfig.espPathTrace) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) return;
        if (minecraft.gameRenderer == null || minecraft.gameRenderer.getCamera() == null) return;

        int screenCenterX = minecraft.getWindow().getGuiScaledWidth() / 2;
        int screenCenterY = minecraft.getWindow().getGuiScaledHeight() / 2;

        float tickDelta = tickCounter.getTickProgress(true);

        float fov = minecraft.options.getFov().get().floatValue();
        Matrix4f projMatrix = minecraft.gameRenderer.getBasicProjectionMatrix(fov);

        PoseStack modelViewStack = new PoseStack();
        net.minecraft.client.render.Camera camera = minecraft.gameRenderer.getCamera();
        modelViewStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotationDegrees(camera.getXRot()));
        modelViewStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYRot() + 180.0f));
        Matrix4f mvMatrix = modelViewStack.peek().getPositionMatrix();

        Vec3 cameraPos = camera.getCameraPos();
        Vec3 playerPos = minecraft.player.position();

        for (Entity entity : minecraft.level.entitiesForRendering()) {
            if (entity == minecraft.player) continue;
            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;
            if (!shouldShow(entity)) continue;

            Vec3 entityPos = entity.getLerpedPos(tickDelta);
            int color = getColor(entity);

            // Tracer lines from crosshair to entity center
            if (ModConfig.espLines) {
                double ex = entityPos.x - cameraPos.x;
                double ey = entityPos.y - cameraPos.y + entity.getHeight() / 2.0;
                double ez = entityPos.z - cameraPos.z;

                int[] screenPos = projectToScreen(ex, ey, ez, mvMatrix, projMatrix, minecraft);
                if (screenPos != null) {
                    drawLine(context, screenCenterX, screenCenterY, screenPos[0], screenPos[1], color);
                }
            }

            // Path trace - ground-level waypoints from player to entity
            if (ModConfig.espPathTrace) {
                double dx = playerPos.x - entityPos.x;
                double dz = playerPos.z - entityPos.z;
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > 200) continue; // Don't trace very far entities

                // Generate ground-level path points
                int numPoints = Math.min(30, (int) (dist / 2.0) + 1);
                int[] prevScreen = null;

                for (int i = 0; i <= numPoints; i++) {
                    double t = (double) i / numPoints;
                    double px = playerPos.x + (entityPos.x - playerPos.x) * t;
                    double pz = playerPos.z + (entityPos.z - playerPos.z) * t;

                    // Find ground level at this position
                    double py = findGroundY(minecraft.level, px, playerPos.y, pz);

                    // Offset slightly above ground for visibility
                    double wx = px - cameraPos.x;
                    double wy = py + 0.1 - cameraPos.y;
                    double wz = pz - cameraPos.z;

                    int[] screenPos = projectToScreen(wx, wy, wz, mvMatrix, projMatrix, minecraft);
                    if (screenPos != null) {
                        // Draw waypoint dot
                        int dotSize = (i == numPoints) ? 3 : 2;
                        int dotColor = (i == numPoints) ? color : withAlpha(color, 0xBB);
                        context.fill(screenPos[0] - dotSize, screenPos[1] - dotSize,
                                     screenPos[0] + dotSize, screenPos[1] + dotSize, dotColor);

                        // Connect dots with lines
                        if (prevScreen != null) {
                            drawLine(context, prevScreen[0], prevScreen[1],
                                     screenPos[0], screenPos[1], withAlpha(color, 0x88));
                        }
                        prevScreen = screenPos;
                    } else {
                        prevScreen = null;
                    }
                }
            }
        }
    }

    private static double findGroundY(Level world, double x, double startY, double z) {
        int bx = (int) Math.floor(x);
        int bz = (int) Math.floor(z);
        int sy = (int) Math.floor(startY);

        // Search downward from start Y for solid ground
        for (int y = sy + 2; y > sy - 10 && y > world.getMinY(); y--) {
            BlockPos pos = new BlockPos(bx, y, bz);
            BlockPos above = new BlockPos(bx, y + 1, bz);
            if (!world.getBlockState(pos).isAir() && world.getBlockState(above).isAir()) {
                return y + 1;
            }
        }
        // Search upward
        for (int y = sy; y < sy + 10 && y < world.getTopYInclusive(); y++) {
            BlockPos pos = new BlockPos(bx, y, bz);
            BlockPos above = new BlockPos(bx, y + 1, bz);
            if (!world.getBlockState(pos).isAir() && world.getBlockState(above).isAir()) {
                return y + 1;
            }
        }
        return startY;
    }

    private static int[] projectToScreen(double x, double y, double z,
                                          Matrix4f mvMatrix, Matrix4f projMatrix,
                                          Minecraft minecraft) {
        Vector4f pos4 = new Vector4f((float) x, (float) y, (float) z, 1.0f);
        pos4.mul(mvMatrix);
        pos4.mul(projMatrix);

        if (pos4.w <= 0) return null;

        float ndcX = pos4.x / pos4.w;
        float ndcY = pos4.y / pos4.w;

        int screenW = minecraft.getWindow().getGuiScaledWidth();
        int screenH = minecraft.getWindow().getGuiScaledHeight();
        int sx = (int) ((ndcX + 1.0f) / 2.0f * screenW);
        int sy = (int) ((1.0f - ndcY) / 2.0f * screenH);

        return new int[]{sx, sy};
    }

    private static int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    private static boolean shouldShow(Entity entity) {
        if (entity instanceof Player) return ModConfig.espPlayers;
        if (entity instanceof Monster) return ModConfig.espHostile;
        if (entity instanceof Mob && !(entity instanceof Animal)) return ModConfig.espHostile;
        if (entity instanceof Animal) return ModConfig.espPassive;
        if (entity instanceof LivingEntity) return ModConfig.espHostile;
        return false;
    }

    private static int getColor(Entity entity) {
        if (entity instanceof Player) return 0xFFFF5555;
        if (entity instanceof Monster) return 0xFFFF8800;
        if (entity instanceof Mob && !(entity instanceof Animal)) return 0xFFFF8800;
        if (entity instanceof Animal) return 0xFF55FF55;
        return 0xFFFF8800;
    }

    private static void drawLine(GuiGraphics context, int x1, int y1, int x2, int y2, int color) {
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
