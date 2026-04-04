package com.reachfly;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

/**
 * In-game configuration screen for all mod features.
 * Provides toggles and sliders organized by feature.
 */
public class ConfigScreen extends Screen {

    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Reach & Fly Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int bw = 200;
        int bh = 20;
        int sp = 22;
        int y = 30;

        // ========== REACH ==========
        addDrawableChild(toggleButton(cx, y, bw, bh, "Reach",
                () -> ModConfig.reachEnabled, v -> ModConfig.reachEnabled = v));
        y += sp;
        addDrawableChild(new ConfigSlider(cx - bw / 2, y, bw, bh, "Reach Distance",
                ModConfig.reachDistance, ModConfig.REACH_MIN, ModConfig.REACH_MAX,
                v -> ModConfig.reachDistance = v));
        y += sp;

        // ========== FLY ==========
        addDrawableChild(toggleButton(cx, y, bw, bh, "Fly",
                () -> ModConfig.flyEnabled, v -> ModConfig.flyEnabled = v));
        y += sp;
        addDrawableChild(new ConfigSlider(cx - bw / 2, y, bw, bh, "Fly Speed",
                ModConfig.flySpeed, ModConfig.FLY_SPEED_MIN, ModConfig.FLY_SPEED_MAX,
                v -> ModConfig.flySpeed = v));
        y += sp;

        // ========== ESP ==========
        addDrawableChild(toggleButton(cx, y, bw, bh, "ESP",
                () -> ModConfig.espEnabled, v -> ModConfig.espEnabled = v));
        y += sp;
        addDrawableChild(toggleButton(cx, y, bw, bh, "ESP Players",
                () -> ModConfig.espPlayers, v -> ModConfig.espPlayers = v));
        y += sp;
        addDrawableChild(toggleButton(cx, y, bw, bh, "ESP Hostile",
                () -> ModConfig.espHostile, v -> ModConfig.espHostile = v));
        y += sp;
        addDrawableChild(toggleButton(cx, y, bw, bh, "ESP Passive",
                () -> ModConfig.espPassive, v -> ModConfig.espPassive = v));
        y += sp;

        // ========== AUTO HIT ==========
        addDrawableChild(toggleButton(cx, y, bw, bh, "Auto Hit",
                () -> ModConfig.autoHitEnabled, v -> ModConfig.autoHitEnabled = v));
        y += sp;
        addDrawableChild(new ConfigSlider(cx - bw / 2, y, bw, bh, "Auto Hit Range",
                ModConfig.autoHitRange, ModConfig.AUTO_HIT_RANGE_MIN, ModConfig.AUTO_HIT_RANGE_MAX,
                v -> ModConfig.autoHitRange = v));
        y += sp;
        addDrawableChild(toggleButton(cx, y, bw, bh, "Auto Hit Players Only",
                () -> ModConfig.autoHitPlayersOnly, v -> ModConfig.autoHitPlayersOnly = v));
        y += sp;

        // ========== LOW HEALTH KILL ==========
        addDrawableChild(toggleButton(cx, y, bw, bh, "Low Health Kill",
                () -> ModConfig.lowHealthKillEnabled, v -> ModConfig.lowHealthKillEnabled = v));
        y += sp;
        addDrawableChild(new ConfigSlider(cx - bw / 2, y, bw, bh, "Health Threshold",
                ModConfig.lowHealthThreshold, ModConfig.LOW_HEALTH_MIN, ModConfig.LOW_HEALTH_MAX,
                v -> ModConfig.lowHealthThreshold = v));
        y += sp;

        // ========== EATING ASSIST ==========
        addDrawableChild(toggleButton(cx, y, bw, bh, "Eating Assist",
                () -> ModConfig.eatingAssistEnabled, v -> ModConfig.eatingAssistEnabled = v));
        y += sp;
        addDrawableChild(new ConfigSlider(cx - bw / 2, y, bw, bh, "Hunger Threshold",
                ModConfig.eatingHungerThreshold, ModConfig.EATING_HUNGER_MIN, ModConfig.EATING_HUNGER_MAX,
                v -> ModConfig.eatingHungerThreshold = (int) v));
        y += sp;

        // ========== SHIELD ASSIST ==========
        addDrawableChild(toggleButton(cx, y, bw, bh, "Shield Assist",
                () -> ModConfig.shieldAssistEnabled, v -> ModConfig.shieldAssistEnabled = v));
        y += sp;

        // ========== DUPE ==========
        addDrawableChild(toggleButton(cx, y, bw, bh, "Dupe",
                () -> ModConfig.dupeEnabled, v -> ModConfig.dupeEnabled = v));
        y += sp + 4;

        // ========== DONE ==========
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), btn -> close())
                .dimensions(cx - bw / 2, y, bw, bh).build());
    }

    @Override
    public void close() {
        ModConfig.save();
        if (this.client != null) this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, 0xFFFFFF);
    }

    /**
     * Helper to create a toggle button.
     */
    private ButtonWidget toggleButton(int cx, int y, int w, int h, String label,
                                       java.util.function.Supplier<Boolean> getter,
                                       java.util.function.Consumer<Boolean> setter) {
        return ButtonWidget.builder(
                Text.literal(label + ": " + (getter.get() ? "ON" : "OFF")),
                btn -> {
                    setter.accept(!getter.get());
                    btn.setMessage(Text.literal(label + ": " + (getter.get() ? "ON" : "OFF")));
                    ModConfig.save();
                }
        ).dimensions(cx - w / 2, y, w, h).build();
    }

    /**
     * Reusable slider with min/max range and a value setter callback.
     */
    private static class ConfigSlider extends SliderWidget {
        private final String label;
        private final float min;
        private final float max;
        private final java.util.function.Consumer<Float> setter;

        public ConfigSlider(int x, int y, int w, int h, String label,
                            float current, float min, float max,
                            java.util.function.Consumer<Float> setter) {
            super(x, y, w, h,
                    Text.literal(String.format("%s: %.1f", label, current)),
                    (current - min) / (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            this.setter = setter;
        }

        @Override
        protected void updateMessage() {
            float val = min + (float) this.value * (max - min);
            this.setMessage(Text.literal(String.format("%s: %.1f", label, val)));
        }

        @Override
        protected void applyValue() {
            setter.accept(min + (float) this.value * (max - min));
            ModConfig.save();
        }
    }
}
