package com.reachfly;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Custom payload for teleport requests sent from client to server.
 * Used by the server-side addon to teleport players.
 */
public record TeleportPayload(double x, double y, double z) implements CustomPayload {

    public static final Id<TeleportPayload> ID =
            new Id<>(ResourceLocation.fromNamespaceAndPath("reachfly", "teleport"));

    public static final PacketCodec<PacketByteBuf, TeleportPayload> CODEC =
            PacketCodec.of(TeleportPayload::write, TeleportPayload::read);

    private void write(PacketByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    private static TeleportPayload read(PacketByteBuf buf) {
        return new TeleportPayload(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
