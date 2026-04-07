package com.reachfly;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * ESP entity data packet (S2C).
 * Server sends extended entity positions to the client.
 * Mirrors the server addon's EspDataPayload.
 */
public record EspDataPayload(List<EntityEntry> entities) implements CustomPacketPayload {

    public static final Type<EspDataPayload> ID =
            new Type<>(ResourceLocation.fromNamespaceAndPath("reachfly", "esp_entities"));

    public static final StreamCodec<FriendlyByteBuf, EspDataPayload> CODEC =
            StreamCodec.of(EspDataPayload::write, EspDataPayload::read);

    public record EntityEntry(int entityId, double x, double y, double z,
                               String type, float health) {}

    private void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entities.size());
        for (EntityEntry e : entities) {
            buf.writeVarInt(e.entityId());
            buf.writeDouble(e.x());
            buf.writeDouble(e.y());
            buf.writeDouble(e.z());
            buf.writeString(e.type());
            buf.writeFloat(e.health());
        }
    }

    private static EspDataPayload read(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<EntityEntry> entries = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            entries.add(new EntityEntry(
                    buf.readVarInt(),
                    buf.readDouble(), buf.readDouble(), buf.readDouble(),
                    buf.readString(),
                    buf.readFloat()));
        }
        return new EspDataPayload(entries);
    }

    @Override
    public Type<? extends CustomPacketPayload> getId() {
        return ID;
    }
}
