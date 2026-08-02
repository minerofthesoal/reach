package com.reachfly.mixin;

import com.reachfly.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * InvMove: Allows player movement while inventory/GUI screens are open.
 * Sets PlayerInput based on key states even when a screen is shown.
 */
@Mixin(MinecraftClient.class)
public abstract class InvMoveMixin {

    @Shadow public ClientPlayerEntity player;
    @Shadow public net.minecraft.client.gui.screen.Screen currentScreen;
    @Shadow @Final public GameOptions options;

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void onHandleInputEvents(CallbackInfo ci) {
        if (!ModConfig.invMoveEnabled) return;
        if (currentScreen == null) return;
        if (player == null) return;

        // Skip chat screen - typing should work normally there
        if (currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen) return;

        // Build a PlayerInput from the currently pressed keys
        boolean forward = options.forwardKey.isPressed();
        boolean backward = options.backKey.isPressed();
        boolean left = options.leftKey.isPressed();
        boolean right = options.rightKey.isPressed();
        boolean jump = options.jumpKey.isPressed();
        boolean sneak = options.sneakKey.isPressed();
        boolean sprint = options.sprintKey.isPressed();

        PlayerInput input = new PlayerInput(forward, backward, left, right, jump, sneak, sprint);
        player.input.playerInput = input;

        if (sprint && forward) {
            player.setSprinting(true);
        }
    }
}
