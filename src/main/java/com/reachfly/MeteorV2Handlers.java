package com.reachfly;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * Meteor Client-style features v2:
 * ElytraFly, Surround, CrystalAura, AnchorAura, HoleFiller, AutoTrap, Reversal
 */
public class MeteorV2Handlers {

    private static int surroundCooldown = 0;
    private static int crystalCooldown = 0;
    private static int anchorCooldown = 0;
    private static int holeFillCooldown = 0;
    private static int trapCooldown = 0;
    private static int reversalCooldown = 0;

    public static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        tickElytraFly(client);
        tickSurround(client);
        tickCrystalAura(client);
        tickAnchorAura(client);
        tickHoleFiller(client);
        tickAutoTrap(client);
        tickReversal(client);
    }

    /**
     * ElytraFly: Fly with elytra at configurable speed.
     */
    private static void tickElytraFly(MinecraftClient client) {
        if (!ModConfig.elytraFlyEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;

        if (!p.isGliding()) return;

        float yaw = (float) Math.toRadians(p.getYaw());
        float pitch = (float) Math.toRadians(p.getPitch());

        double speed = ModConfig.elytraFlySpeed * 0.05;

        double motionX = -Math.sin(yaw) * Math.cos(pitch) * speed;
        double motionY = -Math.sin(pitch) * speed;
        double motionZ = Math.cos(yaw) * Math.cos(pitch) * speed;

        p.setVelocity(motionX, motionY, motionZ);
        p.fallDistance = 0.0f;

        if (client.getNetworkHandler() != null) {
            client.getNetworkHandler().sendPacket(
                    new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full(
                            p.getX(), p.getY(), p.getZ(),
                            p.getYaw(), p.getPitch(),
                            false, false));
        }
    }

    /**
     * Surround: Places obsidian around feet for crystal PvP protection.
     */
    private static void tickSurround(MinecraftClient client) {
        if (!ModConfig.surroundEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.interactionManager == null) return;
        if (client.currentScreen != null) return;

        surroundCooldown++;
        if (surroundCooldown < 2) return;
        surroundCooldown = 0;

        if (!p.isOnGround()) return;

        BlockPos feet = p.getBlockPos();
        BlockPos[] positions = {
                feet.north(), feet.south(), feet.east(), feet.west(),
                feet.north().down(), feet.south().down(), feet.east().down(), feet.west().down()
        };

        int obsidianSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = p.getInventory().getStack(i);
            if (stack.isOf(Items.OBSIDIAN)) {
                obsidianSlot = i;
                break;
            }
        }
        if (obsidianSlot < 0) return;

        int prevSlot = p.getInventory().getSelectedSlot();
        p.getInventory().setSelectedSlot(obsidianSlot);

        for (BlockPos pos : positions) {
            BlockState state = client.world.getBlockState(pos);
            if (state.isAir() || state.isReplaceable()) {
                client.interactionManager.interactBlock(p,
                        Hand.MAIN_HAND,
                        new BlockHitResult(
                                Vec3d.ofCenter(pos),
                                Direction.UP,
                                pos,
                                false));
                p.getInventory().setSelectedSlot(prevSlot);
                return;
            }
        }

        p.getInventory().setSelectedSlot(prevSlot);
    }

    /**
     * CrystalAura: Automatically places and detonates end crystals for PvP.
     */
    private static void tickCrystalAura(MinecraftClient client) {
        if (!ModConfig.crystalAuraEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.interactionManager == null) return;
        if (client.currentScreen != null) return;

        crystalCooldown++;
        if (crystalCooldown < 4) return;
        crystalCooldown = 0;

        PlayerEntity target = null;
        double nearestDist = 6.0;
        for (Entity entity : client.world.getEntities()) {
            if (entity == p) continue;
            if (!(entity instanceof PlayerEntity other)) continue;
            if (!other.isAlive()) continue;
            double dist = p.distanceTo(other);
            if (dist < nearestDist) {
                nearestDist = dist;
                target = other;
            }
        }

        if (target == null) return;

        // Phase 1: Attack existing end crystals near the target
        for (Entity entity : client.world.getEntities()) {
            if (!(entity instanceof EndCrystalEntity crystal)) continue;
            if (p.distanceTo(crystal) > 6.0) continue;
            if (crystal.distanceTo(target) > 8.0) continue;

            client.interactionManager.attackEntity(p, crystal);
            p.swingHand(Hand.MAIN_HAND);
            return;
        }

        // Phase 2: Place end crystals on obsidian/bedrock near target
        int crystalSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = p.getInventory().getStack(i);
            if (stack.isOf(Items.END_CRYSTAL)) {
                crystalSlot = i;
                break;
            }
        }
        if (crystalSlot < 0) return;

        BlockPos targetPos = target.getBlockPos();
        int prevSlot = p.getInventory().getSelectedSlot();
        p.getInventory().setSelectedSlot(crystalSlot);

        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                for (int y = -1; y <= 2; y++) {
                    BlockPos pos = targetPos.add(x, y, z);
                    if (p.squaredDistanceTo(Vec3d.ofCenter(pos)) > 36) continue;

                    BlockState below = client.world.getBlockState(pos);
                    BlockState above = client.world.getBlockState(pos.up());
                    BlockState above2 = client.world.getBlockState(pos.up(2));

                    if ((below.isOf(Blocks.OBSIDIAN) || below.isOf(Blocks.BEDROCK))
                            && above.isAir() && above2.isAir()) {
                        client.interactionManager.interactBlock(p,
                                Hand.MAIN_HAND,
                                new BlockHitResult(
                                        Vec3d.ofCenter(pos).add(0, 0.5, 0),
                                        Direction.UP,
                                        pos,
                                        false));
                        p.getInventory().setSelectedSlot(prevSlot);
                        return;
                    }
                }
            }
        }

        p.getInventory().setSelectedSlot(prevSlot);
    }

    // ====== NEW METEOR FEATURES ======

    /**
     * AnchorAura: Uses respawn anchors for PvP in the overworld/end.
     * Places respawn anchors near enemies, charges them with glowstone,
     * then detonates them by right-clicking (they explode outside the Nether).
     */
    private static void tickAnchorAura(MinecraftClient client) {
        if (!ModConfig.anchorAuraEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.interactionManager == null) return;
        if (client.currentScreen != null) return;

        anchorCooldown++;
        if (anchorCooldown < 5) return;
        anchorCooldown = 0;

        // Find nearest enemy
        PlayerEntity target = null;
        double nearestDist = 5.0;
        for (Entity entity : client.world.getEntities()) {
            if (entity == p) continue;
            if (!(entity instanceof PlayerEntity other)) continue;
            if (!other.isAlive()) continue;
            double dist = p.distanceTo(other);
            if (dist < nearestDist) {
                nearestDist = dist;
                target = other;
            }
        }
        if (target == null) return;

        // Check for placed anchors near target - try to charge and detonate
        BlockPos targetFeet = target.getBlockPos();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = -1; y <= 2; y++) {
                    BlockPos pos = targetFeet.add(x, y, z);
                    if (p.squaredDistanceTo(Vec3d.ofCenter(pos)) > 25) continue;
                    BlockState state = client.world.getBlockState(pos);
                    if (state.isOf(Blocks.RESPAWN_ANCHOR)) {
                        // Found an anchor - charge it with glowstone or detonate
                        int glowstoneSlot = findHotbarItem(p, Items.GLOWSTONE);
                        if (glowstoneSlot >= 0) {
                            int prevSlot = p.getInventory().getSelectedSlot();
                            p.getInventory().setSelectedSlot(glowstoneSlot);
                            client.interactionManager.interactBlock(p, Hand.MAIN_HAND,
                                    new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false));
                            // Immediately try to detonate by clicking again without glowstone
                            p.getInventory().setSelectedSlot(prevSlot);
                            client.interactionManager.interactBlock(p, Hand.MAIN_HAND,
                                    new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false));
                        } else {
                            // No glowstone, try to detonate existing charge
                            client.interactionManager.interactBlock(p, Hand.MAIN_HAND,
                                    new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false));
                        }
                        return;
                    }
                }
            }
        }

        // Place new anchor near target
        int anchorSlot = findHotbarItem(p, Items.RESPAWN_ANCHOR);
        if (anchorSlot < 0) return;

        // Find valid placement position near target
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos pos = targetFeet.add(x, 0, z);
                if (p.squaredDistanceTo(Vec3d.ofCenter(pos)) > 25) continue;
                BlockState state = client.world.getBlockState(pos);
                BlockState below = client.world.getBlockState(pos.down());
                if (state.isAir() && !below.isAir()) {
                    int prevSlot = p.getInventory().getSelectedSlot();
                    p.getInventory().setSelectedSlot(anchorSlot);
                    client.interactionManager.interactBlock(p, Hand.MAIN_HAND,
                            new BlockHitResult(Vec3d.ofCenter(pos.down()).add(0, 0.5, 0),
                                    Direction.UP, pos.down(), false));
                    p.getInventory().setSelectedSlot(prevSlot);
                    return;
                }
            }
        }
    }

    /**
     * HoleFiller: Fills 1x1 holes in the ground near the player with obsidian.
     * In crystal PvP, holes (1x1 bedrock/obsidian pits) are safe spots.
     * This fills enemy holes to force them out into the open.
     */
    private static void tickHoleFiller(MinecraftClient client) {
        if (!ModConfig.holeFillerEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.interactionManager == null) return;
        if (client.currentScreen != null) return;

        holeFillCooldown++;
        if (holeFillCooldown < 3) return;
        holeFillCooldown = 0;

        int obsidianSlot = findHotbarItem(p, Items.OBSIDIAN);
        if (obsidianSlot < 0) return;

        BlockPos playerPos = p.getBlockPos();

        // Search for holes within range
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                BlockPos base = playerPos.add(x, -1, z);
                if (p.squaredDistanceTo(Vec3d.ofCenter(base)) > 20) continue;

                // A "hole" is: solid walls on all 4 sides, solid floor, air inside (1x1x2)
                BlockPos inside = base.up();
                BlockPos insideTop = base.up(2);

                if (!client.world.getBlockState(inside).isAir()) continue;
                if (!client.world.getBlockState(insideTop).isAir()) continue;

                // Check if surrounded by blast-resistant blocks
                boolean isHole = true;
                BlockPos[] walls = { inside.north(), inside.south(), inside.east(), inside.west() };
                for (BlockPos wall : walls) {
                    BlockState wallState = client.world.getBlockState(wall);
                    if (!wallState.isOf(Blocks.OBSIDIAN) && !wallState.isOf(Blocks.BEDROCK)
                            && !wallState.isOf(Blocks.CRYING_OBSIDIAN) && !wallState.isOf(Blocks.ENDER_CHEST)) {
                        isHole = false;
                        break;
                    }
                }

                BlockState floor = client.world.getBlockState(base);
                if (!floor.isOf(Blocks.OBSIDIAN) && !floor.isOf(Blocks.BEDROCK)) isHole = false;

                if (!isHole) continue;

                // Don't fill the hole the player is standing in
                if (Math.abs(x) <= 1 && Math.abs(z) <= 1) continue;

                // Fill the hole
                int prevSlot = p.getInventory().getSelectedSlot();
                p.getInventory().setSelectedSlot(obsidianSlot);
                client.interactionManager.interactBlock(p, Hand.MAIN_HAND,
                        new BlockHitResult(Vec3d.ofCenter(inside), Direction.UP, base, false));
                p.getInventory().setSelectedSlot(prevSlot);
                return;
            }
        }
    }

    /**
     * AutoTrap: Automatically places obsidian above and around enemy players' heads.
     * Traps them in a box so they can't move or escape crystal attacks.
     */
    private static void tickAutoTrap(MinecraftClient client) {
        if (!ModConfig.autoTrapEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.interactionManager == null) return;
        if (client.currentScreen != null) return;

        trapCooldown++;
        if (trapCooldown < 2) return;
        trapCooldown = 0;

        int obsidianSlot = findHotbarItem(p, Items.OBSIDIAN);
        if (obsidianSlot < 0) return;

        // Find nearest enemy
        PlayerEntity target = null;
        double nearestDist = 4.5;
        for (Entity entity : client.world.getEntities()) {
            if (entity == p) continue;
            if (!(entity instanceof PlayerEntity other)) continue;
            if (!other.isAlive()) continue;
            double dist = p.distanceTo(other);
            if (dist < nearestDist) {
                nearestDist = dist;
                target = other;
            }
        }
        if (target == null) return;

        BlockPos head = target.getBlockPos().up(2); // Above head

        // Trap positions: top of head, and 4 sides at head level
        BlockPos[] trapPositions = {
                head,                    // Top
                head.north(), head.south(), head.east(), head.west(), // Sides at head height
        };

        int prevSlot = p.getInventory().getSelectedSlot();
        p.getInventory().setSelectedSlot(obsidianSlot);

        for (BlockPos pos : trapPositions) {
            if (p.squaredDistanceTo(Vec3d.ofCenter(pos)) > 20) continue;
            BlockState state = client.world.getBlockState(pos);
            if (state.isAir() || state.isReplaceable()) {
                // Find a face to place against
                Direction placeDir = Direction.DOWN;
                BlockPos placeAgainst = pos.up();
                for (Direction dir : Direction.values()) {
                    BlockPos neighbor = pos.offset(dir);
                    if (!client.world.getBlockState(neighbor).isAir()) {
                        placeDir = dir.getOpposite();
                        placeAgainst = neighbor;
                        break;
                    }
                }

                client.interactionManager.interactBlock(p, Hand.MAIN_HAND,
                        new BlockHitResult(Vec3d.ofCenter(placeAgainst),
                                placeDir, placeAgainst, false));
                p.getInventory().setSelectedSlot(prevSlot);
                return; // One block per tick
            }
        }

        p.getInventory().setSelectedSlot(prevSlot);
    }

    /**
     * Reversal: When hit, automatically counterattacks the attacker with maximum force.
     * Detects incoming damage and immediately swings at the attacker.
     * Also applies a speed boost toward the attacker for aggressive counter-play.
     */
    private static void tickReversal(MinecraftClient client) {
        if (!ModConfig.reversalEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.interactionManager == null) return;

        reversalCooldown++;
        if (reversalCooldown < 2) return;

        // Detect if player was recently hit (hurtTime > 0 means damage was taken this tick)
        if (p.hurtTime == p.maxHurtTime && p.hurtTime > 0) {
            reversalCooldown = 0;

            // Find the attacker - nearest entity in range that could have attacked
            Entity attacker = null;
            double nearestDist = 6.0;
            for (Entity entity : client.world.getEntities()) {
                if (entity == p) continue;
                if (!(entity instanceof LivingEntity living)) continue;
                if (!living.isAlive()) continue;
                double dist = p.distanceTo(entity);
                if (dist < nearestDist) {
                    nearestDist = dist;
                    attacker = entity;
                }
            }

            if (attacker != null && nearestDist <= 4.5) {
                // Counter-attack
                client.interactionManager.attackEntity(p, attacker);
                p.swingHand(Hand.MAIN_HAND);

                // Boost toward attacker
                Vec3d dir = attacker.getPos().subtract(p.getPos()).normalize();
                p.setVelocity(dir.x * 0.4, 0.1, dir.z * 0.4);
            }
        }
    }

    private static int findHotbarItem(ClientPlayerEntity p, net.minecraft.item.Item item) {
        for (int i = 0; i < 9; i++) {
            if (p.getInventory().getStack(i).isOf(item)) return i;
        }
        return -1;
    }
}
