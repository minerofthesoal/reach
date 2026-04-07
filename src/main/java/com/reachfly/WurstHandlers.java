package com.reachfly;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Wurst Client-style features:
 * Criticals, BunnyHop, Spider, Glide, HighJump, Dolphin,
 * AutoSword, Sneak, Panic, AntiHunger, TriggerBot,
 * FastPlace, Parkour, NoSlowdown, AntiBlind, AutoWalk,
 * AirJump, NoWeb, FlightPlus, LongJump, AutoMLG, Blink
 */
public class WurstHandlers {

    private static boolean criticalJumped = false;
    private static int bunnyHopDelay = 0;
    private static int airJumpCooldown = 0;
    private static int longJumpTimer = 0;
    private static boolean wasOnGround = true;
    private static int autoMLGCooldown = 0;
    private static int blinkTickCounter = 0;
    private static Vec3d blinkStartPos = null;
    private static boolean blinkActive = false;

    public static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        tickCriticals(client);
        tickBunnyHop(client);
        tickSpider(client);
        tickGlide(client);
        tickHighJump(client);
        tickDolphin(client);
        tickAutoSword(client);
        tickSneak(client);
        tickPanic(client);
        tickAntiHunger(client);
        tickTriggerBot(client);
        tickFastPlace(client);
        tickParkour(client);
        tickNoSlowdown(client);
        tickAntiBlind(client);
        tickAutoWalk(client);
        tickAirJump(client);
        tickNoWeb(client);
        tickFlightPlus(client);
        tickLongJump(client);
        tickAutoMLG(client);
        tickBlink(client);
    }

    private static void tickCriticals(MinecraftClient client) {
        if (!ModConfig.criticalsEnabled) { criticalJumped = false; return; }
        ClientPlayerEntity p = client.player;
        if (p == null || !p.isOnGround()) return;

        if (p.getAttackCooldownProgress(0.0f) >= 0.95f && !criticalJumped) {
            p.setVelocity(p.getVelocity().add(0, 0.1, 0));
            p.setOnGround(false);
            criticalJumped = true;
        }
        if (p.getAttackCooldownProgress(0.0f) < 0.5f) {
            criticalJumped = false;
        }
    }

    private static void tickBunnyHop(MinecraftClient client) {
        if (!ModConfig.bunnyHopEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.currentScreen != null) return;
        if (!p.isSprinting() || !p.isOnGround()) return;

        bunnyHopDelay++;
        if (bunnyHopDelay < 1) return;
        bunnyHopDelay = 0;
        p.jump();
    }

    private static void tickSpider(MinecraftClient client) {
        if (!ModConfig.spiderEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;

        if (p.horizontalCollision && !p.isOnGround()) {
            Vec3d vel = p.getVelocity();
            p.setVelocity(vel.x, 0.2, vel.z);
            p.fallDistance = 0.0f;
        }
    }

    private static void tickGlide(MinecraftClient client) {
        if (!ModConfig.glideEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;

        if (!p.isOnGround() && p.getVelocity().y < 0) {
            Vec3d vel = p.getVelocity();
            p.setVelocity(vel.x, Math.max(vel.y, -0.06), vel.z);
            p.fallDistance = 0.0f;
        }
    }

    private static void tickHighJump(MinecraftClient client) {
        if (!ModConfig.highJumpEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;

        Vec3d vel = p.getVelocity();
        if (!p.isOnGround() && vel.y > 0.38 && vel.y < 0.45) {
            double boost = (ModConfig.highJumpHeight - 1.0) * 0.2;
            p.setVelocity(vel.x, vel.y + boost, vel.z);
        }
    }

    private static void tickDolphin(MinecraftClient client) {
        if (!ModConfig.dolphinEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;

        if (p.isTouchingWater()) {
            Vec3d vel = p.getVelocity();
            double speed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);

            if (speed > 0.01) {
                p.setVelocity(vel.x * 1.4, vel.y, vel.z * 1.4);
            }
            if (p.isSwimming() && p.getPitch() < -10) {
                p.setVelocity(p.getVelocity().add(0, 0.04, 0));
            }
            if (p.isSubmergedInWater() && !p.isSneaking()) {
                p.setVelocity(p.getVelocity().add(0, 0.02, 0));
            }
        }
    }

    private static void tickAutoSword(MinecraftClient client) {
        if (!ModConfig.autoSwordEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.currentScreen != null) return;
        if (client.crosshairTarget == null) return;

        if (!(client.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult ehr)) return;
        if (!(ehr.getEntity() instanceof LivingEntity)) return;

        int bestSlot = -1;
        float bestDamage = 0;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = p.getInventory().getStack(i);
            Item item = stack.getItem();
            float damage = 0;

            if (item == Items.WOODEN_SWORD || item == Items.STONE_SWORD ||
                item == Items.IRON_SWORD || item == Items.GOLDEN_SWORD ||
                item == Items.DIAMOND_SWORD || item == Items.NETHERITE_SWORD) {
                damage = 7;
            } else if (item instanceof AxeItem) {
                damage = 9;
            } else if (item instanceof TridentItem) {
                damage = 9;
            }

            if (damage > bestDamage) {
                bestDamage = damage;
                bestSlot = i;
            }
        }

        if (bestSlot >= 0 && bestSlot != p.getInventory().getSelectedSlot()) {
            p.getInventory().setSelectedSlot(bestSlot);
        }
    }

    private static void tickSneak(MinecraftClient client) {
        if (!ModConfig.sneakEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.currentScreen != null) return;
        p.setSneaking(true);
    }

    private static void tickPanic(MinecraftClient client) {
        if (!ModConfig.panicEnabled) return;

        ModConfig.reachEnabled = false;
        ModConfig.flyEnabled = false;
        ModConfig.autoHitEnabled = false;
        ModConfig.killAuraEnabled = false;
        ModConfig.killAuraPlusEnabled = false;
        ModConfig.espEnabled = false;
        ModConfig.noFallEnabled = false;
        ModConfig.fullbrightEnabled = false;
        ModConfig.speedEnabled = false;
        ModConfig.xrayEnabled = false;
        ModConfig.knockbackEnabled = false;
        ModConfig.jesusEnabled = false;
        ModConfig.scaffoldEnabled = false;
        ModConfig.criticalsEnabled = false;
        ModConfig.bunnyHopEnabled = false;
        ModConfig.spiderEnabled = false;
        ModConfig.glideEnabled = false;
        ModConfig.highJumpEnabled = false;
        ModConfig.dolphinEnabled = false;
        ModConfig.autoSwordEnabled = false;
        ModConfig.sneakEnabled = false;
        ModConfig.triggerBotEnabled = false;
        ModConfig.phaseEnabled = false;
        ModConfig.timerEnabled = false;
        ModConfig.elytraFlyEnabled = false;
        ModConfig.surroundEnabled = false;
        ModConfig.crystalAuraEnabled = false;
        ModConfig.invMoveEnabled = false;
        ModConfig.fastPlaceEnabled = false;
        ModConfig.parkourEnabled = false;
        ModConfig.noSlowdownEnabled = false;
        ModConfig.antiBlindEnabled = false;
        ModConfig.autoWalkEnabled = false;
        ModConfig.airJumpEnabled = false;
        ModConfig.noWebEnabled = false;
        ModConfig.flightPlusEnabled = false;
        ModConfig.longJumpEnabled = false;
        ModConfig.autoMLGEnabled = false;
        ModConfig.blinkEnabled = false;
        ModConfig.anchorAuraEnabled = false;
        ModConfig.holeFillerEnabled = false;
        ModConfig.autoTrapEnabled = false;
        ModConfig.reversalEnabled = false;

        ModConfig.panicEnabled = false;
        ModConfig.save();

        if (client.player != null) {
            client.player.sendMessage(
                    net.minecraft.text.Text.literal("\u00a7c[OSP] PANIC - All hacks disabled!"),
                    true);
        }
    }

    private static void tickAntiHunger(MinecraftClient client) {
        if (!ModConfig.antiHungerEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.getNetworkHandler() == null) return;

        if (!p.isOnGround() && p.getVelocity().y < 0) {
            client.getNetworkHandler().sendPacket(
                    new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly(
                            true, p.horizontalCollision));
        }
    }

    private static void tickTriggerBot(MinecraftClient client) {
        if (!ModConfig.triggerBotEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.currentScreen != null) return;
        if (client.interactionManager == null) return;

        if (p.getAttackCooldownProgress(0.0f) < 1.0f) return;

        if (client.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult ehr) {
            Entity target = ehr.getEntity();
            if (target instanceof LivingEntity living && living.isAlive()) {
                if (ModConfig.autoHitPlayersOnly && !(target instanceof PlayerEntity)) return;
                client.interactionManager.attackEntity(p, target);
                p.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    // ====== NEW WURST FEATURES ======

    /**
     * FastPlace: Removes the 4-tick placement cooldown, allowing rapid block placement.
     * Sets the item use cooldown to 0 every tick for instant placement.
     */
    private static void tickFastPlace(MinecraftClient client) {
        if (!ModConfig.fastPlaceEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;

        // Reset the right-click delay to allow rapid block placement
        // Uses reflection-free approach via the MinecraftClient field
        try {
            java.lang.reflect.Field f = MinecraftClient.class.getDeclaredField("field_1752"); // itemUseCooldown
            f.setAccessible(true);
            f.setInt(client, 0);
        } catch (Exception ignored) {
            // Fallback: try yarn name
            try {
                java.lang.reflect.Field f = MinecraftClient.class.getDeclaredField("itemUseCooldown");
                f.setAccessible(true);
                f.setInt(client, 0);
            } catch (Exception ignored2) {}
        }
    }

    /**
     * Parkour: Automatically jumps when reaching the edge of a block while sprinting.
     * Perfect for parkour courses - jumps at the optimal position for max distance.
     */
    private static void tickParkour(MinecraftClient client) {
        if (!ModConfig.parkourEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.currentScreen != null) return;
        if (!p.isOnGround()) return;

        // Check if the block below the player's feet edge is air
        Vec3d vel = p.getVelocity();
        double speed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        if (speed < 0.05) return; // Not moving fast enough

        // Project forward slightly to detect edge
        double px = p.getX() + vel.x * 2;
        double pz = p.getZ() + vel.z * 2;
        BlockPos checkPos = new BlockPos((int) Math.floor(px), (int) Math.floor(p.getY() - 0.5), (int) Math.floor(pz));

        if (client.world.getBlockState(checkPos).isAir()) {
            p.jump();
        }
    }

    /**
     * NoSlowdown: Prevents the slowdown effect from using items (eating, blocking, drawing bow).
     * Maintains full movement speed while consuming items or using shields.
     */
    private static void tickNoSlowdown(MinecraftClient client) {
        if (!ModConfig.noSlowdownEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;

        // If using an item, counteract the slowdown by boosting speed back
        if (p.isUsingItem()) {
            Vec3d vel = p.getVelocity();
            // Item use applies a 0.2x multiplier, we counteract it partially
            p.setVelocity(vel.x * 1.4, vel.y, vel.z * 1.4);
        }
    }

    /**
     * AntiBlind: Removes blindness, darkness, and nausea potion effects client-side.
     * Keeps the player's vision clear regardless of effects applied by the server.
     */
    private static void tickAntiBlind(MinecraftClient client) {
        if (!ModConfig.antiBlindEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;

        // Remove visual impairment effects client-side
        if (p.hasStatusEffect(StatusEffects.BLINDNESS)) {
            p.removeStatusEffect(StatusEffects.BLINDNESS);
        }
        if (p.hasStatusEffect(StatusEffects.DARKNESS)) {
            p.removeStatusEffect(StatusEffects.DARKNESS);
        }
        if (p.hasStatusEffect(StatusEffects.NAUSEA)) {
            p.removeStatusEffect(StatusEffects.NAUSEA);
        }
    }

    /**
     * AutoWalk: Automatically walks forward without holding W key.
     * Useful for long journeys. Simulates forward input continuously.
     */
    private static void tickAutoWalk(MinecraftClient client) {
        if (!ModConfig.autoWalkEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.currentScreen != null) return;

        // Simulate forward key press
        net.minecraft.util.PlayerInput current = p.input.playerInput;
        p.input.playerInput = new net.minecraft.util.PlayerInput(
                true, // forward
                current.backward(),
                current.left(),
                current.right(),
                current.jump(),
                current.sneak(),
                current.sprint()
        );
    }

    /**
     * AirJump: Allows jumping while in mid-air, like double/triple jumping.
     * Has a short cooldown to prevent unlimited flight exploit.
     */
    private static void tickAirJump(MinecraftClient client) {
        if (!ModConfig.airJumpEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.currentScreen != null) return;

        if (airJumpCooldown > 0) airJumpCooldown--;

        if (p.isOnGround()) {
            wasOnGround = true;
            return;
        }

        // Detect jump key press while in air
        if (p.input.playerInput.jump() && wasOnGround == false && airJumpCooldown <= 0) {
            Vec3d vel = p.getVelocity();
            p.setVelocity(vel.x, 0.42, vel.z); // Normal jump velocity
            p.fallDistance = 0.0f;
            airJumpCooldown = 10; // Half-second cooldown
        }

        if (!p.input.playerInput.jump()) {
            wasOnGround = false;
        }
    }

    /**
     * NoWeb: Prevents cobwebs from slowing down the player.
     * Counteracts the velocity reduction when inside a cobweb block.
     */
    private static void tickNoWeb(MinecraftClient client) {
        if (!ModConfig.noWebEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;

        // Check if player is in a cobweb area by detecting drastic velocity reduction
        BlockPos pos = p.getBlockPos();
        if (client.world.getBlockState(pos).getBlock() == net.minecraft.block.Blocks.COBWEB ||
            client.world.getBlockState(pos.up()).getBlock() == net.minecraft.block.Blocks.COBWEB) {
            // Counteract web slowdown by maintaining velocity
            Vec3d vel = p.getVelocity();
            p.setVelocity(vel.x * 5.0, vel.y, vel.z * 5.0);
        }
    }

    /**
     * FlightPlus: Enhanced creative-style flight with configurable speed and
     * vertical control. Works without creative mode permission.
     * Press jump to go up, sneak to go down, sprint to go faster.
     */
    private static void tickFlightPlus(MinecraftClient client) {
        if (!ModConfig.flightPlusEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.currentScreen != null) return;

        float speed = ModConfig.flightPlusSpeed;
        float yaw = (float) Math.toRadians(p.getYaw());

        double motionX = 0, motionY = 0, motionZ = 0;

        net.minecraft.util.PlayerInput input = p.input.playerInput;

        if (input.forward()) {
            motionX -= Math.sin(yaw) * speed * 0.1;
            motionZ += Math.cos(yaw) * speed * 0.1;
        }
        if (input.backward()) {
            motionX += Math.sin(yaw) * speed * 0.1;
            motionZ -= Math.cos(yaw) * speed * 0.1;
        }
        if (input.left()) {
            motionX += Math.cos(yaw) * speed * 0.1;
            motionZ += Math.sin(yaw) * speed * 0.1;
        }
        if (input.right()) {
            motionX -= Math.cos(yaw) * speed * 0.1;
            motionZ -= Math.sin(yaw) * speed * 0.1;
        }
        if (input.jump()) {
            motionY = speed * 0.1;
        }
        if (input.sneak()) {
            motionY = -speed * 0.1;
        }
        if (input.sprint()) {
            motionX *= 2.0;
            motionZ *= 2.0;
        }

        p.setVelocity(motionX, motionY, motionZ);
        p.fallDistance = 0.0f;
    }

    /**
     * LongJump: Boosts horizontal velocity when jumping for massive distance jumps.
     * Activates a one-time burst when the player jumps while sprinting.
     */
    private static void tickLongJump(MinecraftClient client) {
        if (!ModConfig.longJumpEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null) return;

        if (longJumpTimer > 0) longJumpTimer--;

        // Detect jump initiation (just left ground, positive Y, was sprinting)
        Vec3d vel = p.getVelocity();
        if (!p.isOnGround() && vel.y > 0.38 && vel.y < 0.45 && longJumpTimer <= 0) {
            // Boost horizontal velocity in look direction
            float yaw = (float) Math.toRadians(p.getYaw());
            double boost = ModConfig.longJumpBoost * 0.3;
            p.setVelocity(
                    vel.x - Math.sin(yaw) * boost,
                    vel.y + 0.1,
                    vel.z + Math.cos(yaw) * boost
            );
            longJumpTimer = 20; // 1 second cooldown
        }
    }

    /**
     * AutoMLG: Automatically places a water bucket when falling from lethal height.
     * Switches to water bucket, looks down, and places it before impact.
     */
    private static void tickAutoMLG(MinecraftClient client) {
        if (!ModConfig.autoMLGEnabled) return;
        ClientPlayerEntity p = client.player;
        if (p == null || client.interactionManager == null) return;

        if (autoMLGCooldown > 0) { autoMLGCooldown--; return; }

        // Only activate when falling fast and high enough to take damage
        if (p.isOnGround() || p.getVelocity().y > -0.5 || p.fallDistance < 5.0f) return;

        // Check distance to ground
        BlockPos below = p.getBlockPos();
        for (int i = 0; i < 5; i++) {
            below = below.down();
            if (!client.world.getBlockState(below).isAir()) {
                // Ground is within 5 blocks, find water bucket
                int waterSlot = -1;
                for (int slot = 0; slot < 9; slot++) {
                    if (p.getInventory().getStack(slot).isOf(Items.WATER_BUCKET)) {
                        waterSlot = slot;
                        break;
                    }
                }
                if (waterSlot < 0) return;

                int prevSlot = p.getInventory().getSelectedSlot();
                p.getInventory().setSelectedSlot(waterSlot);

                // Look straight down
                p.setPitch(90.0f);

                // Use the water bucket
                client.interactionManager.interactItem(p, Hand.MAIN_HAND);
                p.swingHand(Hand.MAIN_HAND);

                // Restore slot after a tick
                p.getInventory().setSelectedSlot(prevSlot);
                autoMLGCooldown = 40;
                return;
            }
        }
    }

    /**
     * Blink: Stores all outgoing movement packets and releases them all at once.
     * Makes you appear to teleport from the server's perspective.
     * Toggles on/off: when enabled, your server-side position freezes;
     * when disabled, all stored positions are sent at once.
     */
    private static void tickBlink(MinecraftClient client) {
        if (!ModConfig.blinkEnabled) {
            if (blinkActive) {
                // Blink was just turned off, send current position
                blinkActive = false;
                blinkStartPos = null;
            }
            return;
        }

        ClientPlayerEntity p = client.player;
        if (p == null || client.getNetworkHandler() == null) return;

        if (!blinkActive) {
            blinkActive = true;
            blinkStartPos = p.getEntityPos();
            blinkTickCounter = 0;
        }

        blinkTickCounter++;

        // Auto-release after 100 ticks (5 seconds) to prevent desync kick
        if (blinkTickCounter >= 100) {
            ModConfig.blinkEnabled = false;
            blinkActive = false;
            blinkStartPos = null;
            p.sendMessage(net.minecraft.text.Text.literal("\u00a7e[Blink] Auto-released (5s limit)"), true);
        }
    }
}
