package com.reachfly.mixin;

import com.reachfly.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.PlayerInput;
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
@Mixin(Minecraft.class)
public abstract class InvMoveMixin {

    @Shadow public LocalPlayer player;
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
        boolean forward = options.forwardKey.isDown();
        boolean backward = options.backKey.isDown();
        boolean left = options.leftKey.isDown();
        boolean right = options.rightKey.isDown();
        boolean jump = options.jumpKey.isDown();
        boolean sneak = options.sneakKey.isDown();
        boolean sprint = options.sprintKey.isDown();

        PlayerInput input = new PlayerInput(forward, backward, left, right, jump, sneak, sprint);
        player.input.playerInput = input;

        if (sprint && forward) {
            player.setSprinting(true);
        }
    }
}
