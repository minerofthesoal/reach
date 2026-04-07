package com.reachfly;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Unified feature sync packet (C2S).
 * Mirrors the server addon's FeatureSyncPayload.
 */
public record FeatureSyncPayload(String feature, boolean enabled, float value) implements CustomPayload {

    public static final Id<FeatureSyncPayload> ID =
            new Id<>(Identifier.of("reachfly", "feature_sync"));

    public static final PacketCodec<PacketByteBuf, FeatureSyncPayload> CODEC =
            PacketCodec.of(FeatureSyncPayload::write, FeatureSyncPayload::read);

    private void write(PacketByteBuf buf) {
        buf.writeString(feature);
        buf.writeBoolean(enabled);
        buf.writeFloat(value);
    }

    private static FeatureSyncPayload read(PacketByteBuf buf) {
        return new FeatureSyncPayload(buf.readString(), buf.readBoolean(), buf.readFloat());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
