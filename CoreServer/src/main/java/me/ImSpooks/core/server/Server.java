package me.ImSpooks.core.server;

import lombok.Getter;
import me.ImSpooks.core.common.client.Client;
import me.ImSpooks.core.common.interfaces.IServer;
import me.ImSpooks.core.packets.init.Packet;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Nick on 26 sep. 2019.
 * No part of this publication may be reproduced, distributed, or transmitted in any form or by any means.
 * Copyright © ImSpooks
 */
public class Server implements IServer {

    @Getter private final int port;
    @Getter private final List<Client> clients;

    @Getter private ServerSocket serverSocket;

    @Getter private boolean started = false;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();

        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }


        new Thread(this::handleClients).start();
        new Thread(this::handleServer).start();

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public void handleServer() {
        while (!this.serverSocket.isClosed()) {

            Iterator<Client> iterator = this.clients.iterator();

            while (iterator.hasNext()) {
                Client client = iterator.next();
                try {
                    client.handleClient();
                } catch (Exception e) {
                    iterator.remove();
                    Logger.error(e, "Something went wrong with client \'{}\', removing them from the list", client.getClientName().isEmpty() ? "unknown" : client.getClientName());
                }
            }
        }
    }

    @Override
    public void handleClients() {
        this.started = true;
        while (!this.serverSocket.isClosed()) {
            try {
                System.out.println("Waiting for incoming connection...");
                Socket socket = serverSocket.accept();
                System.out.println("Connection accepted");
                Client client = new Client(socket);

                this.clients.add(client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.started = false;
    }

    @Override
    public void write(String server, Packet packet) {
        for (Client client : this.clients) {
            if (client.getClientName().equalsIgnoreCase(server)) {
                client.write(packet);
            }
        }
    }

    @Override
    public void close() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}