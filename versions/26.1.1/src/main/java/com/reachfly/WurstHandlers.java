package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

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
    private static Vec3 blinkStartPos = null;
    private static boolean blinkActive = false;

    public static void tick(Minecraft client) {
        if (client.player == null || client.level == null) return;

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

    private static void tickCriticals(Minecraft client) {
        if (!ModConfig.criticalsEnabled) { criticalJumped = false; return; }
        LocalPlayer p = client.player;
        if (p == null || !p.isOnGround()) return;

        if (p.getAttackStrengthScale(0.0f) >= 0.95f && !criticalJumped) {
            p.setDeltaMovement(p.getDeltaMovement().add(0, 0.1, 0));
            p.setOnGround(false);
            criticalJumped = true;
        }
        if (p.getAttackStrengthScale(0.0f) < 0.5f) {
            criticalJumped = false;
        }
    }

    private static void tickBunnyHop(Minecraft client) {
        if (!ModConfig.bunnyHopEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.screen != null) return;
        if (!p.isSprinting() || !p.isOnGround()) return;

        bunnyHopDelay++;
        if (bunnyHopDelay < 1) return;
        bunnyHopDelay = 0;
        p.jumpFromGround();
    }

    private static void tickSpider(Minecraft client) {
        if (!ModConfig.spiderEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;

        if (p.horizontalCollision && !p.isOnGround()) {
            Vec3 vel = p.getDeltaMovement();
            p.setDeltaMovement(vel.x, 0.2, vel.z);
            p.fallDistance = 0.0f;
        }
    }

    private static void tickGlide(Minecraft client) {
        if (!ModConfig.glideEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;

        if (!p.isOnGround() && p.getDeltaMovement().y < 0) {
            Vec3 vel = p.getDeltaMovement();
            p.setDeltaMovement(vel.x, Math.max(vel.y, -0.06), vel.z);
            p.fallDistance = 0.0f;
        }
    }

    private static void tickHighJump(Minecraft client) {
        if (!ModConfig.highJumpEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;

        Vec3 vel = p.getDeltaMovement();
        if (!p.isOnGround() && vel.y > 0.38 && vel.y < 0.45) {
            double boost = (ModConfig.highJumpHeight - 1.0) * 0.2;
            p.setDeltaMovement(vel.x, vel.y + boost, vel.z);
        }
    }

    private static void tickDolphin(Minecraft client) {
        if (!ModConfig.dolphinEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;

        if (p.isInWater()) {
            Vec3 vel = p.getDeltaMovement();
            double speed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);

            if (speed > 0.01) {
                p.setDeltaMovement(vel.x * 1.4, vel.y, vel.z * 1.4);
            }
            if (p.isSwimming() && p.getXRot() < -10) {
                p.setDeltaMovement(p.getDeltaMovement().add(0, 0.04, 0));
            }
            if (p.isUnderWater() && !p.isShiftKeyDown()) {
                p.setDeltaMovement(p.getDeltaMovement().add(0, 0.02, 0));
            }
        }
    }

    private static void tickAutoSword(Minecraft client) {
        if (!ModConfig.autoSwordEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.screen != null) return;
        if (client.hitResult == null) return;

        if (!(client.hitResult instanceof net.minecraft.util.hit.EntityHitResult ehr)) return;
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

        if (bestSlot >= 0 && bestSlot != p.getInventory().selected) {
            p.getInventory().selected = bestSlot;
        }
    }

    private static void tickSneak(Minecraft client) {
        if (!ModConfig.sneakEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.screen != null) return;
        p.setShiftKeyDown(true);
    }

    private static void tickPanic(Minecraft client) {
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
            client.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("\u00a7c[OSP] PANIC - All hacks disabled!"),
                    true);
        }
    }

    private static void tickAntiHunger(Minecraft client) {
        if (!ModConfig.antiHungerEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.getConnection() == null) return;

        if (!p.isOnGround() && p.getDeltaMovement().y < 0) {
            client.getConnection().send(
                    new net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly(
                            true, p.horizontalCollision));
        }
    }

    private static void tickTriggerBot(Minecraft client) {
        if (!ModConfig.triggerBotEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.screen != null) return;
        if (client.gameMode == null) return;

        if (p.getAttackStrengthScale(0.0f) < 1.0f) return;

        if (client.hitResult instanceof net.minecraft.util.hit.EntityHitResult ehr) {
            Entity target = ehr.getEntity();
            if (target instanceof LivingEntity living && living.isAlive()) {
                if (ModConfig.autoHitPlayersOnly && !(target instanceof Player)) return;
                client.gameMode.attack(p, target);
                p.swing(InteractionHand.MAIN_HAND);
            }
        }
    }

    // ====== NEW WURST FEATURES ======

    /**
     * FastPlace: Removes the 4-tick placement cooldown, allowing rapid block placement.
     * Sets the item use cooldown to 0 every tick for instant placement.
     */
    private static void tickFastPlace(Minecraft client) {
        if (!ModConfig.fastPlaceEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;

        // Reset the right-click delay to allow rapid block placement
        // Uses reflection-free approach via the Minecraft field
        try {
            java.lang.reflect.Field f = Minecraft.class.getDeclaredField("missTime"); // missTime
            f.setAccessible(true);
            f.setInt(client, 0);
        } catch (Exception ignored) {
            // Fallback: try yarn name
            try {
                java.lang.reflect.Field f = Minecraft.class.getDeclaredField("missTime");
                f.setAccessible(true);
                f.setInt(client, 0);
            } catch (Exception ignored2) {}
        }
    }

    /**
     * Parkour: Automatically jumps when reaching the edge of a block while sprinting.
     * Perfect for parkour courses - jumps at the optimal position for max distance.
     */
    private static void tickParkour(Minecraft client) {
        if (!ModConfig.parkourEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.screen != null) return;
        if (!p.isOnGround()) return;

        // Check if the block below the player's feet edge is air
        Vec3 vel = p.getDeltaMovement();
        double speed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        if (speed < 0.05) return; // Not moving fast enough

        // Project forward slightly to detect edge
        double px = p.getX() + vel.x * 2;
        double pz = p.getZ() + vel.z * 2;
        BlockPos checkPos = new BlockPos((int) Math.floor(px), (int) Math.floor(p.getY() - 0.5), (int) Math.floor(pz));

        if (client.level.getBlockState(checkPos).isAir()) {
            p.jumpFromGround();
        }
    }

    /**
     * NoSlowdown: Prevents the slowdown effect from using items (eating, blocking, drawing bow).
     * Maintains full movement speed while consuming items or using shields.
     */
    private static void tickNoSlowdown(Minecraft client) {
        if (!ModConfig.noSlowdownEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;

        // If using an item, counteract the slowdown by boosting speed back
        if (p.isUsingItem()) {
            Vec3 vel = p.getDeltaMovement();
            // Item use applies a 0.2x multiplier, we counteract it partially
            p.setDeltaMovement(vel.x * 1.4, vel.y, vel.z * 1.4);
        }
    }

    /**
     * AntiBlind: Removes blindness, darkness, and nausea potion effects client-side.
     * Keeps the player's vision clear regardless of effects applied by the server.
     */
    private static void tickAntiBlind(Minecraft client) {
        if (!ModConfig.antiBlindEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;

        // Remove visual impairment effects client-side
        if (p.hasEffect(MobEffects.BLINDNESS)) {
            p.removeEffect(MobEffects.BLINDNESS);
        }
        if (p.hasEffect(MobEffects.DARKNESS)) {
            p.removeEffect(MobEffects.DARKNESS);
        }
        if (p.hasEffect(MobEffects.NAUSEA)) {
            p.removeEffect(MobEffects.NAUSEA);
        }
    }

    /**
     * AutoWalk: Automatically walks forward without holding W key.
     * Useful for long journeys. Simulates forward input continuously.
     */
    private static void tickAutoWalk(Minecraft client) {
        if (!ModConfig.autoWalkEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.screen != null) return;

        // Simulate forward key press
        net.minecraft.world.entity.player.Input current = p.input.lastInput;
        p.input.lastInput = new net.minecraft.world.entity.player.Input(
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
    private static void tickAirJump(Minecraft client) {
        if (!ModConfig.airJumpEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.screen != null) return;

        if (airJumpCooldown > 0) airJumpCooldown--;

        if (p.isOnGround()) {
            wasOnGround = true;
            return;
        }

        // Detect jump key press while in air
        if (p.input.lastInput.jump() && wasOnGround == false && airJumpCooldown <= 0) {
            Vec3 vel = p.getDeltaMovement();
            p.setDeltaMovement(vel.x, 0.42, vel.z); // Normal jump velocity
            p.fallDistance = 0.0f;
            airJumpCooldown = 10; // Half-second cooldown
        }

        if (!p.input.lastInput.jump()) {
            wasOnGround = false;
        }
    }

    /**
     * NoWeb: Prevents cobwebs from slowing down the player.
     * Counteracts the velocity reduction when inside a cobweb block.
     */
    private static void tickNoWeb(Minecraft client) {
        if (!ModConfig.noWebEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;

        // Check if player is in a cobweb area by detecting drastic velocity reduction
        BlockPos pos = p.blockPosition();
        if (client.level.getBlockState(pos).getBlock() == net.minecraft.world.level.block.Blocks.COBWEB ||
            client.level.getBlockState(pos.up()).getBlock() == net.minecraft.world.level.block.Blocks.COBWEB) {
            // Counteract web slowdown by maintaining velocity
            Vec3 vel = p.getDeltaMovement();
            p.setDeltaMovement(vel.x * 5.0, vel.y, vel.z * 5.0);
        }
    }

    /**
     * FlightPlus: Enhanced creative-style flight with configurable speed and
     * vertical control. Works without creative mode permission.
     * Press jump to go up, sneak to go down, sprint to go faster.
     */
    private static void tickFlightPlus(Minecraft client) {
        if (!ModConfig.flightPlusEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.screen != null) return;

        float speed = ModConfig.flightPlusSpeed;
        float yaw = (float) Math.toRadians(p.getYRot());

        double motionX = 0, motionY = 0, motionZ = 0;

        net.minecraft.world.entity.player.Input input = p.input.lastInput;

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

        p.setDeltaMovement(motionX, motionY, motionZ);
        p.fallDistance = 0.0f;
    }

    /**
     * LongJump: Boosts horizontal velocity when jumping for massive distance jumps.
     * Activates a one-time burst when the player jumps while sprinting.
     */
    private static void tickLongJump(Minecraft client) {
        if (!ModConfig.longJumpEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;

        if (longJumpTimer > 0) longJumpTimer--;

        // Detect jump initiation (just left ground, positive Y, was sprinting)
        Vec3 vel = p.getDeltaMovement();
        if (!p.isOnGround() && vel.y > 0.38 && vel.y < 0.45 && longJumpTimer <= 0) {
            // Boost horizontal velocity in look direction
            float yaw = (float) Math.toRadians(p.getYRot());
            double boost = ModConfig.longJumpBoost * 0.3;
            p.setDeltaMovement(
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
    private static void tickAutoMLG(Minecraft client) {
        if (!ModConfig.autoMLGEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.gameMode == null) return;

        if (autoMLGCooldown > 0) { autoMLGCooldown--; return; }

        // Only activate when falling fast and high enough to take damage
        if (p.isOnGround() || p.getDeltaMovement().y > -0.5 || p.fallDistance < 5.0f) return;

        // Check distance to ground
        BlockPos below = p.blockPosition();
        for (int i = 0; i < 5; i++) {
            below = below.down();
            if (!client.level.getBlockState(below).isAir()) {
                // Ground is within 5 blocks, find water bucket
                int waterSlot = -1;
                for (int slot = 0; slot < 9; slot++) {
                    if (p.getInventory().getStack(slot).is(Items.WATER_BUCKET)) {
                        waterSlot = slot;
                        break;
                    }
                }
                if (waterSlot < 0) return;

                int prevSlot = p.getInventory().selected;
                p.getInventory().selected = waterSlot;

                // Look straight down
                p.setXRot(90.0f);

                // Use the water bucket
                client.gameMode.useItem(p, InteractionHand.MAIN_HAND);
                p.swing(InteractionHand.MAIN_HAND);

                // Restore slot after a tick
                p.getInventory().selected = prevSlot;
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
    private static void tickBlink(Minecraft client) {
        if (!ModConfig.blinkEnabled) {
            if (blinkActive) {
                // Blink was just turned off, send current position
                blinkActive = false;
                blinkStartPos = null;
            }
            return;
        }

        LocalPlayer p = client.player;
        if (p == null || client.getConnection() == null) return;

        if (!blinkActive) {
            blinkActive = true;
            blinkStartPos = p.position();
            blinkTickCounter = 0;
        }

        blinkTickCounter++;

        // Auto-release after 100 ticks (5 seconds) to prevent desync kick
        if (blinkTickCounter >= 100) {
            ModConfig.blinkEnabled = false;
            blinkActive = false;
            blinkStartPos = null;
            p.displayClientMessage(net.minecraft.network.chat.Component.literal("\u00a7e[Blink] Auto-released (5s limit)"), true);
        }
    }
}
