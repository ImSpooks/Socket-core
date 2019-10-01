package me.ImSpooks.core.common.client;

import com.google.gson.JsonParseException;
import lombok.Getter;
import lombok.Setter;
import me.ImSpooks.core.common.interfaces.IClient;
import me.ImSpooks.core.helpers.JavaHelpers;
import me.ImSpooks.core.packets.collection.client.PacketClientName;
import me.ImSpooks.core.packets.handler.NetworkPacketHandler;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.WrappedOutputStream;

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
public class Client implements IClient {

    @Getter @Setter private String clientName;

    private Socket socket;

    @Getter private DataInputStream in;
    private DataOutputStream out;

    @Getter private boolean started = false;

    public Client(String ip, int port, String clientName) {
        try {
            this.socket = new Socket(ip, port);
            this.socket.setTcpNoDelay(true);

            this.in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
            this.out = new DataOutputStream(this.socket.getOutputStream());


            new Thread(() -> {
                started = true;
                while (!socket.isClosed()) {
                    handleClient();
                }
                started = false;
            }).start();

            Runtime.getRuntime().addShutdownHook(new Thread(this::close));

            this.write(new PacketClientName(this.clientName = clientName));

            while (true) {
                JavaHelpers.sleep(500);
                //this.write(new PacketClientStatus());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Client(Socket socket) {
        try {
            started = true;
            this.socket = socket;
            this.socket.setTcpNoDelay(true);

            this.in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
            this.out = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handlePacket(Packet receivedPacket) {
        if (receivedPacket == null) {
            return;
        }

        switch (receivedPacket.getType()) {
            default:
                break;

            case NETWORK:
                NetworkPacketHandler.getInstance().handlePacket(receivedPacket, this);
                break;

            case OTHER:
                break;
        }
    }

    @Override
    public void handleClient() {
        try {
            if (this.in.available() == 0) {
                JavaHelpers.sleep(50);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            int length = in.readInt();
            byte[] buffer = new byte[length];
            in.read(buffer);
            try {
                this.handlePacket(Packet.deserialize(buffer));
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(Packet packet) {
        try {
            byte[] serialized = packet.serialize(new WrappedOutputStream());
            this.out.writeInt(serialized.length);
            this.out.write(serialized);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            started = false;
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
