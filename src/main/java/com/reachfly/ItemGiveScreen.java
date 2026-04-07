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
    private static final int BG = 0xF0101020;
    private static final int PANEL_BG = 0xF0181828;
    private static final int ACCENT = 0xFF8B5CF6;
    private static final int ACCENT_DIM = 0xFF5B3CB6;
    private static final int TEXT_PRIMARY = 0xFFE0E0E0;
    private static final int TEXT_DIM = 0xFF888898;
    private static final int GREEN = 0xFF4ADE80;
    private static final int RED = 0xFFEF4444;
    private static final int GOLD = 0xFFFFD700;
    private static final int HOVER_BG = 0xFF282848;
    private static final int SELECTED_BG = 0xFF2A1F4E;

    // Trigger codes: computed lazily from sorted item registry.
    // Matches the alphabetically-sorted give_item.mcfunction in the datapack.
    private static Map<String, Integer> triggerCodes = null;

    private static Map<String, Integer> getTriggerCodes() {
        if (triggerCodes == null) {
            triggerCodes = new HashMap<>();
            List<String> ids = new ArrayList<>();
            for (Item item : Registries.ITEM) {
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
        // Build item list from registry
        allItems = new ArrayList<>();
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
            allItems.add(new ItemEntry(item, id, stack, name));
        }

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

    private void filterItems() {
        String query = lastQuery.toLowerCase(Locale.ROOT).trim();
        if (query.isEmpty()) {
            filteredItems = new ArrayList<>(allItems);
        } else {
            filteredItems = allItems.stream()
                    .filter(e -> e.name.toLowerCase(Locale.ROOT).contains(query)
                            || e.id.getPath().contains(query)
                            || e.id.toString().contains(query))
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
        String tooltipId = null;
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
                tooltipId = entry.id.toString();
            }

            // Render item icon
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
            int tw = Math.max(this.textRenderer.getWidth(tooltipName), this.textRenderer.getWidth(tooltipId)) + 8;
            int tx = Math.min(mouseX + 12, this.width - tw - 4);
            int ty = mouseY - 24;
            ctx.fill(tx - 2, ty - 2, tx + tw + 2, ty + 22, 0xE0101020);
            ctx.fill(tx - 2, ty - 2, tx + tw + 2, ty - 1, ACCENT);
            ctx.drawTextWithShadow(this.textRenderer, Text.literal(tooltipName), tx + 2, ty, TEXT_PRIMARY);
            ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a78" + tooltipId), tx + 2, ty + 11, TEXT_DIM);
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
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("\u00a78" + selectedItem.id), boxX + 32, boxY + 23, TEXT_DIM);

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
        boolean sent = false;

        // 1. Try server addon payload (any item, custom qty) - only if server has the addon
        if (net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.canSend(ItemGivePayload.ID)) {
            try {
                net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
                        new ItemGivePayload(itemId, qty));
                sent = true;
            } catch (Exception ignored) {}
        }

        // 2. Fallback: datapack trigger (200 mapped items, no OP needed)
        if (!sent) {
            Integer code = getTriggerCodes().get(itemId);
            if (code != null) {
                client.getNetworkHandler().sendChatCommand("trigger f1sch.give set " + code);
                sent = true;
            }
        }

        // 3. Last resort: /give chat command (requires OP)
        if (!sent) {
            client.getNetworkHandler().sendChatCommand("give @s " + itemId + " " + qty);
        }

        toastMessage = "\u00a7aGave " + selectedItem.name;
        toastTimer = 40;

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

        ItemEntry(Item item, Identifier id, ItemStack stack, String name) {
            this.item = item;
            this.id = id;
            this.stack = stack;
            this.name = name;
        }
    }
}
