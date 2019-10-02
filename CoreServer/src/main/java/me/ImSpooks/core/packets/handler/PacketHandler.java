package me.ImSpooks.core.packets.handler;

import lombok.Getter;
import me.ImSpooks.core.common.client.AbstractClient;
import me.ImSpooks.core.packets.collection.network.PacketRequestConnection;
import me.ImSpooks.core.packets.handler.handlers.DatabasePacketHandler;
import me.ImSpooks.core.packets.handler.handlers.NetworkPacketHandler;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.type.PacketType;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public class PacketHandler {

    @Getter private String password;

    @Getter private final Map<PacketType, SubPacketHandler> packetHandlers = new HashMap<>();
    public Map<AbstractClient, Boolean> confirmed = new HashMap<>();

    public PacketHandler(String password) {
        this.password = password;
        this.packetHandlers.put(PacketType.NETWORK, new NetworkPacketHandler(this));
        this.packetHandlers.put(PacketType.DATABASE, new DatabasePacketHandler(this));
    }

    public void handlePacket(Packet packet, AbstractClient client) {
        confirmed.putIfAbsent(client, false);

        if (!(packet instanceof PacketRequestConnection)) {
            if (!confirmed.get(client)) {
                Logger.warn("Client's first packet was not a PacketRequestConnection but was {} instead, reconnecting...", packet.getClass().getName());
                client.close();
                return;
            }
        }

        this.packetHandlers.get(packet.getType()).handlePacket(packet, client);
    }
}
