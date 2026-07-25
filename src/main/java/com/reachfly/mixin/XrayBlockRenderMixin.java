package com.reachfly.mixin;

import com.reachfly.ModConfig;
import com.reachfly.XrayHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects into BlockRenderManager.renderBlock() to cancel rendering of
 * non-valuable blocks when X-Ray is enabled.
 *
 * FIX: Removed the bogus List<?> buffers parameter that doesn't exist in
 * BlockRenderManager.renderBlock() in MC 1.21.x. The mixin's required=true
 * caused the game to crash silently at startup when the method signature
 * didn't match. The actual signature is:
 *   renderBlock(BlockState, BlockPos, BlockRenderView, MatrixStack, VertexConsumer, boolean, Random)
 * but Mixin can match positional params; we just drop the non-existent last arg.
 */
@Mixin(BlockRenderManager.class)
public class XrayBlockRenderMixin {

    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
    private void onRenderBlock(BlockState state, BlockPos pos, BlockRenderView world,
                                MatrixStack matrices, VertexConsumer vertexConsumer,
                                boolean cull, CallbackInfo ci) {
        if (!ModConfig.xrayEnabled) return;

        if (!XrayHandler.shouldRenderBlock(state.getBlock())) {
            ci.cancel();
        }
    }
}
