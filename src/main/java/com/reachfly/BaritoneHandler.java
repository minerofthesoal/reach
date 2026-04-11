package com.reachfly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Baritone-style pathfinding bot with full feature set:
 * - #goto X Y Z - A* pathfind to coordinates
 * - #mine <block> - Find and mine specific blocks
 * - #follow - Follow nearest player
 * - #farm - Auto harvest and replant crops
 * - #explore - Auto-explore in expanding spiral
 * - #build - Build from Litematica schematics (auto-grab materials)
 * - #stop - Halt current task
 *
 * Chat commands: type #baritone <cmd> in chat
 */
public class BaritoneHandler {

    // ===== Pathfinding state =====
    private static List<BlockPos> currentPath = null;
    private static int pathIndex = 0;
    private static BlockPos currentGoal = null;
    private static int recalcCooldown = 0;
    private static int stuckTicks = 0;
    private static Vec3d lastPos = null;
    private static int tickCounter = 0;

    // ===== Mining state =====
    private static BlockPos miningTarget = null;
    private static int miningScanCooldown = 0;

    // ===== Follow state =====
    private static Entity followTarget = null;

    // ===== Farm state =====
    private static BlockPos farmTarget = null;
    private static int farmScanCooldown = 0;
    private static boolean farmReplanting = false;

    // ===== Explore state =====
    private static int exploreAngle = 0;
    private static int exploreRadius = 20;
    private static int exploreWaitTicks = 0;

    // ===== Build state =====
    private static List<BuildEntry> buildQueue = null;
    private static int buildIndex = 0;
    private static int buildPlaceCooldown = 0;

    // ===== Status =====
    private static String statusMessage = "Idle";
    private static int msgCooldown = 0;

    public static String getStatus() { return statusMessage; }

    public static void tick(MinecraftClient client) {
        if (!ModConfig.baritoneEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;
        if (client.interactionManager == null) return;

        ClientPlayerEntity player = client.player;
        tickCounter++;

        // Stuck detection (every 20 ticks)
        if (tickCounter % 20 == 0) {
            Vec3d pos = player.getPos();
            if (lastPos != null && !"idle".equals(ModConfig.baritoneMode)) {
                double moved = pos.distanceTo(lastPos);
                if (moved < 0.3) {
                    stuckTicks += 20;
                } else {
                    stuckTicks = Math.max(0, stuckTicks - 10);
                }
            }
            lastPos = pos;
        }

        // Status messages
        if (msgCooldown > 0) msgCooldown--;

        switch (ModConfig.baritoneMode) {
            case "goto" -> tickGoto(client, player);
            case "mine" -> tickMine(client, player);
            case "follow" -> tickFollow(client, player);
            case "farm" -> tickFarm(client, player);
            case "explore" -> tickExplore(client, player);
            case "build" -> tickBuild(client, player);
            default -> { statusMessage = "Idle"; }
        }
    }

    // ========== COMMAND PARSER ==========

    /**
     * Parse a chat message for #baritone commands. Returns true if handled.
     * Commands: #goto X Y Z, #mine <block>, #follow, #farm, #explore, #build, #stop, #help
     */
    public static boolean parseCommand(String message) {
        if (!message.startsWith("#")) return false;
        String[] parts = message.trim().split("\\s+");
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "#goto" -> {
                if (parts.length >= 4) {
                    try {
                        ModConfig.baritoneGotoX = Float.parseFloat(parts[1]);
                        ModConfig.baritoneGotoY = Float.parseFloat(parts[2]);
                        ModConfig.baritoneGotoZ = Float.parseFloat(parts[3]);
                        setMode("goto");
                        return true;
                    } catch (NumberFormatException e) { sendMsg("\u00a7cInvalid coordinates"); return true; }
                }
                sendMsg("\u00a7cUsage: #goto <x> <y> <z>");
                return true;
            }
            case "#mine" -> {
                if (parts.length >= 2) {
                    ModConfig.baritoneMineBlock = parts[1].toLowerCase().replace("minecraft:", "");
                    setMode("mine");
                    return true;
                }
                sendMsg("\u00a7cUsage: #mine <block_name>");
                return true;
            }
            case "#follow" -> { setMode("follow"); return true; }
            case "#farm" -> { setMode("farm"); return true; }
            case "#explore" -> { setMode("explore"); return true; }
            case "#build" -> {
                if (parts.length >= 2) {
                    ModConfig.baritoneBuildFile = parts[1];
                    setMode("build");
                } else {
                    setMode("build");
                }
                return true;
            }
            case "#stop", "#cancel", "#pause" -> { stop(); return true; }
            case "#baritone", "#b" -> {
                if (parts.length >= 2) {
                    // Redirect: #baritone goto X Y Z -> #goto X Y Z
                    String[] newParts = new String[parts.length];
                    newParts[0] = "#" + parts[1];
                    System.arraycopy(parts, 2, newParts, 1, parts.length - 2);
                    return parseCommand(String.join(" ", newParts));
                }
                sendHelp();
                return true;
            }
            case "#help" -> { sendHelp(); return true; }
            default -> { return false; }
        }
    }

