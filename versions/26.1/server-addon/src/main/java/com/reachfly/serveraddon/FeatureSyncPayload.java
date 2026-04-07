package com.reachfly.serveraddon;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Unified feature sync packet (C2S).
 * Client sends feature name + enabled state + float value.
 * Server applies the corresponding attribute/ability change.
 *
 * Supported features:
 *   "knockback" - ATTACK_KNOCKBACK attribute (value = strength)
 *   "reach"     - BLOCK/ENTITY_INTERACTION_RANGE (value = distance)
 *   "speed"     - MOVEMENT_SPEED modifier (value = multiplier)
 *   "nofall"    - Cancel fall damage (value ignored, enabled matters)
 *   "fly"       - Allow flight (value = fly speed multiplier)
 *   "esp"       - Request extended entity tracking (value = range)
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
