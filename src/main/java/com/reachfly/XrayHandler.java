package com.reachfly;

import net.minecraft.client.MinecraftClient;

/**
 * X-Ray - Makes non-ore blocks transparent so you can see ores through terrain.
 * Works by forcing block culling and making solid blocks invisible,
 * showing only valuable blocks like ores, chests, spawners, etc.
 * Uses internal rendering flag that triggers the xray mixin.
 */
public class XrayHandler {

    private static boolean wasEnabled = false;

    public static void tick(MinecraftClient client) {
        if (client.player == null) return;

        if (ModConfig.xrayEnabled && !wasEnabled) {
            wasEnabled = true;
            // Force chunk rebuild so xray takes effect
            client.worldRenderer.reload();
        } else if (!ModConfig.xrayEnabled && wasEnabled) {
            wasEnabled = false;
            // Rebuild chunks to restore normal rendering
            client.worldRenderer.reload();
        }
    }

    /**
     * Called from the block rendering mixin to determine if a block should be visible.
     * Returns true if the block should be rendered (is an ore/valuable block).
     */
    public static boolean shouldRenderBlock(net.minecraft.block.Block block) {
        if (!ModConfig.xrayEnabled) return true;

        String blockId = net.minecraft.registry.Registries.BLOCK.getId(block).getPath();

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
}
