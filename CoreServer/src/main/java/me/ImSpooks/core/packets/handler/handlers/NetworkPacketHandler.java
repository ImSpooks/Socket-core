package me.ImSpooks.core.packets.handler.handlers;

import me.ImSpooks.core.common.client.AbstractClient;
import me.ImSpooks.core.packets.collection.network.PacketConfirmConnection;
import me.ImSpooks.core.packets.collection.network.PacketRequestConnection;
import me.ImSpooks.core.packets.handler.PacketHandler;
import me.ImSpooks.core.packets.handler.SubPacketHandler;
import me.ImSpooks.core.server.connection.ConnectionMaps;
import org.tinylog.Logger;

/**
 * Created by Nick on 01 okt. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class NetworkPacketHandler extends SubPacketHandler {

    public NetworkPacketHandler(PacketHandler packetHandler) {
        super(packetHandler);
    }


    private void handlePacket(PacketRequestConnection packet, AbstractClient client) {
        if (!packet.getPassword().equals(this.packetHandler.getPassword())) {
            int attempts = ConnectionMaps.connectionAttempts.getOrDefault(client, 0);
            ConnectionMaps.connectionAttempts.put(client, (attempts + 1));

            Logger.info("Client ({}) tried to connect with the wrong password, closing connection... (Attempt: {})", packet.getClientName(), attempts);
            if (attempts >= 5) {
                ConnectionMaps.blockedHosts.add(packet.getClientName());
                Logger.info("{} is now blocked until blocked hosts are getting cleared.", client.getClientName());
            }
            return;
        }
        Logger.info("Password was correct. Client ({}) connected with IP \'{}\'", packet.getClientName(), client.getSocket().getInetAddress().getHostAddress());

        this.packetHandler.confirmed.put(client, true);

        client.setClientName(packet.getClientName());
        client.write(new PacketConfirmConnection(packet.getRandomKey()));
    }
}
