package com.reachfly;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Registers and manages all mod keybinds.
 */
public class KeybindHandler {

    private static final String CATEGORY = "key.categories.reachfly";

    // Toggle reach on/off
    public static KeyBinding toggleReach;

    // Toggle fly on/off
    public static KeyBinding toggleFly;

    // Open config GUI
    public static KeyBinding openConfig;

    /**
     * Register all keybinds with Fabric's keybinding system.
     */
    public static void register() {
        toggleReach = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_reach",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,  // Default: R key
                CATEGORY
        ));

        toggleFly = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.toggle_fly",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,  // Default: G key
                CATEGORY
        ));

        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachfly.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,  // Default: Right Shift
                CATEGORY
        ));
    }
}
