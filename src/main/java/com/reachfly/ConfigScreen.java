package com.reachfly;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

/**
 * In-game configuration screen for the Reach & Fly mod.
 * Provides toggles and sliders for all mod settings.
 * No external dependencies (Cloth Config, etc.) required.
 */
public class ConfigScreen extends Screen {

    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Reach & Fly Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 4;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 26;

        // ========== REACH SECTION ==========

        // Reach toggle button
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Reach: " + (ModConfig.reachEnabled ? "ON" : "OFF")),
                button -> {
                    ModConfig.reachEnabled = !ModConfig.reachEnabled;
                    button.setMessage(Text.literal("Reach: " + (ModConfig.reachEnabled ? "ON" : "OFF")));
                    ModConfig.save();
                }
        ).dimensions(centerX - buttonWidth / 2, startY, buttonWidth, buttonHeight).build());

        // Reach distance slider
        addDrawableChild(new ReachSlider(
                centerX - buttonWidth / 2, startY + spacing,
                buttonWidth, buttonHeight,
                ModConfig.reachDistance
        ));

        // ========== FLY SECTION ==========

        // Fly toggle button
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Fly: " + (ModConfig.flyEnabled ? "ON" : "OFF")),
                button -> {
                    ModConfig.flyEnabled = !ModConfig.flyEnabled;
                    button.setMessage(Text.literal("Fly: " + (ModConfig.flyEnabled ? "ON" : "OFF")));
                    ModConfig.save();
                }
        ).dimensions(centerX - buttonWidth / 2, startY + spacing * 2, buttonWidth, buttonHeight).build());

        // Fly speed slider
        addDrawableChild(new FlySpeedSlider(
                centerX - buttonWidth / 2, startY + spacing * 3,
                buttonWidth, buttonHeight,
                ModConfig.flySpeed
        ));

        // ========== DONE BUTTON ==========
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                button -> close()
        ).dimensions(centerX - buttonWidth / 2, startY + spacing * 5, buttonWidth, buttonHeight).build());
    }

    @Override
    public void close() {
        ModConfig.save();
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 4 - 20, 0xFFFFFF);
        // Section labels
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("--- Reach ---"),
                this.width / 2, this.height / 4 - 8, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("--- Fly ---"),
                this.width / 2, this.height / 4 + 26 * 2 - 8, 0xAAAAAA);
    }

    /**
     * Custom slider for reach distance configuration.
     */
    private static class ReachSlider extends SliderWidget {
        public ReachSlider(int x, int y, int width, int height, float currentValue) {
            super(x, y, width, height,
                    Text.literal(String.format("Reach Distance: %.1f", currentValue)),
                    (currentValue - ModConfig.REACH_MIN) / (ModConfig.REACH_MAX - ModConfig.REACH_MIN));
        }

        @Override
        protected void updateMessage() {
            float val = ModConfig.REACH_MIN + (float) this.value * (ModConfig.REACH_MAX - ModConfig.REACH_MIN);
            this.setMessage(Text.literal(String.format("Reach Distance: %.1f", val)));
        }

        @Override
        protected void applyValue() {
            ModConfig.reachDistance = ModConfig.REACH_MIN + (float) this.value * (ModConfig.REACH_MAX - ModConfig.REACH_MIN);
            ModConfig.save();
        }
    }

    /**
     * Custom slider for fly speed configuration.
     */
    private static class FlySpeedSlider extends SliderWidget {
        public FlySpeedSlider(int x, int y, int width, int height, float currentValue) {
            super(x, y, width, height,
                    Text.literal(String.format("Fly Speed: %.1fx", currentValue)),
                    (currentValue - ModConfig.FLY_SPEED_MIN) / (ModConfig.FLY_SPEED_MAX - ModConfig.FLY_SPEED_MIN));
        }

        @Override
        protected void updateMessage() {
            float val = ModConfig.FLY_SPEED_MIN + (float) this.value * (ModConfig.FLY_SPEED_MAX - ModConfig.FLY_SPEED_MIN);
            this.setMessage(Text.literal(String.format("Fly Speed: %.1fx", val)));
        }

        @Override
        protected void applyValue() {
            ModConfig.flySpeed = ModConfig.FLY_SPEED_MIN + (float) this.value * (ModConfig.FLY_SPEED_MAX - ModConfig.FLY_SPEED_MIN);
            ModConfig.save();
        }
    }
}
