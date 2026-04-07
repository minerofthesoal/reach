package com.reachfly.mixin;

import com.reachfly.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.Options;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * InvMove: Allows player movement while inventory/GUI screens are open.
 * Sets Input based on key states even when a screen is shown.
 */
@Mixin(Minecraft.class)
public abstract class InvMoveMixin {

    @Shadow public LocalPlayer player;
    @Shadow public net.minecraft.client.gui.screens.Screen screen;
    @Shadow @Final public Options options;

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void onHandleKeybinds(CallbackInfo ci) {
        if (!ModConfig.invMoveEnabled) return;
        if (screen == null) return;
        if (player == null) return;

        if (screen instanceof net.minecraft.client.gui.screens.ChatScreen) return;

        boolean forward = options.keyUp.isDown();
        boolean backward = options.keyDown.isDown();
        boolean left = options.keyLeft.isDown();
        boolean right = options.keyRight.isDown();
        boolean jump = options.keyJump.isDown();
        boolean sneak = options.keyShift.isDown();
        boolean sprint = options.keySprint.isDown();

        Input input = new Input(forward, backward, left, right, jump, sneak, sprint);
        player.input = input;

        if (sprint && forward) {
            player.setSprinting(true);
        }
    }
}
