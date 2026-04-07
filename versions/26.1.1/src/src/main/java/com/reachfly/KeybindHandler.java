package com.reachfly;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.create(ResourceLocation.fromNamespaceAndPath("reachfly", "category"));

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
        toggleReach = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_reach",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY));

        toggleFly = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_fly",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY));

        toggleEsp = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_esp",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY));

        toggleAutoHit = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_autohit",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY));

        toggleLowHealthKill = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_lowhealthkill",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY));

        toggleEatingAssist = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_eating",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY));

        toggleAutoKillWhenLow = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_autokilllow",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, CATEGORY));

        toggleJesus = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_jesus",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_U, CATEGORY));

        toggleAutoElytraSwap = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_elytraswap",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Y, CATEGORY));

        toggleFlyToCoords = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_flytocoords",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, CATEGORY));

        toggleNoFall = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_nofall",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_I, CATEGORY));

        toggleFullbright = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_fullbright",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_L, CATEGORY));

        toggleSpeed = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_speed",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, CATEGORY));

        toggleWalkToCoords = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_walktocoords",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_SEMICOLON, CATEGORY));

        toggleXray = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_xray",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY));

        toggleKnockback = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_knockback",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, CATEGORY));

        toggleAutoTotem = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_autototem",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, CATEGORY));

        toggleAutoArmor = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_autoarmor",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_COMMA, CATEGORY));

        toggleScaffold = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_scaffold",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_PERIOD, CATEGORY));

        toggleHud = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.toggle_hud",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, CATEGORY));

        triggerTeleport = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.trigger_teleport",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_T, CATEGORY));

        openConfig = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.reachfly.open_config",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY));
    }
}
