package com.reachfly;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigScreen extends Screen {

    private final Screen parent;
    private String activeCategory = "Combat";
    private double scrollOffset = 0;
    private String expandedModule = null;

    private TextFieldWidget editField = null;
    private String editLabel = null;
    private java.util.function.Consumer<Float> editSetter = null;
    private float editMin, editMax;

    private static final int BG = 0xF0101020;
    private static final int PANEL_BG = 0xF0181828;
    private static final int ACCENT = 0xFF8B5CF6;
    private static final int ACCENT_DIM = 0xFF5B3CB6;
    private static final int MODULE_BG = 0xFF1E1E36;
    private static final int MODULE_HOVER = 0xFF282848;
    private static final int MODULE_ON = 0xFF2A1F4E;
    private static final int SETTING_BG = 0xFF151528;
    private static final int TEXT_PRIMARY = 0xFFE0E0E0;
    private static final int TEXT_DIM = 0xFF888898;
    private static final int GREEN = 0xFF4ADE80;
    private static final int RED = 0xFFEF4444;
    private static final int TAB_H = 28;
    private static final int MODULE_H = 22;
    private static final int SETTING_H = 18;

    private final Map<String, List<Module>> categories = new LinkedHashMap<>();

    public ConfigScreen(Screen parent) {
        super(Text.literal("OSP"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        clearChildren();
        editField = null;
        categories.clear();
        scrollOffset = 0;

        List<Module> combat = new ArrayList<>();
        combat.add(new Module("Auto Hit", () -> ModConfig.autoHitEnabled, v -> ModConfig.autoHitEnabled = v)
                .addNumber("Range", () -> ModConfig.autoHitRange, ModConfig.AUTO_HIT_RANGE_MIN, ModConfig.AUTO_HIT_RANGE_MAX, v -> ModConfig.autoHitRange = v)
                .addToggle("Players Only", () -> ModConfig.autoHitPlayersOnly, v -> ModConfig.autoHitPlayersOnly = v)
                .addToggle("Kill Aura", () -> ModConfig.killAuraEnabled, v -> ModConfig.killAuraEnabled = v));
        combat.add(new Module("Low Health Kill", () -> ModConfig.lowHealthKillEnabled, v -> ModConfig.lowHealthKillEnabled = v)
                .addNumber("HP Threshold", () -> ModConfig.lowHealthThreshold, ModConfig.LOW_HEALTH_MIN, ModConfig.LOW_HEALTH_MAX, v -> ModConfig.lowHealthThreshold = v));
        combat.add(new Module("Auto Kill (Low HP)", () -> ModConfig.autoKillWhenLowEnabled, v -> ModConfig.autoKillWhenLowEnabled = v)
                .addNumber("Self HP Threshold", () -> ModConfig.autoKillSelfHpThreshold, ModConfig.AUTO_KILL_SELF_HP_MIN, ModConfig.AUTO_KILL_SELF_HP_MAX, v -> ModConfig.autoKillSelfHpThreshold = v)
                .addNumber("Kill Range", () -> ModConfig.autoKillWhenLowRange, ModConfig.AUTO_KILL_RANGE_MIN, ModConfig.AUTO_KILL_RANGE_MAX, v -> ModConfig.autoKillWhenLowRange = v));
        combat.add(new Module("Reach", () -> ModConfig.reachEnabled, v -> ModConfig.reachEnabled = v)
                .addNumber("Distance", () -> ModConfig.reachDistance, ModConfig.REACH_MIN, ModConfig.REACH_MAX, v -> ModConfig.reachDistance = v));
        categories.put("Combat", combat);

        List<Module> movement = new ArrayList<>();
        movement.add(new Module("Fly", () -> ModConfig.flyEnabled, v -> ModConfig.flyEnabled = v)
                .addNumber("Speed", () -> ModConfig.flySpeed, ModConfig.FLY_SPEED_MIN, ModConfig.FLY_SPEED_MAX, v -> ModConfig.flySpeed = v));
        movement.add(new Module("Speed", () -> ModConfig.speedEnabled, v -> ModConfig.speedEnabled = v)
                .addNumber("Multiplier", () -> ModConfig.speedMultiplier, ModConfig.SPEED_MIN, ModConfig.SPEED_MAX, v -> ModConfig.speedMultiplier = v));
        movement.add(new Module("Jesus", () -> ModConfig.jesusEnabled, v -> ModConfig.jesusEnabled = v));
        movement.add(new Module("NoFall", () -> ModConfig.noFallEnabled, v -> ModConfig.noFallEnabled = v));
        movement.add(new Module("Fly to Coords", () -> ModConfig.flyToCoordsEnabled, v -> { ModConfig.flyToCoordsEnabled = v; if (!v) FlyToCoordsHandler.onDisable(); })
                .addNumber("X", () -> ModConfig.flyToX, -30000000, 30000000, v -> ModConfig.flyToX = v)
                .addNumber("Y", () -> ModConfig.flyToY, -64, 320, v -> ModConfig.flyToY = v)
                .addNumber("Z", () -> ModConfig.flyToZ, -30000000, 30000000, v -> ModConfig.flyToZ = v)
                .addNumber("Speed", () -> ModConfig.flyToCoordsSpeed, ModConfig.FLY_TO_SPEED_MIN, ModConfig.FLY_TO_SPEED_MAX, v -> ModConfig.flyToCoordsSpeed = v));
        movement.add(new Module("Walk to Coords", () -> ModConfig.walkToCoordsEnabled, v -> { ModConfig.walkToCoordsEnabled = v; if (!v) WalkToCoordsHandler.onDisable(); })
                .addNumber("X", () -> ModConfig.walkToX, -30000000, 30000000, v -> ModConfig.walkToX = v)
                .addNumber("Y", () -> ModConfig.walkToY, -64, 320, v -> ModConfig.walkToY = v)
                .addNumber("Z", () -> ModConfig.walkToZ, -30000000, 30000000, v -> ModConfig.walkToZ = v));
        categories.put("Movement", movement);

        List<Module> render = new ArrayList<>();
        render.add(new Module("ESP", () -> ModConfig.espEnabled, v -> ModConfig.espEnabled = v)
                .addToggle("Players", () -> ModConfig.espPlayers, v -> ModConfig.espPlayers = v)
                .addToggle("Hostile", () -> ModConfig.espHostile, v -> ModConfig.espHostile = v)
                .addToggle("Passive", () -> ModConfig.espPassive, v -> ModConfig.espPassive = v)
                .addToggle("Tracer Lines", () -> ModConfig.espLines, v -> ModConfig.espLines = v)
                .addToggle("Path Trace", () -> ModConfig.espPathTrace, v -> ModConfig.espPathTrace = v));
        render.add(new Module("Fullbright", () -> ModConfig.fullbrightEnabled, v -> ModConfig.fullbrightEnabled = v));
        render.add(new Module("HUD", () -> ModConfig.hudVisible, v -> ModConfig.hudVisible = v));
        categories.put("Render", render);

        List<Module> player = new ArrayList<>();
        player.add(new Module("Auto Elytra", () -> ModConfig.autoElytraSwapEnabled, v -> ModConfig.autoElytraSwapEnabled = v));
        player.add(new Module("Eating Assist", () -> ModConfig.eatingAssistEnabled, v -> ModConfig.eatingAssistEnabled = v)
                .addNumber("Hunger Threshold", () -> (float) ModConfig.eatingHungerThreshold, ModConfig.EATING_HUNGER_MIN, ModConfig.EATING_HUNGER_MAX, v -> ModConfig.eatingHungerThreshold = Math.round(v)));
        categories.put("Player", player);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, BG);
        if (editField != null) { renderEditModal(ctx, mouseX, mouseY, delta); return; }
        int panelW = Math.min(320, this.width - 40);
        int panelX = (this.width - panelW) / 2;
        int panelTop = 20;
        int panelBottom = this.height - 20;
        ctx.fill(panelX - 1, panelTop - 1, panelX + panelW + 1, panelBottom + 1, ACCENT_DIM);
        ctx.fill(panelX, panelTop, panelX + panelW, panelBottom, PANEL_BG);
        ctx.fill(panelX, panelTop, panelX + panelW, panelTop + TAB_H, 0xFF12122A);
        ctx.fill(panelX, panelTop + TAB_H - 1, panelX + panelW, panelTop + TAB_H, ACCENT);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a7d\u00a7lOSP"), panelX + 6, panelTop + 6, TEXT_PRIMARY);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a78v2.1"), panelX + 32, panelTop + 6, TEXT_DIM);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a7cx"), panelX + panelW - 16, panelTop + 6, RED);
        int tabY = panelTop + TAB_H;
        int tabCount = categories.size();
        int tabW = panelW / tabCount;
        int tabIdx = 0;
        for (String cat : categories.keySet()) {
            int tx = panelX + tabIdx * tabW;
            boolean selected = cat.equals(activeCategory);
            boolean hovered = mouseX >= tx && mouseX < tx + tabW && mouseY >= tabY && mouseY < tabY + 20;
            ctx.fill(tx, tabY, tx + tabW, tabY + 20, selected ? 0xFF222244 : (hovered ? 0xFF1A1A38 : 0xFF141430));
            if (selected) ctx.fill(tx, tabY + 18, tx + tabW, tabY + 20, ACCENT);
            int textW = this.textRenderer.getWidth(cat);
            ctx.drawTextWithShadow(this.textRenderer, Text.literal(selected ? "\u00a7f" + cat : "\u00a78" + cat), tx + (tabW - textW) / 2, tabY + 5, TEXT_PRIMARY);
            tabIdx++;
        }
        int listTop = tabY + 20;
        int listBottom = panelBottom - 2;
        ctx.enableScissor(panelX, listTop, panelX + panelW, listBottom);
        List<Module> modules = categories.get(activeCategory);
        if (modules != null) {
            int y = listTop - (int) scrollOffset;
            for (Module mod : modules) { y = renderModule(ctx, mod, panelX + 2, y, panelW - 4, mouseX, mouseY); }
        }
        ctx.disableScissor();
        if (modules != null) {
            int contentH = getContentHeight(modules);
            int viewH = listBottom - listTop;
            if (contentH > viewH) {
                int barX = panelX + panelW - 4;
                float ratio = (float) viewH / contentH;
                int thumbH = Math.max(15, (int) (viewH * ratio));
                int maxScroll = contentH - viewH;
                int thumbY = listTop + (maxScroll > 0 ? (int) (scrollOffset / maxScroll * (viewH - thumbH)) : 0);
                ctx.fill(barX, listTop, barX + 3, listBottom, 0x20FFFFFF);
                ctx.fill(barX, thumbY, barX + 3, thumbY + thumbH, ACCENT);
            }
        }
    }

    private int renderModule(DrawContext ctx, Module mod, int x, int y, int w, int mx, int my) {
        boolean enabled = mod.enabled.get();
        boolean hovered = mx >= x && mx < x + w && my >= y && my < y + MODULE_H;
        boolean expanded = mod.name.equals(expandedModule) && !mod.settings.isEmpty();
        ctx.fill(x, y, x + w, y + MODULE_H, enabled ? MODULE_ON : (hovered ? MODULE_HOVER : MODULE_BG));
        if (enabled) ctx.fill(x, y, x + 2, y + MODULE_H, ACCENT);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal(mod.name), x + 8, y + 6, enabled ? TEXT_PRIMARY : TEXT_DIM);
        String status = enabled ? "\u00a7aON" : "\u00a78OFF";
        int statusW = this.textRenderer.getWidth(enabled ? "ON" : "OFF");
        ctx.drawTextWithShadow(this.textRenderer, Text.literal(status), x + w - statusW - 20, y + 6, TEXT_PRIMARY);
        if (!mod.settings.isEmpty()) {
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a78" + (expanded ? "\u25BC" : "\u25B6")), x + w - 12, y + 6, TEXT_DIM);
        }
        ctx.fill(x, y + MODULE_H - 1, x + w, y + MODULE_H, 0x18FFFFFF);
        y += MODULE_H;
        if (expanded) {
            for (Setting s : mod.settings) {
                ctx.fill(x, y, x + w, y + SETTING_H, SETTING_BG);
                ctx.fill(x + 2, y, x + 3, y + SETTING_H, 0x40FFFFFF);
                if (s.type == SettingType.TOGGLE) {
                    boolean on = s.boolGetter.get();
                    ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a77  " + s.name), x + 10, y + 4, TEXT_DIM);
                    int valW = this.textRenderer.getWidth(on ? "ON" : "OFF");
                    ctx.drawTextWithShadow(this.textRenderer, Text.literal(on ? "\u00a7aON" : "\u00a7cOFF"), x + w - valW - 10, y + 4, TEXT_PRIMARY);
                } else {
                    float val = s.floatGetter.get();
                    ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a77  " + s.name + ": \u00a7f" + formatNumber(val)), x + 10, y + 4, TEXT_DIM);
                    ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a78[\u00a7bedit\u00a78]"), x + w - 28, y + 4, TEXT_DIM);
                }
                ctx.fill(x, y + SETTING_H - 1, x + w, y + SETTING_H, 0x10FFFFFF);
                y += SETTING_H;
            }
        }
        return y;
    }

    private void renderEditModal(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0xC0000000);
        int boxW = 220; int boxH = 90;
        int boxX = this.width / 2 - boxW / 2; int boxY = this.height / 2 - boxH / 2;
        ctx.fill(boxX - 1, boxY - 1, boxX + boxW + 1, boxY + boxH + 1, ACCENT);
        ctx.fill(boxX, boxY, boxX + boxW, boxY + boxH, 0xFF1A1A2E);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("\u00a7b\u00a7l" + editLabel), this.width / 2, boxY + 8, TEXT_PRIMARY);
        editField.setX(this.width / 2 - 100); editField.setY(boxY + 24);
        editField.render(ctx, mouseX, mouseY, delta);
        int btnY = boxY + 50;
        boolean confirmHover = mouseX >= this.width / 2 - 50 && mouseX < this.width / 2 - 5 && mouseY >= btnY && mouseY < btnY + 14;
        boolean cancelHover = mouseX >= this.width / 2 + 5 && mouseX < this.width / 2 + 50 && mouseY >= btnY && mouseY < btnY + 14;
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(confirmHover ? "\u00a7a\u00a7l[Confirm]" : "\u00a7a[Confirm]"), this.width / 2 - 28, btnY, GREEN);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(cancelHover ? "\u00a7c\u00a7l[Cancel]" : "\u00a7c[Cancel]"), this.width / 2 + 28, btnY, RED);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal("\u00a78" + formatNumber(editMin) + " \u2014 " + formatNumber(editMax)), this.width / 2, boxY + boxH - 14, TEXT_DIM);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        if (editField != null) {
            if (editField.isMouseOver(mouseX, mouseY)) { editField.mouseClicked(mouseX, mouseY, button); setFocused(editField); return true; }
            int btnY = this.height / 2 - 45 + 50;
            if (mouseY >= btnY && mouseY < btnY + 14) {
                if (mouseX >= this.width / 2 - 50 && mouseX < this.width / 2 - 5) { confirmEdit(); return true; }
                if (mouseX >= this.width / 2 + 5 && mouseX < this.width / 2 + 50) { cancelEdit(); return true; }
            }
            return true;
        }
        int panelW = Math.min(320, this.width - 40); int panelX = (this.width - panelW) / 2; int panelTop = 20;
        if (mouseX >= panelX + panelW - 18 && mouseX <= panelX + panelW - 4 && mouseY >= panelTop + 2 && mouseY <= panelTop + TAB_H - 2) { close(); return true; }
        int tabY = panelTop + TAB_H;
        if (mouseY >= tabY && mouseY < tabY + 20) {
            int tabW = panelW / categories.size(); int idx = 0;
            for (String cat : categories.keySet()) {
                int tx = panelX + idx * tabW;
                if (mouseX >= tx && mouseX < tx + tabW) { activeCategory = cat; scrollOffset = 0; expandedModule = null; return true; }
                idx++;
            }
        }
        int listTop = tabY + 20;
        List<Module> modules = categories.get(activeCategory);
        if (modules == null) return false;
        int y = listTop - (int) scrollOffset;
        for (Module mod : modules) {
            boolean expanded = mod.name.equals(expandedModule) && !mod.settings.isEmpty();
            if (mouseX >= panelX + 2 && mouseX < panelX + panelW - 2 && mouseY >= y && mouseY < y + MODULE_H) {
                if (mouseX >= panelX + panelW - 30 && !mod.settings.isEmpty()) { expandedModule = expanded ? null : mod.name; }
                else { mod.setter.accept(!mod.enabled.get()); ModConfig.save(); }
                return true;
            }
            y += MODULE_H;
            if (expanded) {
                for (Setting s : mod.settings) {
                    if (mouseX >= panelX + 2 && mouseX < panelX + panelW - 2 && mouseY >= y && mouseY < y + SETTING_H) {
                        if (s.type == SettingType.TOGGLE) { s.boolSetter.accept(!s.boolGetter.get()); ModConfig.save(); }
                        else { openEditModal(s.name, s.floatGetter.get(), s.min, s.max, s.floatSetter); }
                        return true;
                    }
                    y += SETTING_H;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (editField != null) return true;
        List<Module> modules = categories.get(activeCategory);
        if (modules == null) return true;
        int viewH = (this.height - 20) - (20 + TAB_H + 20) - 2;
        int maxScroll = Math.max(0, getContentHeight(modules) - viewH);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - verticalAmount * 16));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (editField != null) {
            if (keyCode == 257) { confirmEdit(); return true; }
            if (keyCode == 256) { cancelEdit(); return true; }
            return editField.keyPressed(keyCode, scanCode, modifiers);
        }
        if (keyCode == 256) { close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (editField != null) return editField.charTyped(chr, modifiers);
        return super.charTyped(chr, modifiers);
    }

    private void openEditModal(String label, float current, float min, float max, java.util.function.Consumer<Float> setter) {
        editLabel = label; editMin = min; editMax = max; editSetter = setter;
        editField = new TextFieldWidget(this.textRenderer, 0, 0, 200, 20, Text.literal(label));
        editField.setText(formatNumber(current)); editField.setMaxLength(15); editField.setEditable(true);
        setFocused(editField);
    }

    private void confirmEdit() {
        if (editField == null || editSetter == null) return;
        try { float val = Math.max(editMin, Math.min(editMax, Float.parseFloat(editField.getText().trim()))); editSetter.accept(val); ModConfig.save(); } catch (NumberFormatException ignored) {}
        cancelEdit();
    }

    private void cancelEdit() { editField = null; editLabel = null; editSetter = null; }

    @Override
    public void close() {
        if (editField != null) { cancelEdit(); return; }
        ModConfig.save();
        if (this.client != null) this.client.setScreen(parent);
    }

    private int getContentHeight(List<Module> modules) {
        int h = 0;
        for (Module mod : modules) { h += MODULE_H; if (mod.name.equals(expandedModule)) h += mod.settings.size() * SETTING_H; }
        return h;
    }

    private static String formatNumber(float val) {
        if (val == Math.floor(val) && !Float.isInfinite(val)) return String.valueOf((int) val);
        return String.format("%.1f", val);
    }

    private enum SettingType { TOGGLE, NUMBER }

    private static class Setting {
        final String name; final SettingType type;
        java.util.function.Supplier<Boolean> boolGetter; java.util.function.Consumer<Boolean> boolSetter;
        java.util.function.Supplier<Float> floatGetter; java.util.function.Consumer<Float> floatSetter;
        float min, max;
        Setting(String name, SettingType type) { this.name = name; this.type = type; }
    }

    private static class Module {
        final String name; final java.util.function.Supplier<Boolean> enabled; final java.util.function.Consumer<Boolean> setter;
        final List<Setting> settings = new ArrayList<>();
        Module(String name, java.util.function.Supplier<Boolean> enabled, java.util.function.Consumer<Boolean> setter) { this.name = name; this.enabled = enabled; this.setter = setter; }
        Module addToggle(String sName, java.util.function.Supplier<Boolean> getter, java.util.function.Consumer<Boolean> setter) {
            Setting s = new Setting(sName, SettingType.TOGGLE); s.boolGetter = getter; s.boolSetter = setter; settings.add(s); return this;
        }
        Module addNumber(String sName, java.util.function.Supplier<Float> getter, float min, float max, java.util.function.Consumer<Float> setter) {
            Setting s = new Setting(sName, SettingType.NUMBER); s.floatGetter = getter; s.floatSetter = setter; s.min = min; s.max = max; settings.add(s); return this;
        }
    }
}
