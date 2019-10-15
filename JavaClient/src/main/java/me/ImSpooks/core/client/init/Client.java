package me.ImSpooks.core.client.init;

import me.ImSpooks.core.client.CoreClient;
import me.ImSpooks.core.common.client.AbstractClient;
import me.ImSpooks.core.common.exceptions.SocketDisconnectedException;
import me.ImSpooks.core.helpers.JavaHelpers;
import me.ImSpooks.core.packets.collection.network.PacketConfirmConnection;
import me.ImSpooks.core.packets.collection.network.PacketRequestConnection;
import me.ImSpooks.core.packets.init.Packet;
import org.tinylog.Logger;

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

        Runtime.getRuntime().addShutdownHook(new Thread(this::close, "Shutdown hook"));
    }

    @Override
    public void connect() {
        this.initialize();
    }

    @Override
    public void handleConnection() throws SocketDisconnectedException {
        try {
            if (this.isConnected())
                throw new SocketDisconnectedException(String.format("Server listening on %s:%s has disconnected (Crash?)", this.ip, this.port));
        } catch (SocketDisconnectedException e) {
            throw new SocketDisconnectedException(String.format("Server listening on %s:%s has disconnected (Crash?)", this.ip, this.port));
        }
    }

    @Override
    public void handlePacket(Packet receivedPacket) {
        if (receivedPacket == null) {
            Logger.debug("Received a packet that is equal to NULL, check whats wrong.");
            return;
        }

        //System.out.println("handling received packet " + receivedPacket.getClass().getSimpleName());
        if (this.connectionConfirmed) {
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
            Logger.info("Connection confirmed, packets can now be send...");

            this.coreClient.getCoreConnected().run();
        }
    }

    private void initialize() {
        Logger.info(String.format("Establishing connection... %s:%s", this.ip, this.port));
        try {
            this.socket = new Socket(this.ip, this.port);
            this.socket.setTcpNoDelay(true);

            this.in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
            this.out = new DataOutputStream(this.socket.getOutputStream());

            new Thread(new Runnable() {
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
                            handleConnection();
                        } catch (SocketDisconnectedException e) {
                            Logger.warn("Connection to server lost, trying again in {} seconds", RECONNECT_INTERVAL / 1000L);
                            disconnect();
                            break;
                        } catch (Exception e) {
                            Logger.warn(e, "Connection to server lost duo to a strange error, trying again in {} seconds", RECONNECT_INTERVAL / 1000L);
                            disconnect();
                            break;
                        }
                    }
                }
            }, "Connection handler").start();

            new Thread(() -> {
                started = true;
                while (!socket.isClosed()) {
                    try {
                        handleClient();
                    } catch (Exception e) {
                        Logger.error(e);
                        break;
                    }
                }
                started = false;
            }, "Client handler").start();

            this.write(new PacketRequestConnection(this.coreClient.getPassword(), this.coreClient.getVerificationId(), this.clientName));

        } catch (IOException e) {
            Logger.info("No connection established, trying again in {} seconds", RECONNECT_INTERVAL / 1000L);
            connectionConfirmed = false;
            JavaHelpers.sleep(RECONNECT_INTERVAL);
            initialize();
        }
    }
}
