package me.ImSpooks.core.bridge.init;

import com.google.common.base.Ascii;
import com.google.gson.JsonParseException;
import lombok.Getter;
import me.ImSpooks.core.bridge.Bridge;
import me.ImSpooks.core.common.client.AbstractClient;
import me.ImSpooks.core.common.exceptions.SocketDisconnectedException;
import me.ImSpooks.core.helpers.JavaHelpers;
import me.ImSpooks.core.packets.collection.other.PacketResponseExpired;
import me.ImSpooks.core.packets.init.IncomingPacket;
import me.ImSpooks.core.packets.init.Packet;
import me.ImSpooks.core.packets.init.PacketRegister;
import org.tinylog.Logger;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Nick on 27 sep. 2019.
 * Copyright © ImSpooks
 */
public class ClientBridge extends AbstractClient {

    @Getter private final Bridge coreServer;
    @Getter private final ClientBridgeServer server;

    public ClientBridge(Socket socket, Bridge server) {
        this.coreServer = server;
        this.server = this.coreServer.getServer();
        this.socket = socket;
    }

    StringBuilder tmp = new StringBuilder();
    @Override
    public void handleClient() {
        try {
            if (this.in.available() == 0) {
                JavaHelpers.sleep(1);
                return;
            }
        } catch (IOException e) {
            Logger.error(e);
        }



        try {
            StringBuilder stringLength = new StringBuilder();
            String current = "";
            while (true) {
                byte[] buffer = new byte[1];
                in.read(buffer);
                current = new String(buffer);
                if (!current.equalsIgnoreCase("\n"))
                    stringLength.append(current);
                else break;
            }

            int length;

            try {
                length = Integer.parseInt(stringLength.toString());
            } catch (NumberFormatException e) {
                // throws this exception if previous data was corrupted
                // instead of parsing it will add the corrupted temp string to the current one

                String s = stringLength.toString().trim();
                s = tmp.toString() + s;
                if (s.startsWith("[") && !s.endsWith("]")) {
                    tmp.append(stringLength.toString());
                    Logger.debug("s = {}", s);
                    return;
                }
                tmp = new StringBuilder();
                this.handleCommand(s.trim());
                return;
            }

            byte[] buffer = new byte[length > 0 ? length : 128];
            in.read(buffer);
            String decoded = new String(buffer).trim();

            // Got corrupted data, splitting each packet with Form Feed
            for (byte splitter : new byte[] {Ascii.FF, Ascii.DC2}) {
                decoded = decoded.replace(
                        Character.toString((char) splitter),
                        "\n"
                );
            }

            if (decoded.contains("\n")) {
                String[] split = decoded.split("\n");

                for (String s : split) {
                    if (s == null || s.isEmpty())
                        continue;
                    s = s.trim();

                    s = tmp.toString() + s;
                    if (s.startsWith("[") && !s.endsWith("]")) {
                        // add corrupted data to tmp string
                        tmp.append(s.trim());
                        continue;
                    }
                    tmp = new StringBuilder();
                    this.handleCommand(s.trim());

                }
                return;
            }
            if (decoded.startsWith("[") && !decoded.endsWith("]")) {
                tmp.append(decoded.trim());
                // add corrupted data to tmp string
                return;
            }
            tmp = new StringBuilder();

            this.handleCommand(decoded);


        } catch (IOException e) {
            Logger.error(e);
        }
    }


    private void handleCommand(String input) throws IOException {
        if (input.contains("|")) {
            CommandHandler handler = new CommandHandler(input);
            if (handler.handleCommand()) {

            }
            return;
        }
        try {
            Packet packet = Packet.deserialize(input);
            try {
                Class<? extends Packet> response = PacketRegister.getPacketFromClassName(packet.getClass().getSimpleName() + "Response");
                this.coreServer.getJavaClient().sendAndReadPacket(packet, response, new IncomingPacket<Packet>(10 * 1000) {
                    @Override
                    public boolean onReceive(Packet packet) {
                        write(packet);
                        return true;
                    }

                    @Override
                    public void onExpire() {
                        write(new PacketResponseExpired(this.getExpireAfter()));
                    }
                });
            } catch (ClassNotFoundException e) {
                this.coreServer.getJavaClient().sendPacket(packet);
            }
        } catch (JsonParseException e) {
            this.write(new PacketResponseExpired(-1));
        } catch (Exception e) {
            Logger.error("Received corrupted data: " + input);
        }
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
            Logger.error(e);
        }
    }

    @Override
    public void handlePacket(Packet receivedPacket) {

    }

    @Override
    public void write(Packet packet) {
        try {
            byte[] serialized = packet.serialize();
            this.out.write(String.valueOf(serialized.length).getBytes());
            this.out.write(serialized);

            // Sleaping 5 millisecond to prevent data from being sent too fast that can cause corruption
            //JavaHelpers.sleep(5);
        } catch (IOException e) {
            Logger.error(e);
        }
    }
}