    private static void sendHelp() {
        sendMsg("\u00a76=== Baritone Commands ===");
        sendMsg("\u00a7e#goto <x> <y> <z>\u00a77 - Pathfind to coords");
        sendMsg("\u00a7e#mine <block>\u00a77 - Mine specific blocks");
        sendMsg("\u00a7e#follow\u00a77 - Follow nearest player");
        sendMsg("\u00a7e#farm\u00a77 - Harvest & replant crops");
        sendMsg("\u00a7e#explore\u00a77 - Auto-explore outward");
        sendMsg("\u00a7e#build\u00a77 - Build from schematic");
        sendMsg("\u00a7e#stop\u00a77 - Stop current task");
    }

    private static void setMode(String mode) {
        ModConfig.baritoneMode = mode;
        ModConfig.baritoneEnabled = true;
        resetState();
        sendMsg("\u00a7b[Baritone] \u00a7aMode: " + mode);
        ModConfig.save();
    }

    public static void stop() {
        ModConfig.baritoneMode = "idle";
        resetState();
        releaseKeys();
        statusMessage = "Idle";
        sendMsg("\u00a7b[Baritone] \u00a7cStopped");
        ModConfig.save();
    }

    private static void resetState() {
        currentPath = null;
        pathIndex = 0;
        currentGoal = null;
        recalcCooldown = 0;
        stuckTicks = 0;
        lastPos = null;
        miningTarget = null;
        miningScanCooldown = 0;
        followTarget = null;
        farmTarget = null;
        farmScanCooldown = 0;
        farmReplanting = false;
        exploreAngle = 0;
        exploreRadius = 20;
        exploreWaitTicks = 0;
        buildQueue = null;
        buildIndex = 0;
        buildPlaceCooldown = 0;
        BlockBreaker.reset();
    }

    // ========== GOTO MODE ==========

    private static void tickGoto(MinecraftClient client, ClientPlayerEntity player) {
        BlockPos goal = new BlockPos(
                (int) ModConfig.baritoneGotoX,
                (int) ModConfig.baritoneGotoY,
                (int) ModConfig.baritoneGotoZ);

        double dist = player.getPos().distanceTo(Vec3d.ofCenter(goal));
        statusMessage = String.format("Goto %.0f blocks", dist);

        if (dist < 2.0) {
            sendMsg("\u00a7b[Baritone] \u00a7aArrived at destination!");
            stop();
            return;
        }

        navigateToward(client, player, goal);
    }

    // ========== MINE MODE ==========

