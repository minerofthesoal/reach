package com.reachfly;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {

    private static final String CATEGORY = "category.reachfly";

    public static KeyMapping toggleReach;
    public static KeyMapping toggleFly;
    public static KeyMapping toggleEsp;
    public static KeyMapping toggleAutoHit;
    public static KeyMapping toggleLowHealthKill;
    public static KeyMapping toggleEatingAssist;
    public static KeyMapping toggleAutoKillWhenLow;
    public static KeyMapping toggleJesus;
    public static KeyMapping toggleAutoElytraSwap;
    public static KeyMapping toggleFlyToCoords;
    public static KeyMapping toggleNoFall;
    public static KeyMapping toggleFullbright;
    public static KeyMapping toggleSpeed;
    public static KeyMapping toggleWalkToCoords;
    public static KeyMapping toggleXray;
    public static KeyMapping toggleKnockback;
    public static KeyMapping toggleAutoTotem;
    public static KeyMapping toggleAutoArmor;
    public static KeyMapping toggleScaffold;
    public static KeyMapping toggleHud;
    public static KeyMapping triggerTeleport;
    public static KeyMapping openConfig;

    public static void register() {
        toggleReach = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_reach",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY));

        toggleFly = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_fly",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY));

        toggleEsp = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_esp",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY));

        toggleAutoHit = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_autohit",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY));

        toggleLowHealthKill = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_lowhealthkill",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY));

        toggleEatingAssist = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_eating",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY));

        toggleAutoKillWhenLow = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_autokilllow",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, CATEGORY));

        toggleJesus = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_jesus",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, CATEGORY));

        toggleAutoElytraSwap = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_elytraswap",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Y, CATEGORY));

        toggleFlyToCoords = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_flytocoords",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, CATEGORY));

        toggleNoFall = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_nofall",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, CATEGORY));

        toggleFullbright = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_fullbright",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_L, CATEGORY));

        toggleSpeed = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_speed",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, CATEGORY));

        toggleWalkToCoords = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_walktocoords",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_SEMICOLON, CATEGORY));

        toggleXray = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_xray",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY));

        toggleKnockback = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_knockback",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, CATEGORY));

        toggleAutoTotem = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_autototem",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, CATEGORY));

        toggleAutoArmor = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_autoarmor",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_COMMA, CATEGORY));

        toggleScaffold = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_scaffold",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PERIOD, CATEGORY));

        toggleHud = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.toggle_hud",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, CATEGORY));

        triggerTeleport = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.trigger_teleport",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_T, CATEGORY));

        openConfig = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly.open_config",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY));

        // Apply saved keybind overrides from our cross-version config
        ModConfig.applyKeybinds();
    }

    /** Returns all registered f1sch keybinds for config save/load. */
    public static KeyMapping[] allKeybinds() {
        return new KeyMapping[] {
            toggleReach, toggleFly, toggleEsp, toggleAutoHit, toggleLowHealthKill,
            toggleEatingAssist, toggleAutoKillWhenLow, toggleJesus, toggleAutoElytraSwap,
            toggleFlyToCoords, toggleNoFall, toggleFullbright, toggleSpeed, toggleWalkToCoords,
            toggleXray, toggleKnockback, toggleAutoTotem, toggleAutoArmor, toggleScaffold,
            toggleHud, triggerTeleport, openConfig
        };
    }
}
