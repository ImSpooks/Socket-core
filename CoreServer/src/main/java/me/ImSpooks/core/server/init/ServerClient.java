package me.ImSpooks.core.server.init;

import lombok.Getter;
import me.ImSpooks.core.common.client.AbstractClient;
import me.ImSpooks.core.common.exceptions.SocketDisconnectedException;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.server.CoreServer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Nick on 27 sep. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class ServerClient extends AbstractClient {

    @Getter private final CoreServer coreServer;
    @Getter private final Server server;

    public ServerClient(Socket socket, CoreServer server) {
        this.coreServer = server;
        this.server = this.coreServer.getServer();
        this.socket = socket;

    }

    @Override
    public void handleConnection() throws SocketDisconnectedException {
        if (this.isConnected())
            throw new SocketDisconnectedException(String.format("Client \'%s\' has disconnected (Crash?)", this.clientName));
    }

    @Override
    public void connect() {
        started = true;
        try {
            this.socket.setTcpNoDelay(true);

            this.in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
            this.out = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handlePacket(Packet receivedPacket) {
        this.coreServer.getPacketHandler().handlePacket(receivedPacket, this);
        /*switch (receivedPacket.getType()) {
            default:
                break;

            case NETWORK:
                NetworkPacketHandler.getInstance().handlePacket(receivedPacket, this);
                break;

            case DATABASE:
                DatabasePacketHandler.getInstance().handlePacket(receivedPacket, this);

            case OTHER:
                break;
        }*/
    }

    @Override
    public void write(Packet packet) {
        super.write(packet);
    }
}
