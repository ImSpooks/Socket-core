package me.ImSpooks.core.bridge.init;

import com.google.common.base.Ascii;
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

    @Override
    public void handleClient() {
        try {
            if (this.in.available() == 0) {
                JavaHelpers.sleep(50);
                return;
            }
        } catch (IOException e) {
            Logger.error(e);
        }



        try {
            int length = in.readInt();
            byte[] buffer = new byte[length > 0 ? length : 128];
            in.read(buffer);
            String decoded = new String(buffer);

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
                    this.handleCommand(new CommandHandler(s.trim()));
                }
                return;
            }
            this.handleCommand(new CommandHandler(decoded.trim()));


        } catch (IOException e) {
            Logger.error(e);
        }
    }

    private void handleCommand(CommandHandler handler) {
        if (handler.handleCommand()) { // return output
            Packet packet = handler.getPacket();
            if (packet != null) {
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
                    this.coreServer.getJavaClient().sendPacket(handler.getPacket());
                }
            }
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
        super.write(packet);
    }
}
