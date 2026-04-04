package com.reachfly;

/**
 * ESP is handled via EntityGlowMixin which overrides isGlowing() on the client side.
 * This makes target entities render with the vanilla outline effect (visible through walls).
 * No custom rendering code needed — the vanilla renderer handles everything.
 *
 * Color is controlled via getTeamColorValue() override in the same mixin.
 */
public class EspRenderer {

    public static void register() {
        // ESP rendering is handled entirely by the EntityGlowMixin.
        // No event registration needed.
    }
}
