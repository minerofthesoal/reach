package com.reachfly;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Custom payload for teleport requests sent from client to server.
 * Used by the server-side addon to teleport players.
 */
public record TeleportPayload(double x, double y, double z) implements CustomPacketPayload {

    public static final Type<TeleportPayload> ID =
            new Type<>(ResourceLocation.fromNamespaceAndPath("reachfly", "teleport"));

    public static final StreamCodec<FriendlyByteBuf, TeleportPayload> CODEC =
            StreamCodec.of(TeleportPayload::write, TeleportPayload::read);

    private void write(FriendlyByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    private static TeleportPayload read(FriendlyByteBuf buf) {
        return new TeleportPayload(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    @Override
    public Type<? extends CustomPacketPayload> getId() {
        return ID;
    }
}
