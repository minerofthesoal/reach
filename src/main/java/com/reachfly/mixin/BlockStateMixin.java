package com.reachfly.mixin;

import com.reachfly.ModConfig;
import com.reachfly.XrayHandler;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Disables chunk occlusion culling when X-Ray is active by making
 * non-valuable blocks report as non-opaque. This lets the renderer
 * "see through" stone/dirt to render ores underground.
 */
@Mixin(Block.AbstractBlockState.class)
public abstract class BlockStateMixin {

    @Shadow
    public abstract Block getBlock();

    @Inject(method = "isOpaque", at = @At("HEAD"), cancellable = true)
    private void onIsOpaque(CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.xrayEnabled) {
            if (!XrayHandler.shouldRenderBlock(getBlock())) {
                cir.setReturnValue(false);
            }
        }
    }
}
