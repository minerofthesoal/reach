package com.reachfly;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Mod Menu integration. When Mod Menu is installed, this provides
 * a config button that opens our ConfigScreen.
 * If Mod Menu is not installed, this class is never loaded (soft dependency).
 */
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::new;
    }
}
