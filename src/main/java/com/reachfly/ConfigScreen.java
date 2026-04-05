package com.reachfly;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {

    private final Screen parent;
    private double scrollOffset = 0;
    private int contentHeight = 0;
    private final List<Entry> entries = new ArrayList<>();

    private static final int ROW_HEIGHT = 24;
    private static final int HEADER = 28;
    private static final int FOOTER = 36;
    private static final int BUTTON_W = 200;
    private static final int BUTTON_H = 20;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Reach & Fly Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        entries.clear();
        clearChildren();

        // Build all entries
        addLabel("\u00a76\u00a7l--- Reach ---");
        addToggle("Reach", () -> ModConfig.reachEnabled, v -> ModConfig.reachEnabled = v);
        addSlider("Reach Distance", ModConfig.reachDistance, ModConfig.REACH_MIN, ModConfig.REACH_MAX, v -> ModConfig.reachDistance = v);

        addLabel("\u00a7b\u00a7l--- Fly ---");
        addToggle("Fly", () -> ModConfig.flyEnabled, v -> ModConfig.flyEnabled = v);
        addSlider("Fly Speed", ModConfig.flySpeed, ModConfig.FLY_SPEED_MIN, ModConfig.FLY_SPEED_MAX, v -> ModConfig.flySpeed = v);

        addLabel("\u00a7d\u00a7l--- ESP ---");
        addToggle("ESP", () -> ModConfig.espEnabled, v -> ModConfig.espEnabled = v);
        addToggle("ESP Players", () -> ModConfig.espPlayers, v -> ModConfig.espPlayers = v);
        addToggle("ESP Hostile", () -> ModConfig.espHostile, v -> ModConfig.espHostile = v);
        addToggle("ESP Passive", () -> ModConfig.espPassive, v -> ModConfig.espPassive = v);

        addLabel("\u00a7c\u00a7l--- Auto Hit ---");
        addToggle("Auto Hit", () -> ModConfig.autoHitEnabled, v -> ModConfig.autoHitEnabled = v);
        addSlider("Auto Hit Range", ModConfig.autoHitRange, ModConfig.AUTO_HIT_RANGE_MIN, ModConfig.AUTO_HIT_RANGE_MAX, v -> ModConfig.autoHitRange = v);
        addToggle("Auto Hit Players Only", () -> ModConfig.autoHitPlayersOnly, v -> ModConfig.autoHitPlayersOnly = v);

        addLabel("\u00a74\u00a7l--- Low Health Kill ---");
        addToggle("Low Health Kill", () -> ModConfig.lowHealthKillEnabled, v -> ModConfig.lowHealthKillEnabled = v);
        addSlider("Health Threshold", ModConfig.lowHealthThreshold, ModConfig.LOW_HEALTH_MIN, ModConfig.LOW_HEALTH_MAX, v -> ModConfig.lowHealthThreshold = v);

        addLabel("\u00a7e\u00a7l--- Auto Kill (Self Low HP) ---");
        addToggle("Auto Kill When Low", () -> ModConfig.autoKillWhenLowEnabled, v -> ModConfig.autoKillWhenLowEnabled = v);
        addSlider("Your HP Threshold", ModConfig.autoKillSelfHpThreshold, ModConfig.AUTO_KILL_SELF_HP_MIN, ModConfig.AUTO_KILL_SELF_HP_MAX, v -> ModConfig.autoKillSelfHpThreshold = v);
        addSlider("Kill Range", ModConfig.autoKillWhenLowRange, ModConfig.AUTO_KILL_RANGE_MIN, ModConfig.AUTO_KILL_RANGE_MAX, v -> ModConfig.autoKillWhenLowRange = v);

        addLabel("\u00a7a\u00a7l--- Eating Assist ---");
        addToggle("Eating Assist", () -> ModConfig.eatingAssistEnabled, v -> ModConfig.eatingAssistEnabled = v);
        addSlider("Hunger Threshold", ModConfig.eatingHungerThreshold, ModConfig.EATING_HUNGER_MIN, ModConfig.EATING_HUNGER_MAX, v -> ModConfig.eatingHungerThreshold = Math.round(v));

        addLabel("\u00a79\u00a7l--- Shield Assist ---");
        addToggle("Shield Assist", () -> ModConfig.shieldAssistEnabled, v -> ModConfig.shieldAssistEnabled = v);

        addLabel("\u00a75\u00a7l--- Dupe ---");
        addToggle("Dupe", () -> ModConfig.dupeEnabled, v -> ModConfig.dupeEnabled = v);

        contentHeight = entries.size() * ROW_HEIGHT;

        // Done button pinned to bottom
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), btn -> close())
                .dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build());

        repositionWidgets();
    }

    private void addLabel(String text) {
        entries.add(new Entry(text, null));
    }

    private void addToggle(String label, java.util.function.Supplier<Boolean> getter,
                            java.util.function.Consumer<Boolean> setter) {
        ButtonWidget btn = ButtonWidget.builder(
                toggleText(label, getter.get()),
                b -> {
                    setter.accept(!getter.get());
                    b.setMessage(toggleText(label, getter.get()));
                    ModConfig.save();
                }
        ).dimensions(0, 0, BUTTON_W, BUTTON_H).build();
        addDrawableChild(btn);
        entries.add(new Entry(null, btn));
    }

    private void addSlider(String label, float current, float min, float max,
                            java.util.function.Consumer<Float> setter) {
        ConfigSlider slider = new ConfigSlider(0, 0, BUTTON_W, BUTTON_H, label, current, min, max, setter);
        addDrawableChild(slider);
        entries.add(new Entry(null, slider));
    }

    private static Text toggleText(String label, boolean on) {
        String status = on ? "\u00a7a\u00a7lON" : "\u00a7c\u00a7lOFF";
        return Text.literal("\u00a7f" + label + ": " + status);
    }

    private void repositionWidgets() {
        int viewTop = HEADER;
        int viewBottom = this.height - FOOTER;
        int centerX = this.width / 2 - BUTTON_W / 2;

        for (int i = 0; i < entries.size(); i++) {
            int entryY = HEADER + i * ROW_HEIGHT - (int) scrollOffset;
            Entry e = entries.get(i);
            if (e.widget != null) {
                e.widget.setX(centerX);
                e.widget.setY(entryY);
                e.widget.visible = (entryY + BUTTON_H > viewTop && entryY < viewBottom);
                e.widget.active = e.widget.visible;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        repositionWidgets();

        int viewTop = HEADER;
        int viewBottom = this.height - FOOTER;

        // Dark background for the list area
        context.fill(0, viewTop, this.width, viewBottom, 0xC0101010);

        // Enable scissor so entries don't render outside the list area
        context.enableScissor(0, viewTop, this.width, viewBottom);

        // Render widgets (super handles drawable children)
        super.render(context, mouseX, mouseY, delta);

        // Render labels
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            if (e.label != null) {
                int entryY = HEADER + i * ROW_HEIGHT - (int) scrollOffset + 5;
                if (entryY + 10 > viewTop && entryY < viewBottom) {
                    Text text = Text.literal(e.label);
                    int textW = this.textRenderer.getWidth(text);
                    context.drawTextWithShadow(this.textRenderer, text,
                            (this.width - textW) / 2, entryY, 0xFFFFFF);
                }
            }
        }

        context.disableScissor();

        // Scrollbar
        if (contentHeight > (viewBottom - viewTop)) {
            int viewH = viewBottom - viewTop;
            int barX = this.width / 2 + BUTTON_W / 2 + 8;
            int barW = 6;
            float ratio = (float) viewH / contentHeight;
            int thumbH = Math.max(15, (int) (viewH * ratio));
            int maxScroll = contentHeight - viewH;
            int thumbY = viewTop + (maxScroll > 0 ? (int) (scrollOffset / maxScroll * (viewH - thumbH)) : 0);

            // Track
            context.fill(barX, viewTop, barX + barW, viewBottom, 0x40FFFFFF);
            // Thumb
            context.fill(barX, thumbY, barX + barW, thumbY + thumbH, 0xC0AAAAAA);
        }

        // Title
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("\u00a7b\u00a7lReach\u00a7r \u00a76& \u00a7d\u00a7lFly\u00a7r \u00a77Config"),
                this.width / 2, 10, 0xFFFFFF);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int viewH = this.height - HEADER - FOOTER;
        int maxScroll = Math.max(0, contentHeight - viewH);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - verticalAmount * 10));
        return true;
    }

    @Override
    public void close() {
        ModConfig.save();
        if (this.client != null) this.client.setScreen(parent);
    }

    private record Entry(String label, net.minecraft.client.gui.widget.ClickableWidget widget) {}

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
