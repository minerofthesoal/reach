package com.reachfly.serveraddon;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server-side addon for Optimizer Super Premium.
 *
 * When installed on a server, allows players running the client mod
 * to teleport to any coordinates by pressing T (or via the config screen).
 *
 * Players WITHOUT the client mod are completely unaffected - the server
 * simply never receives the custom packet from them.
 */
public class OspServerAddon implements DedicatedServerModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("osp-server-addon");

    @Override
    public void onInitializeServer() {
        LOGGER.info("[OSP Server Addon] Initializing...");

        // Register the teleport payload type (C2S = client to server)
        PayloadTypeRegistry.playC2S().register(TeleportPayload.ID, TeleportPayload.CODEC);

        // Register the receiver - when a client sends a teleport packet, teleport them
        ServerPlayNetworking.registerGlobalReceiver(TeleportPayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    double x = payload.x();
                    double y = payload.y();
                    double z = payload.z();

                    // Clamp Y to valid range
                    y = Math.max(-64, Math.min(320, y));

                    LOGGER.info("[OSP Server Addon] Teleporting {} to {}, {}, {}",
                            player.getName().getString(), x, y, z);

                    // Execute on the server thread
                    double finalY = y;
                    context.server().execute(() -> {
                        player.requestTeleport(x, finalY, z);

                        player.sendMessage(
                                Text.literal("\u00a7a[OSP] Teleported to " +
                                        String.format("%.0f, %.0f, %.0f", x, finalY, z)),
                                true);
                    });
                });

        LOGGER.info("[OSP Server Addon] Ready. Players with the client mod can now teleport.");
    }
}
