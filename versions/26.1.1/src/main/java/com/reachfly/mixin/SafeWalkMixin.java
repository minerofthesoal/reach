package com.reachfly.mixin;

import com.reachfly.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * SafeWalk: Prevents the player from walking off block edges without
 * applying the sneak speed reduction. Works by making clipAtLedge()
 * return true when the player is on the ground, which gives the
 * edge-stopping behavior of sneaking without the visual/speed penalty.
 */
@Mixin(Player.class)
public class SafeWalkMixin {

    @Inject(method = "isStayingOnGroundSurface", at = @At("HEAD"), cancellable = true)
    private void onClipAtLedge(CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.safeWalkEnabled) return;

        // Only apply to the local player
        Object self = this;
        if (!(self instanceof LocalPlayer)) return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        if (client.screen != null) return;

        // Return true = clip at ledge (prevent falling off)
        // This is what sneaking does internally, but without the speed reduction
        cir.setReturnValue(true);
    }
}
