package me.ImSpooks.core.packets.handler.handlers;

import me.ImSpooks.core.common.client.AbstractClient;
import me.ImSpooks.core.packets.collection.network.PacketClosing;
import me.ImSpooks.core.packets.collection.network.PacketConfirmConnection;
import me.ImSpooks.core.packets.collection.network.PacketRequestConnection;
import me.ImSpooks.core.packets.handler.PacketHandler;
import me.ImSpooks.core.packets.handler.PacketHandling;
import me.ImSpooks.core.packets.handler.SubPacketHandler;
import me.ImSpooks.core.server.connection.ConnectionMaps;
import me.ImSpooks.core.server.init.ServerClient;
import org.tinylog.Logger;

import java.io.IOException;

/**
 * Created by Nick on 01 okt. 2019.
 * Copyright © ImSpooks
 */
public class NetworkPacketHandler extends SubPacketHandler {

    public NetworkPacketHandler(PacketHandler packetHandler) {
        super(packetHandler);
    }

    @PacketHandling
    private void handlePacket(PacketRequestConnection packet, AbstractClient client) {
        if (ConnectionMaps.blockedHosts.contains(packet.getClientName())) {
            client.close();
            return;
        }

        if (!packet.getPassword().equals(this.packetHandler.getPassword())) {
            int attempts = ConnectionMaps.connectionAttempts.getOrDefault(packet.getRandomKey(), 0);
            ConnectionMaps.connectionAttempts.put(packet.getRandomKey(), (attempts + 1));

            Logger.info("Client ({}) tried to connect with the wrong password, closing connection... (Attempt: {})", packet.getClientName(), attempts);
            if (attempts >= 5) {
                ConnectionMaps.blockedHosts.add(packet.getClientName());
                Logger.info("{} is now blocked until blocked hosts are getting cleared.", client.getClientName());
            }
            client.close();
            return;
        }
        Logger.info("Password was correct. Client (\"{}\":{}) connected with IP \'{}\'", packet.getClientName(), packet.getClientType().getName(), client.getSocket().getInetAddress().getHostAddress());

        Thread thread = this.packetHandler.getCoreServer().getServer().getClients().get((ServerClient) client);
        thread.setName(String.format(thread.getName(), client.getClientName()));

        this.packetHandler.confirmed.put(client, true);

        client.setClientName(packet.getClientName());
        client.write(new PacketConfirmConnection(packet.getRandomKey()));
    }

    @PacketHandling
    private void handlePacket(PacketClosing packet, AbstractClient client) {
        client.setClosed(true);
        try {
            client.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