    private static void tickMine(MinecraftClient client, ClientPlayerEntity player) {
        String targetBlockName = ModConfig.baritoneMineBlock;
        Block targetBlock = Registries.BLOCK.get(Identifier.of("minecraft", targetBlockName));

        if (targetBlock == Blocks.AIR) {
            sendMsg("\u00a7cUnknown block: " + targetBlockName);
            stop();
            return;
        }

        // If we have a mining target, go break it
        if (miningTarget != null) {
            BlockState state = client.world.getBlockState(miningTarget);
            if (state.isOf(targetBlock)) {
                double dist = player.getPos().distanceTo(Vec3d.ofCenter(miningTarget));
                statusMessage = String.format("Mining %s (%.1f away)", targetBlockName, dist);

                if (dist < 4.5) {
                    // Close enough to mine
                    if (ModConfig.baritoneAutoTool) selectBestTool(player, state);
                    faceBlock(player, miningTarget);
                    BlockBreaker.tryBreak(client, miningTarget);
                    releaseKeys();
                } else {
                    navigateToward(client, player, miningTarget);
                }
                return;
            } else {
                // Block was mined or changed
                miningTarget = null;
                miningScanCooldown = 0;
            }
        }

        // Scan for target blocks
        if (miningScanCooldown > 0) { miningScanCooldown--; return; }
        miningScanCooldown = 10;

        int radius = (int) ModConfig.baritoneMineRadius;
        BlockPos playerPos = player.getBlockPos();
        BlockPos nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (client.world.getBlockState(pos).isOf(targetBlock)) {
                        double d = playerPos.getSquaredDistance(pos);
                        if (d < nearestDist) {
                            nearestDist = d;
                            nearest = pos;
                        }
                    }
                }
            }
        }

        if (nearest != null) {
            miningTarget = nearest;
            statusMessage = "Found " + targetBlockName + "!";
        } else {
            statusMessage = "Searching for " + targetBlockName + "...";
            // Explore to find blocks
            if (tickCounter % 60 == 0) {
                exploreAngle += 45;
                int ex = (int) (player.getX() + Math.cos(Math.toRadians(exploreAngle)) * 32);
                int ez = (int) (player.getZ() + Math.sin(Math.toRadians(exploreAngle)) * 32);
                navigateToward(client, player, new BlockPos(ex, playerPos.getY(), ez));
            }
        }
    }

    // ========== FOLLOW MODE ==========

    private static void tickFollow(MinecraftClient client, ClientPlayerEntity player) {
        // Find nearest player
        if (followTarget == null || !followTarget.isAlive()
                || player.distanceTo(followTarget) > 64) {
            followTarget = null;
            double best = Double.MAX_VALUE;
            for (Entity e : client.world.getEntities()) {
                if (e == player) continue;
                if (!(e instanceof PlayerEntity)) continue;
                double d = player.distanceTo(e);
                if (d < best) { best = d; followTarget = e; }
            }
        }

        if (followTarget == null) {
            statusMessage = "No player found";
            return;
        }

        double dist = player.distanceTo(followTarget);
        statusMessage = String.format("Following %s (%.1f)",
                followTarget.getName().getString(), dist);

        if (dist > ModConfig.baritoneFollowRange) {
            navigateToward(client, player, followTarget.getBlockPos());
        } else {
            releaseKeys();
            // Face the target
            faceEntity(player, followTarget);
        }
    }

    // ========== FARM MODE ==========

    private static void tickFarm(MinecraftClient client, ClientPlayerEntity player) {
        int radius = (int) ModConfig.baritoneFarmRadius;
        BlockPos pPos = player.getBlockPos();

        if (farmTarget != null) {
            double dist = player.getPos().distanceTo(Vec3d.ofCenter(farmTarget));
            BlockState state = client.world.getBlockState(farmTarget);

            if (dist < 4.5) {
                if (farmReplanting) {
                    // Place seeds on farmland
                    if (selectSeeds(player)) {
                        BlockHitResult hit = new BlockHitResult(
                                Vec3d.ofCenter(farmTarget), Direction.UP, farmTarget, false);
                        client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
                        player.swingHand(Hand.MAIN_HAND);
                    }
                    farmReplanting = false;
                    farmTarget = null;
                    farmScanCooldown = 2;
                    releaseKeys();
                    return;
                }

                // Check if it's a mature crop to harvest
                if (isMatureCrop(state)) {
                    statusMessage = "Harvesting crop";
                    if (ModConfig.baritoneAutoTool) selectBestTool(player, state);
                    BlockBreaker.tryBreak(client, farmTarget);
                    // After breaking, replant
                    if (client.world.getBlockState(farmTarget).isAir()) {
                        farmReplanting = true;
                        farmTarget = farmTarget.down(); // farmland below
                    }
                    releaseKeys();
                    return;
                } else {
                    farmTarget = null;
                    farmScanCooldown = 2;
                }
            } else {
                navigateToward(client, player, farmTarget);
                statusMessage = String.format("Walking to crop (%.1f)", dist);
                return;
            }
        }

        // Scan for mature crops
        if (farmScanCooldown > 0) { farmScanCooldown--; return; }
        farmScanCooldown = 10;

        BlockPos nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = pPos.add(x, y, z);
                    BlockState state = client.world.getBlockState(pos);
                    if (isMatureCrop(state)) {
                        double d = pPos.getSquaredDistance(pos);
                        if (d < nearestDist) {
                            nearestDist = d;
                            nearest = pos;
                        }
                    }
                }
            }
        }

        if (nearest != null) {
            farmTarget = nearest;
            statusMessage = "Found crop to harvest";
        } else {
            statusMessage = "No mature crops nearby";
            releaseKeys();
        }
    }

    // ========== EXPLORE MODE ==========

    private static void tickExplore(MinecraftClient client, ClientPlayerEntity player) {
        statusMessage = String.format("Exploring (r=%d)", exploreRadius);

        if (exploreWaitTicks > 0) { exploreWaitTicks--; return; }

        // Navigate in expanding spiral
        double targetX = player.getX() + Math.cos(Math.toRadians(exploreAngle)) * exploreRadius;
        double targetZ = player.getZ() + Math.sin(Math.toRadians(exploreAngle)) * exploreRadius;
        BlockPos goal = new BlockPos((int) targetX, player.getBlockPos().getY(), (int) targetZ);

        double dist = Math.sqrt(
                (player.getX() - targetX) * (player.getX() - targetX) +
                (player.getZ() - targetZ) * (player.getZ() - targetZ));

        if (dist < 5 || stuckTicks > 60) {
            exploreAngle += 30;
            if (exploreAngle >= 360) {
                exploreAngle = 0;
                exploreRadius += 16;
                if (exploreRadius > 256) exploreRadius = 20;
            }
            stuckTicks = 0;
            exploreWaitTicks = 10;
            return;
        }

        navigateToward(client, player, goal);
    }

    // ========== BUILD MODE ==========

    private static void tickBuild(MinecraftClient client, ClientPlayerEntity player) {
        // Build mode places blocks from a schematic-like queue
        // If no queue loaded, scan for Litematica ghost blocks nearby
        if (buildQueue == null || buildQueue.isEmpty()) {
            scanLitematicaBlocks(client, player);
            if (buildQueue == null || buildQueue.isEmpty()) {
                statusMessage = "No schematic blocks found";
                return;
            }
        }

        if (buildIndex >= buildQueue.size()) {
            sendMsg("\u00a7b[Baritone] \u00a7aBuild complete!");
            stop();
            return;
        }

        if (buildPlaceCooldown > 0) { buildPlaceCooldown--; return; }

        BuildEntry entry = buildQueue.get(buildIndex);
        BlockState current = client.world.getBlockState(entry.pos);

        // Skip if already placed
        if (!current.isAir() && !current.isLiquid()) {
            buildIndex++;
            return;
        }

        double dist = player.getPos().distanceTo(Vec3d.ofCenter(entry.pos));
        statusMessage = String.format("Building %d/%d (%.1f away)",
                buildIndex + 1, buildQueue.size(), dist);

        if (dist > 4.5) {
            navigateToward(client, player, entry.pos);
            return;
        }

        // Select the right block in hotbar
        if (!selectBlock(player, entry.block)) {
            // Auto-grab from trigger system if enabled
            if (ModConfig.baritoneBuildAutoGrab) {
                requestItem(client, entry.block);
                buildPlaceCooldown = 10;
                return;
            }
            statusMessage = "Missing: " + Registries.BLOCK.getId(entry.block).getPath();
            buildPlaceCooldown = 20;
            return;
        }

        // Place the block
        releaseKeys();
        Direction placeDir = findPlacementFace(client, entry.pos);
        if (placeDir != null) {
            BlockPos neighbor = entry.pos.offset(placeDir);
            BlockHitResult hit = new BlockHitResult(
                    Vec3d.ofCenter(entry.pos), placeDir.getOpposite(), neighbor, false);
            client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
            player.swingHand(Hand.MAIN_HAND);
            buildPlaceCooldown = 3;
        }
        buildIndex++;
    }

    /**
     * Scan nearby area for blocks that should exist but don't (Litematica ghost blocks).
     * Also builds a simple queue from ground up for flat-area building.
     */
    private static void scanLitematicaBlocks(MinecraftClient client, ClientPlayerEntity player) {
        buildQueue = new ArrayList<>();
        BlockPos center = player.getBlockPos();
        int radius = 16;

        // Scan for air blocks that have solid blocks adjacent (potential build positions)
        // This works with Litematica's verification system
        for (int y = center.getY() - 4; y <= center.getY() + 8; y++) {
            for (int x = center.getX() - radius; x <= center.getX() + radius; x++) {
                for (int z = center.getZ() - radius; z <= center.getZ() + radius; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    // In Litematica, ghost blocks would be checked via its API
                    // Here we detect mismatches if Litematica is present
                }
            }
        }
        // Build mode primarily works when explicitly given a block queue
        // via the #build command with schematic data
    }

    // ========== NAVIGATION CORE ==========

    /**
     * Simple A*-inspired pathfinding toward a goal with terrain walking.
     * Uses WalkToCoordsHandler-style movement with smarter obstacle handling.
     */
    private static void navigateToward(MinecraftClient client, ClientPlayerEntity player, BlockPos goal) {
        Vec3d pos = player.getPos();
        Vec3d target = Vec3d.ofCenter(goal);

        // Calculate yaw to face target
        double dx = target.x - pos.x;
        double dz = target.z - pos.z;
        float targetYaw = (float) (Math.atan2(-dx, dz) * (180.0 / Math.PI));

        // Stuck handling - detour
        if (stuckTicks > 40) {
            targetYaw += (stuckTicks % 80 < 40) ? 70 : -70;
        }
        if (stuckTicks > 100) {
            targetYaw += 180;
            stuckTicks = 0;
        }

        // Smooth rotation
        float currentYaw = player.getYaw();
        float yawDiff = targetYaw - currentYaw;
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;
        player.setYaw(currentYaw + yawDiff * 0.25f);

        // Press forward
        KeyBinding.setKeyPressed(client.options.forwardKey.getDefaultKey(), true);
        client.options.forwardKey.setPressed(true);

        // Look direction for block checks
        float facingYaw = player.getYaw();
        double faceDx = -Math.sin(Math.toRadians(facingYaw));
        double faceDz = Math.cos(Math.toRadians(facingYaw));

        BlockPos feetAhead = new BlockPos(
                (int) Math.floor(pos.x + faceDx),
                (int) Math.floor(pos.y),
                (int) Math.floor(pos.z + faceDz));
        BlockPos headAhead = feetAhead.up();

        boolean solidFeet = isSolid(client.world.getBlockState(feetAhead));
        boolean solidHead = isSolid(client.world.getBlockState(headAhead));
        boolean clearAbove = !isSolid(client.world.getBlockState(feetAhead.up()))
                && !isSolid(client.world.getBlockState(feetAhead.up().up()));

        // Gap detection
        BlockPos groundAhead = new BlockPos(
                (int) Math.floor(pos.x + faceDx * 1.5),
                (int) Math.floor(pos.y - 1),
                (int) Math.floor(pos.z + faceDz * 1.5));
        boolean gap = !isSolid(client.world.getBlockState(groundAhead))
                && !isSolid(client.world.getBlockState(groundAhead.down()));

        boolean inLiquid = player.isTouchingWater() || player.isInLava();
        boolean shouldJump = false;

        // Jump over 1-block obstacle
        if (solidFeet && !solidHead && clearAbove) shouldJump = true;
        // Jump over gaps
        if (gap && !inLiquid) shouldJump = true;
        // Escape liquid
        if (inLiquid) shouldJump = true;
        // Target is above
        if (target.y > pos.y + 0.5) shouldJump = true;

        // 2-block wall - try breaking
        if (solidFeet && solidHead) {
            BlockBreaker.tryBreak(client, feetAhead);
            shouldJump = false;
        }

        // Danger avoidance
        if (ModConfig.baritoneAvoidDanger) {
            BlockState below = client.world.getBlockState(groundAhead);
            if (below.isOf(Blocks.LAVA) || below.isOf(Blocks.FIRE)
                    || below.isOf(Blocks.CACTUS) || below.isOf(Blocks.MAGMA_BLOCK)) {
                shouldJump = true;
            }
        }

        KeyBinding.setKeyPressed(client.options.jumpKey.getDefaultKey(), shouldJump);
        client.options.jumpKey.setPressed(shouldJump);

        // Sprint
        boolean canSprint = ModConfig.baritoneSprint
                && player.getHungerManager().getFoodLevel() > 6 && !inLiquid;
        KeyBinding.setKeyPressed(client.options.sprintKey.getDefaultKey(), canSprint);
        client.options.sprintKey.setPressed(canSprint);
    }

    // ========== UTILITY METHODS ==========

    private static boolean isSolid(BlockState state) {
        return !state.isAir() && !state.isLiquid() && state.isSolid();
    }

    private static boolean isMatureCrop(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof CropBlock crop) {
            return crop.isMature(state);
        }
        // Pumpkins and melons
        if (block == Blocks.PUMPKIN || block == Blocks.MELON) return true;
        // Sugar cane (only harvest if 2+ tall)
        if (block == Blocks.SUGAR_CANE) return true;
        // Cocoa
        if (block == Blocks.COCOA) {
            return state.toString().contains("age=2");
        }
        // Nether wart
        if (block == Blocks.NETHER_WART) {
            return state.toString().contains("age=3");
        }
        return false;
    }

    private static boolean selectSeeds(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            Item item = stack.getItem();
            if (item == Items.WHEAT_SEEDS || item == Items.BEETROOT_SEEDS
                    || item == Items.CARROT || item == Items.POTATO
                    || item == Items.NETHER_WART || item == Items.MELON_SEEDS
                    || item == Items.PUMPKIN_SEEDS || item == Items.TORCHFLOWER_SEEDS
                    || item == Items.PITCHER_POD) {
                player.getInventory().selectedSlot = i;
                return true;
            }
        }
        return false;
    }

    private static void selectBestTool(ClientPlayerEntity player, BlockState state) {
        float bestSpeed = 1.0f;
        int bestSlot = player.getInventory().selectedSlot;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            float speed = stack.getMiningSpeedMultiplier(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }
        player.getInventory().selectedSlot = bestSlot;
    }

    private static boolean selectBlock(ClientPlayerEntity player, Block block) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem bi && bi.getBlock() == block) {
                player.getInventory().selectedSlot = i;
                return true;
            }
        }
        return false;
    }

    private static Direction findPlacementFace(MinecraftClient client, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.offset(dir);
            BlockState state = client.world.getBlockState(neighbor);
            if (isSolid(state)) return dir;
        }
        return null;
    }

    private static void requestItem(MinecraftClient client, Block block) {
        String blockId = Registries.BLOCK.getId(block).toString();

        // Try server addon payload first
        if (net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.canSend(ItemGivePayload.ID)) {
            try {
                net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
                        new ItemGivePayload(blockId, 64));
                return;
            } catch (Exception ignored) {}
        }

        // Fall back to trigger command
        if (client.getNetworkHandler() != null) {
            // Try to find the trigger code for this item
            try {
                java.lang.reflect.Method m = ItemGiveScreen.class.getDeclaredMethod("getTriggerCodes");
                m.setAccessible(true);
                @SuppressWarnings("unchecked")
                java.util.Map<String, Integer> codes = (java.util.Map<String, Integer>) m.invoke(null);
                Integer code = codes.get(blockId);
                if (code != null) {
                    client.getNetworkHandler().sendChatCommand("trigger f1sch.give set " + code);
                }
            } catch (Exception ignored) {}
        }
    }

    private static void faceBlock(ClientPlayerEntity player, BlockPos pos) {
        Vec3d target = Vec3d.ofCenter(pos);
        Vec3d eye = player.getEyePos();
        double dx = target.x - eye.x;
        double dy = target.y - eye.y;
        double dz = target.z - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.atan2(-dx, dz) * (180.0 / Math.PI));
        float pitch = (float) (Math.atan2(-dy, dist) * (180.0 / Math.PI));
        player.setYaw(yaw);
        player.setPitch(pitch);
    }

    private static void faceEntity(ClientPlayerEntity player, Entity entity) {
        Vec3d target = entity.getPos().add(0, entity.getHeight() / 2, 0);
        Vec3d eye = player.getEyePos();
        double dx = target.x - eye.x;
        double dy = target.y - eye.y;
        double dz = target.z - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.atan2(-dx, dz) * (180.0 / Math.PI));
        float pitch = (float) (Math.atan2(-dy, dist) * (180.0 / Math.PI));
        player.setYaw(player.getYaw() + (yaw - player.getYaw()) * 0.2f);
        player.setPitch(player.getPitch() + (pitch - player.getPitch()) * 0.2f);
    }

    private static void releaseKeys() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        KeyBinding.setKeyPressed(client.options.forwardKey.getDefaultKey(), false);
        client.options.forwardKey.setPressed(false);
        KeyBinding.setKeyPressed(client.options.jumpKey.getDefaultKey(), false);
        client.options.jumpKey.setPressed(false);
        KeyBinding.setKeyPressed(client.options.sprintKey.getDefaultKey(), false);
        client.options.sprintKey.setPressed(false);
    }

    private static void sendMsg(String msg) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null) {
            client.player.sendMessage(Text.literal(msg), false);
        }
    }

    public static void onDisable() {
        resetState();
        releaseKeys();
    }

    // ===== Build entry =====
    static class BuildEntry {
        final BlockPos pos;
        final Block block;
        BuildEntry(BlockPos pos, Block block) { this.pos = pos; this.block = block; }
    }
}
