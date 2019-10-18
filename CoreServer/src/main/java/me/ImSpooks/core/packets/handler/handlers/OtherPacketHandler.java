package me.ImSpooks.core.packets.handler.handlers;

import me.ImSpooks.core.common.client.AbstractClient;
import me.ImSpooks.core.packets.collection.other.PacketPing;
import me.ImSpooks.core.packets.collection.other.PacketPingResponse;
import me.ImSpooks.core.packets.handler.PacketHandler;
import me.ImSpooks.core.packets.handler.PacketHandling;
import me.ImSpooks.core.packets.handler.SubPacketHandler;

/**
 * Created by Nick on 01 okt. 2019.
 * Copyright © ImSpooks
 */
public class OtherPacketHandler extends SubPacketHandler {

    public OtherPacketHandler(PacketHandler packetHandler) {
        super(packetHandler);
    }

    @PacketHandling
    private void handlePacket(PacketPing packet, AbstractClient client) {
        client.write(new PacketPingResponse(System.currentTimeMillis(), packet.getClientTime()));
    }
}
