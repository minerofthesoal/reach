package com.reachfly.mixin;

import com.reachfly.ModConfig;
import com.reachfly.ReachHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into ClientPlayerInteractionManager to apply/remove
 * reach attribute modifiers when the player ticks.
 * In 1.21.1, reach is controlled by EntityAttributes rather than methods.
 */
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    /**
     * Inject at the head of tick() to update reach attribute modifiers
     * every tick, ensuring they stay in sync with config.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ReachHandler.updateReachAttributes();
    }
}
