package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.Click;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemGiveScreen extends Screen {

    private final Screen parent;
    private TextFieldWidget searchField;
    private List<ItemEntry> allItems;
    private List<ItemEntry> filteredItems;
    private double scrollOffset = 0;
    private String lastQuery = "";

    // Quantity selection
    private ItemEntry selectedItem = null;
    private TextFieldWidget qtyField = null;

    // Toast notification
    private String toastMessage = null;
    private int toastTimer = 0;

    // Layout constants
    private static final int ITEM_SIZE = 20;
    private static final int GRID_PAD = 2;
    private static final int CELL = ITEM_SIZE + GRID_PAD;

    // Colors (matching ConfigScreen style)
    private static final int PANEL_BG = 0xF0181828;
    private static final int ACCENT = 0xFF8B5CF6;
    private static final int ACCENT_DIM = 0xFF5B3CB6;
    private static final int TEXT_PRIMARY = 0xFFE0E0E0;
    private static final int TEXT_DIM = 0xFF888898;
    private static final int GREEN = 0xFF4ADE80;
    private static final int RED = 0xFFEF4444;
    private static final int GOLD = 0xFFFFD700;
    private static final int HOVER_BG = 0xFF282848;

    // Special items start code (must match give_item.mcfunction)
    private static final int SPECIAL_START = 1481;

    // Potion effects sorted alphabetically (must match mcfunction order exactly)
    private static final String[][] POTION_EFFECTS = {
        {"awkward", "Awkward"},
        {"fire_resistance", "Fire Resistance"},
        {"harming", "Harming"},
        {"healing", "Healing"},
        {"infested", "Infested"},
        {"invisibility", "Invisibility"},
        {"leaping", "Leaping"},
        {"long_fire_resistance", "Fire Resistance (Long)"},
        {"long_invisibility", "Invisibility (Long)"},
        {"long_leaping", "Leaping (Long)"},
        {"long_night_vision", "Night Vision (Long)"},
        {"long_poison", "Poison (Long)"},
        {"long_regeneration", "Regeneration (Long)"},
        {"long_slow_falling", "Slow Falling (Long)"},
        {"long_slowness", "Slowness (Long)"},
        {"long_strength", "Strength (Long)"},
        {"long_swiftness", "Swiftness (Long)"},
        {"long_turtle_master", "Turtle Master (Long)"},
        {"long_water_breathing", "Water Breathing (Long)"},
        {"long_weakness", "Weakness (Long)"},
        {"luck", "Luck"},
        {"mundane", "Mundane"},
        {"night_vision", "Night Vision"},
        {"oozing", "Oozing"},
        {"poison", "Poison"},
        {"regeneration", "Regeneration"},
        {"slow_falling", "Slow Falling"},
        {"slowness", "Slowness"},
        {"strength", "Strength"},
        {"strong_harming", "Harming II"},
        {"strong_healing", "Healing II"},
        {"strong_leaping", "Leaping II"},
        {"strong_poison", "Poison II"},
        {"strong_regeneration", "Regeneration II"},
        {"strong_slowness", "Slowness IV"},
        {"strong_strength", "Strength II"},
        {"strong_swiftness", "Swiftness II"},
        {"strong_turtle_master", "Turtle Master II"},
        {"swiftness", "Swiftness"},
        {"thick", "Thick"},
        {"turtle_master", "Turtle Master"},
        {"water", "Water Bottle"},
        {"water_breathing", "Water Breathing"},
        {"weakness", "Weakness"},
        {"weaving", "Weaving"},
        {"wind_charged", "Wind Charged"},
    };

    // Enchantments sorted alphabetically: {id, maxLevel, displayName}
    private static final Object[][] ENCHANTMENTS = {
        {"aqua_affinity", 1, "Aqua Affinity"},
        {"bane_of_arthropods", 5, "Bane of Arthropods V"},
        {"binding_curse", 1, "Curse of Binding"},
        {"blast_protection", 4, "Blast Protection IV"},
        {"breach", 4, "Breach IV"},
        {"channeling", 1, "Channeling"},
        {"density", 5, "Density V"},
        {"depth_strider", 3, "Depth Strider III"},
        {"efficiency", 5, "Efficiency V"},
        {"feather_falling", 4, "Feather Falling IV"},
        {"fire_aspect", 2, "Fire Aspect II"},
        {"fire_protection", 4, "Fire Protection IV"},
        {"flame", 1, "Flame"},
        {"fortune", 3, "Fortune III"},
        {"frost_walker", 2, "Frost Walker II"},
        {"impaling", 5, "Impaling V"},
        {"infinity", 1, "Infinity"},
        {"knockback", 2, "Knockback II"},
        {"looting", 3, "Looting III"},
        {"loyalty", 3, "Loyalty III"},
        {"luck_of_the_sea", 3, "Luck of the Sea III"},
        {"lure", 3, "Lure III"},
        {"mending", 1, "Mending"},
        {"multishot", 1, "Multishot"},
        {"piercing", 4, "Piercing IV"},
        {"power", 5, "Power V"},
        {"projectile_protection", 4, "Projectile Protection IV"},
        {"protection", 4, "Protection IV"},
        {"punch", 2, "Punch II"},
        {"quick_charge", 3, "Quick Charge III"},
        {"respiration", 3, "Respiration III"},
        {"riptide", 3, "Riptide III"},
        {"sharpness", 5, "Sharpness V"},
        {"silk_touch", 1, "Silk Touch"},
        {"smite", 5, "Smite V"},
        {"soul_speed", 3, "Soul Speed III"},
        {"sweeping_edge", 3, "Sweeping Edge III"},
        {"swift_sneak", 3, "Swift Sneak III"},
        {"thorns", 3, "Thorns III"},
        {"unbreaking", 3, "Unbreaking III"},
        {"vanishing_curse", 1, "Curse of Vanishing"},
        {"wind_burst", 3, "Wind Burst III"},
    };

    // Trigger codes computed from sorted item registry (must match give_item.mcfunction order)
    private static Map<String, Integer> triggerCodes = null;

    private static Map<String, Integer> getTriggerCodes() {
        if (triggerCodes == null) {
            triggerCodes = new HashMap<>();
            List<String> ids = new ArrayList<>();
            for (Item item : Registries.ITEM) {
                ItemStack stack = new ItemStack(item);
                if (stack.isEmpty()) continue; // Skip air
                ids.add(Registries.ITEM.getId(item).toString());
            }
            Collections.sort(ids);
            for (int i = 0; i < ids.size(); i++) {
                triggerCodes.put(ids.get(i), i + 1);
            }
        }
        return triggerCodes;
    }

    public ItemGiveScreen(Screen parent) {
        super(Text.literal("Item Give"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        allItems = new ArrayList<>();

        // Regular items from registry
        for (Item item : Registries.ITEM) {
            Identifier id = Registries.ITEM.getId(item);
            ItemStack stack = new ItemStack(item);
            if (stack.isEmpty()) continue;
            String name;
            try {
                name = stack.getName().getString();
            } catch (Exception e) {
                name = id.getPath();
            }
            allItems.add(new ItemEntry(item, id, stack, name, 0, id.toString()));
        }

        // Special items: potions, splash potions, lingering potions, tipped arrows, enchanted books
        int code = SPECIAL_START;
        code = addPotionEntries(Items.POTION, "Potion of ", code);
        code = addPotionEntries(Items.SPLASH_POTION, "Splash P. of ", code);
        code = addPotionEntries(Items.LINGERING_POTION, "Lingering P. of ", code);
        code = addPotionEntries(Items.TIPPED_ARROW, "Arrow of ", code);
        addEnchantedBookEntries(code);

        // Search field
        int panelW = Math.min(400, this.width - 40);
        int panelX = (this.width - panelW) / 2;
        searchField = new TextFieldWidget(this.textRenderer, panelX + 6, 28, panelW - 50, 16, Text.literal("Search"));
        searchField.setMaxLength(50);
        searchField.setEditable(true);
        searchField.setChangedListener(q -> {
            if (!q.equals(lastQuery)) {
                lastQuery = q;
                filterItems();
                scrollOffset = 0;
            }
        });
        setFocused(searchField);

        filterItems();
    }

    private int addPotionEntries(Item containerItem, String prefix, int startCode) {
        Identifier containerId = Registries.ITEM.getId(containerItem);
        for (int i = 0; i < POTION_EFFECTS.length; i++) {
            String effectName = POTION_EFFECTS[i][1];
            String effectId = POTION_EFFECTS[i][0];
            allItems.add(new ItemEntry(containerItem, containerId,
                    new ItemStack(containerItem), prefix + effectName,
                    startCode + i, "Effect: " + effectId));
        }
        return startCode + POTION_EFFECTS.length;
    }

    private void addEnchantedBookEntries(int startCode) {
        Identifier bookId = Registries.ITEM.getId(Items.ENCHANTED_BOOK);
        for (int i = 0; i < ENCHANTMENTS.length; i++) {
            String enchName = (String) ENCHANTMENTS[i][2];
            String enchId = (String) ENCHANTMENTS[i][0];
            allItems.add(new ItemEntry(Items.ENCHANTED_BOOK, bookId,
                    new ItemStack(Items.ENCHANTED_BOOK), "Book: " + enchName,
                    startCode + i, "Enchantment: " + enchId));
        }
    }

    private void filterItems() {
        String query = lastQuery.toLowerCase(Locale.ROOT).trim();
        if (query.isEmpty()) {
            filteredItems = new ArrayList<>(allItems);
        } else {
            filteredItems = allItems.stream()
                    .filter(e -> e.name.toLowerCase(Locale.ROOT).contains(query)
                            || e.id.getPath().contains(query)
                            || e.id.toString().contains(query)
                            || e.subtitle.toLowerCase(Locale.ROOT).contains(query))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        int panelW = Math.min(400, this.width - 40);
        int panelX = (this.width - panelW) / 2;
        int panelTop = 16;
        int panelBottom = this.height - 16;

        // Panel border + background
        ctx.fill(panelX - 1, panelTop - 1, panelX + panelW + 1, panelBottom + 1, ACCENT_DIM);
        ctx.fill(panelX, panelTop, panelX + panelW, panelBottom, PANEL_BG);

        // Title bar
        ctx.fill(panelX, panelTop, panelX + panelW, panelTop + 24, 0xFF12122A);
        ctx.fill(panelX, panelTop + 23, panelX + panelW, panelTop + 24, GOLD);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a76\u00a7lItem Give"), panelX + 6, panelTop + 7, TEXT_PRIMARY);

        // Item count
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a78" + filteredItems.size() + " items"),
                panelX + panelW - 60, panelTop + 7, TEXT_DIM);

        // Close button
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a7cx"), panelX + panelW - 14, panelTop + 7, RED);

        // Search field
        searchField.render(ctx, mouseX, mouseY, delta);

        // Grid area
        int gridTop = panelTop + 48;
        int gridBottom = panelBottom - 4;
        int gridW = panelW - 16;
        int cols = Math.max(1, gridW / CELL);

        ctx.enableScissor(panelX + 4, gridTop, panelX + panelW - 4, gridBottom);

        int rows = (filteredItems.size() + cols - 1) / cols;
        int contentH = rows * CELL;
        int viewH = gridBottom - gridTop;

        // Render item grid
        String tooltipName = null;
        String tooltipSub = null;
        for (int i = 0; i < filteredItems.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int ix = panelX + 8 + col * CELL;
            int iy = gridTop + row * CELL - (int) scrollOffset;

            if (iy + CELL < gridTop || iy > gridBottom) continue;

            ItemEntry entry = filteredItems.get(i);
            boolean hovered = mouseX >= ix && mouseX < ix + ITEM_SIZE && mouseY >= iy && mouseY < iy + ITEM_SIZE
                    && mouseY >= gridTop && mouseY < gridBottom;

            if (hovered) {
                ctx.fill(ix - 1, iy - 1, ix + ITEM_SIZE + 1, iy + ITEM_SIZE + 1, HOVER_BG);
                tooltipName = entry.name;
                tooltipSub = entry.subtitle;
            }

            ctx.drawItem(entry.stack, ix + 2, iy + 2);
        }

        ctx.disableScissor();

        // Scrollbar
        if (contentH > viewH) {
            int barX = panelX + panelW - 6;
            float ratio = (float) viewH / contentH;
            int thumbH = Math.max(15, (int) (viewH * ratio));
            int maxScroll = contentH - viewH;
            int thumbY = gridTop + (maxScroll > 0 ? (int) (scrollOffset / maxScroll * (viewH - thumbH)) : 0);
            ctx.fill(barX, gridTop, barX + 3, gridBottom, 0x20FFFFFF);
            ctx.fill(barX, thumbY, barX + 3, thumbY + thumbH, ACCENT);
        }

        // Tooltip
        if (tooltipName != null && qtyField == null) {
            int tw = Math.max(this.textRenderer.getWidth(tooltipName), this.textRenderer.getWidth(tooltipSub)) + 8;
            int tx = Math.min(mouseX + 12, this.width - tw - 4);
            int ty = mouseY - 24;
            ctx.fill(tx - 2, ty - 2, tx + tw + 2, ty + 22, 0xE0101020);
            ctx.fill(tx - 2, ty - 2, tx + tw + 2, ty - 1, ACCENT);
            ctx.drawTextWithShadow(this.textRenderer, Text.literal(tooltipName), tx + 2, ty, TEXT_PRIMARY);
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a78" + tooltipSub), tx + 2, ty + 11, TEXT_DIM);
        }

        // Quantity modal
        if (qtyField != null && selectedItem != null) {
            renderQtyModal(ctx, mouseX, mouseY, delta);
        }

        // Toast notification
        if (toastTimer > 0) {
            toastTimer--;
            int tw = this.textRenderer.getWidth(toastMessage) + 16;
            int tx = this.width / 2 - tw / 2;
            int ty = this.height - 50;
            float alpha = Math.min(1f, toastTimer / 10f);
            int a = (int) (alpha * 240);
            ctx.fill(tx, ty, tx + tw, ty + 18, (a << 24) | 0x101020);
            ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(toastMessage), this.width / 2, ty + 5, TEXT_PRIMARY);
        }
    }

    private void renderQtyModal(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0xC0000000);
        int boxW = 240;
        int boxH = 110;
        int boxX = this.width / 2 - boxW / 2;
        int boxY = this.height / 2 - boxH / 2;

        ctx.fill(boxX - 1, boxY - 1, boxX + boxW + 1, boxY + boxH + 1, ACCENT);
        ctx.fill(boxX, boxY, boxX + boxW, boxY + boxH, 0xFF1A1A2E);

        // Item preview
        ctx.drawItem(selectedItem.stack, boxX + 10, boxY + 8);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a7f\u00a7l" + selectedItem.name), boxX + 32, boxY + 12, TEXT_PRIMARY);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a78" + selectedItem.subtitle), boxX + 32, boxY + 23, TEXT_DIM);

        // Quantity label
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a77Quantity:"), boxX + 10, boxY + 42, TEXT_DIM);
        qtyField.setX(boxX + 70);
        qtyField.setY(boxY + 38);
        qtyField.render(ctx, mouseX, mouseY, delta);

        // Quick quantity buttons
        int btnY = boxY + 62;
        String[] qtys = {"1", "16", "32", "64"};
        for (int i = 0; i < qtys.length; i++) {
            int bx = boxX + 10 + i * 55;
            boolean hover = mouseX >= bx && mouseX < bx + 50 && mouseY >= btnY && mouseY < btnY + 16;
            ctx.fill(bx, btnY, bx + 50, btnY + 16, hover ? HOVER_BG : 0xFF222240);
            ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal((hover ? "\u00a7b" : "\u00a77") + qtys[i]),
                    bx + 25, btnY + 4, TEXT_PRIMARY);
        }

        // Give / Cancel buttons
        int confirmY = boxY + boxH - 22;
        boolean giveHover = mouseX >= boxX + 30 && mouseX < boxX + 100 && mouseY >= confirmY && mouseY < confirmY + 16;
        boolean cancelHover = mouseX >= boxX + 140 && mouseX < boxX + 210 && mouseY >= confirmY && mouseY < confirmY + 16;
        ctx.fill(boxX + 30, confirmY, boxX + 100, confirmY + 16, giveHover ? 0xFF1A4A1A : 0xFF1A2A1A);
        ctx.fill(boxX + 140, confirmY, boxX + 210, confirmY + 16, cancelHover ? 0xFF4A1A1A : 0xFF2A1A1A);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(giveHover ? "\u00a7a\u00a7l[Give]" : "\u00a7a[Give]"), boxX + 65, confirmY + 4, GREEN);
        ctx.drawCenteredTextWithShadow(this.textRenderer, Text.literal(cancelHover ? "\u00a7c\u00a7l[Cancel]" : "\u00a7c[Cancel]"), boxX + 175, confirmY + 4, RED);
    }

    @Override
    public boolean mouseClicked(Click click, boolean bl) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (button != 0) return false;

        // Quantity modal
        if (qtyField != null && selectedItem != null) {
            if (qtyField.isMouseOver(mouseX, mouseY)) {
                qtyField.mouseClicked(click, bl);
                setFocused(qtyField);
                return true;
            }

            int boxW = 240;
            int boxH = 110;
            int boxX = this.width / 2 - boxW / 2;
            int boxY = this.height / 2 - boxH / 2;

            // Quick quantity buttons
            int btnY = boxY + 62;
            String[] qtys = {"1", "16", "32", "64"};
            for (int i = 0; i < qtys.length; i++) {
                int bx = boxX + 10 + i * 55;
                if (mouseX >= bx && mouseX < bx + 50 && mouseY >= btnY && mouseY < btnY + 16) {
                    qtyField.setText(qtys[i]);
                    return true;
                }
            }

            // Give button
            int confirmY = boxY + boxH - 22;
            if (mouseX >= boxX + 30 && mouseX < boxX + 100 && mouseY >= confirmY && mouseY < confirmY + 16) {
                giveSelectedItem();
                return true;
            }
            // Cancel button
            if (mouseX >= boxX + 140 && mouseX < boxX + 210 && mouseY >= confirmY && mouseY < confirmY + 16) {
                closeQtyModal();
                return true;
            }
            return true;
        }

        int panelW = Math.min(400, this.width - 40);
        int panelX = (this.width - panelW) / 2;
        int panelTop = 16;
        int panelBottom = this.height - 16;

        // Close button
        if (mouseX >= panelX + panelW - 18 && mouseX < panelX + panelW && mouseY >= panelTop + 2 && mouseY < panelTop + 22) {
            close();
            return true;
        }

        // Search field
        if (searchField.isMouseOver(mouseX, mouseY)) {
            searchField.mouseClicked(click, bl);
            setFocused(searchField);
            return true;
        }

        // Grid clicks
        int gridTop = panelTop + 48;
        int gridBottom = panelBottom - 4;
        int gridW = panelW - 16;
        int cols = Math.max(1, gridW / CELL);

        if (mouseY >= gridTop && mouseY < gridBottom) {
            for (int i = 0; i < filteredItems.size(); i++) {
                int col = i % cols;
                int row = i / cols;
                int ix = panelX + 8 + col * CELL;
                int iy = gridTop + row * CELL - (int) scrollOffset;

                if (mouseX >= ix && mouseX < ix + ITEM_SIZE && mouseY >= iy && mouseY < iy + ITEM_SIZE) {
                    openQtyModal(filteredItems.get(i));
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (qtyField != null) return true;

        int panelW = Math.min(400, this.width - 40);
        int panelTop = 16 + 48;
        int panelBottom = this.height - 16 - 4;
        int gridW = panelW - 16;
        int cols = Math.max(1, gridW / CELL);
        int rows = (filteredItems.size() + cols - 1) / cols;
        int contentH = rows * CELL;
        int viewH = panelBottom - panelTop;
        int maxScroll = Math.max(0, contentH - viewH);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - verticalAmount * CELL * 2));
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        int keyCode = keyInput.key();

        if (qtyField != null) {
            if (keyCode == 257) {
                giveSelectedItem();
                return true;
            }
            if (keyCode == 256) {
                closeQtyModal();
                return true;
            }
            return qtyField.keyPressed(keyInput);
        }

        if (keyCode == 256) {
            close();
            return true;
        }

        return searchField.keyPressed(keyInput);
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        if (qtyField != null) return qtyField.charTyped(charInput);
        return searchField.charTyped(charInput);
    }

    private void openQtyModal(ItemEntry entry) {
        selectedItem = entry;
        qtyField = new TextFieldWidget(this.textRenderer, 0, 0, 100, 16, Text.literal("Qty"));
        qtyField.setText("64");
        qtyField.setMaxLength(4);
        qtyField.setEditable(true);
        setFocused(qtyField);
    }

    private void closeQtyModal() {
        qtyField = null;
        selectedItem = null;
        setFocused(searchField);
    }

    private void giveSelectedItem() {
        if (selectedItem == null || qtyField == null) return;

        int qty;
        try {
            qty = Integer.parseInt(qtyField.getText().trim());
            qty = Math.max(1, Math.min(6400, qty));
        } catch (NumberFormatException e) {
            qty = 1;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getNetworkHandler() == null) return;

        String itemId = selectedItem.id.toString();

        // 1. Try server addon payload (supports custom qty)
        if (net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.canSend(ItemGivePayload.ID)) {
            try {
                net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
                        new ItemGivePayload(itemId, qty));
                toastMessage = "\u00a7aGave \u00a7f" + qty + "x " + selectedItem.name;
                toastTimer = 40;
                closeQtyModal();
                return;
            } catch (Exception ignored) {}
        }

        // 2. Use datapack trigger (gives 1 item per trigger)
        int code = 0;
        if (selectedItem.triggerCode > 0) {
            code = selectedItem.triggerCode;
        } else {
            Integer c = getTriggerCodes().get(itemId);
            if (c != null) code = c;
        }

        if (code > 0) {
            client.getNetworkHandler().sendChatCommand("trigger f1sch.give set " + code);
            toastMessage = "\u00a7aGave " + selectedItem.name;
            toastTimer = 40;
        } else {
            toastMessage = "\u00a7cItem not available via trigger";
            toastTimer = 40;
        }

        closeQtyModal();
    }

    @Override
    public void close() {
        if (qtyField != null) {
            closeQtyModal();
            return;
        }
        if (this.client != null) this.client.setScreen(parent);
    }

    private static class ItemEntry {
        final Item item;
        final Identifier id;
        final ItemStack stack;
        final String name;
        final int triggerCode; // 0 = compute from registry, >0 = hardcoded
        final String subtitle;

        ItemEntry(Item item, Identifier id, ItemStack stack, String name, int triggerCode, String subtitle) {
            this.item = item;
            this.id = id;
            this.stack = stack;
            this.name = name;
            this.triggerCode = triggerCode;
            this.subtitle = subtitle;
        }
    }
}
