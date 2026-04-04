package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Scrollable in-game configuration screen for all mod features.
 * Uses ElementListWidget for a proper scrollbar.
 */
public class ConfigScreen extends Screen {

    private final Screen parent;
    private ConfigList optionList;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Reach & Fly Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int headerHeight = 28;
        int footerHeight = 36;
        int listTop = headerHeight;
        int listHeight = this.height - headerHeight - footerHeight;
        int bw = 200;
        int bh = 20;

        // Create scrollable list
        optionList = new ConfigList(this.client, this.width, listHeight, listTop, 25);

        // ========== REACH ==========
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,toggleButton(bw, bh, "Reach",
                () -> ModConfig.reachEnabled, v -> ModConfig.reachEnabled = v)));
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,new ConfigSlider(0, 0, bw, bh, "Reach Distance",
                ModConfig.reachDistance, ModConfig.REACH_MIN, ModConfig.REACH_MAX,
                v -> ModConfig.reachDistance = v)));

        // ========== FLY ==========
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,toggleButton(bw, bh, "Fly",
                () -> ModConfig.flyEnabled, v -> ModConfig.flyEnabled = v)));
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,new ConfigSlider(0, 0, bw, bh, "Fly Speed",
                ModConfig.flySpeed, ModConfig.FLY_SPEED_MIN, ModConfig.FLY_SPEED_MAX,
                v -> ModConfig.flySpeed = v)));

        // ========== ESP ==========
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,toggleButton(bw, bh, "ESP",
                () -> ModConfig.espEnabled, v -> ModConfig.espEnabled = v)));
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,toggleButton(bw, bh, "ESP Players",
                () -> ModConfig.espPlayers, v -> ModConfig.espPlayers = v)));
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,toggleButton(bw, bh, "ESP Hostile",
                () -> ModConfig.espHostile, v -> ModConfig.espHostile = v)));
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,toggleButton(bw, bh, "ESP Passive",
                () -> ModConfig.espPassive, v -> ModConfig.espPassive = v)));

        // ========== AUTO HIT ==========
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,toggleButton(bw, bh, "Auto Hit",
                () -> ModConfig.autoHitEnabled, v -> ModConfig.autoHitEnabled = v)));
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,new ConfigSlider(0, 0, bw, bh, "Auto Hit Range",
                ModConfig.autoHitRange, ModConfig.AUTO_HIT_RANGE_MIN, ModConfig.AUTO_HIT_RANGE_MAX,
                v -> ModConfig.autoHitRange = v)));
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,toggleButton(bw, bh, "Auto Hit Players Only",
                () -> ModConfig.autoHitPlayersOnly, v -> ModConfig.autoHitPlayersOnly = v)));

        // ========== LOW HEALTH KILL ==========
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,toggleButton(bw, bh, "Low Health Kill",
                () -> ModConfig.lowHealthKillEnabled, v -> ModConfig.lowHealthKillEnabled = v)));
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,new ConfigSlider(0, 0, bw, bh, "Health Threshold",
                ModConfig.lowHealthThreshold, ModConfig.LOW_HEALTH_MIN, ModConfig.LOW_HEALTH_MAX,
                v -> ModConfig.lowHealthThreshold = v)));

        // ========== EATING ASSIST ==========
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,toggleButton(bw, bh, "Eating Assist",
                () -> ModConfig.eatingAssistEnabled, v -> ModConfig.eatingAssistEnabled = v)));
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,new ConfigSlider(0, 0, bw, bh, "Hunger Threshold",
                ModConfig.eatingHungerThreshold, ModConfig.EATING_HUNGER_MIN, ModConfig.EATING_HUNGER_MAX,
                v -> ModConfig.eatingHungerThreshold = Math.round(v))));

        // ========== SHIELD ASSIST ==========
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,toggleButton(bw, bh, "Shield Assist",
                () -> ModConfig.shieldAssistEnabled, v -> ModConfig.shieldAssistEnabled = v)));

        // ========== DUPE ==========
        optionList.addOptionEntry(new ConfigList.WidgetEntry(this.width,toggleButton(bw, bh, "Dupe",
                () -> ModConfig.dupeEnabled, v -> ModConfig.dupeEnabled = v)));

        addDrawableChild(optionList);

        // Done button at the bottom
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), btn -> close())
                .dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build());
    }

    @Override
    public void close() {
        ModConfig.save();
        if (this.client != null) this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
    }

    /**
     * Helper to create a toggle button (not yet positioned — list entry handles that).
     */
    private ButtonWidget toggleButton(int w, int h, String label,
                                       java.util.function.Supplier<Boolean> getter,
                                       java.util.function.Consumer<Boolean> setter) {
        return ButtonWidget.builder(
                Text.literal(label + ": " + (getter.get() ? "ON" : "OFF")),
                btn -> {
                    setter.accept(!getter.get());
                    btn.setMessage(Text.literal(label + ": " + (getter.get() ? "ON" : "OFF")));
                    ModConfig.save();
                }
        ).dimensions(0, 0, w, h).build();
    }

    // =====================================================
    // Scrollable list widget
    // =====================================================

    /**
     * Scrollable list of config entries with a scrollbar.
     */
    public static class ConfigList extends ElementListWidget<ConfigList.WidgetEntry> {

        public ConfigList(MinecraftClient client, int width, int height, int y, int itemHeight) {
            super(client, width, height, y, itemHeight);
        }

        @Override
        public int getRowWidth() {
            return 220;
        }

        /**
         * Expose the protected addEntry method.
         */
        public void addOptionEntry(WidgetEntry entry) {
            super.addEntry(entry);
        }

        /**
         * A single row in the scrollable list, containing one ClickableWidget.
         */
        public static class WidgetEntry extends ElementListWidget.Entry<WidgetEntry> {
            private final ClickableWidget widget;
            private final int listWidth;

            public WidgetEntry(int listWidth, ClickableWidget widget) {
                this.widget = widget;
                this.listWidth = listWidth;
            }

            @Override
            public List<? extends Element> children() {
                return List.of(widget);
            }

            @Override
            public List<? extends Selectable> selectableChildren() {
                return List.of(widget);
            }

            @Override
            public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                widget.setX((listWidth - widget.getWidth()) / 2);
                widget.render(context, mouseX, mouseY, tickDelta);
            }
        }
    }

    // =====================================================
    // Reusable slider
    // =====================================================

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
