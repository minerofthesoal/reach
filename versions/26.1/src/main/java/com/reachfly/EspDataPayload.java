package com.reachfly;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * ESP entity data packet (S2C).
 * Server sends extended entity positions to the client.
 * Mirrors the server addon's EspDataPayload.
 */
public record EspDataPayload(List<EntityEntry> entities) implements CustomPayload {

    public static final Id<EspDataPayload> ID =
            new Id<>(Identifier.of("reachfly", "esp_entities"));

    public static final PacketCodec<PacketByteBuf, EspDataPayload> CODEC =
            PacketCodec.of(EspDataPayload::write, EspDataPayload::read);

    public record EntityEntry(int entityId, double x, double y, double z,
                               String type, float health) {}

    private void write(PacketByteBuf buf) {
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

    private static EspDataPayload read(PacketByteBuf buf) {
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
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
