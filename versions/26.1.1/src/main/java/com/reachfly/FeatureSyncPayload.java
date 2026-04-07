package com.reachfly;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Unified feature sync packet (C2S).
 * Mirrors the server addon's FeatureSyncPayload.
 */
public record FeatureSyncPayload(String feature, boolean enabled, float value) implements CustomPacketPayload {

    public static final Type<FeatureSyncPayload> ID =
            new Type<>(ResourceLocation.fromNamespaceAndPath("reachfly", "feature_sync"));

    public static final StreamCodec<FriendlyByteBuf, FeatureSyncPayload> CODEC =
            StreamCodec.of(FeatureSyncPayload::write, FeatureSyncPayload::read);

    private void write(FriendlyByteBuf buf) {
        buf.writeString(feature);
        buf.writeBoolean(enabled);
        buf.writeFloat(value);
    }

    private static FeatureSyncPayload read(FriendlyByteBuf buf) {
        return new FeatureSyncPayload(buf.readString(), buf.readBoolean(), buf.readFloat());
    }

    @Override
    public Type<? extends CustomPacketPayload> getId() {
        return ID;
    }
}
