package com.reachfly;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {

    private final Screen parent;
    private double scrollOffset = 0;
    private int contentHeight = 0;
    private final List<Entry> entries = new ArrayList<>();

    // Active text editing state
    private TextFieldWidget activeTextField = null;
    private String activeLabel = null;
    private java.util.function.Consumer<Float> activeSetter = null;
    private float activeMin, activeMax;
    private ButtonWidget activeSource = null;

    private static final int ROW_HEIGHT = 24;
    private static final int HEADER = 40;
    private static final int FOOTER = 36;
    private static final int BUTTON_W = 240;
    private static final int BUTTON_H = 20;

    // Gradient colors for header
    private static final int HEADER_LEFT = 0xFF8B5CF6;  // Purple
    private static final int HEADER_RIGHT = 0xFF3B82F6; // Blue

    public ConfigScreen(Screen parent) {
        super(Text.literal("Optimizer Super Premium"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        entries.clear();
        clearChildren();
        activeTextField = null;

        // --- REACH ---
        addLabel("\u00a76\u00a7l--- Reach ---");
        addToggle("Reach", () -> ModConfig.reachEnabled, v -> ModConfig.reachEnabled = v);
        addNumberButton("Reach Distance", () -> ModConfig.reachDistance, ModConfig.REACH_MIN, ModConfig.REACH_MAX, v -> ModConfig.reachDistance = v);

        // --- FLY ---
        addLabel("\u00a7b\u00a7l--- Fly ---");
        addToggle("Fly", () -> ModConfig.flyEnabled, v -> ModConfig.flyEnabled = v);
        addNumberButton("Fly Speed", () -> ModConfig.flySpeed, ModConfig.FLY_SPEED_MIN, ModConfig.FLY_SPEED_MAX, v -> ModConfig.flySpeed = v);

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
        addNumberButton("Speed Multiplier", () -> ModConfig.speedMultiplier, ModConfig.SPEED_MIN, ModConfig.SPEED_MAX, v -> ModConfig.speedMultiplier = v);

        // --- X-RAY ---
        addLabel("\u00a7a\u00a7l--- X-Ray ---");
        addToggle("X-Ray (See Through Blocks)", () -> ModConfig.xrayEnabled, v -> ModConfig.xrayEnabled = v);

        // --- KNOCKBACK ---
        addLabel("\u00a7c\u00a7l--- Knockback ---");
        addToggle("Knockback", () -> ModConfig.knockbackEnabled, v -> ModConfig.knockbackEnabled = v);
        addNumberButton("Knockback Strength", () -> ModConfig.knockbackStrength, ModConfig.KNOCKBACK_MIN, ModConfig.KNOCKBACK_MAX, v -> ModConfig.knockbackStrength = v);

        // --- SCAFFOLD ---
        addLabel("\u00a76\u00a7l--- Scaffold ---");
        addToggle("Scaffold (Auto-Bridge)", () -> ModConfig.scaffoldEnabled, v -> ModConfig.scaffoldEnabled = v);

        // --- AUTO TOTEM ---
        addLabel("\u00a7d\u00a7l--- Auto Totem ---");
        addToggle("Auto Totem (Offhand)", () -> ModConfig.autoTotemEnabled, v -> ModConfig.autoTotemEnabled = v);

        // --- AUTO ARMOR ---
        addLabel("\u00a79\u00a7l--- Auto Armor ---");
        addToggle("Auto Armor (Best Equip)", () -> ModConfig.autoArmorEnabled, v -> ModConfig.autoArmorEnabled = v);

        // --- FULLBRIGHT ---
        addLabel("\u00a7e\u00a7l--- Fullbright ---");
        addToggle("Fullbright", () -> ModConfig.fullbrightEnabled, v -> ModConfig.fullbrightEnabled = v);

        // --- AUTO HIT ---
        addLabel("\u00a7c\u00a7l--- Auto Hit ---");
        addToggle("Auto Hit", () -> ModConfig.autoHitEnabled, v -> ModConfig.autoHitEnabled = v);
        addNumberButton("Auto Hit Range", () -> ModConfig.autoHitRange, ModConfig.AUTO_HIT_RANGE_MIN, ModConfig.AUTO_HIT_RANGE_MAX, v -> ModConfig.autoHitRange = v);
        addToggle("Auto Hit Players Only", () -> ModConfig.autoHitPlayersOnly, v -> ModConfig.autoHitPlayersOnly = v);
        addToggle("Kill Aura (Hit All In Range)", () -> ModConfig.killAuraEnabled, v -> ModConfig.killAuraEnabled = v);

        // --- LOW HEALTH KILL ---
        addLabel("\u00a74\u00a7l--- Low Health Kill ---");
        addToggle("Low Health Kill", () -> ModConfig.lowHealthKillEnabled, v -> ModConfig.lowHealthKillEnabled = v);
        addNumberButton("Health Threshold", () -> ModConfig.lowHealthThreshold, ModConfig.LOW_HEALTH_MIN, ModConfig.LOW_HEALTH_MAX, v -> ModConfig.lowHealthThreshold = v);

        // --- AUTO KILL WHEN LOW ---
        addLabel("\u00a76\u00a7l--- Auto Kill (Self Low HP) ---");
        addToggle("Auto Kill When Low", () -> ModConfig.autoKillWhenLowEnabled, v -> ModConfig.autoKillWhenLowEnabled = v);
        addNumberButton("Your HP Threshold", () -> ModConfig.autoKillSelfHpThreshold, ModConfig.AUTO_KILL_SELF_HP_MIN, ModConfig.AUTO_KILL_SELF_HP_MAX, v -> ModConfig.autoKillSelfHpThreshold = v);
        addNumberButton("Kill Range", () -> ModConfig.autoKillWhenLowRange, ModConfig.AUTO_KILL_RANGE_MIN, ModConfig.AUTO_KILL_RANGE_MAX, v -> ModConfig.autoKillWhenLowRange = v);

        // --- AUTO ELYTRA SWAP ---
        addLabel("\u00a7b\u00a7l--- Auto Elytra Swap ---");
        addToggle("Elytra Swap", () -> ModConfig.autoElytraSwapEnabled, v -> ModConfig.autoElytraSwapEnabled = v);

        // --- FLY TO COORDS ---
        addLabel("\u00a73\u00a7l--- Fly to Coords ---");
        addToggle("Fly to Coords", () -> ModConfig.flyToCoordsEnabled, v -> {
            ModConfig.flyToCoordsEnabled = v;
            if (!v) FlyToCoordsHandler.onDisable();
        });
        addNumberButton("Target X", () -> ModConfig.flyToX, -30000000, 30000000, v -> ModConfig.flyToX = v);
        addNumberButton("Target Y", () -> ModConfig.flyToY, -64, 320, v -> ModConfig.flyToY = v);
        addNumberButton("Target Z", () -> ModConfig.flyToZ, -30000000, 30000000, v -> ModConfig.flyToZ = v);
        addNumberButton("Fly Speed", () -> ModConfig.flyToCoordsSpeed, ModConfig.FLY_TO_SPEED_MIN, ModConfig.FLY_TO_SPEED_MAX, v -> ModConfig.flyToCoordsSpeed = v);

        // --- WALK TO COORDS ---
        addLabel("\u00a7a\u00a7l--- Walk to Coords ---");
        addToggle("Walk to Coords", () -> ModConfig.walkToCoordsEnabled, v -> {
            ModConfig.walkToCoordsEnabled = v;
            if (!v) WalkToCoordsHandler.onDisable();
        });
        addNumberButton("Walk Target X", () -> ModConfig.walkToX, -30000000, 30000000, v -> ModConfig.walkToX = v);
        addNumberButton("Walk Target Y", () -> ModConfig.walkToY, -64, 320, v -> ModConfig.walkToY = v);
        addNumberButton("Walk Target Z", () -> ModConfig.walkToZ, -30000000, 30000000, v -> ModConfig.walkToZ = v);

        // --- TELEPORT ---
        addLabel("\u00a75\u00a7l--- Teleport (press T) ---");
        addToggle("Normal Mode (needs addon on server)", () -> ModConfig.tpUseServerAddon, v -> ModConfig.tpUseServerAddon = v);
        addNumberButton("TP Target X", () -> ModConfig.tpX, -30000000, 30000000, v -> ModConfig.tpX = v);
        addNumberButton("TP Target Y", () -> ModConfig.tpY, -64, 320, v -> ModConfig.tpY = v);
        addNumberButton("TP Target Z", () -> ModConfig.tpZ, -30000000, 30000000, v -> ModConfig.tpZ = v);

        // --- EATING ASSIST ---
        addLabel("\u00a7a\u00a7l--- Eating Assist ---");
        addToggle("Eating Assist", () -> ModConfig.eatingAssistEnabled, v -> ModConfig.eatingAssistEnabled = v);
        addNumberButton("Hunger Threshold", () -> (float) ModConfig.eatingHungerThreshold, ModConfig.EATING_HUNGER_MIN, ModConfig.EATING_HUNGER_MAX, v -> ModConfig.eatingHungerThreshold = Math.round(v));

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
                    if (activeTextField != null) return; // Don't toggle while editing
                    setter.accept(!getter.get());
                    b.setMessage(toggleText(label, getter.get()));
                    ModConfig.save();
                }
        ).dimensions(0, 0, BUTTON_W, BUTTON_H).build();
        addDrawableChild(btn);
        entries.add(new Entry(null, btn));
    }

    private void addNumberButton(String label, java.util.function.Supplier<Float> getter,
                                  float min, float max,
                                  java.util.function.Consumer<Float> setter) {
        ButtonWidget btn = ButtonWidget.builder(
                Text.literal(label + ": " + formatNumber(getter.get()) + "  \u00a77[click to edit]"),
                b -> {
                    openEditor(label, getter.get(), min, max, setter, b);
                }
        ).dimensions(0, 0, BUTTON_W, BUTTON_H).build();
        addDrawableChild(btn);
        entries.add(new Entry(null, btn));
    }

    // Confirm/cancel buttons for edit mode
    private ButtonWidget confirmBtn = null;
    private ButtonWidget cancelBtn = null;

    private void openEditor(String label, float current, float min, float max,
                             java.util.function.Consumer<Float> setter, ButtonWidget source) {
        // Remove previous edit widgets
        cancelEdit();

        activeLabel = label;
        activeSetter = setter;
        activeMin = min;
        activeMax = max;
        activeSource = source;

        // Create text field at fixed position at top of screen
        int fieldW = 200;
        int fieldX = this.width / 2 - fieldW / 2;
        int fieldY = 22;
        activeTextField = new TextFieldWidget(this.textRenderer, fieldX, fieldY, fieldW, BUTTON_H,
                Text.literal(label));
        activeTextField.setText(formatNumber(current));
        activeTextField.setMaxLength(15);
        activeTextField.setEditable(true);
        addDrawableChild(activeTextField);
        setFocused(activeTextField);

        // Confirm button
        confirmBtn = ButtonWidget.builder(Text.literal("\u00a7aConfirm"), b -> confirmEdit())
                .dimensions(this.width / 2 - 102, fieldY + 24, 100, BUTTON_H).build();
        addDrawableChild(confirmBtn);

        // Cancel button
        cancelBtn = ButtonWidget.builder(Text.literal("\u00a7cCancel"), b -> cancelEdit())
                .dimensions(this.width / 2 + 2, fieldY + 24, 100, BUTTON_H).build();
        addDrawableChild(cancelBtn);
    }

    private void confirmEdit() {
        if (activeTextField == null || activeSetter == null) return;

        String text = activeTextField.getText().trim();
        try {
            float val = Float.parseFloat(text);
            val = Math.max(activeMin, Math.min(activeMax, val));
            activeSetter.accept(val);
            ModConfig.save();

            // Update the source button text
            if (activeSource != null) {
                activeSource.setMessage(Text.literal(
                        activeLabel + ": " + formatNumber(val) + "  \u00a77[click to edit]"));
            }
        } catch (NumberFormatException ignored) {
        }

        cancelEdit();
    }

    private void cancelEdit() {
        if (activeTextField != null) {
            remove(activeTextField);
            activeTextField = null;
        }
        if (confirmBtn != null) {
            remove(confirmBtn);
            confirmBtn = null;
        }
        if (cancelBtn != null) {
            remove(cancelBtn);
            cancelBtn = null;
        }
        activeLabel = null;
        activeSetter = null;
        activeSource = null;
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
                e.widget.setX(centerX);
                e.widget.setY(entryY);
                boolean inView = (entryY + BUTTON_H > viewTop && entryY < viewBottom);
                // Don't hide widgets while editing
                e.widget.visible = inView;
                e.widget.active = inView && activeTextField == null;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        repositionWidgets();

        int viewTop = HEADER;
        int viewBottom = this.height - FOOTER;

        // If editing, draw a darkened overlay with edit box
        if (activeTextField != null) {
            context.fill(0, 0, this.width, this.height, 0xC0000000);

            // Draw editing box background with border
            int boxW = 240;
            int boxH = 90;
            int boxX = this.width / 2 - boxW / 2;
            int boxY = this.height / 2 - boxH / 2 - 20;
            // Outer border (purple accent)
            context.fill(boxX - 1, boxY - 1, boxX + boxW + 1, boxY + boxH + 1, 0xFF8B5CF6);
            // Inner background
            context.fill(boxX, boxY, boxX + boxW, boxY + boxH, 0xFF1A1A2E);

            // Label above field
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("\u00a7b\u00a7l" + activeLabel),
                    this.width / 2, boxY + 8, 0xFFFFFF);

            // Reposition text field and buttons to center of screen
            if (activeTextField != null) {
                activeTextField.setX(this.width / 2 - 100);
                activeTextField.setY(boxY + 24);
            }
            if (confirmBtn != null) {
                confirmBtn.setX(this.width / 2 - 102);
                confirmBtn.setY(boxY + 50);
            }
            if (cancelBtn != null) {
                cancelBtn.setX(this.width / 2 + 2);
                cancelBtn.setY(boxY + 50);
            }

            // Render the text field, confirm, cancel via super (they're added as children)
            super.render(context, mouseX, mouseY, delta);

            // Range hint below buttons
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("\u00a78Range: " + formatNumber(activeMin) + " \u2014 " + formatNumber(activeMax)),
                    this.width / 2, boxY + boxH - 12, 0x888888);
        } else {
            // Main background
            context.fill(0, 0, this.width, this.height, 0xFF0D0D1A);

            // Header gradient bar
            context.fill(0, 0, this.width, HEADER, 0xFF16162E);
            // Accent line under header
            context.fill(0, HEADER - 1, this.width, HEADER, 0xFF8B5CF6);

            // Content area
            context.fill(0, viewTop, this.width, viewBottom, 0xFF111122);

            context.enableScissor(0, viewTop, this.width, viewBottom);

            super.render(context, mouseX, mouseY, delta);

            for (int i = 0; i < entries.size(); i++) {
                Entry e = entries.get(i);
                if (e.label != null && e.widget == null) {
                    int entryY = HEADER + i * ROW_HEIGHT - (int) scrollOffset + 5;
                    if (entryY + 10 > viewTop && entryY < viewBottom) {
                        // Draw subtle separator line above category labels
                        int lineY = entryY - 2;
                        int lineX1 = this.width / 2 - BUTTON_W / 2;
                        int lineX2 = this.width / 2 + BUTTON_W / 2;
                        context.fill(lineX1, lineY, lineX2, lineY + 1, 0x40FFFFFF);

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
                int barX = this.width / 2 + BUTTON_W / 2 + 10;
                int barW = 4;
                float ratio = (float) viewH / contentHeight;
                int thumbH = Math.max(20, (int) (viewH * ratio));
                int maxScroll = contentHeight - viewH;
                int thumbY = viewTop + (maxScroll > 0 ? (int) (scrollOffset / maxScroll * (viewH - thumbH)) : 0);

                // Track
                context.fill(barX, viewTop, barX + barW, viewBottom, 0x20FFFFFF);
                // Thumb with rounded feel
                context.fill(barX, thumbY, barX + barW, thumbY + thumbH, 0xA08B5CF6);
            }

            // Footer accent line
            context.fill(0, viewBottom, this.width, viewBottom + 1, 0xFF8B5CF6);

            // Title - "Optimizer Super Premium" with subtitle
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("\u00a7d\u00a7lOptimizer Super Premium"),
                    this.width / 2, 8, 0xFFFFFF);
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("\u00a78v2.1 \u00a75|\u00a78 Settings"),
                    this.width / 2, 20, 0x888888);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (activeTextField != null) return true; // No scroll while editing
        int viewH = this.height - HEADER - FOOTER;
        int maxScroll = Math.max(0, contentHeight - viewH);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - verticalAmount * 10));
        return true;
    }

    @Override
    public void close() {
        if (activeTextField != null) {
            cancelEdit();
            return; // First close cancels edit, second close closes screen
        }
        ModConfig.save();
        if (this.client != null) this.client.setScreen(parent);
    }

    private record Entry(String label, net.minecraft.client.gui.widget.ClickableWidget widget) {}
}
