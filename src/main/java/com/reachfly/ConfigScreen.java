package com.reachfly;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
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
        super(Text.literal("Optimizer Super Premium"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        entries.clear();
        clearChildren();

        // --- REACH ---
        addLabel("\u00a76\u00a7l--- Reach ---");
        addToggle("Reach", () -> ModConfig.reachEnabled, v -> ModConfig.reachEnabled = v);
        addNumberField("Reach Distance", ModConfig.reachDistance, ModConfig.REACH_MIN, ModConfig.REACH_MAX, v -> ModConfig.reachDistance = v);

        // --- FLY ---
        addLabel("\u00a7b\u00a7l--- Fly ---");
        addToggle("Fly", () -> ModConfig.flyEnabled, v -> ModConfig.flyEnabled = v);
        addNumberField("Fly Speed", ModConfig.flySpeed, ModConfig.FLY_SPEED_MIN, ModConfig.FLY_SPEED_MAX, v -> ModConfig.flySpeed = v);

        // --- ESP ---
        addLabel("\u00a7d\u00a7l--- ESP ---");
        addToggle("ESP", () -> ModConfig.espEnabled, v -> ModConfig.espEnabled = v);
        addToggle("ESP Players", () -> ModConfig.espPlayers, v -> ModConfig.espPlayers = v);
        addToggle("ESP Hostile", () -> ModConfig.espHostile, v -> ModConfig.espHostile = v);
        addToggle("ESP Passive", () -> ModConfig.espPassive, v -> ModConfig.espPassive = v);
        addToggle("ESP Tracer Lines", () -> ModConfig.espLines, v -> ModConfig.espLines = v);
        addToggle("ESP Path Trace", () -> ModConfig.espPathTrace, v -> ModConfig.espPathTrace = v);

        // --- JESUS ---
        addLabel("\u00a73\u00a7l--- Jesus ---");
        addToggle("Jesus (Walk on Water)", () -> ModConfig.jesusEnabled, v -> ModConfig.jesusEnabled = v);

        // --- NOFALL ---
        addLabel("\u00a7e\u00a7l--- NoFall ---");
        addToggle("NoFall", () -> ModConfig.noFallEnabled, v -> ModConfig.noFallEnabled = v);

        // --- SPEED ---
        addLabel("\u00a7f\u00a7l--- Speed ---");
        addToggle("Speed", () -> ModConfig.speedEnabled, v -> ModConfig.speedEnabled = v);
        addNumberField("Speed Multiplier", ModConfig.speedMultiplier, ModConfig.SPEED_MIN, ModConfig.SPEED_MAX, v -> ModConfig.speedMultiplier = v);

        // --- X-RAY ---
        addLabel("\u00a7a\u00a7l--- X-Ray ---");
        addToggle("X-Ray (See Through Blocks)", () -> ModConfig.xrayEnabled, v -> ModConfig.xrayEnabled = v);

        // --- KNOCKBACK ---
        addLabel("\u00a7c\u00a7l--- Knockback ---");
        addToggle("Knockback", () -> ModConfig.knockbackEnabled, v -> ModConfig.knockbackEnabled = v);
        addNumberField("Knockback Strength", ModConfig.knockbackStrength, ModConfig.KNOCKBACK_MIN, ModConfig.KNOCKBACK_MAX, v -> ModConfig.knockbackStrength = v);

        // --- FULLBRIGHT ---
        addLabel("\u00a7e\u00a7l--- Fullbright ---");
        addToggle("Fullbright", () -> ModConfig.fullbrightEnabled, v -> ModConfig.fullbrightEnabled = v);

        // --- AUTO HIT ---
        addLabel("\u00a7c\u00a7l--- Auto Hit ---");
        addToggle("Auto Hit", () -> ModConfig.autoHitEnabled, v -> ModConfig.autoHitEnabled = v);
        addNumberField("Auto Hit Range", ModConfig.autoHitRange, ModConfig.AUTO_HIT_RANGE_MIN, ModConfig.AUTO_HIT_RANGE_MAX, v -> ModConfig.autoHitRange = v);
        addToggle("Auto Hit Players Only", () -> ModConfig.autoHitPlayersOnly, v -> ModConfig.autoHitPlayersOnly = v);
        addToggle("Kill Aura (Hit All In Range)", () -> ModConfig.killAuraEnabled, v -> ModConfig.killAuraEnabled = v);

        // --- LOW HEALTH KILL ---
        addLabel("\u00a74\u00a7l--- Low Health Kill ---");
        addToggle("Low Health Kill", () -> ModConfig.lowHealthKillEnabled, v -> ModConfig.lowHealthKillEnabled = v);
        addNumberField("Health Threshold", ModConfig.lowHealthThreshold, ModConfig.LOW_HEALTH_MIN, ModConfig.LOW_HEALTH_MAX, v -> ModConfig.lowHealthThreshold = v);

        // --- AUTO KILL WHEN LOW ---
        addLabel("\u00a76\u00a7l--- Auto Kill (Self Low HP) ---");
        addToggle("Auto Kill When Low", () -> ModConfig.autoKillWhenLowEnabled, v -> ModConfig.autoKillWhenLowEnabled = v);
        addNumberField("Your HP Threshold", ModConfig.autoKillSelfHpThreshold, ModConfig.AUTO_KILL_SELF_HP_MIN, ModConfig.AUTO_KILL_SELF_HP_MAX, v -> ModConfig.autoKillSelfHpThreshold = v);
        addNumberField("Kill Range", ModConfig.autoKillWhenLowRange, ModConfig.AUTO_KILL_RANGE_MIN, ModConfig.AUTO_KILL_RANGE_MAX, v -> ModConfig.autoKillWhenLowRange = v);

        // --- AUTO ELYTRA SWAP ---
        addLabel("\u00a7b\u00a7l--- Auto Elytra Swap ---");
        addToggle("Elytra Swap", () -> ModConfig.autoElytraSwapEnabled, v -> ModConfig.autoElytraSwapEnabled = v);

        // --- FLY TO COORDS ---
        addLabel("\u00a73\u00a7l--- Fly to Coords ---");
        addToggle("Fly to Coords", () -> ModConfig.flyToCoordsEnabled, v -> {
            ModConfig.flyToCoordsEnabled = v;
            if (!v) FlyToCoordsHandler.onDisable();
        });
        addNumberField("Target X", ModConfig.flyToX, -30000000, 30000000, v -> ModConfig.flyToX = v);
        addNumberField("Target Y", ModConfig.flyToY, -64, 320, v -> ModConfig.flyToY = v);
        addNumberField("Target Z", ModConfig.flyToZ, -30000000, 30000000, v -> ModConfig.flyToZ = v);
        addNumberField("Fly Speed", ModConfig.flyToCoordsSpeed, ModConfig.FLY_TO_SPEED_MIN, ModConfig.FLY_TO_SPEED_MAX, v -> ModConfig.flyToCoordsSpeed = v);

        // --- WALK TO COORDS ---
        addLabel("\u00a7a\u00a7l--- Walk to Coords ---");
        addToggle("Walk to Coords", () -> ModConfig.walkToCoordsEnabled, v -> {
            ModConfig.walkToCoordsEnabled = v;
            if (!v) WalkToCoordsHandler.onDisable();
        });
        addNumberField("Walk Target X", ModConfig.walkToX, -30000000, 30000000, v -> ModConfig.walkToX = v);
        addNumberField("Walk Target Y", ModConfig.walkToY, -64, 320, v -> ModConfig.walkToY = v);
        addNumberField("Walk Target Z", ModConfig.walkToZ, -30000000, 30000000, v -> ModConfig.walkToZ = v);

        // --- TELEPORT ---
        addLabel("\u00a75\u00a7l--- Teleport (press T) ---");
        addToggle("Normal Mode (needs addon on server)", () -> ModConfig.tpUseServerAddon, v -> ModConfig.tpUseServerAddon = v);
        addNumberField("TP Target X", ModConfig.tpX, -30000000, 30000000, v -> ModConfig.tpX = v);
        addNumberField("TP Target Y", ModConfig.tpY, -64, 320, v -> ModConfig.tpY = v);
        addNumberField("TP Target Z", ModConfig.tpZ, -30000000, 30000000, v -> ModConfig.tpZ = v);

        // --- EATING ASSIST ---
        addLabel("\u00a7a\u00a7l--- Eating Assist ---");
        addToggle("Eating Assist", () -> ModConfig.eatingAssistEnabled, v -> ModConfig.eatingAssistEnabled = v);
        addNumberField("Hunger Threshold", ModConfig.eatingHungerThreshold, ModConfig.EATING_HUNGER_MIN, ModConfig.EATING_HUNGER_MAX, v -> ModConfig.eatingHungerThreshold = Math.round(v));

        // --- HUD ---
        addLabel("\u00a77\u00a7l--- Display ---");
        addToggle("Show HUD", () -> ModConfig.hudVisible, v -> ModConfig.hudVisible = v);

        contentHeight = entries.size() * ROW_HEIGHT;

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

    private void addNumberField(String label, float current, float min, float max,
                                 java.util.function.Consumer<Float> setter) {
        // Label (80px) + TextFieldWidget (120px)
        int fieldW = 120;
        TextFieldWidget field = new TextFieldWidget(this.textRenderer, 0, 0, fieldW, BUTTON_H,
                Text.literal(label));
        field.setText(formatNumber(current));
        field.setMaxLength(15);
        field.setChangedListener(text -> {
            try {
                float val = Float.parseFloat(text.trim());
                val = Math.max(min, Math.min(max, val));
                setter.accept(val);
                ModConfig.save();
                field.setEditableColor(0xFFFFFF);
            } catch (NumberFormatException e) {
                field.setEditableColor(0xFF5555);
            }
        });
        addDrawableChild(field);
        entries.add(new Entry(label, field));
    }

    private static String formatNumber(float val) {
        if (val == Math.floor(val) && !Float.isInfinite(val)) {
            return String.valueOf((int) val);
        }
        return String.format("%.1f", val);
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
                if (e.widget instanceof TextFieldWidget) {
                    // Position text field to the right, leaving room for label
                    int fieldW = 120;
                    e.widget.setX(centerX + BUTTON_W - fieldW);
                    e.widget.setY(entryY);
                    e.widget.setWidth(fieldW);
                } else {
                    e.widget.setX(centerX);
                    e.widget.setY(entryY);
                }
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
        int centerX = this.width / 2 - BUTTON_W / 2;

        context.fill(0, viewTop, this.width, viewBottom, 0xC0101010);

        context.enableScissor(0, viewTop, this.width, viewBottom);

        super.render(context, mouseX, mouseY, delta);

        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            int entryY = HEADER + i * ROW_HEIGHT - (int) scrollOffset + 5;
            if (entryY + 10 > viewTop && entryY < viewBottom) {
                if (e.label != null && e.widget == null) {
                    // Section header label - centered
                    Text text = Text.literal(e.label);
                    int textW = this.textRenderer.getWidth(text);
                    context.drawTextWithShadow(this.textRenderer, text,
                            (this.width - textW) / 2, entryY, 0xFFFFFF);
                } else if (e.label != null && e.widget instanceof TextFieldWidget) {
                    // Number field label - draw to the left of the text field
                    context.drawTextWithShadow(this.textRenderer,
                            Text.literal("\u00a7f" + e.label),
                            centerX, entryY, 0xFFFFFF);
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

            context.fill(barX, viewTop, barX + barW, viewBottom, 0x40FFFFFF);
            context.fill(barX, thumbY, barX + barW, thumbY + thumbH, 0xC0AAAAAA);
        }

        // Title
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("\u00a7b\u00a7lOptimizer\u00a7r \u00a76\u00a7lSuper \u00a7d\u00a7lPremium\u00a7r \u00a77v2.0"),
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
}
