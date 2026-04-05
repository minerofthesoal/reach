package com.reachfly.mixin;

import com.reachfly.KnockbackHandler;
import com.reachfly.ReachHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ReachHandler.updateReachAttributes();
    }

    @Inject(method = "attackEntity", at = @At("TAIL"))
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (player instanceof ClientPlayerEntity clientPlayer) {
            KnockbackHandler.onAttack(clientPlayer, target);
        }
    }
}
