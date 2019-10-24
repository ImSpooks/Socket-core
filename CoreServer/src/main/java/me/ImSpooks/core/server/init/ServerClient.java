package me.ImSpooks.core.server.init;

import lombok.Getter;
import me.ImSpooks.core.common.client.AbstractClient;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.server.CoreServer;
import org.tinylog.Logger;

import java.io.*;
import java.net.Socket;

/**
 * Created by Nick on 27 sep. 2019.
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
    public void connect() {
        closed = false;
        try {
            this.in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
            this.out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));

            this.socket.setTcpNoDelay(true);
            this.socket.setSoTimeout(5000);
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    private int received = 0;
    @Override
    public void handlePacket(Packet receivedPacket) {
//        System.out.println("received = " + received++);
        this.coreServer.getPacketHandler().handlePacket(receivedPacket, this);
    }
    private int send = 0;
    @Override
    public void write(Packet packet) {
//        System.out.println("send = " + send++);
        super.write(packet);
    }
}
