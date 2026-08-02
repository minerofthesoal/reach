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

import java.util.List;

/**
 * Injects into BlockRenderManager.renderBlock() to cancel rendering of
 * non-valuable blocks when X-Ray is enabled. This is the actual entry point
 * for chunk mesh building - canceling here makes blocks truly invisible.
 */
@Mixin(BlockRenderManager.class)
public class XrayBlockRenderMixin {

    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
    private void onRenderBlock(BlockState state, BlockPos pos, BlockRenderView world,
                                MatrixStack matrices, VertexConsumer vertexConsumer,
                                boolean cull, List<?> buffers, CallbackInfo ci) {
        if (!ModConfig.xrayEnabled) return;

        if (!XrayHandler.shouldRenderBlock(state.getBlock())) {
            ci.cancel();
        }
    }
}
