package com.reachfly;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/**
 * Registers and manages all mod keybinds.
 */
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

        toggleDupe = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_dupe",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, CATEGORY));

        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.open_config",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY));
    }
}
