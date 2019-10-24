package me.ImSpooks.core.client.init;

import me.ImSpooks.core.client.CoreClient;
import me.ImSpooks.core.common.client.AbstractClient;
import me.ImSpooks.core.enums.ClientType;
import me.ImSpooks.core.helpers.JavaHelpers;
import me.ImSpooks.core.helpers.ThreadBuilder;
import me.ImSpooks.core.packets.collection.network.PacketClosing;
import me.ImSpooks.core.packets.collection.network.PacketConfirmConnection;
import me.ImSpooks.core.packets.collection.network.PacketRequestConnection;
import me.ImSpooks.core.packets.collection.network.PacketStop;
import me.ImSpooks.core.packets.init.Packet;
import org.tinylog.Logger;

import java.io.*;
import java.net.Socket;

/**
 * Created by Nick on 27 sep. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class Client extends AbstractClient {

    private CoreClient coreClient;

    private String ip;
    private int port;

    private volatile boolean connectionConfirmed = false;

    private final long RECONNECT_INTERVAL = 5000; // 5 seconds

    public Client(String ip, int port, String clientName, CoreClient coreClient) {
        this.coreClient = coreClient;
        this.ip = ip;
        this.port = port;
        this.clientName = clientName;
    }


    @Override
    public void connect() {
        this.initialize();
    }

    private int received = 0;
    @Override
    public void handlePacket(Packet receivedPacket) {
        if (receivedPacket == null) {
            Logger.debug("Received a packet that is equal to NULL, check whats wrong.");
            return;
        }
//        System.out.println("received = " + received++);

        //System.out.println("handling received packet " + receivedPacket.getClass().getSimpleName());
        if (receivedPacket instanceof PacketClosing) {
            try {
                this.socket.close();
            } catch (IOException e) {
                Logger.error(e);
            }
        }
        else if (receivedPacket instanceof PacketStop) {
            Logger.info("Received stop command from the core server");
            this.coreClient.stop();
        }
        else if (this.connectionConfirmed) {
            this.coreClient.getPacketReceiver().received(receivedPacket);
        }
        else {
            if (!(receivedPacket instanceof PacketConfirmConnection)) {
                Logger.warn("Incoming packet was {} instead of required packet {}, reconnecting...", receivedPacket.getClass().getName(), "PacketConfirmConnection");
                this.initialize();
                return;
            }

            PacketConfirmConnection correct = (PacketConfirmConnection) receivedPacket;
            if (correct.getRandomKey() != this.coreClient.getVerificationId()) {
                Logger.info("Verification key was {} but had to be {}, disconnecting...", correct.getRandomKey(), this.coreClient.getVerificationId());
                this.initialize();
                return;
            }
            this.connectionConfirmed = true;
            Logger.info("Connection confirmed, packets can now be send.");

            new Thread(this.coreClient.getCoreConnected()).start();
        }
    }

    private void initialize() {
        Logger.info(String.format("Establishing connection... %s:%s", this.ip, this.port));
        try {
            this.socket = new Socket(this.ip, this.port);

            this.in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
            this.out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));

//            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            this.out = new PrintWriter(socket.getOutputStream(), true);
//
//            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
//            this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)), true);

            this.socket.setTcpNoDelay(true);
            this.socket.setSoTimeout(5000);

            new ThreadBuilder(new Runnable() {
                void disconnect() {
                    connectionConfirmed = false;
                    JavaHelpers.sleep(RECONNECT_INTERVAL);
                    initialize();
                }
                @Override
                public void run() {
                    while (!socket.isClosed()) {
                        try {
                            JavaHelpers.sleep(RECONNECT_INTERVAL);
                        } catch (Exception e) {
                            Logger.warn(e, "Connection to server lost duo to a strange error, trying again in {} seconds", RECONNECT_INTERVAL / 1000L);
                            disconnect();
                            break;
                        }
                    }
                    Logger.warn("Connection to server lost, trying again in {} seconds", RECONNECT_INTERVAL / 1000L);
                    disconnect();
                }
            }, "Connection handler", Thread.MIN_PRIORITY).start();

            new ThreadBuilder(() -> {
                closed = false;
                while (!socket.isClosed()) {
                    try {
                        handleClient();
                    } catch (Exception e) {
                        Logger.error(e);
                        break;
                    }
                }
                closed = true;
            }, "Client handler", Thread.MAX_PRIORITY).start();

            Logger.info("Connection established, confirming identity...");
            this.write(new PacketRequestConnection(this.coreClient.getPassword(), this.coreClient.getVerificationId(), this.clientName, ClientType.JAVA));
            Logger.info("Connection verification requested.");

        } catch (IOException e) {
            Logger.info("No connection established, trying again in {} seconds", RECONNECT_INTERVAL / 1000L);
            connectionConfirmed = false;
            JavaHelpers.sleep(RECONNECT_INTERVAL);
            initialize();
        }
    }
}
