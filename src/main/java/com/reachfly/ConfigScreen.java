package com.reachfly;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigScreen extends Screen {

    private final Screen parent;

    private static final int PANEL_WIDTH = 120;
    private static final int PANEL_TOP = 30;
    private static final int CATEGORY_HEIGHT = 22;
    private static final int MODULE_ROW_HEIGHT = 18;
    private static final int DETAIL_LEFT_PAD = 8;
    private static final int DETAIL_ROW_HEIGHT = 22;
    private static final int SLIDER_W = 160;
    private static final int SLIDER_H = 18;
    private static final int TOGGLE_W = 160;
    private static final int TOGGLE_H = 18;
    private static final int FIELD_W = 100;
    private static final int FIELD_H = 16;

    private static final int COLOR_COMBAT   = 0xFFFF5555;
    private static final int COLOR_MOVEMENT = 0xFF55AAFF;
    private static final int COLOR_RENDER   = 0xFFFF55FF;
    private static final int COLOR_PLAYER   = 0xFF55FF55;

    private final List<Category> categories = new ArrayList<>();
    private Module selectedModule = null;
    private double detailScroll = 0;
    private int detailContentHeight = 0;
    private final List<Object> detailWidgets = new ArrayList<>();

    public ConfigScreen(Screen parent) {
        super(Text.literal("ReachFly ClickGUI"));
        this.parent = parent;
        buildCategories();
    }

    private static class Category {
        final String name;
        final int color;
        final List<Module> modules = new ArrayList<>();
        boolean expanded = true;
        Category(String name, int color) { this.name = name; this.color = color; }
    }

    private static class Module {
        final String name;
        final Supplier<Boolean> getter;
        final Consumer<Boolean> setter;
        final List<Setting> settings = new ArrayList<>();
        Module(String name, Supplier<Boolean> getter, Consumer<Boolean> setter) {
            this.name = name; this.getter = getter; this.setter = setter;
        }
    }

    private static class Setting {
        enum Type { TOGGLE, SLIDER, TEXT_FIELD }
        final Type type;
        final String label;
        Supplier<Boolean> tGetter; Consumer<Boolean> tSetter;
        float sValue; float sMin; float sMax; Consumer<Float> sSetter;
        Supplier<String> tfGetter; Consumer<String> tfSetter;

        static Setting toggle(String label, Supplier<Boolean> g, Consumer<Boolean> s) {
            Setting st = new Setting(Type.TOGGLE, label); st.tGetter = g; st.tSetter = s; return st;
        }
        static Setting slider(String label, float val, float min, float max, Consumer<Float> s) {
            Setting st = new Setting(Type.SLIDER, label); st.sValue = val; st.sMin = min; st.sMax = max; st.sSetter = s; return st;
        }
        static Setting textField(String label, Supplier<String> g, Consumer<String> s) {
            Setting st = new Setting(Type.TEXT_FIELD, label); st.tfGetter = g; st.tfSetter = s; return st;
        }
        private Setting(Type type, String label) { this.type = type; this.label = label; }
    }

    private void buildCategories() {
        categories.clear();

        Category combat = new Category("Combat", COLOR_COMBAT);
        Module autoHit = new Module("Auto Hit", () -> ModConfig.autoHitEnabled, v -> ModConfig.autoHitEnabled = v);
        autoHit.settings.add(Setting.slider("Range", ModConfig.autoHitRange, ModConfig.AUTO_HIT_RANGE_MIN, ModConfig.AUTO_HIT_RANGE_MAX, v -> ModConfig.autoHitRange = v));
        autoHit.settings.add(Setting.toggle("Players Only", () -> ModConfig.autoHitPlayersOnly, v -> ModConfig.autoHitPlayersOnly = v));
        autoHit.settings.add(Setting.toggle("Kill Aura", () -> ModConfig.killAuraEnabled, v -> ModConfig.killAuraEnabled = v));
        combat.modules.add(autoHit);
        Module lowHP = new Module("Low Health Kill", () -> ModConfig.lowHealthKillEnabled, v -> ModConfig.lowHealthKillEnabled = v);
        lowHP.settings.add(Setting.slider("HP Threshold", ModConfig.lowHealthThreshold, ModConfig.LOW_HEALTH_MIN, ModConfig.LOW_HEALTH_MAX, v -> ModConfig.lowHealthThreshold = v));
        combat.modules.add(lowHP);
        Module autoKill = new Module("Auto Kill When Low", () -> ModConfig.autoKillWhenLowEnabled, v -> ModConfig.autoKillWhenLowEnabled = v);
        autoKill.settings.add(Setting.slider("Self HP Threshold", ModConfig.autoKillSelfHpThreshold, ModConfig.AUTO_KILL_SELF_HP_MIN, ModConfig.AUTO_KILL_SELF_HP_MAX, v -> ModConfig.autoKillSelfHpThreshold = v));
        autoKill.settings.add(Setting.slider("Kill Range", ModConfig.autoKillWhenLowRange, ModConfig.AUTO_KILL_RANGE_MIN, ModConfig.AUTO_KILL_RANGE_MAX, v -> ModConfig.autoKillWhenLowRange = v));
        combat.modules.add(autoKill);
        Module reach = new Module("Reach", () -> ModConfig.reachEnabled, v -> ModConfig.reachEnabled = v);
        reach.settings.add(Setting.slider("Distance", ModConfig.reachDistance, ModConfig.REACH_MIN, ModConfig.REACH_MAX, v -> ModConfig.reachDistance = v));
        combat.modules.add(reach);
        categories.add(combat);

        Category movement = new Category("Movement", COLOR_MOVEMENT);
        Module fly = new Module("Fly", () -> ModConfig.flyEnabled, v -> ModConfig.flyEnabled = v);
        fly.settings.add(Setting.slider("Speed", ModConfig.flySpeed, ModConfig.FLY_SPEED_MIN, ModConfig.FLY_SPEED_MAX, v -> ModConfig.flySpeed = v));
        movement.modules.add(fly);
        Module speed = new Module("Speed", () -> ModConfig.speedEnabled, v -> ModConfig.speedEnabled = v);
        speed.settings.add(Setting.slider("Multiplier", ModConfig.speedMultiplier, ModConfig.SPEED_MIN, ModConfig.SPEED_MAX, v -> ModConfig.speedMultiplier = v));
        movement.modules.add(speed);
        movement.modules.add(new Module("Jesus", () -> ModConfig.jesusEnabled, v -> ModConfig.jesusEnabled = v));
        movement.modules.add(new Module("NoFall", () -> ModConfig.noFallEnabled, v -> ModConfig.noFallEnabled = v));
        Module flyTo = new Module("Fly to Coords", () -> ModConfig.flyToCoordsEnabled, v -> { ModConfig.flyToCoordsEnabled = v; if (!v) FlyToCoordsHandler.onDisable(); });
        flyTo.settings.add(Setting.slider("Speed", ModConfig.flyToCoordsSpeed, ModConfig.FLY_TO_SPEED_MIN, ModConfig.FLY_TO_SPEED_MAX, v -> ModConfig.flyToCoordsSpeed = v));
        flyTo.settings.add(Setting.textField("X", () -> String.valueOf((int) ModConfig.flyToX), s -> { try { ModConfig.flyToX = Float.parseFloat(s); } catch (NumberFormatException e) {} }));
        flyTo.settings.add(Setting.textField("Y", () -> String.valueOf((int) ModConfig.flyToY), s -> { try { ModConfig.flyToY = Float.parseFloat(s); } catch (NumberFormatException e) {} }));
        flyTo.settings.add(Setting.textField("Z", () -> String.valueOf((int) ModConfig.flyToZ), s -> { try { ModConfig.flyToZ = Float.parseFloat(s); } catch (NumberFormatException e) {} }));
        movement.modules.add(flyTo);
        Module walkTo = new Module("Walk to Coords", () -> ModConfig.walkToCoordsEnabled, v -> { ModConfig.walkToCoordsEnabled = v; if (!v) WalkToCoordsHandler.onDisable(); });
        walkTo.settings.add(Setting.textField("X", () -> String.valueOf((int) ModConfig.walkToX), s -> { try { ModConfig.walkToX = Float.parseFloat(s); } catch (NumberFormatException e) {} }));
        walkTo.settings.add(Setting.textField("Y", () -> String.valueOf((int) ModConfig.walkToY), s -> { try { ModConfig.walkToY = Float.parseFloat(s); } catch (NumberFormatException e) {} }));
        walkTo.settings.add(Setting.textField("Z", () -> String.valueOf((int) ModConfig.walkToZ), s -> { try { ModConfig.walkToZ = Float.parseFloat(s); } catch (NumberFormatException e) {} }));
        movement.modules.add(walkTo);
        categories.add(movement);

        Category render = new Category("Render", COLOR_RENDER);
        Module esp = new Module("ESP", () -> ModConfig.espEnabled, v -> ModConfig.espEnabled = v);
        esp.settings.add(Setting.toggle("Players", () -> ModConfig.espPlayers, v -> ModConfig.espPlayers = v));
        esp.settings.add(Setting.toggle("Hostile", () -> ModConfig.espHostile, v -> ModConfig.espHostile = v));
        esp.settings.add(Setting.toggle("Passive", () -> ModConfig.espPassive, v -> ModConfig.espPassive = v));
        esp.settings.add(Setting.toggle("Tracer Lines", () -> ModConfig.espLines, v -> ModConfig.espLines = v));
        esp.settings.add(Setting.toggle("Path Trace", () -> ModConfig.espPathTrace, v -> ModConfig.espPathTrace = v));
        render.modules.add(esp);
        render.modules.add(new Module("Fullbright", () -> ModConfig.fullbrightEnabled, v -> ModConfig.fullbrightEnabled = v));
        render.modules.add(new Module("HUD", () -> ModConfig.hudVisible, v -> ModConfig.hudVisible = v));
        categories.add(render);

        Category player = new Category("Player", COLOR_PLAYER);
        player.modules.add(new Module("Auto Elytra", () -> ModConfig.autoElytraSwapEnabled, v -> ModConfig.autoElytraSwapEnabled = v));
        Module eating = new Module("Eating Assist", () -> ModConfig.eatingAssistEnabled, v -> ModConfig.eatingAssistEnabled = v);
        eating.settings.add(Setting.slider("Hunger Threshold", ModConfig.eatingHungerThreshold, ModConfig.EATING_HUNGER_MIN, ModConfig.EATING_HUNGER_MAX, v -> ModConfig.eatingHungerThreshold = Math.round(v)));
        player.modules.add(eating);
        categories.add(player);

        selectedModule = null;
    }

    @Override
    protected void init() {
        rebuildDetailWidgets();
    }

    @Override
    public void close() {
        ModConfig.save();
        if (this.client != null) this.client.setScreen(parent);
    }

    private void rebuildDetailWidgets() {
        for (Object w : detailWidgets) {
            if (w instanceof net.minecraft.client.gui.widget.ClickableWidget cw) {
                remove(cw);
            }
        }
        detailWidgets.clear();
        detailScroll = 0;
        if (selectedModule == null) return;
        int detailX = PANEL_WIDTH + DETAIL_LEFT_PAD + 10;
        int y = PANEL_TOP + 28;
        for (Setting s : selectedModule.settings) {
            switch (s.type) {
                case TOGGLE -> {
                    ButtonWidget btn = ButtonWidget.builder(toggleText(s.label, s.tGetter.get()), b -> {
                        s.tSetter.accept(!s.tGetter.get());
                        b.setMessage(toggleText(s.label, s.tGetter.get()));
                        ModConfig.save();
                    }).dimensions(detailX, y, TOGGLE_W, TOGGLE_H).build();
                    addDrawableChild(btn);
                    detailWidgets.add(btn);
                    y += DETAIL_ROW_HEIGHT;
                }
                case SLIDER -> {
                    ConfigSlider slider = new ConfigSlider(detailX, y, SLIDER_W, SLIDER_H, s.label, s.sValue, s.sMin, s.sMax, s.sSetter);
                    addDrawableChild(slider);
                    detailWidgets.add(slider);
                    y += DETAIL_ROW_HEIGHT;
                }
                case TEXT_FIELD -> {
                    TextFieldWidget tf = new TextFieldWidget(this.textRenderer, detailX + 30, y, FIELD_W, FIELD_H, Text.literal(s.label));
                    tf.setText(s.tfGetter.get());
                    tf.setChangedListener(text -> { s.tfSetter.accept(text); ModConfig.save(); });
                    addDrawableChild(tf);
                    detailWidgets.add(tf);
                    y += DETAIL_ROW_HEIGHT;
                }
            }
        }
        detailContentHeight = y - PANEL_TOP;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xC0101010);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("\u00a7b\u00a7lReachFly \u00a7r\u00a77ClickGUI"), this.width / 2, 8, 0xFFFFFF);
        int panelBottom = this.height - 4;
        context.fill(2, PANEL_TOP - 2, PANEL_WIDTH + 4, panelBottom, 0xA0181818);
        int y = PANEL_TOP;
        for (Category cat : categories) {
            int hc = cat.expanded ? cat.color : darken(cat.color);
            context.fill(4, y, PANEL_WIDTH + 2, y + CATEGORY_HEIGHT, hc & 0x60FFFFFF | 0x40000000);
            context.drawTextWithShadow(this.textRenderer, Text.literal((cat.expanded ? "\u25BC " : "\u25B6 ") + cat.name), 8, y + 6, cat.color);
            y += CATEGORY_HEIGHT;
            if (cat.expanded) {
                for (Module mod : cat.modules) {
                    boolean on = mod.getter.get();
                    boolean sel = (mod == selectedModule);
                    context.fill(6, y, PANEL_WIDTH, y + MODULE_ROW_HEIGHT, sel ? 0x60FFFFFF : (on ? 0x3000FF00 : 0x20FF0000));
                    context.drawTextWithShadow(this.textRenderer, Text.literal((on ? "\u00a7a\u25CF " : "\u00a7c\u25CB ") + "\u00a7f" + mod.name), 14, y + 4, 0xFFFFFF);
                    y += MODULE_ROW_HEIGHT;
                }
            }
        }
        int detailLeft = PANEL_WIDTH + 8;
        context.fill(detailLeft, PANEL_TOP - 2, this.width - 4, panelBottom, 0xA0181818);
        if (selectedModule != null) {
            boolean on = selectedModule.getter.get();
            context.drawTextWithShadow(this.textRenderer, Text.literal("\u00a7l" + selectedModule.name + " " + (on ? "\u00a7aENABLED" : "\u00a7cDISABLED")), detailLeft + DETAIL_LEFT_PAD, PANEL_TOP + 6, 0xFFFFFF);
            int sy = PANEL_TOP + 28;
            for (Setting s : selectedModule.settings) {
                if (s.type == Setting.Type.TEXT_FIELD) {
                    context.drawTextWithShadow(this.textRenderer, Text.literal("\u00a77" + s.label + ":"), detailLeft + DETAIL_LEFT_PAD, sy + 4, 0xAAAAAA);
                }
                sy += DETAIL_ROW_HEIGHT;
            }
        } else {
            context.drawTextWithShadow(this.textRenderer, Text.literal("\u00a78Select a module..."), detailLeft + DETAIL_LEFT_PAD, PANEL_TOP + 20, 0x888888);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int y = PANEL_TOP;
        for (Category cat : categories) {
            if (mouseX >= 4 && mouseX <= PANEL_WIDTH + 2 && mouseY >= y && mouseY < y + CATEGORY_HEIGHT) { cat.expanded = !cat.expanded; return true; }
            y += CATEGORY_HEIGHT;
            if (cat.expanded) {
                for (Module mod : cat.modules) {
                    if (mouseX >= 6 && mouseX <= PANEL_WIDTH && mouseY >= y && mouseY < y + MODULE_ROW_HEIGHT) {
                        if (button == 0) { selectedModule = mod; rebuildDetailWidgets(); }
                        else if (button == 1) { mod.setter.accept(!mod.getter.get()); ModConfig.save(); if (selectedModule == mod) rebuildDetailWidgets(); }
                        return true;
                    }
                    y += MODULE_ROW_HEIGHT;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX > PANEL_WIDTH + 8) {
            int viewH = this.height - PANEL_TOP - 8;
            int maxScroll = Math.max(0, detailContentHeight - viewH);
            detailScroll = Math.max(0, Math.min(maxScroll, detailScroll - verticalAmount * 10));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return super.charTyped(chr, modifiers);
    }

    private static Text toggleText(String label, boolean on) {
        return Text.literal("\u00a7f" + label + ": " + (on ? "\u00a7a\u00a7lON" : "\u00a7c\u00a7lOFF"));
    }

    private static int darken(int color) {
        int r = ((color >> 16) & 0xFF) / 2;
        int g = ((color >> 8) & 0xFF) / 2;
        int b = (color & 0xFF) / 2;
        return (color & 0xFF000000) | (r << 16) | (g << 8) | b;
    }

    private static class ConfigSlider extends SliderWidget {
        private final String label;
        private final float min;
        private final float max;
        private final Consumer<Float> setter;
        public ConfigSlider(int x, int y, int w, int h, String label, float current, float min, float max, Consumer<Float> setter) {
            super(x, y, w, h, Text.literal(String.format("%s: %.1f", label, current)), clampNorm(current, min, max));
            this.label = label; this.min = min; this.max = max; this.setter = setter;
        }
        private static double clampNorm(float current, float min, float max) {
            if (max <= min) return 0;
            return Math.max(0, Math.min(1, (current - min) / (max - min)));
        }
        @Override protected void updateMessage() {
            float val = min + (float) this.value * (max - min);
            this.setMessage(Text.literal(String.format("%s: %.1f", label, val)));
        }
        @Override protected void applyValue() {
            setter.accept(min + (float) this.value * (max - min));
            ModConfig.save();
        }
    }
}
