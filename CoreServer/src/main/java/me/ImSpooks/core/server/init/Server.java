package me.ImSpooks.core.server.init;

import lombok.Getter;
import me.ImSpooks.core.common.client.AbstractClient;
import me.ImSpooks.core.common.interfaces.IServer;
import me.ImSpooks.core.helpers.ThreadBuilder;
import me.ImSpooks.core.packets.collection.network.PacketClosing;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.server.CoreServer;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 26 sep. 2019.
 * Copyright © ImSpooks
 */
public class Server implements IServer {

    @Getter private final int port;
    @Getter private final Map<ServerClient, Thread> clients;

    @Getter private ServerSocket serverSocket;

    @Getter private boolean started = false;

    @Getter private CoreServer coreServer;


    public Server(int port, CoreServer coreServer) {
        this.coreServer = coreServer;
        this.port = port;
        this.clients = new HashMap<>();

        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            Logger.error(e);
        }


        new ThreadBuilder(this::handleClients, "Connection handler", Thread.MAX_PRIORITY).start();
    }

    @Override
    public void handleServer(AbstractClient client) {
        if (!(client instanceof ServerClient)) {
            Logger.error("Something went wrong here");
            client.close();
            return;
        }
        while (!this.serverSocket.isClosed()) {
            if (client.isClosed()) {
                this.clients.remove(client);
                Logger.info("Client \'{}\' on ip \'{}\' was disconnected.", client.getClientName().isEmpty() ? "unknown" : client.getClientName(), client.getSocket().getInetAddress().getHostAddress());
                return;
            }

            if (client.getSocket().isClosed()) {
                this.clients.remove(client);
                Logger.error("Client \'{}\' on ip \'\' closed unexpectedly, removing them from the list", client.getClientName().isEmpty() ? "unknown" : client.getClientName(), client.getSocket().getInetAddress().getHostAddress());
                break;
            }

            try {
                client.handleClient();
            } catch (Exception e) {
                this.clients.remove(client);
                Logger.error(e, "Something went wrong while handling client \'{}\', closing client...", client.getClientName().isEmpty() ? "unknown" : client.getClientName());
                client.close();
            }
        }
    }

    @Override
    public void handleClients() {
        this.started = true;
        while (!this.serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                if (serverSocket.isClosed())
                    break;

                ServerClient client = new ServerClient(socket, this.coreServer);
                client.connect();
                Logger.info("Client \'{}\' on ip \'{}\' connected, waiting for approval.", client.getClientName().isEmpty() ? "unknown" : client.getClientName(), client.getSocket().getInetAddress().getHostAddress());

                this.clients.put(client, new ThreadBuilder(() -> this.handleServer(client), "Client  Handler %s @" + socket.getInetAddress().getHostAddress()).start().getThread());
            } catch (IOException e) {
                if (!serverSocket.isClosed())
                    Logger.error(e);
            }
        }
        this.started = false;
    }

    @Override
    public void write(String server, Packet packet) {
        for (ServerClient client : this.clients.keySet()) {
            if (client.getClientName().equalsIgnoreCase(server)) {
                client.write(packet);
            }
        }
    }

    @Override
    public void close() {
        try {
            this.serverSocket.close();
            this.clients.forEach((client, thread) -> {
                client.write(new PacketClosing());
                client.close();
                thread.interrupt();
            });
        } catch (IOException e) {
            Logger.error(e);
        }
    }
}