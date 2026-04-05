package com.reachfly;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {

    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of("reachfly", "category"));

    public static KeyBinding toggleReach;
    public static KeyBinding toggleFly;
    public static KeyBinding toggleEsp;
    public static KeyBinding toggleAutoHit;
    public static KeyBinding toggleLowHealthKill;
    public static KeyBinding toggleEatingAssist;
    public static KeyBinding toggleShieldAssist;
    public static KeyBinding toggleAutoKillWhenLow;
    public static KeyBinding toggleJesus;
    public static KeyBinding toggleAutoElytraSwap;
    public static KeyBinding toggleFlyToCoords;
    public static KeyBinding toggleNoFall;
    public static KeyBinding toggleFullbright;
    public static KeyBinding toggleSpeed;
    public static KeyBinding toggleHud;
    public static KeyBinding toggleDupe;
    public static KeyBinding openConfig;

    public static void register() {
        toggleReach = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_reach",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY));

        toggleFly = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_fly",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY));

        toggleEsp = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_esp",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY));

        toggleAutoHit = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_autohit",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY));

        toggleLowHealthKill = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_lowhealthkill",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY));

        toggleEatingAssist = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_eating",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY));

        toggleShieldAssist = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_shield",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, CATEGORY));

        toggleAutoKillWhenLow = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_autokilllow",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, CATEGORY));

        toggleJesus = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_jesus",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, CATEGORY));

        toggleAutoElytraSwap = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_elytraswap",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Y, CATEGORY));

        toggleFlyToCoords = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_flytocoords",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, CATEGORY));

        toggleNoFall = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_nofall",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, CATEGORY));

        toggleFullbright = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_fullbright",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_L, CATEGORY));

        toggleSpeed = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_speed",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, CATEGORY));

        toggleHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_hud",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, CATEGORY));

        toggleDupe = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_dupe",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, CATEGORY));

        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.open_config",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY));
    }
}
