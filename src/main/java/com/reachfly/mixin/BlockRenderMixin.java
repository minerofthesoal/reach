package com.reachfly.mixin;

import com.reachfly.ModConfig;
import com.reachfly.XrayHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockRenderMixin {

    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private static void onShouldDrawSide(BlockState state, BlockView world, BlockPos pos,
                                          Direction side, BlockPos otherPos,
                                          CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.xrayEnabled) return;

        Block block = state.getBlock();
        if (XrayHandler.shouldRenderBlock(block)) {
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }
}
