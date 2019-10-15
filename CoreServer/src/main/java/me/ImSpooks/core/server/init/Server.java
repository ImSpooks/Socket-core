package me.ImSpooks.core.server.init;

import lombok.Getter;
import me.ImSpooks.core.common.exceptions.SocketDisconnectedException;
import me.ImSpooks.core.common.interfaces.IServer;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.server.CoreServer;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Nick on 26 sep. 2019.
 * Copyright © ImSpooks
 */
public class Server implements IServer {

    @Getter private final int port;
    @Getter private final List<ServerClient> clients;

    @Getter private ServerSocket serverSocket;

    @Getter private boolean started = false;

    @Getter private CoreServer coreServer;


    public Server(int port, CoreServer coreServer) {
        this.coreServer = coreServer;
        this.port = port;
        this.clients = new ArrayList<>();

        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            Logger.error(e);
        }


        new Thread(this::handleClients, "Client handler").start();
        new Thread(this::handleServer, "Server handler").start();
    }

    @Override
    public void handleServer() {
        while (!this.serverSocket.isClosed()) {
            Iterator<ServerClient> iterator = this.clients.iterator();

            try {
                while (iterator.hasNext()) {
                    ServerClient client = iterator.next();

                    if (client.getSocket().isClosed()) {
                        iterator.remove();
                        continue;
                    }

                    try {
                        client.handleConnection();
                        client.handleClient();
                    } catch (SocketDisconnectedException e) {
                        iterator.remove();
                        Logger.info("Client \'{}\' on ip \'{}\' was disconnected.", client.getClientName().isEmpty() ? "unknown" : client.getClientName(), client.getSocket().getInetAddress().getHostAddress());
                    } catch (Exception e) {
                        iterator.remove();
                        Logger.error(e, "Something went wrong with client \'{}\', removing them from the list", client.getClientName().isEmpty() ? "unknown" : client.getClientName());
                        client.close();
                    }
                }
            } catch (ConcurrentModificationException ignored) {
                // why does this even exist
            }
        }
    }

    @Override
    public void handleClients() {
        this.started = true;
        while (!this.serverSocket.isClosed()) {
            try {
                if (serverSocket.isClosed())
                    break;
                Socket socket = serverSocket.accept();

                ServerClient client = new ServerClient(socket, this.coreServer);
                client.connect();
                Logger.info("Client \'{}\' on ip \'{}\' connected, waiting for approval.", client.getClientName().isEmpty() ? "unknown" : client.getClientName(), client.getSocket().getInetAddress().getHostAddress());

                this.clients.add(client);
            } catch (IOException e) {
                Logger.error(e);
            }
        }
        this.started = false;
    }

    @Override
    public void write(String server, Packet packet) {
        for (ServerClient client : this.clients) {
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
            Logger.error(e);
        }
    }
}