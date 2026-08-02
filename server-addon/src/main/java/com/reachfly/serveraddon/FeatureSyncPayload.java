package com.reachfly.serveraddon;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
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
public record FeatureSyncPayload(String feature, boolean enabled, float value) implements CustomPayload {

    public static final Id<FeatureSyncPayload> ID =
            new Id<>(ResourceLocation.of("reachfly", "feature_sync"));

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
