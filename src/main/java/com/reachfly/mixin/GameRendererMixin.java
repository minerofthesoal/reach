package com.reachfly.mixin;

import com.reachfly.ModConfig;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mixin into GameRenderer to extend the crosshair raycast distances.
 * In 1.21.1, updateCrosshairTarget reads the player's block and entity
 * interaction range attributes, then passes them as double locals to
 * findCrosshairTarget. We modify both locals to match our custom reach.
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    /**
     * Modify the block interaction range (first double local).
     * This controls how far the crosshair can target blocks.
     */
    @ModifyVariable(
            method = "updateCrosshairTarget",
            at = @At("STORE"),
            ordinal = 0
    )
    private double modifyBlockReachDistance(double original) {
        if (ModConfig.reachEnabled) {
            return (double) ModConfig.reachDistance;
        }
        return original;
    }

    /**
     * Modify the entity interaction range (second double local).
     * This controls how far the crosshair can target entities.
     */
    @ModifyVariable(
            method = "updateCrosshairTarget",
            at = @At("STORE"),
            ordinal = 1
    )
    private double modifyEntityReachDistance(double original) {
        if (ModConfig.reachEnabled) {
            return (double) ModConfig.reachDistance;
        }
        return original;
    }
}
