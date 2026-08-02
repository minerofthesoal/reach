package com.reachfly.mixin;

import com.reachfly.BaritoneHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts chat messages starting with '#' and routes them to Baritone command handler.
 */
@Mixin(ClientPacketListener.class)
public class ChatCommandMixin {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        if (message.startsWith("#")) {
            BaritoneHandler.parseCommand(message);
            ci.cancel();
        }
    }
}
