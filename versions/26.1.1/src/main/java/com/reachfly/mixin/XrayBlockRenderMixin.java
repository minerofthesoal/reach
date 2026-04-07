package com.reachfly.mixin;

import com.reachfly.ModConfig;
import com.reachfly.XrayHandler;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Injects into BlockRenderDispatcher.renderBlock() to cancel rendering of
 * non-valuable blocks when X-Ray is enabled. This is the actual entry point
 * for chunk mesh building - canceling here makes blocks truly invisible.
 */
@Mixin(BlockRenderDispatcher.class)
public class XrayBlockRenderMixin {

    @Inject(method = "renderBatched", at = @At("HEAD"), cancellable = true)
    private void onRenderBlock(BlockState state, BlockPos pos, BlockAndTintGetter world,
                                PoseStack matrices, VertexConsumer vertexConsumer,
                                boolean cull, List<?> buffers, CallbackInfo ci) {
        if (!ModConfig.xrayEnabled) return;

        if (!XrayHandler.shouldRenderBlock(state.getBlock())) {
            ci.cancel();
        }
    }
}
