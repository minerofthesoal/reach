package com.reachfly;

import net.minecraft.client.Minecraft;

/**
 * X-Ray - Makes non-ore blocks invisible so you can see ores through terrain.
 * Works via three mechanisms:
 *   1. XrayBlockRenderMixin cancels BlockRenderDispatcher.renderBlock() for non-valuable blocks
 *   2. BlockRenderMixin forces shouldDrawSide=true for valuable blocks (all faces visible)
 *   3. BlockStateMixin makes non-valuable blocks non-opaque (disables occlusion culling)
 *
 * When toggled, forces a full chunk rebuild so changes take effect immediately.
 * Also applies fullbright so ores underground are visible.
 */
public class XrayHandler {

    private static boolean wasEnabled = false;
    private static boolean needsReload = false;
    private static int reloadDelay = 0;

    public static void tick(Minecraft client) {
        if (client.player == null || client.levelRenderer == null) return;

        // Handle delayed reload (wait a tick for state to propagate)
        if (needsReload) {
            reloadDelay--;
            if (reloadDelay <= 0) {
                needsReload = false;
                client.levelRenderer.allChanged();
            }
        }

        if (ModConfig.xrayEnabled && !wasEnabled) {
            wasEnabled = true;
            // Schedule a chunk rebuild with a 1-tick delay
            needsReload = true;
            reloadDelay = 1;
        } else if (!ModConfig.xrayEnabled && wasEnabled) {
            wasEnabled = false;
            needsReload = true;
            reloadDelay = 1;
        }

        // Force fullbright when xray is active so ores are visible underground
        if (ModConfig.xrayEnabled) {
            // Handled by FullbrightHandler integration - just ensure gamma is high
            // This is a backup; the gamma approach in FullbrightHandler handles the main case
        }
    }

    /**
     * Called from the block rendering mixins to determine if a block should be visible.
     * Returns true if the block should be rendered (is an ore/valuable block).
     */
    public static boolean shouldRenderBlock(net.minecraft.world.level.block.Block block) {
        if (!ModConfig.xrayEnabled) return true;

        String blockId = net.minecraft.registry.BuiltInRegistries.BLOCK.getId(block).getPath();

        // Ores
        if (blockId.contains("ore")) return true;

        // Valuable blocks
        if (blockId.contains("diamond") || blockId.contains("emerald") ||
            blockId.contains("ancient_debris") || blockId.contains("netherite")) return true;

        // Storage/utility
        if (blockId.contains("chest") || blockId.contains("barrel") ||
            blockId.contains("shulker") || blockId.contains("hopper")) return true;

        // Spawners and end portal
        if (blockId.contains("spawner") || blockId.contains("end_portal") ||
            blockId.contains("end_gateway")) return true;

        // Beacons and enchanting
        if (blockId.contains("beacon") || blockId.contains("enchanting")) return true;

        // Amethyst
        if (blockId.contains("amethyst") || blockId.contains("budding")) return true;

        // Lava and water (useful for caving)
        if (blockId.equals("lava") || blockId.equals("water")) return true;

        // TNT
        if (blockId.equals("tnt")) return true;

        return false;
    }

    /**
     * Returns true when X-Ray is active. Used by FullbrightHandler
     * to force gamma high so ores are visible underground.
     */
    public static boolean isActive() {
        return ModConfig.xrayEnabled;
    }
}
