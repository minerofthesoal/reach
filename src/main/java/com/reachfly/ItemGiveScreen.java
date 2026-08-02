package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    // Trigger codes parsed from the bundled mcfunction (single source of truth)
    private static Map<String, Integer> triggerCodes = null;

    private static Map<String, Integer> getTriggerCodes() {
        if (triggerCodes == null) {
            triggerCodes = new HashMap<>();
            // Parse the bundled give_item.mcfunction for item -> code mapping
            // Format: execute if entity @s[scores={f1sch.give=N}] run function f1sch:features/macros/give_item {item:"minecraft:xxx"}
            try (InputStream is = ItemGiveScreen.class.getResourceAsStream(
                    "/data/f1sch/function/features/give_item.mcfunction")) {
                if (is != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        int scoreIdx = line.indexOf("f1sch.give=");
                        if (scoreIdx < 0) continue;
                        scoreIdx += "f1sch.give=".length();
                        int scoreEnd = line.indexOf('}', scoreIdx);
                        if (scoreEnd < 0) continue;
                        // Match {item:"minecraft:xxx"} format
                        int itemIdx = line.indexOf("{item:\"");
                        if (itemIdx < 0) continue;
                        itemIdx += "{item:\"".length();
                        int itemEnd = line.indexOf('"', itemIdx);
                        if (itemEnd < 0) continue;
                        try {
                            int code = Integer.parseInt(line.substring(scoreIdx, scoreEnd));
                            String itemId = line.substring(itemIdx, itemEnd);
                            triggerCodes.put(itemId, code);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            } catch (Exception ignored) {}

            // Fallback: if mcfunction not found, compute from sorted registry
            if (triggerCodes.isEmpty()) {
                List<String> ids = new ArrayList<>();
                for (Item item : Registries.ITEM) {
                    ItemStack stack = new ItemStack(item);
                    if (stack.isEmpty()) continue;
                    ids.add(Registries.ITEM.getId(item).toString());
                }
                Collections.sort(ids);
                for (int i = 0; i < ids.size(); i++) {
                    triggerCodes.put(ids.get(i), i + 1);
                }
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

        // Build trigger code map first so we can assign codes to entries
        Map<String, Integer> codes = getTriggerCodes();

        // Regular items from registry, sorted alphabetically by ID (same order as mcfunction)
        List<ItemEntry> regularItems = new ArrayList<>();
        for (Item item : Registries.ITEM) {
            Identifier id = Registries.ITEM.getId(item);
            ItemStack stack = new ItemStack(item);
            if (stack.isEmpty()) continue;
            String displayName;
            try {
                displayName = stack.getName().getString();
            } catch (Exception e) {
                displayName = id.getPath();
            }
            // Format: "Display Name" with subtitle showing ID path
            String idPath = id.getPath();
            Integer triggerCode = codes.get(id.toString());
            regularItems.add(new ItemEntry(item, id, stack, displayName,
                    triggerCode != null ? triggerCode : 0, idPath));
        }
        // Sort alphabetically by full ID (matches mcfunction trigger code order)
        regularItems.sort((a, b) -> a.id.toString().compareTo(b.id.toString()));
        allItems.addAll(regularItems);

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
                    startCode + i, containerId.getPath() + " [" + effectId + "]"));
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
                    startCode + i, "enchanted_book [" + enchId + "]"));
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
        int tooltipCode = 0;
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
                tooltipCode = entry.triggerCode;
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
        if (tooltipName != null) {
            String codeLine = tooltipCode > 0 ? "\u00a7aTrigger: " + tooltipCode : "";
            int tw = Math.max(this.textRenderer.getWidth(tooltipName),
                    Math.max(this.textRenderer.getWidth(tooltipSub),
                            this.textRenderer.getWidth(codeLine))) + 8;
            int tx = Math.min(mouseX + 12, this.width - tw - 4);
            int lines = codeLine.isEmpty() ? 2 : 3;
            int th = lines * 11 + 2;
            int ty = mouseY - th - 4;
            ctx.fill(tx - 2, ty - 2, tx + tw + 2, ty + th, 0xE0101020);
            ctx.fill(tx - 2, ty - 2, tx + tw + 2, ty - 1, ACCENT);
            ctx.drawTextWithShadow(this.textRenderer, Text.literal(tooltipName), tx + 2, ty, TEXT_PRIMARY);
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a78" + tooltipSub), tx + 2, ty + 11, TEXT_DIM);
            if (!codeLine.isEmpty()) {
                ctx.drawTextWithShadow(this.textRenderer, Text.literal(codeLine), tx + 2, ty + 22, GREEN);
            }
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;

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
            searchField.mouseClicked(mouseX, mouseY, button);
            setFocused(searchField);
            return true;
        }

        // Grid clicks - give item directly
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
                    giveItem(filteredItems.get(i));
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            close();
            return true;
        }

        return searchField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return searchField.charTyped(chr, modifiers);
    }

    private void giveItem(ItemEntry entry) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getNetworkHandler() == null) return;

        String itemId = entry.id.toString();

        // 1. Try server addon payload (gives 64)
        if (net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.canSend(ItemGivePayload.ID)) {
            try {
                net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
                        new ItemGivePayload(itemId, 64));
                toastMessage = "\u00a7aGave \u00a7f64x " + entry.name;
                toastTimer = 40;
                return;
            } catch (Exception ignored) {}
        }

        // 2. Use datapack trigger (always gives 64)
        int code = entry.triggerCode;
        if (code <= 0) {
            Integer c = getTriggerCodes().get(itemId);
            if (c != null) code = c;
        }

        if (code > 0) {
            client.getNetworkHandler().sendChatCommand("trigger f1sch.give set " + code);
            toastMessage = "\u00a7aGave \u00a7f" + entry.name + " \u00a78(" + entry.subtitle + ")";
            toastTimer = 40;
        } else {
            toastMessage = "\u00a7cItem not available via trigger";
            toastTimer = 40;
        }
    }

    @Override
    public void close() {
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
