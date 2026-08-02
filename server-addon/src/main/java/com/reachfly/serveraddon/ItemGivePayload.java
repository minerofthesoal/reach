package com.reachfly.serveraddon;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Custom payload for item give requests (C2S).
 * Server executes /give using server command source, bypassing OP requirement.
 */
public record ItemGivePayload(String itemId, int quantity) implements CustomPayload {

    public static final Id<ItemGivePayload> ID =
            new Id<>(ResourceLocation.of("reachfly", "item_give"));

    public static final PacketCodec<PacketByteBuf, ItemGivePayload> CODEC =
            PacketCodec.of(ItemGivePayload::write, ItemGivePayload::read);

    private void write(PacketByteBuf buf) {
        buf.writeString(itemId);
        buf.writeInt(quantity);
    }

    private static ItemGivePayload read(PacketByteBuf buf) {
        return new ItemGivePayload(buf.readString(), buf.readInt());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
