package me.ImSpooks.core.common.client;

import com.google.common.base.Ascii;
import lombok.Getter;
import lombok.Setter;
import me.ImSpooks.core.common.exceptions.SocketDisconnectedException;
import me.ImSpooks.core.common.interfaces.IClient;
import me.ImSpooks.core.packets.collection.network.PacketClosing;
import me.ImSpooks.core.packets.init.Packet;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Nick on 02 okt. 2019.
 * Copyright © ImSpooks
 */
public abstract class AbstractClient implements IClient {
    @Getter @Setter protected String clientName = "";

    @Getter protected Socket socket;

    @Getter protected InputStream in;
    protected OutputStream out;

    @Getter @Setter protected boolean closed = true;

    protected static final byte[] SPLITTERS = new byte[] {Ascii.FF, Ascii.DC2, Ascii.DC4, Ascii.CAN};

    private String tmp = "";
    @Override
    public void handleClient() {
        try {
            if (this.socket.getInputStream().available() == 0) {
                return;
            }
        } catch (IOException e) {
            Logger.error(e);
        }

        try {
            String packet;
            byte[] buffer = new byte[this.in.available()];
            in.read(buffer);
            packet = new String(buffer);

            if (!tmp.isEmpty() && !packet.startsWith("[")) {
                packet = tmp + packet;
            }

            for (byte splitter : SPLITTERS) {
                String character = Character.toString((char) splitter);
                if (packet.contains(character)) {
                    System.out.println("Packet contains character " + character);
                    packet = packet.replace(
                            Character.toString((char) splitter),
                            "\n"
                    );
                }
            }

            tmp = "";
            String[] split = packet.split("\n");
            for (int i = 0; i < split.length; i++) {
                String s = split[i].trim();
                if (s.isEmpty())
                    continue;

                if (i == split.length - 1) { // last entry
                    if (!s.endsWith("]")) { // check if packet wasn't correctly received, and add it temporary and add it to the next receive event
                        tmp = s;
                        continue;
                    }
                }

                this.handlePacket(Packet.deserialize(s));
            }
//            System.out.println();
//            System.out.println();
//            System.out.println();
//            System.out.println();
//
//            System.out.println("packet = " + packet);
//            System.out.println("trimmed = " + trimmed);
//
//            for (String s : trimmed.split("\n")) {
//                s = s .trim();
//                if (s.isEmpty())
//                    continue;
//
//
//            }

//            StringBuilder packet = new StringBuilder();
//            byte[] buffer = new byte[this.in.available()];
//            in.read(buffer);
//            packet.append(new String(buffer).replace("\r", ""));
//
//            String decoded = packet.toString().trim();
//
//            decoded = tmp.toString() + decoded;
//
//            if (decoded.startsWith("\n")) decoded = decoded.substring(1);
//            if (decoded.isEmpty())
//                return;
//
//            if (!decoded.startsWith("[") && decoded.endsWith("]")) {
//                decoded = "[" + decoded;
//            }
//            if (!decoded.startsWith("[") || !decoded.endsWith("]")) {
//                tmp.append(packet.toString());
//                return;
//            }
//            if (!decoded.startsWith("[") && decoded.endsWith("]"))
//                decoded = "[" + decoded;
//            tmp = new StringBuilder();
//
//            decoded = decoded.trim();
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    @Override
    public void write(Packet packet) {
        try {
            if (this.out != null) {
                this.out.write((packet.serialize() + '\r' + '\n').getBytes());
                this.out.flush();
            }
        } catch (SocketException e) {
            this.out = null;
            this.close();
        } catch (IOException e) {
            Logger.error(e);
        }

//        this.out.println(packet.serialize());
//        this.out.flush();
    }

    @Override
    public void close() {
        try {
            closed = true;
            if (this.socket != null) {
                this.write(new PacketClosing());
                this.socket.close();
            }
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    protected boolean isConnected() throws SocketDisconnectedException {
//        try {
            return true;
//        } catch (IOException e) {
//            throw new SocketDisconnectedException(String.format("Client \'%s\' has disconnected (Crash?)", this.clientName));
//        }
    }
}
