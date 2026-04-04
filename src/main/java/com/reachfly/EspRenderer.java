package com.reachfly;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/**
 * ESP/Wallhack - Renders colored bounding boxes around entities visible through walls.
 * Players = red, hostiles = orange, passives = green.
 */
public class EspRenderer {

    public static void register() {
        WorldRenderEvents.LAST.register(EspRenderer::onWorldRender);
    }

    private static void onWorldRender(WorldRenderContext context) {
        if (!ModConfig.espEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();
        float tickDelta = context.tickCounter().getTickDelta(true);

        // Enable rendering through walls
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        for (Entity entity : client.world.getEntities()) {
            if (entity == client.player) continue;
            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;

            // Filter by entity type based on config
            float r, g, b;
            if (entity instanceof PlayerEntity) {
                if (!ModConfig.espPlayers) continue;
                r = 1.0f; g = 0.2f; b = 0.2f; // Red
            } else if (entity instanceof HostileEntity) {
                if (!ModConfig.espHostile) continue;
                r = 1.0f; g = 0.6f; b = 0.0f; // Orange
            } else if (entity instanceof PassiveEntity) {
                if (!ModConfig.espPassive) continue;
                r = 0.2f; g = 1.0f; b = 0.2f; // Green
            } else {
                if (!ModConfig.espHostile) continue;
                r = 0.8f; g = 0.8f; b = 0.2f; // Yellow for other living entities
            }

            // Interpolate entity position for smooth rendering
            double x = entity.prevX + (entity.getX() - entity.prevX) * tickDelta - cameraPos.x;
            double y = entity.prevY + (entity.getY() - entity.prevY) * tickDelta - cameraPos.y;
            double z = entity.prevZ + (entity.getZ() - entity.prevZ) * tickDelta - cameraPos.z;

            Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());

            matrices.push();
            matrices.translate(x, y, z);

            drawBox(matrices, box, r, g, b, 0.6f);

            matrices.pop();
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    /**
     * Draw a wireframe box using line strips.
     */
    private static void drawBox(MatrixStack matrices, Box box, float r, float g, float b, float a) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        // Bottom face
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);

        // Top face
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a);

        // Vertical edges
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }
}
